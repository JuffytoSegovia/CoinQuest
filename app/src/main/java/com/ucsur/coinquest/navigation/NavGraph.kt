package com.ucsur.coinquest.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ucsur.coinquest.ui.screens.*
import com.ucsur.coinquest.viewmodel.GameViewModel

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
    viewModel: GameViewModel = viewModel(),
    onExitApp: () -> Unit = {}
) {
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
                    // Al dar click en Jugar, vamos directamente a la selección de personaje
                    navController.navigate(Screen.Characters.route)
                },
                onNavigateToScores = { navController.navigate(Screen.Scores.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToCredits = { navController.navigate(Screen.Credits.route) }
            )
        }

        composable(Screen.Game.route) {
            GameScreen(
                viewModel = viewModel,
                onNavigateToCharacterSelect = {
                    navController.navigate(Screen.Characters.route)
                },
                onNavigateToMenu = {
                    navController.navigate(Screen.Menu.route) {
                        // Limpiamos el back stack hasta el menú
                        popUpTo(Screen.Menu.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Screen.Characters.route) {
            CharactersScreen(
                onNavigateBack = {
                    // Al dar atrás en selección de personaje, volvemos al menú
                    navController.navigate(Screen.Menu.route) {
                        popUpTo(Screen.Menu.route) {
                            inclusive = true
                        }
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
                    // Al dar atrás en la pantalla de bienvenida, volvemos a la selección de personaje
                    navController.navigate(Screen.Characters.route) {
                        popUpTo(Screen.Characters.route) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Screen.Menu.route) {
                        popUpTo(Screen.Menu.route) {
                            inclusive = true
                        }
                    }
                }
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