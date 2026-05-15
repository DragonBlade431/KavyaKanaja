package com.kavyakanaja.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kavyakanaja.app.ui.screens.FavoritesScreen
import com.kavyakanaja.app.ui.screens.HomeScreen
import com.kavyakanaja.app.ui.screens.LibraryScreen
import com.kavyakanaja.app.ui.screens.PoemDetailScreen
import com.kavyakanaja.app.ui.screens.PoetCornerScreen
import com.kavyakanaja.app.ui.screens.PoetDetailScreen
import com.kavyakanaja.app.ui.theme.KavyaKanajaTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    val splashScreen = installSplashScreen()
    super.onCreate(savedInstanceState)
    splashScreen.setKeepOnScreenCondition { false }
    setContent {
      KavyaKanajaTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          val navController = rememberNavController()
          KavyaKanajaNavHost(navController = navController)
        }
      }
    }
  }
}

private data class BottomTab(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun KavyaKanajaNavHost(navController: NavHostController) {
  val tabs = listOf(
    BottomTab("home", "ಮುಖ್ಯ / Home", Icons.Default.Home),
    BottomTab("library", "ಸಂಗ್ರಹ / Library", Icons.Default.MenuBook),
    BottomTab("poets", "ಕವಿಗಳು / Poets", Icons.Default.RecordVoiceOver),
    BottomTab("favourites", "ಇಷ್ಟಗಳು / Favourites", Icons.Default.Favorite)
  )
  val backStack by navController.currentBackStackEntryAsState()
  val route = backStack?.destination?.route
  val showBottom = tabs.any { it.route == route }

  Scaffold(
    bottomBar = {
      if (showBottom) {
        NavigationBar {
          tabs.forEach { tab ->
            NavigationBarItem(
              selected = route == tab.route,
              onClick = {
                navController.navigate(tab.route) {
                  popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                  launchSingleTop = true
                  restoreState = true
                }
              },
              icon = { Icon(tab.icon, contentDescription = tab.label) },
              label = { Text(tab.label) }
            )
          }
        }
      }
    }
  ) { padding ->
    NavHost(navController = navController, startDestination = "home") {
      composable("home") { HomeScreen(navController, padding) }
      composable("library") { LibraryScreen(navController, padding) }
      composable("poets") { PoetCornerScreen(navController, padding) }
      composable("favourites") { FavoritesScreen(navController, padding) }
      composable("poem/{poemId}", arguments = listOf(navArgument("poemId") { type = NavType.IntType })) {
        PoemDetailScreen(navController, it.arguments?.getInt("poemId") ?: 0)
      }
      composable("poet/{poetId}", arguments = listOf(navArgument("poetId") { type = NavType.IntType })) {
        PoetDetailScreen(navController, it.arguments?.getInt("poetId") ?: 0)
      }
    }
  }
}
