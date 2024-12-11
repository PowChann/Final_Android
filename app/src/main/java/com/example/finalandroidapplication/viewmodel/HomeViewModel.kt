package com.example.finalandroidapplication.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.finalandroidapplication.model.PostModel
import com.example.finalandroidapplication.model.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val _postsAndUsers = MutableLiveData<List<Pair<PostModel, UserModel>>>()
    val postsAndUsers: LiveData<List<Pair<PostModel, UserModel>>> = _postsAndUsers

    fun fetchPostsAndUsers() {
        firestore.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { postsSnapshot ->
                val posts = postsSnapshot.toObjects(PostModel::class.java)
                fetchUsersForPosts(posts) { usersMap ->
                    val result = posts.mapNotNull { post ->
                        usersMap[post.userId]?.let { user ->
                            Pair(post, user)
                        }
                    }
                    _postsAndUsers.value = result
                }
            }
            .addOnFailureListener { error ->
                Log.e("HomeViewModel", "Error fetching posts: ${error.message}")
            }
    }

    private fun fetchUsersForPosts(
        posts: List<PostModel>,
        onComplete: (Map<String, UserModel>) -> Unit
    ) {
        val userIds = posts.map { it.userId }.distinct()
        firestore.collection("users")
            .whereIn("uid", userIds)
            .get()
            .addOnSuccessListener { snapshot ->
                val usersMap = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(UserModel::class.java)?.let { it.uid to it }
                }.toMap()

                onComplete(usersMap)
            }
            .addOnFailureListener { error ->
                Log.e("HomeViewModel", "Error fetching users: ${error.message}")
                onComplete(emptyMap())
            }
    }
}