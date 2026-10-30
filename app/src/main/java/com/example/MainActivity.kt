package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ObsidianBottomNavigation
import com.example.ui.screens.AlarmsScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MemoryScreen
import com.example.ui.screens.PhoneControlScreen
import com.example.ui.screens.ScannerScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.screens.VoiceScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianBackground
import com.example.viewmodel.BrainHelperViewModel
import com.example.viewmodel.ScreenTab
import com.example.viewmodel.SubScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val brainViewModel: BrainHelperViewModel = viewModel()
            val isAmoled by brainViewModel.isAmoled.collectAsStateWithLifecycle()

            MyApplicationTheme(isAmoled = isAmoled) {
                BrainHelperMainContent(viewModel = brainViewModel)
            }
        }
    }
}

@Composable
fun BrainHelperMainContent(
    viewModel: BrainHelperViewModel
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val currentSubScreen by viewModel.currentSubScreen.collectAsStateWithLifecycle()

    BackHandler(enabled = currentSubScreen != SubScreen.NONE || currentTab != ScreenTab.HOME) {
        viewModel.navigateBack()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground),
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            // Only show bottom navigation bar when on primary top-level tabs
            if (currentSubScreen == SubScreen.NONE) {
                ObsidianBottomNavigation(
                    currentTab = currentTab,
                    onTabSelected = { tab ->
                        viewModel.navigateToTab(tab)
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentSubScreen) {
                SubScreen.VOICE -> {
                    VoiceScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                }
                SubScreen.ALARMS -> {
                    AlarmsScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                }
                SubScreen.SCANNER -> {
                    ScannerScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                }
                SubScreen.PHONE_CONTROL -> {
                    PhoneControlScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                }
                SubScreen.NONE -> {
                    when (currentTab) {
                        ScreenTab.HOME -> {
                            HomeScreen(viewModel = viewModel)
                        }
                        ScreenTab.TASKS -> {
                            TasksScreen(viewModel = viewModel)
                        }
                        ScreenTab.CHAT -> {
                            ChatScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.navigateToTab(ScreenTab.HOME) }
                            )
                        }
                        ScreenTab.MEMORY -> {
                            MemoryScreen(viewModel = viewModel)
                        }
                        ScreenTab.SETTINGS -> {
                            SettingsScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
