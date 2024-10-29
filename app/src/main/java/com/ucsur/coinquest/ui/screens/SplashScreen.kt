package com.ucsur.coinquest.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucsur.coinquest.R
import kotlinx.coroutines.delay
import androidx.compose.ui.draw.alpha


@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit = {}
) {
    var isVisible by remember { mutableStateOf(false) }

    // Animación de escala para el logo
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.5f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "scale"
    )

    // Animación de alpha para el título
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            delayMillis = 500 // El título aparece medio segundo después
        ),
        label = "alpha"
    )

    // Efecto para controlar la duración del splash
    LaunchedEffect(key1 = true) {
        isVisible = true
        delay(3000) // Duración total del splash: 3 segundos
        onSplashFinished()
    }

    // Contenido del Splash
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.applogo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(250.dp)
                    .scale(scale),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Coin Quest",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(alpha)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}