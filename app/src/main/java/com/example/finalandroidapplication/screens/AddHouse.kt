package com.example.finalandroidapplication.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.finalandroidapplication.viewmodel.PostViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHouse(navController: NavHostController, uid: String) {
    val postViewModel: PostViewModel = viewModel()

    var location by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var roomType by remember { mutableStateOf("Private") }
    var numOfPeople by remember { mutableStateOf("") }
    val selectedAmenities = remember { mutableStateListOf<String>() }

    val context = LocalContext.current




    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add House", fontSize = 24.sp, fontWeight = FontWeight.Bold) },
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(Color.White)
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                // Location
                TextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White
                    ),

                )
                Spacer(modifier = Modifier.height(16.dp))

                // Price
                TextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Room Type (Private or Shared)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Room Type", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                RadioButton(
                                    selected = roomType == "Private",
                                    onClick = { roomType = "Private" }
                                )
                                Text(
                                    text = "Private",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 8.dp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                RadioButton(
                                    selected = roomType == "Shared",
                                    onClick = { roomType = "Shared" }
                                )
                                Text(
                                    text = "Shared",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 8.dp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Number of People
                TextField(
                    value = numOfPeople,
                    onValueChange = { numOfPeople = it },
                    label = { Text("Maximum Number of People") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Amenities
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Amenities",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // List of amenities
                        val amenities = listOf("Wi-Fi", "Parking", "Gym", "Pool", "Air Conditioner", "Laundry")

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Distribute amenities into 2 columns
                            val amenitiesPerRow = 2
                            for (i in amenities.indices step amenitiesPerRow) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    for (j in i until (i + amenitiesPerRow).coerceAtMost(amenities.size)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f).padding(end = if (j % 2 == 0) 8.dp else 0.dp)
                                        ) {
                                            Checkbox(
                                                checked = selectedAmenities.contains(amenities[j]),
                                                onCheckedChange = {
                                                    if (it) {
                                                        selectedAmenities.add(amenities[j]) // Add selected amenity
                                                    } else {
                                                        selectedAmenities.remove(amenities[j]) // Remove unselected amenity
                                                    }
                                                }
                                            )
                                            Text(
                                                text = amenities[j],
                                                modifier = Modifier.padding(start = 8.dp),
                                                fontSize = 16.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }



                Spacer(modifier = Modifier.height(32.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (location.isBlank() || price.isBlank() || numOfPeople.isBlank()) {
                            Toast.makeText(
                                context,
                                "Please fill all required fields.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            postViewModel.uploadHouse(
                                uid,
                                location,
                                price,
                                roomType,
                                numOfPeople,
                                selectedAmenities.toSet()
                            )
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Submit", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    )
}

