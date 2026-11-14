package com.example.domain.checkers

import kotlin.math.abs
import kotlin.random.Random

object RussianCheckersEngine {

  private val ALL_DIAGONALS = listOf(
    -1 to -1,
    -1 to 1,
    1 to -1,
    1 to 1,
  )

  fun isPromotionRow(color: PieceColor, row: Int): Boolean =
    (color == PieceColor.WHITE && row == 0) || (color == PieceColor.BLACK && row == 7)

  fun getLegalMoves(board: CheckersBoard, turn: PieceColor): List<CheckersMove> {
    val captures = mutableListOf<CheckersMove>()
    val simpleMoves = mutableListOf<CheckersMove>()

    for (r in 0..7) {
      for (c in 0..7) {
        val sq = Square(r, c)
        val piece = board[sq] ?: continue
        if (piece.color != turn) continue

        findCaptureSequences(
          board = board,
          currentSq = sq,
          piece = piece,
          alreadyCaptured = emptySet(),
          accSteps = emptyList(),
          outMoves = captures,
        )

        if (captures.isEmpty()) {
          findSimpleMoves(board, sq, piece, simpleMoves)
        }
      }
    }

    // Mandatory capture rule in Russian Checkers ("Бить обязательно!")
    return if (captures.isNotEmpty()) captures else simpleMoves
  }

  private fun findSimpleMoves(
    board: CheckersBoard,
    from: Square,
    piece: Piece,
    outMoves: MutableList<CheckersMove>,
  ) {
    if (!piece.isKing) {
      val forwardDr = if (piece.color == PieceColor.WHITE) -1 else 1
      for (dc in listOf(-1, 1)) {
        val target = Square(from.row + forwardDr, from.col + dc)
        if (target.isInBounds && board[target] == null) {
          val promoted = isPromotionRow(piece.color, target.row)
          outMoves.add(
            CheckersMove(
              listOf(MoveStep(from = from, to = target, captured = null, promotedToKing = promoted))
            )
          )
        }
      }
    } else {
      for ((dr, dc) in ALL_DIAGONALS) {
        var r = from.row + dr
        var c = from.col + dc
        while (r in 0..7 && c in 0..7) {
          val target = Square(r, c)
          if (board[target] == null) {
            outMoves.add(
              CheckersMove(
                listOf(MoveStep(from = from, to = target, captured = null, promotedToKing = false))
              )
            )
            r += dr
            c += dc
          } else {
            break
          }
        }
      }
    }
  }

  private fun findCaptureSequences(
    board: CheckersBoard,
    currentSq: Square,
    piece: Piece,
    alreadyCaptured: Set<Square>,
    accSteps: List<MoveStep>,
    outMoves: MutableList<CheckersMove>,
  ) {
    if (!piece.isKing) {
      var foundContinuation = false
      for ((dr, dc) in ALL_DIAGONALS) {
        val enemySq = Square(currentSq.row + dr, currentSq.col + dc)
        val landSq = Square(currentSq.row + 2 * dr, currentSq.col + 2 * dc)
        if (!landSq.isInBounds) continue
        val enemyPiece = board[enemySq] ?: continue
        if (enemyPiece.color == piece.color || enemySq in alreadyCaptured) continue
        if (board[landSq] != null) continue

        foundContinuation = true
        val promotedNow = isPromotionRow(piece.color, landSq.row)
        val nextPiece = if (promotedNow) piece.copy(isKing = true) else piece
        // Move piece temporarily on board for further jumps; captured piece remains on board as obstacle until sequence finishes
        val nextBoard = board.withPiece(currentSq, null).withPiece(landSq, nextPiece)
        val step = MoveStep(
          from = currentSq,
          to = landSq,
          captured = enemySq,
          promotedToKing = promotedNow,
        )
        findCaptureSequences(
          board = nextBoard,
          currentSq = landSq,
          piece = nextPiece,
          alreadyCaptured = alreadyCaptured + enemySq,
          accSteps = accSteps + step,
          outMoves = outMoves,
        )
      }
      if (!foundContinuation && accSteps.isNotEmpty()) {
        outMoves.add(CheckersMove(accSteps))
      }
    } else {
      // Flying King capture in Russian Checkers
      var anyDiagonalCaptured = false
      for ((dr, dc) in ALL_DIAGONALS) {
        var r = currentSq.row + dr
        var c = currentSq.col + dc
        var enemySq: Square? = null
        while (r in 0..7 && c in 0..7) {
          val sq = Square(r, c)
          val p = board[sq]
          if (p == null) {
            r += dr
            c += dc
            continue
          }
          if (p.color != piece.color && sq !in alreadyCaptured) {
            enemySq = sq
          }
          break
        }
        if (enemySq == null) continue

        // Collect all empty landing squares beyond enemySq
        val landingSquares = mutableListOf<Square>()
        var lr = enemySq.row + dr
        var lc = enemySq.col + dc
        while (lr in 0..7 && lc in 0..7) {
          val landSq = Square(lr, lc)
          if (board[landSq] != null) break
          landingSquares.add(landSq)
          lr += dr
          lc += dc
        }
        if (landingSquares.isEmpty()) continue
        anyDiagonalCaptured = true

        // Check which landing squares allow continuing the capture chain
        val continuingResults = mutableListOf<CheckersMove>()
        val terminalResults = mutableListOf<CheckersMove>()

        for (landSq in landingSquares) {
          val nextBoard = board.withPiece(currentSq, null).withPiece(landSq, piece)
          val step = MoveStep(
            from = currentSq,
            to = landSq,
            captured = enemySq,
            promotedToKing = false,
          )
          val branchMoves = mutableListOf<CheckersMove>()
          findCaptureSequences(
            board = nextBoard,
            currentSq = landSq,
            piece = piece,
            alreadyCaptured = alreadyCaptured + enemySq,
            accSteps = accSteps + step,
            outMoves = branchMoves,
          )
          for (m in branchMoves) {
            if (m.steps.size > accSteps.size + 1) {
              continuingResults.add(m)
            } else {
              terminalResults.add(m)
            }
          }
        }

        // Official Russian Checkers rule: if a continuation exists from any landing square after capturing enemySq,
        // the king must continue capturing rather than stopping early.
        if (continuingResults.isNotEmpty()) {
          outMoves.addAll(continuingResults)
        } else {
          outMoves.addAll(terminalResults)
        }
      }
      if (!anyDiagonalCaptured && accSteps.isNotEmpty()) {
        outMoves.add(CheckersMove(accSteps))
      }
    }
  }

  fun applyMove(board: CheckersBoard, move: CheckersMove): CheckersBoard {
    var result = board
    val startPiece = result[move.from] ?: return board
    result = result.withPiece(move.from, null)
    for (step in move.steps) {
      step.captured?.let { capSq ->
        result = result.withPiece(capSq, null)
      }
    }
    val finalKing = startPiece.isKing || move.promotesToKing
    result = result.withPiece(move.to, startPiece.copy(isKing = finalKing))
    return result
  }

  fun applySingleStep(board: CheckersBoard, step: MoveStep): CheckersBoard {
    val piece = board[step.from] ?: return board
    var next = board.withPiece(step.from, null)
    if (step.captured != null) {
      next = next.withPiece(step.captured, null)
    }
    val isKing = piece.isKing || step.promotedToKing
    next = next.withPiece(step.to, piece.copy(isKing = isKing))
    return next
  }

  fun checkWinner(board: CheckersBoard, nextTurn: PieceColor): PieceColor? {
    if (board.countPieces(nextTurn) == 0) return nextTurn.opposite()
    if (getLegalMoves(board, nextTurn).isEmpty()) return nextTurn.opposite()
    return null
  }

  // --- AI Search Infrastructure (Zobrist + TT + Iterative Deepening + Quiescence) ---

  private val ZOBRIST_PIECES: LongArray = LongArray(64 * 4).also { arr ->
    val rng = Random(0x5A17_9E37_79B9L)
    for (i in arr.indices) arr[i] = rng.nextLong()
  }
  private val ZOBRIST_BLACK_TURN: Long = 0x243F_6A88_85A3_08D3L

  private const val TT_EXACT: Byte = 0
  private const val TT_LOWER: Byte = 1
  private const val TT_UPPER: Byte = 2
  private const val INF = 1_000_000
  private const val MATE_SCORE = 100_000

  private class TranspositionTable(bits: Int = 15) {
    private val size = 1 shl bits
    private val mask = size - 1
    private val keys = LongArray(size)
    private val depths = IntArray(size)
    private val scores = IntArray(size)
    private val flags = ByteArray(size)
    private val bestMoves = IntArray(size) // (fromIdx shl 6) or toIdx, -1 if none

    init {
      depths.fill(-1)
      bestMoves.fill(-1)
    }

    fun probeIndex(hash: Long): Int {
      val idx = (hash and mask.toLong()).toInt()
      return if (depths[idx] >= 0 && keys[idx] == hash) idx else -1
    }

    fun depthAt(idx: Int): Int = depths[idx]
    fun scoreAt(idx: Int): Int = scores[idx]
    fun flagAt(idx: Int): Byte = flags[idx]
    fun bestMoveAt(idx: Int): Int = bestMoves[idx]

    fun store(hash: Long, depth: Int, score: Int, flag: Byte, moveKey: Int) {
      val idx = (hash and mask.toLong()).toInt()
      if (depths[idx] <= depth || keys[idx] != hash) {
        keys[idx] = hash
        depths[idx] = depth
        scores[idx] = score
        flags[idx] = flag
        if (moveKey >= 0) bestMoves[idx] = moveKey
      }
    }
  }

  private class SearchContext(
    val deadlineNanos: Long,
    ttBits: Int = 15,
  ) {
    val tt = TranspositionTable(ttBits)
    val killers = Array(32) { IntArray(2) { -1 } }
    var nodes: Int = 0
    var aborted: Boolean = false
    var allowTimeout: Boolean = false

    fun checkTimeout(): Boolean {
      nodes++
      if (allowTimeout && (nodes and 1023) == 0 && System.nanoTime() >= deadlineNanos) {
        aborted = true
      }
      return aborted
    }

    fun recordKiller(ply: Int, moveKey: Int) {
      if (ply in killers.indices && killers[ply][0] != moveKey) {
        killers[ply][1] = killers[ply][0]
        killers[ply][0] = moveKey
      }
    }
  }

  private fun moveKey(move: CheckersMove): Int {
    val f = move.from.row * 8 + move.from.col
    val t = move.to.row * 8 + move.to.col
    return (f shl 6) or t
  }

  private fun computeHash(board: CheckersBoard, turn: PieceColor): Long {
    var h = if (turn == PieceColor.BLACK) ZOBRIST_BLACK_TURN else 0L
    val cells = board.cells
    for (idx in 0..63) {
      val p = cells[idx] ?: continue
      val code = when {
        p.color == PieceColor.WHITE && !p.isKing -> 0
        p.color == PieceColor.WHITE && p.isKing -> 1
        p.color == PieceColor.BLACK && !p.isKing -> 2
        else -> 3
      }
      h = h xor ZOBRIST_PIECES[idx * 4 + code]
    }
    return h
  }

  fun chooseBotMove(
    board: CheckersBoard,
    botColor: PieceColor,
    difficulty: BotDifficulty,
    random: Random = Random.Default,
  ): CheckersMove? {
    val legalMoves = getLegalMoves(board, botColor)
    if (legalMoves.isEmpty()) return null
    if (legalMoves.size == 1) return legalMoves.first()

    return when (difficulty) {
      BotDifficulty.BEGINNER -> {
        // Gentle beginner bot: plays valid moves, occasionally picks a center/capture move
        if (random.nextFloat() < 0.45f) {
          legalMoves.random(random)
        } else {
          val scored = legalMoves.map { it to evaluateMoveShallow(board, it, botColor) }
          val topPool = scored.sortedByDescending { it.second }.take(minOf(3, scored.size))
          topPool.random(random).first
        }
      }
      BotDifficulty.LEARNER -> {
        val ranked = searchIterative(board, botColor, maxDepth = 3, timeBudgetMs = 220L, ttBits = 13)
        pickFromRanked(ranked, maxDiffCp = 10, random = random)
      }
      BotDifficulty.TACTICIAN -> {
        val ranked = searchIterative(board, botColor, maxDepth = 5, timeBudgetMs = 480L, ttBits = 14)
        val totalPieces = board.countPieces(PieceColor.WHITE) + board.countPieces(PieceColor.BLACK)
        val tolerance = if (totalPieces >= 22) 5 else 0
        pickFromRanked(ranked, maxDiffCp = tolerance, random = random)
      }
      BotDifficulty.GRANDMASTER -> {
        val ranked = searchIterative(board, botColor, maxDepth = 7, timeBudgetMs = 950L, ttBits = 15)
        val totalPieces = board.countPieces(PieceColor.WHITE) + board.countPieces(PieceColor.BLACK)
        val tolerance = if (totalPieces == 24) 2 else 0
        pickFromRanked(ranked, maxDiffCp = tolerance, random = random)
      }
      BotDifficulty.CHAMPION -> {
        val ranked = searchIterative(board, botColor, maxDepth = 9, timeBudgetMs = 1500L, ttBits = 15)
        ranked.first().first
      }
    }
  }

  fun computeBestHint(board: CheckersBoard, playerColor: PieceColor): HintRecommendation? {
    val legalMoves = getLegalMoves(board, playerColor)
    if (legalMoves.isEmpty()) return null
    val bestMove = if (legalMoves.size == 1) {
      legalMoves.first()
    } else {
      searchIterative(board, playerColor, maxDepth = 6, timeBudgetMs = 450L, ttBits = 14)
        .first().first
    }
    val explanation = buildHintExplanation(board, bestMove, playerColor)
    return HintRecommendation(move = bestMove, explanation = explanation)
  }

  private fun pickFromRanked(
    ranked: List<Pair<CheckersMove, Int>>,
    maxDiffCp: Int,
    random: Random,
  ): CheckersMove {
    if (ranked.isEmpty()) error("Empty ranked moves")
    val bestScore = ranked.first().second
    if (maxDiffCp <= 0) {
      val exactTies = ranked.takeWhile { it.second == bestScore }
      return exactTies.random(random).first
    }
    val nearBest = ranked.takeWhile { bestScore - it.second <= maxDiffCp }
    return nearBest.random(random).first
  }

  private fun buildHintExplanation(
    board: CheckersBoard,
    move: CheckersMove,
    playerColor: PieceColor,
  ): String {
    val piece = board[move.from]
    return when {
      move.capturedSquares.size >= 3 ->
        "Ого! Целый тройной удар (${move.notationString}) — сруби 3 шашки за один ход!"
      move.capturedSquares.size == 2 ->
        "Двойной удар! Перепрыгни сразу через две шашки: ${move.notationString}."
      move.promotesToKing && move.isCapture ->
        "Удар с прорывом в дамки! Сруби шашку и сразу получи корону на ${move.to.notation}."
      move.promotesToKing ->
        "Прорыв в дамки! Дойди до края доски (${move.to.notation}) и стань сильной дамкой!"
      move.isCapture ->
        "Обязательное взятие: сруби шашку соперника ходом ${move.notationString}."
      piece?.isKing == true ->
        "Отличный манёвр дамкой на ${move.to.notation} — контролируй длинную диагональ!"
      move.to.row in 2..5 && move.to.col in 2..5 ->
        "Займи центр доски ходом ${move.notationString} — отсюда шашка сильнее!"
      else ->
        "Надёжный ход ${move.notationString}, который укрепляет твою позицию."
    }
  }

  private fun evaluateMoveShallow(board: CheckersBoard, move: CheckersMove, color: PieceColor): Int {
    val next = applyMove(board, move)
    return evaluateBoard(next, color)
  }

  private fun searchIterative(
    board: CheckersBoard,
    maximizingColor: PieceColor,
    maxDepth: Int,
    timeBudgetMs: Long,
    ttBits: Int,
  ): List<Pair<CheckersMove, Int>> {
    val legalMoves = getLegalMoves(board, maximizingColor)
    if (legalMoves.size <= 1) return legalMoves.map { it to 0 }

    val deadline = System.nanoTime() + timeBudgetMs * 1_000_000L
    val ctx = SearchContext(deadline, ttBits)

    var orderedRoot = legalMoves.sortedByDescending {
      it.capturedSquares.size * 1000 + (if (it.promotesToKing) 500 else 0)
    }
    var lastCompletedScores: List<Pair<CheckersMove, Int>> = orderedRoot.map { it to 0 }

    for (depth in 1..maxDepth) {
      ctx.aborted = false
      // Depth 1 must always complete cleanly so we never return an unsearched move
      ctx.allowTimeout = depth > 1

      val iterationResults = ArrayList<Pair<CheckersMove, Int>>(orderedRoot.size)
      var alpha = -INF
      val beta = INF

      for (move in orderedRoot) {
        val nextBoard = applyMove(board, move)
        val score = -negamax(
          board = nextBoard,
          depth = depth - 1,
          ply = 1,
          alpha = -beta,
          beta = -alpha,
          turn = maximizingColor.opposite(),
          ctx = ctx,
        )
        if (ctx.aborted) break
        iterationResults.add(move to score)
        if (score > alpha) alpha = score
      }

      if (ctx.aborted) break

      val sorted = iterationResults.sortedByDescending { it.second }
      lastCompletedScores = sorted
      orderedRoot = sorted.map { it.first }

      // Early exit if a forced win is already found
      if (sorted.first().second >= MATE_SCORE - 100) break
      if (System.nanoTime() >= deadline) break
    }

    return lastCompletedScores
  }

  private fun negamax(
    board: CheckersBoard,
    depth: Int,
    ply: Int,
    alpha: Int,
    beta: Int,
    turn: PieceColor,
    ctx: SearchContext,
  ): Int {
    if (ctx.checkTimeout()) return 0

    val originalAlpha = alpha
    val hash = computeHash(board, turn)
    val ttIdx = ctx.tt.probeIndex(hash)
    var ttMoveKey = -1

    if (ttIdx >= 0) {
      ttMoveKey = ctx.tt.bestMoveAt(ttIdx)
      if (ctx.tt.depthAt(ttIdx) >= depth) {
        val ttScore = ctx.tt.scoreAt(ttIdx)
        when (ctx.tt.flagAt(ttIdx)) {
          TT_EXACT -> return ttScore
          TT_LOWER -> if (ttScore >= beta) return ttScore
          TT_UPPER -> if (ttScore <= alpha) return ttScore
        }
      }
    }

    val legalMoves = getLegalMoves(board, turn)
    if (legalMoves.isEmpty()) {
      return -MATE_SCORE + ply
    }

    if (depth <= 0) {
      return quiescence(board, ply, alpha, beta, turn, qDepth = 8, ctx = ctx)
    }

    // Extension: single forced move (especially forced capture chains) gets free depth!
    val extension = if (legalMoves.size == 1 && depth < 10) 1 else 0
    val nextDepth = depth - 1 + extension

    val orderedMoves = orderMoves(legalMoves, ttMoveKey, ply, ctx)

    var a = alpha
    var bestScore = -INF
    var bestMoveKey = -1

    for (move in orderedMoves) {
      val nextBoard = applyMove(board, move)
      val score = -negamax(
        board = nextBoard,
        depth = nextDepth,
        ply = ply + 1,
        alpha = -beta,
        beta = -a,
        turn = turn.opposite(),
        ctx = ctx,
      )
      if (ctx.aborted) return 0

      if (score > bestScore) {
        bestScore = score
        bestMoveKey = moveKey(move)
      }
      if (score > a) a = score
      if (a >= beta) {
        if (!move.isCapture) ctx.recordKiller(ply, bestMoveKey)
        break
      }
    }

    val flag = when {
      bestScore <= originalAlpha -> TT_UPPER
      bestScore >= beta -> TT_LOWER
      else -> TT_EXACT
    }
    ctx.tt.store(hash, depth, bestScore, flag, bestMoveKey)
    return bestScore
  }

  private fun quiescence(
    board: CheckersBoard,
    ply: Int,
    alpha: Int,
    beta: Int,
    turn: PieceColor,
    qDepth: Int,
    ctx: SearchContext,
  ): Int {
    if (ctx.checkTimeout()) return 0

    val legalMoves = getLegalMoves(board, turn)
    if (legalMoves.isEmpty()) {
      return -MATE_SCORE + ply
    }

    val isForcedCapture = legalMoves.first().isCapture
    if (!isForcedCapture || qDepth <= 0) {
      return evaluateBoard(board, turn)
    }

    // Captures are mandatory in Russian Checkers: NO stand-pat allowed!
    val ordered = if (legalMoves.size > 1) {
      legalMoves.sortedByDescending {
        it.capturedSquares.size * 100 + (if (it.promotesToKing) 50 else 0)
      }
    } else {
      legalMoves
    }

    var a = alpha
    var bestScore = -INF
    for (move in ordered) {
      val nextBoard = applyMove(board, move)
      val score = -quiescence(
        board = nextBoard,
        ply = ply + 1,
        alpha = -beta,
        beta = -a,
        turn = turn.opposite(),
        qDepth = qDepth - 1,
        ctx = ctx,
      )
      if (ctx.aborted) return 0
      if (score > bestScore) bestScore = score
      if (score > a) a = score
      if (a >= beta) break
    }
    return bestScore
  }

  private fun orderMoves(
    moves: List<CheckersMove>,
    ttMoveKey: Int,
    ply: Int,
    ctx: SearchContext,
  ): List<CheckersMove> {
    if (moves.size <= 1) return moves
    val k0 = if (ply in ctx.killers.indices) ctx.killers[ply][0] else -1
    val k1 = if (ply in ctx.killers.indices) ctx.killers[ply][1] else -1

    return moves.sortedByDescending { move ->
      val key = moveKey(move)
      var priority = 0
      if (key == ttMoveKey) priority += 10_000_000
      if (move.isCapture) priority += 1_000_000 + move.capturedSquares.size * 10_000
      if (move.promotesToKing) priority += 500_000
      if (key == k0) priority += 40_000
      else if (key == k1) priority += 20_000
      // Prefer central destinations in ordering
      if (move.to.col in 2..5 && move.to.row in 2..5) priority += 50
      priority
    }
  }

  fun evaluateBoard(board: CheckersBoard, perspective: PieceColor): Int {
    val cells = board.cells
    var whiteMen = 0
    var blackMen = 0
    var whiteKings = 0
    var blackKings = 0

    for (i in 0..63) {
      val p = cells[i] ?: continue
      if (p.color == PieceColor.WHITE) {
        if (p.isKing) whiteKings++ else whiteMen++
      } else {
        if (p.isKing) blackKings++ else blackMen++
      }
    }

    val whiteTotal = whiteMen + whiteKings
    val blackTotal = blackMen + blackKings
    if (whiteTotal == 0) return if (perspective == PieceColor.WHITE) -MATE_SCORE else MATE_SCORE
    if (blackTotal == 0) return if (perspective == PieceColor.BLACK) -MATE_SCORE else MATE_SCORE

    var whiteScore = 0
    var blackScore = 0
    var whiteLeftWing = 0
    var whiteRightWing = 0
    var blackLeftWing = 0
    var blackRightWing = 0

    for (r in 0..7) {
      for (c in 0..7) {
        val p = cells[r * 8 + c] ?: continue
        if (p.color == PieceColor.WHITE) {
          if (c <= 3) whiteLeftWing++ else whiteRightWing++
        } else {
          if (c <= 3) blackLeftWing++ else blackRightWing++
        }

        var pos = 0
        if (!p.isKing) {
          pos += 100
          val advancement = if (p.color == PieceColor.WHITE) (7 - r) else r
          pos += advancement * 6

          // Threat of promotion on next row when clear
          if (advancement == 6) {
            val promoRow = if (p.color == PieceColor.WHITE) 0 else 7
            var promoClear = false
            for (dc in listOf(-1, 1)) {
              val nc = c + dc
              if (nc in 0..7 && cells[promoRow * 8 + nc] == null) {
                promoClear = true
                break
              }
            }
            if (promoClear) pos += 24
          }

          // Golden back-rank defense while opponent still has men
          if (p.color == PieceColor.WHITE && r == 7 && blackMen >= 3) {
            pos += if (c == 2 || c == 4) 20 else 10 // c1, e1 golden defenders
          } else if (p.color == PieceColor.BLACK && r == 0 && whiteMen >= 3) {
            pos += if (c == 3 || c == 5) 20 else 10 // d8, f8 golden defenders
          }

          // Diagonal support chain from friendly pieces behind
          val backRow = if (p.color == PieceColor.WHITE) r + 1 else r - 1
          var defenders = 0
          if (backRow in 0..7) {
            if (c - 1 >= 0 && cells[backRow * 8 + (c - 1)]?.color == p.color) defenders++
            if (c + 1 <= 7 && cells[backRow * 8 + (c + 1)]?.color == p.color) defenders++
          }
          pos += when (defenders) {
            2 -> 18
            1 -> 11
            else -> if (advancement in 4..5) -10 else 0
          }

          // Safe forward mobility hint
          val fwdRow = if (p.color == PieceColor.WHITE) r - 1 else r + 1
          if (fwdRow in 0..7) {
            if (c - 1 >= 0 && cells[fwdRow * 8 + (c - 1)] == null) pos += 3
            if (c + 1 <= 7 && cells[fwdRow * 8 + (c + 1)] == null) pos += 3
          }

          // Inner center / outer center / edge penalty
          if (r in 3..4 && c in 2..5) {
            pos += 18
          } else if (r in 2..5 && c in 2..5) {
            pos += 10
          } else if (c == 0 || c == 7) {
            pos -= 9
          }
        } else {
          pos += 360
          val centerDist = abs(r - 3.5) + abs(c - 3.5)
          pos += ((7.0 - centerDist) * 6).toInt()

          // Main diagonal a1-h8 (r + c == 7) & double diagonals (|r + c - 7| == 2)
          val mainDiagDiff = abs(r + c - 7)
          if (mainDiagDiff == 0) pos += 28
          else if (mainDiagDiff == 2) pos += 14

          // King mobility along rays
          var openRays = 0
          for ((dr, dc) in ALL_DIAGONALS) {
            var rr = r + dr
            var cc = c + dc
            var steps = 0
            while (rr in 0..7 && cc in 0..7 && cells[rr * 8 + cc] == null && steps < 4) {
              openRays++
              steps++
              rr += dr
              cc += dc
            }
          }
          pos += openRays * 2
        }

        if (p.color == PieceColor.WHITE) whiteScore += pos else blackScore += pos
      }
    }

    // Wing balance penalty when many pieces remain on board
    if (whiteTotal >= 6 && abs(whiteLeftWing - whiteRightWing) >= 4) whiteScore -= 10
    if (blackTotal >= 6 && abs(blackLeftWing - blackRightWing) >= 4) blackScore -= 10

    val rawDiff = if (perspective == PieceColor.WHITE) whiteScore - blackScore else blackScore - whiteScore

    // Trade-down bonus when ahead in material: simplify the position!
    val myMat = if (perspective == PieceColor.WHITE) whiteMen * 100 + whiteKings * 360 else blackMen * 100 + blackKings * 360
    val opMat = if (perspective == PieceColor.WHITE) blackMen * 100 + blackKings * 360 else whiteMen * 100 + whiteKings * 360
    val opCount = if (perspective == PieceColor.WHITE) blackTotal else whiteTotal
    val tradeBonus = if (myMat - opMat >= 100) (12 - opCount) * 6 else 0

    return rawDiff + tradeBonus
  }
}
