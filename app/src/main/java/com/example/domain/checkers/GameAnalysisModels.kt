package com.example.domain.checkers

enum class TimeControlOption(
  val label: String,
  val initialSeconds: Int,
  val incrementSeconds: Int,
) {
  NONE("Без часов", 0, 0),
  RAPID_5_3("Рапид 5+3", 5 * 60, 3),
  BLITZ_3_2("Блиц 3+2", 3 * 60, 2),
}

enum class PlayerColorPreference(val label: String) {
  WHITE("Белыми"),
  BLACK("Чёрными"),
  RANDOM("Случайно"),
}

enum class MoveQuality(val label: String) {
  BEST("Точный ход"),
  GOOD("Хорошо"),
  INACCURACY("Неточность"),
  MISTAKE("Ошибка"),
  BLUNDER("Зевок"),
}

data class RecordedGameMove(
  val plyIndex: Int,
  val moveNumber: Int,
  val mover: PieceColor,
  val move: CheckersMove,
  val boardBefore: CheckersBoard,
  val boardAfter: CheckersBoard,
)

data class AnalyzedMoveEntry(
  val recorded: RecordedGameMove,
  val whiteEvalBefore: Int,
  val whiteEvalAfter: Int,
  val bestAlternative: CheckersMove?,
  val evalLossCp: Int,
  val quality: MoveQuality,
)

object GameAnalyzer {
  /**
   * Analyzes every move in a completed game from the human player's perspective.
   */
  fun analyzeHistory(
    moves: List<RecordedGameMove>,
    humanColor: PieceColor,
  ): List<AnalyzedMoveEntry> {
    if (moves.isEmpty()) return emptyList()
    return moves.map { rec ->
      val bestForMover = if (rec.mover == humanColor) {
        RussianCheckersEngine.computeBestHint(rec.boardBefore, rec.mover)?.move
      } else {
        null
      }
      val whiteBefore = RussianCheckersEngine.evaluatePositionQuick(rec.boardBefore, rec.mover)
      val whiteAfter = RussianCheckersEngine.evaluatePositionQuick(rec.boardAfter, rec.mover.opposite())
      val bestBoardAfter = if (bestForMover != null && bestForMover.notationString != rec.move.notationString) {
        RussianCheckersEngine.applyMove(rec.boardBefore, bestForMover)
      } else {
        null
      }
      val bestWhiteEval = if (bestBoardAfter != null) {
        RussianCheckersEngine.evaluatePositionQuick(bestBoardAfter, rec.mover.opposite())
      } else {
        whiteAfter
      }

      val sign = if (rec.mover == PieceColor.WHITE) 1 else -1
      val rawLoss = ((bestWhiteEval - whiteAfter) * sign).coerceAtLeast(0)
      val quality = when {
        bestForMover == null || bestForMover.notationString == rec.move.notationString || rawLoss < 20 -> MoveQuality.BEST
        rawLoss < 55 -> MoveQuality.GOOD
        rawLoss < 110 -> MoveQuality.INACCURACY
        rawLoss < 220 -> MoveQuality.MISTAKE
        else -> MoveQuality.BLUNDER
      }

      AnalyzedMoveEntry(
        recorded = rec,
        whiteEvalBefore = whiteBefore,
        whiteEvalAfter = whiteAfter,
        bestAlternative = bestForMover?.takeIf { it.notationString != rec.move.notationString && rawLoss >= 35 },
        evalLossCp = rawLoss,
        quality = quality,
      )
    }
  }
}
