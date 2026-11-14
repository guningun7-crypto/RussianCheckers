package com.example.domain.curriculum

data class PlayerRank(
  val title: String,
  val minStars: Int,
  val nextRankStars: Int?,
  val emoji: String,
)

data class RewardBadge(
  val id: String,
  val title: String,
  val description: String,
  val iconName: String,
)

object RewardCatalog {
  val ranks = listOf(
    PlayerRank("Любопытный новичок", 0, 6, "🌱"),
    PlayerRank("Юный исследователь", 6, 15, "🦉"),
    PlayerRank("Смелый тактик", 15, 25, "⚡"),
    PlayerRank("Хранитель дамок", 25, 36, "👑"),
    PlayerRank("Великий Мастер Шашек", 36, 50, "🏆"),
    PlayerRank("Кандидат в разрядники", 50, null, "🥇"),
  )

  fun rankForStars(stars: Int): PlayerRank {
    return ranks.lastOrNull { stars >= it.minStars } ?: ranks.first()
  }

  val badges: List<RewardBadge> = listOf(
    RewardBadge(
      id = "badge_first_lesson",
      title = "Первый шаг",
      description = "Пройди свой первый интерактивный урок",
      iconName = "school",
    ),
    RewardBadge(
      id = "badge_rule_master",
      title = "Знаток правил",
      description = "Пройди 4 урока по правилам русских шашек",
      iconName = "menu_book",
    ),
    RewardBadge(
      id = "badge_graduate",
      title = "Выпускник Академии",
      description = "Заверши 8 базовых уроков от Совёнка Шашика",
      iconName = "workspace_premium",
    ),
    RewardBadge(
      id = "badge_tournament_ready",
      title = "Готов к турниру",
      description = "Пройди все 12 уроков вместе со школой разрядника",
      iconName = "military_tech",
    ),
    RewardBadge(
      id = "badge_first_puzzle",
      title = "Сыщик логики",
      description = "Реши свою первую логическую задачу",
      iconName = "extension",
    ),
    RewardBadge(
      id = "badge_puzzle_pro",
      title = "Мастер комбинаций",
      description = "Реши 5 логических задач",
      iconName = "psychology",
    ),
    RewardBadge(
      id = "badge_combination_king",
      title = "Охотник за разрядом",
      description = "Реши все 20 задач, включая мастерские комбинации",
      iconName = "workspace_premium",
    ),
    RewardBadge(
      id = "badge_first_win",
      title = "Первая победа",
      description = "Обыграй Совёнка в настоящей партии",
      iconName = "emoji_events",
    ),
    RewardBadge(
      id = "badge_tactician",
      title = "Юный стратег",
      description = "Победи на уровне «Хитрый Тактик» или выше",
      iconName = "military_tech",
    ),
    RewardBadge(
      id = "badge_star_collector",
      title = "Звёздный чемпион",
      description = "Собери 25 золотых звёзд",
      iconName = "star",
    ),
  )

  fun findBadge(id: String): RewardBadge? = badges.find { it.id == id }
}
