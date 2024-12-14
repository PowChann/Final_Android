package com.example.finalandroidapplication.screens



import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.finalandroidapplication.R
import com.example.finalandroidapplication.model.HouseItem
import com.example.finalandroidapplication.navigation.Routes
import com.example.finalandroidapplication.viewmodel.HouseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindHouse(navController: NavHostController) {
    val houseViewModel: HouseViewModel = viewModel()
    val housesAndUsers by houseViewModel.houses.observeAsState(emptyList())

    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("") }
    var inputValue by remember { mutableStateOf("") }
    var selectedPriceRange by remember { mutableStateOf("") }
    var selectedRoomType by remember { mutableStateOf("") }
    var selectedAmenities = remember { mutableStateListOf<String>() }
    val filters = listOf("Location", "Price", "Room Type", "Number of People", "Amenities")


    LaunchedEffect(Unit) {
        houseViewModel.fetchHousesWithUsers()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Find House", fontSize = 24.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Routes.AddHouse.routes) {
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
                Icon(Icons.Filled.Add, contentDescription = "Add House")
            }
        },

        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                LazyColumn {
                    items(housesAndUsers) { (house, user) ->
                        HouseItem(house, user, navController)
                    }
                }
            //AlertDialog
                if (showFilterDialog) {
                    AlertDialog(
                        onDismissRequest = { showFilterDialog = false },
                        containerColor = MaterialTheme.colorScheme.onPrimary,

                                title = { Text("Select Filter", color = MaterialTheme.colorScheme.primary, fontSize = 24.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold) },
                        text = {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    filters.forEach { filter ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp)
                                                .clickable {
                                                    selectedFilter = filter
                                                    inputValue = ""
                                                    selectedPriceRange = ""
                                                    selectedRoomType = ""
                                                    selectedAmenities.clear()
                                                }
                                        ) {
                                            RadioButton(
                                                selected = selectedFilter == filter,
                                                onClick = { selectedFilter = filter }
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(filter, fontSize = 16.sp)
                                        }
                                    }

                                    Divider(
                                        color = MaterialTheme.colorScheme.primary,
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(vertical = 16.dp)
                                    )
                                    when (selectedFilter) {
                                        "Location" -> {
                                            OutlinedTextField(
                                                value = inputValue,
                                                onValueChange = { inputValue = it },
                                                label = { Text("Enter Location") },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                        "Price" -> {
                                            Column {
                                                listOf("<2.000.000", "2.000.000 - 5.000.000", ">5.000.000").forEach { range ->
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clickable { selectedPriceRange = range }
                                                    ) {
                                                        RadioButton(
                                                            selected = selectedPriceRange == range,
                                                            onClick = { selectedPriceRange = range }
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(range, fontSize = 16.sp)
                                                    }
                                                }
                                            }
                                        }
                                        "Room Type" -> {
                                            Column {
                                                listOf("Private", "Shared").forEach { type ->
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clickable { selectedRoomType = type }
                                                    ) {
                                                        RadioButton(
                                                            selected = selectedRoomType == type,
                                                            onClick = { selectedRoomType = type }
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(type, fontSize = 16.sp)
                                                    }
                                                }
                                            }
                                        }
                                        "Number of People" -> {
                                            OutlinedTextField(
                                                value = inputValue,
                                                onValueChange = { inputValue = it },
                                                label = { Text("Enter Number of People") },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                        "Amenities" -> {
                                            val amenities = listOf("Wi-Fi", "Parking", "Gym", "Pool", "Air Conditioner", "Laundry")
                                            amenities.forEach { amenity ->
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Checkbox(
                                                        checked = selectedAmenities.contains(amenity),
                                                        onCheckedChange = {
                                                            if (it) selectedAmenities.add(amenity)
                                                            else selectedAmenities.remove(amenity)
                                                        }
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(amenity, fontSize = 16.sp)
                                                }
                                            }
                                        }

                                    }
                                }

                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                when (selectedFilter) {
                                    "Location" -> houseViewModel.searchHouses(location = inputValue)
                                    "Price" -> houseViewModel.searchHouses(price = selectedPriceRange)
                                    "Room Type" -> houseViewModel.searchHouses(roomType = selectedRoomType)
                                    "Number of People" -> houseViewModel.searchHouses(numOfPeople = inputValue)
                                    "Amenities" -> houseViewModel.searchHouses(amenities = selectedAmenities)
                                }
                                showFilterDialog = false
                            }) {
                                Text("Apply")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showFilterDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }


                //Add more FLoating Button
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FloatingActionButton(
                        onClick = {
                            showFilterDialog = true
                        },
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ) {
                        Icon(Icons.Filled.Search, contentDescription = "Filter")
                    }
                }
            }
        }
    )
}


