package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import com.example.domain.curriculum.InteractiveLesson
import com.example.ui.components.CheckersBoardView
import com.example.ui.components.HintsToggleChip
import com.example.ui.components.MascotSpeechCard
import com.example.ui.components.StarCounterPill
import com.example.ui.theme.CrownGold
import com.example.ui.theme.ForestEmerald
import com.example.ui.viewmodel.ExerciseFeedback
import com.example.ui.viewmodel.LessonDetailViewModel
import com.example.ui.viewmodel.LessonTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonDetailScreen(
  lesson: InteractiveLesson,
  allLessons: List<InteractiveLesson>,
  hintsEnabled: Boolean,
  onToggleHints: (Boolean) -> Unit,
  onBack: () -> Unit,
  onNavigateToNextLesson: (String) -> Unit,
  onLessonCompleted: (String, Int) -> Unit,
) {
  val viewModel: LessonDetailViewModel = viewModel(
    key = lesson.id,
    factory = object : ViewModelProvider.Factory {
      @Suppress("UNCHECKED_CAST")
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LessonDetailViewModel(lesson, onLessonCompleted) as T
      }
    },
  )
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  val currentIdx = allLessons.indexOfFirst { it.id == lesson.id }
  val nextLesson = if (currentIdx >= 0 && currentIdx + 1 < allLessons.size) {
    allLessons[currentIdx + 1]
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
            modifier = Modifier.testTag("lesson_back_button"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Назад к урокам",
            )
          }
        },
        title = {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "Урок ${lesson.order}: ${lesson.title}",
              style = MaterialTheme.typography.titleMedium,
            )
            Text(
              text = lesson.subtitle,
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        },
        actions = {
          StarCounterPill(
            stars = lesson.starsReward,
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
          .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        // Step switcher: 1. Правило и показ | 2. Упражнение
        SingleChoiceSegmentedButtonRow(
          modifier = Modifier.fillMaxWidth(),
        ) {
          SegmentedButton(
            selected = state.activeTab == LessonTab.RULE_AND_DEMO,
            onClick = { viewModel.selectTab(LessonTab.RULE_AND_DEMO) },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
            modifier = Modifier.testTag("lesson_tab_demo"),
          ) {
            Text("1. Правило и показ")
          }
          SegmentedButton(
            selected = state.activeTab == LessonTab.EXERCISE,
            onClick = { viewModel.selectTab(LessonTab.EXERCISE) },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
            modifier = Modifier.testTag("lesson_tab_exercise"),
          ) {
            Text("2. Попробуй сам!")
          }
        }

        if (state.activeTab == LessonTab.RULE_AND_DEMO) {
          // 1. Short rule explanation
          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = "Правило урока",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = lesson.shortRule,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
              )
            }
          }

          // 2. Visual animated demonstration on the board
          CheckersBoardView(
            board = state.demoBoard,
            highlightedSquares = state.demoHighlights,
            lastMove = state.demoLastMove,
            animatedHint = state.demoHintArrow,
            interactive = false,
          )

          MascotSpeechCard(
            title = "Показ на доске",
            message = state.demoCaption,
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
          )

          // Demo playback controls
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            FilledTonalButton(
              onClick = { viewModel.toggleDemoPlay() },
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("demo_play_pause_button"),
            ) {
              Icon(
                imageVector = if (state.isDemoPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = null,
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(if (state.isDemoPlaying) "Пауза" else "Показать")
            }

            OutlinedButton(
              onClick = { viewModel.stepDemoForward() },
              modifier = Modifier
                .height(48.dp)
                .testTag("demo_step_button"),
              contentPadding = PaddingValues(horizontal = 14.dp),
            ) {
              Icon(imageVector = Icons.Filled.SkipNext, contentDescription = "Следующий шаг")
              Spacer(modifier = Modifier.width(4.dp))
              Text("Шаг")
            }

            OutlinedButton(
              onClick = { viewModel.replayDemo() },
              modifier = Modifier
                .height(48.dp)
                .testTag("demo_replay_button"),
              contentPadding = PaddingValues(horizontal = 14.dp),
            ) {
              Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Повторить")
            }
          }

          MascotSpeechCard(
            title = "Совет от Совёнка",
            message = lesson.mascotTip,
          )

          Button(
            onClick = { viewModel.selectTab(LessonTab.EXERCISE) },
            modifier = Modifier
              .fillMaxWidth()
              .height(54.dp)
              .testTag("go_to_exercise_button"),
            shape = RoundedCornerShape(16.dp),
          ) {
            Text("Перейти к задаче!", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
          }
        } else {
          // 3. Interactive Exercise
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              text = "Практика по уроку ${lesson.order}",
              style = MaterialTheme.typography.labelLarge,
              color = ForestEmerald,
            )
            HintsToggleChip(
              hintsEnabled = hintsEnabled,
              onToggle = onToggleHints,
            )
          }

          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)),
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = "Твоё задание",
                style = MaterialTheme.typography.labelLarge,
                color = ForestEmerald,
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = lesson.exercisePrompt,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
              )
            }
          }

          CheckersBoardView(
            board = state.exerciseBoard,
            selectedSquare = state.selectedSquare,
            targetSquares = if (hintsEnabled) state.targetSquares else emptySet(),
            mandatoryCaptureSources = if (hintsEnabled) state.mandatorySources else emptySet(),
            animatedHint = if (hintsEnabled) state.animatedHint else null,
            showCelebration = state.isSolved,
            interactive = !state.isSolved,
            onSquareClick = { viewModel.onExerciseSquareClick(it, hintsEnabled) },
          )

          // Animated hint speech bubble
          AnimatedVisibility(visible = hintsEnabled && state.hintMessage != null && !state.isSolved) {
            MascotSpeechCard(
              title = "Анимированная подсказка",
              message = state.hintMessage.orEmpty(),
              containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            )
          }

          // 4. Immediate Feedback on Exercise Results
          AnimatedVisibility(visible = state.feedback != null) {
            when (val fb = state.feedback) {
              is ExerciseFeedback.Success -> {
                Card(
                  shape = RoundedCornerShape(20.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("lesson_success_feedback"),
                ) {
                  Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
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
                        text = "Отлично! +${fb.starsEarned} ⭐",
                        style = MaterialTheme.typography.titleLarge,
                        color = ForestEmerald,
                      )
                      Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = CrownGold,
                      )
                    }
                    Text(
                      text = fb.message,
                      style = MaterialTheme.typography.bodyLarge,
                    )
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                      if (nextLesson != null) {
                        Button(
                          onClick = { onNavigateToNextLesson(nextLesson.id) },
                          modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("next_lesson_button"),
                          colors = ButtonDefaults.buttonColors(containerColor = ForestEmerald),
                        ) {
                          Text("Урок ${nextLesson.order}")
                          Spacer(modifier = Modifier.width(6.dp))
                          Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                        }
                      } else {
                        Button(
                          onClick = onBack,
                          modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("finish_lessons_button"),
                          colors = ButtonDefaults.buttonColors(containerColor = ForestEmerald),
                        ) {
                          Text("К списку уроков")
                        }
                      }
                    }
                  }
                }
              }

              is ExerciseFeedback.TryAgain -> {
                MascotSpeechCard(
                  title = "Попробуй ещё раз!",
                  message = fb.message,
                  containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.65f),
                )
              }

              null -> Unit
            }
          }

          if (!state.isSolved) {
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
                    .testTag("lesson_hint_button"),
                ) {
                  Icon(imageVector = Icons.Filled.Lightbulb, contentDescription = null, tint = CrownGold)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Подсказка")
                }
              }
              OutlinedButton(
                onClick = { viewModel.resetExercise() },
                modifier = Modifier
                  .weight(1f)
                  .height(50.dp)
                  .testTag("lesson_reset_button"),
              ) {
                Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Сначала")
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}
