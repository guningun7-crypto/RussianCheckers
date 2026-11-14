package com.example.domain.checkers

import kotlin.math.pow
import kotlin.math.roundToInt

enum class GameOutcome {
  WIN,
  DRAW,
  LOSS,
}

data class SportsRankInfo(
  val title: String,
  val minElo: Int,
  val nextElo: Int?,
  val badgeEmoji: String,
)

object EloRatingCalculator {
  const val DEFAULT_ELO = 1000
  const val MIN_ELO = 600

  fun botEloFor(difficulty: BotDifficulty): Int = when (difficulty) {
    BotDifficulty.BEGINNER -> 800
    BotDifficulty.LEARNER -> 1050
    BotDifficulty.TACTICIAN -> 1300
    BotDifficulty.GRANDMASTER -> 1600
    BotDifficulty.CHAMPION -> 1900
  }

  /**
   * Calculates new player Elo using FIDE-style K-factor (K = 32) and returns (newElo, delta).
   */
  fun calculateGameDelta(
    playerElo: Int,
    opponentElo: Int,
    outcome: GameOutcome,
    hintsEnabled: Boolean,
  ): Pair<Int, Int> {
    val expected = 1.0 / (1.0 + 10.0.pow((opponentElo - playerElo) / 400.0))
    val actual = when (outcome) {
      GameOutcome.WIN -> 1.0
      GameOutcome.DRAW -> 0.5
      GameOutcome.LOSS -> 0.0
    }
    val kFactor = if (playerElo < 1400) 36.0 else 32.0
    var rawDelta = (kFactor * (actual - expected)).roundToInt()

    // Guarantee positive reward on wins, reduced slightly when playing with hints
    if (outcome == GameOutcome.WIN) {
      if (!hintsEnabled) {
        rawDelta = maxOf(rawDelta, 8)
      } else {
        rawDelta = maxOf((rawDelta * 0.7).roundToInt(), 4)
      }
    } else if (outcome == GameOutcome.LOSS && hintsEnabled) {
      // Gentle penalty if hints were active
      rawDelta = (rawDelta * 0.8).roundToInt()
    }

    val nextElo = (playerElo + rawDelta).coerceAtLeast(MIN_ELO)
    return nextElo to (nextElo - playerElo)
  }

  fun sportsRankForElo(elo: Int): SportsRankInfo = when {
    elo >= 2000 -> SportsRankInfo("Кандидат в мастера (КМС)", 2000, null, "🥇")
    elo >= 1850 -> SportsRankInfo("1-й спортивный разряд", 1850, 2000, "🏆")
    elo >= 1700 -> SportsRankInfo("2-й спортивный разряд", 1700, 1850, "🎖️")
    elo >= 1550 -> SportsRankInfo("3-й спортивный разряд", 1550, 1700, "⭐")
    elo >= 1400 -> SportsRankInfo("1-й юношеский разряд", 1400, 1550, "👑")
    elo >= 1250 -> SportsRankInfo("2-й юношеский разряд", 1250, 1400, "⚡")
    elo >= 1100 -> SportsRankInfo("3-й юношеский разряд", 1100, 1250, "🦉")
    else -> SportsRankInfo("Начинающий шашист", 600, 1100, "🌱")
  }
}
