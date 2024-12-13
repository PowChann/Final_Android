package com.example.finalandroidapplication.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalandroidapplication.model.NotificationModel
import com.example.finalandroidapplication.model.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class NotificationViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val _isNotifPushed = MutableLiveData<Boolean>()
    private val _notificationsWithUsers = MutableLiveData<List<Pair<NotificationModel, UserModel>>>()
    val notificationsWithUsers: LiveData<List<Pair<NotificationModel, UserModel>>> = _notificationsWithUsers

    fun fetchNotificationsByUser() {
        firestore.collection("notifications")
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { notificationSnapshot ->
                val notificationsAndUsersList = mutableListOf<Pair<NotificationModel, UserModel>>()
                viewModelScope.launch {
                    notificationSnapshot.documents.forEach { notificationDoc ->
                        val notification = notificationDoc.toObject(NotificationModel::class.java)
                        notification?.let {
                            if (!notification.userId.isNullOrEmpty()) {
                                try {
                                    val userDoc = firestore.collection("users")
                                        .document(notification.userId)
                                        .get()
                                        .await()

                                    val user = userDoc.toObject(UserModel::class.java)
                                    if (user != null) {
                                        notificationsAndUsersList.add(Pair(notification, user))
                                    } else {
                                        Log.w("NotificationViewModel", "User not found for userId: ${notification.userId}")
                                    }
                                } catch (e: Exception) {
                                    Log.e("NotificationViewModel", "Error fetching user: ${e.message}")
                                }
                            } else {
                                Log.w("NotificationViewModel", "Invalid userId for notification: ${notification.notifyId}")
                            }
                        }
                    }
                    Log.d("NotificationViewModel", "Fetched notifications: ${notificationsAndUsersList.size}")
                    _notificationsWithUsers.postValue(notificationsAndUsersList)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("NotificationViewModel", "Error fetching notifications: ${exception.message}")
                _notificationsWithUsers.postValue(emptyList())
            }
    }


    fun pushNotification(
        userId: String,
        type: String,
        title: String,
        description: String,
        actionUrl: String,
        triggerTimeStamp: String

    ) {
        val notifyId= UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis().toString()

        val notificationData = mapOf(
            "notifyId" to notifyId,
            "userId" to userId,
            "type" to type ,
            "title" to title ,
            "description" to description,
            "actionUrl" to actionUrl,
            "timeStamp" to timestamp ,
            "triggerTimeStamp" to triggerTimeStamp

        )

        firestore.collection("notifications").document(notifyId)
            .set(notificationData)
            .addOnSuccessListener {
                _isNotifPushed.postValue(true)
            }
            .addOnFailureListener {
                _isNotifPushed.postValue(false)
            }
    }



}
