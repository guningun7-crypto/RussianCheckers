package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lesson_progress")
data class LessonProgressEntity(
  @PrimaryKey val lessonId: String,
  val completed: Boolean = false,
  val starsEarned: Int = 0,
  val attempts: Int = 0,
  val updatedAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "puzzle_progress")
data class PuzzleProgressEntity(
  @PrimaryKey val puzzleId: String,
  val solved: Boolean = false,
  val starsEarned: Int = 0,
  val hintsUsed: Int = 0,
  val updatedAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "player_stats")
data class PlayerStatsEntity(
  @PrimaryKey val id: Int = 1,
  val totalStars: Int = 0,
  val gamesWon: Int = 0,
  val gamesPlayed: Int = 0,
  val highestDifficultyWon: Int = 0,
  val currentStreak: Int = 0,
  val unlockedBadgesCsv: String = "",
  val eloRating: Int = 1000,
  val bestEloRating: Int = 1000,
  val puzzleRushBest: Int = 0,
  val dailyPuzzleLastDate: String = "",
  val endgamesSolvedCsv: String = "",
  val drawsCount: Int = 0,
)

@Entity(tableName = "custom_puzzles")
data class CustomPuzzleEntity(
  @PrimaryKey val id: String,
  val title: String,
  val packTitle: String,
  val puzzleJson: String,
  val addedAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "custom_lessons")
data class CustomLessonEntity(
  @PrimaryKey val id: String,
  val title: String,
  val packTitle: String,
  val lessonJson: String,
  val addedAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "update_settings")
data class UpdateSettingsEntity(
  @PrimaryKey val id: Int = 1,
  val manifestUrl: String = "",
  val lastCheckedAt: Long = 0L,
  val lastImportedPackName: String = "",
  val hintsEnabled: Boolean = true,
)
