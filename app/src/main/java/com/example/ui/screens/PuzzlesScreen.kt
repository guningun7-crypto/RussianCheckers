package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.PuzzleProgressEntity
import com.example.domain.curriculum.DailyPuzzleHelper
import com.example.domain.curriculum.EndgameCatalog
import com.example.domain.curriculum.LogicPuzzle
import com.example.domain.curriculum.PuzzleDifficulty
import com.example.ui.components.HintsModeCard
import com.example.ui.components.MascotSpeechCard
import com.example.ui.components.StarCounterPill
import com.example.ui.theme.CoralAlert
import com.example.ui.theme.CrownGold
import com.example.ui.theme.ForestEmerald
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.TerracottaOrange
import com.example.ui.viewmodel.AppProgressUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PuzzlesScreen(
  progressState: AppProgressUiState,
  hintsEnabled: Boolean,
  onToggleHints: (Boolean) -> Unit,
  onPuzzleClick: (String, Boolean) -> Unit,
  onStartPuzzleRush: () -> Unit,
  onOpenEndgameHub: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var selectedDifficulty by remember { mutableStateOf<PuzzleDifficulty?>(null) }
  val allPuzzles = progressState.allPuzzles
  val puzzleProgress = progressState.puzzleProgress

  val filtered = remember(selectedDifficulty, allPuzzles) {
    if (selectedDifficulty == null) allPuzzles
    else allPuzzles.filter { it.difficulty == selectedDifficulty }
  }
  val puzzleIndexById = remember(allPuzzles) {
    allPuzzles.withIndex().associate { (index, p) -> p.id to (index + 1) }
  }
  val solvedCount = remember(allPuzzles, puzzleProgress) {
    allPuzzles.count { puzzleProgress[it.id]?.solved == true }
  }
  val difficultyCounts = remember(allPuzzles) {
    PuzzleDifficulty.entries.associateWith { diff -> allPuzzles.count { it.difficulty == diff } }
  }
  val dailyPuzzle = remember(allPuzzles, progressState.todayDateKey) {
    DailyPuzzleHelper.pickDailyPuzzle(allPuzzles, progressState.todayDateKey)
  }

  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.TopCenter,
  ) {
    LazyColumn(
      modifier = Modifier
        .widthIn(max = 700.dp)
        .fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      item(key = "header", contentType = "header") {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Логические задачи",
              style = MaterialTheme.typography.headlineMedium,
            )
            Text(
              text = "Решено $solvedCount из ${allPuzzles.size} комбинаций",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
          StarCounterPill(stars = progressState.stats.totalStars)
        }
      }

      item(key = "mascot", contentType = "mascot") {
        MascotSpeechCard(
          title = "Тренировка ума",
          message = if (hintsEnabled) {
            "Решай сложные комбинации: турецкий удар, многоходовые жертвы, мельница и ловля дамки!"
          } else {
            "Включён режим «Без подсказок»: точки ходов и подсказки скрыты — решай задачи полностью самостоятельно!"
          },
        )
      }

      if (dailyPuzzle != null) {
        item(key = "daily_puzzle", contentType = "daily_puzzle") {
          val solvedToday = progressState.dailyPuzzleSolvedToday
          Card(
            onClick = { onPuzzleClick(dailyPuzzle.id, true) },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("daily_puzzle_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (solvedToday) {
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f)
              } else {
                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.78f)
              },
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              Box(
                modifier = Modifier
                  .size(48.dp)
                  .clip(CircleShape)
                  .background(if (solvedToday) ForestEmerald else CrownGold),
                contentAlignment = Alignment.Center,
              ) {
                Icon(
                  imageVector = if (solvedToday) Icons.Filled.CheckCircle else Icons.Filled.CalendarToday,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.onPrimary,
                  modifier = Modifier.size(24.dp),
                )
              }
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = if (solvedToday) "Задача дня выполнена!" else "Задача дня • +2 бонус ⭐",
                  style = MaterialTheme.typography.labelLarge,
                  color = if (solvedToday) ForestEmerald else MaterialTheme.colorScheme.primary,
                  fontWeight = FontWeight.Bold,
                )
                Text(
                  text = "${dailyPuzzle.title} (${dailyPuzzle.difficulty.label})",
                  style = MaterialTheme.typography.titleMedium,
                )
              }
            }
          }
        }
      }

      item(key = "quick_modes", contentType = "quick_modes") {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Card(
            onClick = onStartPuzzleRush,
            modifier = Modifier
              .weight(1f)
              .testTag("puzzle_rush_entry_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
          ) {
            Column(
              modifier = Modifier.padding(14.dp),
              verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
              ) {
                Icon(Icons.Filled.Bolt, contentDescription = null, tint = TerracottaOrange)
                Text(
                  text = "Штурм 3 мин",
                  style = MaterialTheme.typography.titleMedium,
                )
              }
              Text(
                text = "Рекорд: ${progressState.stats.puzzleRushBest} • 3 жизни",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
          }

          Card(
            onClick = onOpenEndgameHub,
            modifier = Modifier
              .weight(1f)
              .testTag("puzzles_endgame_entry_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
          ) {
            Column(
              modifier = Modifier.padding(14.dp),
              verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
              ) {
                Icon(Icons.Filled.MilitaryTech, contentDescription = null, tint = ForestEmerald)
                Text(
                  text = "Эндшпиль",
                  style = MaterialTheme.typography.titleMedium,
                )
              }
              Text(
                text = "Пройдено ${progressState.endgamesSolvedIds.size}/${EndgameCatalog.scenarios.size}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
          }
        }
      }

      item(key = "hints_toggle", contentType = "hints") {
        HintsModeCard(
          hintsEnabled = hintsEnabled,
          onToggle = onToggleHints,
        )
      }

      item(key = "difficulty_filters", contentType = "filters") {
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          FilterChip(
            selected = selectedDifficulty == null,
            onClick = { selectedDifficulty = null },
            label = { Text("Все (${allPuzzles.size})") },
            modifier = Modifier.testTag("puzzle_filter_all"),
          )
          PuzzleDifficulty.entries.forEach { diff ->
            val diffCount = difficultyCounts[diff] ?: 0
            FilterChip(
              selected = selectedDifficulty == diff,
              onClick = { selectedDifficulty = if (selectedDifficulty == diff) null else diff },
              label = { Text("${diff.label} ($diffCount)") },
              modifier = Modifier.testTag("puzzle_filter_${diff.name}"),
            )
          }
        }
      }

      items(
        items = filtered,
        key = { it.id },
        contentType = { "puzzle_card" },
      ) { puzzle ->
        PuzzleCard(
          puzzle = puzzle,
          displayNumber = puzzleIndexById[puzzle.id] ?: puzzle.order,
          isCustom = puzzle.id in progressState.customPuzzleIds,
          progress = puzzleProgress[puzzle.id],
          onClick = { onPuzzleClick(puzzle.id, false) },
        )
      }

      item(key = "bottom_spacer", contentType = "spacer") {
        Spacer(modifier = Modifier.height(12.dp))
      }
    }
  }
}

@Composable
private fun PuzzleCard(
  puzzle: LogicPuzzle,
  displayNumber: Int,
  isCustom: Boolean,
  progress: PuzzleProgressEntity?,
  onClick: () -> Unit,
) {
  val solved = progress?.solved == true
  val diffColor = when (puzzle.difficulty) {
    PuzzleDifficulty.EASY -> ForestEmerald
    PuzzleDifficulty.MEDIUM -> SkyBlue
    PuzzleDifficulty.HARD -> TerracottaOrange
    PuzzleDifficulty.MASTER -> CoralAlert
  }

  Card(
    onClick = onClick,
    modifier = Modifier
      .fillMaxWidth()
      .testTag("puzzle_card_${puzzle.id}"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (solved) {
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
      } else {
        MaterialTheme.colorScheme.surface
      },
    ),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .background(if (solved) ForestEmerald else diffColor.copy(alpha = 0.16f)),
        contentAlignment = Alignment.Center,
      ) {
        if (solved) {
          Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = "Задача решена",
            tint = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.size(26.dp),
          )
        } else {
          Icon(
            imageVector = Icons.Filled.Extension,
            contentDescription = null,
            tint = diffColor,
            modifier = Modifier.size(26.dp),
          )
        }
      }

      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          Surface(
            shape = RoundedCornerShape(50),
            color = diffColor.copy(alpha = 0.15f),
          ) {
            Text(
              text = puzzle.difficulty.label,
              style = MaterialTheme.typography.labelMedium,
              color = diffColor,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            )
          }
          Surface(
            shape = RoundedCornerShape(50),
            color = MaterialTheme.colorScheme.surfaceVariant,
          ) {
            Text(
              text = puzzle.themeTag,
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            )
          }
          if (isCustom) {
            Surface(
              shape = RoundedCornerShape(50),
              color = MaterialTheme.colorScheme.tertiaryContainer,
            ) {
              Text(
                text = "Новое",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
              )
            }
          }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
          text = "$displayNumber. ${puzzle.title}",
          style = MaterialTheme.typography.titleMedium,
        )
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Filled.Star,
          contentDescription = null,
          tint = CrownGold,
          modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
          text = "+${puzzle.difficulty.starsReward}",
          style = MaterialTheme.typography.labelLarge,
        )
      }
    }
  }
}
