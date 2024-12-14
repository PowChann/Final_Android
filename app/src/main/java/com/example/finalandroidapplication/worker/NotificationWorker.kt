package com.example.finalandroidapplication.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.finalandroidapplication.model.NotificationModel
import com.example.finalandroidapplication.utils.pushPhoneNotification
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val firestore = FirebaseFirestore.getInstance()

        try {
            // Fetch notifications from Firestore
            val snapshot = firestore.collection("notifications")
                .whereEqualTo("isPushed", false)
                .get()
                .await()

            val currentTime = System.currentTimeMillis()


            snapshot.documents.forEach { document ->
                val notification = document.toObject(NotificationModel::class.java)
                notification?.let {
                    // Check if the notification should be triggered
                    val triggerTimeStamp = notification.triggerTimeStamp?.toLongOrNull()
                    if (triggerTimeStamp != null && triggerTimeStamp <= currentTime) {
                        // Push phone notification
                        pushPhoneNotification(
                            applicationContext,
                            notification.notifyId,
                            notification.title,
                            notification.description
                        )

                        // Mark as pushed
                        firestore.collection("notifications")
                            .document(notification.notifyId)
                            .update("isPushed", true)
                            .await()
                    }
                }
            }

            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }
    }
}

object WorkScheduler {
    fun scheduleNotificationWorker(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            15, TimeUnit.MINUTES // Minimum repeat interval is 15 minutes
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "NotificationWorker",
            ExistingPeriodicWorkPolicy.REPLACE, // Replace the work if already scheduled
            workRequest
        )
    }
}





