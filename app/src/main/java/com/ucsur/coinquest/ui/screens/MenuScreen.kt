package com.ucsur.coinquest.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucsur.coinquest.R

@Composable
fun MenuScreen() {
    var selectedButton by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mensaje de bienvenida
            Text(
                text = "¡Bienvenido a",
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 32.dp)
            )

            // Logo
            Image(
                painter = painterResource(id = R.drawable.applogo),
                contentDescription = "Game Logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(vertical = 8.dp)
            )

            // Título
            Text(
                text = "Coin Quest!",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Botones
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                MenuButton("Jugar", selectedButton == "jugar") {
                    selectedButton = "jugar"
                }
                MenuButton("Personajes", selectedButton == "personajes") {
                    selectedButton = "personajes"
                }
                MenuButton("Puntajes", selectedButton == "puntajes") {
                    selectedButton = "puntajes"
                }
                MenuButton("Créditos", selectedButton == "creditos") {
                    selectedButton = "creditos"
                }
            }
        }
    }
}

@Composable
fun MenuButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale = animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        label = "scale"
    )

    Button(
        onClick = onClick,
        modifier = Modifier
            .scale(scale.value)
            .width(220.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ),
        shape = RoundedCornerShape(25.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        )
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}