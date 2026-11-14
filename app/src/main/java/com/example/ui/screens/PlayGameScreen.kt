package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Timer
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
import com.example.domain.checkers.GameOutcome
import com.example.domain.checkers.Piece
import com.example.domain.checkers.PieceColor
import com.example.domain.checkers.TimeControlOption
import com.example.ui.components.CheckerPieceCanvas
import com.example.ui.components.CheckersBoardView
import com.example.ui.components.HintsToggleChip
import com.example.ui.components.MascotSpeechCard
import com.example.ui.theme.CoralAlert
import com.example.ui.theme.CrownGold
import com.example.ui.theme.ForestEmerald
import com.example.ui.theme.SkyBlue
import com.example.ui.viewmodel.GameLaunchConfig
import com.example.ui.viewmodel.PlayGameViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayGameScreen(
  launchConfig: GameLaunchConfig,
  hintsEnabled: Boolean,
  lastGameEloDelta: Int?,
  onToggleHints: (Boolean) -> Unit,
  onNewMatchStarted: () -> Unit,
  onBack: () -> Unit,
  onGameFinished: (GameOutcome, BotDifficulty?, Int, String?) -> Unit,
) {
  val viewModelKey = "${launchConfig.difficulty?.name ?: "TWO_PLAYERS"}_${launchConfig.colorPref.name}_${launchConfig.timeControl.name}_${launchConfig.flyingCheckers}_${launchConfig.endgameScenarioId ?: "std"}"
  val viewModel: PlayGameViewModel = viewModel(
    key = viewModelKey,
    factory = object : ViewModelProvider.Factory {
      @Suppress("UNCHECKED_CAST")
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlayGameViewModel(
          launchConfig = launchConfig,
          onGameFinishedCallback = onGameFinished,
          onNewMatchStarted = onNewMatchStarted,
        ) as T
      }
    },
  )
  val state by viewModel.uiState.collectAsStateWithLifecycle()

  BackHandler(enabled = state.isReviewMode) {
    viewModel.closePostGameAnalysis()
  }

  val whiteCount = state.board.countPieces(PieceColor.WHITE)
  val blackCount = state.board.countPieces(PieceColor.BLACK)

  val whiteLabel = when {
    state.difficulty == null -> "Белые"
    state.humanColor == PieceColor.WHITE -> "Ты (Бел.)"
    else -> state.difficulty!!.title
  }
  val blackLabel = when {
    state.difficulty == null -> "Чёрные"
    state.humanColor == PieceColor.BLACK -> "Ты (Чёрн.)"
    else -> state.difficulty!!.title
  }

  val screenTitle = state.endgameScenario?.title
    ?: state.difficulty?.title
    ?: "Игра вдвоём"

  Scaffold(
    containerColor = MaterialTheme.colorScheme.background,
    topBar = {
      CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
          containerColor = MaterialTheme.colorScheme.background,
        ),
        navigationIcon = {
          IconButton(
            onClick = {
              if (state.isReviewMode) viewModel.closePostGameAnalysis() else onBack()
            },
            modifier = Modifier.testTag("game_back_button"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = if (state.isReviewMode) "Закрыть разбор" else "Выйти из партии",
            )
          }
        },
        title = {
          Text(
            text = screenTitle,
            style = MaterialTheme.typography.titleLarge,
          )
        },
        actions = {
          IconButton(
            onClick = { viewModel.toggleBoardFlip() },
            modifier = Modifier.testTag("game_flip_button"),
          ) {
            Icon(
              imageVector = Icons.Filled.SwapVert,
              contentDescription = "Перевернуть доску",
            )
          }
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
        if (state.isReviewMode) {
          GameReviewSection(
            entries = state.analysisEntries,
            isAnalyzing = state.isAnalyzing,
            isBoardFlipped = state.isBoardFlipped,
            humanColor = state.humanColor,
            onClose = { viewModel.closePostGameAnalysis() },
          )
        } else {
          // Piece counters, active clocks & turn indicator
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            ScorePieceChip(
              color = PieceColor.WHITE,
              count = whiteCount,
              isActive = state.turn == PieceColor.WHITE && !state.isGameOver,
              label = whiteLabel,
              timeText = if (state.timeControl != TimeControlOption.NONE) formatClockMs(state.whiteTimeMs) else null,
            )
            ScorePieceChip(
              color = PieceColor.BLACK,
              count = blackCount,
              isActive = state.turn == PieceColor.BLACK && !state.isGameOver,
              label = blackLabel,
              timeText = if (state.timeControl != TimeControlOption.NONE) formatClockMs(state.blackTimeMs) else null,
            )
          }

          val subtitleBadge = state.flyingOpeningName?.let { "Летающие шашки: $it" }
            ?: state.detectedOpeningName?.let { "Дебют: $it" }
            ?: state.endgameScenario?.subtitle?.let { "Эндшпиль: $it" }

          if (subtitleBadge != null || state.quietKingPlies >= 6) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              if (subtitleBadge != null) {
                Surface(
                  shape = RoundedCornerShape(50),
                  color = SkyBlue.copy(alpha = 0.14f),
                ) {
                  Text(
                    text = subtitleBadge,
                    style = MaterialTheme.typography.labelMedium,
                    color = SkyBlue,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                  )
                }
              }
              if (state.quietKingPlies >= 6) {
                Surface(
                  shape = RoundedCornerShape(50),
                  color = MaterialTheme.colorScheme.tertiaryContainer,
                ) {
                  Text(
                    text = "Правило 15 ходов: ${state.quietKingPlies / 2}/15",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                  )
                }
              }
            }
          }

          CheckersBoardView(
            board = state.board,
            selectedSquare = state.selectedSquare,
            targetSquares = if (hintsEnabled) state.targetSquares else emptySet(),
            mandatoryCaptureSources = if (hintsEnabled) state.mandatorySources else emptySet(),
            lastMove = state.lastMove,
            animatedHint = if (hintsEnabled) state.animatedHint else null,
            showCelebration = state.outcome == GameOutcome.WIN,
            interactive = !state.isGameOver && !state.isBotThinking,
            flipped = state.isBoardFlipped,
            onSquareClick = { viewModel.onSquareClick(it, hintsEnabled) },
          )

          AnimatedVisibility(visible = hintsEnabled && state.hintExplanation != null && !state.isGameOver) {
            MascotSpeechCard(
              title = "Анимированная подсказка",
              message = state.hintExplanation.orEmpty(),
              containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            )
          }

          if (state.isGameOver) {
            val playerWon = state.outcome == GameOutcome.WIN
            val isDraw = state.outcome == GameOutcome.DRAW
            Card(
              shape = RoundedCornerShape(20.dp),
              colors = CardDefaults.cardColors(
                containerColor = when {
                  playerWon || state.difficulty == null -> MaterialTheme.colorScheme.secondaryContainer
                  isDraw -> MaterialTheme.colorScheme.tertiaryContainer
                  else -> MaterialTheme.colorScheme.primaryContainer
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
                    modifier = Modifier.size(34.dp),
                  )
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = state.statusText,
                      style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                      horizontalArrangement = Arrangement.spacedBy(8.dp),
                      verticalAlignment = Alignment.CenterVertically,
                    ) {
                      if (playerWon && state.difficulty != null) {
                        val reward = state.endgameScenario?.starsReward ?: state.difficulty!!.starsReward
                        Text(
                          text = "+$reward ⭐",
                          style = MaterialTheme.typography.labelLarge,
                          color = ForestEmerald,
                          fontWeight = FontWeight.Bold,
                        )
                      } else if (isDraw && state.difficulty != null) {
                        Text(
                          text = "+1 ⭐ за ничью",
                          style = MaterialTheme.typography.labelLarge,
                          color = ForestEmerald,
                          fontWeight = FontWeight.Bold,
                        )
                      }
                      if (lastGameEloDelta != null && state.difficulty != null) {
                        val positive = lastGameEloDelta >= 0
                        val deltaStr = if (positive) "+$lastGameEloDelta Эло" else "$lastGameEloDelta Эло"
                        Surface(
                          shape = RoundedCornerShape(50),
                          color = if (positive) ForestEmerald.copy(alpha = 0.16f) else CoralAlert.copy(alpha = 0.16f),
                        ) {
                          Text(
                            text = deltaStr,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (positive) ForestEmerald else CoralAlert,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                          )
                        }
                      }
                    }
                  }
                }

                if (state.moveHistory.isNotEmpty()) {
                  FilledTonalButton(
                    onClick = { viewModel.openPostGameAnalysis() },
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(48.dp)
                      .testTag("analyze_game_button"),
                  ) {
                    Icon(Icons.Filled.Analytics, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Разбор партии (Работа над ошибками)")
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
                    Text("Выйти")
                  }
                }
              }
            }
          } else {
            MascotSpeechCard(
              title = if (state.isBotThinking) "Ход соперника" else "Ход партии",
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
  timeText: String?,
) {
  Surface(
    shape = RoundedCornerShape(50),
    color = if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
    tonalElevation = if (isActive) 3.dp else 0.dp,
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      CheckerPieceCanvas(
        piece = Piece(color = color, isKing = false),
        modifier = Modifier.size(20.dp),
      )
      Text(
        text = "$label: $count",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = if (isActive) FontWeight.Bold else FontWeight.SemiBold,
      )
      if (timeText != null) {
        Spacer(modifier = Modifier.width(2.dp))
        Icon(
          imageVector = Icons.Filled.Timer,
          contentDescription = null,
          modifier = Modifier.size(15.dp),
          tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
          text = timeText,
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold,
          color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
  }
}

private fun formatClockMs(ms: Long): String {
  val totalSec = ((ms + 999L) / 1000L).coerceAtLeast(0L)
  val min = totalSec / 60L
  val sec = totalSec % 60L
  return String.format(Locale.US, "%d:%02d", min, sec)
}
