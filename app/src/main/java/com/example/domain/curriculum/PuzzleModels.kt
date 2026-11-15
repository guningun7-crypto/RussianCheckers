package com.example.domain.curriculum

import androidx.compose.runtime.Immutable
import com.example.domain.checkers.CheckersBoard
import com.example.domain.checkers.CheckersMove
import com.example.domain.checkers.PieceColor

enum class PuzzleDifficulty(val label: String, val starsReward: Int) {
  EASY("Лёгкая", 2),
  MEDIUM("Средняя", 3),
  HARD("Сложная", 4),
  MASTER("Мастер", 5),
}

@Immutable
data class PuzzleStep(
  val acceptedNotations: Set<String>,
  val hintMove: CheckersMove,
  val hintText: String,
  val botAutoReply: CheckersMove? = null,
  val replyCaption: String? = null,
)

@Immutable
data class LogicPuzzle(
  val id: String,
  val order: Int,
  val title: String,
  val themeTag: String,
  val difficulty: PuzzleDifficulty,
  val prompt: String,
  val initialRows: List<String>,
  val steps: List<PuzzleStep>,
  val successExplanation: String,
  val playerColor: PieceColor = PieceColor.WHITE,
) {
  fun initialBoard(): CheckersBoard = CheckersBoard.fromStrings(initialRows)
}

object PuzzleCatalog {
  private val starterPuzzles: List<LogicPuzzle> = listOf(
    LogicPuzzle(
      id = "puzzle_1_zigzag",
      order = 1,
      title = "Зигзаг удачи",
      themeTag = "Двойной удар",
      difficulty = PuzzleDifficulty.EASY,
      prompt = "Белые начинают и срубают сразу две шашки соперника одним красивым зигзагом!",
      initialRows = listOf(
        "........",
        "........",
        "........",
        "....b...",
        "........",
        "....b...",
        "...w....",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("d2:f4:d6"),
          hintMove = CheckersMove.captureChain(
            Triple("d2", "f4", "e3"),
            Triple("f4", "d6", "e5"),
          ),
          hintText = "Прыгни шашкой d2 направо через e3, а затем сразу налево через e5!",
        )
      ),
      successExplanation = "Отличный зигзаг d2:f4:d6! Ты срубил две шашки за один ход!",
    ),

    LogicPuzzle(
      id = "puzzle_2_back_strike",
      order = 2,
      title = "Удар в тыл",
      themeTag = "Бой назад",
      difficulty = PuzzleDifficulty.EASY,
      prompt = "Чёрные думают, что в безопасности, но русская шашка бьёт и назад! Собери обе шашки!",
      initialRows = listOf(
        "........",
        "........",
        "........",
        "....w...",
        "...b....",
        "........",
        "...b....",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("e5:c3:e1"),
          hintMove = CheckersMove.captureChain(
            Triple("e5", "c3", "d4"),
            Triple("c3", "e1", "d2"),
          ),
          hintText = "Бей шашкой e5 назад через d4 на c3, а затем ещё назад через d2 на e1!",
        )
      ),
      successExplanation = "Супер-удар e5:c3:e1! В русских шашках от боя назад не спрячешься!",
    ),

    LogicPuzzle(
      id = "puzzle_3_promote_mid_jump",
      order = 3,
      title = "Корона на лету!",
      themeTag = "Превращение в бою",
      difficulty = PuzzleDifficulty.MEDIUM,
      prompt = "Если простая шашка во время боя заскакивает на 8-й ряд, она СРАЗУ становится Дамкой и летит бить дальше! Выполни этот трюк!",
      initialRows = listOf(
        "........",
        "..b.....",
        ".w...b..",
        "........",
        "........",
        "........",
        "........",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("b6:d8:g5", "b6:d8:h4"),
          hintMove = CheckersMove.captureChain(
            Triple("b6", "d8", "c7"),
            Triple("d8", "g5", "f6"),
            promotesFlags = listOf(true, false),
          ),
          hintText = "Прыгни с b6 через c7 на d8 — шашка станет Дамкой и сразу срубит f6, приземлившись на g5 или h4!",
        )
      ),
      successExplanation = "Невероятно! Шашка получила золотую корону на d8 прямо в полёте и тут же как Дамка срубила вторую цель!",
    ),

    LogicPuzzle(
      id = "puzzle_4_triple_salute",
      order = 4,
      title = "Тройной салют",
      themeTag = "Серия взятий",
      difficulty = PuzzleDifficulty.MEDIUM,
      prompt = "Белая шашка на a1 готова к большому путешествию! Найди путь, который срубит сразу ТРИ чёрные шашки!",
      initialRows = listOf(
        "........",
        "........",
        "...b....",
        "........",
        "...b....",
        "........",
        ".b......",
        "w.......",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("a1:c3:e5:c7"),
          hintMove = CheckersMove.captureChain(
            Triple("a1", "c3", "b2"),
            Triple("c3", "e5", "d4"),
            Triple("e5", "c7", "d6"),
          ),
          hintText = "Начни с a1 → c3 → e5 и сверни налево через d6 на c7!",
        )
      ),
      successExplanation = "Браво! Целых три шашки соперника срублены одним стремительным ударом a1:c3:e5:c7!",
    ),

    LogicPuzzle(
      id = "puzzle_5_sacrifice_fork",
      order = 5,
      title = "Жертва ради двойного удара",
      themeTag = "Тактическая жертва",
      difficulty = PuzzleDifficulty.MEDIUM,
      prompt = "Задача в 2 хода! Сначала пожертвуй шашку d4 на e5, чтобы заманить чёрных под двойной удар!",
      initialRows = listOf(
        "........",
        "........",
        "...b.b..",
        "........",
        "...w.b..",
        "..w.....",
        ".w......",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("d4–e5"),
          hintMove = CheckersMove.simple("d4", "e5"),
          hintText = "Шаг 1: отдай шашку d4 на клетку e5, заставив чёрных бить!",
          botAutoReply = CheckersMove.captureChain(Triple("f6", "d4", "e5")),
          replyCaption = "Чёрные обязаны бить f6:d4 и сами попали в нашу ловушку! Теперь бей!",
        ),
        PuzzleStep(
          acceptedNotations = setOf("c3:e5:c7", "c3:e5:g3"),
          hintMove = CheckersMove.captureChain(
            Triple("c3", "e5", "d4"),
            Triple("e5", "c7", "d6"),
          ),
          hintText = "Шаг 2: бей шашкой c3 через d4 и сразу забирай вторую шашку!",
        ),
      ),
      successExplanation = "Блестящая комбинация! Маленькая жертва принесла нам большой перевес!",
    ),

    LogicPuzzle(
      id = "puzzle_6_zugzwang",
      order = 6,
      title = "Ловушка без боя",
      themeTag = "Запирание",
      difficulty = PuzzleDifficulty.HARD,
      prompt = "Победить можно не только срубив все шашки, но и заперев соперника! Найди ход, после которого у чёрных не останется ни одного хода!",
      initialRows = listOf(
        "........",
        "........",
        ".......b",
        "........",
        ".....w.w",
        "........",
        "........",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("h4–g5"),
          hintMove = CheckersMove.simple("h4", "g5"),
          hintText = "Поставь правую белую шашку с h4 прямо перед чёрной на g5 — вторая шашка f4 прикроет её сзади!",
        )
      ),
      successExplanation = "Победа запиранием! Чёрная шашка h6 не может сходить, потому что единственный путь перекрыт, а бить некуда (f4 занята)!",
    ),

    LogicPuzzle(
      id = "puzzle_7_sniper_king",
      order = 7,
      title = "Точный прицел Дамки",
      themeTag = "Геометрия Дамки",
      difficulty = PuzzleDifficulty.HARD,
      prompt = "Дамка на a1 должна срубить c3 так, чтобы сразу же развернуться и побить вторую шашку c7! На какую клетку встать после первого прыжка?",
      initialRows = listOf(
        "........",
        "..b.....",
        "........",
        "........",
        "........",
        "..b.....",
        "........",
        "W.......",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("a1:e5:b8"),
          hintMove = CheckersMove.captureChain(
            Triple("a1", "e5", "c3"),
            Triple("e5", "b8", "c7"),
          ),
          hintText = "Перепрыгни c3 и остановись точно на пересечении диагоналей — на клетке e5, чтобы оттуда сразу побить c7 на b8!",
        )
      ),
      successExplanation = "Снайперская точность! Клетка e5 — единственная точка перекрёстка, откуда Дамка достаёт до второй шашки c7!",
    ),

    LogicPuzzle(
      id = "puzzle_8_grandmaster",
      order = 8,
      title = "Комбинация гроссмейстера",
      themeTag = "Прорыв в дамки",
      difficulty = PuzzleDifficulty.HARD,
      prompt = "Супер-задача в 2 хода! Отдай шашку c5 вперёд, а затем сделай двойной удар с прорывом в Дамки!",
      initialRows = listOf(
        "........",
        "..b.b...",
        "........",
        "..w.....",
        ".w......",
        "w.......",
        "........",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("c5–d6"),
          hintMove = CheckersMove.simple("c5", "d6"),
          hintText = "Шаг 1: пожертвуй шашку c5 на поле d6!",
          botAutoReply = CheckersMove.captureChain(Triple("e7", "c5", "d6")),
          replyCaption = "Чёрная шашка e7 срубила на c5 — и открыла нам дорогу прямо к короне!",
        ),
        PuzzleStep(
          acceptedNotations = setOf("b4:d6:b8"),
          hintMove = CheckersMove.captureChain(
            Triple("b4", "d6", "c5"),
            Triple("d6", "b8", "c7"),
            promotesFlags = listOf(false, true),
          ),
          hintText = "Шаг 2: бей b4 → d6 → b8 и получай золотую корону Дамки!",
        ),
      ),
      successExplanation = "Потрясающе! Ты мыслишь как настоящий гроссмейстер: хитрая жертва, двойной удар и прорыв в Дамки!",
    ),

    LogicPuzzle(
      id = "puzzle_9_springboard",
      order = 9,
      title = "Турнирный трамплин",
      themeTag = "Жертва на разряд",
      difficulty = PuzzleDifficulty.HARD,
      prompt = "Разрядная комбинация в 2 хода! Отдай шашку c5 на b6, чтобы вторым ходом перепрыгнуть через две чёрные шашки прямо в Дамки!",
      initialRows = listOf(
        "........",
        "b...b...",
        "........",
        "..w.....",
        ".w......",
        "w.......",
        "........",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("c5–b6"),
          hintMove = CheckersMove.simple("c5", "b6"),
          hintText = "Шаг 1: сделай смелую жертву c5–b6, заставляя шашку a7 прыгнуть на c5!",
          botAutoReply = CheckersMove.captureChain(Triple("a7", "c5", "b6")),
          replyCaption = "Чёрные обязаны бить a7:c5 — трамплин для прыжка в Дамки готов!",
        ),
        PuzzleStep(
          acceptedNotations = setOf("b4:d6:f8"),
          hintMove = CheckersMove.captureChain(
            Triple("b4", "d6", "c5"),
            Triple("d6", "f8", "e7"),
            promotesFlags = listOf(false, true),
          ),
          hintText = "Шаг 2: прыгай с b4 через c5 и e7 прямо на дамочное поле f8!",
        ),
      ),
      successExplanation = "Настоящий разрядный уровень! Жертва на b6 выстроила идеальный трамплин в Дамки!",
    ),

    LogicPuzzle(
      id = "puzzle_10_shlagbaum",
      order = 10,
      title = "Удар «Шлагбаум»",
      themeTag = "Турнирная классика",
      difficulty = PuzzleDifficulty.HARD,
      prompt = "Знаменитый турнирный удар «Шлагбаум»! Пожертвуй шашку c5–b6, а затем прорвись в Дамки на f8 и на лету сруби третью шашку g7!",
      initialRows = listOf(
        "........",
        "b...b.b.",
        "........",
        "..w.....",
        ".w......",
        "w.......",
        "........",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("c5–b6"),
          hintMove = CheckersMove.simple("c5", "b6"),
          hintText = "Шаг 1: начни комбинацию «Шлагбаум» жертвой c5–b6!",
          botAutoReply = CheckersMove.captureChain(Triple("a7", "c5", "b6")),
          replyCaption = "Чёрная шашка a7 побила на c5 — теперь открой шлагбаум в Дамки!",
        ),
        PuzzleStep(
          acceptedNotations = setOf("b4:d6:f8:h6"),
          hintMove = CheckersMove.captureChain(
            Triple("b4", "d6", "c5"),
            Triple("d6", "f8", "e7"),
            Triple("f8", "h6", "g7"),
            promotesFlags = listOf(false, true, false),
          ),
          hintText = "Шаг 2: бей b4 → d6 → f8 (Дамка!) → h6, забирая сразу три чёрные шашки!",
        ),
      ),
      successExplanation = "Блестяще! Ты провёл классический турнирный удар «Шлагбаум» с превращением в Дамку в полёте!",
    ),

    LogicPuzzle(
      id = "puzzle_11_column_strike",
      order = 11,
      title = "Выстрел колонки",
      themeTag = "Ударная колонка",
      difficulty = PuzzleDifficulty.MEDIUM,
      prompt = "У белых построена турнирная колонка g1–f2–e3! Пожертвуй переднюю шашку e3–f4, чтобы сработала пружина двойного удара!",
      initialRows = listOf(
        "........",
        "........",
        "........",
        "..b...b.",
        "........",
        "....w...",
        ".....w..",
        "......w.",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("e3–f4"),
          hintMove = CheckersMove.simple("e3", "f4"),
          hintText = "Шаг 1: выдвини вершину колонки e3–f4 под бой шашки g5!",
          botAutoReply = CheckersMove.captureChain(Triple("g5", "e3", "f4")),
          replyCaption = "Чёрные побили g5:e3, а шашка g1 защитила нашу колонку сзади! Теперь бей!",
        ),
        PuzzleStep(
          acceptedNotations = setOf("f2:d4:b6"),
          hintMove = CheckersMove.captureChain(
            Triple("f2", "d4", "e3"),
            Triple("d4", "b6", "c5"),
          ),
          hintText = "Шаг 2: бей шашкой f2 через e3 и c5 на поле b6!",
        ),
      ),
      successExplanation = "Вот зачем разрядники строят ударные колонки: задняя шашка g1 остановила врага, а средняя f2 нанесла двойной удар!",
    ),

    LogicPuzzle(
      id = "puzzle_12_king_trap",
      order = 12,
      title = "Петля для Дамки",
      themeTag = "Эндшпиль и ловля",
      difficulty = PuzzleDifficulty.HARD,
      prompt = "Чёрная Дамка на a7 кажется неуловимой! Но если отдать ей шашку c5–b6, она упрётся в барьер d4–e3 и попадёт прямо под прицел нашей Дамки a3!",
      initialRows = listOf(
        "........",
        "B.......",
        "........",
        "..w.....",
        "...w....",
        "W...w...",
        "........",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("c5–b6"),
          hintMove = CheckersMove.simple("c5", "b6"),
          hintText = "Шаг 1: пожертвуй шашку c5–b6 — пара шашек d4 и e3 не даст чёрной Дамке пролететь дальше c5!",
          botAutoReply = CheckersMove.captureChain(Triple("a7", "c5", "b6")),
          replyCaption = "Чёрная Дамка обязана бить и вынуждена остановиться точно на поле c5!",
        ),
        PuzzleStep(
          acceptedNotations = setOf("a3:d6", "a3:e7", "a3:f8"),
          hintMove = CheckersMove.captureChain(Triple("a3", "d6", "c5")),
          hintText = "Шаг 2: сруби чёрную Дамку на c5 своей Дамкой с поля a3!",
        ),
      ),
      successExplanation = "Высший пилотаж эндшпиля! Барьер d4–e3 остановил вражескую Дамку точно на перекрёстке c5!",
    ),

    LogicPuzzle(
      id = "puzzle_13_turkish_strike",
      order = 13,
      title = "Турецкий удар",
      themeTag = "Мастерская тактика",
      difficulty = PuzzleDifficulty.MASTER,
      prompt = "Ловушка на расчёт! Простая a1 манит взятием, но настоящая победа — гигантский «Турецкий удар» Дамкой a3 через 6 чёрных шашек! Помни: сбитые шашки остаются на доске до конца хода и не дают пролететь дважды!",
      initialRows = listOf(
        "........",
        "..b.....",
        ".....b..",
        "........",
        ".b.b....",
        "W.....b.",
        ".b.b....",
        "w.......",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("a3:e7:h4:e1:c3:e5:b8"),
          hintMove = CheckersMove.captureChain(
            Triple("a3", "e7", "b4"),
            Triple("e7", "h4", "f6"),
            Triple("h4", "e1", "g3"),
            Triple("e1", "c3", "d2"),
            Triple("c3", "e5", "d4"),
            Triple("e5", "b8", "c7"),
          ),
          hintText = "Начни Дамкой a3 на e7 → h4 → e1 → c3 → e5 (тут сбитая f6 остановит тебя перед e5!) → b8!",
        )
      ),
      successExplanation = "Феноменально! Целых 6 шашек по большой петле с использованием правила «сбитая шашка как преграда»!",
    ),

    LogicPuzzle(
      id = "puzzle_14_double_sacrifice",
      order = 14,
      title = "Двойная жертва в миттельшпиле",
      themeTag = "Многоходовая жертва",
      difficulty = PuzzleDifficulty.HARD,
      prompt = "Задача в 3 хода! Пожертвуй сначала одну шашку на правом фланге, потом вторую на левом — и проведи решающий тройной удар прямо в Дамки!",
      initialRows = listOf(
        ".b.....b",
        "b...b.b.",
        "........",
        "b...w.b.",
        "........",
        "..w.....",
        ".w.w.w.w",
        "w.w.w.w.",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("e5–f6"),
          hintMove = CheckersMove.simple("e5", "f6"),
          hintText = "Шаг 1: отдай шашку e5 на поле f6 — шашки e7 и g5 заперты, поэтому бить обязана g7!",
          botAutoReply = CheckersMove.captureChain(Triple("g7", "e5", "f6")),
          replyCaption = "Чёрные побили g7:e5! Теперь расчисти вход на левом фланге!",
        ),
        PuzzleStep(
          acceptedNotations = setOf("c3–b4"),
          hintMove = CheckersMove.simple("c3", "b4"),
          hintText = "Шаг 2: пожертвуй шашку c3 на b4, заманивая чёрную a5 на поле c3!",
          botAutoReply = CheckersMove.captureChain(Triple("a5", "c3", "b4")),
          replyCaption = "Трамплин из двух шашек готов! Нанеси решающий удар!",
        ),
        PuzzleStep(
          acceptedNotations = setOf("b2:d4:f6:d8"),
          hintMove = CheckersMove.captureChain(
            Triple("b2", "d4", "c3"),
            Triple("d4", "f6", "e5"),
            Triple("f6", "d8", "e7"),
            promotesFlags = listOf(false, false, true),
          ),
          hintText = "Шаг 3: бей шашкой b2 через d4 и f6 прямо в Дамки на d8!",
        ),
      ),
      successExplanation = "Блестящий трёхходовый расчёт! Две последовательные жертвы на разных флангах привели к прорыву в Дамки!",
    ),

    LogicPuzzle(
      id = "puzzle_15_cross_fire",
      order = 15,
      title = "Перекрёстный контрудар",
      themeTag = "Контрудар",
      difficulty = PuzzleDifficulty.HARD,
      prompt = "Позиция в 2 хода! Создай ловушку так, чтобы единственное взятие чёрных открыло дорогу тройному удару через весь центр!",
      initialRows = listOf(
        ".b.b...b",
        "b.b...b.",
        ".b.b.b..",
        "........",
        ".w.w....",
        "w...w...",
        ".w.w.w.w",
        "....w.w.",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("d4–c5"),
          hintMove = CheckersMove.simple("d4", "c5"),
          hintText = "Шаг 1: двинь центральную шашку d4–c5! На b4 у нас защита, поэтому бить обязана b6!",
          botAutoReply = CheckersMove.captureChain(Triple("b6", "d4", "c5")),
          replyCaption = "Чёрные побили b6:d4 прямо под наш контрудар! Собери все 3 шашки!",
        ),
        PuzzleStep(
          acceptedNotations = setOf("e3:c5:e7:g5"),
          hintMove = CheckersMove.captureChain(
            Triple("e3", "c5", "d4"),
            Triple("c5", "e7", "d6"),
            Triple("e7", "g5", "f6"),
          ),
          hintText = "Шаг 2: бей шашкой e3 → c5 → e7 → g5!",
        ),
      ),
      successExplanation = "Идеальный перекрёстный удар e3:c5:e7:g5! Тихий ход d4–c5 выманил защитника и обрушил весь центр чёрных!",
    ),

    LogicPuzzle(
      id = "puzzle_16_king_snare",
      order = 16,
      title = "Снайперский эндшпиль",
      themeTag = "Ловля Дамки",
      difficulty = PuzzleDifficulty.MASTER,
      prompt = "Острейший эндшпиль! Найди единственную тихую жертву, которая запрёт чёрную Дамку на нужном перекрёстке под двойной удар нашей Дамки!",
      initialRows = listOf(
        "........",
        "..B.....",
        ".......W",
        "........",
        "...w....",
        "......b.",
        ".....b..",
        "....w...",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("d4–e5"),
          hintMove = CheckersMove.simple("d4", "e5"),
          hintText = "Шаг 1: поставь шашку d4 на e5! Шашка g3 мешает Дамке пролететь дальше f4!",
          botAutoReply = CheckersMove.captureChain(Triple("c7", "f4", "e5")),
          replyCaption = "Чёрная Дамка вынуждена встать точно на f4! Теперь покажи правило продолжения боя!",
        ),
        PuzzleStep(
          acceptedNotations = setOf("h6:e3:g1"),
          hintMove = CheckersMove.captureChain(
            Triple("h6", "e3", "f4"),
            Triple("e3", "g1", "f2"),
          ),
          hintText = "Шаг 2: бей Дамкой h6 через f4 и останавливайся на e3, чтобы сразу добить f2 на g1!",
        ),
      ),
      successExplanation = "Мастерский эндшпиль! Чёрная Дамка c7 попала в засаду на f4, а правило обязательного продолжения боя принесло чистую победу!",
    ),

    LogicPuzzle(
      id = "puzzle_17_grand_cascade",
      order = 17,
      title = "Большой каскад",
      themeTag = "Разрядная комбинация",
      difficulty = PuzzleDifficulty.MASTER,
      prompt = "Настоящая турнирная задача в 3 хода! Разрушь оба фланга соперника двумя жертвами и проведи шашку f2 через 4 взятия с превращением в Дамку на лету!",
      initialRows = listOf(
        ".b.....b",
        "b.b.b.b.",
        "........",
        "w.w...b.",
        "........",
        "......w.",
        ".w.w.w.w",
        "w.w.w.w.",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("c5–b6"),
          hintMove = CheckersMove.simple("c5", "b6"),
          hintText = "Шаг 1: пожертвуй c5–b6 (шашка a5 прикроет поле a5, поэтому бить обязана a7)!",
          botAutoReply = CheckersMove.captureChain(Triple("a7", "c5", "b6")),
          replyCaption = "Отлично, шашка a7 побила на c5! Теперь вскрой правый фланг!",
        ),
        PuzzleStep(
          acceptedNotations = setOf("g3–f4"),
          hintMove = CheckersMove.simple("g3", "f4"),
          hintText = "Шаг 2: отдай шашку g3 на f4, чтобы g5 прыгнула на e3!",
          botAutoReply = CheckersMove.captureChain(Triple("g5", "e3", "f4")),
          replyCaption = "Дорога открыта от f2 до самой короны и дальше! Нанеси удар на 4 шашки!",
        ),
        PuzzleStep(
          acceptedNotations = setOf("f2:d4:b6:d8:f6", "f2:d4:b6:d8:g5", "f2:d4:b6:d8:h4"),
          hintMove = CheckersMove.captureChain(
            Triple("f2", "d4", "e3"),
            Triple("d4", "b6", "c5"),
            Triple("b6", "d8", "c7"),
            Triple("d8", "g5", "e7"),
            promotesFlags = listOf(false, false, true, false),
          ),
          hintText = "Шаг 3: бей f2 → d4 → b6 → d8 (Дамка!) и сразу руби e7 по длинной диагонали!",
        ),
      ),
      successExplanation = "Гроссмейстерский расчёт! 3 хода, 2 точные жертвы и сокрушительный каскад на 4 шашки с превращением в Дамку!",
    ),

    LogicPuzzle(
      id = "puzzle_18_flank_breach",
      order = 18,
      title = "Прорыв сквозь решето",
      themeTag = "Идея «Решето»",
      difficulty = PuzzleDifficulty.HARD,
      prompt = "Плотная боевая позиция в 2 хода! Пожертвуй одну шашку в центре так, чтобы через образовавшуюся брешь шашка b2 прошла с 4 взятиями через дамки!",
      initialRows = listOf(
        ".b.....b",
        "b.b...b.",
        ".....b.b",
        "..b.b...",
        "........",
        "..w.w...",
        ".w.w.w.w",
        "w...w.w.",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("c3–d4"),
          hintMove = CheckersMove.simple("c3", "d4"),
          hintText = "Шаг 1: пожертвуй c3–d4! Шашка e3 защищает центр от c5, поэтому бить обязана e5!",
          botAutoReply = CheckersMove.captureChain(Triple("e5", "c3", "d4")),
          replyCaption = "Чёрные прыгнули прямо перед нашей b2! Прорви оборону до конца!",
        ),
        PuzzleStep(
          acceptedNotations = setOf("b2:d4:b6:d8:g5", "b2:d4:b6:d8:h4"),
          hintMove = CheckersMove.captureChain(
            Triple("b2", "d4", "c3"),
            Triple("d4", "b6", "c5"),
            Triple("b6", "d8", "c7"),
            Triple("d8", "g5", "f6"),
            promotesFlags = listOf(false, false, true, false),
          ),
          hintText = "Шаг 2: бей b2 → d4 → b6 → d8 (Дамка!) и сруби f6 на g5 или h4!",
        ),
      ),
      successExplanation = "Мощный фланговый прорыв! Даже плотное расположение соперника не спасло от точной 4-кратной комбинации!",
    ),

    LogicPuzzle(
      id = "puzzle_19_king_windmill",
      order = 19,
      title = "Мельница дамки",
      themeTag = "Полный круговой удар",
      difficulty = PuzzleDifficulty.MASTER,
      prompt = "Найди 2-ходовую «Мельницу»! Тихий ход блокирует неправильное взятие чёрных, а затем Дамка a3 проходит через все 4 шашки по замкнутому кругу!",
      initialRows = listOf(
        "........",
        "........",
        ".b.b.b..",
        "..b.....",
        "...w.w..",
        "W.......",
        "...b....",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("f4–e5"),
          hintMove = CheckersMove.simple("f4", "e5"),
          hintText = "Шаг 1: сыграй f4–e5! Шашка d4 не даёт чёрной f6 бить на d4, заставляя прыгнуть d6:f4!",
          botAutoReply = CheckersMove.captureChain(Triple("d6", "f4", "e5")),
          replyCaption = "Клетка d6 освободилась, а чёрная встала точно на f4! Запускай мельницу Дамкой a3!",
        ),
        PuzzleStep(
          acceptedNotations = setOf("a3:e7:g5:e3:c1"),
          hintMove = CheckersMove.captureChain(
            Triple("a3", "e7", "c5"),
            Triple("e7", "g5", "f6"),
            Triple("g5", "e3", "f4"),
            Triple("e3", "c1", "d2"),
          ),
          hintText = "Шаг 2: не сворачивай на d6 раньше времени — лети Дамкой a3 → e7 → g5 → e3 → c1, срубая 4 шашки!",
        ),
      ),
      successExplanation = "Красота! Через d6 Дамка взяла бы только 3 шашки, но точный выбор поля e7 позволил срубить все 4 в одной мельнице!",
    ),

    LogicPuzzle(
      id = "puzzle_20_petrov_fork",
      order = 20,
      title = "Вилка Петрова",
      themeTag = "Этюд чемпиона",
      difficulty = PuzzleDifficulty.MASTER,
      prompt = "Сложнейший этюд в 2 хода! Чёрная Дамка на b8 неуязвима, но точная жертва c5–d6 заманит её под сокрушительный двойной удар с обязательным поворотом на c3!",
      initialRows = listOf(
        ".B.....W",
        "........",
        "........",
        "..w.....",
        ".....b..",
        "........",
        "...b....",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("c5–d6"),
          hintMove = CheckersMove.simple("c5", "d6"),
          hintText = "Шаг 1: отдай шашку c5–d6! Чёрная шашка f4 не пропустит Дамку дальше поля e5!",
          botAutoReply = CheckersMove.captureChain(Triple("b8", "e5", "d6")),
          replyCaption = "Чёрная Дамка встала точно на e5! Найди единственную точку перекрёстка, чтобы срубить сразу две фигуры!",
        ),
        PuzzleStep(
          acceptedNotations = setOf("h8:c3:e1"),
          hintMove = CheckersMove.captureChain(
            Triple("h8", "c3", "e5"),
            Triple("c3", "e1", "d2"),
          ),
          hintText = "Шаг 2: бей Дамкой h8 → c3 → e1, срубая вражескую Дамку и шашку d2!",
        ),
      ),
      successExplanation = "Браво, настоящий этюдный расчёт! Жертва c5–d6 заманила Дамку под двойной удар h8:c3:e1!",
    ),
  )

  val puzzles: List<LogicPuzzle> =
    starterPuzzles + HardPuzzlesPack.puzzles + MasterPuzzlesPack.puzzles

  fun findById(id: String): LogicPuzzle? = puzzles.find { it.id == id }
}

object DailyPuzzleHelper {
  fun todayDateKey(): String {
    val cal = java.util.Calendar.getInstance()
    val year = cal.get(java.util.Calendar.YEAR)
    val dayOfYear = cal.get(java.util.Calendar.DAY_OF_YEAR)
    return "$year-$dayOfYear"
  }

  fun pickDailyPuzzle(allPuzzles: List<LogicPuzzle>, dateKey: String = todayDateKey()): LogicPuzzle? {
    val hardPool = allPuzzles.filter {
      it.difficulty == PuzzleDifficulty.HARD || it.difficulty == PuzzleDifficulty.MASTER
    }
    val pool = if (hardPool.isNotEmpty()) hardPool else allPuzzles
    if (pool.isEmpty()) return null
    val hash = dateKey.fold(17) { acc, ch -> acc * 31 + ch.code }
    val idx = kotlin.math.abs(hash) % pool.size
    return pool[idx]
  }
}

