package com.example

import com.example.domain.curriculum.ContentPackParser
import com.example.domain.curriculum.PuzzleDifficulty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ContentPackParserTest {

  @Test
  fun `sampleTemplateJson parses and round-trips cleanly`() {
    val result = ContentPackParser.parse(ContentPackParser.sampleTemplateJson())
    assertTrue("Sample template should parse: ${result.exceptionOrNull()}", result.isSuccess)
    val pack = result.getOrThrow()
    assertEquals(1, pack.puzzles.size)
    val puzzle = pack.puzzles.first()
    assertEquals("family_puzzle_1", puzzle.id)
    assertEquals(PuzzleDifficulty.EASY, puzzle.difficulty)
    assertEquals("b2:d4:b6", puzzle.steps.first().acceptedNotations.first())

    val serialized = ContentPackParser.serializeSinglePuzzle(puzzle)
    val reparsed = ContentPackParser.parseSinglePuzzle(serialized)
    assertNotNull(reparsed)
    assertEquals(puzzle.title, reparsed?.title)
    assertEquals(puzzle.steps.first().acceptedNotations, reparsed?.steps?.first()?.acceptedNotations)
  }

  @Test
  fun `parses release manifest and custom lesson`() {
    val json = """
      {
        "packTitle": "Весеннее обновление",
        "versionCode": 2,
        "versionName": "1.1",
        "changelog": "Добавлены новые задачи на дамки",
        "downloadUrl": "https://example.com/shashki-1.1.apk",
        "lessons": [
          {
            "id": "custom_lesson_1",
            "title": "Ловля дамки",
            "subtitle": "Секрет угла",
            "category": "STRATEGY",
            "shortRule": "Запри дамку соперника на краю доски.",
            "demoInitialRows": [
              "........",
              "........",
              "........",
              "........",
              "........",
              "........",
              ".w......",
              "........"
            ],
            "demoSteps": [
              { "caption": "Начало", "move": null },
              { "caption": "Ход вперёд", "move": "b2-c3" }
            ],
            "exercisePrompt": "Сделай ход шашкой с b2 на c3",
            "exerciseInitialRows": [
              "........",
              "........",
              "........",
              "........",
              "........",
              "........",
              ".w......",
              "........"
            ],
            "acceptedMoveNotations": ["b2-c3"],
            "hintText": "Иди по диагонали вверх и вправо",
            "successFeedback": "Супер!"
          }
        ]
      }
    """.trimIndent()

    val result = ContentPackParser.parse(json)
    assertTrue("Lesson + release manifest should parse: ${result.exceptionOrNull()}", result.isSuccess)
    val pack = result.getOrThrow()
    assertEquals(2, pack.releaseInfo?.versionCode)
    assertEquals("1.1", pack.releaseInfo?.versionName)
    assertEquals(1, pack.lessons.size)

    val lesson = pack.lessons.first()
    val serialized = ContentPackParser.serializeSingleLesson(lesson)
    val reparsed = ContentPackParser.parseSingleLesson(serialized)
    assertNotNull(reparsed)
    assertEquals("Ловля дамки", reparsed?.title)
    assertEquals("b2–c3", reparsed?.hintMove?.notationString)
  }

  @Test
  fun `rejects illegal puzzle move according to Russian checkers rules`() {
    val json = """
      {
        "packTitle": "Ошибка",
        "puzzles": [
          {
            "title": "Неверный ход",
            "difficulty": "EASY",
            "prompt": "Попытка сделать простой ход, когда есть обязательное взятие",
            "initialRows": [
              "........",
              "........",
              "........",
              "........",
              "........",
              "..b.....",
              ".w......",
              "........"
            ],
            "steps": [
              {
                "acceptedNotations": ["b2-a3"]
              }
            ]
          }
        ]
      }
    """.trimIndent()

    val result = ContentPackParser.parse(json)
    assertTrue("Simple move when capture is mandatory must fail validation", result.isFailure)
  }

  @Test
  fun `tournamentBonusPackJson parses and validates all moves`() {
    val result = ContentPackParser.parse(ContentPackParser.tournamentBonusPackJson())
    assertTrue("Tournament bonus pack should parse: ${result.exceptionOrNull()}", result.isSuccess)
    val pack = result.getOrThrow()
    assertEquals(1, pack.lessons.size)
    assertEquals(1, pack.puzzles.size)
    assertEquals("bonus_lesson_city_opening", pack.lessons.first().id)
    assertEquals("bonus_puzzle_resheto", pack.puzzles.first().id)
  }
}
