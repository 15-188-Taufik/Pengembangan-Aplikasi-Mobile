package com.taufik.notesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.taufik.notesapp.navigation.NotesNavGraph
import com.taufik.notesapp.navigation.Screen
import com.taufik.notesapp.navigation.bottomNavItems
import com.taufik.notesapp.viewmodel.NotesViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController  = rememberNavController()
            val notesViewModel : NotesViewModel = viewModel()

            val navBackStack by navController.currentBackStackEntryAsState()
            val currentRoute  = navBackStack?.destination?.route

            // Bottom nav hanya muncul di 3 tab utama
            val showBottomNav = currentRoute in listOf(
                Screen.NoteList.route,
                Screen.Favorites.route,
                Screen.Profile.route
            )

            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary    = Color(0xFF1A73E8),
                    background = Color(0xFF0D1B2A),
                    surface    = Color(0xFF1E2D3D)
                )
            ) {
                Scaffold(
                    modifier       = Modifier.fillMaxSize(),
                    containerColor = Color(0xFF0D1B2A),
                    bottomBar = {
                        if (showBottomNav) {
                            NavigationBar(containerColor = Color(0xFF1E2D3D)) {
                                bottomNavItems.forEach { item ->
                                    NavigationBarItem(
                                        selected = currentRoute == item.screen.route,
                                        onClick  = {
                                            navController.navigate(item.screen.route) {
                                                popUpTo(Screen.NoteList.route) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState    = true
                                            }
                                        },
                                        icon  = {
                                            Icon(item.icon, contentDescription = item.label)
                                        },
                                        label = { Text(item.label) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor   = Color(0xFF1A73E8),
                                            selectedTextColor   = Color(0xFF1A73E8),
                                            unselectedIconColor = Color.Gray,
                                            unselectedTextColor = Color.Gray,
                                            indicatorColor      = Color(0xFF1A73E8).copy(alpha = 0.15f)
                                        )
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NotesNavGraph(
                        navController = navController,
                        viewModel     = notesViewModel,
                        modifier      = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
