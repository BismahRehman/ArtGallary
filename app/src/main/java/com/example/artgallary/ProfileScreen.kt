package com.example.artgallary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun ProfileScreen() {
    var username by remember { mutableStateOf("first profile") }
    var bio by remember { mutableStateOf("hi I’m apointer") }
    var profileImageUrl by remember { mutableStateOf("") }
    var posts by remember { mutableStateOf<List<String>>(emptyList()) }

    Column(modifier = Modifier.fillMaxSize()) {

        // 🔹 User Info Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = R.drawable.artgallaryappicon, // replace with profileImageUrl if you want dynamic
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(text = username, style = MaterialTheme.typography.titleMedium)
                Text(text = bio, style = MaterialTheme.typography.bodySmall)
            }
        }

        // 🔹 Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ProfileStat("Posts", posts.size.toString())
            ProfileStat("Followers", "120") // TODO: fetch later
            ProfileStat("Following", "80")  // TODO: fetch later
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 Edit Profile Button
        Button(
            onClick = { /* TODO: Edit Profile */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text("Edit Profile")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Fetch posts from Firestore
        LaunchedEffect(Unit) {
            val snapshot = FirebaseFirestore.getInstance().collection("posts").get().await()
            posts = snapshot.documents.mapNotNull { it.getString("imageUrl") }
        }

        // 🔹 Posts Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(posts) { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Post",
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun ProfileStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleMedium)
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}
