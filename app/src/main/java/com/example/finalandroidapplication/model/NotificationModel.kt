package com.example.finalandroidapplication.model

data class NotificationModel(
    val notifyId: String = "",
    val userId: String = "",
    val type: NotificationType = NotificationType.SYSTEM,
    val title: String = "",
    val description: String = "",
    val timeStamp: String = "",
    val triggerTimeStamp: String = "",
    val isPushed : Boolean = false ,
    val actionUrl: String? = null
)

enum class NotificationType {
    SYSTEM,
    CHORES,
    PAYMENT,
    OFFERS,
    VALIDATE,
    HOUSE,
    PARTNER,
    APPOINTMENT
}