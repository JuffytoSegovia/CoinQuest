package com.ucsur.coinquest.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ucsur.coinquest.ui.screens.*
import com.ucsur.coinquest.viewmodel.GameViewModel
import com.ucsur.coinquest.viewmodel.GameViewModelFactory
import com.ucsur.coinquest.utils.SoundManager

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Menu : Screen("menu")
    object Game : Screen("game")
    object Characters : Screen("characters")
    object Scores : Screen("scores")
    object Settings : Screen("settings")
    object Credits : Screen("credits")
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    soundManager: SoundManager,  // Añadido soundManager
    onExitApp: () -> Unit = {}
) {
    // Crear el ViewModel usando el Factory
    val viewModel: GameViewModel = viewModel(
        factory = GameViewModelFactory(soundManager)
    )

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Menu.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Menu.route) {
            MenuScreen(
                onNavigateToGame = {
                    navController.navigate(Screen.Characters.route)
                },
                onNavigateToScores = { navController.navigate(Screen.Scores.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToCredits = { navController.navigate(Screen.Credits.route) },
                soundManager = soundManager  // Pasar el soundManager
            )
        }

        composable(Screen.Characters.route) {
            CharactersScreen(
                onNavigateBack = {
                    navController.navigate(Screen.Menu.route) {
                        popUpTo(Screen.Menu.route) { inclusive = true }
                    }
                },
                onCharacterSelected = { character ->
                    viewModel.setSelectedCharacter(character)
                    navController.navigate(Screen.Game.route)
                }
            )
        }

        composable(Screen.Game.route) {
            GameScreen(
                viewModel = viewModel,
                onNavigateToCharacterSelect = {
                    navController.navigate(Screen.Characters.route) {
                        popUpTo(Screen.Characters.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Screen.Menu.route) {
                        popUpTo(Screen.Menu.route) { inclusive = true }
                    }
                },
                soundManager = soundManager  // Añadir este parámetro
            )
        }

        composable(Screen.Credits.route) {
            CreditsScreen()
        }

        composable(Screen.Scores.route) {
            // ScoresScreen()
        }

        composable(Screen.Settings.route) {
            // SettingsScreen()
        }
    }
}