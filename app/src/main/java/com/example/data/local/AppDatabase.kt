package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
  entities = [
    LessonProgressEntity::class,
    PuzzleProgressEntity::class,
    PlayerStatsEntity::class,
    CustomPuzzleEntity::class,
    CustomLessonEntity::class,
    UpdateSettingsEntity::class,
  ],
  version = 4,
  exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun progressDao(): ProgressDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    private val MIGRATION_1_2 = object : Migration(1, 2) {
      override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
          """
          CREATE TABLE IF NOT EXISTS `custom_puzzles` (
            `id` TEXT NOT NULL,
            `title` TEXT NOT NULL,
            `packTitle` TEXT NOT NULL,
            `puzzleJson` TEXT NOT NULL,
            `addedAt` INTEGER NOT NULL,
            PRIMARY KEY(`id`)
          )
          """.trimIndent()
        )
        db.execSQL(
          """
          CREATE TABLE IF NOT EXISTS `custom_lessons` (
            `id` TEXT NOT NULL,
            `title` TEXT NOT NULL,
            `packTitle` TEXT NOT NULL,
            `lessonJson` TEXT NOT NULL,
            `addedAt` INTEGER NOT NULL,
            PRIMARY KEY(`id`)
          )
          """.trimIndent()
        )
        db.execSQL(
          """
          CREATE TABLE IF NOT EXISTS `update_settings` (
            `id` INTEGER NOT NULL,
            `manifestUrl` TEXT NOT NULL,
            `lastCheckedAt` INTEGER NOT NULL,
            `lastImportedPackName` TEXT NOT NULL,
            PRIMARY KEY(`id`)
          )
          """.trimIndent()
        )
      }
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
      override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
          "ALTER TABLE `update_settings` ADD COLUMN `hintsEnabled` INTEGER NOT NULL DEFAULT 1"
        )
      }
    }

    private val MIGRATION_3_4 = object : Migration(3, 4) {
      override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `player_stats` ADD COLUMN `eloRating` INTEGER NOT NULL DEFAULT 1000")
        db.execSQL("ALTER TABLE `player_stats` ADD COLUMN `bestEloRating` INTEGER NOT NULL DEFAULT 1000")
        db.execSQL("ALTER TABLE `player_stats` ADD COLUMN `puzzleRushBest` INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE `player_stats` ADD COLUMN `dailyPuzzleLastDate` TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE `player_stats` ADD COLUMN `endgamesSolvedCsv` TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE `player_stats` ADD COLUMN `drawsCount` INTEGER NOT NULL DEFAULT 0")
      }
    }

    fun getInstance(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        INSTANCE ?: Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "shashki_kids_db",
        )
          .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
          .build()
          .also { INSTANCE = it }
      }
    }
  }
}
