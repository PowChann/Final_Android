package com.example.finalandroidapplication.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.finalandroidapplication.model.UserModel
import com.example.finalandroidapplication.navigation.Routes
import com.example.finalandroidapplication.utils.showDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractTemplate(navHostController: NavHostController) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val currentUser = remember { mutableStateOf<UserModel?>(null) }
    var nameA by remember { mutableStateOf("") }
    var phoneA by remember { mutableStateOf("") }
    var nameB by remember { mutableStateOf("") }
    var phoneB by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var rent by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("1 tháng (1 month)") }
    var costSplit by remember { mutableStateOf("Chia đều") }
    var showDialog by remember { mutableStateOf(false) }
    var agreed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().currentUser?.uid ?: "")
            .get()
            .addOnSuccessListener { userDoc ->
                currentUser.value = userDoc.toObject(UserModel::class.java)
            }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Contract Template", fontSize = 24.sp, fontWeight = FontWeight.Bold) },
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
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(scrollState)
                    .fillMaxSize()
            ) {
                HeaderContract()
                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Party A Information
                Text(
                    "Thông tin Bên A (Party A) - Chủ nhà (The Lessor)",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                InputField(
                    "Họ và tên (Full Name)", value = nameA, onValueChange = { nameA = it }
                )
                InputField(
                    "Số điện thoại (Phone Number)",
                    value = phoneA,
                    onValueChange = { phoneA = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: Party B Information
                Text(
                    "Thông tin Bên B (Party B) - Người thuê (The Lessee)",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                InputField(
                    "Họ và tên (Full Name)", value = nameB, onValueChange = { nameB = it }
                )
                InputField(
                    "Số điện thoại (Phone Number)",
                    value = phoneB,
                    onValueChange = { phoneB = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: Contract Details
                Text(
                    "Chi tiết hợp đồng (Contract Details)",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                InputField(
                    "Địa chỉ (Address)", value = address, onValueChange = { address = it }
                )
                InputField(
                    "Tiền thuê phòng (Room Rent)",
                    value = rent,
                    onValueChange = { rent = it }
                )
                DatePickerField(
                    label = "Ngày bắt đầu thuê (Start Date)",
                    value = startDate,
                    onValueChange = { startDate = it },
                    context = LocalContext.current
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Thời hạn thuê (Rental Duration)",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                DropdownMenuField(
                    options = listOf("1 tháng (1 month)", "6 tháng (6 months)", "12 tháng (12 months)"),
                    selectedOption = duration,
                    onOptionSelected = { duration = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Cost Sharing Method
                Text(
                    text = "Phương thức phân chia chi phí (Cost Sharing Method)",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = costSplit == "Chia đều",
                            onClick = { costSplit = "Chia đều" }
                        )
                        Text(
                            text = "Chia đều (Equally)",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = costSplit == "Chia theo mức sử dụng",
                            onClick = { costSplit = "Chia theo mức sử dụng" }
                        )
                        Text(
                            text = "Chia theo mức sử dụng (Usage-based)",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Continue Button
                Button(
                    onClick = {
                        if (nameA.isEmpty() || phoneA.isEmpty() || nameB.isEmpty() || phoneB.isEmpty() || address.isEmpty() || rent.isEmpty() || startDate.isEmpty()) {
                            Toast.makeText(context, "Please fill out all required fields.", Toast.LENGTH_SHORT).show()
                        } else {
                            showDialog = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = "Continue", color = Color.White)
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        confirmButton = {
                            Button(
                                onClick = {
                                    if (currentUser != null) {
                                        val contractData = hashMapOf(
                                            "userId" to currentUser.value?.uid,
                                            "partyA_name" to nameA,
                                            "partyA_phone" to phoneA,
                                            "partyB_name" to nameB,
                                            "partyB_phone" to phoneB,
                                            "address" to address,
                                            "rent" to rent,
                                            "startDate" to startDate,
                                            "duration" to duration,
                                            "costSplit" to costSplit
                                        )
                                        FirebaseFirestore.getInstance().collection("contracts")
                                            .add(contractData)
                                            .addOnSuccessListener {
                                                Toast.makeText(context, "Contract saved successfully!", Toast.LENGTH_SHORT).show()
                                                showDialog = false
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(context, "Failed to save contract: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        navHostController.popBackStack()
                                    } else {
                                        Toast.makeText(context, "Failed to save contract", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                enabled = agreed,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text(text = "Agree & Confirm", color = Color.White)
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = { showDialog = false },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Text(text = "Cancel", color = Color.White)
                            }
                        },
                        title = {
                            Text(
                                text = "Summary of Contract Information",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        text = {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Thông tin Bên A (Party A):",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                                )
                                Text(
                                    text = "Họ và tên: ",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(text = nameA, color = Color.Black)

                                Text(
                                    text = "Số điện thoại: ",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(text = phoneA, color = Color.Black)

                                Spacer(modifier = Modifier.height(8.dp))

                                // Party B Information
                                Text(
                                    text = "Thông tin Bên B (Party B):",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                                )
                                Text(
                                    text = "Họ và tên: ",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(text = nameB, color = Color.Black)

                                Text(
                                    text = "Số điện thoại: ",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(text = phoneB, color = Color.Black)

                                Spacer(modifier = Modifier.height(8.dp))

                                // Contract Details
                                Text(
                                    text = "Chi tiết hợp đồng:",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                                )
                                Text(
                                    text = "Địa chỉ: ",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(text = address, color = Color.Black)

                                Text(
                                    text = "Tiền thuê phòng: ",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(text = rent, color = Color.Black)

                                Text(
                                    text = "Ngày bắt đầu thuê: ",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(text = startDate, color = Color.Black)

                                Text(
                                    text = "Thời hạn thuê: ",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(text = duration, color = Color.Black)

                                Text(
                                    text = "Phương thức chia tiền: ",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(text = costSplit, color = Color.Black)

                                Spacer(modifier = Modifier.height(16.dp))

                                // Agreement Checkbox
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = agreed,
                                        onCheckedChange = { agreed = it }
                                    )
                                    Text(text = "I have read and agree to the terms", color = Color.Black)
                                }
                            }
                        }
                    )
                }

            }
        }
    )
}



@Composable
fun HeaderContract() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "(SOCIALIST REPUBLIC OF VIETNAM)",
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = "Độc lập - Tự do - Hạnh phúc",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = "(Independence - Freedom - Happiness)",
            fontSize = 16.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(10.dp))
        Divider(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(), thickness = 2.dp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "HỢP ĐỒNG THUÊ NHÀ",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = "(HOUSE LEASE CONTRACT)",
            fontSize = 18.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Date: ${LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/YYYY"))}",
            fontSize = 16.sp,
            color = Color.Black,
            textAlign = TextAlign.Right
        )


    }
}

@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        Text(text = label, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun DropdownMenuField(options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true, // Đảm bảo TextField chỉ đọc
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }, // Kích hoạt DropdownMenu khi nhấn vào TextField
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) { // Cũng có thể mở/đóng từ nút Icon
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
@Composable
fun DatePickerField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    context: Context
) {
    val calendarIcon = Icons.Default.Event
    OutlinedTextField(
        value = value,
        onValueChange = { }, // Không thay đổi giá trị trực tiếp từ người dùng
        readOnly = true, // Đảm bảo TextField chỉ đọc
        label = { Text(label) },
        trailingIcon = {
            IconButton(onClick = {
                showDatePicker(context) { selectedDate ->
                    onValueChange(selectedDate)
                }
            }) {
                Icon(
                    imageVector = calendarIcon,
                    contentDescription = "Select Date"
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                showDatePicker(context) { selectedDate ->
                    onValueChange(selectedDate)
                }
            }
    )
}

