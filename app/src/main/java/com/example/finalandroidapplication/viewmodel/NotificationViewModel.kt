package com.example.finalandroidapplication.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalandroidapplication.model.NotificationModel
import com.example.finalandroidapplication.model.UserModel
import com.example.finalandroidapplication.utils.pushPhoneNotification
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class NotificationViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val _isNotifPushed = MutableLiveData<Boolean>()
    val isNotifPushed: LiveData<Boolean> get() = _isNotifPushed

    private val _notificationsWithUsers = MutableLiveData<List<Pair<NotificationModel, UserModel>>>()
    val notificationsWithUsers: LiveData<List<Pair<NotificationModel, UserModel>>> get() = _notificationsWithUsers

    fun fetchNotificationsByUser(context: Context) {
        firestore.collection("notifications")
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .addSnapshotListener { notificationSnapshot, error ->
                if (error != null) {
                    Log.e("NotificationViewModel", "Error listening to notifications: ${error.message}")
                    _notificationsWithUsers.postValue(emptyList())
                    return@addSnapshotListener
                }

                val notificationsAndUsersList = mutableListOf<Pair<NotificationModel, UserModel>>()

                notificationSnapshot?.documents?.forEach { notificationDoc ->
                    val notification = notificationDoc.toObject(NotificationModel::class.java)
                    notification?.let {
                        if (!notification.userId.isNullOrEmpty()) {
                            viewModelScope.launch {
                                try {
                                    // Only process notifications whose triggerTimeStamp has passed
                                    if (notification.triggerTimeStamp.toLong() <= System.currentTimeMillis()) {
                                        val userDoc = firestore.collection("users")
                                            .document(notification.userId)
                                            .get()
                                            .await()

                                        val user = userDoc.toObject(UserModel::class.java)
                                        if (user != null) {
                                            notificationsAndUsersList.add(Pair(notification, user))

                                            // Push to phone notification

                                        } else {
                                            Log.w(
                                                "NotificationViewModel",
                                                "User not found for userId: ${notification.userId}"
                                            )
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e("NotificationViewModel", "Error fetching user: ${e.message}")
                                }
                            }
                        } else {
                            Log.w(
                                "NotificationViewModel",
                                "Invalid userId for notification: ${notification.notifyId}"
                            )
                        }
                    }
                }
                Log.d(
                    "NotificationViewModel",
                    "Fetched notifications: ${notificationsAndUsersList.size}"
                )
                _notificationsWithUsers.postValue(notificationsAndUsersList)
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
        val notifyId = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis().toString()
        val isPushed = false ;

        val notificationData = mapOf(
            "notifyId" to notifyId,
            "userId" to userId,
            "type" to type,
            "title" to title,
            "description" to description,
            "actionUrl" to actionUrl,
            "timeStamp" to timestamp,
            "triggerTimeStamp" to triggerTimeStamp,
            "isPushed" to isPushed
        )

        firestore.collection("notifications").document(notifyId)
            .set(notificationData)
            .addOnSuccessListener {
                _isNotifPushed.postValue(true)
            }
            .addOnFailureListener { exception ->
                Log.e("NotificationViewModel", "Error pushing notification: ${exception.message}")
                _isNotifPushed.postValue(false)
            }
    }
}
