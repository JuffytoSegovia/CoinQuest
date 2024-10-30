package com.ucsur.coinquest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ucsur.coinquest.navigation.NavGraph
import com.ucsur.coinquest.ui.theme.CoinQuestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoinQuestTheme {
                NavGraph(
                    onExitApp = {
                        finish()
                    }
                )
            }
        }
    }
}