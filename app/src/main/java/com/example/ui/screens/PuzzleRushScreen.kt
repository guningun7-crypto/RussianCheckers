package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.domain.curriculum.LogicPuzzle
import com.example.ui.components.CheckersBoardView
import com.example.ui.components.MascotSpeechCard
import com.example.ui.theme.CoralAlert
import com.example.ui.theme.CrownGold
import com.example.ui.theme.ForestEmerald
import com.example.ui.viewmodel.PuzzleRushViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzleRushScreen(
  allPuzzles: List<LogicPuzzle>,
  personalBest: Int,
  hintsEnabled: Boolean,
  onBack: () -> Unit,
  onRushFinished: (Int) -> Unit,
) {
  val viewModel: PuzzleRushViewModel = viewModel(
    factory = object : ViewModelProvider.Factory {
      @Suppress("UNCHECKED_CAST")
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PuzzleRushViewModel(allPuzzles, onRushFinished) as T
      }
    },
  )
  val state by viewModel.uiState.collectAsStateWithLifecycle()

  val minutes = state.remainingSeconds / 60
  val seconds = state.remainingSeconds % 60
  val timerText = String.format(Locale.US, "%d:%02d", minutes, seconds)

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
            modifier = Modifier.testTag("rush_back_button"),
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Выйти из штурма")
          }
        },
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            Icon(Icons.Filled.Bolt, contentDescription = null, tint = CrownGold)
            Text(
              text = "Штурм задач",
              style = MaterialTheme.typography.titleLarge,
            )
          }
        },
        actions = {
          Surface(
            shape = RoundedCornerShape(50),
            color = if (state.remainingSeconds <= 20) CoralAlert.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.padding(end = 10.dp),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Icon(
                imageVector = Icons.Filled.Timer,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (state.remainingSeconds <= 20) CoralAlert else MaterialTheme.colorScheme.primary,
              )
              Text(
                text = timerText,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
              )
            }
          }
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
          .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        // Score + Lives + PB bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Surface(
            shape = RoundedCornerShape(50),
            color = ForestEmerald.copy(alpha = 0.16f),
          ) {
            Text(
              text = "Решено: ${state.score}",
              style = MaterialTheme.typography.titleMedium,
              color = ForestEmerald,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            )
          }

          Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(3) { idx ->
              Icon(
                imageVector = if (idx < state.livesLeft) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "Жизни: ${state.livesLeft}",
                tint = if (idx < state.livesLeft) CoralAlert else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(26.dp),
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(50),
            color = MaterialTheme.colorScheme.surfaceVariant,
          ) {
            Text(
              text = "Рекорд: ${maxOf(personalBest, state.score)}",
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            )
          }
        }

        MascotSpeechCard(
          title = "${state.currentPuzzle.title} (${state.currentPuzzle.difficulty.label})",
          message = if (state.lastWasMistake) state.statusMessage else state.currentPuzzle.prompt,
          containerColor = if (state.lastWasMistake) {
            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
          } else {
            MaterialTheme.colorScheme.surface
          },
        )

        CheckersBoardView(
          board = state.board,
          selectedSquare = state.selectedSquare,
          targetSquares = if (hintsEnabled) state.targetSquares else emptySet(),
          mandatoryCaptureSources = if (hintsEnabled) state.mandatorySources else emptySet(),
          interactive = !state.isFinished && !state.isBotReplying,
          onSquareClick = { viewModel.onSquareClick(it, hintsEnabled) },
        )

        if (state.isFinished) {
          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("rush_finished_card"),
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
              ) {
                Icon(
                  imageVector = Icons.Filled.EmojiEvents,
                  contentDescription = null,
                  tint = CrownGold,
                  modifier = Modifier.size(34.dp),
                )
                Column {
                  Text(
                    text = state.statusMessage,
                    style = MaterialTheme.typography.titleMedium,
                  )
                  Text(
                    text = "Итоговый счёт: ${state.score} • Рекорд: ${maxOf(personalBest, state.score)}",
                    style = MaterialTheme.typography.labelLarge,
                    color = ForestEmerald,
                  )
                }
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
              ) {
                Button(
                  onClick = { viewModel.restartRush() },
                  colors = ButtonDefaults.buttonColors(containerColor = ForestEmerald),
                  modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("rush_restart_button"),
                ) {
                  Icon(Icons.Filled.Refresh, contentDescription = null)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Ещё штурм")
                }
                OutlinedButton(
                  onClick = onBack,
                  modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                ) {
                  Text("К задачам")
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))
      }
    }
  }
}
