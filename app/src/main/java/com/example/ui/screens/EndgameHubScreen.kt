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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.checkers.BotDifficulty
import com.example.domain.curriculum.EndgameCatalog
import com.example.ui.components.MascotSpeechCard
import com.example.ui.theme.CrownGold
import com.example.ui.theme.ForestEmerald
import com.example.ui.theme.TerracottaOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EndgameHubScreen(
  solvedEndgameIds: Set<String>,
  onStartEndgame: (scenarioId: String, botDifficulty: BotDifficulty) -> Unit,
  onBack: () -> Unit,
) {
  val scenarios = EndgameCatalog.scenarios
  val solvedCount = remember(solvedEndgameIds) { scenarios.count { it.id in solvedEndgameIds } }

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
            modifier = Modifier.testTag("endgame_back_button"),
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
          }
        },
        title = {
          Text(
            text = "Эндшпиль-спарринг",
            style = MaterialTheme.typography.titleLarge,
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
      LazyColumn(
        modifier = Modifier
          .widthIn(max = 700.dp)
          .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        item(key = "mascot", contentType = "mascot") {
          MascotSpeechCard(
            title = "Отработка окончаний ($solvedCount/${scenarios.size})",
            message = "Здесь нет длинного дебюта: сразу разыграй классический эндшпиль против сильного бота и доведи перевес до победы!",
          )
        }

        items(
          items = scenarios,
          key = { it.id },
          contentType = { "endgame_card" },
        ) { scenario ->
          val solved = scenario.id in solvedEndgameIds
          Card(
            onClick = { onStartEndgame(scenario.id, scenario.recommendedBot) },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("endgame_card_${scenario.id}"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (solved) {
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
              } else {
                MaterialTheme.colorScheme.surface
              },
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)),
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
                  .background(if (solved) ForestEmerald else TerracottaOrange.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center,
              ) {
                Icon(
                  imageVector = if (solved) Icons.Filled.CheckCircle else Icons.Filled.MilitaryTech,
                  contentDescription = null,
                  tint = if (solved) MaterialTheme.colorScheme.onSecondary else TerracottaOrange,
                  modifier = Modifier.size(26.dp),
                )
              }

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "${scenario.order}. ${scenario.title}",
                  style = MaterialTheme.typography.titleMedium,
                )
                Text(
                  text = scenario.subtitle,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                  ) {
                    Text(
                      text = scenario.recommendedBot.title,
                      style = MaterialTheme.typography.labelMedium,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    )
                  }
                  Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                      verticalAlignment = Alignment.CenterVertically,
                    ) {
                      Icon(Icons.Filled.Star, contentDescription = null, tint = CrownGold, modifier = Modifier.size(14.dp))
                      Spacer(modifier = Modifier.width(3.dp))
                      Text(
                        text = "+${scenario.starsReward}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                      )
                    }
                  }
                }
              }

              Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Начать спарринг",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp),
              )
            }
          }
        }
      }
    }
  }
}
