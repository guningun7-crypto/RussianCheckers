package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.checkers.BotDifficulty
import com.example.domain.checkers.EloRatingCalculator
import com.example.domain.checkers.PlayerColorPreference
import com.example.domain.checkers.TimeControlOption
import com.example.domain.curriculum.EndgameCatalog
import com.example.ui.components.HintsModeCard
import com.example.ui.components.StarCounterPill
import com.example.ui.theme.CoralAlert
import com.example.ui.theme.CrownGold
import com.example.ui.theme.ForestEmerald
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.TerracottaOrange
import com.example.ui.viewmodel.AppProgressUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlayHubScreen(
  progressState: AppProgressUiState,
  hintsEnabled: Boolean,
  onToggleHints: (Boolean) -> Unit,
  onStartBotMatch: (BotDifficulty, PlayerColorPreference, TimeControlOption, Boolean) -> Unit,
  onStartTwoPlayerMatch: (TimeControlOption, Boolean) -> Unit,
  onOpenEndgameHub: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val totalStars = progressState.stats.totalStars
  val playerElo = progressState.stats.eloRating
  val sportsRank = EloRatingCalculator.sportsRankForElo(playerElo)

  var colorPref by rememberSaveable { mutableStateOf(PlayerColorPreference.WHITE) }
  var timeControl by rememberSaveable { mutableStateOf(TimeControlOption.NONE) }
  var flyingCheckers by rememberSaveable { mutableStateOf(false) }

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
              text = "Побед: ${progressState.stats.gamesWon} • Ничьих: ${progressState.stats.drawsCount}",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
          StarCounterPill(stars = totalStars)
        }
      }

      // Internal Elo & Sports Rank Banner
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)),
          modifier = Modifier.fillMaxWidth().testTag("play_hub_elo_card"),
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
                .background(CrownGold.copy(alpha = 0.25f)),
              contentAlignment = Alignment.Center,
            ) {
              Text(
                text = sportsRank.badgeEmoji,
                style = MaterialTheme.typography.headlineSmall,
              )
            }
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Рейтинг Эло: $playerElo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
              )
              Text(
                text = sportsRank.title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
              )
            }
            if (sportsRank.nextElo != null) {
              Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.surface,
              ) {
                Text(
                  text = "Цель ${sportsRank.nextElo}",
                  style = MaterialTheme.typography.labelMedium,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                )
              }
            }
          }
        }
      }

      // Match Options Card: Color, Clock, Flying Checkers
      item(key = "match_options", contentType = "match_options") {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)),
          elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Text(
              text = "Настройки партии",
              style = MaterialTheme.typography.titleMedium,
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                text = "Цвет шашек (против компьютера)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
              FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PlayerColorPreference.entries.forEach { pref ->
                  FilterChip(
                    selected = colorPref == pref,
                    onClick = { colorPref = pref },
                    label = { Text(pref.label) },
                    modifier = Modifier.testTag("match_color_${pref.name}"),
                  )
                }
              }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                text = "Контроль времени",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
              FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TimeControlOption.entries.forEach { tc ->
                  FilterChip(
                    selected = timeControl == tc,
                    onClick = { timeControl = tc },
                    label = { Text(tc.label) },
                    modifier = Modifier.testTag("match_clock_${tc.name}"),
                  )
                }
              }
            }

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .clickable { flyingCheckers = !flyingCheckers }
                .padding(vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
              ) {
                Icon(
                  imageVector = Icons.Filled.Casino,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                )
                Column {
                  Text(
                    text = "Летающие шашки",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                  )
                  Text(
                    text = "Случайный жеребьёвочный дебют 1-го хода",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                }
              }
              Switch(
                checked = flyingCheckers,
                onCheckedChange = { flyingCheckers = it },
                modifier = Modifier.testTag("flying_checkers_switch"),
              )
            }
          }
        }
      }

      item {
        HintsModeCard(
          hintsEnabled = hintsEnabled,
          onToggle = onToggleHints,
        )
      }

      items(
        items = BotDifficulty.entries,
        key = { it.name },
        contentType = { "bot_card" },
      ) { diff ->
        val isUnlocked = totalStars >= diff.requiredStarsToUnlock
        val botElo = EloRatingCalculator.botEloFor(diff)
        val levelColor = when (diff) {
          BotDifficulty.BEGINNER -> ForestEmerald
          BotDifficulty.LEARNER -> SkyBlue
          BotDifficulty.TACTICIAN -> TerracottaOrange
          BotDifficulty.GRANDMASTER -> MaterialTheme.colorScheme.tertiary
          BotDifficulty.CHAMPION -> CoralAlert
        }

        Card(
          onClick = { onStartBotMatch(diff, colorPref, timeControl, flyingCheckers) },
          enabled = isUnlocked,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("play_difficulty_${diff.name}"),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) {
              MaterialTheme.colorScheme.surface
            } else {
              MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
            },
          ),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)),
          elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
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
                  text = "${diff.title}",
                  style = MaterialTheme.typography.titleMedium,
                )
                Surface(
                  shape = RoundedCornerShape(50),
                  color = levelColor.copy(alpha = 0.14f),
                ) {
                  Text(
                    text = "Эло $botElo",
                    style = MaterialTheme.typography.labelMedium,
                    color = levelColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                  )
                }
              }
              Spacer(modifier = Modifier.height(3.dp))
              Text(
                text = diff.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
              Spacer(modifier = Modifier.height(6.dp))
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
                    text = "Награда: +${diff.starsReward} ⭐",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                  )
                }
              }
            }

            Icon(
              imageVector = Icons.Filled.PlayArrow,
              contentDescription = "Начать игру",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(28.dp),
            )
          }
        }
      }

      // Endgame Sparring quick entry card
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("play_endgame_hub_card")
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onOpenEndgameHub),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f)),
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
                .background(ForestEmerald),
              contentAlignment = Alignment.Center,
            ) {
              Icon(
                imageVector = Icons.Filled.MilitaryTech,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.size(28.dp),
              )
            }
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Эндшпиль-спарринг (${progressState.endgamesSolvedIds.size}/${EndgameCatalog.scenarios.size})",
                style = MaterialTheme.typography.titleMedium,
              )
              Text(
                text = "Отработай треугольник Петрова, оппозицию и реализацию дамок против бота!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
            Icon(
              imageVector = Icons.Filled.PlayArrow,
              contentDescription = "Открыть эндшпили",
              tint = ForestEmerald,
              modifier = Modifier.size(28.dp),
            )
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
            .clickable { onStartTwoPlayerMatch(timeControl, flyingCheckers) },
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
