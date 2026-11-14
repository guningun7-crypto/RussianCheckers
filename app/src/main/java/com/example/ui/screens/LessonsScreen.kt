package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.LessonProgressEntity
import com.example.domain.curriculum.InteractiveLesson
import com.example.domain.curriculum.LessonCategory
import com.example.domain.curriculum.RewardCatalog
import com.example.ui.components.HintsModeCard
import com.example.ui.components.MascotSpeechCard
import com.example.ui.components.StarCounterPill
import com.example.ui.theme.CrownGold
import com.example.ui.theme.ForestEmerald
import com.example.ui.viewmodel.AppProgressUiState

@Composable
fun LessonsScreen(
  progressState: AppProgressUiState,
  hintsEnabled: Boolean,
  onToggleHints: (Boolean) -> Unit,
  onLessonClick: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val lessons = progressState.allLessons
  val completedCount = lessons.count { progressState.lessonProgress[it.id]?.completed == true }
  val nextRecommendedId = lessons.firstOrNull { progressState.lessonProgress[it.id]?.completed != true }?.id
  val rank = RewardCatalog.rankForStars(progressState.stats.totalStars)

  val grouped = LessonCategory.entries.mapNotNull { cat ->
    val catLessons = lessons.filter { it.category == cat }
    if (catLessons.isEmpty()) null else cat to catLessons
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
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Школа шашек",
              style = MaterialTheme.typography.headlineMedium,
              color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
              text = "${rank.emoji} ${rank.title}",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.Bold,
            )
          }
          StarCounterPill(stars = progressState.stats.totalStars)
        }
      }

      item {
        val mascotMsg = when {
          completedCount == 0 -> "Привет! Я Совёнок Шашик. Давай вместе изучим русские шашки шаг за шагом!"
          completedCount < lessons.size -> "Ты уже прошёл $completedCount из ${lessons.size} уроков! Выбирай следующий и зарабатывай звёзды!"
          else -> "Ура! Все ${lessons.size} уроков пройдены! Теперь решай логические задачи и играй против «Мастера спорта»!"
        }
        MascotSpeechCard(message = mascotMsg)
      }

      item {
        HintsModeCard(
          hintsEnabled = hintsEnabled,
          onToggle = onToggleHints,
        )
      }

      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Text(
                text = "Прогресс обучения",
                style = MaterialTheme.typography.titleMedium,
              )
              Text(
                text = "$completedCount / ${lessons.size}",
                style = MaterialTheme.typography.titleMedium,
                color = ForestEmerald,
                fontWeight = FontWeight.Bold,
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
              progress = { completedCount.toFloat() / lessons.size.coerceAtLeast(1) },
              modifier = Modifier
                .fillMaxWidth()
                .height(10.dp),
              color = ForestEmerald,
              trackColor = MaterialTheme.colorScheme.surface,
              strokeCap = StrokeCap.Round,
            )
          }
        }
      }

      grouped.forEach { (category, catLessons) ->
        item(key = "header_${category.name}") {
          Text(
            text = category.title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 6.dp),
          )
        }
        items(catLessons, key = { it.id }) { lesson ->
          LessonCard(
            lesson = lesson,
            progress = progressState.lessonProgress[lesson.id],
            isRecommended = lesson.id == nextRecommendedId,
            isCustom = lesson.id in progressState.customLessonIds,
            onClick = { onLessonClick(lesson.id) },
          )
        }
      }

      item { Spacer(modifier = Modifier.height(12.dp)) }
    }
  }
}

@Composable
private fun LessonCard(
  lesson: InteractiveLesson,
  progress: LessonProgressEntity?,
  isRecommended: Boolean,
  isCustom: Boolean,
  onClick: () -> Unit,
) {
  val completed = progress?.completed == true
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("lesson_card_${lesson.id}")
      .clip(RoundedCornerShape(20.dp))
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (completed) {
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.48f)
      } else {
        MaterialTheme.colorScheme.surface
      },
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = if (isRecommended) 4.dp else 1.dp),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .background(
            if (completed) ForestEmerald
            else if (isRecommended) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.primaryContainer
          ),
        contentAlignment = Alignment.Center,
      ) {
        if (completed) {
          Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = "Урок пройден",
            tint = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.size(26.dp),
          )
        } else {
          Text(
            text = "${lesson.order}",
            style = MaterialTheme.typography.titleLarge,
            color = if (isRecommended) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer,
          )
        }
      }

      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Text(
            text = lesson.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
          )
          if (isCustom) {
            Surface(
              shape = RoundedCornerShape(50),
              color = MaterialTheme.colorScheme.tertiaryContainer,
            ) {
              Text(
                text = "Новое",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
              )
            }
          } else if (isRecommended && !completed) {
            Surface(
              shape = RoundedCornerShape(50),
              color = MaterialTheme.colorScheme.primaryContainer,
            ) {
              Text(
                text = "Дальше",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
              )
            }
          }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = lesson.subtitle,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      Column(horizontalAlignment = Alignment.End) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = CrownGold,
            modifier = Modifier.size(16.dp),
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = "+${lesson.starsReward}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Icon(
          imageVector = Icons.Filled.PlayArrow,
          contentDescription = "Открыть урок",
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(22.dp),
        )
      }
    }
  }
}
