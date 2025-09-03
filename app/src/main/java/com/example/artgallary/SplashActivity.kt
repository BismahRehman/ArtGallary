package com.example.artgallary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.artgallary.ui.theme.ArtGallaryTheme
import com.example.artgallery.LoginScreen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArtGallaryTheme {
                val navController = rememberNavController()
                AppNavHost(navController)


            }
        }
    }
}


@Composable
fun SplashScreen(
    onNavigate: (Boolean) -> Unit // callback: true = logged in, false = not logged in
) {
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 2000),
        label = "splashAlpha"
    )

    // Start animation when screen loads
    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2500) // wait 2.5 sec

        // 👇 FirebaseAuth check karo
        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
       onNavigate(isLoggedIn)
    }

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // App Logo
            Image(
                painter = painterResource(id = R.drawable.artgallaryappicon),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(120.dp)
                    .alpha(alphaAnim.value)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Art Gallery",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
                modifier = Modifier.alpha(alphaAnim.value)
            )
        }
    }
}

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen { isLoggedIn ->
                if (isLoggedIn) {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                } else {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            }
        }
        composable("home") { MyApp() }
        composable("login") { LoginScreen(onLoginSuccess = {}, onSignupClick = {}, auth = FirebaseAuth.getInstance())}

        }
}



@Preview
@Composable
fun SplashScreenPreview() {
    val navController: NavHostController = rememberNavController()
    AppNavHost(navController)
}