package com.example.finalandroidapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalandroidapplication.model.PostModel
import com.example.finalandroidapplication.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RoommateViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val _postsAndUsers = MutableLiveData<List<Pair<PostModel, UserModel>>>()
    val postsAndUsers: LiveData<List<Pair<PostModel, UserModel>>> = _postsAndUsers

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _success = MutableLiveData<String>()
    val success: LiveData<String> = _success

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchAllPostsWithUsers() {
        _isLoading.postValue(true)
        firestore.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { postSnapshot ->
                val postsList = mutableListOf<Pair<PostModel, UserModel>>()
                viewModelScope.launch {
                    postSnapshot.documents.forEach { postDoc ->
                        val post = postDoc.toObject(PostModel::class.java)
                        post?.let {
                            val userDoc = firestore.collection("users")
                                .document(it.userId)
                                .get()
                                .await()

                            val user = userDoc.toObject(UserModel::class.java)
                            user?.let { userData ->
                                postsList.add(Pair(post, userData))
                            }
                        }
                    }
                    _postsAndUsers.postValue(postsList)
                }
            }
            .addOnFailureListener { exception ->
                _error.postValue("Error fetching posts: ${exception.message}")
            }
            .addOnCompleteListener {
                _isLoading.postValue(false)
            }
    }

    fun fetchCurrentUser(onResult: (UserModel?) -> Unit) {
        _isLoading.postValue(true)
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId == null) {
            _isLoading.postValue(false)
            onResult(null)
            return
        }

        firestore.collection("users").document(currentUserId).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(UserModel::class.java)
                onResult(user)
            }
            .addOnFailureListener {
                _error.postValue("Failed to fetch current user: ${it.message}")
                onResult(null)
            }
            .addOnCompleteListener {
                _isLoading.postValue(false)
            }
    }

    fun fetchUsersWithSimilarHabits(currentUserHabits: Map<String, String>, onResult: (List<UserModel>) -> Unit) {
        _isLoading.postValue(true)
        firestore.collection("users").get()
            .addOnSuccessListener { documents ->
                val matchedUsers = documents.mapNotNull { document ->
                    val user = document.toObject(UserModel::class.java)

                    if (user.uid == FirebaseAuth.getInstance().currentUser?.uid) return@mapNotNull null

                    val userHabits = user.habits.filterValues { it.isNotBlank() }
                    if (userHabits.isNotEmpty() && currentUserHabits.entries.any { entry ->
                            userHabits.containsValue(entry.value)
                        }
                    ) {
                        user
                    } else {
                        null
                    }
                }
                onResult(matchedUsers)
            }
            .addOnFailureListener { e ->
                _error.postValue("Failed to fetch users: ${e.message}")
                onResult(emptyList())
            }
            .addOnCompleteListener {
                _isLoading.postValue(false)
            }
    }

    fun fetchPostsForMatchedUsers(
        matchedUsers: List<UserModel>,
        onResult: (List<Pair<PostModel, UserModel>>) -> Unit
    ) {
        _isLoading.postValue(true)
        val postsWithUsers = mutableListOf<Pair<PostModel, UserModel>>()

        firestore.collection("posts").get()
            .addOnSuccessListener { postDocs ->
                val allPosts = postDocs.map { it.toObject(PostModel::class.java) }

                for (user in matchedUsers) {
                    val userPosts = allPosts.filter { it.userId == user.uid }
                    userPosts.forEach { post ->
                        postsWithUsers.add(post to user)
                    }
                }
                onResult(postsWithUsers)
            }
            .addOnFailureListener {
                _error.postValue("Failed to fetch posts for matched users: ${it.message}")
                onResult(emptyList())
            }
            .addOnCompleteListener {
                _isLoading.postValue(false)
            }
    }
}