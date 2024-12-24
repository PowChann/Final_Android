package com.example.finalandroidapplication.model

import android.net.Uri
import android.util.Log
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HouseItem(
    house: HouseModel,
    user: UserModel,
    navHostController: NavHostController
) {
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
                    contentScale = ContentScale.Crop, // Điều chỉnh avatar vừa khung
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
                    model = house.imageUrl, // Dùng URL trực tiếp thay vì parse Uri
                    onError = {
                        Log.e("ImageLoading", "Lỗi load ảnh: ${it.result.throwable}")
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
        }
    }
}
