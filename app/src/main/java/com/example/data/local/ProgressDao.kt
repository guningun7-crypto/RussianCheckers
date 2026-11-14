package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {
  @Query("SELECT * FROM lesson_progress")
  fun observeLessonProgress(): Flow<List<LessonProgressEntity>>

  @Query("SELECT * FROM lesson_progress")
  suspend fun getAllLessonProgress(): List<LessonProgressEntity>

  @Query("SELECT * FROM lesson_progress WHERE lessonId = :lessonId")
  suspend fun getLessonProgress(lessonId: String): LessonProgressEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertLessonProgress(entity: LessonProgressEntity)

  @Query("SELECT * FROM puzzle_progress")
  fun observePuzzleProgress(): Flow<List<PuzzleProgressEntity>>

  @Query("SELECT * FROM puzzle_progress")
  suspend fun getAllPuzzleProgress(): List<PuzzleProgressEntity>

  @Query("SELECT * FROM puzzle_progress WHERE puzzleId = :puzzleId")
  suspend fun getPuzzleProgress(puzzleId: String): PuzzleProgressEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertPuzzleProgress(entity: PuzzleProgressEntity)

  @Query("SELECT * FROM player_stats WHERE id = 1")
  fun observePlayerStats(): Flow<PlayerStatsEntity?>

  @Query("SELECT * FROM player_stats WHERE id = 1")
  suspend fun getPlayerStats(): PlayerStatsEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertPlayerStats(stats: PlayerStatsEntity)

  // Custom imported content packs & update settings
  @Query("SELECT * FROM custom_puzzles ORDER BY addedAt ASC")
  fun observeCustomPuzzles(): Flow<List<CustomPuzzleEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertCustomPuzzles(items: List<CustomPuzzleEntity>)

  @Query("DELETE FROM custom_puzzles WHERE id = :puzzleId")
  suspend fun deleteCustomPuzzle(puzzleId: String)

  @Query("SELECT * FROM custom_lessons ORDER BY addedAt ASC")
  fun observeCustomLessons(): Flow<List<CustomLessonEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertCustomLessons(items: List<CustomLessonEntity>)

  @Query("DELETE FROM custom_lessons WHERE id = :lessonId")
  suspend fun deleteCustomLesson(lessonId: String)

  @Query("SELECT * FROM update_settings WHERE id = 1")
  fun observeUpdateSettings(): Flow<UpdateSettingsEntity?>

  @Query("SELECT * FROM update_settings WHERE id = 1")
  suspend fun getUpdateSettings(): UpdateSettingsEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertUpdateSettings(settings: UpdateSettingsEntity)
}
