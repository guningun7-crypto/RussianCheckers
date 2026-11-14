package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.checkers.AnalyzedMoveEntry
import com.example.domain.checkers.MoveQuality
import com.example.domain.checkers.PieceColor
import com.example.ui.components.CheckersBoardView
import com.example.ui.theme.CoralAlert
import com.example.ui.theme.CrownGold
import com.example.ui.theme.ForestEmerald
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.TerracottaOrange

@Composable
fun GameReviewSection(
  entries: List<AnalyzedMoveEntry>,
  isAnalyzing: Boolean,
  isBoardFlipped: Boolean,
  humanColor: PieceColor,
  onClose: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var selectedPlyIndex by rememberSaveable(entries.size) { mutableIntStateOf(entries.lastIndex.coerceAtLeast(0)) }

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Column {
        Text(
          text = "Работа над ошибками",
          style = MaterialTheme.typography.titleLarge,
        )
        Text(
          text = "Разбор каждого хода после партии",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
      OutlinedButton(
        onClick = onClose,
        modifier = Modifier
          .height(48.dp)
          .testTag("close_review_button"),
      ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
        Spacer(modifier = Modifier.width(6.dp))
        Text("К итогу")
      }
    }

    if (isAnalyzing) {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          CircularProgressIndicator()
          Text(
            text = "Совёнок анализирует партию и ищет лучшие ходы...",
            style = MaterialTheme.typography.bodyMedium,
          )
        }
      }
      return
    }

    if (entries.isEmpty()) {
      Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Text(
          text = "В этой партии ещё не было ходов для анализа.",
          modifier = Modifier.padding(16.dp),
        )
      }
      return
    }

    val safeIndex = selectedPlyIndex.coerceIn(0, entries.lastIndex)
    val current = entries[safeIndex]
    val qualityColor = colorForQuality(current.quality)

    // Evaluation bar: White advantage on left, Black on right (normalized +-500cp)
    val whiteEvalClamped = current.whiteEvalAfter.coerceIn(-500, 500)
    val whiteShare = ((whiteEvalClamped + 500f) / 1000f).coerceIn(0.05f, 0.95f)
    val advantageText = formatEvalText(current.whiteEvalAfter, humanColor)

    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = "Шкала оценки",
            style = MaterialTheme.typography.labelLarge,
          )
          Text(
            text = advantageText,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
          )
        }
        LinearProgressIndicator(
          progress = { whiteShare },
          modifier = Modifier
            .fillMaxWidth()
            .height(10.dp),
          color = Color(0xFFF3E2C5),
          trackColor = Color(0xFF3E302C),
          strokeCap = StrokeCap.Round,
        )
      }
    }

    CheckersBoardView(
      board = current.recorded.boardAfter,
      lastMove = current.recorded.move,
      animatedHint = current.bestAlternative,
      interactive = false,
      flipped = isBoardFlipped,
    )

    // Move quality & best alternative card
    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("review_move_detail_card"),
    ) {
      Column(
        modifier = Modifier.padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          val sideLabel = if (current.recorded.mover == PieceColor.WHITE) "Белые" else "Чёрные"
          Text(
            text = "Ход ${current.recorded.moveNumber} ($sideLabel): ${current.recorded.move.notationString}",
            style = MaterialTheme.typography.titleMedium,
          )
          Surface(
            shape = RoundedCornerShape(50),
            color = qualityColor.copy(alpha = 0.16f),
          ) {
            Text(
              text = current.quality.label,
              style = MaterialTheme.typography.labelMedium,
              color = qualityColor,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            )
          }
        }
        if (current.bestAlternative != null) {
          Text(
            text = "Сильнее было: ${current.bestAlternative.notationString} (золотая стрелка на доске)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
          )
        } else {
          Text(
            text = if (current.recorded.mover == humanColor) {
              "Отличный выбор — сильнее хода компьютер не нашёл!"
            } else {
              "Ответный ход соперника."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    }

    // Playback controls
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      OutlinedButton(
        onClick = { selectedPlyIndex = 0 },
        enabled = safeIndex > 0,
        modifier = Modifier.height(48.dp),
      ) {
        Icon(Icons.Filled.SkipPrevious, contentDescription = "В начало")
      }
      FilledTonalButton(
        onClick = { selectedPlyIndex = (safeIndex - 1).coerceAtLeast(0) },
        enabled = safeIndex > 0,
        modifier = Modifier
          .weight(1f)
          .height(48.dp)
          .testTag("review_prev_button"),
      ) {
        Icon(Icons.AutoMirrored.Filled.NavigateBefore, contentDescription = null)
        Text("Назад")
      }
      FilledTonalButton(
        onClick = { selectedPlyIndex = (safeIndex + 1).coerceAtMost(entries.lastIndex) },
        enabled = safeIndex < entries.lastIndex,
        modifier = Modifier
          .weight(1f)
          .height(48.dp)
          .testTag("review_next_button"),
      ) {
        Text("Вперёд")
        Icon(Icons.AutoMirrored.Filled.NavigateNext, contentDescription = null)
      }
      OutlinedButton(
        onClick = { selectedPlyIndex = entries.lastIndex },
        enabled = safeIndex < entries.lastIndex,
        modifier = Modifier.height(48.dp),
      ) {
        Icon(Icons.Filled.SkipNext, contentDescription = "В конец")
      }
    }

    // Scrollable horizontal notation pills
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      entries.forEachIndexed { idx, entry ->
        val selected = idx == safeIndex
        val dotColor = colorForQuality(entry.quality)
        val prefix = if (entry.recorded.mover == PieceColor.WHITE) "${entry.recorded.moveNumber}." else "${entry.recorded.moveNumber}..."
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { selectedPlyIndex = idx },
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(50))
                .background(dotColor),
            )
            Text(
              text = "$prefix ${entry.recorded.move.notationString}",
              style = MaterialTheme.typography.labelLarge,
              fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            )
          }
        }
      }
    }
  }
}

private fun colorForQuality(quality: MoveQuality): Color = when (quality) {
  MoveQuality.BEST -> ForestEmerald
  MoveQuality.GOOD -> SkyBlue
  MoveQuality.INACCURACY -> CrownGold
  MoveQuality.MISTAKE -> TerracottaOrange
  MoveQuality.BLUNDER -> CoralAlert
}

private fun formatEvalText(whiteEvalCp: Int, humanColor: PieceColor): String {
  val absDiff = kotlin.math.abs(whiteEvalCp) / 100.0
  return when {
    kotlin.math.abs(whiteEvalCp) >= 50_000 -> if (whiteEvalCp > 0) "Выигрыш белых" else "Выигрыш чёрных"
    kotlin.math.abs(whiteEvalCp) < 35 -> "Равная позиция"
    whiteEvalCp > 0 -> {
      val tag = if (humanColor == PieceColor.WHITE) "Твой перевес" else "Перевес белых"
      "$tag +${String.format(java.util.Locale.US, "%.1f", absDiff)}"
    }
    else -> {
      val tag = if (humanColor == PieceColor.BLACK) "Твой перевес" else "Перевес чёрных"
      "$tag +${String.format(java.util.Locale.US, "%.1f", absDiff)}"
    }
  }
}
