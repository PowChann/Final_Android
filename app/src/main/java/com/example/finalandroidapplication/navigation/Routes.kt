package com.example.finalandroidapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.finalandroidapplication.screens.AddHouse
import com.example.finalandroidapplication.screens.AddPost
import com.example.finalandroidapplication.screens.BottomNav
import com.example.finalandroidapplication.screens.FindHouse
import com.example.finalandroidapplication.screens.FindRoommate
import com.example.finalandroidapplication.screens.Login
import com.example.finalandroidapplication.screens.Messages
import com.example.finalandroidapplication.screens.Notifications
import com.example.finalandroidapplication.screens.OtherProfile
import com.example.finalandroidapplication.screens.Profile
import com.example.finalandroidapplication.screens.Register
import com.example.finalandroidapplication.screens.YourRoommate
import com.google.firebase.auth.FirebaseAuth

sealed class Routes(val routes: String) {
    object FindHouse: Routes("findhouse")
    object FindRoommate: Routes("findroommate")
    object Notifications: Routes("notification")
    object YourRoommate: Routes("yourroommate")
    object Profile: Routes("profile")
    object BottomNav: Routes("bottom_nav")
    object Login: Routes("login")
    object Register: Routes("register")
    object AddPost: Routes("addpost")
    object AddHouse: Routes("addhouse")
    object Messages: Routes("messages")
    object ContractTemplate: Routes("contract")
    object ChannelDetails: Routes("ChannelDetails")


}


