package com.example.domain.curriculum

import com.example.domain.checkers.CheckersBoard
import com.example.domain.checkers.CheckersMove
import com.example.domain.checkers.PieceColor
import com.example.domain.checkers.RussianCheckersEngine
import org.json.JSONArray
import org.json.JSONObject

data class AppReleaseInfo(
  val versionCode: Int,
  val versionName: String,
  val changelog: String,
  val downloadUrl: String,
)

data class ParsedContentPack(
  val packTitle: String,
  val releaseInfo: AppReleaseInfo?,
  val puzzles: List<LogicPuzzle>,
  val lessons: List<InteractiveLesson>,
)

object ContentPackParser {

  fun parse(jsonText: String): Result<ParsedContentPack> {
    return try {
      val root = JSONObject(jsonText.trim())
      val packTitle = root.optString("packTitle", "Пакет обновлений").ifBlank { "Пакет обновлений" }

      val downloadUrl = root.optNonNullString("downloadUrl")
      val releaseInfo = if (root.has("versionCode") && !root.isNull("versionCode") && downloadUrl != null) {
        AppReleaseInfo(
          versionCode = root.getInt("versionCode"),
          versionName = root.optNonNullString("versionName") ?: "1.0",
          changelog = root.optNonNullString("changelog") ?: "Новые возможности и улучшения.",
          downloadUrl = downloadUrl,
        )
      } else {
        null
      }

      val parsedPuzzles = mutableListOf<LogicPuzzle>()
      val puzzlesArr = root.optJSONArray("puzzles") ?: JSONArray()
      for (i in 0 until puzzlesArr.length()) {
        val obj = puzzlesArr.getJSONObject(i)
        parsedPuzzles.add(parsePuzzle(obj, i))
      }

      val parsedLessons = mutableListOf<InteractiveLesson>()
      val lessonsArr = root.optJSONArray("lessons") ?: JSONArray()
      for (i in 0 until lessonsArr.length()) {
        val obj = lessonsArr.getJSONObject(i)
        parsedLessons.add(parseLesson(obj, i))
      }

      if (releaseInfo == null && parsedPuzzles.isEmpty() && parsedLessons.isEmpty()) {
        return Result.failure(IllegalArgumentException("В файле не найдено ни задач, ни уроков, ни информации об обновлении."))
      }

      Result.success(
        ParsedContentPack(
          packTitle = packTitle,
          releaseInfo = releaseInfo,
          puzzles = parsedPuzzles,
          lessons = parsedLessons,
        )
      )
    } catch (e: Exception) {
      Result.failure(IllegalArgumentException(e.message ?: "Неверный формат JSON"))
    }
  }

  private fun parsePuzzle(obj: JSONObject, index: Int): LogicPuzzle {
    val id = obj.optString("id", "custom_puzzle_${System.currentTimeMillis()}_$index")
    val title = obj.getString("title")
    val themeTag = obj.optString("themeTag", "Логика")
    val diffName = obj.optString("difficulty", "EASY").uppercase()
    val difficulty = PuzzleDifficulty.entries.firstOrNull { it.name == diffName } ?: PuzzleDifficulty.EASY
    val prompt = obj.getString("prompt")
    val initialRows = parseBoardRows(obj.getJSONArray("initialRows"), "задачи «$title»")
    val successExplanation = obj.optString("successExplanation", "Молодец! Задача решена верно!")

    val stepsArr = obj.getJSONArray("steps")
    require(stepsArr.length() > 0) { "У задачи «$title» должен быть хотя бы 1 шаг." }

    var simBoard = CheckersBoard.fromStrings(initialRows)
    val steps = mutableListOf<PuzzleStep>()

    for (s in 0 until stepsArr.length()) {
      val stepObj = stepsArr.getJSONObject(s)
      val acceptedArr = stepObj.getJSONArray("acceptedNotations")
      val acceptedSet = mutableSetOf<String>()
      for (k in 0 until acceptedArr.length()) {
        acceptedSet.add(normalizeNotation(acceptedArr.getString(k)))
      }
      require(acceptedSet.isNotEmpty()) { "В задаче «$title» не указан правильный ход." }

      val legalMoves = RussianCheckersEngine.getLegalMoves(simBoard, PieceColor.WHITE)
      val hintMove = legalMoves.firstOrNull { it.notationString in acceptedSet }
        ?: throw IllegalArgumentException(
          "Ход ${acceptedSet.first()} в задаче «$title» (шаг ${s + 1}) не соответствует правилам русских шашек. Доступные ходы: ${legalMoves.joinToString { it.notationString }}"
        )

      simBoard = RussianCheckersEngine.applyMove(simBoard, hintMove)

      val replyNotation = stepObj.optNonNullString("botAutoReply")?.let { normalizeNotation(it) }
      var botReplyMove: CheckersMove? = null
      if (replyNotation != null) {
        val botLegal = RussianCheckersEngine.getLegalMoves(simBoard, PieceColor.BLACK)
        botReplyMove = botLegal.firstOrNull { it.notationString == replyNotation }
          ?: throw IllegalArgumentException(
            "Ответный ход $replyNotation в задаче «$title» не соответствует правилам."
          )
        simBoard = RussianCheckersEngine.applyMove(simBoard, botReplyMove)
      }

      steps.add(
        PuzzleStep(
          acceptedNotations = acceptedSet,
          hintMove = hintMove,
          hintText = stepObj.optNonNullString("hintText") ?: "Посмотри на золотую стрелку подсказки!",
          botAutoReply = botReplyMove,
          replyCaption = stepObj.optNonNullString("replyCaption"),
        )
      )
    }

    return LogicPuzzle(
      id = id,
      order = obj.optInt("order", 100 + index),
      title = title,
      themeTag = themeTag,
      difficulty = difficulty,
      prompt = prompt,
      initialRows = initialRows,
      steps = steps,
      successExplanation = successExplanation,
    )
  }

  fun parseSinglePuzzle(json: String): LogicPuzzle? = runCatching {
    parsePuzzle(JSONObject(json), 0)
  }.getOrNull()

  fun parseSingleLesson(json: String): InteractiveLesson? = runCatching {
    parseLesson(JSONObject(json), 0)
  }.getOrNull()

  private fun parseLesson(obj: JSONObject, index: Int): InteractiveLesson {
    val id = obj.optString("id", "custom_lesson_${System.currentTimeMillis()}_$index")
    val title = obj.getString("title")
    val subtitle = obj.optString("subtitle", "Новый интерактивный урок")
    val catName = obj.optString("category", "STRATEGY").uppercase()
    val category = LessonCategory.entries.firstOrNull { it.name == catName } ?: LessonCategory.STRATEGY
    val shortRule = obj.getString("shortRule")
    val mascotTip = obj.optString("mascotTip", "Внимательно посмотри на доску и попробуй сам!")

    val demoRows = parseBoardRows(obj.getJSONArray("demoInitialRows"), "демо урока «$title»")
    var demoSim = CheckersBoard.fromStrings(demoRows)
    val demoStepsArr = obj.optJSONArray("demoSteps") ?: JSONArray()
    val demoSteps = mutableListOf<DemoStep>()
    for (i in 0 until demoStepsArr.length()) {
      val sObj = demoStepsArr.getJSONObject(i)
      val caption = sObj.getString("caption")
      val moveNotation = sObj.optNonNullString("move")?.let { normalizeNotation(it) }
      var moveObj: CheckersMove? = null
      if (moveNotation != null) {
        val fromStr = moveNotation.substring(0, 2)
        val fromSq = com.example.domain.checkers.Square.fromNotation(fromStr)
        val color = demoSim[fromSq]?.color ?: PieceColor.WHITE
        val legal = RussianCheckersEngine.getLegalMoves(demoSim, color)
        moveObj = legal.firstOrNull { it.notationString == moveNotation }
          ?: throw IllegalArgumentException("Демо-ход $moveNotation в уроке «$title» невозможен по правилам.")
        demoSim = RussianCheckersEngine.applyMove(demoSim, moveObj)
      }
      demoSteps.add(DemoStep(caption = caption, move = moveObj))
    }

    val exRows = parseBoardRows(obj.getJSONArray("exerciseInitialRows"), "упражнения урока «$title»")
    val exBoard = CheckersBoard.fromStrings(exRows)
    val acceptedArr = obj.getJSONArray("acceptedMoveNotations")
    val acceptedSet = mutableSetOf<String>()
    for (k in 0 until acceptedArr.length()) {
      acceptedSet.add(normalizeNotation(acceptedArr.getString(k)))
    }
    val exLegal = RussianCheckersEngine.getLegalMoves(exBoard, PieceColor.WHITE)
    val hintMove = exLegal.firstOrNull { it.notationString in acceptedSet }
      ?: throw IllegalArgumentException("Ход ${acceptedSet.firstOrNull()} в упражнении урока «$title» невозможен по правилам.")

    return InteractiveLesson(
      id = id,
      order = obj.optInt("order", 100 + index),
      title = title,
      subtitle = subtitle,
      category = category,
      shortRule = shortRule,
      mascotTip = mascotTip,
      demoInitialRows = demoRows,
      demoSteps = demoSteps,
      exercisePrompt = obj.getString("exercisePrompt"),
      exerciseInitialRows = exRows,
      acceptedMoveNotations = acceptedSet,
      hintMove = hintMove,
      hintText = obj.optString("hintText", "Следуй золотой стрелке!"),
      successFeedback = obj.optString("successFeedback", "Отлично! Упражнение выполнено!"),
      suboptimalMoveHint = obj.optString("suboptimalMoveHint", "Попробуй найти более точный ход по правилу урока!"),
      starsReward = obj.optInt("starsReward", 2).coerceIn(1, 5),
    )
  }

  fun parseBoardRows(arr: JSONArray, contextName: String): List<String> {
    require(arr.length() == 8) { "Доска для $contextName должна содержать ровно 8 строк." }
    val rows = mutableListOf<String>()
    for (r in 0 until 8) {
      val rowStr = arr.getString(r)
      require(rowStr.length == 8) { "Строка ${r + 1} доски для $contextName должна содержать ровно 8 символов." }
      rows.add(rowStr)
    }
    return rows
  }

  /** Accepts '-' or '–' for simple moves and ':' for captures. */
  fun normalizeNotation(raw: String): String {
    return raw.trim().lowercase().replace('-', '–')
  }

  private fun JSONObject.optNonNullString(key: String): String? {
    if (!has(key) || isNull(key)) return null
    return optString(key, "").trim().takeIf { it.isNotEmpty() && !it.equals("null", ignoreCase = true) }
  }

  fun serializeSinglePuzzle(puzzle: LogicPuzzle): String {
    val root = JSONObject()
    root.put("id", puzzle.id)
    root.put("order", puzzle.order)
    root.put("title", puzzle.title)
    root.put("themeTag", puzzle.themeTag)
    root.put("difficulty", puzzle.difficulty.name)
    root.put("prompt", puzzle.prompt)
    root.put("initialRows", JSONArray(puzzle.initialRows))
    root.put("successExplanation", puzzle.successExplanation)

    val stepsArr = JSONArray()
    for (step in puzzle.steps) {
      val sObj = JSONObject()
      sObj.put("acceptedNotations", JSONArray(step.acceptedNotations.toList()))
      sObj.put("hintText", step.hintText)
      step.botAutoReply?.let { sObj.put("botAutoReply", it.notationString) }
      step.replyCaption?.let { sObj.put("replyCaption", it) }
      stepsArr.put(sObj)
    }
    root.put("steps", stepsArr)
    return root.toString()
  }

  fun serializeSingleLesson(lesson: InteractiveLesson): String {
    val root = JSONObject()
    root.put("id", lesson.id)
    root.put("order", lesson.order)
    root.put("title", lesson.title)
    root.put("subtitle", lesson.subtitle)
    root.put("category", lesson.category.name)
    root.put("shortRule", lesson.shortRule)
    root.put("mascotTip", lesson.mascotTip)
    root.put("demoInitialRows", JSONArray(lesson.demoInitialRows))

    val demoArr = JSONArray()
    for (step in lesson.demoSteps) {
      val dObj = JSONObject()
      dObj.put("caption", step.caption)
      step.move?.let { dObj.put("move", it.notationString) }
      demoArr.put(dObj)
    }
    root.put("demoSteps", demoArr)
    root.put("exercisePrompt", lesson.exercisePrompt)
    root.put("exerciseInitialRows", JSONArray(lesson.exerciseInitialRows))
    root.put("acceptedMoveNotations", JSONArray(lesson.acceptedMoveNotations.toList()))
    root.put("hintText", lesson.hintText)
    root.put("successFeedback", lesson.successFeedback)
    root.put("suboptimalMoveHint", lesson.suboptimalMoveHint)
    root.put("starsReward", lesson.starsReward)
    return root.toString()
  }

  fun sampleTemplateJson(): String = """
{
  "packTitle": "Семейный пакет задач",
  "puzzles": [
    {
      "id": "family_puzzle_1",
      "title": "Секретный прыжок",
      "themeTag": "Двойной удар",
      "difficulty": "EASY",
      "prompt": "Сруби сразу две чёрные шашки одним ходом!",
      "initialRows": [
        "........",
        "........",
        "........",
        "..b.....",
        "........",
        "..b.....",
        ".w......",
        "........"
      ],
      "steps": [
        {
          "acceptedNotations": ["b2:d4:b6"],
          "hintText": "Прыгай с b2 через c3 на d4, а потом через c5 на b6!"
        }
      ],
      "successExplanation": "Здорово! Две шашки срублены за один ход!"
    }
  ]
}
""".trimIndent()

  fun tournamentBonusPackJson(): String = """
{
  "packTitle": "Турнирный бонус: Городская партия и Решето",
  "lessons": [
    {
      "id": "bonus_lesson_city_opening",
      "order": 13,
      "title": "Дебют «Городская партия»",
      "subtitle": "Острая борьба за центр на турнире",
      "category": "TOURNAMENT",
      "shortRule": "Дебют «Городская партия» начинается ходами 1. c3–d4 d6–c5 2. b2–c3. Шашки c5 и d4 стоят вплотную, но не могут бить друг друга, потому что поля b6 и e3 надёжно прикрыты!",
      "mascotTip": "Подводи резерв с b2 на c3, чтобы построить крепкую центральную пирамиду!",
      "demoInitialRows": [
        "........",
        "........",
        ".b.b.b.b",
        "........",
        "........",
        "w.w.w.w.",
        ".w.w.w.w",
        "........"
      ],
      "demoSteps": [
        {
          "caption": "Белые смело занимают центр: 1. c3–d4.",
          "move": "c3-d4"
        },
        {
          "caption": "Чёрные встречают в центре: 1... d6–c5. Бить нельзя — поля сзади прикрыты!",
          "move": "d6-c5"
        },
        {
          "caption": "Белые укрепляют центр резервом: 2. b2–c3!",
          "move": "b2-c3"
        }
      ],
      "exercisePrompt": "Разыграна «Городская партия» (1. c3–d4 d6–c5). Укрепи центральную шашку d4 правильным турнирным ходом с поля b2!",
      "exerciseInitialRows": [
        "........",
        "........",
        ".b...b.b",
        "..b.....",
        "...w....",
        "w...w.w.",
        ".w.w.w.w",
        "........"
      ],
      "acceptedMoveNotations": ["b2-c3"],
      "hintText": "Подтяни шашку с b2 на освободившееся поле c3!",
      "successFeedback": "Отлично! Ты разыграл классический дебют «Городская партия» по всем правилам спортивной школы!",
      "suboptimalMoveHint": "В «Городской партии» важно сразу укрепить поле c3 шашкой с b2.",
      "starsReward": 3
    }
  ],
  "puzzles": [
    {
      "id": "bonus_puzzle_resheto",
      "order": 13,
      "title": "Комбинация «Решето»",
      "themeTag": "Норматив разряда",
      "difficulty": "HARD",
      "prompt": "Экзамен на разряд! Пожертвуй вершину колонки e3–f4, а затем соверши тройной прыжок через всё «решето» чёрных шашек прямо в Дамки!",
      "initialRows": [
        "........",
        "..b.....",
        "........",
        "..b...b.",
        "........",
        "....w...",
        ".....w..",
        "......w."
      ],
      "steps": [
        {
          "acceptedNotations": ["e3-f4"],
          "hintText": "Шаг 1: отдай шашку e3–f4, заманивая чёрную шашку g5 на поле e3!",
          "botAutoReply": "g5:e3",
          "replyCaption": "Чёрные обязаны побить g5:e3 — теперь перед шашкой f2 выстроилось «решето» из трёх шашек!"
        },
        {
          "acceptedNotations": ["f2:d4:b6:d8"],
          "hintText": "Шаг 2: прыгай f2 → d4 → b6 → d8, срубая три шашки и становясь Дамкой!"
        }
      ],
      "successExplanation": "Поздравляем! Ты блестяще провёл комбинацию «Решето» с тройным взятием и прорывом в Дамки!"
    }
  ]
}
""".trimIndent()
}
