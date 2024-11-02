package com.ucsur.coinquest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ucsur.coinquest.model.Score
import com.ucsur.coinquest.viewmodel.ScoresViewModel

@Composable
fun ScoresScreen(
    viewModel: ScoresViewModel,
    onNavigateBack: () -> Unit
) {
    val scores = viewModel.scores.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver"
            )
        }

        Text(
            text = "Tabla de Puntajes",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        if (scores.isEmpty()) {
            // Mostrar mensaje cuando no hay puntajes
            Text(
                text = "No hay puntajes registrados",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn {
                items(scores) { score ->
                    ScoreItem(score = score)
                }
            }
        }
    }
}

@Composable
private fun ScoreItem(score: Score) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = score.playerName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Nivel ${score.level}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = "${score.score}",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}