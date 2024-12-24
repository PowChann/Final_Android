package com.example.finalandroidapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class AppointmentViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()



    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _myAppointments = MutableLiveData<List<Map<String, Any>>>()
    val myAppointments: LiveData<List<Map<String, Any>>> get() = _myAppointments

    private val _appointmentsWithMe = MutableLiveData<List<Map<String, Any>>>()
    val appointmentsWithMe: LiveData<List<Map<String, Any>>> get() = _appointmentsWithMe






    fun fetchAppointments(currentUserId: String) {
        _isLoading.value = true

        firestore.collection("appointments")
            .whereEqualTo("currentUserId", currentUserId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val myAppointmentsList = querySnapshot.documents.map { it.data ?: emptyMap() }
                _myAppointments.value = myAppointmentsList
            }
            .addOnFailureListener {
                _myAppointments.value = emptyList()
            }

        firestore.collection("appointments")
            .whereEqualTo("otherUserId", currentUserId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val appointmentsWithMeList = querySnapshot.documents.map { it.data ?: emptyMap() }
                _appointmentsWithMe.value = appointmentsWithMeList
            }
            .addOnFailureListener {
                _appointmentsWithMe.value = emptyList()
            }
            .addOnCompleteListener {
                _isLoading.value = false
            }
    }


    fun scheduleAppointment(
        selectedDate: String,
        selectedTime: String,
        currentUserId: String?,
        otherUserId: String?,
        onSuccess: (String, String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (selectedDate.isEmpty() || selectedTime.isEmpty()) {
            throw IllegalArgumentException("Date and time must be selected")
        }

        val appointmentId = firestore.collection("appointments").document().id
        val appointmentData = hashMapOf(
            "appointmentId" to appointmentId,
            "currentUserId" to (currentUserId ?: "Unknown"),
            "otherUserId" to (otherUserId ?: "Unknown"),
            "date" to selectedDate,
            "time" to selectedTime
        )

        firestore.collection("appointments")
            .document(appointmentId)
            .set(appointmentData)
            .addOnSuccessListener {
                onSuccess(selectedDate, selectedTime)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}

