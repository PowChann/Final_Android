package com.example.finalandroidapplication.navigation

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
    object RulesAndPolicies: Routes("rules")
}


