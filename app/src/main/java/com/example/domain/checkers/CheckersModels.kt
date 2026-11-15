package com.example.domain.checkers

import androidx.compose.runtime.Immutable

enum class PieceColor {
  WHITE,
  BLACK;

  fun opposite(): PieceColor = if (this == WHITE) BLACK else WHITE
}

@Immutable
data class Piece(
  val color: PieceColor,
  val isKing: Boolean = false,
)

@Immutable
data class Square(val row: Int, val col: Int) {
  val isInBounds: Boolean get() = row in 0..7 && col in 0..7
  val isPlayable: Boolean get() = isInBounds && (row + col) % 2 == 1

  val notation: String
    get() {
      val file = ('a' + col)
      val rank = (8 - row)
      return "$file$rank"
    }

  companion object {
    fun fromNotation(code: String): Square {
      require(code.length == 2) { "Invalid square notation: $code" }
      val col = code[0].lowercaseChar() - 'a'
      val row = 8 - (code[1] - '0')
      return Square(row, col)
    }
  }
}

@Immutable
data class MoveStep(
  val from: Square,
  val to: Square,
  val captured: Square? = null,
  val promotedToKing: Boolean = false,
)

@Immutable
data class CheckersMove(
  val steps: List<MoveStep>,
) {
  val from: Square get() = steps.first().from
  val to: Square get() = steps.last().to
  val capturedSquares: List<Square> get() = steps.mapNotNull { it.captured }
  val isCapture: Boolean get() = capturedSquares.isNotEmpty()
  val promotesToKing: Boolean get() = steps.any { it.promotedToKing }

  val notationString: String
    get() {
      val sep = if (isCapture) ":" else "–"
      val points = listOf(from.notation) + steps.map { it.to.notation }
      return points.joinToString(sep)
    }

  companion object {
    fun simple(fromNotation: String, toNotation: String, promotesToKing: Boolean = false): CheckersMove {
      return CheckersMove(
        listOf(
          MoveStep(
            from = Square.fromNotation(fromNotation),
            to = Square.fromNotation(toNotation),
            captured = null,
            promotedToKing = promotesToKing,
          )
        )
      )
    }

    fun captureChain(vararg notationsAndCaptures: Triple<String, String, String>, promotesFlags: List<Boolean> = emptyList()): CheckersMove {
      val steps = notationsAndCaptures.mapIndexed { index, (from, to, cap) ->
        MoveStep(
          from = Square.fromNotation(from),
          to = Square.fromNotation(to),
          captured = Square.fromNotation(cap),
          promotedToKing = promotesFlags.getOrElse(index) { false },
        )
      }
      return CheckersMove(steps)
    }
  }
}

@Immutable
data class CheckersBoard(
  val cells: List<Piece?> = List(64) { null },
) {
  operator fun get(row: Int, col: Int): Piece? {
    if (row !in 0..7 || col !in 0..7) return null
    return cells[row * 8 + col]
  }

  operator fun get(square: Square): Piece? = get(square.row, square.col)

  fun withPiece(square: Square, piece: Piece?): CheckersBoard {
    val mutable = cells.toMutableList()
    mutable[square.row * 8 + square.col] = piece
    return CheckersBoard(mutable)
  }

  fun countPieces(color: PieceColor): Int = cells.count { it?.color == color }

  fun countKings(color: PieceColor): Int = cells.count { it?.color == color && it.isKing }

  companion object {
    fun initial(): CheckersBoard {
      val grid = MutableList<Piece?>(64) { null }
      for (row in 0..7) {
        for (col in 0..7) {
          if ((row + col) % 2 == 1) {
            when {
              row <= 2 -> grid[row * 8 + col] = Piece(PieceColor.BLACK, isKing = false)
              row >= 5 -> grid[row * 8 + col] = Piece(PieceColor.WHITE, isKing = false)
            }
          }
        }
      }
      return CheckersBoard(grid)
    }

    /**
     * Parses 8 strings of length 8:
     * '.' = empty, 'w' = white man, 'W' = white king, 'b' = black man, 'B' = black king.
     */
    fun fromStrings(rows: List<String>): CheckersBoard {
      require(rows.size == 8) { "Board must have 8 rows" }
      val grid = MutableList<Piece?>(64) { null }
      for (r in 0..7) {
        require(rows[r].length == 8) { "Row $r must have 8 columns" }
        for (c in 0..7) {
          grid[r * 8 + c] = when (rows[r][c]) {
            'w' -> Piece(PieceColor.WHITE, isKing = false)
            'W' -> Piece(PieceColor.WHITE, isKing = true)
            'b' -> Piece(PieceColor.BLACK, isKing = false)
            'B' -> Piece(PieceColor.BLACK, isKing = true)
            else -> null
          }
        }
      }
      return CheckersBoard(grid)
    }
  }
}

enum class BotDifficulty(
  val level: Int,
  val title: String,
  val subtitle: String,
  val starsReward: Int,
  val requiredStarsToUnlock: Int,
  val searchDepth: Int,
) {
  BEGINNER(
    level = 1,
    title = "Малыш-Совёнок",
    subtitle = "Лёгкий уровень для самых первых шагов",
    starsReward = 2,
    requiredStarsToUnlock = 0,
    searchDepth = 1,
  ),
  LEARNER(
    level = 2,
    title = "Любознайка",
    subtitle = "Видит взятия, размены и базовые комбинации (до 3 полуходов)",
    starsReward = 3,
    requiredStarsToUnlock = 0,
    searchDepth = 3,
  ),
  TACTICIAN(
    level = 3,
    title = "Хитрый Тактик",
    subtitle = "Просчитывает жертвы и серии ударов с полным добором взятий",
    starsReward = 4,
    requiredStarsToUnlock = 0,
    searchDepth = 4,
  ),
  GRANDMASTER(
    level = 4,
    title = "Гроссмейстер",
    subtitle = "Глубокий перебор до 6 полуходов, защита золотого поля и точный эндшпиль",
    starsReward = 5,
    requiredStarsToUnlock = 0,
    searchDepth = 6,
  ),
  CHAMPION(
    level = 5,
    title = "Мастер спорта",
    subtitle = "Максимальный интеллект: до 8–10 полуходов, таблица позиций и расчёт форсированных ударов",
    starsReward = 6,
    requiredStarsToUnlock = 0,
    searchDepth = 8,
  ),
}

data class HintRecommendation(
  val move: CheckersMove,
  val explanation: String,
)
