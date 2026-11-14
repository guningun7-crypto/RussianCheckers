package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.domain.curriculum.LogicPuzzle
import com.example.ui.components.CheckersBoardView
import com.example.ui.components.HintsToggleChip
import com.example.ui.components.MascotSpeechCard
import com.example.ui.components.StarCounterPill
import com.example.ui.theme.CrownGold
import com.example.ui.theme.ForestEmerald
import com.example.ui.viewmodel.PuzzleDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzleDetailScreen(
  puzzle: LogicPuzzle,
  allPuzzles: List<LogicPuzzle>,
  hintsEnabled: Boolean,
  onToggleHints: (Boolean) -> Unit,
  onBack: () -> Unit,
  onNavigateToNextPuzzle: (String) -> Unit,
  onPuzzleSolved: (String, Int, Boolean) -> Unit,
) {
  val viewModel: PuzzleDetailViewModel = viewModel(
    key = puzzle.id,
    factory = object : ViewModelProvider.Factory {
      @Suppress("UNCHECKED_CAST")
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PuzzleDetailViewModel(puzzle, onPuzzleSolved) as T
      }
    },
  )
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  val currentIdx = allPuzzles.indexOfFirst { it.id == puzzle.id }
  val nextPuzzle = if (currentIdx >= 0 && currentIdx + 1 < allPuzzles.size) {
    allPuzzles[currentIdx + 1]
  } else {
    null
  }

  Scaffold(
    containerColor = MaterialTheme.colorScheme.background,
    topBar = {
      CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
          containerColor = MaterialTheme.colorScheme.background,
        ),
        navigationIcon = {
          IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("puzzle_back_button"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Назад к задачам",
            )
          }
        },
        title = {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = puzzle.title,
              style = MaterialTheme.typography.titleMedium,
            )
            Text(
              text = "${puzzle.difficulty.label} • ${puzzle.themeTag}",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        },
        actions = {
          StarCounterPill(
            stars = puzzle.difficulty.starsReward,
            modifier = Modifier.padding(end = 10.dp),
          )
        },
      )
    },
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentAlignment = Alignment.TopCenter,
    ) {
      Column(
        modifier = Modifier
          .widthIn(max = 620.dp)
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = if (puzzle.steps.size > 1 && !state.isSolved) {
              "Ход ${state.currentStepIndex + 1} из ${puzzle.steps.size}"
            } else {
              "Задача №${puzzle.order}"
            },
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
          )
          HintsToggleChip(
            hintsEnabled = hintsEnabled,
            onToggle = onToggleHints,
          )
        }

        MascotSpeechCard(
          title = if (state.isSolved) "Задача решена! +${puzzle.difficulty.starsReward} ⭐" else "Задание",
          message = state.statusBanner,
          containerColor = when {
            state.isSolved -> MaterialTheme.colorScheme.secondaryContainer
            state.isErrorBanner -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.65f)
            else -> MaterialTheme.colorScheme.surface
          },
        )

        CheckersBoardView(
          board = state.board,
          selectedSquare = state.selectedSquare,
          targetSquares = if (hintsEnabled) state.targetSquares else emptySet(),
          mandatoryCaptureSources = if (hintsEnabled) state.mandatorySources else emptySet(),
          animatedHint = if (hintsEnabled) state.animatedHint else null,
          showCelebration = state.isSolved,
          interactive = !state.isSolved && !state.isBotReplying,
          onSquareClick = { viewModel.onSquareClick(it, hintsEnabled) },
        )

        AnimatedVisibility(visible = hintsEnabled && state.hintMessage != null && !state.isSolved) {
          MascotSpeechCard(
            title = "Анимированная подсказка",
            message = state.hintMessage.orEmpty(),
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
          )
        }

        if (state.isSolved) {
          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("puzzle_solved_card"),
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                Icon(
                  imageVector = Icons.Filled.CheckCircle,
                  contentDescription = null,
                  tint = ForestEmerald,
                  modifier = Modifier.size(28.dp),
                )
                Text(
                  text = "Отлично решено!",
                  style = MaterialTheme.typography.titleMedium,
                  color = ForestEmerald,
                )
              }
              if (nextPuzzle != null) {
                Button(
                  onClick = { onNavigateToNextPuzzle(nextPuzzle.id) },
                  colors = ButtonDefaults.buttonColors(containerColor = ForestEmerald),
                  modifier = Modifier
                    .height(48.dp)
                    .testTag("next_puzzle_button"),
                ) {
                  Text("Дальше")
                  Spacer(modifier = Modifier.width(6.dp))
                  Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
              } else {
                Button(
                  onClick = onBack,
                  colors = ButtonDefaults.buttonColors(containerColor = ForestEmerald),
                  modifier = Modifier.height(48.dp),
                ) {
                  Text("К задачам")
                }
              }
            }
          }
        } else {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            if (hintsEnabled) {
              FilledTonalButton(
                onClick = { viewModel.requestHint() },
                modifier = Modifier
                  .weight(1f)
                  .height(50.dp)
                  .testTag("puzzle_hint_button"),
              ) {
                Icon(imageVector = Icons.Filled.Lightbulb, contentDescription = null, tint = CrownGold)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Подсказка")
              }
            }
            OutlinedButton(
              onClick = { viewModel.resetPuzzle() },
              modifier = Modifier
                .weight(1f)
                .height(50.dp)
                .testTag("puzzle_reset_button"),
            ) {
              Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
              Spacer(modifier = Modifier.width(6.dp))
              Text("Сначала")
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}
