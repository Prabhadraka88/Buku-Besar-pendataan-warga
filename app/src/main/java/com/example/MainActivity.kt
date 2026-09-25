package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.Citizen
import com.example.ui.CitizenViewModel
import com.example.ui.components.MiningDialog
import com.example.ui.screens.AddEditCitizenScreen
import com.example.ui.screens.BlockchainExplorerScreen
import com.example.ui.screens.CitizenDetailScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.RtStatsScreen
import com.example.ui.theme.MyApplicationTheme

sealed class Screen {
    data object Dashboard : Screen()
    data object BlockchainExplorer : Screen()
    data object RtStats : Screen()
    data class AddEditCitizen(val citizenId: Long? = null) : Screen()
    data class CitizenDetail(val citizenId: Long) : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val citizenViewModel: CitizenViewModel = viewModel()
                MainAppContent(viewModel = citizenViewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: CitizenViewModel) {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }
    var screenStack by remember { mutableStateOf<List<Screen>>(listOf(Screen.Dashboard)) }

    val allCitizens by viewModel.allCitizens.collectAsStateWithLifecycle()
    val miningState by viewModel.miningState.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    fun navigateTo(screen: Screen) {
        screenStack = screenStack + screen
        currentScreen = screen
    }

    fun navigateBack() {
        if (screenStack.size > 1) {
            val newStack = screenStack.dropLast(1)
            screenStack = newStack
            currentScreen = newStack.last()
        } else {
            currentScreen = Screen.Dashboard
        }
    }

    val isRootScreen = currentScreen is Screen.Dashboard ||
            currentScreen is Screen.BlockchainExplorer ||
            currentScreen is Screen.RtStats

    BackHandler(enabled = !isRootScreen) {
        navigateBack()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (isRootScreen) {
                NavigationBar(
                    modifier = Modifier.testTag("bottom_navigation_bar")
                ) {
                    NavigationBarItem(
                        selected = currentScreen is Screen.Dashboard,
                        onClick = {
                            currentScreen = Screen.Dashboard
                            screenStack = listOf(Screen.Dashboard)
                        },
                        icon = {
                            Icon(
                                if (currentScreen is Screen.Dashboard) Icons.Filled.People else Icons.Outlined.People,
                                contentDescription = "Warga RT"
                            )
                        },
                        label = { Text("Warga RT", fontWeight = FontWeight.SemiBold) },
                        modifier = Modifier.testTag("nav_warga")
                    )

                    NavigationBarItem(
                        selected = currentScreen is Screen.BlockchainExplorer,
                        onClick = {
                            currentScreen = Screen.BlockchainExplorer
                            screenStack = listOf(Screen.BlockchainExplorer)
                        },
                        icon = {
                            Icon(
                                if (currentScreen is Screen.BlockchainExplorer) Icons.Filled.Hub else Icons.Outlined.Hub,
                                contentDescription = "Buku Besar"
                            )
                        },
                        label = { Text("Buku Besar", fontWeight = FontWeight.SemiBold) },
                        modifier = Modifier.testTag("nav_blockchain")
                    )

                    NavigationBarItem(
                        selected = currentScreen is Screen.RtStats,
                        onClick = {
                            currentScreen = Screen.RtStats
                            screenStack = listOf(Screen.RtStats)
                        },
                        icon = {
                            Icon(
                                if (currentScreen is Screen.RtStats) Icons.Filled.BarChart else Icons.Outlined.BarChart,
                                contentDescription = "Rekapitulasi"
                            )
                        },
                        label = { Text("Rekapitulasi", fontWeight = FontWeight.SemiBold) },
                        modifier = Modifier.testTag("nav_stats")
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is Screen.Dashboard -> {
                    DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToAddCitizen = { navigateTo(Screen.AddEditCitizen(null)) },
                        onNavigateToDetail = { id -> navigateTo(Screen.CitizenDetail(id)) },
                        onNavigateToEdit = { id -> navigateTo(Screen.AddEditCitizen(id)) },
                        onNavigateToExplorer = {
                            currentScreen = Screen.BlockchainExplorer
                            screenStack = listOf(Screen.BlockchainExplorer)
                        }
                    )
                }

                is Screen.BlockchainExplorer -> {
                    BlockchainExplorerScreen(
                        viewModel = viewModel
                    )
                }

                is Screen.RtStats -> {
                    RtStatsScreen(
                        viewModel = viewModel
                    )
                }

                is Screen.AddEditCitizen -> {
                    val existingCitizen = screen.citizenId?.let { id ->
                        allCitizens.firstOrNull { it.id == id }
                    }
                    AddEditCitizenScreen(
                        viewModel = viewModel,
                        existingCitizen = existingCitizen,
                        onNavigateBack = { navigateBack() }
                    )
                }

                is Screen.CitizenDetail -> {
                    CitizenDetailScreen(
                        citizenId = screen.citizenId,
                        viewModel = viewModel,
                        onNavigateBack = { navigateBack() },
                        onNavigateToEdit = { id -> navigateTo(Screen.AddEditCitizen(id)) }
                    )
                }
            }

            // Mining / Blockchain consensus progress dialog
            MiningDialog(
                state = miningState,
                onDismiss = { viewModel.clearMiningState() }
            )
        }
    }
}
