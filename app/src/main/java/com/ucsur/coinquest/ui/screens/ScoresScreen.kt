package com.ucsur.coinquest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucsur.coinquest.model.Score
import com.ucsur.coinquest.viewmodel.ScoresViewModel
import java.util.Locale

// Función formatTime
private fun formatTime(timeInMillis: Long): String {
    val totalSeconds = timeInMillis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}

@Composable
fun ScoresScreen(
    viewModel: ScoresViewModel,
    onNavigateBack: () -> Unit
) {
    val level1Scores by viewModel.level1Scores.collectAsState()
    val level2Scores by viewModel.level2Scores.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Botón de retroceso
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver"
            )
        }

        // Título centrado con el formato estandarizado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Puntajes",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }

        // TabRow para seleccionar el nivel
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { viewModel.onTabSelected(0) },
                text = { Text("Nivel 1") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { viewModel.onTabSelected(1) },
                text = { Text("Nivel 2") }
            )
        }

        // Contenido según la pestaña seleccionada
        when (selectedTab) {
            0 -> ScoresList(scores = level1Scores, levelTitle = "Nivel 1")
            1 -> ScoresList(scores = level2Scores, levelTitle = "Nivel 2")
        }
    }
}

@Composable
private fun ScoresList(
    scores: List<Score>,
    levelTitle: String
) {
    Column {
        // Añadimos el título del nivel
        Text(
            text = "Top 5 - $levelTitle",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        if (scores.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay puntajes registrados para este nivel",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(scores.size) { index ->
                    ScoreItemWithRank(
                        score = scores[index],
                        rank = index + 1
                    )
                }
            }
        }
    }
}

@Composable
private fun ScoreItemWithRank(
    score: Score,
    rank: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Medalla o número de ranking
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        when (rank) {
                            1 -> Color(0xFFFFD700) // Oro
                            2 -> Color(0xFFC0C0C0) // Plata
                            3 -> Color(0xFFCD7F32) // Bronce
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = rank.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            // Información del jugador y tiempo
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = score.playerName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Personaje: ${score.characterName}",
                    style = MaterialTheme.typography.bodyMedium
                )
                // Añadimos el tiempo
                Text(
                    text = "Tiempo: ${formatTime(score.timeElapsed)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Puntaje y estrellas
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${score.score}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Row {
                    repeat(score.stars) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}