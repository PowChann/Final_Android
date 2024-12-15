package com.example.finalandroidapplication.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.finalandroidapplication.model.RoommateItem
import com.example.finalandroidapplication.model.UserModel
import com.example.finalandroidapplication.navigation.Routes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourRoommate(navController: NavHostController) {
    var showContractDialog by remember { mutableStateOf(false) }
    var contractDetails by remember { mutableStateOf<Map<String, String>?>(null) }
    var roommates by remember { mutableStateOf(listOf<UserModel>()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        try {
            val querySnapshot = db.collection("users")
                .whereEqualTo("isRoommate", true) // Lọc người dùng có isRoommate = true
                .whereNotEqualTo("uid", currentUserId) // Loại bỏ người dùng hiện tại
                .get()
                .await()

            roommates = querySnapshot.toObjects(UserModel::class.java) // Chuyển dữ liệu thành danh sách UserModel
            isLoading = false
        } catch (exception: Exception) {
            errorMessage = "Failed to load roommates: ${exception.localizedMessage}"
            isLoading = false
        }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Your Roommate", fontSize = 24.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
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
                                        .whereEqualTo("userId", FirebaseAuth.getInstance().currentUser?.uid)
                                        .get()
                                        .addOnSuccessListener { querySnapshot ->
                                            if (!querySnapshot.isEmpty) {
                                                val document = querySnapshot.documents[0]
                                                contractDetails = document.data as Map<String, String>
                                            } else {
                                                contractDetails = mapOf("Message" to "No contract found!")
                                            }
                                            showContractDialog = true

                                        }
                                        .addOnFailureListener {
                                            contractDetails = mapOf("Error" to "Failed to fetch contract details.")
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(roommates.size) { index ->
                        RoommateItem(
                            user = roommates[index],
                            navHostController = navController
                        )
                    }

                    if (roommates.isEmpty()) {
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

