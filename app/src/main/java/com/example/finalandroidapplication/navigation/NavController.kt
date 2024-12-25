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
import com.example.finalandroidapplication.screens.ChannelDetails
import com.example.finalandroidapplication.screens.ContractTemplate
import com.example.finalandroidapplication.screens.MyHome
import com.example.finalandroidapplication.screens.RulesAndPolicies
import com.example.finalandroidapplication.screens.Schedule
import com.google.firebase.auth.FirebaseAuth

//@Composable
//fun NavGraph(navController: NavHostController) {
//    val auth = FirebaseAuth.getInstance()
//    NavHost(
//        navController = navController,
//        startDestination = Routes.Login.routes
//    ) {
//        composable(Routes.FindHouse.routes) {
//            FindHouse(navController)
//        }
//        composable(Routes.FindRoommate.routes) {
//            FindRoommate(navController)
//        }
//        composable(Routes.Notifications.routes) {
//            Notifications(navController)
//        }
//        composable(Routes.YourRoommate.routes) {
//            YourRoommate(navController)
//        }
//        composable("${Routes.Profile.routes}/{uid}") { backStackEntry ->
//            val uid = backStackEntry.arguments?.getString("uid") ?: ""
//            Profile(navController, uid)
//        }
//        composable(Routes.BottomNav.routes) {
//            val uid = auth.currentUser?.uid ?: ""
//            BottomNav(navController = navController,uid = uid)
//        }
//        composable(Routes.Login.routes) {
//            Login(navController)
//        }
//        composable(Routes.Register.routes) {
//            Register(navController)
//        }
//        composable(Routes.AddPost.routes) {
//            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
//            AddPost(navController, uid)
//        }
//        composable(
//            route = "OtherProfile/{postId}",
//            arguments = listOf(navArgument("postId") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val postId = backStackEntry.arguments?.getString("postId") ?: ""
//            OtherProfile(navController, postId)
//        }
//    }
//}



@Composable
fun NavGraph(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val isUserLoggedIn = auth.currentUser != null

    NavHost(
        navController = navController,
        startDestination = if (isUserLoggedIn) Routes.BottomNav.routes else Routes.Login.routes // Dynamic start destination
    ) {
        composable(Routes.FindHouse.routes) {
            FindHouse(navController)
        }
        composable(Routes.FindRoommate.routes) {
            FindRoommate(navController)
        }
        composable("${Routes.Notifications.routes}/{uid}") {backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            Notifications(navController, uid)
        }

        composable("${Routes.Messages.routes}/{uid}") {backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            Messages(navController, uid)
        }
        composable(Routes.YourRoommate.routes) {
            YourRoommate(navController)
        }
        composable("${Routes.Profile.routes}/{uid}") { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            Profile(navController, uid)
        }

        composable("${Routes.Schedule.routes}/{uid}") { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            Schedule(navController, uid)
        }


        composable(Routes.BottomNav.routes) {
            val uid = auth.currentUser?.uid ?: ""
            BottomNav(navController = navController, uid = uid)
        }
        composable(Routes.Login.routes) {
            Login(navController)
        }
        composable(Routes.Register.routes) {
            Register(navController)
        }
        composable(Routes.AddPost.routes) {
            val uid = auth.currentUser?.uid ?: ""
            AddPost(navController, uid)
        }
        composable(Routes.AddHouse.routes) {
            val uid = auth.currentUser?.uid ?: ""
            AddHouse(navController, uid)
        }

        composable("${Routes.ChannelDetails.routes}/{channelID}") {
            backStackEntry -> val channelID = backStackEntry.arguments?.getString("channelID") ?: ""
            ChannelDetails(channelID, navController)
        }

        composable(
            route = "OtherProfile/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            OtherProfile(navController, userId)
        }

        composable(Routes.ContractTemplate.routes) {
            ContractTemplate(navController)
        }
        composable(Routes.RulesAndPolicies.routes) {
            RulesAndPolicies(navController)
        }

        composable("${Routes.MyHome.routes}/{uid}") {backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            MyHome(navController, uid)
        }

    }
}



