package com.example.finalandroidapplication.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesAndPolicies(navHostController: NavHostController) {
    val scrollState = rememberScrollState()
    val db = FirebaseFirestore.getInstance()
    var isEditing by remember { mutableStateOf(false) }

    var rules by remember {
        mutableStateOf(
            mutableListOf(
                "Timing" to "All members must adhere to the lights-off rule at 11 PM.",
                "Cleanliness" to "Common areas must be cleaned weekly according to the assigned schedule.",
                "Expenses" to "Electricity, water, and internet bills will be equally divided among members.",
                "Noise" to "No loud noises are allowed from 10 PM to 6 AM to maintain a peaceful environment.",
                "Guests" to "Guests are allowed only during daytime hours and must be approved by all members."
            )
        )
    }


    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        val snapshot = db.collection("rules")
            .document("sharedRules")
            .get()
            .await()

        val fetchedRules = snapshot.get("rules") as? List<Map<String, String>>
        fetchedRules?.let {
            rules = it.map { rule ->
                (rule["title"] ?: "") to (rule["description"] ?: "")
            }.toMutableList()
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Rules And Policies", fontSize = 24.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { innerPadding ->
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp)
                        .verticalScroll(scrollState)
                        .fillMaxSize()
                ) {
                    rules.forEachIndexed { index, rule ->
                        RuleField(
                            title = rule.first,
                            description = rule.second,
                            isEditable = isEditing,
                            onValueChange = { newDescription ->
                                rules = rules.toMutableList().apply {
                                    this[index] = this[index].copy(second = newDescription)
                                }
                            },
                            onTitleChange = { newTitle ->
                                rules = rules.toMutableList().apply {
                                    this[index] = this[index].copy(first = newTitle)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Button(
                        onClick = {
                            if (isEditing) {
                                val rulesMap = rules.map { mapOf("title" to it.first, "description" to it.second) }
                                db.collection("rules")
                                    .document("sharedRules")
                                    .set(mapOf("rules" to rulesMap))
                            }
                            isEditing = !isEditing
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(if (isEditing) "Save Changes" else "Edit Rules", color = Color.White)
                    }
                }
            }
        }
    )
}

@Composable
fun RuleField(
    title: String,
    description: String,
    isEditable: Boolean,
    onValueChange: (String) -> Unit,
    onTitleChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (isEditable) {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Title") },
                textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Description") }
            )
        } else {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
