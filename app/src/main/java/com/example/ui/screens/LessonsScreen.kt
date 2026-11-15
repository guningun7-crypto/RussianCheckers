package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.LessonProgressEntity
import com.example.domain.curriculum.InteractiveLesson
import com.example.domain.curriculum.LessonFolder
import com.example.domain.curriculum.RewardCatalog
import com.example.ui.components.HintsModeCard
import com.example.ui.components.MascotSpeechCard
import com.example.ui.components.StarCounterPill
import com.example.ui.theme.CoralAlert
import com.example.ui.theme.CrownGold
import com.example.ui.theme.ForestEmerald
import com.example.ui.theme.TerracottaOrange
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
  val lessonProgress = progressState.lessonProgress
  val totalStars = progressState.stats.totalStars

  val completedCount = remember(lessons, lessonProgress) {
    lessons.count { lessonProgress[it.id]?.completed == true }
  }
  val nextRecommendedId = remember(lessons, lessonProgress) {
    lessons.firstOrNull { lessonProgress[it.id]?.completed != true }?.id
  }
  val rank = remember(totalStars) { RewardCatalog.rankForStars(totalStars) }

  val folderMap = remember(lessons, progressState.customLessonIds) {
    val builtIn = lessons.filterNot { it.id in progressState.customLessonIds }
    LessonFolder.entries.mapNotNull { folder ->
      val list = builtIn.filter { it.folder == folder }
      if (list.isEmpty()) null else folder to list
    }
  }
  val customLessons = remember(lessons, progressState.customLessonIds) {
    lessons.filter { it.id in progressState.customLessonIds }
  }

  // «Для начинающих» свёрнута по умолчанию; новые папки раскрываются по нажатию
  val expandedFolders = remember {
    mutableStateMapOf(
      LessonFolder.BEGINNER.name to false,
      LessonFolder.TACTICIAN.name to true,
      LessonFolder.GRANDMASTER.name to true,
      "CUSTOM" to true,
    )
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
          StarCounterPill(stars = totalStars)
        }
      }

      item(key = "mascot", contentType = "mascot") {
        val mascotMsg = when {
          completedCount == 0 -> "Привет! Я Совёнок Шашик. Базовые 17 уроков собраны в папке «Для начинающих», а ниже ждут уроки «Хитрый Тактик» и «Гроссмейстер»!"
          completedCount < lessons.size -> "Ты уже прошёл $completedCount из ${lessons.size} уроков! Выбирай папку под свой уровень и зарабатывай звёзды!"
          else -> "Браво! Все ${lessons.size} уроков всех трёх папок пройдены! Испытай себя в задачах Мастера и в игре против Чемпиона!"
        }
        MascotSpeechCard(message = mascotMsg)
      }

      item(key = "hints_toggle", contentType = "hints") {
        HintsModeCard(
          hintsEnabled = hintsEnabled,
          onToggle = onToggleHints,
        )
      }

      item(key = "overall_progress", contentType = "progress") {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Text(
                text = "Общий прогресс обучения",
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

      folderMap.forEach { (folder, folderLessons) ->
        val isExpanded = expandedFolders[folder.name] ?: false
        val accentColor = when (folder) {
          LessonFolder.BEGINNER -> ForestEmerald
          LessonFolder.TACTICIAN -> TerracottaOrange
          LessonFolder.GRANDMASTER -> CoralAlert
        }

        item(key = "folder_${folder.name}", contentType = "folder_header") {
          val folderDone = remember(folderLessons, lessonProgress) {
            folderLessons.count { lessonProgress[it.id]?.completed == true }
          }
          LessonFolderCard(
            title = folder.title,
            subtitle = folder.subtitle,
            badgeText = folder.badgeText,
            completedCount = folderDone,
            totalCount = folderLessons.size,
            accentColor = accentColor,
            expanded = isExpanded,
            testTag = "lesson_folder_${folder.name}",
            onToggle = { expandedFolders[folder.name] = !isExpanded },
          )
        }

        if (isExpanded) {
          items(
            items = folderLessons,
            key = { it.id },
            contentType = { "lesson_card" },
          ) { lesson ->
            LessonCard(
              lesson = lesson,
              progress = lessonProgress[lesson.id],
              isRecommended = lesson.id == nextRecommendedId,
              isCustom = false,
              onClick = { onLessonClick(lesson.id) },
            )
          }
        }
      }

      if (customLessons.isNotEmpty()) {
        val customExpanded = expandedFolders["CUSTOM"] ?: true
        item(key = "folder_CUSTOM", contentType = "folder_header") {
          val customDone = remember(customLessons, lessonProgress) {
            customLessons.count { lessonProgress[it.id]?.completed == true }
          }
          LessonFolderCard(
            title = "Загруженные уроки",
            subtitle = "Импортированные уроки из пакетов обновлений",
            badgeText = "Бонус",
            completedCount = customDone,
            totalCount = customLessons.size,
            accentColor = CrownGold,
            expanded = customExpanded,
            testTag = "lesson_folder_CUSTOM",
            onToggle = { expandedFolders["CUSTOM"] = !customExpanded },
          )
        }
        if (customExpanded) {
          items(
            items = customLessons,
            key = { it.id },
            contentType = { "lesson_card" },
          ) { lesson ->
            LessonCard(
              lesson = lesson,
              progress = lessonProgress[lesson.id],
              isRecommended = lesson.id == nextRecommendedId,
              isCustom = true,
              onClick = { onLessonClick(lesson.id) },
            )
          }
        }
      }

      item(key = "bottom_spacer", contentType = "spacer") {
        Spacer(modifier = Modifier.height(12.dp))
      }
    }
  }
}

@Composable
private fun LessonFolderCard(
  title: String,
  subtitle: String,
  badgeText: String,
  completedCount: Int,
  totalCount: Int,
  accentColor: Color,
  expanded: Boolean,
  testTag: String,
  onToggle: () -> Unit,
) {
  Card(
    onClick = onToggle,
    modifier = Modifier
      .fillMaxWidth()
      .testTag(testTag),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (expanded) {
        accentColor.copy(alpha = 0.13f)
      } else {
        MaterialTheme.colorScheme.surface
      },
    ),
    border = BorderStroke(
      width = 1.dp,
      color = if (expanded) accentColor.copy(alpha = 0.35f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Box(
          modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(accentColor.copy(alpha = 0.18f)),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            imageVector = if (expanded) Icons.Filled.FolderOpen else Icons.Filled.Folder,
            contentDescription = if (expanded) "Свернуть папку" else "Открыть папку",
            tint = accentColor,
            modifier = Modifier.size(26.dp),
          )
        }

        Column(modifier = Modifier.weight(1f)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Text(
              text = title,
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface,
            )
            Surface(
              shape = RoundedCornerShape(50),
              color = accentColor.copy(alpha = 0.18f),
            ) {
              Text(
                text = badgeText,
                style = MaterialTheme.typography.labelMedium,
                color = accentColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
              )
            }
          }
          Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "$completedCount / $totalCount",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = accentColor,
          )
          Icon(
            imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
            contentDescription = if (expanded) "Свернуть" else "Развернуть",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }

      LinearProgressIndicator(
        progress = { completedCount.toFloat() / totalCount.coerceAtLeast(1) },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp),
        color = accentColor,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
        strokeCap = StrokeCap.Round,
      )
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
    onClick = onClick,
    modifier = Modifier
      .fillMaxWidth()
      .testTag("lesson_card_${lesson.id}"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (completed) {
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.48f)
      } else {
        MaterialTheme.colorScheme.surface
      },
    ),
    border = BorderStroke(
      width = if (isRecommended && !completed) 1.5.dp else 1.dp,
      color = if (isRecommended && !completed) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
      } else {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
      },
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
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
