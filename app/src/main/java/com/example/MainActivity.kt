package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.SystemUpdate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.local.AppDatabase
import com.example.data.local.ProgressRepository
import com.example.domain.checkers.BotDifficulty
import com.example.ui.components.BadgeCelebrationDialog
import com.example.ui.screens.LessonDetailScreen
import com.example.ui.screens.LessonsScreen
import com.example.ui.screens.PlayGameScreen
import com.example.ui.screens.PlayHubScreen
import com.example.ui.screens.PuzzleDetailScreen
import com.example.ui.screens.PuzzlesScreen
import com.example.ui.screens.RewardsScreen
import com.example.ui.screens.UpdatesScreen
import com.example.ui.theme.ShashkiTheme
import com.example.ui.viewmodel.AppProgressViewModel
import com.example.ui.viewmodel.UpdatesViewModel

const val ROUTE_LESSONS = "lessons"
const val ROUTE_PUZZLES = "puzzles"
const val ROUTE_PLAY_HUB = "play_hub"
const val ROUTE_REWARDS = "rewards"
const val ROUTE_UPDATES = "updates"
const val ROUTE_LESSON_DETAIL = "lesson/{lessonId}"
const val ROUTE_PUZZLE_DETAIL = "puzzle/{puzzleId}"
const val ROUTE_PLAY_GAME = "game/{mode}"

private data class TopTab(
  val route: String,
  val labelRes: Int,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val testTag: String,
)

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      ShashkiTheme {
        ShashkiApp()
      }
    }
  }
}

@Composable
fun ShashkiApp() {
  val context = LocalContext.current
  val repository = remember(context) {
    ProgressRepository(AppDatabase.getInstance(context).progressDao())
  }
  val progressViewModel: AppProgressViewModel = viewModel(
    factory = AppProgressViewModel.Factory(repository),
  )
  val progressState by progressViewModel.uiState.collectAsStateWithLifecycle()
  val pendingBadges by progressViewModel.pendingBadges.collectAsStateWithLifecycle()

  val navController = rememberNavController()
  val backStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = backStackEntry?.destination?.route

  val tabs = remember {
    listOf(
      TopTab(ROUTE_LESSONS, R.string.nav_lessons, Icons.Filled.School, Icons.Outlined.School, "nav_lessons"),
      TopTab(ROUTE_PUZZLES, R.string.nav_puzzles, Icons.Filled.Extension, Icons.Outlined.Extension, "nav_puzzles"),
      TopTab(ROUTE_PLAY_HUB, R.string.nav_play, Icons.Filled.SmartToy, Icons.Outlined.SmartToy, "nav_play"),
      TopTab(ROUTE_REWARDS, R.string.nav_rewards, Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents, "nav_rewards"),
      TopTab(ROUTE_UPDATES, R.string.nav_updates, Icons.Filled.SystemUpdate, Icons.Outlined.SystemUpdate, "nav_updates"),
    )
  }

  val showBottomNav = currentRoute in setOf(
    ROUTE_LESSONS,
    ROUTE_PUZZLES,
    ROUTE_PLAY_HUB,
    ROUTE_REWARDS,
    ROUTE_UPDATES,
  )

  fun navigateTab(route: String) {
    navController.navigate(route) {
      popUpTo(navController.graph.findStartDestination().id) {
        saveState = true
      }
      launchSingleTop = true
      restoreState = true
    }
  }

  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val useRail = maxWidth >= 600.dp

    Scaffold(
      containerColor = MaterialTheme.colorScheme.background,
      contentWindowInsets = WindowInsets.safeDrawing,
      bottomBar = {
        if (showBottomNav && !useRail) {
          NavigationBar {
            tabs.forEach { tab ->
              val selected = currentRoute == tab.route
              val label = stringResource(id = tab.labelRes)
              NavigationBarItem(
                selected = selected,
                onClick = { navigateTab(tab.route) },
                icon = {
                  Icon(
                    imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                    contentDescription = label,
                  )
                },
                label = { Text(label) },
                modifier = Modifier.testTag(tab.testTag),
              )
            }
          }
        }
      },
    ) { innerPadding ->
      Row(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding),
      ) {
        if (showBottomNav && useRail) {
          NavigationRail(modifier = Modifier.fillMaxHeight()) {
            tabs.forEach { tab ->
              val selected = currentRoute == tab.route
              val label = stringResource(id = tab.labelRes)
              NavigationRailItem(
                selected = selected,
                onClick = { navigateTab(tab.route) },
                icon = {
                  Icon(
                    imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                    contentDescription = label,
                  )
                },
                label = { Text(label) },
                modifier = Modifier.testTag(tab.testTag),
              )
            }
          }
        }

        NavHost(
          navController = navController,
          startDestination = ROUTE_LESSONS,
          modifier = Modifier.weight(1f).fillMaxHeight(),
        ) {
          composable(ROUTE_LESSONS) {
            LessonsScreen(
              progressState = progressState,
              hintsEnabled = progressState.hintsEnabled,
              onToggleHints = { progressViewModel.setHintsEnabled(it) },
              onLessonClick = { lessonId ->
                navController.navigate("lesson/$lessonId")
              },
            )
          }

          composable(ROUTE_PUZZLES) {
            PuzzlesScreen(
              progressState = progressState,
              hintsEnabled = progressState.hintsEnabled,
              onToggleHints = { progressViewModel.setHintsEnabled(it) },
              onPuzzleClick = { puzzleId ->
                navController.navigate("puzzle/$puzzleId")
              },
            )
          }

          composable(ROUTE_PLAY_HUB) {
            PlayHubScreen(
              progressState = progressState,
              hintsEnabled = progressState.hintsEnabled,
              onToggleHints = { progressViewModel.setHintsEnabled(it) },
              onStartBotMatch = { diff ->
                navController.navigate("game/${diff.name}")
              },
              onStartTwoPlayerMatch = {
                navController.navigate("game/TWO_PLAYERS")
              },
            )
          }

          composable(ROUTE_REWARDS) {
            RewardsScreen(progressState = progressState)
          }

          composable(ROUTE_UPDATES) {
            val updatesViewModel: UpdatesViewModel = viewModel(
              factory = UpdatesViewModel.Factory(repository),
            )
            UpdatesScreen(viewModel = updatesViewModel)
          }

          composable(
            route = ROUTE_LESSON_DETAIL,
            arguments = listOf(navArgument("lessonId") { type = NavType.StringType }),
          ) { entry ->
            val lessonId = entry.arguments?.getString("lessonId") ?: return@composable
            val lesson = progressState.allLessons.firstOrNull { it.id == lessonId } ?: return@composable
            LessonDetailScreen(
              lesson = lesson,
              allLessons = progressState.allLessons,
              hintsEnabled = progressState.hintsEnabled,
              onToggleHints = { progressViewModel.setHintsEnabled(it) },
              onBack = { navController.popBackStack() },
              onNavigateToNextLesson = { nextId ->
                navController.navigate("lesson/$nextId") {
                  popUpTo("lesson/$lessonId") { inclusive = true }
                }
              },
              onLessonCompleted = { id, stars ->
                progressViewModel.onLessonCompleted(id, stars)
              },
            )
          }

          composable(
            route = ROUTE_PUZZLE_DETAIL,
            arguments = listOf(navArgument("puzzleId") { type = NavType.StringType }),
          ) { entry ->
            val puzzleId = entry.arguments?.getString("puzzleId") ?: return@composable
            val puzzle = progressState.allPuzzles.firstOrNull { it.id == puzzleId } ?: return@composable
            PuzzleDetailScreen(
              puzzle = puzzle,
              allPuzzles = progressState.allPuzzles,
              hintsEnabled = progressState.hintsEnabled,
              onToggleHints = { progressViewModel.setHintsEnabled(it) },
              onBack = { navController.popBackStack() },
              onNavigateToNextPuzzle = { nextId ->
                navController.navigate("puzzle/$nextId") {
                  popUpTo("puzzle/$puzzleId") { inclusive = true }
                }
              },
              onPuzzleSolved = { id, stars, usedHint ->
                progressViewModel.onPuzzleSolved(id, stars, usedHint)
              },
            )
          }

          composable(
            route = ROUTE_PLAY_GAME,
            arguments = listOf(navArgument("mode") { type = NavType.StringType }),
          ) { entry ->
            val mode = entry.arguments?.getString("mode") ?: "BEGINNER"
            val difficulty = BotDifficulty.entries.firstOrNull { it.name == mode }
            PlayGameScreen(
              difficulty = difficulty,
              hintsEnabled = progressState.hintsEnabled,
              onToggleHints = { progressViewModel.setHintsEnabled(it) },
              onBack = { navController.popBackStack() },
              onGameFinished = { won, diff, stars ->
                progressViewModel.onGameFinished(won, diff, stars)
              },
            )
          }
        }
      }
    }

    pendingBadges.firstOrNull()?.let { badge ->
      BadgeCelebrationDialog(
        badge = badge,
        onDismiss = { progressViewModel.dismissCurrentBadge() },
      )
    }
  }
}
