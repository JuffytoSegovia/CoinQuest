package com.ucsur.coinquest.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucsur.coinquest.R

@Composable
fun CreditsScreen(
    onNavigateBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botón de retroceso
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver"
                )
            }
        }

        // Título principal
        Text(
            text = "Créditos",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        // Sección Desarrolladores
        Text(
            text = "Desarrolladores",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        DeveloperCard(
            name = "Tifany Ramos Espinoza",
            description = "Estudiante de Ingeniería de sistemas e informática de la UPB y UCSUR. ",
            linkedinUrl = "https://www.linkedin.com/in/tifany-brissette-ramos-espinoza-5077211b5/",
            instagramUrl = "https://www.instagram.com/_tifany_ramos/"
        )

        Spacer(modifier = Modifier.height(24.dp))

        DeveloperCard(
            name = "Junior Segovia Chalco",
            description = "Estudiante de Ingeniería de sistemas e informática de la UPB y UCSUR.",
            linkedinUrl = "https://www.linkedin.com/in/juniorsegovia/",
            instagramUrl = "https://www.instagram.com/juffyto/"
        )

        // Sección Recursos
        Text(
            text = "Recursos Utilizados",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 32.dp, bottom = 16.dp)
        )

        ResourcesSection()
    }
}

@Composable
private fun DeveloperCard(
    name: String,
    description: String,
    linkedinUrl: String,
    instagramUrl: String
) {
    val uriHandler = LocalUriHandler.current
    val imageRes = if (name.contains("Tifany")) R.drawable.tifany else R.drawable.junior

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Foto de $name",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { uriHandler.openUri(linkedinUrl) }) {
                    Image(
                        painter = painterResource(id = R.drawable.linkedin),
                        contentDescription = "LinkedIn",
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(32.dp))

                IconButton(onClick = { uriHandler.openUri(instagramUrl) }) {
                    Image(
                        painter = painterResource(id = R.drawable.instagram),
                        contentDescription = "Instagram",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ResourcesSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            ResourceCategory(
                title = "Desarrollo",
                items = listOf(
                    ResourceItem(
                        name = "Android Studio con Kotlin",
                        description = "IDE principal para el desarrollo"
                    ),
                    ResourceItem(
                        name = "Jetpack Compose",
                        description = "Framework UI moderno para Android"
                    ),
                    ResourceItem(
                        name = "Firebase",
                        url = "https://firebase.google.com",
                        description = "Base de datos y backend"
                    ),
                    ResourceItem(
                        name = "GitHub",
                        description = "Control de versiones y repositorio"
                    )
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            ResourceCategory(
                title = "Diseño y Recursos Visuales",
                items = listOf(
                    ResourceItem(
                        name = "ChatGPT",
                        description = "Generación del logo del aplicativo"
                    ),
                    ResourceItem(
                        name = "Photopea",
                        url = "https://www.photopea.com/",
                        description = "Editor de imágenes online"
                    ),
                    ResourceItem(
                        name = "Flaticon",
                        url = "https://www.flaticon.es/",
                        description = "Iconos del aplicativo"
                    )
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            ResourceCategory(
                title = "Recursos de Audio",
                items = listOf(
                    ResourceItem(
                        name = "Pixabay Music",
                        url = "https://pixabay.com/es/music/",
                        description = "Música de fondo"
                    ),
                    ResourceItem(
                        name = "Sonidos MP3",
                        url = "https://www.sonidosmp3gratis.com/",
                        description = "Efectos de sonido"
                    )
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            ResourceCategory(
                title = "Bibliotecas Adicionales",
                items = listOf(
                    ResourceItem(
                        name = "Material Design 3",
                        description = "Componentes y sistema de diseño"
                    ),
                    ResourceItem(
                        name = "Coroutines",
                        description = "Programación asíncrona"
                    ),
                    ResourceItem(
                        name = "Navigation Component",
                        description = "Navegación entre pantallas"
                    ),
                    ResourceItem(
                        name = "DataStore",
                        description = "Almacenamiento de preferencias"
                    )
                )
            )
        }
    }
}

@Composable
private fun ResourceCategory(
    title: String,
    items: List<ResourceItem>
) {
    val isDarkTheme = isSystemInDarkTheme()  // Añadir esta línea

    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = if (isDarkTheme) Color(0xFFFF8A80) else MaterialTheme.colorScheme.primary,  // Rojo claro para modo oscuro
        modifier = Modifier.padding(bottom = 8.dp)
    )

    items.forEach { item ->
        ResourceItemRow(item)
    }
}

@Composable
private fun ResourceItemRow(
    item: ResourceItem,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    val isDarkTheme = isSystemInDarkTheme()  // Añadir esta línea

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "•",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(end = 8.dp)
            )

            Column {
                if (item.url != null) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isDarkTheme) Color(0xFF90CAF9) else MaterialTheme.colorScheme.primary,  // Azul claro para modo oscuro
                        modifier = Modifier.clickable { uriHandler.openUri(item.url) }
                    )
                } else {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                item.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

private data class ResourceItem(
    val name: String,
    val url: String? = null,
    val description: String? = null
) {
    constructor(name: String, description: String) : this(name, null, description)
}