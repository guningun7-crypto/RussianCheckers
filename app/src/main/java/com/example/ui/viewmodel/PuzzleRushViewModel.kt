package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.checkers.CheckersBoard
import com.example.domain.checkers.CheckersMove
import com.example.domain.checkers.RussianCheckersEngine
import com.example.domain.checkers.Square
import com.example.domain.curriculum.LogicPuzzle
import com.example.domain.curriculum.PuzzleCatalog
import com.example.domain.curriculum.PuzzleDifficulty
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PuzzleRushUiState(
  val currentPuzzle: LogicPuzzle,
  val board: CheckersBoard,
  val stepStartBoard: CheckersBoard,
  val currentStepIndex: Int = 0,
  val selectedSquare: Square? = null,
  val legalMoves: List<CheckersMove> = emptyList(),
  val targetSquares: Set<Square> = emptySet(),
  val mandatorySources: Set<Square> = emptySet(),
  val pendingChainStepIndex: Int = 0,
  val activeChainMoves: List<CheckersMove> = emptyList(),
  val score: Int = 0,
  val livesLeft: Int = 3,
  val remainingSeconds: Int = 180,
  val isFinished: Boolean = false,
  val isBotReplying: Boolean = false,
  val statusMessage: String = "Найди сильнейший ход! Время пошло!",
  val lastWasMistake: Boolean = false,
)

class PuzzleRushViewModel(
  allPuzzles: List<LogicPuzzle>,
  private val onRushFinished: (Int) -> Unit,
) : ViewModel() {

  private val queue: MutableList<LogicPuzzle> = buildProgressiveQueue(allPuzzles).toMutableList()
  private var queueIdx = 0
  private var timerJob: Job? = null
  private var resultReported = false

  private val _uiState = MutableStateFlow(buildInitialState())
  val uiState: StateFlow<PuzzleRushUiState> = _uiState.asStateFlow()

  init {
    startTimer()
  }

  private fun buildProgressiveQueue(puzzles: List<LogicPuzzle>): List<LogicPuzzle> {
    val easy = puzzles.filter { it.difficulty == PuzzleDifficulty.EASY }.shuffled()
    val med = puzzles.filter { it.difficulty == PuzzleDifficulty.MEDIUM }.shuffled()
    val hard = puzzles.filter { it.difficulty == PuzzleDifficulty.HARD }.shuffled()
    val master = puzzles.filter { it.difficulty == PuzzleDifficulty.MASTER }.shuffled()
    val combined = easy + med + hard + master
    return if (combined.isNotEmpty()) combined else PuzzleCatalog.puzzles
  }

  private fun buildInitialState(): PuzzleRushUiState {
    queueIdx = 0
    val first = queue.firstOrNull() ?: PuzzleCatalog.puzzles.first()
    val start = first.initialBoard()
    val legal = RussianCheckersEngine.getLegalMoves(start, first.playerColor)
    val mandatory = if (legal.any { it.isCapture }) legal.map { it.from }.toSet() else emptySet()
    return PuzzleRushUiState(
      currentPuzzle = first,
      board = start,
      stepStartBoard = start,
      legalMoves = legal,
      mandatorySources = mandatory,
      statusMessage = "Штурм 3 минуты: решай комбинации без ошибок!",
    )
  }

  private fun startTimer() {
    timerJob?.cancel()
    timerJob = viewModelScope.launch {
      while (true) {
        delay(1000L)
        val st = _uiState.value
        if (st.isFinished) break
        val nextSec = (st.remainingSeconds - 1).coerceAtLeast(0)
        _uiState.update { it.copy(remainingSeconds = nextSec) }
        if (nextSec == 0) {
          finishRush("Время вышло! Отличный спринт!")
          break
        }
      }
    }
  }

  fun onSquareClick(square: Square, hintsEnabled: Boolean = true) {
    val state = _uiState.value
    if (state.isFinished || state.isBotReplying) return

    if (state.activeChainMoves.isNotEmpty()) {
      val stepIdx = state.pendingChainStepIndex
      val matchingNext = state.activeChainMoves.filter { it.steps.getOrNull(stepIdx)?.to == square }
      if (matchingNext.isNotEmpty()) {
        val completed = matchingNext.firstOrNull { it.steps.size == stepIdx + 1 }
        if (completed != null) {
          evaluateMove(completed)
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
        evaluateMove(directFinal)
        return
      }
      val legal = RussianCheckersEngine.getLegalMoves(state.stepStartBoard, state.currentPuzzle.playerColor)
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
        )
      }
      return
    }

    val sel = state.selectedSquare
    if (sel != null) {
      val movesFromSel = state.legalMoves.filter { it.from == sel }
      val directMove = movesFromSel.firstOrNull { it.to == square }
      if (directMove != null) {
        evaluateMove(directMove)
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
    if (piece != null && piece.color == state.currentPuzzle.playerColor) {
      val pieceMoves = state.legalMoves.filter { it.from == square }
      if (pieceMoves.isEmpty()) {
        _uiState.update {
          it.copy(
            selectedSquare = null,
            targetSquares = emptySet(),
            statusMessage = if (hintsEnabled && state.mandatorySources.isNotEmpty()) {
              "Бить обязательно! Смотри оранжевую подсветку."
            } else {
              "Эта шашка не может так пойти."
            },
          )
        }
        return
      }
      val targets = pieceMoves.flatMap { listOf(it.to, it.steps.first().to) }.toSet()
      _uiState.update {
        it.copy(
          selectedSquare = square,
          targetSquares = targets,
          lastWasMistake = false,
        )
      }
    }
  }

  private fun evaluateMove(move: CheckersMove) {
    val state = _uiState.value
    val puzzle = state.currentPuzzle
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
            isBotReplying = true,
            lastWasMistake = false,
            statusMessage = "Точно! Добивай!",
          )
        }
        viewModelScope.launch {
          delay(350L)
          if (_uiState.value.isFinished) return@launch
          val afterReply = RussianCheckersEngine.applyMove(nextBoard, botReply)
          val nextLegal = RussianCheckersEngine.getLegalMoves(afterReply, puzzle.playerColor)
          val nextMandatory = if (nextLegal.any { it.isCapture }) nextLegal.map { it.from }.toSet() else emptySet()
          _uiState.update {
            it.copy(
              board = afterReply,
              stepStartBoard = afterReply,
              currentStepIndex = it.currentStepIndex + 1,
              legalMoves = nextLegal,
              mandatorySources = nextMandatory,
              isBotReplying = false,
              statusMessage = "Заверши комбинацию!",
            )
          }
        }
      } else {
        val nextScore = state.score + 1
        loadNextPuzzle(nextScore = nextScore, nextLives = state.livesLeft, msg = "Верно (+1)! Следующая задача!", mistake = false)
      }
    } else {
      val nextLives = state.livesLeft - 1
      if (nextLives <= 0) {
        _uiState.update {
          it.copy(
            livesLeft = 0,
            selectedSquare = null,
            targetSquares = emptySet(),
            lastWasMistake = true,
          )
        }
        finishRush("Закончились 3 жизни! Итого решено: ${state.score}.")
      } else {
        loadNextPuzzle(
          nextScore = state.score,
          nextLives = nextLives,
          msg = "Промах (-1 ❤️)! Переходим к следующей задаче!",
          mistake = true,
        )
      }
    }
  }

  private fun loadNextPuzzle(nextScore: Int, nextLives: Int, msg: String, mistake: Boolean) {
    queueIdx = (queueIdx + 1) % queue.size
    if (queueIdx == 0) {
      queue.shuffle()
    }
    val nextPuzzle = queue[queueIdx]
    val start = nextPuzzle.initialBoard()
    val legal = RussianCheckersEngine.getLegalMoves(start, nextPuzzle.playerColor)
    val mandatory = if (legal.any { it.isCapture }) legal.map { it.from }.toSet() else emptySet()

    _uiState.update {
      it.copy(
        currentPuzzle = nextPuzzle,
        board = start,
        stepStartBoard = start,
        currentStepIndex = 0,
        selectedSquare = null,
        legalMoves = legal,
        targetSquares = emptySet(),
        mandatorySources = mandatory,
        pendingChainStepIndex = 0,
        activeChainMoves = emptyList(),
        score = nextScore,
        livesLeft = nextLives,
        isBotReplying = false,
        statusMessage = msg,
        lastWasMistake = mistake,
      )
    }
  }

  private fun finishRush(reason: String) {
    timerJob?.cancel()
    val finalScore = _uiState.value.score
    _uiState.update {
      it.copy(
        isFinished = true,
        isBotReplying = false,
        selectedSquare = null,
        targetSquares = emptySet(),
        statusMessage = reason,
      )
    }
    if (!resultReported) {
      resultReported = true
      onRushFinished(finalScore)
    }
  }

  fun restartRush() {
    timerJob?.cancel()
    resultReported = false
    queue.shuffle()
    _uiState.value = buildInitialState()
    startTimer()
  }

  override fun onCleared() {
    super.onCleared()
    timerJob?.cancel()
  }
}
