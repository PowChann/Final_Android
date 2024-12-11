package com.example.finalandroidapplication.screens

import PostViewModel
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.finalandroidapplication.model.BottomNavItem
import com.example.finalandroidapplication.navigation.Routes

@Composable
fun BottomBar(navControllerBot: NavHostController) {
    val backStackEntry = navControllerBot.currentBackStackEntryAsState()
    val list = listOf(
        BottomNavItem(
            "Find House",
            Routes.FindHouse.routes,
            Icons.Rounded.Home
        ),
        BottomNavItem(
            "Find Roommate",
            Routes.FindRoommate.routes,
            Icons.Rounded.People
        ),
        BottomNavItem(
            "Notifications\nand\nMessages",
            Routes.Notifications.routes,
            Icons.Rounded.Notifications
        ),
        BottomNavItem(
            "Your Roommate",
            Routes.YourRoommate.routes,
            Icons.Rounded.AccountCircle
        ),
        BottomNavItem(
            "Profile",
            Routes.Profile.routes,
            Icons.Rounded.Settings
        )
    )
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        list.forEach{
            val selected = it.route == backStackEntry.value?.destination?.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navControllerBot.navigate(it.route){
                        popUpTo(navControllerBot.graph.findStartDestination().id){
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }, icon = {
                    Icon(imageVector = it.icon,
                        contentDescription = it.title,
                        tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                })
            }
        }
    }


@Composable
fun BottomNav(navController: NavHostController, uid: String){
    val navControllerBot = rememberNavController()
    Scaffold(bottomBar = { BottomBar(navControllerBot) }) { innerPadding ->
        NavHost(navController = navControllerBot, startDestination = Routes.FindHouse.routes,
            modifier = Modifier.padding(innerPadding)) {
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
            composable(Routes.Profile.routes) {
                Profile(navController, uid)
            }
        }
    }
}


