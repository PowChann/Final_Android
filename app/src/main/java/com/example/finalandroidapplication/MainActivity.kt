package com.example.finalandroidapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.finalandroidapplication.navigation.NavGraph
import com.example.finalandroidapplication.permissions.requestNotificationPermission
import com.example.finalandroidapplication.ui.theme.FinalAndroidApplicationTheme
import com.example.finalandroidapplication.work.NotificationWorker
import com.example.finalandroidapplication.work.WorkScheduler

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermission(this)
        WorkScheduler.scheduleNotificationWorker(this);
        setContent {
            FinalAndroidApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    NavGraph(navController = navController)
                }
            }
        }
    }
}
