package com.example.finalandroidapplication.screens

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.example.finalandroidapplication.R
import com.example.finalandroidapplication.navigation.Routes
import com.example.finalandroidapplication.viewmodel.AuthViewModel
import com.example.finalandroidapplication.viewmodel.ProfileViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(navController: NavHostController, uid: String) {
    val authViewModel: AuthViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val user by profileViewModel.userData.observeAsState(null)
    val context = LocalContext.current
    val activity = context as? Activity

    var isLoading by remember { mutableStateOf(true) }
    var isEditing by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("Loading...") }
    var name by remember { mutableStateOf("Loading...") }
    var gender by remember { mutableStateOf("Loading...") }
    var phone by remember { mutableStateOf("Loading...") }
    var career by remember { mutableStateOf("Loading...") }
    var age by remember { mutableStateOf("Loading...") }
    var habits by remember { mutableStateOf(mapOf<String, String>()) }
    var bio by remember { mutableStateOf("Loading...") }
    var avatarUrl by remember { mutableStateOf<Uri?>(null) }

    var showOTPDialog by remember { mutableStateOf(false) }
    var phoneInput by remember { mutableStateOf("") }
    var otpInput by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf("") }


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        avatarUrl = uri
    }

    LaunchedEffect(uid) {
        profileViewModel.fetchUserProfile(uid)
    }

    LaunchedEffect(user) {
        user?.let {
            username = it.username
            name = it.name
            gender = it.gender
            phone = it.phone
            career = it.career
            age = it.age
            bio = it.bio
            habits = it.habits
            avatarUrl = Uri.parse(it.avatarUrl)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),

            )
        },
        floatingActionButton = {
            if (!isEditing) {
                FloatingActionButton(
                    onClick = {
//                        authViewModel.logout()
                        navController.navigate(Routes.Login.routes) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    },
                    containerColor = Color.White,
                    contentColor = Color.Black,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 12.dp
                    )
                ) {
                    Icon(Icons.Filled.Logout, contentDescription = "Logout")
                }
            }
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = when {
                            avatarUrl != null -> avatarUrl
                            user?.avatarUrl?.isNotBlank() == true -> user?.avatarUrl
                            else -> null
                        }
                    ),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape)
                        .background(Color.White)
                        .then(
                            if (isEditing) {
                                Modifier.clickable {
                                    launcher.launch("image/*")
                                }
                            } else {
                                Modifier
                            }
                        ),
                    contentScale = ContentScale.Fit,
                    colorFilter = if (avatarUrl == null) ColorFilter.tint(Color.White) else null
                )



                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier
                        .padding(8.dp)
                        .border(
                            width = 2.dp,
                            color = if (user?.isVerified == true) Color.Green else Color.Red,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .clickable(enabled = user?.isVerified == false || user?.isVerified == null) {
                            showOTPDialog = true
                        },
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Icon
                        Icon(
                            imageVector = if (user?.isVerified == true) Icons.Filled.CheckCircle else Icons.Filled.Close,
                            contentDescription = if (user?.isVerified == true) "Verified Icon" else "Not Verified Icon",
                            tint = if (user?.isVerified == true) Color.Green else Color.Red,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Text
                        Text(
                            text = if (user?.isVerified == true) "VERIFIED" else "NOT VERIFIED",
                            color = if (user?.isVerified == true) Color.Green else Color.Red,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                if (showOTPDialog) {
                    AlertDialog(
                        onDismissRequest = { showOTPDialog = false },
                        title = { Text("Phone Verification") },
                        text = {
                            Column {
                                TextField(
                                    value = phoneInput,
                                    onValueChange = { phoneInput = it },
                                    label = { Text("Enter phone number") }
                                )
                                if (verificationId.isNotBlank()) {
                                    TextField(
                                        value = otpInput,
                                        onValueChange = { otpInput = it },
                                        label = { Text("Enter OTP") }
                                    )
                                }
                            }
                        },
                        confirmButton = {
                            if (verificationId.isBlank()) {
                                Button(onClick = {
                                    activity?.let {
                                        profileViewModel.sendOTP(
                                            activity = it,
                                            phone = phoneInput,
                                            onCodeSent = { id ->
                                                verificationId = id
                                                Toast.makeText(context, "OTP sent!", Toast.LENGTH_SHORT).show()
                                            },
                                            onError = { error ->
                                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    } ?: Toast.makeText(context, "Activity is null", Toast.LENGTH_SHORT).show()
                                }) {
                                    Text("Send OTP")
                                }
                            } else {
                                Button(onClick = {
                                    profileViewModel.verifyAndUpdateUser(
                                        uid = uid,
                                        otp = otpInput,
                                        onVerified = {
                                            showOTPDialog = false
                                            Toast.makeText(context, "Authenticated!", Toast.LENGTH_SHORT).show()
                                        },
                                        onError = { error ->
                                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }) {
                                    Text("Verify OTP")
                                }
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showOTPDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))




                ProfileField("Username", username, isEditing, { username = it })
                ProfileField("Full Name", name, isEditing, { name = it })
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Gender", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Male", "Female").forEach { genderOption ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (isEditing) {
                                                gender = if (gender == genderOption) "" else genderOption
                                            }
                                        }
                                ) {
                                    RadioButton(
                                        selected = gender == genderOption,
                                        onClick = {
                                            if (isEditing) {
                                                gender = if (gender == genderOption) "" else genderOption
                                            }
                                        },
                                        enabled = isEditing
                                    )
                                    Text(
                                        text = genderOption,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                ProfileField("Phone Number", phone, isEditing, { phone = it }, isNumeric = true)
                ProfileField("Career", career, isEditing, { career = it })
                ProfileField("Age", age, isEditing, { age = it }, isNumeric = true)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Habits", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                        val habitOptions = mapOf(
                            "Sleeping" to listOf("Early Sleeper", "Late Sleeper"),
                            "Smoking" to listOf("Smoker", "Non-Smoker"),
                            "Diet" to listOf("Vegetarian", "Non-Vegetarian"),
                            "Pets" to listOf("Pet Lover", "No Pets"),
                            "Social" to listOf("Stay Home", "Go Out")
                        )

                        habitOptions.forEach { (category, options) ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(category, fontSize = 20.sp, fontWeight = FontWeight.Medium)

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                options.forEach { option ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                if (isEditing) {
                                                    habits = habits.toMutableMap().apply {
                                                        this[category] = if (habits[category] == option) "" else option
                                                    }
                                                }
                                            }
                                    ) {
                                        RadioButton(
                                            selected = habits[category] == option,
                                            onClick = {
                                                if (isEditing) {
                                                    habits = habits.toMutableMap().apply {
                                                        this[category] = if (habits[category] == option) "" else option
                                                    }
                                                }
                                            },
                                            enabled = isEditing
                                        )
                                        Text(
                                            text = option,
                                            fontSize = 18.sp,
                                            modifier = Modifier.padding(start = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                ProfileField("Bio", bio, isEditing, { bio = it })

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (isEditing) {
                            // Chỉ upload ảnh nếu người dùng chọn ảnh mới
                            val imageUriToUpload = if (avatarUrl != null && avatarUrl!!.scheme in listOf("content", "file")) {
                                avatarUrl
                            } else {
                                null
                            }

                            profileViewModel.uploadImageAndSaveProfile(
                                uid = uid,
                                imageUri = imageUriToUpload,
                                name = name,
                                gender = gender,
                                phone = phone,
                                career = career,
                                age = age,
                                bio = bio,
                                username = username,
                                habits = habits,
                                onSuccess = {
                                    isEditing = false
                                    profileViewModel.fetchUserProfile(uid)
                                    Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                                },
                                onError = { errorMessage ->
                                    Toast.makeText(context, "Failed to update profile: $errorMessage", Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            isEditing = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isEditing) "Save Profile" else "Edit Profile")
                }
            }
        }
    )
}

@Composable
fun ProfileField(
    label: String,
    value: String,
    isEditable: Boolean,
    onValueChange: (String) -> Unit,
    isNumeric: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(text = label, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        if (isEditable) {
            OutlinedTextField(
                value = value,
                onValueChange = { if (!isNumeric || it.all { char -> char.isDigit() }) onValueChange(it) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = if (isNumeric) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default
            )
        } else {
            Text(text = value, fontSize = 20.sp, modifier = Modifier.padding(top = 4.dp, bottom = 4.dp))
            Divider(color = Color.Gray, thickness = 1.dp)
        }
    }
}


