package com.example.finalandroidapplication.model

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import com.example.finalandroidapplication.R
import com.example.finalandroidapplication.viewmodel.ChannelViewModel
import com.example.finalandroidapplication.viewmodel.NotificationViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HouseItem(
    house: HouseModel,
    user: UserModel,
    navHostController: NavHostController
) {
    val firestore = FirebaseFirestore.getInstance()
    var isInterested by remember { mutableStateOf(false) }
    var numInterested by remember { mutableStateOf(0) }
    val minPeople: Int = house.numOfPeople.toInt() // Minimum people to disable button

    // Fetch the current logged-in user's ID
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    // Fetch initial queue size
    firestore.collection("interestQueues")
        .document(house.houseId)
        .get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val queue = document.get("queue") as? List<*> ?: emptyList<Any>()
                numInterested = queue.size
                isInterested = queue.contains(currentUserId)
            }
        }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navHostController.navigate("OtherProfile/${user.uid}") {
                    popUpTo(navHostController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = if (!user.avatarUrl.isNullOrEmpty()) {
                        rememberAsyncImagePainter(model = user.avatarUrl)
                    } else {
                        painterResource(id = R.drawable.baseline_person_24)
                    },
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape)
                        .background(Color.LightGray)
                        .clickable {
                            navHostController.navigate("OtherProfile/${user.uid}")
                        },
                    contentScale = ContentScale.Crop,
                    colorFilter = if (user.avatarUrl.isNullOrEmpty()) ColorFilter.tint(Color.White) else null
                )
                Spacer(modifier = Modifier.size(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user.username,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = "Location: ${house.location}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Price: ${house.price} VND/Month",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Room Type: ${house.roomType}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Max People: ${house.numOfPeople}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Amenities: ${house.amenities.joinToString(", ")}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.size(8.dp))

            if (!house.imageUrl.isNullOrEmpty()) {
                val painter = rememberAsyncImagePainter(
                    model = house.imageUrl,
                    onError = {
                        Log.e("ImageLoading", "Error loading image: ${it.result.throwable}")
                    }
                )

                Image(
                    painter = painter,
                    contentDescription = "House Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .border(2.dp, Color.Gray)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.size(8.dp))

            val formattedDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(Date(house.timestamp.toLongOrNull() ?: 0L))

            Text(
                text = "Posted on: $formattedDate",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Show the "Interested" button only if the house.userId != currentUserId
            if (house.userId != currentUserId) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            firestore.collection("interestQueues")
                                .document(house.houseId)
                                .get()
                                .addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        val queue = document.get("queue") as? List<*> ?: emptyList<Any>()
                                        if (isInterested) {
                                            // Remove current user from the queue
                                            firestore.collection("interestQueues")
                                                .document(house.houseId)
                                                .update("queue", FieldValue.arrayRemove(currentUserId))
                                                .addOnSuccessListener {
                                                    numInterested--
                                                    isInterested = false
                                                }
                                                .addOnFailureListener {
                                                    isInterested = true
                                                    Toast.makeText(
                                                        navHostController.context,
                                                        "Error removing interest",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                        } else {
                                            // Add current user and house owner to the queue
                                            if (!queue.contains(currentUserId) && queue.size < minPeople) {
                                                firestore.collection("interestQueues")
                                                    .document(house.houseId)
                                                    .update("queue", FieldValue.arrayUnion(currentUserId, house.userId))
                                                    .addOnSuccessListener {
                                                        numInterested++
                                                        isInterested = true

                                                        // Refetch the queue
                                                        firestore.collection("interestQueues")
                                                            .document(house.houseId)
                                                            .get()
                                                            .addOnSuccessListener { updatedDocument ->
                                                                if (updatedDocument.exists()) {
                                                                    val updatedQueue = updatedDocument.get("queue") as? List<*> ?: emptyList<Any>()
                                                                    val stringQueue: List<String> = updatedQueue.mapNotNull { it as? String }
                                                                    Log.d("Updated Queue", stringQueue.toString())

                                                                    if (numInterested >= minPeople) {
                                                                        val notificationViewModel = NotificationViewModel()
                                                                        val channelViewModel = ChannelViewModel()

                                                                        channelViewModel.createChannel(
                                                                            participants = stringQueue,
                                                                            onChannelExists = { channelId ->
                                                                                Log.d("HouseItem", "Channel already exists with ID: $channelId")
                                                                                stringQueue.forEach { userId ->
                                                                                    notificationViewModel.pushNotification(
                                                                                        userId,
                                                                                        "HOUSE",
                                                                                        "Min people reached",
                                                                                        "Your interested house reached the minimum required people. Click to enter the group chat.",
                                                                                        "ChannelDetails/${channelId}",
                                                                                        System.currentTimeMillis().toString()
                                                                                    )
                                                                                }
                                                                            },
                                                                            onChannelCreated = { channelId ->
                                                                                Log.d("HouseItem", "New channel created with ID: $channelId")
                                                                                stringQueue.forEach { userId ->
                                                                                    notificationViewModel.pushNotification(
                                                                                        userId,
                                                                                        "HOUSE",
                                                                                        "Min people reached",
                                                                                        "Your interested house reached the minimum required people. Click to enter the group chat.",
                                                                                        "ChannelDetails/${channelId}",
                                                                                        System.currentTimeMillis().toString()
                                                                                    )
                                                                                }
                                                                            },
                                                                            onError = { errorMessage ->
                                                                                Log.e("HouseItem", "Error creating or checking channel: $errorMessage")
                                                                            }
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                            .addOnFailureListener {
                                                                Log.e("HouseItem", "Error refetching queue after interest: ${it.message}")
                                                            }
                                                    }
                                                    .addOnFailureListener {
                                                        isInterested = false
                                                        Toast.makeText(
                                                            navHostController.context,
                                                            "Error showing interest",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                            }

                                        }
                                    } else {
                                        // Queue does not exist, create a new one
                                        val newQueue = mapOf(
                                            "queue" to listOf(currentUserId, house.userId),
                                            "houseID" to house.houseId,
                                            "min" to 1,
                                            "max" to minPeople
                                        )
                                        firestore.collection("interestQueues")
                                            .document(house.houseId)
                                            .set(newQueue)
                                            .addOnSuccessListener {
                                                numInterested = 1
                                                isInterested = true
                                            }
                                            .addOnFailureListener {
                                                isInterested = false
                                                Toast.makeText(
                                                    navHostController.context,
                                                    "Error creating interest queue",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        navHostController.context,
                                        "Error checking interest queue",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isInterested) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                        ),
                        enabled = numInterested < minPeople || isInterested


                    ) {
                        Text(
                            text = if (isInterested) "Uninterest" else "Interest",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

