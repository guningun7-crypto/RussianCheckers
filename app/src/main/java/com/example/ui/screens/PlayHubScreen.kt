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
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.checkers.BotDifficulty
import com.example.ui.components.HintsModeCard
import com.example.ui.components.MascotSpeechCard
import com.example.ui.components.StarCounterPill
import com.example.ui.theme.CoralAlert
import com.example.ui.theme.CrownGold
import com.example.ui.theme.ForestEmerald
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.TerracottaOrange
import com.example.ui.viewmodel.AppProgressUiState

@Composable
fun PlayHubScreen(
  progressState: AppProgressUiState,
  hintsEnabled: Boolean,
  onToggleHints: (Boolean) -> Unit,
  onStartBotMatch: (BotDifficulty) -> Unit,
  onStartTwoPlayerMatch: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val totalStars = progressState.stats.totalStars

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
              text = "Игровой зал",
              style = MaterialTheme.typography.headlineMedium,
            )
            Text(
              text = "Побед в партиях: ${progressState.stats.gamesWon}",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
          StarCounterPill(stars = totalStars)
        }
      }

      item {
        MascotSpeechCard(
          title = "Выбери уровень сложности",
          message = "Все 5 уровней открыты сразу — от «Малыша-Совёнка» до сильного «Мастера спорта» с глубоким просчётом!",
        )
      }

      item {
        HintsModeCard(
          hintsEnabled = hintsEnabled,
          onToggle = onToggleHints,
        )
      }

      items(BotDifficulty.entries, key = { it.name }) { diff ->
        val isUnlocked = totalStars >= diff.requiredStarsToUnlock
        val levelColor = when (diff) {
          BotDifficulty.BEGINNER -> ForestEmerald
          BotDifficulty.LEARNER -> SkyBlue
          BotDifficulty.TACTICIAN -> TerracottaOrange
          BotDifficulty.GRANDMASTER -> MaterialTheme.colorScheme.tertiary
          BotDifficulty.CHAMPION -> CoralAlert
        }

        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("play_difficulty_${diff.name}")
            .clip(RoundedCornerShape(20.dp))
            .clickable(enabled = isUnlocked) { onStartBotMatch(diff) },
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) {
              MaterialTheme.colorScheme.surface
            } else {
              MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
            },
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 3.dp else 0.dp),
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
          ) {
            Box(
              modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(
                  if (isUnlocked) levelColor.copy(alpha = 0.16f)
                  else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                ),
              contentAlignment = Alignment.Center,
            ) {
              Icon(
                imageVector = if (isUnlocked) Icons.Filled.SmartToy else Icons.Filled.Lock,
                contentDescription = null,
                tint = if (isUnlocked) levelColor else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp),
              )
            }

            Column(modifier = Modifier.weight(1f)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                Text(
                  text = "Уровень ${diff.level}: ${diff.title}",
                  style = MaterialTheme.typography.titleMedium,
                )
              }
              Spacer(modifier = Modifier.height(3.dp))
              Text(
                text = diff.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
              Spacer(modifier = Modifier.height(6.dp))
              if (isUnlocked) {
                Surface(
                  shape = RoundedCornerShape(50),
                  color = MaterialTheme.colorScheme.tertiaryContainer,
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                  ) {
                    Icon(
                      imageVector = Icons.Filled.Star,
                      contentDescription = null,
                      tint = CrownGold,
                      modifier = Modifier.size(14.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "Награда за победу: +${diff.starsReward}",
                      style = MaterialTheme.typography.labelMedium,
                      fontWeight = FontWeight.Bold,
                    )
                  }
                }
              } else {
                Text(
                  text = "Откроется при ${diff.requiredStarsToUnlock} ⭐ (осталось ${diff.requiredStarsToUnlock - totalStars})",
                  style = MaterialTheme.typography.labelMedium,
                  color = MaterialTheme.colorScheme.primary,
                  fontWeight = FontWeight.Bold,
                )
              }
            }

            if (isUnlocked) {
              Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Начать игру",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp),
              )
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Семейная игра",
          style = MaterialTheme.typography.titleLarge,
        )
      }

      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("play_two_players_card")
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onStartTwoPlayerMatch),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)),
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
          ) {
            Box(
              modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
              contentAlignment = Alignment.Center,
            ) {
              Icon(
                imageVector = Icons.Filled.Groups,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(28.dp),
              )
            }
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Игра вдвоём на одном экране",
                style = MaterialTheme.typography.titleMedium,
              )
              Text(
                text = "Сыграйте вместе с родителями или друзьями по правилам русских шашек!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
            Icon(
              imageVector = Icons.Filled.PlayArrow,
              contentDescription = "Играть вдвоём",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(28.dp),
            )
          }
        }
      }

      item { Spacer(modifier = Modifier.height(12.dp)) }
    }
  }
}
