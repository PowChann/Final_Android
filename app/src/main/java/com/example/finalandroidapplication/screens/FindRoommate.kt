package com.example.finalandroidapplication.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.finalandroidapplication.model.PostItem
import com.example.finalandroidapplication.model.PostModel
import com.example.finalandroidapplication.model.UserModel
import com.example.finalandroidapplication.navigation.Routes
import com.example.finalandroidapplication.viewmodel.ProfileViewModel
import com.example.finalandroidapplication.viewmodel.RoommateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindRoommate(navController: NavHostController) {
    val roommateViewModel: RoommateViewModel = viewModel()
    val isLoading by roommateViewModel.isLoading.observeAsState(false)
    val postsAndUsers by roommateViewModel.postsAndUsers.observeAsState(emptyList())
    val matchedUsers = remember { mutableStateListOf<UserModel>() }
    val matchedPosts = remember { mutableStateListOf<Pair<PostModel, UserModel>>() }
    val currentUser = remember { mutableStateOf<UserModel?>(null) }
    val isMatchedPostsEmpty = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        roommateViewModel.fetchCurrentUser { user ->
            currentUser.value = user
            if (user != null) {
                roommateViewModel.fetchUsersWithSimilarHabits(user.habits) { users ->
                    matchedUsers.clear()
                    matchedUsers.addAll(users)

                    if (users.isNotEmpty()) {
                        roommateViewModel.fetchPostsForMatchedUsers(users) { posts ->
                            matchedPosts.clear()
                            matchedPosts.addAll(posts)
                            isMatchedPostsEmpty.value = posts.isEmpty()
                        }
                    } else {
                        isMatchedPostsEmpty.value = true
                        roommateViewModel.fetchAllPostsWithUsers()
                    }
                }
            } else {
                isMatchedPostsEmpty.value = true
                roommateViewModel.fetchAllPostsWithUsers()
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Find Roommate", fontSize = 24.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Routes.AddPost.routes) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                containerColor = Color.White,
                contentColor = Color.Black,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp
                )
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Post")
            }
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else if (isMatchedPostsEmpty.value) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(postsAndUsers) { (post, user) ->
                            PostItem(
                                post = post,
                                users = user,
                                navHostController = navController
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(matchedPosts) { (post, user) ->
                            PostItem(
                                post = post,
                                users = user,
                                navHostController = navController
                            )
                        }
                    }
                }
            }
        }
    )
}
