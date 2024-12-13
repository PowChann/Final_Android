package com.example.finalandroidapplication.model

import android.text.format.DateUtils
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun MessageItem(
    message: MessageModel,
    navHostController: NavHostController
) {
    val timeAgo = calculateTimeAgo(message.timeStamp)

    
}


