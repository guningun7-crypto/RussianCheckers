package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.local.ProgressRepository
import com.example.data.remote.UpdateRepository
import com.example.domain.checkers.CheckersBoard
import com.example.domain.checkers.Piece
import com.example.domain.checkers.PieceColor
import com.example.domain.checkers.RussianCheckersEngine
import com.example.domain.checkers.Square
import com.example.domain.curriculum.AppReleaseInfo
import com.example.domain.curriculum.ContentPackParser
import com.example.domain.curriculum.InteractiveLesson
import com.example.domain.curriculum.LogicPuzzle
import com.example.domain.curriculum.PuzzleDifficulty
import com.example.domain.curriculum.PuzzleStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UpdateActionStatus {
  data object Idle : UpdateActionStatus
  data object Loading : UpdateActionStatus
  data class Success(val message: String) : UpdateActionStatus
  data class Error(val message: String) : UpdateActionStatus
}

data class UpdatesUiState(
  val currentVersionName: String = BuildConfig.VERSION_NAME,
  val currentVersionCode: Int = BuildConfig.VERSION_CODE,
  val manifestUrl: String = "",
  val lastImportedPackName: String = "",
  val availableRelease: AppReleaseInfo? = null,
  val customPuzzles: List<LogicPuzzle> = emptyList(),
  val customLessons: List<InteractiveLesson> = emptyList(),
  val status: UpdateActionStatus = UpdateActionStatus.Idle,
)

class UpdatesViewModel(
  private val progressRepository: ProgressRepository,
  private val updateRepository: UpdateRepository = UpdateRepository(),
) : ViewModel() {

  private val _status = MutableStateFlow<UpdateActionStatus>(UpdateActionStatus.Idle)
  private val _availableRelease = MutableStateFlow<AppReleaseInfo?>(null)

  val uiState: StateFlow<UpdatesUiState> = combine(
    progressRepository.updateSettings,
    progressRepository.customPuzzles,
    progressRepository.customLessons,
    _status,
    _availableRelease,
  ) { settings, puzzles, lessons, status, release ->
    UpdatesUiState(
      manifestUrl = settings.manifestUrl,
      lastImportedPackName = settings.lastImportedPackName,
      availableRelease = release,
      customPuzzles = puzzles,
      customLessons = lessons,
      status = status,
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000L),
    initialValue = UpdatesUiState(),
  )

  fun checkUrlUpdate(url: String) {
    val trimmed = url.trim()
    if (trimmed.isBlank()) {
      _status.value = UpdateActionStatus.Error("Введите HTTPS-ссылку на файл обновлений (.json).")
      return
    }
    viewModelScope.launch {
      _status.value = UpdateActionStatus.Loading
      progressRepository.saveManifestUrl(trimmed)
      val result = updateRepository.fetchContentPack(trimmed)
      result.fold(
        onSuccess = { pack ->
          applyParsedPack(pack)
        },
        onFailure = { error ->
          _status.value = UpdateActionStatus.Error(error.message ?: "Ошибка загрузки обновления.")
        },
      )
    }
  }

  fun importFromRawJson(jsonText: String) {
    if (jsonText.isBlank()) {
      _status.value = UpdateActionStatus.Error("Вставьте или выберите содержимое JSON.")
      return
    }
    viewModelScope.launch {
      _status.value = UpdateActionStatus.Loading
      val result = ContentPackParser.parse(jsonText)
      result.fold(
        onSuccess = { pack ->
          applyParsedPack(pack)
        },
        onFailure = { error ->
          _status.value = UpdateActionStatus.Error(error.message ?: "Не удалось прочитать пакет.")
        },
      )
    }
  }

  fun loadTournamentBonusPack() {
    importFromRawJson(ContentPackParser.tournamentBonusPackJson())
  }

  private suspend fun applyParsedPack(pack: com.example.domain.curriculum.ParsedContentPack) {
    if (pack.puzzles.isNotEmpty() || pack.lessons.isNotEmpty()) {
      progressRepository.saveCustomPack(pack)
    }
    val release = pack.releaseInfo
    val hasNewApk = release != null && release.versionCode > BuildConfig.VERSION_CODE
    _availableRelease.value = if (hasNewApk) release else null

    val parts = mutableListOf<String>()
    if (pack.puzzles.isNotEmpty()) parts.add("новых задач: ${pack.puzzles.size}")
    if (pack.lessons.isNotEmpty()) parts.add("новых уроков: ${pack.lessons.size}")
    if (hasNewApk) {
      parts.add("доступна новая версия программы v${release.versionName}")
    } else if (release != null && parts.isEmpty()) {
      parts.add("у вас установлена актуальная версия v${BuildConfig.VERSION_NAME}")
    }

    val summary = if (parts.isEmpty()) {
      "Обновление проверено."
    } else {
      "Готово (${pack.packTitle}): ${parts.joinToString(", ")}."
    }
    _status.value = UpdateActionStatus.Success(summary)
  }

  fun createQuickCustomPuzzle(
    title: String,
    themeTag: String,
    difficulty: PuzzleDifficulty,
    prompt: String,
    board: CheckersBoard,
    solutionNotation: String,
    hintText: String,
  ) {
    viewModelScope.launch {
      val cleanNotation = ContentPackParser.normalizeNotation(solutionNotation)
      val legal = RussianCheckersEngine.getLegalMoves(board, PieceColor.WHITE)
      val match = legal.firstOrNull { it.notationString == cleanNotation }
      if (match == null) {
        _status.value = UpdateActionStatus.Error(
          "Ход «$solutionNotation» невозможен на этой доске. Доступные ходы: ${legal.joinToString { it.notationString }.ifBlank { "нет ходов" }}"
        )
        return@launch
      }

      val rows = (0..7).map { r ->
        (0..7).joinToString("") { c ->
          when (val p = board[Square(r, c)]) {
            null -> "."
            Piece(PieceColor.WHITE, false) -> "w"
            Piece(PieceColor.WHITE, true) -> "W"
            Piece(PieceColor.BLACK, false) -> "b"
            Piece(PieceColor.BLACK, true) -> "B"
            else -> "."
          }
        }
      }

      val puzzle = LogicPuzzle(
        id = "parent_puzzle_${System.currentTimeMillis()}",
        order = 999,
        title = title.trim().ifBlank { "Своя задача" },
        themeTag = themeTag.trim().ifBlank { "Семейная" },
        difficulty = difficulty,
        prompt = prompt.trim().ifBlank { "Найди лучший ход белых!" },
        initialRows = rows,
        steps = listOf(
          PuzzleStep(
            acceptedNotations = setOf(cleanNotation),
            hintMove = match,
            hintText = hintText.trim().ifBlank { "Попробуй ход $cleanNotation!" },
          )
        ),
        successExplanation = "Отлично! Семейная задача успешно решена!",
      )
      progressRepository.saveSingleCustomPuzzle(puzzle)
      _status.value = UpdateActionStatus.Success("Задача «${puzzle.title}» добавлена в раздел «Задачи»!")
    }
  }

  fun deleteCustomPuzzle(id: String) {
    viewModelScope.launch {
      progressRepository.deleteCustomPuzzle(id)
    }
  }

  fun deleteCustomLesson(id: String) {
    viewModelScope.launch {
      progressRepository.deleteCustomLesson(id)
    }
  }

  fun clearStatus() {
    _status.value = UpdateActionStatus.Idle
  }

  class Factory(private val repository: ProgressRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return UpdatesViewModel(repository) as T
    }
  }
}
