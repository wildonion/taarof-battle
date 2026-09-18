package com.wildonion.taarofbattle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.wildonion.taarofbattle.game.GameViewModel
import com.wildonion.taarofbattle.game.Screen
import com.wildonion.taarofbattle.ui.screens.BattleScreen
import com.wildonion.taarofbattle.ui.screens.MenuScreen
import com.wildonion.taarofbattle.ui.screens.ResultScreen
import com.wildonion.taarofbattle.ui.theme.TaarofTheme

class MainActivity : ComponentActivity() {
    private val vm: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaarofTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val ui by vm.ui.collectAsState()
                    when (ui.screen) {
                        Screen.Menu -> MenuScreen(vm)
                        Screen.Battle -> BattleScreen(vm)
                        Screen.Result -> ResultScreen(vm)
                    }
                }
            }
        }
    }
}
