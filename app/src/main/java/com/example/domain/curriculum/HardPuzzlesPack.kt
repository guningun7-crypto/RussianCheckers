package com.example.domain.curriculum

import com.example.domain.checkers.CheckersMove

object HardPuzzlesPack {
  val puzzles: List<LogicPuzzle> = listOf(
    LogicPuzzle(
      id = "puzzle_21_hard_fork_bridge",
      order = 21,
      title = "Переправа через мост",
      themeTag = "Жертва + двойной удар",
      difficulty = PuzzleDifficulty.HARD,
      prompt = "Белые атакуют в 2 хода: жертва заманивает чёрную шашку на простреливаемое поле для двойного удара!",
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
          hintText = "Шаг 1: отдай d4–e5! Чёрная f6 обязана прыгнуть на d4.",
          botAutoReply = CheckersMove.captureChain(Triple("f6", "d4", "e5")),
          replyCaption = "Чёрная встала на d4! Пробивай двойной удар c3:e5:c7!",
        ),
        PuzzleStep(
          acceptedNotations = setOf("c3:e5:c7"),
          hintMove = CheckersMove.captureChain(Triple("c3", "e5", "d4"), Triple("e5", "c7", "d6")),
          hintText = "Шаг 2: бей c3 → e5 → c7 через обе шашки!",
        ),
      ),
      successExplanation = "Классическая комбинация! Жертва d4–e5 открыла c3:e5:c7.",
    ),

    LogicPuzzle(
      id = "puzzle_22_hard_sieve",
      order = 22,
      title = "Удар «Решето»",
      themeTag = "Тройной прорыв в дамки",
      difficulty = PuzzleDifficulty.HARD,
      prompt = "Выстрой «решето» и сметай 3 шашки за один заход прямо до 8-й линии!",
      initialRows = listOf(
        "........",
        "..b.....",
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
          hintText = "Шаг 1: пожертвуй e3–f4 под обязательный бой g5:e3!",
          botAutoReply = CheckersMove.captureChain(Triple("g5", "e3", "f4")),
          replyCaption = "Отлично! Шашка f2 теперь видит три цели подряд!",
        ),
        PuzzleStep(
          acceptedNotations = setOf("f2:d4:b6:d8"),
          hintMove = CheckersMove.captureChain(
            Triple("f2", "d4", "e3"),
            Triple("d4", "b6", "c5"),
            Triple("b6", "d8", "c7"),
            promotesFlags = listOf(false, false, true),
          ),
          hintText = "Шаг 2: прыгай f2 → d4 → b6 → d8 прямо в Дамки!",
        ),
      ),
      successExplanation = "Тройной прыжок f2:d4:b6:d8 с превращением в Дамку!",
    ),

    LogicPuzzle(
      id = "puzzle_23_hard_back_hook",
      order = 23,
      title = "Крюк с боем назад",
      themeTag = "Комбинация тыла",
      difficulty = PuzzleDifficulty.HARD,
      prompt = "Заставь шашку соперника прыгнуть в центр, а затем ударь в обе стороны — вперёд и назад!",
      initialRows = listOf(
        "........",
        "........",
        "...b....",
        "....b...",
        "........",
        "..w.w...",
        "........",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("e3–d4"),
          hintMove = CheckersMove.simple("e3", "d4"),
          hintText = "Шаг 1: шагни e3–d4 прямо под нос шашке e5!",
          botAutoReply = CheckersMove.captureChain(Triple("e5", "c3", "d4")).let {
            // Wait if c3 is white, e5 cannot land on c3! Let's check board: c3 has 'w'!
            // Instead make c3 empty and use a1/b2 + c3 empty:
            it
          },
        ),
      ),
      successExplanation = "",
    ).let {
      // Fix puzzle_23 clean legal 2-step sequence:
      // Row 2: d6(b), f6(b); Row 4: f4(w); Row 5: e3(w); Row 6: b2(w)? No:
      // Step 1: f4-e5 -> d6:f4 -> e3:g5? Wait that only takes 1!
      // For double strike: after d6:f4, white hits e3:g5:e7 (needs b on f6!).
      // Verify: e3(5,4)->g5(3,6) over f4(4,5) -> e7(1,4) over f6(2,5)! And e3 has NO b adjacent initially!
      // And after f4-e5: d6(2,3) captures e5(3,4) -> f4(4,5) empty! Does f6(2,5) also capture? Yes! Put b only on d6 and e7! No, f6 needed for 2nd jump: if Black has d6 AND f6, d6:f4 or f6:d4. If Black plays f6:d4 then b2? Wait if we put White at c3 too, then BOTH b2:e5? Wait c3(5,2):e5(3,4):g7(1,6) -> needs f6! If f6 jumped to d4, f6 is empty! Put b at f6? No, make only ONE black capture possible after f4–e5: put Black at d6 and f6, AND put white on b4? Wait if d4 occupied by white or f6 blocked on d4 by White d4(w)? NO if d4(w) then White has f4 and d4 -> wait does f4-e5 still work? No, d4(w) blocks f6:d4, leaves ONLY d6:f4! Then e3:g5:e7 jumps d6(now at f4) AND f6(still at f6)!! YES!! Double capture guaranteed!!
      // Check legality: init: d6(b), f6(b), d4(w), f4(w), e3(w)? Wait e3(5,4), d4(4,3), f4(4,5), d6(2,3), f6(2,5).
      // Before move 1: White has d4, f4, e3 -> any captures? d4 has c5,e5 empty -> NO. f4 has e5,g5 empty -> NO. e3 has d4,f4 occupied by white -> NO.
      // Step 1: f4–e5! Now Black to move:
      // Black d6(2,3): e5(3,4) is 'w', f4(4,5) is EMPTY! -> d6:f4!
      // Black f6(2,5): e5(3,4) is 'w', d4(4,3) is OCCUPIED by 'w'! So NO capture from f6!
      // Therefore d6:f4 is Black's ONLY legal move!!
      // After 1... d6:f4: e3(5,4) captures f4(4,5)->g5(3,6) AND captures f6(2,5)->e7(1,4)! Or d4(4,3) doesn't have capture! So ONLY e3:g5:e7!!
      LogicPuzzle(
        id = "puzzle_23_hard_back_hook",
        order = 23,
        title = "Замок на диагонали",
        themeTag = "Вынужденное взятие",
        difficulty = PuzzleDifficulty.HARD,
        prompt = "Используй свою шашку d4 как барьер, чтобы заставить чёрных бить единственным путём под двойной удар!",
        initialRows = listOf(
          "........",
          "........",
          "...b.b..",
          "........",
          "...w.w..",
          "....w...",
          "...w....",
          "........",
        ),
        steps = listOf(
          PuzzleStep(
            acceptedNotations = setOf("f4–e5"),
            hintMove = CheckersMove.simple("f4", "e5"),
            hintText = "Шаг 1: ходи f4–e5! Шашка d4 закроет поле для f6, и бить придётся с d6 на f4!",
            botAutoReply = CheckersMove.captureChain(Triple("d6", "f4", "e5")),
            replyCaption = "Точно по плану! Чёрная попала на f4 — сбивай обе шашки!",
          ),
          PuzzleStep(
            acceptedNotations = setOf("e3:g5:e7"),
            hintMove = CheckersMove.captureChain(Triple("e3", "g5", "f4"), Triple("g5", "e7", "f6")),
            hintText = "Шаг 2: прыгай e3 → g5 → e7!",
          ),
        ),
        successExplanation = "Превосходный расчёт! Ты использовал блокировку поля d4 для единственного боя!",
      )
    },

    LogicPuzzle(
      id = "puzzle_24_hard_left_break",
      order = 24,
      title = "Сквозь левый фланг",
      themeTag = "Жертва + сдвоенный удар",
      difficulty = PuzzleDifficulty.HARD,
      prompt = "Найди 2-ходовую комбинацию с жертвой на e5 и прорывом на поле a7!",
      initialRows = listOf(
        "........",
        "........",
        ".b...b..",
        "........",
        "...w....",
        "....w...",
        ".w...w..",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("d4–e5"),
          hintMove = CheckersMove.simple("d4", "e5"),
          hintText = "Шаг 1: пожертвуй d4–e5, чтобы выманить f6 на центр d4!",
          botAutoReply = CheckersMove.captureChain(Triple("f6", "d4", "e5")),
          replyCaption = "Поле d4 занято чёрными! Пробивай двойной сквозной удар e3:c5:a7!",
        ),
        PuzzleStep(
          acceptedNotations = setOf("e3:c5:a7"),
          hintMove = CheckersMove.captureChain(Triple("e3", "c5", "d4"), Triple("c5", "a7", "b6")),
          hintText = "Шаг 2: бей e3 → c5 → a7!",
        ),
      ),
      successExplanation = "Браво! Ударом e3:c5:a7 белые уничтожили фланг и вырвались к дамкам!",
    ),

    LogicPuzzle(
      id = "puzzle_25_hard_king_ambush",
      order = 25,
      title = "Засада на большой дороге",
      themeTag = "Охота на дамку",
      difficulty = PuzzleDifficulty.HARD,
      prompt = "Чёрная дамка h8 чувствует себя королевой на главной диагонали. Поймай её за 2 хода!",
      initialRows = listOf(
        ".......B",
        "........",
        "........",
        "........",
        "........",
        "..w.....",
        ".W......",
        "w.......",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("c3–d4"),
          hintMove = CheckersMove.simple("c3", "d4"),
          hintText = "Шаг 1: поставь c3 на d4 под обязательный бой дамки h8!",
          botAutoReply = CheckersMove.captureChain(Triple("h8", "c3", "d4")),
          replyCaption = "Чёрная дамка обязана прыгнуть за d4 на единственную клетку c3! Забирай её!",
        ),
        PuzzleStep(
          acceptedNotations = setOf("b2:d4", "b2:e5", "b2:f6", "b2:g7", "b2:h8"),
          hintMove = CheckersMove.captureChain(Triple("b2", "e5", "c3")),
          hintText = "Шаг 2: сруби дамку на c3 своей дамкой b2!",
        ),
      ),
      successExplanation = "Чёрная дамка попала в классическую ловушку перехвата!",
    ),

    LogicPuzzle(
      id = "puzzle_26_hard_triple_stair",
      order = 26,
      title = "Тройная лестница в дамки",
      themeTag = "Длинный удар",
      difficulty = PuzzleDifficulty.HARD,
      prompt = "Выбери верное взятие так, чтобы сбить 3 шашки и надеть золотую корону Дамки!",
      initialRows = listOf(
        "........",
        "....b...",
        "........",
        "..b.b...",
        "........",
        "....b...",
        "...w.w..",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("d2:f4:d6:f8"),
          hintMove = CheckersMove.captureChain(
            Triple("d2", "f4", "e3"),
            Triple("f4", "d6", "e5"),
            Triple("d6", "f8", "e7"),
            promotesFlags = listOf(false, false, true),
          ),
          hintText = "Не бей шашкой f2! Веди шашку d2 через e3 → e5 → e7 на дамочное поле f8!",
        ),
      ),
      successExplanation = "Отличный выбор взятия: 3 шашки срублены и Дамка на f8!",
    ),

    LogicPuzzle(
      id = "puzzle_27_hard_midjump_turn",
      order = 27,
      title = "Поворот дамки на лету",
      themeTag = "Превращение в бою",
      difficulty = PuzzleDifficulty.HARD,
      prompt = "Прорвись в дамки во время боя и сразу забирай вторую цель по другой диагонали!",
      initialRows = listOf(
        "........",
        "..b...b.",
        ".w......",
        "........",
        "........",
        "..b.....",
        "........",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("b6:d8:a5"),
          hintMove = CheckersMove.captureChain(
            Triple("b6", "d8", "c7"),
            Triple("d8", "a5", "b6"),
          ).let {
            it
          },
          hintText = "",
        ),
      ),
      successExplanation = "",
    ).let {
      // Clean puzzle_27: b6(w), c7(b), f6(b), d4(b)!
      // Path: b6:d8 (over c7, becomes King on d8!) -> d8:g5 (over f6) -> g5:c1 (Wait: g5(3,6)->f4->e3->d2->c1: put 'b' at e3(5,4) so d8:g5:b2/c1? Wait f6 is (2,5), g5 is (3,6). From g5(3,6) you turn 90 deg -> (4,5)=f4 -> (5,4)=e3(b) -> lands at d2 or c1!! 3-piece capture WITH promotion on flight!!).
      // Check NO black at f6 capture before? White has b6(2,1) -> jumps c7(1,2) ONLY! After d8(0,3,King), jumps f6(2,5) -> ONLY empty landing with another capture is g5(3,6)! Wait: from g5(3,6) -> jumps e3(5,4) -> lands d2(6,3) or c1(7,2)!
      LogicPuzzle(
        id = "puzzle_27_hard_midjump_turn",
        order = 27,
        title = "Поворот дамки на лету",
        themeTag = "Тройной удар с дамкой",
        difficulty = PuzzleDifficulty.HARD,
        prompt = "Прорвись в дамки на d8 прямо в бою и сделай точный поворот на g5, чтобы забрать сразу 3 шашки!",
        initialRows = listOf(
          "........",
          "..b.....",
          ".w...b..",
          "........",
          "........",
          "....b...",
          "........",
          "........",
        ),
        steps = listOf(
          PuzzleStep(
            acceptedNotations = setOf("b6:d8:g5:d2", "b6:d8:g5:c1"),
            hintMove = CheckersMove.captureChain(
              Triple("b6", "d8", "c7"),
              Triple("d8", "g5", "f6"),
              Triple("g5", "c1", "e3"),
              promotesFlags = listOf(true, false, false),
            ),
            hintText = "Бей b6 → d8 (становишься Дамкой!) → останавливайся на g5 → поворачивай через e3 на c1!",
          ),
        ),
        successExplanation = "Супер! Правило «превращения на лету» позволило срубить 3 шашки за один ход!",
      )
    },

    LogicPuzzle(
      id = "puzzle_28_hard_king_decoy_2",
      order = 28,
      title = "Перекрёсток двух дамок",
      themeTag = "Завлечение дамки",
      difficulty = PuzzleDifficulty.HARD,
      prompt = "Заставь чёрную дамку b8 прыгнуть на диагональ твоей дамки h2!",
      initialRows = listOf(
        ".B......",
        "........",
        "........",
        "........",
        "...w....",
        "........",
        ".......W",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("d4–e5"),
          hintMove = CheckersMove.simple("d4", "e5"),
          hintText = "Шаг 1: шагни d4–e5! Дамка b8 проскочит прямо на диагональ h2–b8... точнее b8:f4? Смотри: h2(6,7)->g3(5,6)->f4(4,5)->e5(3,4)! Но b8(0,1)->c7(1,2)->d6(2,3)->e5(3,4) лежит на той же диагонали!! Значит b8:f4 или b8:g3 или b8:h2!! Подожди: h2(6,7) уже стоит на (6,7), а g3 и f4 — на ТОЙ ЖЕ диагонали, только b8 могла прыгнуть и на f4 и на g3, и ОБЕ эти клетки (f4 и g3) на диагонали h2!! Поэтому h2:e5..b8 побьёт ЛЮБУЮ посадку b8!! Проверим: если b8 побила на f4, то h2:e5 (или d4/c3/b2/a1). Если b8 побила на g3, то h2:f4 (или d4..a1)!! Обе legal! Ставим botAutoReply = b8:g3!",
          botAutoReply = CheckersMove.captureChain(Triple("b8", "g3", "e5")),
          replyCaption = "Дамка перепрыгнула через e5 и приземлилась на g3 — бей!",
        ),
        PuzzleStep(
          acceptedNotations = setOf("h2:f4", "h2:e5", "h2:d6", "h2:c7", "h2:b8"),
          hintMove = CheckersMove.captureChain(Triple("h2", "d6", "g3")),
          hintText = "Шаг 2: забирай вражескую дамку своей дамкой h2!",
        ),
      ),
      successExplanation = "Точный перехват! Куда бы ни прыгнула b8 за шашку e5, дамка h2 сметает её!",
    ).let {
      it.copy(
        steps = listOf(
          it.steps[0].copy(
            hintText = "Шаг 1: подставь шашку d4–e5! На какую бы клетку ни прыгнула чёрная дамка b8, твоя дамка h2 её перехватит!",
          ),
          it.steps[1],
        )
      )
    },

    LogicPuzzle(
      id = "puzzle_29_hard_self_block_shot",
      order = 29,
      title = "Поддержка из тыла",
      themeTag = "Жертва + удар назад",
      difficulty = PuzzleDifficulty.HARD,
      prompt = "Замани чёрную шашку на центр d4 с левого фланга и пробей двойной удар до e7!",
      initialRows = listOf(
        "........",
        "........",
        ".b.b....",
        "........",
        ".w.w....",
        "....w...",
        ".....w..",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("d4–c5"),
          hintMove = CheckersMove.simple("d4", "c5"),
          hintText = "Шаг 1: пожертвуй d4–c5! Шашка b4 закроет путь для d6, и b6 прыгнет на d4!",
          botAutoReply = CheckersMove.captureChain(Triple("b6", "d4", "c5")),
          replyCaption = "Чёрная встала на d4, а f2 прикрыла e3 сзади! Пробивай двойной удар e3:c5:e7!",
        ),
        PuzzleStep(
          acceptedNotations = setOf("e3:c5:e7"),
          hintMove = CheckersMove.captureChain(Triple("e3", "c5", "d4"), Triple("c5", "e7", "d6")),
          hintText = "Шаг 2: бей e3 → c5 → e7!",
        ),
      ),
      successExplanation = "Отличный тактический расчёт! Жертва d4–c5 открыла дорогу для удара e3:c5:e7!",
    ),

    LogicPuzzle(
      id = "puzzle_30_hard_clear_lane",
      order = 30,
      title = "Сброс лишнего груза",
      themeTag = "Освобождение линии",
      difficulty = PuzzleDifficulty.HARD,
      prompt = "Два хода до победы! Сначала убери свою шашку с d4 на c5 жертвой, чтобы e3 смела весь ряд!",
      initialRows = listOf(
        "........",
        "........",
        ".b.b....",
        "........",
        "...w....",
        "....w...",
        "........",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("d4–c5"),
          hintMove = CheckersMove.simple("d4", "c5"),
          hintText = "Шаг 1: сыграй d4–c5! Чёрная b6 побьёт на d4 (если d6 на b4 -> a3 нет, значит на d4) — проверь: у чёрных и b6:d4 и d6:b4! Чтобы гарантировать b6:d4, поставим 'w' на b4? Тогда d4–c5: только b6:d4! Отлично — добавим ещё одну чёрную b8? Нет, d6:b4 закрыта шашкой b4? Нет, надо 'w' на b4? Но тогда b4 может пойти c5! Нет, просто добавим 'b' на b4 нет, лучше d6 убрать и поставить 'b' на b2/c7 -> если b на b6 и f6? Нет: если b на b6 и b на c7? После d4-c5: только b6:d4 (d6 пусто), затем e3:c5? Не хватит. Если 'b' на c7 и 'b' на b6 и 'b' на f6: после d4-c5: b6:d4, затем e3:c5? Нет, b6 ушло!",
          botAutoReply = CheckersMove.captureChain(Triple("b6", "d4", "c5")),
        ),
      ),
      successExplanation = "",
    ).let {
      // Let's replace puzzle_30 with a rock-solid, unambiguous 2-move forced puzzle:
      // White: b2(6,1), c3(5,2), e3(5,4). Black: c5(3,2), e5(3,4), b6(2,1).
      // Wait: if e3–d4 -> c5(3,2) is blocked on e3? Wait e3 is empty after e3-d4, so c5:e3? And e5:c3 blocked by c3! So ONLY c5:e3 is legal if e3 was vacated! Wait then c3:f6? No, c3:a5 only 1!
      // Instead: White a3(5,0), d2(6,3), e3(5,4). Black c5(3,2), e5(3,4), e7(1,4).
      // Step 1: e3–d4! Now Black c5(3,2) captures d4 -> lands e3(5,4)! Meanwhile Black e5(3,4) captures d4 -> lands c3(5,2)! Wait which is forced? Block c3 with White c3(w)! Then ONLY c5:e3 is legal! Wait then after 1... c5:e3, White d2(6,3) captures e3 -> lands f4(4,5) -> captures e5(3,4) -> lands d6(2,3) -> captures e7(1,4) -> lands f8(0,5 King)!!
      // Check legality BEFORE move 1:
      // White: c3(5,2), d2(6,3), e3(5,4). Black: c5(3,2), e5(3,4), e7(1,4).
      // Any White captures initially? NO (all black pieces on row 1 & 3, White on 5 & 6).
      // White plays 1. e3–d4!
      // Black to move: c5(3,2) -> d4(4,3='w') -> e3(5,4=EMPTY) => c5:e3 LEGAL!
      // e5(3,4) -> d4(4,3='w') -> c3(5,2='w' BLOCKED!) => NO CAPTURE!
      // Therefore 1... c5:e3 is Black's 100% UNIQUE FORCED MOVE!
      // After 1... c5:e3 (c5 moved to e3, d4 removed):
      // White d2(6,3) captures e3(5,4)->f4(4,5) -> captures e5(3,4)->d6(2,3) -> captures e7(1,4)->f8(0,5, King)!!
      // Does c3(5,2) have any capture? Adjacent to c3 is d4(empty), b4(empty). NO!
      // That's 100% FORCED AND SUPER CLEAN!
      LogicPuzzle(
        id = "puzzle_30_hard_clear_lane",
        order = 30,
        title = "Взлётная полоса на f8",
        themeTag = "Освобождение поля",
        difficulty = PuzzleDifficulty.HARD,
        prompt = "Освободи поле e3 точным ходом вперед, чтобы затем шашка d2 снесла 3 шашки до самой Дамки!",
        initialRows = listOf(
          "........",
          "....b...",
          "........",
          "..b.b...",
          "........",
          "..w.w...",
          "...w....",
          "..w.....",
        ),
        steps = listOf(
          PuzzleStep(
            acceptedNotations = setOf("e3–d4"),
            hintMove = CheckersMove.simple("e3", "d4"),
            hintText = "Шаг 1: шагни e3–d4! Шашка c3 не пустит e5, поэтому бить будет обязана c5 на пустое поле e3!",
            botAutoReply = CheckersMove.captureChain(Triple("c5", "e3", "d4")),
            replyCaption = "Идеально! Шашка c5 встала на e3 — пробивай тройной удар шашкой d2 до f8!",
          ),
          PuzzleStep(
            acceptedNotations = setOf("d2:f4:d6:f8"),
            hintMove = CheckersMove.captureChain(
              Triple("d2", "f4", "e3"),
              Triple("f4", "d6", "e5"),
              Triple("d6", "f8", "e7"),
              promotesFlags = listOf(false, false, true),
            ),
            hintText = "Шаг 2: прыгай d2 → f4 → d6 → f8 (Дамка)!",
          ),
        ),
        successExplanation = "Искусное освобождение поля! Жертва e3–d4 открыла тройной удар d2:f4:d6:f8!",
      )
    },

    LogicPuzzle(
      id = "puzzle_31_hard_king_twin_shot",
      order = 31,
      title = "Двойной залп Дамки",
      themeTag = "Удары Дамкой",
      difficulty = PuzzleDifficulty.HARD,
      prompt = "Чёрная простая шашка кажется под защитой, но точный удар по центру заставит её открыться!",
      initialRows = listOf(
        "........",
        "....b...",
        "...b....",
        "........",
        ".....w..",
        "........",
        ".W......",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("b2–f6"),
          hintMove = CheckersMove.simple("b2", "f6"),
          hintText = "Wait: check! b2(6,1) -> c3(5,2) -> d4(4,3) -> e5(3,4) -> f6(2,5) -> g7(1,6)! Wait how about 1-move double capture by White King after simple setup? Let's put Black at f4(4,5) and c5(3,2), King at b2: no that's immediate. Make 1. f4–e5 forcing d6:f4, wait d6(b), c7(b)? Better: White King on a1(7,0), White man on e3(5,4), Black on d6(2,3) and f6(2,5). Wait step 1: e3-d4! Check: d6 jumps? No. If White man on e5(3,4)? Even simpler: Black on c5(3,2), e7(1,4); White on d4(4,3) and King on g3(5,6). Step 1: d4–c5? Can't, occupied.",
        ),
      ),
      successExplanation = "",
    ).let {
      // Clean puzzle_31: White at d2(6,3), f2(6,5), g3(5,6). Black at b6(2,1), d6(2,3), e5(3,4).
      // Wait let's do an unambiguous King sacrifice + capture:
      // White King b2(6,1), White man e3(5,4). Black men d6(2,3), f6(2,5), g7(1,6).
      // Check: White plays 1. e3–d4? No, doesn't threaten d6.
      // Put Black at d4(4,3)?? No then b2 jumps immediately.
      // Put Black at d6(2,3) and c5(3,2), and White at e5(3,4)? No.
      // Try: White d4(4,3) and King a3(5,0). Black at f6(2,5), d6(2,3), b6(2,1).
      // Step 1: d4–e5! Black f6(2,5) jumps e5(3,4) -> d4(4,3)! (d6 doesn't have e5? Wait d6(2,3) ALSO jumps e5(3,4) -> f4(4,5)! Unless f4 has 'w' or 'b'!)
      // So put 'w' at f4! Wait then after 1... f6:d4 (unique forced!), a3(W) can hit b4.. no d4 is on a3-f8? Wait a3(5,0)->b4(4,1)->c5(3,2)->d6(2,3)='b'! Wait a3 would jump d6 immediately!
      // Instead place White King on a1(7,0)! Diagonal a1-b2-c3-d4(empty after f6:d4!)-e5-f6-g7-h8!
      // Check initial board: White a1(W), d4(w), f4(w); Black b6(b), d6(b), f6(b), e7(b)!
      // Wait initially does a1(W) jump anything? a1->b2->c3->d4(w blocks)! So a1 has NO captures!
      // Step 1: d4–e5! (now d4 becomes empty!)
      // Black to move: d6(2,3)->e5(3,4='w')->f4(4,5='w' BLOCKED!). f6(2,5)->e5(3,4='w')->d4(4,3=EMPTY!) -> f6:d4 is UNIQUE FORCED!
      // After 1... f6:d4: a1(W) jumps OVER d4 -> lands at e5(3,4) -> jumps OVER d6(2,3) -> lands c7(1,2)! Wait b6 isn't hit from c7, so put Black on b4? No, b6(2,1)->c5(3,2) put Black at e7(1,4) from f8? Let's trace: e5(3,4) to d6(2,3) to c7(1,2) to b8(0,1)! Even cleaner: put Black on c7(1,2) EMPTY, wait c5? From d4(4,3) a1(W) can only land on e5(3,4), f6(2,5), g7(1,6), h8(0,7). If it lands e5(3,4), it can turn onto b8..f4 diag: jumps d6(2,3) to c7(1,2) or b8(0,1)!!
      // So White King a1:e5:b8 (or c7) captures BOTH d4(formerly f6) and d6 in one turn! Wait check if b6(2,1) is needed — nope, just f6, d6! Add a black on f4? Already f4 is 'w'.
      LogicPuzzle(
        id = "puzzle_31_hard_king_twin_shot",
        order = 31,
        title = "Снайперский прострел",
        themeTag = "Двойной удар Дамкой",
        difficulty = PuzzleDifficulty.HARD,
        prompt = "Раскрой большую дорогу a1–h8 жертвой шашки, чтобы твоя Дамка a1 срубила сразу двух врагов!",
        initialRows = listOf(
          "........",
          "........",
          "...b.b..",
          "........",
          "...w.w..",
          "........",
          "........",
          "W.......",
        ),
        steps = listOf(
          PuzzleStep(
            acceptedNotations = setOf("d4–e5"),
            hintMove = CheckersMove.simple("d4", "e5"),
            hintText = "Шаг 1: шагни d4–e5! Шашка f4 защитит проход от d6, и чёрной f6 придётся самой залезть на большую дорогу d4!",
            botAutoReply = CheckersMove.captureChain(Triple("f6", "d4", "e5")),
            replyCaption = "Большая дорога открыта, и враг на d4! Лети Дамкой с a1!",
          ),
          PuzzleStep(
            acceptedNotations = setOf("a1:e5:c7", "a1:e5:b8"),
            hintMove = CheckersMove.captureChain(
              Triple("a1", "e5", "d4"),
              Triple("e5", "b8", "d6"),
            ),
            hintText = "Шаг 2: бей Дамкой a1 → на e5 → через d6 на c7 или b8!",
          ),
        ),
        successExplanation = "Точный снайперский расчёт! Жертва d4–e5 расчистила линию для Дамки a1!",
      )
    },

    LogicPuzzle(
      id = "puzzle_32_hard_double_corner_wing",
      order = 32,
      title = "Удар по правому краю",
      themeTag = "Прорыв по борту",
      difficulty = PuzzleDifficulty.HARD,
      prompt = "Замани чёрную шашку назад на e3 и пробей сквозной прорыв прямо на h8!",
      initialRows = listOf(
        "........",
        "........",
        ".....b..",
        "..b.b...",
        "........",
        "..w.w...",
        ".....w..",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("e3–d4"),
          hintMove = CheckersMove.simple("e3", "d4"),
          hintText = "Шаг 1: отдай шашку e3–d4! У чёрных бьёт только c5:e3, потому что c3 закрыто!",
          botAutoReply = CheckersMove.captureChain(Triple("c5", "e3", "d4")),
          replyCaption = "Поле e3 снова цель! Прыгай шашкой f2 через e3 и f6 до самой Дамки на h8!",
        ),
        PuzzleStep(
          acceptedNotations = setOf("f2:d4:f6:h8"),
          hintMove = CheckersMove.captureChain(
            Triple("f2", "d4", "e3"),
            Triple("d4", "f6", "e5"),
            Triple("f6", "h8", "g7"),
            promotesFlags = listOf(false, false, true),
          ).let {
            it
          },
          hintText = "",
        ),
      ),
      successExplanation = "",
    ).let {
      // Let's verify row 1 has g7 ('b') and f6 is empty:
      // Row 1: "......b." -> g7! Row 2: "........", Row 3: "..b.b..." -> c5, e5! Row 5: "..w.w..." -> c3, e3! Row 6: ".....w.." -> f2!
      // Check before move 1: White at c3, e3, f2. Black at c5, e5, g7. No captures initially!
      // Move 1: e3–d4! Black: c5(3,2)->d4(4,3)->e3(5,4=EMPTY!) LEGAL! e5(3,4)->d4(4,3)->c3(5,2='w' BLOCKED!). So c5:e3 ONLY!
      // Move 2: f2(6,5)->e3(5,4)->d4(4,3) -> e5(3,4)->f6(2,5) -> g7(1,6)->h8(0,7, King)!! LEGAL & UNIQUE!!
      LogicPuzzle(
        id = "puzzle_32_hard_double_corner_wing",
        order = 32,
        title = "Сквозь все преграды на h8",
        themeTag = "Тройной прорыв",
        difficulty = PuzzleDifficulty.HARD,
        prompt = "Используй закрытую шашку c3 как упор и проведи тройной прорыв до угловой Дамки h8!",
        initialRows = listOf(
          "........",
          "......b.",
          "........",
          "..b.b...",
          "........",
          "..w.w...",
          ".....w..",
          "......w.",
        ),
        steps = listOf(
          PuzzleStep(
            acceptedNotations = setOf("e3–d4"),
            hintMove = CheckersMove.simple("e3", "d4"),
            hintText = "Шаг 1: шагни e3–d4! Шашка c3 не пропустит e5, и бить обязана c5 на поле e3!",
            botAutoReply = CheckersMove.captureChain(Triple("c5", "e3", "d4")),
            replyCaption = "Маршрут выстроен! Веди шашку f2 через три шашки до h8!",
          ),
          PuzzleStep(
            acceptedNotations = setOf("f2:d4:f6:h8"),
            hintMove = CheckersMove.captureChain(
              Triple("f2", "d4", "e3"),
              Triple("d4", "f6", "e5"),
              Triple("f6", "h8", "g7"),
              promotesFlags = listOf(false, false, true),
            ),
            hintText = "Шаг 2: прыгай f2 → d4 → f6 → h8 (Дамка)!",
          ),
        ),
        successExplanation = "Молниеносный прорыв f2:d4:f6:h8 с превращением в Дамку!",
      )
    },

    LogicPuzzle(
      id = "puzzle_33_hard_king_rescue",
      order = 33,
      title = "Перехват через тыл",
      themeTag = "Контрудар",
      difficulty = PuzzleDifficulty.HARD,
      prompt = "Замани чёрную шашку на удар назад: две шашки исчезнут за один ход!",
      initialRows = listOf(
        "........",
        "........",
        "...b....",
        "......b.",
        ".....w..",
        "..w.....",
        "........",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("f4–e5"),
          hintMove = CheckersMove.simple("f4", "e5"),
          hintText = "Wait: check d6(2,3) -> e5(3,4) -> f4(4,5) after f4-e5! Yes d6:f4! What about c3? Doesn't reach f4! Better put White at g3(5,6) and Black at d6(2,3) + d4? Let's place White at g3(5,6), f4(4,5) and Black at d6(2,3), b4? No, g3:e5:c7 needs e5 occupied after d6:f4? No if d6 moves to f4, d6 is EMPTY! Put Black at f6(2,5)!! Check: Black f6(2,5) + d6(2,3)? No: Black d6(2,3) + f6(2,5) + white f4(4,5) + white g3(5,6) + white d4(4,3) -> 1. f4-e5, Black d6:f4, White g3:e5:g7 (needs f6! Wait f6 stayed!) OR White g3:e5:c7? Wait e5->f6->g7 is same diag, d6 moved away! Put Black at f6(2,5) and c7? Even simpler: g3(5,6)->e5(3,4) over f4(4,5) -> g7(1,6) over f6(2,5)! Put white d4(4,3) so f6:d4 is blocked! Check!",
        ),
      ),
      successExplanation = "",
    ).let {
      // Initial: White d4(4,3), f4(4,5), g3(5,6); Black d6(2,3), f6(2,5).
      // Move 1: f4–e5! Black ONLY legal capture is d6:f4!
      // Move 2: g3:e5:g7 (over f4 then f6)! Check: g3(5,6)->f4(4,5)->e5(3,4)->f6(2,5)->g7(1,6)!
      // Can g3 stop at e5? NO, mandatory chain!
      LogicPuzzle(
        id = "puzzle_33_hard_king_rescue",
        order = 33,
        title = "Зигзаг на седьмую линию",
        themeTag = "Двойной удар",
        difficulty = PuzzleDifficulty.HARD,
        prompt = "Используй щит d4, чтобы завлечь шашку d6 на f4 и пробить двойной удар до g7!",
        initialRows = listOf(
          "........",
          "........",
          "...b.b..",
          "........",
          "...w.w..",
          "......w.",
          ".......w",
          "........",
        ),
        steps = listOf(
          PuzzleStep(
            acceptedNotations = setOf("f4–e5"),
            hintMove = CheckersMove.simple("f4", "e5"),
            hintText = "Шаг 1: шагни f4–e5! Чёрная f6 заперта шашкой d4, поэтому d6 бьёт на f4!",
            botAutoReply = CheckersMove.captureChain(Triple("d6", "f4", "e5")),
            replyCaption = "Двойной путь открыт! Забирай с g3 до самой g7!",
          ),
          PuzzleStep(
            acceptedNotations = setOf("g3:e5:g7"),
            hintMove = CheckersMove.captureChain(Triple("g3", "e5", "f4"), Triple("e5", "g7", "f6")),
            hintText = "Шаг 2: бей g3 → e5 → g7!",
          ),
        ),
        successExplanation = "Отличный зигзаг g3:e5:g7 прямо к преддамочному полю!",
      )
    },

    LogicPuzzle(
      id = "puzzle_34_hard_four_piece_sweep",
      order = 34,
      title = "Четверной прорыв",
      themeTag = "Штурмовая цепь",
      difficulty = PuzzleDifficulty.HARD,
      prompt = "Найди 2-ходовую подготовку, после которой твоя дамка снесёт сразу 3 шашки по зигзагу!",
      initialRows = listOf(
        "........",
        "....b...",
        "...b....",
        "....b...",
        "........",
        "......W.",
        "........",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("g3–h4"),
          hintMove = CheckersMove.simple("g3", "h4"),
          hintText = "Wait: check simple 1-step King capture? Let's make a 2-step puzzle where Black has e5(3,4), f4(4,5), c5(3,2) — no wait: 2-step with Pawn sacrifice first! White e3(5,4), c1(7,2) or King b2(6,1). Black b4(4,1), c7(1,2), e5(3,4), g7(1,6). Put White at d4(4,3) -> Wait simple: White King a1(7,0), White man e3(5,4), Black d6(2,3), f6(2,5), g7(1,6) -> NO if d4(w) empty, put White at d4! Same as #31? Let's make a fresh 1-step 4-piece capture OR fresh 2-step! Actually a 2-step with simple men: White f2(6,5), e3(5,4), g3(5,6). Black d6(2,3), b6(2,1), f6(2,5), g7(1,6) -> let's do King + 3 men on b4, c7, f6! White man f4(4,5), White King a3(5,0)?",
        ),
      ),
      successExplanation = "",
    ).let {
      // Fresh verified 2-step puzzle #34:
      // White: b2(6,1), c3(5,2), e3(5,4). Black: a5(3,0), c5(3,2), e7(1,4), f6(2,5).
      // Step 1: e3–d4! Look: c5(3,2)->d4(4,3)->e3(5,4=EMPTY!), while e5 empty! What about a5? Doesn't touch d4. f6 doesn't touch d4.
      // So 1... c5:e3 is UNIQUE FORCED!
      // Step 2: b2(6,1)->c3? No, c3 is 'w'! Wait make b2 empty and put White at d2(6,3)! Wait then same as #30? Make it RIGHT-to-LEFT!
      // Symmetrical & distinct: White c3(5,2), g3(5,6), f2(6,5). Black c5(3,2), e5(3,4), c7(1,2), g5(3,6).
      // Step 1: c3–d4! Look: c5(3,2)->d4(4,3)->e3(5,4 EMPTY!)? Wait if White is on e3, then e5:c3 blocked by c3(empty!), so e5:c3 IS forced!
      // Hold on: if White has e3(5,4), f2(6,5), c3(5,2) -> and Black has c5(3,2), e5(3,4), c7(1,2), e7?
      // Trace 1. c3–d4: c3 becomes empty, d4 occupied! Now e5(3,4) captures d4(4,3) -> lands c3(5,2=EMPTY!)! Meanwhile c5(3,2) captures d4(4,3) -> e3(5,4='w' BLOCKED!).
      // So 1... e5:c3 is 100% UNIQUE FORCED!
      // After 1... e5:c3: White d2? No, put White at b2(6,1)!
      // Check b2(6,1): jumps c3(5,2)->d4(4,3) -> jumps c5(3,2)->b6(2,1) -> jumps c7(1,2)->d8(0,3 King)!!
      // Check NO black at f4 or white at d2!
      // White: b2(6,1), c3(5,2), e3(5,4). Black: c5(3,2), e5(3,4), c7(1,2).
      // Before move 1: White b2, c3, e3 -> any captures? NO!
      // Move 1: c3–d4! Black ONLY legal capture: e5:c3!
      // Move 2: b2:d4:b6:d8! Captures 3 black pieces AND promotes to King at d8!
      LogicPuzzle(
        id = "puzzle_34_hard_four_piece_sweep",
        order = 34,
        title = "Зеркальное «Решето»",
        themeTag = "Комбинация с прорывом",
        difficulty = PuzzleDifficulty.HARD,
        prompt = "Используй щит e3, чтобы выманить шашку e5 на c3 и снести три фигуры на пути к Дамке d8!",
        initialRows = listOf(
          "........",
          "..b.....",
          "........",
          "..b.b...",
          "........",
          "..w.w...",
          ".w......",
          "w.......",
        ),
        steps = listOf(
          PuzzleStep(
            acceptedNotations = setOf("c3–d4"),
            hintMove = CheckersMove.simple("c3", "d4"),
            hintText = "Шаг 1: шагни c3–d4! Шашка e3 закроет путь для c5, поэтому e5 обязана прыгнуть на c3!",
            botAutoReply = CheckersMove.captureChain(Triple("e5", "c3", "d4")),
            replyCaption = "Клетка c3 заполнена чёрной шашкой! Веди шашку b2 через всё решето до d8!",
          ),
          PuzzleStep(
            acceptedNotations = setOf("b2:d4:b6:d8"),
            hintMove = CheckersMove.captureChain(
              Triple("b2", "d4", "c3"),
              Triple("d4", "b6", "c5"),
              Triple("b6", "d8", "c7"),
              promotesFlags = listOf(false, false, true),
            ),
            hintText = "Шаг 2: прыгай b2 → d4 → b6 → d8 (Дамка)!",
          ),
        ),
        successExplanation = "Идеальная комбинация b2:d4:b6:d8 с прорывом в Дамки!",
      )
    },

    LogicPuzzle(
      id = "puzzle_35_hard_king_double_cut",
      order = 35,
      title = "Перехват через 4 линии",
      themeTag = "Финальная вилка",
      difficulty = PuzzleDifficulty.HARD,
      prompt = "Белая дамка g1 видит слабый пункт на h6, но сначала дай ей нужную цель тихим ходом!",
      initialRows = listOf(
        "........",
        "......b.",
        "........",
        "......w.",
        "...b....",
        "........",
        "........",
        "......W.",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("g5–h6"),
          hintMove = CheckersMove.simple("g5", "h6"),
          hintText = "Wait check! If g5(3,6) -> h6(2,7): g7(1,6) jumps h6? Behind h6 is RIGHT BORDER (col 7), cannot jump!!",
        ),
      ),
      successExplanation = "",
    ).let {
      // Instead make Black g7 jump f6 -> e5! Put 'w' at g5 -> g5-f6!
      // Wait if g5(3,6)->f6(2,5): g7(1,6) jumps f6(2,5) -> lands e5(3,4)!
      // Put 'W' at g1(7,6)? No, to hit e5(3,4) AND d4(4,3) put 'b' at d4? No wait from e5, White King at a1(7,0) jumps d4? No, diagonal a1-h8 passes through e5(3,4) AND g7(empty now)!
      // Add a second Black on c5(3,2) or f6? Wait if 'b' at c7(1,2) and 'W' at h2(6,7):
      // Trace h2(6,7)->g3->f4->e5(now 'b'!)->d6(2,3)->c7(1,2)! Wait if White King is at h2(6,7), it takes e5->d6 or b8! Put second Black on b6(2,1) -> wait from d6(2,3) isn't on same diag as b6. Put second Black on b8? No man on b8. Put second Black on e7(1,4) -> no. Put second Black on c5(3,2)! Wait from e5(3,4) to c5 is horizontal. From d6(2,3) to b4? Yes! d6(2,3)->c5(3,2='b')->b4(4,1)!!
      // Check: White man g5(3,6), White King h2(6,7). Black g7(1,6), Black c5(3,2).
      // Initial: any White capture? h2->g3->f4->e5->d6->c7->b8 (no), h2->g1 (no). g5 has empty f6,h6 -> no!
      // Step 1: g5–f6!
      // Black to move: g7(1,6)->f6(2,5)->e5(3,4=EMPTY!) => g7:e5 LEGAL! Does c5(3,2) have any capture? NO!
      // So 1... g7:e5 is UNIQUE FORCED!
      // Step 2: h2(6,7) King captures OVER e5(3,4) -> lands d6(2,3) -> captures OVER c5(3,2) -> lands b4(4,1) or a3(5,0)!!
      LogicPuzzle(
        id = "puzzle_35_hard_king_double_cut",
        order = 35,
        title = "Поворот за жертвой",
        themeTag = "Завлечение под Дамку",
        difficulty = PuzzleDifficulty.HARD,
        prompt = "Замани крайнюю чёрную шашку g7 в центр под двойной выстрел своей Дамки h2!",
        initialRows = listOf(
          "........",
          "......b.",
          "........",
          "..b...w.",
          "........",
          "........",
          ".......W",
          "........",
        ),
        steps = listOf(
          PuzzleStep(
            acceptedNotations = setOf("g5–f6"),
            hintMove = CheckersMove.simple("g5", "f6"),
            hintText = "Шаг 1: пожертвуй g5–f6! Чёрная g7 прыгнет на e5, прямо под твою Дамку h2!",
            botAutoReply = CheckersMove.captureChain(Triple("g7", "e5", "f6")),
            replyCaption = "Клетка e5 занята чёрными — сбивай Дамкой обе шашки с поворотом на d6!",
          ),
          PuzzleStep(
            acceptedNotations = setOf("h2:d6:b4", "h2:d6:a3"),
            hintMove = CheckersMove.captureChain(
              Triple("h2", "d6", "e5"),
              Triple("d6", "b4", "c5"),
            ),
            hintText = "Шаг 2: лети Дамкой h2 → на d6 → через c5 на b4 или a3!",
          ),
        ),
        successExplanation = "Молниеносный перехват! Остановка на d6 позволила забрать обе шашки одним ударом!",
      )
    },
  )
}
