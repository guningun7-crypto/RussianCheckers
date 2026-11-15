package com.example.ui.viewmodel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.LessonProgressEntity
import com.example.data.local.PlayerStatsEntity
import com.example.data.local.ProgressRepository
import com.example.data.local.PuzzleProgressEntity
import com.example.domain.checkers.BotDifficulty
import com.example.domain.checkers.GameOutcome
import com.example.domain.curriculum.DailyPuzzleHelper
import com.example.domain.curriculum.InteractiveLesson
import com.example.domain.curriculum.LessonCatalog
import com.example.domain.curriculum.LogicPuzzle
import com.example.domain.curriculum.PuzzleCatalog
import com.example.domain.curriculum.PuzzleDifficulty
import com.example.domain.curriculum.RewardBadge
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Immutable
data class AppProgressUiState(
  val lessonProgress: Map<String, LessonProgressEntity> = emptyMap(),
  val puzzleProgress: Map<String, PuzzleProgressEntity> = emptyMap(),
  val stats: PlayerStatsEntity = PlayerStatsEntity(),
  val unlockedBadgeIds: Set<String> = emptySet(),
  val endgamesSolvedIds: Set<String> = emptySet(),
  val allLessons: List<InteractiveLesson> = LessonCatalog.lessons,
  val allPuzzles: List<LogicPuzzle> = PuzzleCatalog.puzzles,
  val customLessonIds: Set<String> = emptySet(),
  val customPuzzleIds: Set<String> = emptySet(),
  val hintsEnabled: Boolean = true,
  val todayDateKey: String = DailyPuzzleHelper.todayDateKey(),
  val dailyPuzzleSolvedToday: Boolean = false,
)

class AppProgressViewModel(
  private val repository: ProgressRepository,
) : ViewModel() {

  val uiState: StateFlow<AppProgressUiState> = combine(
    combine(
      repository.lessonProgressMap,
      repository.puzzleProgressMap,
      repository.playerStats,
    ) { l, p, s -> Triple(l, p, s) },
    repository.customLessons,
    repository.customPuzzles,
    repository.updateSettings,
  ) { base, customLessons, customPuzzles, settings ->
    val (lessons, puzzles, stats) = base
    val badges = stats.unlockedBadgesCsv
      .split(",")
      .filter { it.isNotBlank() }
      .toSet()
    val endgames = stats.endgamesSolvedCsv
      .split(",")
      .filter { it.isNotBlank() }
      .toSet()
    val todayKey = DailyPuzzleHelper.todayDateKey()
    AppProgressUiState(
      lessonProgress = lessons,
      puzzleProgress = puzzles,
      stats = stats,
      unlockedBadgeIds = badges,
      endgamesSolvedIds = endgames,
      allLessons = LessonCatalog.lessons + customLessons,
      allPuzzles = PuzzleCatalog.puzzles + customPuzzles,
      customLessonIds = customLessons.map { it.id }.toSet(),
      customPuzzleIds = customPuzzles.map { it.id }.toSet(),
      hintsEnabled = settings.hintsEnabled,
      todayDateKey = todayKey,
      dailyPuzzleSolvedToday = stats.dailyPuzzleLastDate == todayKey,
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000L),
    initialValue = AppProgressUiState(),
  )

  private val _pendingBadges = MutableStateFlow<List<RewardBadge>>(emptyList())
  val pendingBadges: StateFlow<List<RewardBadge>> = _pendingBadges.asStateFlow()

  private val _lastGameEloDelta = MutableStateFlow<Int?>(null)
  val lastGameEloDelta: StateFlow<Int?> = _lastGameEloDelta.asStateFlow()

  fun clearLastGameEloDelta() {
    _lastGameEloDelta.value = null
  }

  fun onLessonCompleted(lessonId: String, starsAwarded: Int) {
    viewModelScope.launch {
      val newlyUnlocked = repository.recordLessonCompleted(lessonId, starsAwarded)
      if (newlyUnlocked.isNotEmpty()) {
        _pendingBadges.value = _pendingBadges.value + newlyUnlocked
      }
    }
  }

  fun onPuzzleSolved(
    puzzleId: String,
    starsAwarded: Int,
    usedHint: Boolean,
    difficulty: PuzzleDifficulty? = null,
    dailyDateKey: String? = null,
  ) {
    viewModelScope.launch {
      val newlyUnlocked = repository.recordPuzzleSolved(
        puzzleId = puzzleId,
        starsAwarded = starsAwarded,
        usedHint = usedHint,
        puzzleDifficulty = difficulty,
        dailyDateKey = dailyDateKey,
      )
      if (newlyUnlocked.isNotEmpty()) {
        _pendingBadges.value = _pendingBadges.value + newlyUnlocked
      }
    }
  }

  fun onGameFinished(
    outcome: GameOutcome,
    difficulty: BotDifficulty?,
    starsAwarded: Int,
    endgameScenarioId: String? = null,
  ) {
    viewModelScope.launch {
      val hintsOn = uiState.value.hintsEnabled
      val (newlyUnlocked, eloDelta) = repository.recordGameResult(
        outcome = outcome,
        difficulty = difficulty,
        starsAwarded = starsAwarded,
        hintsEnabled = hintsOn,
        endgameScenarioId = endgameScenarioId,
      )
      _lastGameEloDelta.value = if (difficulty != null) eloDelta else null
      if (newlyUnlocked.isNotEmpty()) {
        _pendingBadges.value = _pendingBadges.value + newlyUnlocked
      }
    }
  }

  fun onPuzzleRushFinished(score: Int) {
    viewModelScope.launch {
      val newlyUnlocked = repository.recordPuzzleRushScore(score)
      if (newlyUnlocked.isNotEmpty()) {
        _pendingBadges.value = _pendingBadges.value + newlyUnlocked
      }
    }
  }

  fun dismissCurrentBadge() {
    val current = _pendingBadges.value
    if (current.isNotEmpty()) {
      _pendingBadges.value = current.drop(1)
    }
  }

  fun setHintsEnabled(enabled: Boolean) {
    viewModelScope.launch {
      repository.setHintsEnabled(enabled)
    }
  }

  class Factory(private val repository: ProgressRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return AppProgressViewModel(repository) as T
    }
  }
}

