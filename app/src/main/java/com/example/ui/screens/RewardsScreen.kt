package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.curriculum.RewardBadge
import com.example.domain.curriculum.RewardCatalog
import com.example.ui.components.badgeIconFor
import com.example.ui.theme.CrownGold
import com.example.ui.theme.ForestEmerald
import com.example.ui.viewmodel.AppProgressUiState

@Composable
fun RewardsScreen(
  progressState: AppProgressUiState,
  modifier: Modifier = Modifier,
) {
  val stars = progressState.stats.totalStars
  val rank = RewardCatalog.rankForStars(stars)
  val completedLessons = progressState.allLessons.count { progressState.lessonProgress[it.id]?.completed == true }
  val solvedPuzzles = progressState.allPuzzles.count { progressState.puzzleProgress[it.id]?.solved == true }
  val badges = RewardCatalog.badges

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
        Text(
          text = "Мои награды",
          style = MaterialTheme.typography.headlineMedium,
        )
      }

      item {
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        ) {
          Column(modifier = Modifier.fillMaxWidth()) {
            Image(
              painter = painterResource(id = R.drawable.img_trophy_chest),
              contentDescription = stringResource(id = R.string.trophy_desc),
              modifier = Modifier
                .fillMaxWidth()
                .height(175.dp),
              contentScale = ContentScale.Crop,
            )
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Column {
                  Text(
                    text = "Текущее звание",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                  Text(
                    text = "${rank.emoji} ${rank.title}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                  )
                }
                Surface(
                  shape = RoundedCornerShape(50),
                  color = MaterialTheme.colorScheme.tertiaryContainer,
                ) {
                  Text(
                    text = "$stars ⭐",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                  )
                }
              }

              Spacer(modifier = Modifier.height(12.dp))
              if (rank.nextRankStars != null) {
                val span = (rank.nextRankStars - rank.minStars).coerceAtLeast(1)
                val progress = ((stars - rank.minStars).toFloat() / span).coerceIn(0f, 1f)
                Text(
                  text = "До следующего звания: ${rank.nextRankStars - stars} ⭐",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                  progress = { progress },
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                  color = CrownGold,
                  trackColor = MaterialTheme.colorScheme.surfaceVariant,
                  strokeCap = StrokeCap.Round,
                )
              } else {
                Text(
                  text = "Ты достиг высшего звания Мастера Шашек!",
                  style = MaterialTheme.typography.bodyMedium,
                  color = ForestEmerald,
                  fontWeight = FontWeight.Bold,
                )
              }
            }
          }
        }
      }

      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          StatMiniCard(
            label = "Уроки",
            value = "$completedLessons/${progressState.allLessons.size}",
            modifier = Modifier.weight(1f),
          )
          StatMiniCard(
            label = "Задачи",
            value = "$solvedPuzzles/${progressState.allPuzzles.size}",
            modifier = Modifier.weight(1f),
          )
          StatMiniCard(
            label = "Победы",
            value = "${progressState.stats.gamesWon}",
            modifier = Modifier.weight(1f),
          )
          StatMiniCard(
            label = "Серия",
            value = "${progressState.stats.currentStreak} 🔥",
            modifier = Modifier.weight(1f),
          )
        }
      }

      item {
        Text(
          text = "Коллекция медалей (${progressState.unlockedBadgeIds.size}/${badges.size})",
          style = MaterialTheme.typography.titleLarge,
        )
      }

      items(badges.chunked(2).size) { rowIdx ->
        val pair = badges.chunked(2)[rowIdx]
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          pair.forEach { badge ->
            BadgeCard(
              badge = badge,
              unlocked = badge.id in progressState.unlockedBadgeIds,
              modifier = Modifier.weight(1f),
            )
          }
          if (pair.size == 1) {
            Spacer(modifier = Modifier.weight(1f))
          }
        }
      }

      item { Spacer(modifier = Modifier.height(12.dp)) }
    }
  }
}

@Composable
private fun StatMiniCard(
  label: String,
  value: String,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp, horizontal = 6.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

@Composable
private fun BadgeCard(
  badge: RewardBadge,
  unlocked: Boolean,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier
      .testTag("badge_card_${badge.id}")
      .alpha(if (unlocked) 1f else 0.65f),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (unlocked) {
        MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.65f)
      } else {
        MaterialTheme.colorScheme.surfaceVariant
      },
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = if (unlocked) 2.dp else 0.dp),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Box(
        modifier = Modifier
          .size(54.dp)
          .clip(CircleShape)
          .background(
            if (unlocked) CrownGold.copy(alpha = 0.25f)
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
          ),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = if (unlocked) badgeIconFor(badge.iconName) else Icons.Filled.Lock,
          contentDescription = badge.title,
          tint = if (unlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(28.dp),
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = badge.title,
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center,
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = badge.description,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}
