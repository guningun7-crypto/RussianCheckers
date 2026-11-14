package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.checkers.CheckersBoard
import com.example.domain.checkers.CheckersMove
import com.example.domain.checkers.RussianCheckersEngine
import com.example.domain.checkers.Square
import com.example.domain.curriculum.LogicPuzzle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PuzzleUiState(
  val puzzle: LogicPuzzle,
  val board: CheckersBoard = puzzle.initialBoard(),
  val currentStepIndex: Int = 0,
  val selectedSquare: Square? = null,
  val legalMoves: List<CheckersMove> = RussianCheckersEngine.getLegalMoves(puzzle.initialBoard(), puzzle.playerColor),
  val targetSquares: Set<Square> = emptySet(),
  val mandatorySources: Set<Square> = emptySet(),
  val pendingChainStepIndex: Int = 0,
  val activeChainMoves: List<CheckersMove> = emptyList(),
  val stepStartBoard: CheckersBoard = puzzle.initialBoard(),
  val animatedHint: CheckersMove? = null,
  val hintMessage: String? = null,
  val usedHint: Boolean = false,
  val isBotReplying: Boolean = false,
  val statusBanner: String = puzzle.prompt,
  val isErrorBanner: Boolean = false,
  val isSolved: Boolean = false,
)

class PuzzleDetailViewModel(
  private val puzzle: LogicPuzzle,
  private val onPuzzleSolvedCallback: (String, Int, Boolean) -> Unit,
) : ViewModel() {

  private val _uiState = MutableStateFlow(createInitialState(puzzle))
  val uiState: StateFlow<PuzzleUiState> = _uiState.asStateFlow()

  private fun createInitialState(puzzle: LogicPuzzle): PuzzleUiState {
    val start = puzzle.initialBoard()
    val legal = RussianCheckersEngine.getLegalMoves(start, puzzle.playerColor)
    val mandatory = if (legal.any { it.isCapture }) legal.map { it.from }.toSet() else emptySet()
    return PuzzleUiState(
      puzzle = puzzle,
      board = start,
      stepStartBoard = start,
      legalMoves = legal,
      mandatorySources = mandatory,
    )
  }

  fun onSquareClick(square: Square, hintsEnabled: Boolean = true) {
    val state = _uiState.value
    if (state.isSolved || state.isBotReplying) return

    if (state.activeChainMoves.isNotEmpty()) {
      val stepIdx = state.pendingChainStepIndex
      val matchingNext = state.activeChainMoves.filter { it.steps.getOrNull(stepIdx)?.to == square }
      if (matchingNext.isNotEmpty()) {
        val completed = matchingNext.firstOrNull { it.steps.size == stepIdx + 1 }
        if (completed != null) {
          evaluateCandidateMove(completed, hintsEnabled)
        } else {
          val step = matchingNext.first().steps[stepIdx]
          val nextBoard = RussianCheckersEngine.applySingleStep(state.board, step)
          val nextTargets = matchingNext.mapNotNull { it.steps.getOrNull(stepIdx + 1)?.to }.toSet()
          _uiState.update {
            it.copy(
              board = nextBoard,
              selectedSquare = square,
              targetSquares = nextTargets,
              pendingChainStepIndex = stepIdx + 1,
              activeChainMoves = matchingNext,
            )
          }
        }
        return
      }
      val directFinal = state.activeChainMoves.firstOrNull { it.to == square }
      if (directFinal != null) {
        evaluateCandidateMove(directFinal, hintsEnabled)
        return
      }
      restoreCurrentStepBoard("Попробуй выбрать другую цепочку!")
      return
    }

    val sel = state.selectedSquare
    if (sel != null) {
      val movesFromSel = state.legalMoves.filter { it.from == sel }
      val directMove = movesFromSel.firstOrNull { it.to == square }
      if (directMove != null) {
        evaluateCandidateMove(directMove, hintsEnabled)
        return
      }
      val chainMatches = movesFromSel.filter { it.steps.size > 1 && it.steps[0].to == square }
      if (chainMatches.isNotEmpty()) {
        val step = chainMatches.first().steps[0]
        val nextBoard = RussianCheckersEngine.applySingleStep(state.board, step)
        val nextTargets = chainMatches.mapNotNull { it.steps.getOrNull(1)?.to }.toSet()
        _uiState.update {
          it.copy(
            board = nextBoard,
            selectedSquare = square,
            targetSquares = nextTargets,
            pendingChainStepIndex = 1,
            activeChainMoves = chainMatches,
          )
        }
        return
      }
    }

    val piece = state.board[square]
    if (piece != null && piece.color == puzzle.playerColor) {
      val pieceMoves = state.legalMoves.filter { it.from == square }
      if (pieceMoves.isEmpty()) {
        val msg = if (state.mandatorySources.isNotEmpty()) {
          if (hintsEnabled) {
            "Помни: когда есть взятие, бить обязательно! Выбери шашку с оранжевым кольцом."
          } else {
            "Помни: когда есть взятие, бить обязательно! Найди шашку, которая должна бить."
          }
        } else {
          "У этой шашки нет доступных ходов."
        }
        _uiState.update {
          it.copy(selectedSquare = null, targetSquares = emptySet(), statusBanner = msg, isErrorBanner = true)
        }
        return
      }
      val targets = pieceMoves.flatMap { listOf(it.to, it.steps.first().to) }.toSet()
      _uiState.update {
        it.copy(
          selectedSquare = square,
          targetSquares = targets,
          statusBanner = state.puzzle.prompt,
          isErrorBanner = false,
        )
      }
    }
  }

  private fun evaluateCandidateMove(move: CheckersMove, hintsEnabled: Boolean) {
    val state = _uiState.value
    val step = puzzle.steps[state.currentStepIndex]
    val nextBoard = RussianCheckersEngine.applyMove(state.stepStartBoard, move)

    if (move.notationString in step.acceptedNotations) {
      val botReply = step.botAutoReply
      if (botReply != null && state.currentStepIndex < puzzle.steps.lastIndex) {
        _uiState.update {
          it.copy(
            board = nextBoard,
            selectedSquare = null,
            targetSquares = emptySet(),
            mandatorySources = emptySet(),
            pendingChainStepIndex = 0,
            activeChainMoves = emptyList(),
            animatedHint = null,
            hintMessage = null,
            isBotReplying = true,
            statusBanner = "Отличный ход! Смотрим ответ соперника...",
            isErrorBanner = false,
          )
        }
        viewModelScope.launch {
          delay(700L)
          val afterReplyBoard = RussianCheckersEngine.applyMove(nextBoard, botReply)
          val nextLegal = RussianCheckersEngine.getLegalMoves(afterReplyBoard, puzzle.playerColor)
          val nextMandatory = if (nextLegal.any { it.isCapture }) nextLegal.map { it.from }.toSet() else emptySet()
          _uiState.update {
            it.copy(
              board = afterReplyBoard,
              stepStartBoard = afterReplyBoard,
              currentStepIndex = it.currentStepIndex + 1,
              legalMoves = nextLegal,
              mandatorySources = nextMandatory,
              isBotReplying = false,
              statusBanner = step.replyCaption ?: "Теперь заверши комбинацию!",
              isErrorBanner = false,
            )
          }
        }
      } else {
        _uiState.update {
          it.copy(
            board = nextBoard,
            selectedSquare = null,
            targetSquares = emptySet(),
            mandatorySources = emptySet(),
            pendingChainStepIndex = 0,
            activeChainMoves = emptyList(),
            animatedHint = null,
            hintMessage = null,
            isSolved = true,
            statusBanner = puzzle.successExplanation,
            isErrorBanner = false,
          )
        }
        onPuzzleSolvedCallback(puzzle.id, puzzle.difficulty.starsReward, state.usedHint)
      }
    } else {
      val wrongMsg = if (hintsEnabled) {
        "Не совсем так! Поищи ещё более сильный ход — можешь нажать «Подсказка»."
      } else {
        "Не совсем так! Подумай и найди более сильное продолжение."
      }
      restoreCurrentStepBoard(wrongMsg)
    }
  }

  private fun restoreCurrentStepBoard(message: String) {
    val state = _uiState.value
    val legal = RussianCheckersEngine.getLegalMoves(state.stepStartBoard, puzzle.playerColor)
    val mandatory = if (legal.any { it.isCapture }) legal.map { it.from }.toSet() else emptySet()
    _uiState.update {
      it.copy(
        board = it.stepStartBoard,
        selectedSquare = null,
        targetSquares = emptySet(),
        legalMoves = legal,
        mandatorySources = mandatory,
        pendingChainStepIndex = 0,
        activeChainMoves = emptyList(),
        statusBanner = message,
        isErrorBanner = true,
      )
    }
  }

  fun requestHint() {
    val step = puzzle.steps[_uiState.value.currentStepIndex]
    _uiState.update {
      it.copy(
        animatedHint = step.hintMove,
        hintMessage = step.hintText,
        usedHint = true,
      )
    }
  }

  fun resetPuzzle() {
    _uiState.value = createInitialState(puzzle)
  }
}
