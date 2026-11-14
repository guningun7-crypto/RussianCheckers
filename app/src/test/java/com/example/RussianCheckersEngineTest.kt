package com.example

import com.example.domain.checkers.BotDifficulty
import com.example.domain.checkers.CheckersBoard
import com.example.domain.checkers.PieceColor
import com.example.domain.checkers.RussianCheckersEngine
import com.example.domain.checkers.Square
import com.example.domain.curriculum.LessonCatalog
import com.example.domain.curriculum.PuzzleCatalog
import org.junit.Assert.assertEquals
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
    for (lesson in LessonCatalog.lessons) {
      // Verify exercise accepted moves are all legal
      val exBoard = lesson.exerciseBoard()
      val legalEx = RussianCheckersEngine.getLegalMoves(exBoard, lesson.playerColor)
      val legalNotations = legalEx.map { it.notationString }.toSet()

      assertTrue(
        "Lesson ${lesson.id} hintMove ${lesson.hintMove.notationString} not in legal $legalNotations",
        lesson.hintMove.notationString in legalNotations,
      )
      for (accepted in lesson.acceptedMoveNotations) {
        assertTrue(
          "Lesson ${lesson.id} accepted move $accepted not in legal $legalNotations",
          accepted in legalNotations,
        )
      }

      // Verify demo steps cleanly apply on demoBoard
      var demoBoard = lesson.demoBoard()
      for ((idx, step) in lesson.demoSteps.withIndex()) {
        val m = step.move ?: continue
        val moverColor = demoBoard[m.from]?.color
        assertNotNull("Lesson ${lesson.id} demo step $idx has no piece at ${m.from.notation}", moverColor)
        val legalDemo = RussianCheckersEngine.getLegalMoves(demoBoard, moverColor!!).map { it.notationString }
        assertTrue(
          "Lesson ${lesson.id} demo step $idx move ${m.notationString} not legal in $legalDemo",
          m.notationString in legalDemo,
        )
        demoBoard = RussianCheckersEngine.applyMove(demoBoard, m)
      }
    }
  }

  @Test
  fun allPuzzlesAndBotReplies_are100PercentLegal() {
    for (puzzle in PuzzleCatalog.puzzles) {
      var board = puzzle.initialBoard()
      for ((idx, step) in puzzle.steps.withIndex()) {
        val legal = RussianCheckersEngine.getLegalMoves(board, puzzle.playerColor)
        val legalNotations = legal.map { it.notationString }.toSet()

        assertTrue(
          "Puzzle ${puzzle.id} step $idx hintMove ${step.hintMove.notationString} not in $legalNotations",
          step.hintMove.notationString in legalNotations,
        )
        for (accepted in step.acceptedNotations) {
          assertTrue(
            "Puzzle ${puzzle.id} step $idx accepted $accepted not in $legalNotations",
            accepted in legalNotations,
          )
        }

        val chosenMove = legal.first { it.notationString == step.hintMove.notationString }
        board = RussianCheckersEngine.applyMove(board, chosenMove)

        val reply = step.botAutoReply
        if (reply != null) {
          val botLegal = RussianCheckersEngine.getLegalMoves(board, puzzle.playerColor.opposite())
            .map { it.notationString }
          assertTrue(
            "Puzzle ${puzzle.id} step $idx botReply ${reply.notationString} not in $botLegal",
            reply.notationString in botLegal,
          )
          board = RussianCheckersEngine.applyMove(board, reply)
        }
      }
    }
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
    // Black can make a greedy looking move or avoid a forced 2-piece counter-capture!
    // Board: White at d2(6,3), f2(6,5); Black at d6(2,3) and a7(1,0).
    // Even simpler: Black has b4(4,1) and f6(2,5); White has d4(4,3) and e3(5,4).
    //Wait: test directly on a tactical shot where capturing leads to 2-for-1 loss vs safe move:
    // Black b6(2,1) can move b6-c5 allowing White d4:b6:d8! Or move b6-a5 staying safe.
    val board = CheckersBoard.fromStrings(
      listOf(
        "........",
        "..b.....", // c7 -> jumping from b6-c5 -> White d4:b6:d8! Wait d4(4,3), c5(3,2), b6(2,1)
        ".b......", // b6
        "........",
        "...w....", // d4
        "....w...", // e3
        "........",
        "........",
      )
    )
    // If Black plays b6-c5, White plays d4:b6:d8! (Wait c7 is b, so b6 empty after d4:b6 and c7 -> d8!)
    // Black plays b6-a5 instead! Let's check both moves are legal for Black.
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
  fun puzzleCatalog_contains20PuzzlesIncludingMasterTier() {
    assertTrue(PuzzleCatalog.puzzles.size >= 20)
    assertTrue(
      PuzzleCatalog.puzzles.any { it.difficulty == com.example.domain.curriculum.PuzzleDifficulty.MASTER }
    )
  }
}
