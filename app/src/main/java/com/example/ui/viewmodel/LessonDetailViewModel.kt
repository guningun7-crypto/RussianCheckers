package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.checkers.CheckersBoard
import com.example.domain.checkers.CheckersMove
import com.example.domain.checkers.PieceColor
import com.example.domain.checkers.RussianCheckersEngine
import com.example.domain.checkers.Square
import com.example.domain.curriculum.InteractiveLesson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class LessonTab {
  RULE_AND_DEMO,
  EXERCISE,
}

sealed interface ExerciseFeedback {
  data class Success(val message: String, val starsEarned: Int) : ExerciseFeedback
  data class TryAgain(val message: String) : ExerciseFeedback
}

data class LessonUiState(
  val lesson: InteractiveLesson,
  val activeTab: LessonTab = LessonTab.RULE_AND_DEMO,
  // Demo state
  val demoBoard: CheckersBoard = lesson.demoBoard(),
  val demoStepIndex: Int = -1,
  val isDemoPlaying: Boolean = false,
  val demoCaption: String = "Нажми «Показать», чтобы увидеть движение шашек на доске!",
  val demoHighlights: Set<Square> = emptySet(),
  val demoLastMove: CheckersMove? = null,
  val demoHintArrow: CheckersMove? = null,
  // Exercise state
  val exerciseBoard: CheckersBoard = lesson.exerciseBoard(),
  val selectedSquare: Square? = null,
  val legalMoves: List<CheckersMove> = RussianCheckersEngine.getLegalMoves(lesson.exerciseBoard(), lesson.playerColor),
  val targetSquares: Set<Square> = emptySet(),
  val mandatorySources: Set<Square> = emptySet(),
  val pendingChainStepIndex: Int = 0,
  val activeChainMoves: List<CheckersMove> = emptyList(),
  val animatedHint: CheckersMove? = null,
  val hintMessage: String? = null,
  val feedback: ExerciseFeedback? = null,
  val isSolved: Boolean = false,
)

class LessonDetailViewModel(
  private val lesson: InteractiveLesson,
  private val onLessonSolvedCallback: (String, Int) -> Unit,
) : ViewModel() {

  private val _uiState = MutableStateFlow(createInitialState(lesson))
  val uiState: StateFlow<LessonUiState> = _uiState.asStateFlow()

  private var demoJob: Job? = null

  init {
    startDemoPlayback()
  }

  private fun createInitialState(lesson: InteractiveLesson): LessonUiState {
    val exBoard = lesson.exerciseBoard()
    val legal = RussianCheckersEngine.getLegalMoves(exBoard, lesson.playerColor)
    val mandatory = if (legal.any { it.isCapture }) legal.map { it.from }.toSet() else emptySet()
    return LessonUiState(
      lesson = lesson,
      demoBoard = lesson.demoBoard(),
      exerciseBoard = exBoard,
      legalMoves = legal,
      mandatorySources = mandatory,
    )
  }

  fun selectTab(tab: LessonTab) {
    if (tab == LessonTab.EXERCISE) {
      demoJob?.cancel()
      _uiState.update { it.copy(activeTab = tab, isDemoPlaying = false) }
    } else {
      _uiState.update { it.copy(activeTab = tab) }
      if (_uiState.value.demoStepIndex < 0) {
        startDemoPlayback()
      }
    }
  }

  fun toggleDemoPlay() {
    if (_uiState.value.isDemoPlaying) {
      demoJob?.cancel()
      _uiState.update { it.copy(isDemoPlaying = false) }
    } else {
      startDemoPlayback()
    }
  }

  fun replayDemo() {
    demoJob?.cancel()
    _uiState.update {
      it.copy(
        demoBoard = lesson.demoBoard(),
        demoStepIndex = -1,
        demoHighlights = emptySet(),
        demoLastMove = null,
        demoHintArrow = null,
        demoCaption = "Начинаем показ заново...",
      )
    }
    startDemoPlayback()
  }

  fun stepDemoForward() {
    demoJob?.cancel()
    _uiState.update { it.copy(isDemoPlaying = false) }
    advanceOneDemoStep()
  }

  private fun startDemoPlayback() {
    demoJob?.cancel()
    demoJob = viewModelScope.launch {
      if (_uiState.value.demoStepIndex >= lesson.demoSteps.lastIndex) {
        _uiState.update {
          it.copy(
            demoBoard = lesson.demoBoard(),
            demoStepIndex = -1,
            demoHighlights = emptySet(),
            demoLastMove = null,
            demoHintArrow = null,
          )
        }
      }
      _uiState.update { it.copy(isDemoPlaying = true) }
      while (_uiState.value.demoStepIndex < lesson.demoSteps.lastIndex) {
        delay(650L)
        val nextIdx = _uiState.value.demoStepIndex + 1
        val step = lesson.demoSteps[nextIdx]
        // First show hint arrow & caption before applying the move
        if (step.move != null) {
          _uiState.update {
            it.copy(
              demoStepIndex = nextIdx,
              demoCaption = step.caption,
              demoHighlights = step.highlightSquares.toSet(),
              demoHintArrow = step.move,
            )
          }
          delay(1250L)
          _uiState.update {
            it.copy(
              demoBoard = RussianCheckersEngine.applyMove(it.demoBoard, step.move),
              demoLastMove = step.move,
              demoHintArrow = null,
            )
          }
          delay(900L)
        } else {
          _uiState.update {
            it.copy(
              demoStepIndex = nextIdx,
              demoCaption = step.caption,
              demoHighlights = step.highlightSquares.toSet(),
              demoHintArrow = null,
            )
          }
          delay(1600L)
        }
      }
      _uiState.update { it.copy(isDemoPlaying = false) }
    }
  }

  private fun advanceOneDemoStep() {
    val state = _uiState.value
    val nextIdx = state.demoStepIndex + 1
    if (nextIdx > lesson.demoSteps.lastIndex) {
      replayDemo()
      return
    }
    val step = lesson.demoSteps[nextIdx]
    val nextBoard = if (step.move != null) {
      RussianCheckersEngine.applyMove(state.demoBoard, step.move)
    } else {
      state.demoBoard
    }
    _uiState.update {
      it.copy(
        demoStepIndex = nextIdx,
        demoBoard = nextBoard,
        demoCaption = step.caption,
        demoHighlights = step.highlightSquares.toSet(),
        demoLastMove = step.move ?: it.demoLastMove,
        demoHintArrow = null,
      )
    }
  }

  fun onExerciseSquareClick(square: Square, hintsEnabled: Boolean = true) {
    val state = _uiState.value
    if (state.isSolved) return

    // If an intermediate multi-jump chain is in progress
    if (state.activeChainMoves.isNotEmpty()) {
      val stepIdx = state.pendingChainStepIndex
      val matchingNext = state.activeChainMoves.filter { it.steps.getOrNull(stepIdx)?.to == square }
      if (matchingNext.isNotEmpty()) {
        val completedMove = matchingNext.firstOrNull { it.steps.size == stepIdx + 1 }
        if (completedMove != null) {
          commitExerciseMove(completedMove)
        } else {
          val step = matchingNext.first().steps[stepIdx]
          val updatedBoard = RussianCheckersEngine.applySingleStep(state.exerciseBoard, step)
          val nextTargets = matchingNext.mapNotNull { it.steps.getOrNull(stepIdx + 1)?.to }.toSet()
          _uiState.update {
            it.copy(
              exerciseBoard = updatedBoard,
              selectedSquare = square,
              targetSquares = nextTargets,
              pendingChainStepIndex = stepIdx + 1,
              activeChainMoves = matchingNext,
              feedback = null,
            )
          }
        }
        return
      }
      // Allow tapping final destination directly even mid-chain
      val directFinal = state.activeChainMoves.firstOrNull { it.to == square }
      if (directFinal != null) {
        commitExerciseMove(directFinal)
        return
      }
      // Reset partial chain on tapping elsewhere
      resetExercise(keepFeedback = false)
      return
    }

    val clickedPiece = state.exerciseBoard[square]
    val currentSel = state.selectedSquare

    // If a piece is already selected, check if tapping a target square
    if (currentSel != null) {
      val movesFromSel = state.legalMoves.filter { it.from == currentSel }
      val directMove = movesFromSel.firstOrNull { it.to == square }
      if (directMove != null) {
        commitExerciseMove(directMove)
        return
      }
      // Check if tapping first intermediate jump square of a multi-jump move
      val chainMatches = movesFromSel.filter { it.steps.size > 1 && it.steps[0].to == square }
      if (chainMatches.isNotEmpty()) {
        val step = chainMatches.first().steps[0]
        val nextBoard = RussianCheckersEngine.applySingleStep(state.exerciseBoard, step)
        val nextTargets = chainMatches.mapNotNull { it.steps.getOrNull(1)?.to }.toSet()
        _uiState.update {
          it.copy(
            exerciseBoard = nextBoard,
            selectedSquare = square,
            targetSquares = nextTargets,
            pendingChainStepIndex = 1,
            activeChainMoves = chainMatches,
            feedback = null,
          )
        }
        return
      }
    }

    // Selecting own piece
    if (clickedPiece != null && clickedPiece.color == lesson.playerColor) {
      val movesForPiece = state.legalMoves.filter { it.from == square }
      if (movesForPiece.isEmpty()) {
        val msg = if (state.mandatorySources.isNotEmpty()) {
          if (hintsEnabled) {
            "По правилам русских шашек бить обязательно! Выбери шашку с оранжевым кольцом."
          } else {
            "По правилам русских шашек бить обязательно! Найди шашку, которая должна рубить."
          }
        } else {
          "У этой шашки сейчас нет свободных ходов."
        }
        _uiState.update {
          it.copy(
            selectedSquare = null,
            targetSquares = emptySet(),
            feedback = ExerciseFeedback.TryAgain(msg),
          )
        }
        return
      }
      // Highlight both immediate step targets and final targets so kids can tap either way
      val targets = movesForPiece.flatMap { listOf(it.to, it.steps.first().to) }.toSet()
      _uiState.update {
        it.copy(
          selectedSquare = square,
          targetSquares = targets,
          feedback = null,
        )
      }
    } else if (currentSel != null) {
      // Tapped invalid square while piece was selected
      val selPiece = state.exerciseBoard[currentSel]
      val wrongBackwards = selPiece != null && !selPiece.isKing && square.row > currentSel.row && state.mandatorySources.isEmpty()
      val msg = if (wrongBackwards) {
        "Простая шашка не может ходить назад — только вперёд по тёмным диагоналям (назад она может только бить!)."
      } else if (state.mandatorySources.isNotEmpty()) {
        if (hintsEnabled) {
          "Есть обязательное взятие! Перепрыгни через шашку соперника на светящуюся зелёную точку."
        } else {
          "Есть обязательное взятие! Перепрыгни через шашку соперника на свободное поле за ней."
        }
      } else {
        if (hintsEnabled) {
          "Сюда шашка сходить не может. Нажми на зелёную точку на диагонали!"
        } else {
          "Сюда шашка сходить не может. Проверь ход по тёмной диагонали!"
        }
      }
      _uiState.update {
        it.copy(
          feedback = ExerciseFeedback.TryAgain(msg),
        )
      }
    }
  }

  private fun commitExerciseMove(move: CheckersMove) {
    // Recompute from initial exercise board if mid-chain applied partial steps
    val cleanInitial = lesson.exerciseBoard()
    val nextBoard = RussianCheckersEngine.applyMove(cleanInitial, move)
    val isAccepted = move.notationString in lesson.acceptedMoveNotations

    if (isAccepted) {
      _uiState.update {
        it.copy(
          exerciseBoard = nextBoard,
          selectedSquare = null,
          targetSquares = emptySet(),
          mandatorySources = emptySet(),
          pendingChainStepIndex = 0,
          activeChainMoves = emptyList(),
          animatedHint = null,
          hintMessage = null,
          isSolved = true,
          feedback = ExerciseFeedback.Success(
            message = lesson.successFeedback,
            starsEarned = lesson.starsReward,
          ),
        )
      }
      onLessonSolvedCallback(lesson.id, lesson.starsReward)
    } else {
      // Reset back to start of exercise and explain why
      val legal = RussianCheckersEngine.getLegalMoves(cleanInitial, lesson.playerColor)
      val mandatory = if (legal.any { it.isCapture }) legal.map { it.from }.toSet() else emptySet()
      _uiState.update {
        it.copy(
          exerciseBoard = cleanInitial,
          selectedSquare = null,
          targetSquares = emptySet(),
          mandatorySources = mandatory,
          pendingChainStepIndex = 0,
          activeChainMoves = emptyList(),
          feedback = ExerciseFeedback.TryAgain(lesson.suboptimalMoveHint),
        )
      }
    }
  }

  fun requestHint() {
    _uiState.update {
      it.copy(
        activeTab = LessonTab.EXERCISE,
        animatedHint = lesson.hintMove,
        hintMessage = lesson.hintText,
      )
    }
  }

  fun resetExercise(keepFeedback: Boolean = false) {
    val cleanBoard = lesson.exerciseBoard()
    val legal = RussianCheckersEngine.getLegalMoves(cleanBoard, lesson.playerColor)
    val mandatory = if (legal.any { it.isCapture }) legal.map { it.from }.toSet() else emptySet()
    _uiState.update {
      it.copy(
        exerciseBoard = cleanBoard,
        selectedSquare = null,
        targetSquares = emptySet(),
        legalMoves = legal,
        mandatorySources = mandatory,
        pendingChainStepIndex = 0,
        activeChainMoves = emptyList(),
        animatedHint = null,
        hintMessage = null,
        feedback = if (keepFeedback) it.feedback else null,
        isSolved = false,
      )
    }
  }

  override fun onCleared() {
    super.onCleared()
    demoJob?.cancel()
  }
}
