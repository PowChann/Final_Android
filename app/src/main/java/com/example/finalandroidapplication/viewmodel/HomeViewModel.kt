package com.example.finalandroidapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalandroidapplication.model.PostModel
import com.example.finalandroidapplication.model.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val _postsAndUsers = MutableLiveData<List<Pair<PostModel, UserModel>>>()
    val postsAndUsers: LiveData<List<Pair<PostModel, UserModel>>> = _postsAndUsers

    fun fetchPostsWithUsers() {
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

            }
    }
}