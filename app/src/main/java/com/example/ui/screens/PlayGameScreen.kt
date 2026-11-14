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
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.EmojiEvents
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
import com.example.domain.checkers.BotDifficulty
import com.example.domain.checkers.Piece
import com.example.domain.checkers.PieceColor
import com.example.ui.components.CheckerPieceCanvas
import com.example.ui.components.CheckersBoardView
import com.example.ui.components.HintsToggleChip
import com.example.ui.components.MascotSpeechCard
import com.example.ui.theme.CrownGold
import com.example.ui.theme.ForestEmerald
import com.example.ui.viewmodel.PlayGameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayGameScreen(
  difficulty: BotDifficulty?,
  hintsEnabled: Boolean,
  onToggleHints: (Boolean) -> Unit,
  onBack: () -> Unit,
  onGameFinished: (Boolean, BotDifficulty?, Int) -> Unit,
) {
  val viewModelKey = difficulty?.name ?: "TWO_PLAYERS"
  val viewModel: PlayGameViewModel = viewModel(
    key = viewModelKey,
    factory = object : ViewModelProvider.Factory {
      @Suppress("UNCHECKED_CAST")
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlayGameViewModel(difficulty, onGameFinished) as T
      }
    },
  )
  val state by viewModel.uiState.collectAsStateWithLifecycle()

  val whiteCount = state.board.countPieces(PieceColor.WHITE)
  val blackCount = state.board.countPieces(PieceColor.BLACK)

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
            modifier = Modifier.testTag("game_back_button"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Выйти из партии",
            )
          }
        },
        title = {
          Text(
            text = difficulty?.title ?: "Игра вдвоём",
            style = MaterialTheme.typography.titleLarge,
          )
        },
        actions = {
          HintsToggleChip(
            hintsEnabled = hintsEnabled,
            onToggle = onToggleHints,
            modifier = Modifier.padding(end = 8.dp),
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
          .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        // Piece counters & turn indicator
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          ScorePieceChip(
            color = PieceColor.WHITE,
            count = whiteCount,
            isActive = state.turn == PieceColor.WHITE && state.winner == null,
            label = "Белые",
          )
          ScorePieceChip(
            color = PieceColor.BLACK,
            count = blackCount,
            isActive = state.turn == PieceColor.BLACK && state.winner == null,
            label = difficulty?.title ?: "Чёрные",
          )
        }

        CheckersBoardView(
          board = state.board,
          selectedSquare = state.selectedSquare,
          targetSquares = if (hintsEnabled) state.targetSquares else emptySet(),
          mandatoryCaptureSources = if (hintsEnabled) state.mandatorySources else emptySet(),
          lastMove = state.lastMove,
          animatedHint = if (hintsEnabled) state.animatedHint else null,
          showCelebration = state.winner == PieceColor.WHITE || (difficulty == null && state.winner != null),
          interactive = state.winner == null && !state.isBotThinking,
          onSquareClick = { viewModel.onSquareClick(it, hintsEnabled) },
        )

        AnimatedVisibility(visible = hintsEnabled && state.hintExplanation != null && state.winner == null) {
          MascotSpeechCard(
            title = "Анимированная подсказка",
            message = state.hintExplanation.orEmpty(),
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
          )
        }

        if (state.winner != null) {
          val playerWon = state.winner == PieceColor.WHITE
          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (playerWon || difficulty == null) {
                MaterialTheme.colorScheme.secondaryContainer
              } else {
                MaterialTheme.colorScheme.primaryContainer
              },
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("game_over_card"),
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
                  modifier = Modifier.size(32.dp),
                )
                Column {
                  Text(
                    text = state.statusText,
                    style = MaterialTheme.typography.titleMedium,
                  )
                  if (playerWon && difficulty != null) {
                    Text(
                      text = "Заслуженная награда: +${difficulty.starsReward} ⭐",
                      style = MaterialTheme.typography.labelLarge,
                      color = ForestEmerald,
                    )
                  }
                }
              }
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
              ) {
                Button(
                  onClick = { viewModel.restartGame() },
                  modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("play_again_button"),
                  colors = ButtonDefaults.buttonColors(containerColor = ForestEmerald),
                ) {
                  Icon(Icons.Filled.Refresh, contentDescription = null)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Сыграть ещё")
                }
                OutlinedButton(
                  onClick = onBack,
                  modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                ) {
                  Text("К уровням")
                }
              }
            }
          }
        } else {
          MascotSpeechCard(
            title = if (state.isBotThinking) "Ход соперника" else "Подсказка Совёнка",
            message = state.statusText,
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            if (hintsEnabled) {
              FilledTonalButton(
                onClick = { viewModel.requestHint() },
                enabled = !state.isBotThinking,
                modifier = Modifier
                  .weight(1f)
                  .height(50.dp)
                  .testTag("game_hint_button"),
              ) {
                Icon(Icons.Filled.Lightbulb, contentDescription = null, tint = CrownGold)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Подсказка")
              }
            }

            OutlinedButton(
              onClick = { viewModel.undoMove() },
              enabled = state.canUndo && !state.isBotThinking,
              modifier = Modifier
                .let { if (hintsEnabled) it else it.weight(1f) }
                .height(50.dp)
                .testTag("game_undo_button"),
            ) {
              Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Отменить ход")
              Spacer(modifier = Modifier.width(4.dp))
              Text("Назад")
            }

            OutlinedButton(
              onClick = { viewModel.restartGame() },
              modifier = Modifier
                .height(50.dp)
                .testTag("game_restart_button"),
            ) {
              Icon(Icons.Filled.Refresh, contentDescription = "Начать заново")
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))
      }
    }
  }
}

@Composable
private fun ScorePieceChip(
  color: PieceColor,
  count: Int,
  isActive: Boolean,
  label: String,
) {
  Surface(
    shape = RoundedCornerShape(50),
    color = if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
    tonalElevation = if (isActive) 3.dp else 0.dp,
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      CheckerPieceCanvas(
        piece = Piece(color = color, isKing = false),
        modifier = Modifier.size(22.dp),
      )
      Text(
        text = "$label: $count",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = if (isActive) FontWeight.Bold else FontWeight.SemiBold,
      )
    }
  }
}
