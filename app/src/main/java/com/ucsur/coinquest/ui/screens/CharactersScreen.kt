package com.ucsur.coinquest.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucsur.coinquest.R
import com.ucsur.coinquest.model.GameCharacter

@Composable
fun CharactersScreen(
    onNavigateBack: () -> Unit,
    onCharacterSelected: (GameCharacter) -> Unit
) {
    var selectedCharacter by remember { mutableStateOf<GameCharacter?>(null) }
    var showNameDialog by remember { mutableStateOf(false) }
    var customName by remember { mutableStateOf("") }
    var isCharacterConfirmed by remember { mutableStateOf(false) }

    val characters = listOf(
        GameCharacter(1, "Juffy", R.drawable.juffy),
        GameCharacter(2, "Criss", R.drawable.criss),
        GameCharacter(3, "Ander", R.drawable.ander),
        GameCharacter(4, "Tiff", R.drawable.tiff)
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver"
                )
            }

            Text(
                text = "Selecciona tu Personaje",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(characters) { character ->
                    CharacterCard(
                        character = character,
                        isSelected = selectedCharacter?.id == character.id,
                        onClick = {
                            selectedCharacter = character
                            isCharacterConfirmed = false
                            showNameDialog = true
                        }
                    )
                }
            }

            selectedCharacter?.let { character ->
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Personaje seleccionado: ${character.customName ?: character.defaultName}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { showNameDialog = true },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Text("Cambiar Nombre")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onCharacterSelected(character) },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            "¡Comenzar Juego!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    if (showNameDialog) {
        NameDialog(
            defaultName = selectedCharacter?.defaultName ?: "",
            currentName = customName,
            onNameConfirm = { name ->
                selectedCharacter?.customName = name.takeIf { name.isNotBlank() }
                customName = name
                showNameDialog = false
                isCharacterConfirmed = true
            },
            onDismiss = { showNameDialog = false }
        )
    }
}

@Composable
fun CharacterCard(
    character: GameCharacter,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale = animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        label = "scale"
    )

    Card(
        modifier = Modifier
            .padding(8.dp)
            .scale(scale.value)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = character.imageRes),
                contentDescription = "Character ${character.defaultName}",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(
                        width = if (isSelected) 3.dp else 0.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = CircleShape
                    ),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = character.defaultName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun NameDialog(
    defaultName: String,
    currentName: String,
    onNameConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(currentName.ifEmpty { defaultName }) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Personaliza tu nombre")
        },
        text = {
            Column {
                Text("Ingresa tu nombre o deja el valor por defecto:")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onNameConfirm(name) }) {
                Text("Confirmar", color = MaterialTheme.colorScheme.onSurface)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    )
}