import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.finalandroidapplication.viewmodel.ProfileViewModel

@Composable
fun AppointmentCard(
    appointment: Map<String, Any>,
    isMine: Boolean,
    profileViewModel: ProfileViewModel
) {
    val userId = if (isMine) {
        appointment["otherUserId"] as? String ?: "Unknown User"
    } else {
        appointment["currentUserId"] as? String ?: "Unknown User"
    }

    var username by remember { mutableStateOf("Loading...") }

    LaunchedEffect(userId) {
        profileViewModel.fetchUsername(userId) { fetchedUsername ->
            username = fetchedUsername ?: "Unknown User"
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon on the left
        Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = "Calendar Icon",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.width(16.dp)) // Space between icon and content

        // Date and time in the center
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Date: ${appointment["date"]}",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Time: ${appointment["time"]}",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(16.dp)) // Space between content and username

        // Username on the right
        Text(
            text = username,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
