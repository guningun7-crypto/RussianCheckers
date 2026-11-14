package com.example.domain.checkers

import kotlin.random.Random

data class FlyingOpeningSetup(
  val id: String,
  val name: String,
  val whiteMove: String,
  val blackMove: String,
)

object OpeningBook {

  data class BookLine(
    val name: String,
    val moves: List<String>,
  )

  val bookLines: List<BookLine> = listOf(
    BookLine("Городская партия", listOf("c3–d4", "d6–c5", "b2–c3", "c7–d6", "c3–b4", "b6–a5")),
    BookLine("Городская партия (размен)", listOf("c3–d4", "d6–c5", "b2–c3", "f6–e5", "d4:f6", "e7:g5")),
    BookLine("Косяк", listOf("c3–b4", "f6–g5", "g3–f4", "g7–f6", "b2–c3", "b6–a5")),
    BookLine("Обратный косяк", listOf("c3–b4", "f6–e5", "e3–f4", "b6–a5", "b2–c3", "g7–f6")),
    BookLine("Игра Бодянского", listOf("a3–b4", "h6–g5", "b2–a3", "g7–h6", "c3–d4", "d6–c5")),
    BookLine("Дебют «Кол»", listOf("c3–d4", "f6–g5", "d4–c5", "b6:d4", "e3:c5", "d6:b4")),
    BookLine("Обратный тычок", listOf("g3–f4", "f6–e5", "c3–d4", "e5:c3", "b2:d4", "d6–c5")),
    BookLine("Отыгрыш", listOf("c3–d4", "f6–e5", "d4:f6", "g7:e5", "b2–c3", "e7–f6")),
    BookLine("Классический центр", listOf("e3–d4", "d6–c5", "d2–e3", "c7–d6", "c3–b4", "f6–e5")),
    BookLine("Фланговое развитие", listOf("g3–h4", "b6–a5", "f2–g3", "a5–b4", "a3:c5", "d6:b4")),
  )

  val flyingOpenings: List<FlyingOpeningSetup> = listOf(
    FlyingOpeningSetup("fly_city", "Городская партия", "c3–d4", "d6–c5"),
    FlyingOpeningSetup("fly_kosyak", "Дебют «Косяк»", "c3–b4", "f6–g5"),
    FlyingOpeningSetup("fly_bodyansky", "Игра Бодянского", "a3–b4", "h6–g5"),
    FlyingOpeningSetup("fly_kol", "Дебют «Кол»", "c3–d4", "f6–g5"),
    FlyingOpeningSetup("fly_tychek", "Обратный тычок", "g3–f4", "f6–e5"),
    FlyingOpeningSetup("fly_wing", "Бортовой гамбит", "g3–h4", "b6–a5"),
    FlyingOpeningSetup("fly_center", "Встречный центр", "e3–d4", "b6–c5"),
    FlyingOpeningSetup("fly_cross", "Перекрёсток", "e3–f4", "d6–c5"),
  )

  private data class BookKey(
    val cellsHash: Int,
    val turn: PieceColor,
  )

  private val positionBook: Map<BookKey, List<Pair<String, String>>> by lazy {
    val map = mutableMapOf<BookKey, MutableList<Pair<String, String>>>()
    for (line in bookLines) {
      var board = CheckersBoard.initial()
      var turn = PieceColor.WHITE
      for (notation in line.moves) {
        val legal = RussianCheckersEngine.getLegalMoves(board, turn)
        val found = legal.firstOrNull { it.notationString == notation } ?: break
        val key = BookKey(board.cells.hashCode(), turn)
        val list = map.getOrPut(key) { mutableListOf() }
        if (list.none { it.first == notation }) {
          list.add(notation to line.name)
        }
        board = RussianCheckersEngine.applyMove(board, found)
        turn = turn.opposite()
      }
    }
    map
  }

  fun lookupBookMove(
    board: CheckersBoard,
    turn: PieceColor,
    random: Random = Random.Default,
  ): CheckersMove? {
    val key = BookKey(board.cells.hashCode(), turn)
    val candidates = positionBook[key] ?: return null
    val legal = RussianCheckersEngine.getLegalMoves(board, turn)
    val matching = candidates.mapNotNull { (notation, _) ->
      legal.firstOrNull { it.notationString == notation }
    }
    return if (matching.isEmpty()) null else matching.random(random)
  }

  fun detectOpeningName(playedNotations: List<String>): String? {
    if (playedNotations.isEmpty()) return null
    val firstTwo = playedNotations.take(2)
    return bookLines.firstOrNull { line ->
      line.moves.size >= firstTwo.size && line.moves.take(firstTwo.size) == firstTwo
    }?.name ?: flyingOpenings.firstOrNull {
      firstTwo.size >= 2 && it.whiteMove == firstTwo[0] && it.blackMove == firstTwo[1]
    }?.name
  }

  fun applyFlyingOpening(setup: FlyingOpeningSetup): Pair<CheckersBoard, List<CheckersMove>>? {
    var board = CheckersBoard.initial()
    val wLegal = RussianCheckersEngine.getLegalMoves(board, PieceColor.WHITE)
    val wMove = wLegal.firstOrNull { it.notationString == setup.whiteMove } ?: return null
    board = RussianCheckersEngine.applyMove(board, wMove)
    val bLegal = RussianCheckersEngine.getLegalMoves(board, PieceColor.BLACK)
    val bMove = bLegal.firstOrNull { it.notationString == setup.blackMove } ?: return null
    board = RussianCheckersEngine.applyMove(board, bMove)
    return board to listOf(wMove, bMove)
  }
}
