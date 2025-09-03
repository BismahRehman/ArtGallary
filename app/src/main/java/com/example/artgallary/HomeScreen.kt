package com.example.artgallary

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun HomeScreen() {

    var selectedTab by remember { mutableStateOf("Home") }
    val db = FirebaseFirestore.getInstance()
    val artList = remember { mutableStateListOf<ArtItem>() }

    // ✅ Firestore listener
    LaunchedEffect(Unit) {
        db.collection("posts")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    artList.clear()
                    for (doc in snapshot.documents) {
                        val item = doc.toObject(ArtItem::class.java)
                        if (item != null) {
                            artList.add(item)
                        }
                    }
                }
            }
    }


    Scaffold(
        topBar = { HomeTopBar() }
    ) { padding ->
        // Screen Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            ArtListScreen(artList = sampleArtList)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar() {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.artgallaryappicon),
                    contentDescription = "",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Art Gallery",
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                )
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color.White),
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "",
                    tint = Color.Black
                )
            }
            IconButton(onClick = {}) {
                Image(
                    painter = painterResource(id = R.drawable.artgallaryappicon),
                    contentDescription = "",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                )
            }
        }
    )
}

// ✅ 1. Data class for Art
data class ArtItem(
    val profilePicUrl: String = "",
    val userName: String = "",
    val imageUrl: String = "",
    val artName: String = "",
    val artistName: String = ""
)



val sampleArtList = listOf(
    ArtItem(
        profilePicUrl = "https://res.cloudinary.com/demo/image/upload/sample.jpg",
        userName = "Alice",
        imageUrl = "https://res.cloudinary.com/demo/image/upload/starry-night.jpg",
        artName = "Starry Night",
        artistName = "Vincent van Gogh"
    ),


    ArtItem(
        profilePicUrl = "https://res.cloudinary.com/demo/image/upload/sample.jpg",
        userName = "Alice",
        imageUrl = "https://res.cloudinary.com/demo/image/upload/starry-night.jpg",
        artName = "Starry Night",
        artistName = "Vincent van Gogh"
    ),


    ArtItem(
        profilePicUrl = "https://res.cloudinary.com/demo/image/upload/sample.jpg",
        userName = "Alice",
        imageUrl = "https://res.cloudinary.com/demo/image/upload/starry-night.jpg",
        artName = "Starry Night",
        artistName = "Vincent van Gogh"
    )
)

@Composable
fun ArtCard(item: ArtItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        // ✅ Top Row → Profile
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = item.profilePicUrl,
                contentDescription = "Profile Pic",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(item.userName, style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ✅ Art Image
        AsyncImage(
            model = item.imageUrl,
            contentDescription = item.artName,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))

        // ✅ Like + Comment Row
        var liked by remember { mutableStateOf(false) }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { liked = !liked }) {
                Icon(
                    imageVector = if (liked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (liked) Color.Red else Color.Black
                )
            }
            IconButton(onClick = { /* TODO: open comment screen */ }) {
                Icon(Icons.Outlined.Comment, contentDescription = "Comment")
            }
        }

        // ✅ Art info
        Text(text = item.artName, style = MaterialTheme.typography.titleMedium)
        Text(text = "by ${item.artistName}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}

// ✅ 4. List (like Instagram feed)
@Composable
fun ArtListScreen(artList: List<ArtItem>) {
    LazyColumn {
        items(artList) { art ->
            ArtCard(item = art)
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}