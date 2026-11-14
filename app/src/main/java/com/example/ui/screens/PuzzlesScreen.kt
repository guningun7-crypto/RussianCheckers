package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Extension
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
  onPuzzleClick: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  var selectedDifficulty by remember { mutableStateOf<PuzzleDifficulty?>(null) }
  val allPuzzles = progressState.allPuzzles
  val filtered = remember(selectedDifficulty, allPuzzles) {
    if (selectedDifficulty == null) allPuzzles
    else allPuzzles.filter { it.difficulty == selectedDifficulty }
  }
  val solvedCount = allPuzzles.count { progressState.puzzleProgress[it.id]?.solved == true }

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
      item {
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

      item {
        MascotSpeechCard(
          title = "Тренировка ума",
          message = if (hintsEnabled) {
            "Решай сложные комбинации: турецкий удар, многоходовые жертвы, мельница и ловля дамки!"
          } else {
            "Включён режим «Без подсказок»: точки ходов и подсказки скрыты — решай задачи полностью самостоятельно!"
          },
        )
      }

      item {
        HintsModeCard(
          hintsEnabled = hintsEnabled,
          onToggle = onToggleHints,
        )
      }

      item {
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
            FilterChip(
              selected = selectedDifficulty == diff,
              onClick = { selectedDifficulty = if (selectedDifficulty == diff) null else diff },
              label = { Text(diff.label) },
              modifier = Modifier.testTag("puzzle_filter_${diff.name}"),
            )
          }
        }
      }

      items(filtered, key = { it.id }) { puzzle ->
        PuzzleCard(
          puzzle = puzzle,
          displayNumber = allPuzzles.indexOfFirst { it.id == puzzle.id }.let { if (it >= 0) it + 1 else puzzle.order },
          isCustom = puzzle.id in progressState.customPuzzleIds,
          progress = progressState.puzzleProgress[puzzle.id],
          onClick = { onPuzzleClick(puzzle.id) },
        )
      }

      item { Spacer(modifier = Modifier.height(12.dp)) }
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
    modifier = Modifier
      .fillMaxWidth()
      .testTag("puzzle_card_${puzzle.id}")
      .clip(RoundedCornerShape(20.dp))
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (solved) {
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
      } else {
        MaterialTheme.colorScheme.surface
      },
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
