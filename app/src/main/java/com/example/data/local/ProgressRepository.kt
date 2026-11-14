package com.example.data.local

import com.example.domain.checkers.BotDifficulty
import com.example.domain.curriculum.ContentPackParser
import com.example.domain.curriculum.InteractiveLesson
import com.example.domain.curriculum.LessonCatalog
import com.example.domain.curriculum.LogicPuzzle
import com.example.domain.curriculum.ParsedContentPack
import com.example.domain.curriculum.PuzzleCatalog
import com.example.domain.curriculum.RewardBadge
import com.example.domain.curriculum.RewardCatalog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProgressRepository(private val dao: ProgressDao) {

  val lessonProgressMap: Flow<Map<String, LessonProgressEntity>> =
    dao.observeLessonProgress().map { list -> list.associateBy { it.lessonId } }

  val puzzleProgressMap: Flow<Map<String, PuzzleProgressEntity>> =
    dao.observePuzzleProgress().map { list -> list.associateBy { it.puzzleId } }

  val playerStats: Flow<PlayerStatsEntity> =
    dao.observePlayerStats().map { it ?: PlayerStatsEntity() }

  val customPuzzles: Flow<List<LogicPuzzle>> =
    dao.observeCustomPuzzles().map { entities ->
      val baseOrder = PuzzleCatalog.puzzles.size
      entities.mapIndexedNotNull { idx, entity ->
        ContentPackParser.parseSinglePuzzle(entity.puzzleJson)?.copy(order = baseOrder + idx + 1)
      }
    }

  val customLessons: Flow<List<InteractiveLesson>> =
    dao.observeCustomLessons().map { entities ->
      val baseOrder = LessonCatalog.lessons.size
      entities.mapIndexedNotNull { idx, entity ->
        ContentPackParser.parseSingleLesson(entity.lessonJson)?.copy(order = baseOrder + idx + 1)
      }
    }

  val updateSettings: Flow<UpdateSettingsEntity> =
    dao.observeUpdateSettings().map { it ?: UpdateSettingsEntity() }

  suspend fun saveCustomPack(pack: ParsedContentPack) {
    if (pack.puzzles.isNotEmpty()) {
      val entities = pack.puzzles.map { puzzle ->
        CustomPuzzleEntity(
          id = puzzle.id,
          title = puzzle.title,
          packTitle = pack.packTitle,
          puzzleJson = ContentPackParser.serializeSinglePuzzle(puzzle),
        )
      }
      dao.upsertCustomPuzzles(entities)
    }
    if (pack.lessons.isNotEmpty()) {
      val entities = pack.lessons.map { lesson ->
        CustomLessonEntity(
          id = lesson.id,
          title = lesson.title,
          packTitle = pack.packTitle,
          lessonJson = ContentPackParser.serializeSingleLesson(lesson),
        )
      }
      dao.upsertCustomLessons(entities)
    }
    val current = dao.getUpdateSettings() ?: UpdateSettingsEntity()
    dao.upsertUpdateSettings(
      current.copy(
        lastCheckedAt = System.currentTimeMillis(),
        lastImportedPackName = pack.packTitle,
      )
    )
  }

  suspend fun saveSingleCustomPuzzle(puzzle: LogicPuzzle) {
    dao.upsertCustomPuzzles(
      listOf(
        CustomPuzzleEntity(
          id = puzzle.id,
          title = puzzle.title,
          packTitle = "Создано в приложении",
          puzzleJson = ContentPackParser.serializeSinglePuzzle(puzzle),
        )
      )
    )
  }

  suspend fun deleteCustomPuzzle(puzzleId: String) {
    dao.deleteCustomPuzzle(puzzleId)
  }

  suspend fun deleteCustomLesson(lessonId: String) {
    dao.deleteCustomLesson(lessonId)
  }

  suspend fun saveManifestUrl(url: String) {
    val current = dao.getUpdateSettings() ?: UpdateSettingsEntity()
    dao.upsertUpdateSettings(
      current.copy(
        manifestUrl = url.trim(),
        lastCheckedAt = System.currentTimeMillis(),
      )
    )
  }

  suspend fun setHintsEnabled(enabled: Boolean) {
    val current = dao.getUpdateSettings() ?: UpdateSettingsEntity()
    dao.upsertUpdateSettings(current.copy(hintsEnabled = enabled))
  }

  suspend fun recordLessonCompleted(lessonId: String, starsAwarded: Int): List<RewardBadge> {
    val existing = dao.getLessonProgress(lessonId)
    val bestStars = maxOf(existing?.starsEarned ?: 0, starsAwarded)
    val deltaStars = (bestStars - (existing?.starsEarned ?: 0)).coerceAtLeast(0)

    dao.upsertLessonProgress(
      LessonProgressEntity(
        lessonId = lessonId,
        completed = true,
        starsEarned = bestStars,
        attempts = (existing?.attempts ?: 0) + 1,
      )
    )

    return updateStatsAndEvaluateBadges(
      addedStars = deltaStars,
      incrementStreak = existing?.completed != true,
    )
  }

  suspend fun recordPuzzleSolved(puzzleId: String, starsAwarded: Int, usedHint: Boolean): List<RewardBadge> {
    val existing = dao.getPuzzleProgress(puzzleId)
    val bestStars = maxOf(existing?.starsEarned ?: 0, starsAwarded)
    val deltaStars = (bestStars - (existing?.starsEarned ?: 0)).coerceAtLeast(0)

    dao.upsertPuzzleProgress(
      PuzzleProgressEntity(
        puzzleId = puzzleId,
        solved = true,
        starsEarned = bestStars,
        hintsUsed = (existing?.hintsUsed ?: 0) + if (usedHint) 1 else 0,
      )
    )

    return updateStatsAndEvaluateBadges(
      addedStars = deltaStars,
      incrementStreak = existing?.solved != true,
    )
  }

  suspend fun recordGameResult(
    won: Boolean,
    difficulty: BotDifficulty?,
    starsAwarded: Int,
  ): List<RewardBadge> {
    return updateStatsAndEvaluateBadges(
      addedStars = if (won) starsAwarded else 0,
      incrementGamesPlayed = true,
      incrementGamesWon = won,
      difficultyWon = if (won) difficulty?.level ?: 0 else 0,
      incrementStreak = won,
      resetStreak = !won,
    )
  }

  private suspend fun updateStatsAndEvaluateBadges(
    addedStars: Int = 0,
    incrementGamesPlayed: Boolean = false,
    incrementGamesWon: Boolean = false,
    difficultyWon: Int = 0,
    incrementStreak: Boolean = false,
    resetStreak: Boolean = false,
  ): List<RewardBadge> {
    val current = dao.getPlayerStats() ?: PlayerStatsEntity()
    val lessons = dao.getAllLessonProgress()
    val puzzles = dao.getAllPuzzleProgress()

    val nextStars = current.totalStars + addedStars
    val nextGamesWon = current.gamesWon + if (incrementGamesWon) 1 else 0
    val nextGamesPlayed = current.gamesPlayed + if (incrementGamesPlayed) 1 else 0
    val nextHighestDiff = maxOf(current.highestDifficultyWon, difficultyWon)
    val nextStreak = when {
      resetStreak -> 0
      incrementStreak -> current.currentStreak + 1
      else -> current.currentStreak
    }

    val existingBadges = current.unlockedBadgesCsv
      .split(",")
      .filter { it.isNotBlank() }
      .toMutableSet()

    val completedLessonsCount = lessons.count { it.completed }
    val solvedPuzzlesCount = puzzles.count { it.solved }

    val newlyUnlockedIds = mutableListOf<String>()
    fun unlockIf(id: String, condition: Boolean) {
      if (condition && existingBadges.add(id)) {
        newlyUnlockedIds.add(id)
      }
    }

    unlockIf("badge_first_lesson", completedLessonsCount >= 1)
    unlockIf("badge_rule_master", completedLessonsCount >= 4)
    unlockIf("badge_graduate", completedLessonsCount >= 8)
    unlockIf("badge_tournament_ready", completedLessonsCount >= LessonCatalog.lessons.size)
    unlockIf("badge_first_puzzle", solvedPuzzlesCount >= 1)
    unlockIf("badge_puzzle_pro", solvedPuzzlesCount >= 5)
    unlockIf("badge_combination_king", solvedPuzzlesCount >= PuzzleCatalog.puzzles.size)
    unlockIf("badge_first_win", nextGamesWon >= 1)
    unlockIf("badge_tactician", nextHighestDiff >= BotDifficulty.TACTICIAN.level)
    unlockIf("badge_star_collector", nextStars >= 25)

    dao.upsertPlayerStats(
      current.copy(
        totalStars = nextStars,
        gamesWon = nextGamesWon,
        gamesPlayed = nextGamesPlayed,
        highestDifficultyWon = nextHighestDiff,
        currentStreak = nextStreak,
        unlockedBadgesCsv = existingBadges.joinToString(","),
      )
    )

    return newlyUnlockedIds.mapNotNull { RewardCatalog.findBadge(it) }
  }
}
