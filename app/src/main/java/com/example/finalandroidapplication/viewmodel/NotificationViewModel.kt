package com.example.finalandroidapplication.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.finalandroidapplication.model.NotificationModel

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

import java.util.UUID

class NotificationViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val _isNotifPushed = MutableLiveData<Boolean>()
    val isNotifPushed: LiveData<Boolean> get() = _isNotifPushed


    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _success = MutableLiveData<String>()
    val success: LiveData<String> = _success

    private val _userNotifications = MutableLiveData<List<NotificationModel?>>()
    val userNotifications : LiveData<List<NotificationModel?>> = _userNotifications



    fun fetchNotificationByUid(uid: String) {
        if (uid.isBlank()) {
            _error.postValue("User ID is invalid")
            return
        }
        Log.d("FetchNotification", "Fetching notifications for user ID: $uid")

        firestore.collection("notifications")
            .whereEqualTo("userId", uid) // Filter by user ID
            .orderBy("timeStamp", Query.Direction.DESCENDING) // Order by timeStamp
            .get()
            .addOnSuccessListener { querySnapshot ->
                Log.d("FetchNotification", "Query successful. Found ${querySnapshot.size()} notifications.")

                // Map all notifications to NotificationModel without filtering triggerTimeStamp
                val notifications = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(NotificationModel::class.java)
                }

                // Update LiveData with the notifications
                if (notifications.isNotEmpty()) {
                    Log.d("FetchNotification", "Fetched notifications: ${notifications.size}")
                    _userNotifications.postValue(notifications)
                    _success.postValue("Notifications fetched successfully")
                } else {
                    _error.postValue("No notifications found for this user.")
                }
            }
            .addOnFailureListener { exception ->
                // Handle error while fetching notifications
                _error.postValue("Error fetching notifications: ${exception.localizedMessage}")
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
