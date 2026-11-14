package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.checkers.BotDifficulty
import com.example.domain.checkers.CheckersBoard
import com.example.domain.checkers.CheckersMove
import com.example.domain.checkers.PieceColor
import com.example.domain.checkers.RussianCheckersEngine
import com.example.domain.checkers.Square
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class GameSnapshot(
  val board: CheckersBoard,
  val turn: PieceColor,
  val lastMove: CheckersMove?,
)

data class PlayGameUiState(
  val difficulty: BotDifficulty?, // null = 2 players pass-and-play
  val board: CheckersBoard = CheckersBoard.initial(),
  val turn: PieceColor = PieceColor.WHITE,
  val selectedSquare: Square? = null,
  val legalMoves: List<CheckersMove> = RussianCheckersEngine.getLegalMoves(CheckersBoard.initial(), PieceColor.WHITE),
  val targetSquares: Set<Square> = emptySet(),
  val mandatorySources: Set<Square> = emptySet(),
  val pendingChainStepIndex: Int = 0,
  val activeChainMoves: List<CheckersMove> = emptyList(),
  val turnStartBoard: CheckersBoard = CheckersBoard.initial(),
  val lastMove: CheckersMove? = null,
  val animatedHint: CheckersMove? = null,
  val hintExplanation: String? = null,
  val isBotThinking: Boolean = false,
  val statusText: String = "Твой ход! Белые начинают.",
  val winner: PieceColor? = null,
  val canUndo: Boolean = false,
)

class PlayGameViewModel(
  private val difficulty: BotDifficulty?,
  private val onGameFinishedCallback: (Boolean, BotDifficulty?, Int) -> Unit,
) : ViewModel() {

  private val _uiState = MutableStateFlow(createInitialState())
  val uiState: StateFlow<PlayGameUiState> = _uiState.asStateFlow()

  private val history = mutableListOf<GameSnapshot>()
  private var botJob: Job? = null
  private var resultReported = false

  private fun createInitialState(): PlayGameUiState {
    val start = CheckersBoard.initial()
    val legal = RussianCheckersEngine.getLegalMoves(start, PieceColor.WHITE)
    return PlayGameUiState(
      difficulty = difficulty,
      board = start,
      turnStartBoard = start,
      legalMoves = legal,
      statusText = if (difficulty != null) {
        "Твой ход (белые)! Соперник: ${difficulty.title}."
      } else {
        "Ход белых! Игра вдвоём на одном экране."
      },
    )
  }

  fun onSquareClick(square: Square, hintsEnabled: Boolean = true) {
    val state = _uiState.value
    if (state.winner != null || state.isBotThinking) return
    if (difficulty != null && state.turn == PieceColor.BLACK) return

    if (state.activeChainMoves.isNotEmpty()) {
      val stepIdx = state.pendingChainStepIndex
      val matchingNext = state.activeChainMoves.filter { it.steps.getOrNull(stepIdx)?.to == square }
      if (matchingNext.isNotEmpty()) {
        val completed = matchingNext.firstOrNull { it.steps.size == stepIdx + 1 }
        if (completed != null) {
          commitHumanMove(completed)
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
              statusText = "Продолжай серию взятий!",
            )
          }
        }
        return
      }
      val directFinal = state.activeChainMoves.firstOrNull { it.to == square }
      if (directFinal != null) {
        commitHumanMove(directFinal)
        return
      }
      // Cancel partial selection back to turnStartBoard
      val legal = RussianCheckersEngine.getLegalMoves(state.turnStartBoard, state.turn)
      val mandatory = if (legal.any { it.isCapture }) legal.map { it.from }.toSet() else emptySet()
      _uiState.update {
        it.copy(
          board = it.turnStartBoard,
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
        commitHumanMove(directMove)
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
            statusText = "Прыгай дальше — можно срубить ещё!",
          )
        }
        return
      }
    }

    val clickedPiece = state.board[square]
    if (clickedPiece != null && clickedPiece.color == state.turn) {
      val movesForPiece = state.legalMoves.filter { it.from == square }
      if (movesForPiece.isEmpty()) {
        val msg = if (state.mandatorySources.isNotEmpty()) {
          if (hintsEnabled) {
            "Бить обязательно! Выбери шашку с оранжевым кольцом."
          } else {
            "Бить обязательно! Найди на доске шашку, которая должна рубить."
          }
        } else {
          "У этой шашки пока нет свободного хода."
        }
        _uiState.update { it.copy(selectedSquare = null, targetSquares = emptySet(), statusText = msg) }
        return
      }
      val targets = movesForPiece.flatMap { listOf(it.to, it.steps.first().to) }.toSet()
      _uiState.update {
        it.copy(
          selectedSquare = square,
          targetSquares = targets,
        )
      }
    }
  }

  private fun commitHumanMove(move: CheckersMove) {
    val state = _uiState.value
    history.add(GameSnapshot(state.turnStartBoard, state.turn, state.lastMove))

    val nextBoard = RussianCheckersEngine.applyMove(state.turnStartBoard, move)
    val nextTurn = state.turn.opposite()
    val winner = RussianCheckersEngine.checkWinner(nextBoard, nextTurn)

    if (winner != null) {
      finishGame(nextBoard, move, winner)
      return
    }

    if (difficulty != null && nextTurn == PieceColor.BLACK) {
      _uiState.update {
        it.copy(
          board = nextBoard,
          turnStartBoard = nextBoard,
          turn = PieceColor.BLACK,
          selectedSquare = null,
          targetSquares = emptySet(),
          legalMoves = emptyList(),
          mandatorySources = emptySet(),
          pendingChainStepIndex = 0,
          activeChainMoves = emptyList(),
          lastMove = move,
          animatedHint = null,
          hintExplanation = null,
          isBotThinking = true,
          statusText = "${difficulty.title} думает над ходом...",
          canUndo = history.isNotEmpty(),
        )
      }
      triggerBotTurn(nextBoard)
    } else {
      val nextLegal = RussianCheckersEngine.getLegalMoves(nextBoard, nextTurn)
      val nextMandatory = if (nextLegal.any { it.isCapture }) nextLegal.map { it.from }.toSet() else emptySet()
      val turnLabel = if (nextTurn == PieceColor.WHITE) "Ход белых!" else "Ход чёрных!"
      val capMsg = if (nextMandatory.isNotEmpty()) " $turnLabel Обязательное взятие!" else " $turnLabel"
      _uiState.update {
        it.copy(
          board = nextBoard,
          turnStartBoard = nextBoard,
          turn = nextTurn,
          selectedSquare = null,
          targetSquares = emptySet(),
          legalMoves = nextLegal,
          mandatorySources = nextMandatory,
          pendingChainStepIndex = 0,
          activeChainMoves = emptyList(),
          lastMove = move,
          animatedHint = null,
          hintExplanation = null,
          statusText = capMsg.trim(),
          canUndo = history.isNotEmpty(),
        )
      }
    }
  }

  private fun triggerBotTurn(boardAfterHuman: CheckersBoard) {
    val diff = difficulty ?: return
    botJob?.cancel()
    botJob = viewModelScope.launch {
      delay(500L)
      val botMove = withContext(Dispatchers.Default) {
        RussianCheckersEngine.chooseBotMove(boardAfterHuman, PieceColor.BLACK, diff)
      }
      if (botMove == null) {
        finishGame(boardAfterHuman, _uiState.value.lastMove, PieceColor.WHITE)
        return@launch
      }
      val afterBotBoard = RussianCheckersEngine.applyMove(boardAfterHuman, botMove)
      val winner = RussianCheckersEngine.checkWinner(afterBotBoard, PieceColor.WHITE)
      if (winner != null) {
        finishGame(afterBotBoard, botMove, winner)
        return@launch
      }
      val nextLegal = RussianCheckersEngine.getLegalMoves(afterBotBoard, PieceColor.WHITE)
      val nextMandatory = if (nextLegal.any { it.isCapture }) nextLegal.map { it.from }.toSet() else emptySet()
      val msg = when {
        nextMandatory.isNotEmpty() -> "Твой ход! У тебя есть обязательное взятие!"
        botMove.isCapture -> "Соперник срубил шашку — теперь твой ответный ход!"
        else -> "Твой ход (белые)!"
      }
      _uiState.update {
        it.copy(
          board = afterBotBoard,
          turnStartBoard = afterBotBoard,
          turn = PieceColor.WHITE,
          legalMoves = nextLegal,
          mandatorySources = nextMandatory,
          lastMove = botMove,
          isBotThinking = false,
          statusText = msg,
        )
      }
    }
  }

  private fun finishGame(finalBoard: CheckersBoard, lastMove: CheckersMove?, winner: PieceColor) {
    val msg = when {
      difficulty == null && winner == PieceColor.WHITE -> "Победа белых! Отличная партия!"
      difficulty == null && winner == PieceColor.BLACK -> "Победа чёрных! Отличная партия!"
      winner == PieceColor.WHITE -> "Ура! Ты победил соперника «${difficulty?.title}»!"
      else -> "На этот раз выиграл ${difficulty?.title}. Не расстраивайся — попробуй снова!"
    }
    _uiState.update {
      it.copy(
        board = finalBoard,
        turnStartBoard = finalBoard,
        selectedSquare = null,
        targetSquares = emptySet(),
        legalMoves = emptyList(),
        mandatorySources = emptySet(),
        pendingChainStepIndex = 0,
        activeChainMoves = emptyList(),
        lastMove = lastMove,
        isBotThinking = false,
        winner = winner,
        statusText = msg,
      )
    }
    if (!resultReported && difficulty != null) {
      resultReported = true
      val won = winner == PieceColor.WHITE
      onGameFinishedCallback(won, difficulty, if (won) difficulty.starsReward else 0)
    }
  }

  fun requestHint() {
    val state = _uiState.value
    if (state.winner != null || state.isBotThinking) return
    viewModelScope.launch {
      val rec = withContext(Dispatchers.Default) {
        RussianCheckersEngine.computeBestHint(state.turnStartBoard, state.turn)
      }
      if (rec != null) {
        _uiState.update {
          it.copy(
            animatedHint = rec.move,
            hintExplanation = rec.explanation,
          )
        }
      }
    }
  }

  fun undoMove() {
    if (history.isEmpty() || _uiState.value.isBotThinking) return
    botJob?.cancel()
    val snapshot = history.removeAt(history.lastIndex)
    val legal = RussianCheckersEngine.getLegalMoves(snapshot.board, snapshot.turn)
    val mandatory = if (legal.any { it.isCapture }) legal.map { it.from }.toSet() else emptySet()
    _uiState.update {
      it.copy(
        board = snapshot.board,
        turnStartBoard = snapshot.board,
        turn = snapshot.turn,
        selectedSquare = null,
        targetSquares = emptySet(),
        legalMoves = legal,
        mandatorySources = mandatory,
        pendingChainStepIndex = 0,
        activeChainMoves = emptyList(),
        lastMove = snapshot.lastMove,
        animatedHint = null,
        hintExplanation = null,
        winner = null,
        statusText = "Ход отменён. Попробуй другой вариант!",
        canUndo = history.isNotEmpty(),
      )
    }
  }

  fun restartGame() {
    botJob?.cancel()
    history.clear()
    resultReported = false
    _uiState.value = createInitialState()
  }

  override fun onCleared() {
    super.onCleared()
    botJob?.cancel()
  }
}
