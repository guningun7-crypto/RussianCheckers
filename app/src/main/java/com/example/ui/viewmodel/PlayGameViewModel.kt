package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.checkers.AnalyzedMoveEntry
import com.example.domain.checkers.BotDifficulty
import com.example.domain.checkers.CheckersBoard
import com.example.domain.checkers.CheckersMove
import com.example.domain.checkers.GameAnalyzer
import com.example.domain.checkers.GameOutcome
import com.example.domain.checkers.OpeningBook
import com.example.domain.checkers.PieceColor
import com.example.domain.checkers.PlayerColorPreference
import com.example.domain.checkers.RecordedGameMove
import com.example.domain.checkers.RussianCheckersEngine
import com.example.domain.checkers.Square
import com.example.domain.checkers.TimeControlOption
import com.example.domain.curriculum.EndgameCatalog
import com.example.domain.curriculum.EndgameScenario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class GameLaunchConfig(
  val difficulty: BotDifficulty?,
  val colorPref: PlayerColorPreference = PlayerColorPreference.WHITE,
  val timeControl: TimeControlOption = TimeControlOption.NONE,
  val flyingCheckers: Boolean = false,
  val endgameScenarioId: String? = null,
)

data class GameSnapshot(
  val board: CheckersBoard,
  val turn: PieceColor,
  val lastMove: CheckersMove?,
  val addedHash: Long,
  val quietPlies: Int,
  val whiteTimeMs: Long,
  val blackTimeMs: Long,
  val moveCount: Int,
)

data class PlayGameUiState(
  val difficulty: BotDifficulty?,
  val humanColor: PieceColor = PieceColor.WHITE,
  val botColor: PieceColor = PieceColor.BLACK,
  val isBoardFlipped: Boolean = false,
  val timeControl: TimeControlOption = TimeControlOption.NONE,
  val whiteTimeMs: Long = 0L,
  val blackTimeMs: Long = 0L,
  val flyingOpeningName: String? = null,
  val detectedOpeningName: String? = null,
  val endgameScenario: EndgameScenario? = null,
  val quietKingPlies: Int = 0,
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
  val outcome: GameOutcome? = null,
  val winner: PieceColor? = null,
  val canUndo: Boolean = false,
  val moveHistory: List<RecordedGameMove> = emptyList(),
  val isReviewMode: Boolean = false,
  val isAnalyzing: Boolean = false,
  val analysisEntries: List<AnalyzedMoveEntry> = emptyList(),
) {
  val isGameOver: Boolean get() = outcome != null
}

class PlayGameViewModel(
  private val launchConfig: GameLaunchConfig,
  private val onGameFinishedCallback: (GameOutcome, BotDifficulty?, Int, String?) -> Unit,
  private val onNewMatchStarted: () -> Unit = {},
) : ViewModel() {

  private val history = mutableListOf<GameSnapshot>()
  private val recordedMoves = mutableListOf<RecordedGameMove>()
  private val positionCounts = mutableMapOf<Long, Int>()
  private var quietKingPliesCounter = 0

  private var botJob: Job? = null
  private var clockJob: Job? = null
  private var resultReported = false

  private val _uiState = MutableStateFlow(buildInitialState())
  val uiState: StateFlow<PlayGameUiState> = _uiState.asStateFlow()

  init {
    startClockIfNeeded()
    maybeTriggerOpeningBotMove()
  }

  private fun resolveHumanColor(): PieceColor {
    if (launchConfig.endgameScenarioId != null) return PieceColor.WHITE
    if (launchConfig.difficulty == null) return PieceColor.WHITE
    return when (launchConfig.colorPref) {
      PlayerColorPreference.WHITE -> PieceColor.WHITE
      PlayerColorPreference.BLACK -> PieceColor.BLACK
      PlayerColorPreference.RANDOM -> if (kotlin.random.Random.nextBoolean()) PieceColor.WHITE else PieceColor.BLACK
    }
  }

  private fun buildInitialState(): PlayGameUiState {
    history.clear()
    recordedMoves.clear()
    positionCounts.clear()
    quietKingPliesCounter = 0
    resultReported = false

    val scenario = launchConfig.endgameScenarioId?.let { EndgameCatalog.findById(it) }
    val humanColor = resolveHumanColor()
    val botColor = humanColor.opposite()

    var startBoard = scenario?.initialBoard() ?: CheckersBoard.initial()
    var flyingLabel: String? = null
    if (scenario == null && launchConfig.flyingCheckers) {
      val setup = OpeningBook.flyingOpenings.random()
      val applied = OpeningBook.applyFlyingOpening(setup)
      if (applied != null) {
        startBoard = applied.first
        flyingLabel = "${setup.name} (${setup.whiteMove}, ${setup.blackMove})"
      }
    }

    val startTurn = PieceColor.WHITE
    val initialHash = RussianCheckersEngine.boardHash(startBoard, startTurn)
    positionCounts[initialHash] = 1

    val legal = RussianCheckersEngine.getLegalMoves(startBoard, startTurn)
    val mandatory = if (legal.any { it.isCapture }) legal.map { it.from }.toSet() else emptySet()
    val initClockMs = launchConfig.timeControl.initialSeconds * 1000L

    val initStatus = when {
      scenario != null -> "Эндшпиль «${scenario.title}»: ${scenario.goalText}"
      launchConfig.difficulty == null -> "Ход белых! Игра вдвоём на одном экране."
      flyingLabel != null -> "Летающие шашки: $flyingLabel."
      humanColor == PieceColor.BLACK -> "Ты играешь чёрными! Белые (${launchConfig.difficulty.title}) делают первый ход..."
      else -> "Твой ход (белые)! Соперник: ${launchConfig.difficulty.title}."
    }

    return PlayGameUiState(
      difficulty = launchConfig.difficulty,
      humanColor = humanColor,
      botColor = botColor,
      isBoardFlipped = (launchConfig.difficulty != null && humanColor == PieceColor.BLACK),
      timeControl = launchConfig.timeControl,
      whiteTimeMs = initClockMs,
      blackTimeMs = initClockMs,
      flyingOpeningName = flyingLabel,
      endgameScenario = scenario,
      board = startBoard,
      turnStartBoard = startBoard,
      turn = startTurn,
      legalMoves = legal,
      mandatorySources = mandatory,
      isBotThinking = (launchConfig.difficulty != null && humanColor == PieceColor.BLACK),
      statusText = initStatus,
    )
  }

  private fun maybeTriggerOpeningBotMove() {
    val state = _uiState.value
    if (launchConfig.difficulty != null && state.turn == state.botColor && state.outcome == null) {
      triggerBotTurn(state.board, state.botColor)
    }
  }

  private fun startClockIfNeeded() {
    clockJob?.cancel()
    if (launchConfig.timeControl == TimeControlOption.NONE) return
    clockJob = viewModelScope.launch {
      var lastTick = System.currentTimeMillis()
      while (true) {
        delay(200L)
        val now = System.currentTimeMillis()
        val elapsed = (now - lastTick).coerceIn(0L, 1000L)
        lastTick = now

        val st = _uiState.value
        if (st.isGameOver) break

        if (st.turn == PieceColor.WHITE) {
          val remaining = (st.whiteTimeMs - elapsed).coerceAtLeast(0L)
          _uiState.update { it.copy(whiteTimeMs = remaining) }
          if (remaining == 0L) {
            finishByTimeout(loser = PieceColor.WHITE)
            break
          }
        } else {
          val remaining = (st.blackTimeMs - elapsed).coerceAtLeast(0L)
          _uiState.update { it.copy(blackTimeMs = remaining) }
          if (remaining == 0L) {
            finishByTimeout(loser = PieceColor.BLACK)
            break
          }
        }
      }
    }
  }

  private fun finishByTimeout(loser: PieceColor) {
    botJob?.cancel()
    val state = _uiState.value
    if (state.isGameOver) return
    val winnerColor = loser.opposite()
    val outcome = when {
      launchConfig.difficulty == null -> if (winnerColor == PieceColor.WHITE) GameOutcome.WIN else GameOutcome.LOSS
      winnerColor == state.humanColor -> GameOutcome.WIN
      else -> GameOutcome.LOSS
    }
    val msg = when {
      launchConfig.difficulty == null && winnerColor == PieceColor.WHITE -> "Время чёрных истекло — победа белых!"
      launchConfig.difficulty == null -> "Время белых истекло — победа чёрных!"
      winnerColor == state.humanColor -> "Время соперника вышло! Победа по времени!"
      else -> "Время на часах истекло! Попробуй сыграть быстрее."
    }
    reportAndFinish(state.turnStartBoard, state.lastMove, outcome, winnerColor, msg)
  }

  fun toggleBoardFlip() {
    _uiState.update { it.copy(isBoardFlipped = !it.isBoardFlipped) }
  }

  fun onSquareClick(square: Square, hintsEnabled: Boolean = true) {
    val state = _uiState.value
    if (state.isGameOver || state.isBotThinking) return
    if (state.difficulty != null && state.turn != state.humanColor) return

    if (state.activeChainMoves.isNotEmpty()) {
      val stepIdx = state.pendingChainStepIndex
      val matchingNext = state.activeChainMoves.filter { it.steps.getOrNull(stepIdx)?.to == square }
      if (matchingNext.isNotEmpty()) {
        val completed = matchingNext.firstOrNull { it.steps.size == stepIdx + 1 }
        if (completed != null) {
          commitMove(completed, state.turn)
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
        commitMove(directFinal, state.turn)
        return
      }
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
        commitMove(directMove, state.turn)
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

  private fun commitMove(move: CheckersMove, mover: PieceColor) {
    val state = _uiState.value
    val prevBoard = state.turnStartBoard
    val nextBoard = RussianCheckersEngine.applyMove(prevBoard, move)
    val nextTurn = mover.opposite()

    // Track quiet king-only moves & 3-fold repetition
    val movedPiece = prevBoard[move.from]
    val nextQuietPlies = if (!move.isCapture && movedPiece?.isKing == true) {
      quietKingPliesCounter + 1
    } else {
      0
    }
    val nextHash = RussianCheckersEngine.boardHash(nextBoard, nextTurn)

    history.add(
      GameSnapshot(
        board = prevBoard,
        turn = mover,
        lastMove = state.lastMove,
        addedHash = nextHash,
        quietPlies = quietKingPliesCounter,
        whiteTimeMs = state.whiteTimeMs,
        blackTimeMs = state.blackTimeMs,
        moveCount = recordedMoves.size,
      )
    )

    quietKingPliesCounter = nextQuietPlies
    val newHashCount = (positionCounts[nextHash] ?: 0) + 1
    positionCounts[nextHash] = newHashCount

    val plyIdx = recordedMoves.size
    recordedMoves.add(
      RecordedGameMove(
        plyIndex = plyIdx,
        moveNumber = (plyIdx / 2) + 1,
        mover = mover,
        move = move,
        boardBefore = prevBoard,
        boardAfter = nextBoard,
      )
    )

    val openingName = if (state.endgameScenario == null && state.flyingOpeningName == null) {
      OpeningBook.detectOpeningName(recordedMoves.map { it.move.notationString }) ?: state.detectedOpeningName
    } else {
      state.detectedOpeningName
    }

    val incMs = state.timeControl.incrementSeconds * 1000L
    val nextWhiteMs = if (state.timeControl != TimeControlOption.NONE && mover == PieceColor.WHITE) {
      state.whiteTimeMs + incMs
    } else {
      state.whiteTimeMs
    }
    val nextBlackMs = if (state.timeControl != TimeControlOption.NONE && mover == PieceColor.BLACK) {
      state.blackTimeMs + incMs
    } else {
      state.blackTimeMs
    }

    // 1. Check win/loss by elimination or stalemate
    val winner = RussianCheckersEngine.checkWinner(nextBoard, nextTurn)
    if (winner != null) {
      val outcome = when {
        state.difficulty == null -> if (winner == PieceColor.WHITE) GameOutcome.WIN else GameOutcome.LOSS
        winner == state.humanColor -> GameOutcome.WIN
        else -> GameOutcome.LOSS
      }
      val msg = buildWinLossMessage(winner, outcome, state)
      _uiState.update {
        it.copy(
          whiteTimeMs = nextWhiteMs,
          blackTimeMs = nextBlackMs,
          detectedOpeningName = openingName,
          quietKingPlies = quietKingPliesCounter,
          moveHistory = recordedMoves.toList(),
        )
      }
      reportAndFinish(nextBoard, move, outcome, winner, msg)
      return
    }

    // 2. Check official Russian Checkers Draw rules
    if (newHashCount >= 3) {
      _uiState.update {
        it.copy(
          whiteTimeMs = nextWhiteMs,
          blackTimeMs = nextBlackMs,
          detectedOpeningName = openingName,
          quietKingPlies = quietKingPliesCounter,
          moveHistory = recordedMoves.toList(),
        )
      }
      reportAndFinish(
        finalBoard = nextBoard,
        lastMove = move,
        outcome = GameOutcome.DRAW,
        winner = null,
        message = "Ничья! Позиция повторилась 3 раза по правилам русских шашек.",
      )
      return
    }
    if (quietKingPliesCounter >= 30) {
      _uiState.update {
        it.copy(
          whiteTimeMs = nextWhiteMs,
          blackTimeMs = nextBlackMs,
          detectedOpeningName = openingName,
          quietKingPlies = quietKingPliesCounter,
          moveHistory = recordedMoves.toList(),
        )
      }
      reportAndFinish(
        finalBoard = nextBoard,
        lastMove = move,
        outcome = GameOutcome.DRAW,
        winner = null,
        message = "Ничья! 15 ходов подряд без взятий и ходов простых шашек.",
      )
      return
    }

    // 3. Hand over to Bot or Next Human
    if (state.difficulty != null && nextTurn == state.botColor) {
      _uiState.update {
        it.copy(
          board = nextBoard,
          turnStartBoard = nextBoard,
          turn = nextTurn,
          selectedSquare = null,
          targetSquares = emptySet(),
          legalMoves = emptyList(),
          mandatorySources = emptySet(),
          pendingChainStepIndex = 0,
          activeChainMoves = emptyList(),
          lastMove = move,
          animatedHint = null,
          hintExplanation = null,
          whiteTimeMs = nextWhiteMs,
          blackTimeMs = nextBlackMs,
          detectedOpeningName = openingName,
          quietKingPlies = quietKingPliesCounter,
          moveHistory = recordedMoves.toList(),
          isBotThinking = true,
          statusText = "${state.difficulty.title} думает над ходом...",
          canUndo = history.isNotEmpty(),
        )
      }
      triggerBotTurn(nextBoard, state.botColor)
    } else {
      val nextLegal = RussianCheckersEngine.getLegalMoves(nextBoard, nextTurn)
      val nextMandatory = if (nextLegal.any { it.isCapture }) nextLegal.map { it.from }.toSet() else emptySet()
      val msg = buildHumanTurnMessage(state, move, nextTurn, nextMandatory.isNotEmpty())
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
          whiteTimeMs = nextWhiteMs,
          blackTimeMs = nextBlackMs,
          detectedOpeningName = openingName,
          quietKingPlies = quietKingPliesCounter,
          moveHistory = recordedMoves.toList(),
          isBotThinking = false,
          statusText = msg,
          canUndo = history.isNotEmpty(),
        )
      }
    }
  }

  private fun triggerBotTurn(boardForBot: CheckersBoard, botColor: PieceColor) {
    val diff = launchConfig.difficulty ?: return
    botJob?.cancel()
    botJob = viewModelScope.launch {
      delay(420L)
      val botMove = withContext(Dispatchers.Default) {
        RussianCheckersEngine.chooseBotMove(boardForBot, botColor, diff)
      }
      if (botMove == null) {
        val humanWon = _uiState.value.humanColor
        reportAndFinish(
          finalBoard = boardForBot,
          lastMove = _uiState.value.lastMove,
          outcome = GameOutcome.WIN,
          winner = humanWon,
          message = "Ура! У соперника не осталось ходов — ты победил!",
        )
        return@launch
      }
      commitMove(botMove, botColor)
    }
  }

  private fun buildWinLossMessage(winner: PieceColor, outcome: GameOutcome, state: PlayGameUiState): String {
    return when {
      state.difficulty == null && winner == PieceColor.WHITE -> "Победа белых! Отличная партия!"
      state.difficulty == null -> "Победа чёрных! Отличная партия!"
      outcome == GameOutcome.WIN && state.endgameScenario != null ->
        "Эндшпиль «${state.endgameScenario.title}» выигран! Мастерская техника!"
      outcome == GameOutcome.WIN ->
        "Ура! Ты победил соперника «${state.difficulty.title}»!"
      else ->
        "На этот раз выиграл ${state.difficulty.title}. Сделай разбор партии и попробуй снова!"
    }
  }

  private fun buildHumanTurnMessage(
    state: PlayGameUiState,
    prevMove: CheckersMove,
    nextTurn: PieceColor,
    hasMandatory: Boolean,
  ): String {
    if (state.difficulty == null) {
      val turnLabel = if (nextTurn == PieceColor.WHITE) "Ход белых!" else "Ход чёрных!"
      return if (hasMandatory) "$turnLabel Обязательное взятие!" else turnLabel
    }
    return when {
      hasMandatory -> "Твой ход! У тебя есть обязательное взятие!"
      prevMove.isCapture -> "Соперник срубил шашку — теперь твой ответный ход!"
      else -> if (state.humanColor == PieceColor.WHITE) "Твой ход (белые)!" else "Твой ход (чёрные)!"
    }
  }

  private fun reportAndFinish(
    finalBoard: CheckersBoard,
    lastMove: CheckersMove?,
    outcome: GameOutcome,
    winner: PieceColor?,
    message: String,
  ) {
    clockJob?.cancel()
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
        outcome = outcome,
        winner = winner,
        statusText = message,
        moveHistory = recordedMoves.toList(),
      )
    }
    if (!resultReported && launchConfig.difficulty != null) {
      resultReported = true
      val stars = when (outcome) {
        GameOutcome.WIN -> launchConfig.difficulty.starsReward
        GameOutcome.DRAW -> 1
        GameOutcome.LOSS -> 0
      }
      onGameFinishedCallback(outcome, launchConfig.difficulty, stars, launchConfig.endgameScenarioId)
    }
  }

  fun requestHint() {
    val state = _uiState.value
    if (state.isGameOver || state.isBotThinking) return
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

    val state = _uiState.value
    val targetTurn = if (state.difficulty != null) state.humanColor else null

    var snapshot = popSnapshot()
    if (targetTurn != null) {
      while (snapshot != null && snapshot.turn != targetTurn && history.isNotEmpty()) {
        snapshot = popSnapshot()
      }
      if (snapshot == null || snapshot.turn != targetTurn) return
    }
    val snap = snapshot ?: return

    val legal = RussianCheckersEngine.getLegalMoves(snap.board, snap.turn)
    val mandatory = if (legal.any { it.isCapture }) legal.map { it.from }.toSet() else emptySet()
    _uiState.update {
      it.copy(
        board = snap.board,
        turnStartBoard = snap.board,
        turn = snap.turn,
        selectedSquare = null,
        targetSquares = emptySet(),
        legalMoves = legal,
        mandatorySources = mandatory,
        pendingChainStepIndex = 0,
        activeChainMoves = emptyList(),
        lastMove = snap.lastMove,
        animatedHint = null,
        hintExplanation = null,
        quietKingPlies = quietKingPliesCounter,
        whiteTimeMs = snap.whiteTimeMs,
        blackTimeMs = snap.blackTimeMs,
        moveHistory = recordedMoves.toList(),
        outcome = null,
        winner = null,
        isReviewMode = false,
        statusText = "Ход отменён. Попробуй другой вариант!",
        canUndo = history.isNotEmpty() && !(state.difficulty != null && history.all { h -> h.turn != state.humanColor }),
      )
    }
  }

  private fun popSnapshot(): GameSnapshot? {
    if (history.isEmpty()) return null
    val snap = history.removeAt(history.lastIndex)
    val prevCount = positionCounts[snap.addedHash] ?: 0
    if (prevCount <= 1) positionCounts.remove(snap.addedHash) else positionCounts[snap.addedHash] = prevCount - 1
    quietKingPliesCounter = snap.quietPlies
    while (recordedMoves.size > snap.moveCount) {
      recordedMoves.removeAt(recordedMoves.lastIndex)
    }
    return snap
  }

  fun openPostGameAnalysis() {
    val state = _uiState.value
    if (state.moveHistory.isEmpty()) return
    if (state.analysisEntries.isNotEmpty()) {
      _uiState.update { it.copy(isReviewMode = true) }
      return
    }
    _uiState.update { it.copy(isReviewMode = true, isAnalyzing = true) }
    viewModelScope.launch {
      val entries = withContext(Dispatchers.Default) {
        GameAnalyzer.analyzeHistory(state.moveHistory, state.humanColor)
      }
      _uiState.update { it.copy(isAnalyzing = false, analysisEntries = entries) }
    }
  }

  fun closePostGameAnalysis() {
    _uiState.update { it.copy(isReviewMode = false) }
  }

  fun restartGame() {
    botJob?.cancel()
    clockJob?.cancel()
    onNewMatchStarted()
    _uiState.value = buildInitialState()
    startClockIfNeeded()
    maybeTriggerOpeningBotMove()
  }

  override fun onCleared() {
    super.onCleared()
    botJob?.cancel()
    clockJob?.cancel()
  }
}
