package com.example.finalandroidapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.finalandroidapplication.model.PostModel
import com.example.finalandroidapplication.model.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

//class HomeViewModel: ViewModel() {
//    private val db = FirebaseDatabase.getInstance()
//    val post = db.getReference("posts")
//
//    private val _postsAndUsers = MutableLiveData<List<Pair<PostModel, UserModel>>>()
//    val postsAndUsers: LiveData<List<Pair<PostModel, UserModel>>> = _postsAndUsers
//
//    init {
//        fetchPostsAndUsers {
//            _postsAndUsers.value = it
//        }
//    }
//    private fun fetchPostsAndUsers(onResult: (List<Pair<PostModel, UserModel>>) -> Unit) {
//        post.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val result = mutableListOf<Pair<PostModel, UserModel>>()
//                for (postSnapshot in snapshot.children) {
//                    val post = postSnapshot.getValue(PostModel::class.java)
//                    post.let {
//                        fetchUserFromPost(it!!){
//                            user -> result.add(0,it to user)
//                            _postsAndUsers.value = result
//                            if(result.size == snapshot.childrenCount.toInt()){
//                                onResult(result)
//                            }
//                        }
//                    }
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        })
//    }
//
//    fun fetchUserFromPost(post: PostModel, onResult:(UserModel)-> Unit){
//        db.getReference("users").child(post.userId)
//            .addListenerForSingleValueEvent(object : ValueEventListener{
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val user = snapshot.getValue(UserModel::class.java)
//                    user?.let {
//
//                    }
//            }
//                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
//                }
//        })
//    }
//}

import com.google.firebase.firestore.FirebaseFirestore

class HomeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _postsAndUsers = MutableLiveData<List<Pair<PostModel, UserModel>>>()
    val postsAndUsers: LiveData<List<Pair<PostModel, UserModel>>> = _postsAndUsers

    init {
        fetchPostsAndUsers { result ->
            _postsAndUsers.value = result
        }
    }

    private fun fetchPostsAndUsers(onResult: (List<Pair<PostModel, UserModel>>) -> Unit) {
        db.collection("posts").get().addOnSuccessListener { snapshot ->
            val result = mutableListOf<Pair<PostModel, UserModel>>()
            val posts = snapshot.toObjects(PostModel::class.java)
                .sortedByDescending { it.timestamp.toLongOrNull() }

            if (posts.isNotEmpty()) {
                for (post in posts) {
                    fetchUserFromPost(post) { user ->
                        result.add(post to user)
                        _postsAndUsers.value = result
                        if (result.size == posts.size) {
                            onResult(result)
                        }
                    }
                }
            } else {
                onResult(emptyList())
            }
        }.addOnFailureListener {
            onResult(emptyList())
        }
    }

    private fun fetchUserFromPost(post: PostModel, onResult: (UserModel) -> Unit) {
        db.collection("users").document(post.userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val user = snapshot.toObject(UserModel::class.java)
                if (user != null) {
                    onResult(user)
                }
            }
            .addOnFailureListener {
                // Handle error
            }
    }
}
