package com.example.finalandroidapplication.navigation

import PostViewModel
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.finalandroidapplication.screens.AddPost
import com.example.finalandroidapplication.screens.BottomNav
import com.example.finalandroidapplication.screens.FindHouse
import com.example.finalandroidapplication.screens.FindRoommate
import com.example.finalandroidapplication.screens.Login
import com.example.finalandroidapplication.screens.Notifications
import com.example.finalandroidapplication.screens.Profile
import com.example.finalandroidapplication.screens.Register
import com.example.finalandroidapplication.screens.YourRoommate
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavGraph(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    NavHost(
        navController = navController,
        startDestination = Routes.Login.routes
    ) {
        composable(Routes.FindHouse.routes) {
            FindHouse(navController)
        }
        composable(Routes.FindRoommate.routes) {
            FindRoommate(navController)
        }
        composable(Routes.Notifications.routes) {
            Notifications(navController)
        }
        composable(Routes.YourRoommate.routes) {
            YourRoommate(navController)
        }
        composable("${Routes.Profile.routes}/{uid}") { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            Profile(navController, uid)
        }
        composable(Routes.BottomNav.routes) {
            val uid = auth.currentUser?.uid ?: ""
            BottomNav(navController = navController,uid = uid)
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
    }
}



//@Composable
//fun NavGraph(navController: NavHostController) {
//    val auth = FirebaseAuth.getInstance()
//    val isUserLoggedIn = auth.currentUser != null
//
//    NavHost(
//        navController = navController,
//        startDestination = if (isUserLoggedIn) Routes.BottomNav.routes else Routes.Login.routes // Dynamic start destination
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
//            BottomNav(navController = navController, uid = uid)
//        }
//        composable(Routes.Login.routes) {
//            Login(navController)
//        }
//        composable(Routes.Register.routes) {
//            Register(navController)
//        }
//        composable(Routes.AddPost.routes) {
//            val uid = auth.currentUser?.uid ?: ""
//            AddPost(navController, uid)
//        }
//    }
//}
//
//
