package com.example

import com.example.domain.checkers.BotDifficulty
import com.example.domain.checkers.CheckersBoard
import com.example.domain.checkers.EloRatingCalculator
import com.example.domain.checkers.GameAnalyzer
import com.example.domain.checkers.GameOutcome
import com.example.domain.checkers.OpeningBook
import com.example.domain.checkers.PieceColor
import com.example.domain.checkers.RecordedGameMove
import com.example.domain.checkers.RussianCheckersEngine
import com.example.domain.checkers.Square
import com.example.domain.curriculum.DailyPuzzleHelper
import com.example.domain.curriculum.EndgameCatalog
import com.example.domain.curriculum.LessonCatalog
import com.example.domain.curriculum.LessonFolder
import com.example.domain.curriculum.PuzzleCatalog
import com.example.domain.curriculum.PuzzleDifficulty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RussianCheckersEngineTest {

  @Test
  fun initialBoard_has12WhiteAnd12BlackPieces_and7WhiteOpeningMoves() {
    val board = CheckersBoard.initial()
    assertEquals(12, board.countPieces(PieceColor.WHITE))
    assertEquals(12, board.countPieces(PieceColor.BLACK))

    val legalMoves = RussianCheckersEngine.getLegalMoves(board, PieceColor.WHITE)
    assertEquals(7, legalMoves.size)
    assertTrue(legalMoves.none { it.isCapture })
  }

  @Test
  fun mandatoryCapture_excludesSimpleMoves_andAllowsBackwardCapture() {
    val board = CheckersBoard.fromStrings(
      listOf(
        ".......b",
        "........",
        "........",
        "....w...",
        "...b....",
        "w.......",
        "........",
        "........",
      )
    )
    val moves = RussianCheckersEngine.getLegalMoves(board, PieceColor.WHITE)
    assertEquals(1, moves.size)
    assertEquals("e5:c3", moves.first().notationString)
  }

  @Test
  fun midJumpPromotion_turnsManIntoFlyingKingAndContinuesCapture() {
    val board = CheckersBoard.fromStrings(
      listOf(
        "........",
        "..b.....",
        ".w...b..",
        "........",
        "........",
        "........",
        "........",
        "........",
      )
    )
    val moves = RussianCheckersEngine.getLegalMoves(board, PieceColor.WHITE)
    val notations = moves.map { it.notationString }.toSet()
    assertEquals(setOf("b6:d8:g5", "b6:d8:h4"), notations)
    assertTrue(moves.all { it.promotesToKing })
  }

  @Test
  fun flyingKing_mustChooseLandingSquareThatContinuesCapture() {
    val board = CheckersBoard.fromStrings(
      listOf(
        "........",
        "..b.....",
        "........",
        "........",
        "........",
        "..b.....",
        "........",
        "W.......",
      )
    )
    val moves = RussianCheckersEngine.getLegalMoves(board, PieceColor.WHITE)
    assertEquals(listOf("a1:e5:b8"), moves.map { it.notationString })
  }

  @Test
  fun allLessonDemosAndExercises_are100PercentLegal() {
    assertEquals(47, LessonCatalog.lessons.size)
    assertEquals(17, LessonCatalog.lessons.count { it.folder == LessonFolder.BEGINNER })
    assertEquals(15, LessonCatalog.lessons.count { it.folder == LessonFolder.TACTICIAN })
    assertEquals(15, LessonCatalog.lessons.count { it.folder == LessonFolder.GRANDMASTER })
    assertEquals(47, LessonCatalog.lessons.map { it.id }.toSet().size)
    val lessonErrors = mutableListOf<String>()
    for (lesson in LessonCatalog.lessons) {
      val exBoard = lesson.exerciseBoard()
      val legalEx = RussianCheckersEngine.getLegalMoves(exBoard, lesson.playerColor)
      val legalNotations = legalEx.map { it.notationString }.toSet()

      if (lesson.hintMove.notationString !in legalNotations) {
        lessonErrors.add("Lesson ${lesson.id} hintMove ${lesson.hintMove.notationString} not in legal $legalNotations")
      }
      for (accepted in lesson.acceptedMoveNotations) {
        if (accepted !in legalNotations) {
          lessonErrors.add("Lesson ${lesson.id} accepted move $accepted not in legal $legalNotations")
        }
      }

      var demoBoard = lesson.demoBoard()
      for ((idx, step) in lesson.demoSteps.withIndex()) {
        val m = step.move ?: continue
        val moverColor = demoBoard[m.from]?.color
        if (moverColor == null) {
          lessonErrors.add("Lesson ${lesson.id} demo step $idx has no piece at ${m.from.notation}")
          break
        }
        val legalDemo = RussianCheckersEngine.getLegalMoves(demoBoard, moverColor).map { it.notationString }
        if (m.notationString !in legalDemo) {
          lessonErrors.add("Lesson ${lesson.id} demo step $idx move ${m.notationString} not legal in $legalDemo")
          break
        }
        demoBoard = RussianCheckersEngine.applyMove(demoBoard, m)
      }
    }
    assertTrue(lessonErrors.joinToString("\n"), lessonErrors.isEmpty())
  }

  @Test
  fun allPuzzlesAndBotReplies_are100PercentLegal() {
    val puzzleErrors = mutableListOf<String>()
    for (puzzle in PuzzleCatalog.puzzles) {
      var board = puzzle.initialBoard()
      for ((idx, step) in puzzle.steps.withIndex()) {
        val legal = RussianCheckersEngine.getLegalMoves(board, puzzle.playerColor)
        val legalNotations = legal.map { it.notationString }.toSet()

        if (step.hintMove.notationString !in legalNotations) {
          puzzleErrors.add("Puzzle ${puzzle.id} step $idx hintMove ${step.hintMove.notationString} not in $legalNotations")
          break
        }
        for (accepted in step.acceptedNotations) {
          if (accepted !in legalNotations) {
            puzzleErrors.add("Puzzle ${puzzle.id} step $idx accepted $accepted not in $legalNotations")
          }
        }

        val chosenMove = legal.first { it.notationString == step.hintMove.notationString }
        board = RussianCheckersEngine.applyMove(board, chosenMove)

        val reply = step.botAutoReply
        if (reply != null) {
          val botLegal = RussianCheckersEngine.getLegalMoves(board, puzzle.playerColor.opposite())
            .map { it.notationString }
          if (reply.notationString !in botLegal) {
            puzzleErrors.add("Puzzle ${puzzle.id} step $idx botReply ${reply.notationString} not in $botLegal")
            break
          }
          board = RussianCheckersEngine.applyMove(board, reply)
        }
      }
    }
    assertTrue(puzzleErrors.joinToString("\n"), puzzleErrors.isEmpty())
  }

  @Test
  fun botAndHintEngine_produceValidMoves() {
    val board = CheckersBoard.initial()
    for (diff in BotDifficulty.entries) {
      val move = RussianCheckersEngine.chooseBotMove(board, PieceColor.BLACK, diff)
      assertNotNull("Bot $diff returned null", move)
      val legal = RussianCheckersEngine.getLegalMoves(board, PieceColor.BLACK).map { it.notationString }
      assertTrue(move!!.notationString in legal)
    }

    val hint = RussianCheckersEngine.computeBestHint(board, PieceColor.WHITE)
    assertNotNull(hint)
    assertTrue(hint!!.explanation.isNotBlank())
  }

  @Test
  fun strongAiAvoidsForcedCounterCaptureTrap_andSolvesWithinBudget() {
    val board = CheckersBoard.fromStrings(
      listOf(
        "........",
        "..b.....",
        ".b......",
        "........",
        "...w....",
        "....w...",
        "........",
        "........",
      )
    )
    val legalBlack = RussianCheckersEngine.getLegalMoves(board, PieceColor.BLACK).map { it.notationString }
    assertTrue("b6–c5" in legalBlack)
    assertTrue("b6–a5" in legalBlack)

    val startMs = System.currentTimeMillis()
    val championMove = RussianCheckersEngine.chooseBotMove(board, PieceColor.BLACK, BotDifficulty.CHAMPION)
    val elapsedMs = System.currentTimeMillis() - startMs

    assertNotNull(championMove)
    assertTrue("Champion took too long: ${elapsedMs}ms", elapsedMs < 2500L)
    assertTrue(
      "Champion walked into b6-c5 double-capture trap! Played ${championMove!!.notationString}",
      championMove.notationString != "b6–c5",
    )
  }

  @Test
  fun puzzleCatalog_contains50PuzzlesWithHardAndMasterExpansion() {
    assertEquals(50, PuzzleCatalog.puzzles.size)
    assertEquals(50, PuzzleCatalog.puzzles.map { it.id }.toSet().size)
    assertEquals(24, PuzzleCatalog.puzzles.count { it.difficulty == PuzzleDifficulty.HARD })
    assertEquals(20, PuzzleCatalog.puzzles.count { it.difficulty == PuzzleDifficulty.MASTER })
  }

  @Test
  fun openingBookAndFlyingOpenings_are100PercentLegal() {
    for (line in OpeningBook.bookLines) {
      var board = CheckersBoard.initial()
      var turn = PieceColor.WHITE
      for ((idx, notation) in line.moves.withIndex()) {
        val legal = RussianCheckersEngine.getLegalMoves(board, turn)
        val move = legal.firstOrNull { it.notationString == notation }
        assertNotNull("Opening ${line.name} ply $idx move $notation is illegal!", move)
        board = RussianCheckersEngine.applyMove(board, move!!)
        turn = turn.opposite()
      }
    }

    for (setup in OpeningBook.flyingOpenings) {
      val applied = OpeningBook.applyFlyingOpening(setup)
      assertNotNull("Flying opening ${setup.name} failed to apply!", applied)
      val whiteNext = RussianCheckersEngine.getLegalMoves(applied!!.first, PieceColor.WHITE)
      assertTrue("Flying opening ${setup.name} has no legal white moves", whiteNext.isNotEmpty())
    }

    assertNotNull(OpeningBook.lookupBookMove(CheckersBoard.initial(), PieceColor.WHITE))
  }

  @Test
  fun endgameScenarios_allHaveValidWinningInitialBoardsAndMoves() {
    assertEquals(16, EndgameCatalog.scenarios.size)
    assertEquals(16, EndgameCatalog.scenarios.map { it.id }.toSet().size)
    for (scenario in EndgameCatalog.scenarios) {
      val board = scenario.initialBoard()
      assertTrue(board.countPieces(PieceColor.WHITE) > 0)
      assertTrue(board.countPieces(PieceColor.BLACK) > 0)
      val legal = RussianCheckersEngine.getLegalMoves(board, PieceColor.WHITE)
      assertTrue("Endgame ${scenario.id} has no legal White moves", legal.isNotEmpty())
      val eval = RussianCheckersEngine.evaluatePositionQuick(board, PieceColor.WHITE)
      assertTrue(
        "Endgame ${scenario.id} should have winning White edge, got $eval",
        eval >= 80,
      )
    }

    val endgame3 = EndgameCatalog.findById("endgame_king_man_vs_king")!!
    val eval3 = RussianCheckersEngine.evaluatePositionQuick(endgame3.initialBoard(), PieceColor.WHITE)
    assertTrue("Endgame #3 should be clearly winning (>= 300), got $eval3", eval3 >= 300)
  }

  @Test
  fun eloCalculator_computesValidDeltasAndRanks() {
    val (eloAfterWin, winDelta) = EloRatingCalculator.calculateGameDelta(
      playerElo = 1000,
      opponentElo = 1300,
      outcome = GameOutcome.WIN,
      hintsEnabled = false,
    )
    assertTrue(winDelta > 15)
    assertEquals(1000 + winDelta, eloAfterWin)

    val (_, lossDelta) = EloRatingCalculator.calculateGameDelta(
      playerElo = 1400,
      opponentElo = 1050,
      outcome = GameOutcome.LOSS,
      hintsEnabled = false,
    )
    assertTrue(lossDelta < 0)

    assertEquals("Начинающий шашист", EloRatingCalculator.sportsRankForElo(1000).title)
    assertEquals("Кандидат в мастера (КМС)", EloRatingCalculator.sportsRankForElo(2050).title)
  }

  @Test
  fun boardHash_andGameAnalyzer_andDailyPuzzle_workAccurately() {
    val start = CheckersBoard.initial()
    assertNotEquals(
      RussianCheckersEngine.boardHash(start, PieceColor.WHITE),
      RussianCheckersEngine.boardHash(start, PieceColor.BLACK),
    )

    val firstMove = RussianCheckersEngine.getLegalMoves(start, PieceColor.WHITE).first()
    val afterMove = RussianCheckersEngine.applyMove(start, firstMove)
    val analyzed = GameAnalyzer.analyzeHistory(
      moves = listOf(
        RecordedGameMove(
          plyIndex = 0,
          moveNumber = 1,
          mover = PieceColor.WHITE,
          move = firstMove,
          boardBefore = start,
          boardAfter = afterMove,
        )
      ),
      humanColor = PieceColor.WHITE,
    )
    assertEquals(1, analyzed.size)

    val daily = DailyPuzzleHelper.pickDailyPuzzle(PuzzleCatalog.puzzles, "2026-317")
    assertNotNull(daily)
    assertTrue(daily!!.difficulty == PuzzleDifficulty.HARD || daily.difficulty == PuzzleDifficulty.MASTER)
  }
}
