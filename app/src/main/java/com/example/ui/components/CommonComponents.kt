package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.curriculum.RewardBadge
import com.example.ui.theme.CrownGold

@Composable
fun MascotSpeechCard(
  message: String,
  title: String = "Совёнок Шашик",
  modifier: Modifier = Modifier,
  containerColor: Color = MaterialTheme.colorScheme.surface,
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = containerColor),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Image(
        painter = painterResource(id = R.drawable.img_mascot_shashik),
        contentDescription = stringResource(id = R.string.mascot_desc),
        modifier = Modifier
          .size(60.dp)
          .clip(CircleShape)
          .border(2.dp, MaterialTheme.colorScheme.tertiary, CircleShape),
        contentScale = ContentScale.Crop,
      )
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
          text = message,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurface,
        )
      }
    }
  }
}

@Composable
fun StarCounterPill(
  stars: Int,
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(50),
    color = MaterialTheme.colorScheme.tertiaryContainer,
    tonalElevation = 1.dp,
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
      Icon(
        imageVector = Icons.Filled.Star,
        contentDescription = "Звёзды",
        tint = CrownGold,
        modifier = Modifier.size(18.dp),
      )
      Text(
        text = "$stars",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onTertiaryContainer,
        fontWeight = FontWeight.Bold,
      )
    }
  }
}

@Composable
fun HintsToggleChip(
  hintsEnabled: Boolean,
  onToggle: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
) {
  FilterChip(
    selected = !hintsEnabled,
    onClick = { onToggle(!hintsEnabled) },
    leadingIcon = {
      Icon(
        imageVector = if (hintsEnabled) Icons.Filled.Lightbulb else Icons.Filled.VisibilityOff,
        contentDescription = null,
        tint = if (hintsEnabled) CrownGold else MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier.size(18.dp),
      )
    },
    label = {
      Text(
        text = if (hintsEnabled) "Подсказки" else "Без подсказок",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
      )
    },
    colors = FilterChipDefaults.filterChipColors(
      selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
      selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ),
    modifier = modifier
      .minimumInteractiveComponentSize()
      .testTag("hints_toggle_chip"),
  )
}

@Composable
fun HintsModeCard(
  hintsEnabled: Boolean,
  onToggle: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (hintsEnabled) {
        MaterialTheme.colorScheme.surface
      } else {
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
      },
    ),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Icon(
        imageVector = if (hintsEnabled) Icons.Filled.Lightbulb else Icons.Filled.VisibilityOff,
        contentDescription = null,
        tint = if (hintsEnabled) CrownGold else MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(26.dp),
      )
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = if (hintsEnabled) "Подсказки включены" else "Режим «Без подсказок»",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
        )
        Text(
          text = if (hintsEnabled) {
            "Показываются точки ходов, обязательные взятия и кнопка подсказки"
          } else {
            "Скрыты все подсказки, точки ходов и оранжевые кольца — полная проверка себя"
          },
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
      Switch(
        checked = !hintsEnabled,
        onCheckedChange = { noHints -> onToggle(!noHints) },
        modifier = Modifier.testTag("hints_mode_switch"),
      )
    }
  }
}

@Composable
fun BadgeCelebrationDialog(
  badge: RewardBadge,
  onDismiss: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      Button(
        onClick = onDismiss,
        modifier = Modifier.testTag("badge_dialog_ok_button"),
      ) {
        Text("Ура, спасибо!")
      }
    },
    icon = {
      Box(
        modifier = Modifier
          .size(72.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.tertiaryContainer),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = badgeIconFor(badge.iconName),
          contentDescription = badge.title,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(40.dp),
        )
      }
    },
    title = {
      Text(
        text = "Новая награда!",
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.headlineSmall,
      )
    },
    text = {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Text(
          text = badge.title,
          style = MaterialTheme.typography.titleLarge,
          color = MaterialTheme.colorScheme.primary,
          textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = badge.description,
          style = MaterialTheme.typography.bodyMedium,
          textAlign = TextAlign.Center,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    },
  )
}

fun badgeIconFor(name: String): ImageVector = when (name) {
  "school" -> Icons.Filled.School
  "menu_book" -> Icons.AutoMirrored.Filled.MenuBook
  "workspace_premium" -> Icons.Filled.WorkspacePremium
  "extension" -> Icons.Filled.Extension
  "psychology" -> Icons.Filled.Psychology
  "emoji_events" -> Icons.Filled.EmojiEvents
  "military_tech" -> Icons.Filled.MilitaryTech
  "star" -> Icons.Filled.Star
  else -> Icons.Filled.EmojiEvents
}
