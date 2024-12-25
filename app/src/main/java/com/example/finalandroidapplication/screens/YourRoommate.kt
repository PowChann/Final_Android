package com.example.finalandroidapplication.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material.icons.filled.Textsms
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import com.example.finalandroidapplication.R
import com.example.finalandroidapplication.model.RoommateItem
import com.example.finalandroidapplication.model.UserModel
import com.example.finalandroidapplication.navigation.Routes
import com.example.finalandroidapplication.viewmodel.ChannelViewModel
import com.example.finalandroidapplication.viewmodel.HouseViewModel
import com.example.finalandroidapplication.viewmodel.PostViewModel
import com.example.finalandroidapplication.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourRoommate(navController: NavHostController) {
    val firestore = FirebaseFirestore.getInstance()
    var houseViewModel : HouseViewModel = viewModel()
    var showContractDialog by remember { mutableStateOf(false) }
    var contractDetails by remember { mutableStateOf<Map<String, String>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var owner by remember { mutableStateOf<UserModel?>(null) }
    var roommatesList by remember { mutableStateOf<List<UserModel>>(emptyList()) }
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val channelViewModel : ChannelViewModel = viewModel()
    LaunchedEffect(currentUserId) {
        if (currentUserId != null) {
            try {
                val roommatesDoc = firestore.collection("roommates")
                    .whereArrayContains("roommates", currentUserId) // Check if the user is in the roommates array
                    .get()
                    .await()

                if (!roommatesDoc.isEmpty) {
                    val documentSnapshot = roommatesDoc.documents.first()
                    val roommateIds = documentSnapshot["roommates"] as? List<String> ?: emptyList()
                    val ownerId = documentSnapshot["owner"] as? String

                    // Fetch the owner details
                    if (!ownerId.isNullOrEmpty()) {
                        val ownerDoc = firestore.collection("users").document(ownerId).get().await()
                        owner = ownerDoc.toObject(UserModel::class.java)
                    }

                    // Fetch all roommate details
                    val roommateDocs = firestore.collection("users")
                        .whereIn("uid", roommateIds)
                        .get()
                        .await()
                    roommatesList = roommateDocs.toObjects(UserModel::class.java)
                }
                isLoading = false
            } catch (e: Exception) {
                errorMessage = "Failed to load roommates: ${e.message}"
                isLoading = false
            }
        }





    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Your Roommate", fontSize = 24.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding)) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Button(
                                onClick = {
                                    navController.navigate(Routes.ContractTemplate.routes)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .size(80.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = "Contract Template",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Contract Template",
                                fontSize = 14.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Button(
                                onClick = { navController.navigate(Routes.RulesAndPolicies.routes) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .size(80.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.TextSnippet,
                                    contentDescription = "Rules and Policies",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Rules and Policies",
                                fontSize = 14.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Button(
                                onClick = {
                                    val db = FirebaseFirestore.getInstance()
                                    db.collection("contracts")
                                        .whereEqualTo(
                                            "userId",
                                            FirebaseAuth.getInstance().currentUser?.uid
                                        )
                                        .get()
                                        .addOnSuccessListener { querySnapshot ->
                                            if (!querySnapshot.isEmpty) {
                                                val document = querySnapshot.documents[0]
                                                contractDetails =
                                                    document.data as Map<String, String>
                                            } else {
                                                contractDetails =
                                                    mapOf("Message" to "No contract found!")
                                            }
                                            showContractDialog = true

                                        }
                                        .addOnFailureListener {
                                            contractDetails =
                                                mapOf("Error" to "Failed to fetch contract details.")
                                            showContractDialog = true
                                        }
                                },

                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .size(80.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = "View Contract",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "View Your Contract",
                                fontSize = 14.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Your Roommates",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        if (isLoading) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                            }
                        } else {
                            Column {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                ) {
                                    // Display Roommates
                                    items(roommatesList.size) { index ->
                                        val roommate = roommatesList[index]
                                        RoommateItem(
                                            roommate = roommate,
                                            navHostController = navController,
                                            isOwner = roommate.uid == owner?.uid // Check if the roommate UID matches the owner UID
                                        )
                                    }

                                    if (roommatesList.isEmpty()) {
                                        item {
                                            Text(
                                                text = "No roommates found",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }

                                Button(
                                    onClick = {
                                        val participants = mutableListOf<String>()
                                        for (userModel in roommatesList) {
                                            participants.add(userModel.uid)
                                        }
                                        channelViewModel.createChannel(
                                            participants = participants,
                                            onChannelExists = { channelID ->
                                                navController.navigate("ChannelDetails/$channelID")
                                            },
                                            onChannelCreated = { channelID ->
                                                navController.navigate("ChannelDetails/$channelID")
                                            },
                                            onError = { error ->
                                                Toast.makeText(
                                                    navController.context,
                                                    "Error: $error",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        )
                                    },
                                    enabled = roommatesList.isNotEmpty(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (roommatesList.isNotEmpty()) MaterialTheme.colorScheme.primary else Color.Gray
                                    )
                                ) {
                                    Text(
                                        text = "Group Chat",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )



    if (showContractDialog) {
        AlertDialog(
            onDismissRequest = { showContractDialog = false },
            confirmButton = {
                Button(
                    onClick = { showContractDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Close", color = Color.White)
                }
            },
            title = {
                Text(
                    text = "Contract Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {

                    Text(
                        text = "Party A (Lessor):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Name: ${contractDetails?.get("partyA_name") ?: "N/A"}",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "Phone: ${contractDetails?.get("partyA_phone") ?: "N/A"}",
                        fontSize = 16.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Party B (Lessee):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Name: ${contractDetails?.get("partyB_name") ?: "N/A"}",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "Phone: ${contractDetails?.get("partyB_phone") ?: "N/A"}",
                        fontSize = 16.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Contract Details:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Address: ${contractDetails?.get("address") ?: "N/A"}",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "Rent: ${contractDetails?.get("rent") ?: "N/A"}",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "Start Date: ${contractDetails?.get("startDate") ?: "N/A"}",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "Duration: ${contractDetails?.get("duration") ?: "N/A"}",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "Cost Sharing: ${contractDetails?.get("costSplit") ?: "N/A"}",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }
        )
    }
}


@Composable
fun RoommateItem(
    roommate: UserModel,
    navHostController: NavHostController,
    isOwner: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navHostController.navigate("OtherProfile/${roommate.uid}") {
                    popUpTo(navHostController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
            .padding(vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                if (roommate.avatarUrl.isNullOrEmpty()) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_person_24),
                        contentDescription = "Default Avatar",
                        modifier = Modifier.fillMaxSize(),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                    )
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = roommate.avatarUrl
                        ),
                        contentDescription = "User Avatar",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // User Info
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = roommate.username,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (isOwner) {
                        Text(
                            text = "(Owner)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = roommate.name,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }



    }
}