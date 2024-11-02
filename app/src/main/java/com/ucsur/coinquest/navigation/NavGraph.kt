package com.ucsur.coinquest.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ucsur.coinquest.data.SettingsDataStore
import com.ucsur.coinquest.ui.screens.*
import com.ucsur.coinquest.viewmodel.GameViewModel
import com.ucsur.coinquest.viewmodel.GameViewModelFactory
import com.ucsur.coinquest.utils.SoundManager
import com.ucsur.coinquest.viewmodel.SettingsViewModel
import com.ucsur.coinquest.viewmodel.SettingsViewModelFactory
import androidx.compose.ui.platform.LocalContext
import com.ucsur.coinquest.data.ScoreRepository
import com.ucsur.coinquest.viewmodel.ScoresViewModel
import com.ucsur.coinquest.viewmodel.ScoresViewModelFactory

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Menu : Screen("menu")
    data object Game : Screen("game")
    data object Characters : Screen("characters")
    data object Scores : Screen("scores")
    data object Settings : Screen("settings")
    data object Credits : Screen("credits")
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    soundManager: SoundManager  // Removido onExitApp ya que no se usa
) {
    val context = LocalContext.current
    // Renombrado para evitar shadowing
    val globalScoreRepository = remember { ScoreRepository() }

    // Crear el ViewModel usando el Factory
    val viewModel: GameViewModel = viewModel(
        factory = GameViewModelFactory(soundManager, globalScoreRepository)
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
                soundManager = soundManager
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
                soundManager = soundManager
            )
        }

        composable(Screen.Credits.route) {
            CreditsScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Screen.Scores.route) {
            val scoresViewModel = viewModel<ScoresViewModel>(
                factory = ScoresViewModelFactory(globalScoreRepository)  // Usando el repository global
            )

            ScoresScreen(
                viewModel = scoresViewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Screen.Settings.route) {
            val settingsDataStore = remember { SettingsDataStore(context) }
            val settingsViewModel = viewModel<SettingsViewModel>(
                factory = SettingsViewModelFactory(soundManager, settingsDataStore)
            )

            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}