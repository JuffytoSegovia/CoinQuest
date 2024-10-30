package com.ucsur.coinquest.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ucsur.coinquest.ui.screens.SplashScreen
import com.ucsur.coinquest.ui.screens.MenuScreen
import com.ucsur.coinquest.ui.screens.CreditsScreen
import com.ucsur.coinquest.ui.screens.CharactersScreen

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
    navController: NavHostController = rememberNavController()
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
                onNavigateToGame = { navController.navigate(Screen.Game.route) },
                onNavigateToCharacters = { navController.navigate(Screen.Characters.route) },
                onNavigateToScores = { navController.navigate(Screen.Scores.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToCredits = { navController.navigate(Screen.Credits.route) }
            )
        }

        composable(Screen.Credits.route) {
            CreditsScreen()
        }

        // Las siguientes pantallas serán implementadas después
        composable(Screen.Game.route) {
            // GameScreen()
        }

        composable(Screen.Characters.route) {
            CharactersScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Screen.Scores.route) {
            // ScoresScreen()
        }

        composable(Screen.Settings.route) {
            // SettingsScreen()
        }
    }
}