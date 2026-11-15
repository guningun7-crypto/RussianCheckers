package com.example.domain.curriculum

import com.example.domain.checkers.CheckersMove

object MasterPuzzlesPack {
  val puzzles: List<LogicPuzzle> = listOf(
    LogicPuzzle(
      id = "puzzle_36_master_3step_sieve",
      order = 36,
      title = "Трёхходовый прорыв в дамки",
      themeTag = "Мастерский расчёт",
      difficulty = PuzzleDifficulty.MASTER,
      prompt = "Комбинация в 3 хода! Сначала выстрой двойную приманку, а на 3-м ходу ворвись через 3 шашки в дамки!",
      initialRows = listOf(
        "........",
        "..b.....",
        "........",
        "..b...b.",
        "........",
        "....w...",
        ".....w..",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("f2–g3"),
          hintMove = CheckersMove.simple("f2", "g3"),
          hintText = "Wait: check! Initially White has e3(w) and f2(w), Black has g5, c5, c7. Put White on e1(7,4) instead of f2(6,5)! Wait let's design clean 3-step: White d2(6,3), e3(5,4), c1(7,2); Black b6(2,1), d6(2,3), g5(3,6), c7(1,2)? Let's verify exact 3 steps:",
        ),
      ),
      successExplanation = "",
    ).let {
      // Clean 2-step MASTER #36: Windmill 4-piece symmetric mirror!
      // White: c5(3,2), e5(3,4)? No: Mirror of #19 (King on h6(2,7) instead of a3(5,0)!)
      // Trace mirror across center: Row = 7-r, Col = 7-c -> White Kings on h6(2,7):
      // Wait Black has pieces at g3(5,6), e3(5,4), c3(5,2), f4(4,5), e7(1,4). White has c5(3,2), e5(3,4)? No mirror White men c5(3,2), e5(3,4) -> Black captures UPWARDS? No Black captures BOTH up & down!
      // Verify: Black at g3(5,6), e3(5,4), c3(5,2) -> wait mirror of row 2(".b.b.b.."): c6,e6,g6 -> row 5 ("..b.b.b."): c3,e3,g3! Mirror of row 3 ("..b....."): c5 -> row 4 (".....b.."): f4! Mirror of row 4 ("...w.w.."): d4,f4 -> row 3 ("..w.w..."): c5,e5! Mirror of row 5 ("W......."): a3 -> row 2 (".......W"): h6! Mirror of row 6 ("...b...."): d2 -> row 1 ("....b..."): e7!
      // Let's trace White h6(W), c5(w), e5(w) vs Black e7(b), f4(b), g3(b), e3(b), c3(b)!
      // Move 1: c5–d6! (White advances UP from row 3 to row 2 d6!Wait is d6 legal? Yes!). Wait Black e3(5,4)? No, e3(5,4) captures f4(4,5)->g5? No, f4 is black!
      // What about Black c3(5,2) can't jump. Black e7(1,4) captures d6(2,3) -> lands c5(3,2=EMPTY!)! What about Black f4(4,5)? Doesn't touch d6! What about Black e3(5,4)? Doesn't touch d6!
      // Wait does White have any captures BEFORE 1. c5–d6?!
      // Check White c5(3,2): touches f4? NO. Check White e5(3,4): touches f4(4,5)!! Behind f4 is g3(5,6='b' BLOCKED!). What about White h6(W, 2,7)? Diagonals from h6: (2,7)->(1,6)->(0,5) clear; (2,7)->(3,6)->(4,5='b' f4!!)->(5,4='b' e3)! Since g5(3,6) is empty, h6 WOULD jump f4! Look: f4 to e3 is ADJACENT, so f4 is blocked from landing g5->e3? Wait behind f4(4,5) is e3(5,4='b' OCCUPIED!!). So h6 CANNOT land behind f4!!
      // THEREFORE White has NO captures initially!!
      // Now White plays 1. c5–d6! (d6 is empty, c5->d6 is forward!).
      // Black to move: e7(1,4) captures OVER d6(2,3) -> lands c5(3,2=EMPTY!)! Does any other Black have capture? f4(4,5) touches e5(3,4='w') -> behind e5 is d6(2,3='w' OCCUPIED!!). So f4 CANNOT capture e5!
      // Therefore 1... e7:c5 is Black's 100% UNIQUE FORCED MOVE!!
      // Now look after 1... e7:c5 (e7 removed, c5 occupied by 'b', d6 removed!):
      // White King h6(2,7) captures OVER f4(4,5)... WAIT c5(3,2) is NOT on h6-e3 diag, f4(4,5) still has e3(5,4) behind it!
      // Let's trace #19 carefully to see how it worked: f4(4,5)->e5(3,4), d6(2,3):f4(4,5) -> then a3(5,0) jumps OVER c5(3,2) -> e7(1,4) -> g5(3,6) [over f6] -> e3(5,4) [over f4] -> c1(7,2) [over d2]!!
      // Oh! Because on the mirror, the targets are c5, d6? Wait: mirror of a3(5,0):e7(1,4):g5(3,6):e3(5,4):c1(7,2) (capturing c5, f6, f4, d2) is h6(2,7):d2(6,3):b4(4,1):d6(2,3):f8(0,5) (capturing f4, c3, c5, e7)!
      // For that mirror: Black must stand at c3(5,2), c5(3,2), e7(1,4), AND e5(3,4) (which jumps to c5? Wait: mirror of d6:f4(over e5) is e3(5,4):c5(3,2)(over d4)!!). Wait f4 is already at (4,5) before e3:c5? If f4 is at f4(4,5), mirror of White c5->d4 (wait: #19 was White f4->e5! Mirror of White f4(4,5)->e5(3,4) is Black c5->d4, NOT White!). For White moving UP: White d4(4,3)->c5(3,2) = White! Wait if White moves c3(5,2)->d4(4,3) -> no, let's build a BRAND NEW clean 4-piece windmill or 3-step master tactic!
      // Check NEW clean 3-step MASTER puzzle #36:
      // Let White sacrifice TWICE! Wait:
      // Initial: White b2(6,1), e3(5,4), g3(5,6); Black b6(2,1), d6(2,3), f6(2,5), d4? No:
      // Initial: White a3(5,0), e3(5,4), f2(6,5); Black c5(3,2), e7(1,4), f6(2,5), g7(1,6).
      // Wait simple 3-step:
      // White: d2(6,3), e3(5,4), g3(5,6). Black: c7(1,2), d6(2,3), e5(3,4)? No e3 touches e5? No.
      // Let's craft a clean 2-step Windmill with King on c1(7,2) or h4(4,7)!
      // King h4(4,7) loops h4(4,7) -> d8(0,3) [over f6(2,5)] -> a5(3,0) [over b6(2,1)? wait d8(0,3)->c7->b6(2,1)->a5(3,0)!!] -> e1(7,4) [over c3(5,2)] -> h4(4,7) [over g3(5,6)]!!
      // Check square coords of THAT windmill!!
      // Targets: f6(2,5), b6(2,1), c3(5,2), g3(5,6)! Wait: from d8(0,3) to a5(3,0) over c7/b6 — both b6(2,1) and c7? Check diag: (0,3),(1,2),(2,1),(3,0) = d8-c7-b6-a5! Over b6(2,1) lands a5(3,0)!
      // From a5(3,0) -> (4,1)=b4 -> (5,2)=c3(b) -> (6,3)=d2 -> (7,4)=e1! Wait behind c3(5,2) is d2(6,3) AND e1(7,4)!
      // From e1(7,4) -> (6,5)=f2 -> (5,6)=g3(b) -> (4,7)=h4!! Wait from d2(6,3) it can't turn onto e1-h4, ONLY e1(7,4) connects to g3(5,6)!
      // Wait put White at f4(4,5)?? No, check before setup:
      // Put White King at h4(4,7), Black at f6(2,5), b6(2,1), c3(5,2), AND Black g5(3,6)?? Wait let's make g3(5,6) the 4th piece created by botAutoReply!
      // How does Black step onto g3(5,6)? Black from e5(3,4) jumps f4(4,5='w') -> g3(5,6)!!
      // Wait: if White has g3 initially empty, e5(3,4), f4(4,5='w'), and White h2(6,7)? No, f4(4,5) -> White moves e3(5,4)->f4(4,5)? Wait:
      // Put White at e3(5,4) and White at c5? No: White e3(5,4) moves to f4(4,5), Black e5(3,4) captures OVER f4(4,5) -> lands g3(5,6)!!
      // Check if h4(4,7) initially had a capture! h4(4,7)->g5(3,6)->f6(2,5)->e7(1,4)->d8(0,3)!! Wait BEFORE 1. e3-f4, h4 ALREADY had f6:d8:a5!! We must block h4 from jumping f6 before move 1!
      // Block f6 with e7? No! Put White man on g5(3,6)? If White man on g5, e3-f4 doesn't affect g5. Put Black on g5? No, f6 is blocked by g5(w) or b? If White has g5(3,6) -> h4(4,7) is blocked by g5! Wait what if White plays 1. g5–h6? No! What if White King is at h4, and f6 IS blocked by Black e7? No, put White on e7! No! Put White on f2(6,5) and g3(5,6) empty -> wait: e3–f4 doesn't unblock g5, UNLESS Black e5 jumps OVER f4? No, if White has e5(3,4)? No, White plays 1. g5–f6? No.
      // Easiest way to block h4(W) initially: put Black at g5(3,6) too?! No, if Black at g5 and f6, adjacent can't be jumped! And if Black at g5 moves via g5:e3? Make Black g5 jump OVER White f4 -> e3!!
      // Wait then targets are b6(2,1), f6(2,5), c3(5,2), e3(5,4) — does a King capture b6, f6, c3, e3? Check loop:
      // h4(4,7) -> d8(0,3)[f6] -> a5(3,0)[b6] -> d2(6,3)[c3] -> f4? Let's put Black at e3(5,4) -> from d2(6,3) is it collinear with e3? No!
      // Make Black g5 jump over f4 onto e3, wait what if King sits at h2(6,7)?
      // Loop from c1(7,2): c1(7,2) -> g5(3,6)[over e3(5,4)] -> e7(1,4)[over f6(2,5)] -> a3(5,0)[over c5(3,2)] -> c1(7,2)[over b2(6,1)]!!
      // CHECK THOSE 4 TARGETS ON THAT LOOP: e3(5,4), f6(2,5), c5(3,2), b2(6,1)!
      // Look at White King at c1(7,2): currently blocked from e3 by d2(6,3='w')? Wait! Look! Put White at d2(6,3)!!
      // Step 1: d2–e3? Wait if d2–e3, then d2 vacates, e3 occupied by White -> Black f4(4,5) jumps OVER e3(5,4) -> lands d2(6,3)!!
      // WAIT A MINUTE!! Then d2(6,3) isn't e3, wait: look at e1(7,4)! White d2(6,3) -> c3(5,2), wait:
      // What if Black is at d4(4,3) -> White plays d2–c3? No!
      // Put White at d2(6,3) and White at f4(4,5)? Wait in #19, how was Black kept from jumping? Black had d6(2,3), White had d4(4,3) and f4(4,5), King was a3(5,0). Move 1 was f4–e5, Black d6:f4. And why didn't a3(5,0) capture c5(3,2) on Move 1?! Because c5 was blocked by d6(2,3) behind it!!
      // THAT'S SO CLEAN! c5(3,2) + d6(2,3) ADJACENT blocked a3 from capturing c5 on move 1!!
      // Use the SAME principle on the RIGHT side (vertical/diagonal symmetry)!
      // Let's use horizontal symmetry of #19 (flip cols a<->h):
      // Row 2: "..b.b.b." (c6, e6, g6) -> Row 3: ".....b.." (f5) -> Row 4: "..w.w..." (c4, e4) -> Row 5: ".......W" (h3) -> Row 6: "....b..." (e2).
      // Verify EVERY move on horizontal flip of #19!
      // White: h3(W, 5,7), c4(w, 4,2), e4(w, 4,4). Black: c6(b, 2,2), e6(b, 2,4), g6(b, 2,6), f5(b, 3,5), e2(b, 6,4).
      // Dark squares check:
      // h3(5,7=12? EVEN!! WAIT 5+7=12 IS LIGHT!). Good catch!! Horizontal flip swaps dark and light squares! To preserve dark squares, you must flip BOTH vertically AND horizontally (180-degree rotation, i.e., r'=7-r, c'=7-c), OR shift 1 column!
      // Wait in Puzzle #36 we want a genuine 3-step master puzzle OR brand-new 2-step master puzzle! Let's build a crisp 2-step or 3-step master puzzle directly on dark squares!
      // Let's design #36 ("Двойной шлагбаум") — 2-step MASTER:
      // White King f2(6,5), White man d4(4,3), White man b4(4,1).
      // Black: b6(2,1), d6(2,3), f6(2,5), e7(1,4).
      // Check squares: f2(6,5=11), d4(4,3=7), b4(4,1=5), b6(2,1=3), d6(2,3=5), f6(2,5=7), e7(1,4=5). ALL DARK!
      // Before move 1: White f2(W) diagonals: (6,5)->(5,6)(g3)->(4,7)(h4) clear; (6,5)->(5,4)(e3)->(4,3='w' d4!). Can d4 capture? d4(4,3) sees c5, e5 empty. Can b4(4,1) capture? Sees a5, c5 empty! Wait NO captures for White!
      // Move 1: d4–e5!!
      // Black to move: d6(2,3)->e5(3,4='w')->f4(4,5=EMPTY!) -> d6:f4! What about f6(2,5)->e5(3,4='w')->d4(4,3=EMPTY!) -> f6:d4! Wait make f6 NOT jump d4? Put White on d2? No, put White on f4(4,5)? Wait: if White is on f4(4,5), d6 can't land on f4, ONLY f6:d4 works!!
      // Wait let's trace f6:d4 when White has f4(4,5='w'), f2(W, 6,5), d4(4,3='w'):
      // Wait if White has f4(w), d4(w), f2(W), b4(w) vs Black d6, f6, c7(1,2), a5(3,0):
      // Wait after 1. d4–e5: Black f6:d4 (unique!). Then White f2(W) jumps OVER f4? No, f4 is 'w'. f2(6,5)->e3(5,4)->d4(4,3='b') -> lands c5(3,2) -> jumps d6(2,3)? NO, c5 to d6 is adjacent! Put Black on b6(2,1) -> c5(3,2) jumps b6(2,1) -> lands a7(1,0)!! Wait can it continue from a7? Yes if Black is on c5? No, put Black on b6(2,1) AND d6(2,3) -> wait from c5(3,2) to b6 is col-1, row-1. Where to jump NEXT after a7? Put Black on... a7 is edge!
      // Instead jump to b6(2,1)? Land at b6(2,1) by putting Black at c5? No, d4(4,3) -> land at c5(3,2) OR b6(2,1)? No, d6(2,3) is not on a1-h8.
      // What if f2(6,5) jumps d4(4,3) and lands at b6(2,1)? If White King is f2(6,5), diagonal is f2-e3-d4-c5-b6-a7! So it MUST jump OVER d4(4,3), landing at c5, b6, or a7!
      // If it lands at b6(2,1) -> wait if Black is at c7(1,2)? b6->c7->d8? Put Black at c7(1,2) and d6(2,3)? No, if Black at c7(1,2) AND e7(1,4)? From b6(2,1) -> c7(1,2) -> d8(0,3)!
      // Put Black at e7(1,4) -> from d8(0,3) -> e7(1,4) -> f6? No f6 jumped to d4! Put Black at g5(3,6)!!
      // Check path from d8(0,3) -> e7(1,4='b') -> f6 -> g5? Put Black at f6? No f6 moved to d4 -> put Black at e7(1,4)? From d8(0,3) over e7(1,4) lands f6(2,5) -> from f6(2,5) over g5? Or lands g5(3,6) -> OVER f4? Let's trace:
      // f2(W, 6,5) -> jumps d4(4,3='b') -> lands c5(3,2) -> jumps b6(2,1='b') -> lands a7(1,0)?? No: puts King on h2(6,7)? Let's just use our test runner mindset: check coordinates cleanly!
      // Path of 3-capture King shot:
      // King starts e1(7,4):
      // 1) Jumps e3(5,4)? No diag: (7,4)->(6,5)->(5,6) [g3] -> (4,7) [h4]. Better: e1(7,4)->(6,3)[d2]->(5,2)[c3]->(4,1)[b4]->(3,0)[a5]!
      // Try King a1(7,0):
      // a1(7,0) -> jumps c3(5,2) -> lands e5(3,4) -> jumps d6(2,3) -> lands c7(1,2) or b8(0,1)? Wait if lands b8(0,1) -> doesn't continue. What if lands c7? Still adjacent to d8. What if lands e5(3,4) -> jumps f6(2,5) -> lands g7(1,6)?
      // Actually look at this awesome 3-piece + pawn sacrifice combo (2 moves):
      // White: b2(6,1='W'), d4(4,3='w'), f4(4,5='w'). Black: d6(2,3), f6(2,5), g7(1,6), f8? No, g7(1,6) -> h8? Put Black at c7(1,2) + e7(1,4) -> no. Put Black at d6(2,3), f6(2,5), b4(4,1)? Look: b4(4,1) is on diag c5-b4-a3!
      // Wait after 1. d4–e5, 1... f6:d4: Black moved to d4(4,3), while d6 is STILL at (2,3), put Black at b6(2,1) and Black at c3(5,2)? No, b2(6,1) can't cross c3 unless c3 is 'b'!Wait put Black at c3(5,2) -> then b2 jumps immediately!
      // Put White King h2(6,7)! White men d4(4,3), f4(4,5). Black: d6(2,3), f6(2,5), b6(2,1), c5(3,2)? No, put Black at b4(4,1)? Yes!
      // Check path from h2(6,7):
      // h2(6,7) -> f4? Wait (6,7)->(5,6)->(4,5)=f4('w' blocks!). Put White at f4? If White is at f4, h2 is blocked from d6! Put White at b2? No. Put White King on a1(7,0), White men d4(4,3), f4(4,5):
      // On move 1: White d4–e5! Black f6:d4!
      // Now a1(7,0) jumps OVER d4(4,3), lands e5(3,4), jumps OVER d6(2,3), lands c7(1,2)?Wait if Black is also at c5? No! What if from d4(4,3) a1(7,0) lands on f6(2,5) -> jumps e7(1,4) -> lands d8(0,3)!!
      // AND from d8(0,3) jumps c7(1,2)? If Black is at c7(1,2), lands b6(2,1) OR a5(3,0)!! Wait put Black at b4(4,1)? Then lands a5(3,0) -> no! Put Black at d4(now captured), c5(3,2) -> b6(2,1) jumps c5(3,2) -> lands d4? Wait d4 is JUST captured (removed at end only!).
      // Let's trace 3-capture from f2? Wait put Black at d4(by f6:d4), e7(1,4), c7(1,2)! Check d8(0,3)->c7(1,2)->b6(2,1)->a5(3,0)! Does a1:f6:d8:a5 capture 3 pieces? YES:
      // Leg 1: a1(7,0) -> over d4(4,3='b') -> land f6(2,5) [must be empty - YES, f6 jumped to d4!!]
      // Leg 2: f6(2,5) -> over e7(1,4='b') -> land d8(0,3)
      // Leg 3: d8(0,3) -> over c7(1,2='b') -> land b6(2,1) or a5(3,0)!
      // Can a1 stop at e5 after d4? e5(3,4)->d6(2,3) if d6 is empty, ONLY f6 continues! (Keep d6 EMPTY? No if d6 empty, Black has ONLY f6, so put Black at f6, e7, c7, AND keep d6 empty!! Then wait why wouldn't Black f6 jump f6:d4? Look: f6(2,5)->e5(3,4='w')->d4(4,3=EMPTY!) => 1... f6:d4 is Black's ONLY legal move since f4 is empty too? Wait e7(1,4) touches nothing, c7(1,2) touches nothing!)
      // Wait if d6 is empty, White doesn't even need f4(w)! Wait YES check: if f4 is empty and d6 is empty, Black ONLY has f6 -> e5!
      // Let's double check! Initial:
      // White: a1(W), d4(w). Black: f6(b), e7(b), c7(b)! (And e5 is empty).
      // Before move 1: a1(7,0) sees b2,c3,d4(w) — blocked! d4(4,3) sees c5,e5 — empty! So NO White capture!
      // Move 1: d4–e5!
      // Black to move: f6(2,5) captures e5(3,4) -> lands d4(4,3=EMPTY!) => f6:d4 UNIQUE FORCED! (c7, e7 have no capture).
      // After 1... f6:d4: a1(W) jumps OVER d4 -> MUST land f6(2,5) (wait can it land e5? Wait if d6 is empty, e5 has no further capture! What about g7? Beyond e7! Wait can a1 land h8? No, e7 is between f6 and g7? No, e7 is (1,4), f6 is (2,5), g7 is (1,6)! Wait (2,5)->(1,4)->(0,3) is orthogonal-ish? YES! row-1, col-1: (2,5)->(1,4)->(0,3) is a straight 45° diagonal f6-e7-d8!!).
      // And from a1(7,0): e5 is (3,4), f6 is (2,5), g7 is (1,6), h8 is (0,7)! Does RussianCheckersEngine force maximum chain length or any landing square that continues capturing? In Russian Checkers: if ANY continuation exists from a landing square (e.g., f6 continues to d8:b6!), you MUST pick a landing square that continues (so you CANNOT stop at e5 or g7 or h8 if f6 allows another jump — WAIT in Russian Checkers you aren't required to pick the MAX number of captures globally, you just can't stop early on a path where further jumps exist? Actually, `RussianCheckersEngine` only generates `a1:f6:d8:b6` and `a1:f6:d8:a5` because from f6 you CAN continue, and from e5/g7/h8 you CANNOT capture more? Let's verify in `RussianCheckersEngine` whether a King can choose e5 (stopping after 1 capture) vs f6 (taking 3)! Remember in Test: `flyingKing_mustChooseLandingSquareThatContinuesCapture`: on `(7,0)` capturing `(5,2)` then `(1,2)` -> `assertEquals(listOf("a1:e5:b8"), moves.map { it.notationString })` — YES! Our engine REQUIRES choosing landing squares that continue capturing!
      // Wait: what if `f6:d8:b6` AND `f6:d8:a5`? Both are 3-captures! So `acceptedNotations = setOf("a1:f6:d8:b6", "a1:f6:d8:a5")`!!
      LogicPuzzle(
        id = "puzzle_36_master_3step_sieve",
        order = 36,
        title = "Поворотная мельница с а1",
        themeTag = "Точное приземление дамки",
        difficulty = PuzzleDifficulty.MASTER,
        prompt = "Жертвой раскрой дорогу для Дамки a1 так, чтобы она приземлилась на нужном перекрёстке и собрала все 3 фигуры!",
        initialRows = listOf(
          "........",
          "..b.b...",
          ".....b..",
          "........",
          "...w....",
          "........",
          "........",
          "W.......",
        ),
        steps = listOf(
          PuzzleStep(
            acceptedNotations = setOf("d4–e5"),
            hintMove = CheckersMove.simple("d4", "e5"),
            hintText = "Шаг 1: пожертвуй d4–e5! Чёрная f6 прыгнет на d4 и освободит перекрёсток f6!",
            botAutoReply = CheckersMove.captureChain(Triple("f6", "d4", "e5")),
            replyCaption = "Клетка f6 освободилась, а d4 стала мишенью! Лети Дамкой a1 по точному зигзагу!",
          ),
          PuzzleStep(
            acceptedNotations = setOf("a1:f6:d8:b6", "a1:f6:d8:a5"),
            hintMove = CheckersMove.captureChain(
              Triple("a1", "f6", "d4"),
              Triple("f6", "d8", "e7"),
              Triple("d8", "b6", "c7"),
            ),
            hintText = "Шаг 2: прыгай Дамкой a1 → f6 → d8 → b6 (или a5), срубая все 3 шашки!",
          ),
        ),
        successExplanation = "Гроссмейстерская геометрия! Дамка использовала освобождённое поле f6 для тройного удара!",
      )
    },

    LogicPuzzle(
      id = "puzzle_37_master_lock_squeeze",
      order = 37,
      title = "Смертельный столбняк",
      themeTag = "Лишение ходов",
      difficulty = PuzzleDifficulty.MASTER,
      prompt = "Комбинация без лишнего боя! Отдай шашку так, чтобы замуровать соперника или заставить его отдать проход в 2 хода!",
      initialRows = listOf(
        "........",
        "........",
        ".....b..",
        "........",
        "...w.w..",
        "....w...",
        "........",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("d4–e5"),
          hintMove = CheckersMove.simple("d4", "e5"),
          hintText = "Шаг 1: шагни d4–e5! У чёрной f6 единственный бой на d4 — прямо под тройную вилку... стоп, проверь f4: останемся на e5!",
          botAutoReply = CheckersMove.captureChain(Triple("f6", "d4", "e5")),
          replyCaption = "Wait — in this board Black only has 1 piece so after e3:c5 White wins on count? No let's add Black d6 AND e7 so step 2 captures TWO toward King!",
        ),
      ),
      successExplanation = "",
    ).let {
      // Better Master #37: Mid-jump promotion + 3-piece capture!
      // White: c5(3,2='w'), d2(6,3='w'), e3(5,4='w'). Black: b6(2,1), e7(1,4), g5(3,6), f4(4,5)?
      // No: White: b4(4,1='w'), e3(5,4='w'). Black: b6(2,1), d6(2,3), f6(2,5), f4(4,5)? Wait make Black jump OVER White e3->d4!
      // Let's do:
      // White: b6(2,1='w'), c3(5,2='w'), g3(5,6='w').
      // Wait simple awesome 2-step promotion-in-flight puzzle:
      // White: a5(3,0='w'), e3(5,4='w'). Black: b6(2,1), d6(2,3), f6(2,5), g7(1,6), f4(4,5)...
      // How to make Step 1 a quiet move setting up a mid-jump promotion to d8 + long King capture?
      // Look! White b6(2,1='w') ALREADY faces c7(1,2='b'), unless c7 isn't there yet! Put Black on b8(0,1)? No, put White on a5(3,0='w') and Black on c7(1,2='b') and e5(3,4='b') and f6(2,5='b')?
      // Step 1: White d4(4,3) -> Wait:
      // White: a5(3,0='w'), d4(4,3='w'), f2(6,5='w'). Black: c7(1,2='b'), e7(1,4='b'), d6(2,3='b'), e5(3,4)? No:
      // What if Black has b8(B)? No, make Black step ONTO c7(1,2) OR b6(2,1)?? Wait Black moves DOWN! So Black steps from d8? No, Black on b6(2,1) jumps OVER a5? No! Black on d6(2,3) jumps OVER White c5(3,2) -> lands b4(4,1)!
      // Trace THAT:
      // White a3(5,0='w'), c5(3,2='w'), d2(6,3='w'). Black d6(2,3='b'), c7(1,2='b'), e5(3,4='b')?
      // Put White c5(3,2) -> wait if Black d6(2,3) ALREADY sees c5(3,2) and b4 empty -> White c5 must move FIRST: 1. c5–b6!Wait if c5–b6, Black c7:a5? If White is on a5 no, put nothing on a5:
      // Initial: White b4(4,1='w'), d4(4,3='w'); Black c7(1,2='b'), a7(1,0='b'), f6(2,5='b'), e5(3,4='b').
      // Wait simple & gorgeous 2-step King conversion trap:
      // White: b6(2,1='w'), d4(4,3='w'). Black: d8? Can't! Black: d6(2,3), b8?
      // Try: White c5(3,2='w'), f4(4,5='w'), g3(5,6='w'). Black: b6(2,1='b')? No, c5 would jump b6! Put Black at d6? No, c5 jumps d6! Put Black at e7(1,4), c7(1,2), f6(2,5).
      // Wait: if White is on b4(4,1), d4(4,3), a3(5,0) and Black is on a5(3,0)? No, b4 jumps a5!
      // Just check BEFORE Move 1 that White has ZERO captures! So no 'w' can sit diagonally next to 'b' with empty behind 'b'!
      // Example: Black at c5(3,2), e5(3,4), c7(1,2), g7(1,6). White at d2(6,3), f2(6,5), a5(3,0), e3(5,4).
      // Step 1: e3–d4! -> ONLY Black capture is c5:e3? Wait d4 is next to BOTH c5 and e5! If c3 is White('w'), then e5:c3 is BLOCKED, so c5:e3 is UNIQUE FORCED!
      // Now after 1... c5:e3 (Black on e3, c5 empty!):
      // White f2(6,5) -> e3(5,4='b') -> d4(4,3=empty) -> e5(3,4='b') -> f6(2,5=empty) -> g7(1,6='b') -> h8(0,7 King) -> AND from h8(0,7), look down h8-a1 or add Black on f4? No, c7(1,2) is far. Add Black on c5? No!
      // Add Black on e3? Wait after h8(0,7, King!), put Black on e5(now captured), g7(now captured), what about f6? Can h8 capture anything else? Only on h8-a1 diag! On h8-a1 diag sits f6(empty), e5(just jumped!), d4(empty), c3(w!), b2! Wait if c3 is EMPTY and White is on c1? No, we needed c3 occupied to force c5:e3! Instead make it b2/d4? No — if White d2(6,3) is there, make e5:c3 the forced one (put White on e3, so c5:e3 is BLOCKED by e3, and e5:c3 IS forced!).
      // Then c3(now 'b') can be jumped by b2(6,1)!! Check b2(6,1):
      // b2(6,1) -> c3(5,2='b') -> d4(4,3) -> c5(3,2='b') -> b6(2,1) -> c7(1,2='b') -> d8(0,3 King!)
      // NOW FROM d8(0,3, KING!), look at d8-h4 diagonal: f6(2,5='b') -> g5(3,6) -> h4(4,7)!!
      // WAIT A SECOND! CHECK THAT 4-PIECE KING PROMOTION COMBO IN MASTER #37!!
      // Initial pieces:
      // White: b2(6,1), c3(5,2), e3(5,4).
      // Black: c5(3,2), e5(3,4), c7(1,2), f6(2,5)!
      // Check ANY capture before Move 1:
      // White b2, c3, e3 (rows 5 & 6). Black c5, e5, c7, f6 (rows 1, 2, 3). Row gap 5-3=2 -> NO captures!
      // White plays 1. c3–d4! (c3 becomes empty, d4 occupied).
      // Check Black captures:
      // - c5(3,2) -> d4(4,3='w') -> e3(5,4='w' BLOCKED!). NO capture!
      // - e5(3,4) -> d4(4,3='w') -> c3(5,2=EMPTY!) => e5:c3 LEGAL & UNIQUE FORCED!
      // Bot plays 1... e5:c3! (c3='b', d4=empty, e5=empty).
      // White to move: Check b2(6,1) and e3(5,4):
      // - e3(5,4) touches d4(empty), f4(empty). NO capture!
      // - b2(6,1) jumps c3(5,2='b') -> d4(4,3) -> jumps c5(3,2='b') -> b6(2,1) -> jumps c7(1,2='b') -> d8(0,3, PROMOTES TO KING ON THE FLY!!) -> jumps f6(2,5='b') -> lands g5(3,6) or h4(4,7)!!
      // 4 PIECES CAPTURED WITH MID-JUMP PROMOTION TO KING IN 1 CONTINUOUS CHAIN!!
      LogicPuzzle(
        id = "puzzle_37_master_lock_squeeze",
        order = 37,
        title = "Четыре цели с превращением",
        themeTag = "Каскад 4-в-1 в дамки",
        difficulty = PuzzleDifficulty.MASTER,
        prompt = "Великолепный мастерский удар: простая шашка жертвует позицию, а бьющая проходит 3 шашки, на лету становится Дамкой на d8 и тут же забирает 4-ю цель!",
        initialRows = listOf(
          "........",
          "..b.....",
          ".....b..",
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
            hintText = "Шаг 1: шагни c3–d4! Шашка e3 закроет c5, поэтому e5 обязана прыгнуть на c3!",
            botAutoReply = CheckersMove.captureChain(Triple("e5", "c3", "d4")),
            replyCaption = "Мост построен! Веди b2 через три шашки на d8 (Дамка) и сразу на другую диагональ через f6!",
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
            hintText = "Шаг 2: бей b2 → d4 → b6 → d8 (Дамка!) → g5 (или h4)!",
          ),
        ),
        successExplanation = "Грандиозно! На лету став Дамкой на d8, белая шашка тут же повернула на другую диагональ и смела 4 шашки!",
      )
    },

    LogicPuzzle(
      id = "puzzle_38_master_mirror_4_sweep",
      order = 38,
      title = "Симметрия на правый фланг",
      themeTag = "4-кратный удар на h8",
      difficulty = PuzzleDifficulty.MASTER,
      prompt = "Найди 2-ходовую комбинацию с боем 3 шашек в Дамки на h8? Нет, в h8 и дальше уже дамкой назад через всю доску!",
      initialRows = listOf(
        "........",
        "......b.",
        "...b....",
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
          hintText = "Шаг 1: открой дорогу ходом e3–d4! Чёрная c5 побьёт на e3 (e5 заперта шашкой c3).",
          botAutoReply = CheckersMove.captureChain(Triple("c5", "e3", "d4")),
          replyCaption = "Клетка e3 готова! Забирай цепочку: f2 → d4 → f6 → h8 → и поворачивай дамкой через d6?!",
        ),
      ),
      successExplanation = "",
    ).let {
      // Check from h8(0,7) -> what diag exits h8? h8(0,7) -> g7(1,6) -> f6(2,5) -> e5(3,4) -> d4(4,3)!!
      // Remember f2(6,5)->d4(4,3) [over e3] -> f6(2,5) [over e5] -> h8(0,7) [over g7]!
      // Can h8 go backwards along h8-a1? e5 and g7 are STILL on the board as jumped pieces blocking h8-a1? YES! A King CANNOT cross over a piece it already jumped! So h8 CANNOT turn back down h8-a1!
      // Instead jump to f8(0,5) and turn down onto d6(2,3) or h6(2,7)?
      // Test: d2(6,3) -> f4(4,5) [over e3] -> d6(2,3) [over e5] -> f8(0,5, KING!) -> from f8(0,5), diag goes f8(0,5)->g7(1,6='b')->h6(2,7)!
      // Put Black at g7(1,6)! Wait: behind g7(1,6) is h6(2,7) EMPTY!
      // Verify:
      // Initial White: c3(5,2), e3(5,4), d2(6,3).
      // Initial Black: c5(3,2), e5(3,4), e7? NO, wait: d6->f8 jumps e7! Put Black at e7(1,4) AND g7(1,6)!
      // Trace:
      // 1. e3–d4! Black: c5(3,2)->d4->e3 (UNIQUE FORCED because c3 blocks e5:c3!).
      // 2. d2(6,3) -> over e3 -> f4(4,5) -> over e5 -> d6(2,3) -> over e7 -> f8(0,5, KING!) -> over g7(1,6) -> h6(2,7)!!
      // Check NO black interference and NO duplicate jump! Every leg is 100% crisp and legal!
      LogicPuzzle(
        id = "puzzle_38_master_mirror_4_sweep",
        order = 38,
        title = "Четверной вихрь на f8",
        themeTag = "Комбинация с Дамкой",
        difficulty = PuzzleDifficulty.MASTER,
        prompt = "Разыграй комбинацию из 4 взятий с превращением на f8 и добиванием крайней шашки g7!",
        initialRows = listOf(
          "........",
          "....b.b.",
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
            hintText = "Шаг 1: шагни e3–d4! Шашка c3 не пропустит e5, поэтому c5 прыгнет на e3!",
            botAutoReply = CheckersMove.captureChain(Triple("c5", "e3", "d4")),
            replyCaption = "Путь открыт! Пройди d2 → f4 → d6 → f8 (Дамка) → h6!",
          ),
          PuzzleStep(
            acceptedNotations = setOf("d2:f4:d6:f8:h6"),
            hintMove = CheckersMove.captureChain(
              Triple("d2", "f4", "e3"),
              Triple("f4", "d6", "e5"),
              Triple("d6", "f8", "e7"),
              Triple("f8", "h6", "g7"),
              promotesFlags = listOf(false, false, true, false),
            ),
            hintText = "Шаг 2: бей d2 → f4 → d6 → f8 (Дамка) → h6!",
          ),
        ),
        successExplanation = "Блестящий вихрь! 4 шашки срублены одним ходом с мгновенным разворотом Дамки!",
      )
    },

    LogicPuzzle(
      id = "puzzle_39_master_turkish_freeze",
      order = 39,
      title = "Ловушка турецкого удара",
      themeTag = "Запрет второго прохода",
      difficulty = PuzzleDifficulty.MASTER,
      prompt = "Дамка может побить в две стороны, но один из путей застрянет об ещё не снятую шашку! Выбери единственный путь на 3 шашки!",
      initialRows = listOf(
        "........",
        "....b...",
        ".....b..",
        "..b.....",
        "........",
        "....b...",
        ".....W..",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("f2:b6:f6"),
          hintMove = CheckersMove.captureChain(
            Triple("f2", "b6", "e3"),
            Triple("b6", "f6", "d6"),
          ),
          hintText = "Wait let's trace f2(6,5) carefully!",
        ),
      ),
      successExplanation = "",
    ).let {
      // Exact trace for puzzle_39 (1-step or 2-step subtle King path):
      // White King g1(7,6). Black pieces at: e3(5,4), c7(1,2), e7(1,4), f6(2,5) — wait let's check:
      // g1(7,6)->e3(5,4='b')->d4(4,3)->c5(3,2)->b6(2,1)->a7(1,0)??
      // Put Black at e3(5,4), c5(3,2)? No, e3 and c5 are same diag (blocking).
      // Put Black at e3(5,4), c7(1,2), f6(2,5), g3(5,6)?
      // Look: g1(7,6) -> jumps e3(5,4): can land d4(4,3), c5(3,2), b6(2,1), a7(1,0)!
      // Put Black at e7(1,4) -> from c5(3,2) -> d6(2,3) -> e7(1,4) -> lands f8(0,5)!Wait from b6(2,1) -> c7(1,2='b') -> d8(0,3)!
      // If Black is at e3(5,4), e7(1,4), f6(2,5), c3(5,2):
      // - Wait from d4(4,3) (jumping e3), could it go d4->e5->f6(2,5='b')->g7(1,6)! Wait c3(5,2) can't go from d4? Wait d4->c3->b2!
      // Even simpler & textbook:
      // White King a1(7,0). Black pieces: c3(5,2), d6(2,3), f6(2,5), g3(5,6)!
      // Trace a1(7,0) jumps OVER c3(5,2):
      // Possible landings: d4(4,3), e5(3,4), f6(2,5 occupied!), g7(1,6 blocked by f6!). So ONLY d4(4,3) or e5(3,4)!
      // Look at what happens from d4(4,3):
      //   d4(4,3) -> c5(3,2) -> b6(2,1) -> a7(1,0) (NO black!).
      //   d4(4,3) -> e5(3,4) -> f6(2,5) [on same diag? Yes, c3 blocked from hitting f6!].
      // Look at what happens from e5(3,4):
      //   e5(3,4) -> jumps d6(2,3='b') -> lands c7(1,2) or b8(0,1)!
      //   e5(3,4) -> jumps f6(2,5='b') -> lands g7(1,6) or h8(0,7)!
      // Now add Black at c5(3,2) NO, add Black at d4? No: from c7(1,2) -> no, from b6?
      // Better: put Black at c3(5,2), e7(1,4), f6(2,5), b4(4,1) + White g1(7,6)?
      // Let's trace 3-capture King square loop:
      // White King h2(6,7). Black: f4(4,5), c5(3,2), e7(1,4), g5(3,6) ->
      // From h2(6,7) -> jumps f4(4,5): landings e5(3,4), d6(2,3), c7(1,2), b8(0,1)!
      //   - At e5(3,4): no capture!
      //   - At d6(2,3): jumps c5(3,2) -> b4(4,1) or a3(5,0) [1 more capture = 2 total!]
      //   - At c7(1,2): no capture!
      //   - Now add Black at e5? No: add Black at e7(1,4) -> Wait from d6(2,3) if you go OVER c5(3,2) to b4(4,1) -> add Black at d2(6,3)!! Wait (4,1)->(5,2)[c3]->(6,3)[d2]->(7,4)[e1]!!
      //   Wait: check d6(2,3)->b4(4,1)[c5] -> MUST turn at b4(4,1)? No, can it land a3(5,0)? From a3(5,0) can it capture? Check (5,0)->(6,1)[b2]->(7,2)[c1]! Wait if Black is at d2(6,3), a3 doesn't see d2, ONLY c3 or b4 sees d2! Let's trace b4(4,1)->(5,2)[c3]->(6,3)[d2='b']->(7,4)[e1]!!
      //   Check: from c7(1,2) can h2 jump to f8? No!
      //   What if we put Black at g3(5,6) AND d2(6,3)? No!
      //   Let's keep it crystal clean: 3 captures total vs 2 captures trap!
      //   White King h2(6,7). Black: f4(4,5), c5(3,2), d2(6,3).
      //   Look: h2:d6:b4:e1 captures ALL THREE (f4, c5, d2)!
      //   Meanwhile if White stops at b8 or lands on a3 instead of b4, it only gets 2! AND because `RussianCheckersEngine` enforces continuation when captures remain along a leg, ONLY `h2:d6:b4:e1` continues all 3! Wait let's make `Step 1` a setup move (1... Bot auto reply!) so the player has to find BOTH the quiet setup AND the 3-capture King loop!
      //   Setup move on h2(6,7), Black f4(4,5), d6? No, White man on e3(5,4) moves 1. e3–d4 -> no, Black c5(3,2) moves!
      //   White man g5(3,6) -> 1. g5–f6 -> Black e7(1,4)? No:
      //   Look: White King h2(6,7), White man g3(5,6), Black e5(3,4), c5(3,2), e3(5,4).
      //   1. g3–f4! Black e5(3,4)->f4(4,5)->g3(5,6=EMPTY!) => e5:g3! No wait, Black doesn't jump to f4.
      //   Keep Puzzle #39 as a crisp 2-step King decoy + 3-piece capture!
      //   White King h2(6,7), White man g5(3,6). Black: g7(1,6), c5(3,2), d2(6,3)!
      //   Check White captures initially: h2(6,7) -> (5,6)->(4,5)->(3,4)->(2,3)->(1,2)->(0,1) CLEAR! h2->g1 CLEAR! g5(3,6) has empty f6, h6 -> CLEAR!
      //   Step 1: g5–f6!
      //   Black to move: g7(1,6)->f6(2,5='w')->e5(3,4=EMPTY!) => g7:e5 UNIQUE FORCED! (c5 and d2 have no captures).
      //   Step 2: h2(6,7) King captures OVER e5(3,4) -> lands d6(2,3) -> captures OVER c5(3,2) -> lands b4(4,1) -> captures OVER d2(6,3)? Check (4,1)->(5,2)[c3]->(6,3)[d2]->(7,4)[e1]!! Wait: ONLY landing b4(4,1) continues onto d2(6,3)->e1(7,4)! Landing a3(5,0) stops early, so engine rejects a3 and ONLY allows `h2:d6:b4:e1`!!
      LogicPuzzle(
        id = "puzzle_39_master_turkish_freeze",
        order = 39,
        title = "Треугольный прострел дамки",
        themeTag = "Точный поворот в 3 цели",
        difficulty = PuzzleDifficulty.MASTER,
        prompt = "Замани крайнюю чёрную шашку g7 в центр и проведи Дамку h2 по точному маршруту на 3 цели до e1!",
        initialRows = listOf(
          "........",
          "......b.",
          "........",
          "..b...w.",
          "........",
          "........",
          "...b...W",
          "........",
        ),
        steps = listOf(
          PuzzleStep(
            acceptedNotations = setOf("g5–f6"),
            hintMove = CheckersMove.simple("g5", "f6"),
            hintText = "Шаг 1: пожертвуй g5–f6! Чёрная g7 прыгнет на e5 прямо под твою Дамку h2!",
            botAutoReply = CheckersMove.captureChain(Triple("g7", "e5", "f6")),
            replyCaption = "Мишень на e5! Не проскочи мимо: остановись на d6 и b4, чтобы забрать все 3 шашки!",
          ),
          PuzzleStep(
            acceptedNotations = setOf("h2:d6:b4:e1"),
            hintMove = CheckersMove.captureChain(
              Triple("h2", "d6", "e5"),
              Triple("d6", "b4", "c5"),
              Triple("b4", "e1", "d2"),
            ),
            hintText = "Шаг 2: бей h2 → d6 → b4 → e1!",
          ),
        ),
        successExplanation = "Идеальная геометрия дамки! Маршрут h2:d6:b4:e1 собрал все три фигуры!",
      )
    },

    LogicPuzzle(
      id = "puzzle_40_master_king_catch_corner",
      order = 40,
      title = "Капкан в углу Петрова",
      themeTag = "Ловля дамки",
      difficulty = PuzzleDifficulty.MASTER,
      prompt = "Белая дамка отрезала вражескую дамку a7. Сделай тонкий ход жертвой-подставкой, чтобы немедленно поймать чёрную дамку!",
      initialRows = listOf(
        "........",
        "B.......",
        "........",
        "....w...",
        "........",
        ".......W",
        "........",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("e5–d6"),
          hintMove = CheckersMove.simple("e5", "d6"),
          hintText = "Wait: a7(1,0)->b6(2,1)->c5(3,2)->d4(4,3)->e3(5,4)->f2(6,5)->g1(7,6)! Wait d6(2,3) is NOT on a7-g1 diag! b6 is! Put White on c5(3,2) -> moves c5–b6!! Wait h3(5,7) is LIGHT, so put White King on h2(6,7)? No diag is b8-h2, put White King on g1(7,6)! No diag is a7-g1! If White King is on g1(7,6), then a7 sees White King? Put White man on d4(4,3) -> blocks a7 from seeing g1? YES! White plays 1. c5–b6? Wait c5(3,2) isn't on a7-g1: a7(1,0), b6(2,1), c5(3,2), d4(4,3), e3(5,4), f2(6,5), g1(7,6) ALL ON THAT SAME DIAG!!",
        ),
      ),
      successExplanation = "",
    ).let {
      // Wait if White King is at h4(4,7), a7-g1 intersects h4-d8.. wait a7(1,0)->b6(2,1)->c5(3,2)->d4(4,3)->e3(5,4)->f2(6,5)->g1(7,6)!
      // Put White King at c1(7,2): c1(7,2)-d2(6,3)-e3(5,4)-f4(4,5)-g5(3,6)-h6(2,7)! Intersects a7-g1 at e3(5,4)!
      // Put White King at a1(7,0): a1(7,0)-b2(6,1)-c3(5,2)-d4(4,3)-e5(3,4)-f6(2,5)-g7(1,6)-h8(0,7)! Intersects a7-g1 at d4(4,3)!!
      // TEST THAT! Black King at a7(1,0). White man at b4(4,1)?? Wait b4(4,1) moves to c5(3,2) -> wait if c5(3,2) is occupied by Black or White?
      // Initial: Black King a7(1,0). White man b4(4,1). White King a1(7,0).
      // Wait before move 1: a7(1,0)->b6(2,1)->c5(3,2)->d4(4,3)... CLEAR! No captures for White either!
      // Step 1: White moves b4–c5!
      // Wait Black a7(1,0) MUST jump c5(3,2) -> landings on d4(4,3), e3(5,4), f2(6,5), g1(7,6)!
      // Only d4(4,3) lies on a1-h8? Wait if Black lands e3(5,4), a1 can't capture! To prevent Black landing e3/f2/g1, put a piece at e3(5,4) — e.g. White man e3(5,4)? No, if e3 is occupied by 'w' or 'b', a7 CANNOT land e3, f2, g1 — it can ONLY land d4(4,3)!!
      // Check: Put White man at e3(5,4) (NO capture from e3!) or Black man at e3(5,4)! Better White man at e3(5,4): wait does Black at a7 capture e3? No, because c5(w) and e3(w) — wait between c5 and e3 is d4 empty! After a7:d4, can Black continue jumping e3? Behind e3 is f2 empty — YES! So Black would jump a7:d4:f2! Then White at g1 or h4 captures f2!
      // Wait if White King is at h4(4,7): h4(4,7)-g3(5,6)-f2(6,5)-e1(7,4)!
      // OR simply put TWO Black pieces or put White King on b2? No, make Black's ONLY capture land directly on d4(4,3) by putting TWO White pieces next to each other on e3? Wait if e3 AND f2 are both 'w', Black can jump OVER e3 because f2 blocks landing! So Black CAN only land on d4(4,3)!!
      // Wait check if White e3(5,4) captures anything? Empty d4, f4. f2(6,5) blocked.
      // Check a1(W, 7,0): blocked? Empty d4, a1 hits d4(4,3='b')!! Wait add a second Black piece on f6(2,5) so a1 captures BOTH d4 AND f6!!
      // CHECK:
      // White: a1(W), b4(w), e3(w), f2(w). Black: a7(B), f6(b)!
      // Before Move 1: White b4, e3, f2, a1(W). Diagonal a1-h8 has f6(2,5='b')!! A1 WOULD IMMEDIATELY JUMP f6 BEFORE MOVE 1! Move the second Black off a1-h8: put second Black at b8(0,1)? No, after jumping d4(4,3) to e5(3,4), turn onto b8-h2 diag: put second Black at c7(1,2)!!
      // Check c7(1,2) against White before Move 1: a1(7,0) doesn't see c7(1,2); b4(4,1) is far; e3,f2 far. What about Black a7(1,0)? Empty b6, c5, d4.
      // Step 1: b4–c5!
      // Black to move: a7(1,0) MUST capture c5(3,2='w')! Can it land d4(4,3)? YES! Can it land e3(5,4='w' occupied)? NO! Can it continue jumping e3(5,4) to f2(6,5='w' blocked)? NO!
      // So 1... a7:d4 is Black's 100% UNIQUE FORCED MOVE!!
      // Step 2: White King a1(7,0) jumps OVER d4(4,3='B') -> MUST land e5(3,4) or d6? (No, a1-h8 goes d4(4,3)->e5(3,4)! At e5(3,4) it sees c7(1,2) on e5-b8 diag? Wait e5(3,4)->d6(2,3)->c7(1,2='b')->b8(0,1)!! YES! Because from f6/g7/h8 it can't catch c7, `RussianCheckersEngine` FORCES `a1:e5:b8`!!)
      // Check if White e3(5,4) can jump d4(4,3='B') -> c5(3,2=EMPTY!)!
      // WHOA GOOD CATCH!! e3(5,4) ALSO touches d4(4,3) after 1... a7:d4!! Wait if BOTH a1:e5:b8 AND e3:c5 are legal White moves on Step 2, e3:c5 would ALSO be legal unless e3 is BLACK ('b') or d4 is jumped from elsewhere!
      // Put Black at e3(5,4) AND Black at f2(6,5)? No, if Black at f2, a1 still good, wait e3(b) and f2(b) are adjacent -> Black f2 won't jump e3! No, after 1... a7:d4, Black has e3(b) touching d4(b - same color!). And White ONLY has b4 (moved to c5 -> jumped -> removed!), and a1(W)!Wait can White e3 exist? NO, it's Black 'b' on e3 and Black 'b' on f2! Wait after a7:d4, can White `a1:e5:b8` happen AND `b8:e5`? No, d4 is jumped once. What about initial position BEFORE Move 1: does White b4(4,1) have any captures? No! Wait does Black a7(1,0) have captures? NO! Does Black e3(b) or f2(b) interfere with White b4–c5? NO, e3 captures nothing. Look — making e3 and f2 BLACK (`b`) makes `a1:e5:b8` White's 100% UNIQUE LEGAL MOVE on Step 2 AND adds another obstacle!!
      // Double check: on Step 2 (White to move), White ONLY has `a1(W)`! Its ONLY legal move is `a1:e5:b8`, capturing BOTH Black King d4 AND Black man c7 (actually wait: d4 is Black King, c7 is Black man; a1->e5(d4)->b8(c7))! What a clean, breathtaking MASTER study!!
      LogicPuzzle(
        id = "puzzle_40_master_king_catch_corner",
        order = 40,
        title = "Этюд с перекрытием",
        themeTag = "Ловля дамки на перекрёстке",
        difficulty = PuzzleDifficulty.MASTER,
        prompt = "Используй чужую колонну e3–f2 как стенку! Замани чёрную дамку a7 ровно на поле d4 под двойной удар твоей дамки a1!",
        initialRows = listOf(
          "........",
          "B.b.....",
          "........",
          "........",
          ".w......",
          "....b...",
          ".....b..",
          "W.......",
        ),
        steps = listOf(
          PuzzleStep(
            acceptedNotations = setOf("b4–c5"),
            hintMove = CheckersMove.simple("b4", "c5"),
            hintText = "Шаг 1: шагни b4–c5! Дамка a7 обязана бить c5, а сдвоенные шашки e3 и f2 не пропустят её дальше поля d4!",
            botAutoReply = CheckersMove.captureChain(Triple("a7", "d4", "c5")),
            replyCaption = "Чёрная дамка застряла на d4! Пробивай двойной выстрел через e5 на b8!",
          ),
          PuzzleStep(
            acceptedNotations = setOf("a1:e5:b8"),
            hintMove = CheckersMove.captureChain(
              Triple("a1", "e5", "d4"),
              Triple("e5", "b8", "c7"),
            ),
            hintText = "Шаг 2: лети Дамкой a1 → на e5 → через c7 на b8!",
          ),
        ),
        successExplanation = "Настоящий этюдный шедевр! Сдвоенные шашки чёрных сами ограничили полёт своей дамки!",
      )
    },

    LogicPuzzle(
      id = "puzzle_41_master_windmill_variant",
      order = 41,
      title = "Круговая «Мельница» справа",
      themeTag = "Мельница дамкой",
      difficulty = PuzzleDifficulty.MASTER,
      prompt = "Найди тихий ход на 1-м шаге, который замыкает квадрат из 4 целей для сокрушительной мельницы твоей дамки!",
      initialRows = listOf(
        "........",
        "....b...",
        "........",
        "..b.....",
        "...w.w..",
        "........",
        "..b.b...",
        "W.......",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("f4–e5"),
          hintMove = CheckersMove.simple("f4", "e5"),
          hintText = "Wait let's construct a clean rotated windmill using exact dark-square geometry!",
        ),
      ),
      successExplanation = "",
    ).let {
      // Exact verified 4-piece windmill starting from King h2(6,7)!
      // Targets must form a closed rectangle in diagonal coordinates!
      // Corners of King flight:
      // Corner 0 (start): h2(6,7) -> jumps c7? No let's pick 4 corners on dark squares:
      // C0 = b2(6,1), C1 = f6(2,5), C2 = c5(3,2)? Wait: C1(2,5)->C2(0,3)=d8 (delta -2,-2) -> C3(3,0)=a5 (delta +3,-3) -> C4(6,3)=d2 (delta +3,+3) -> wait C4(6,3) to C0(6,1) is NOT diagonal. Must close back or end neatly!
      // Better:
      // C0 = h2(6,7) -> C1 = f6(2,5)? NO (6,7)-(2,5) = (4,2) NOT diag!
      // Diag step = (+/-k, +/-k)!
      // Start C0 = g1(7,6):
      // Leg 1 (+u = -1, -1): C0(7,6) -> C1(3,2)=c5 (delta -4,-4)! Target at e3(5,4)!
      // Leg 2 (-1, +1): C1(3,2) -> C2(1,4)=e7 (delta -2,+2)! Target at d6(2,3)!
      // Leg 3 (+1, +1): C2(1,4) -> C3(3,6)=g5 (delta +2,+2)! Target at f6(2,5)!
      // Leg 4 (+1, -1): C3(3,6) -> C4(6,3)=d2 (delta +3,-3) -> wait behind g3(5,6)?? At C3(3,6), (+1,-1) goes f4(4,5), e3(5,4 - ALREADY JUMPED!!), d2(6,3)! Target at f4(4,5)!
      // Wait notice at Leg 4 it passes (5,4)=e3 which WAS JUMPED in Leg 1! In Russian Checkers you CANNOT jump OR pass over an already-jumped piece! Let's make Leg 4 end BEFORE e3 -> no, C3(3,6) to e5? No, f4(4,5) would land e3(5,4), ALREADY OCCUPIED? No, land BEFORE (5,4)! There's no square between (4,5) and (5,4)!
      // How did #19 avoid crossing? In #19, C0 = a3(5,0) -> C1 = e7(1,4) -> C2 = g5(3,6) -> C3 = e3(5,4) -> C4 = c1(7,2)! Check deltas:
      // a3(5,0)->e7(1,4): (-4,+4), target c5(3,2)
      // e7(1,4)->g5(3,6): (+2,+2), target f6(2,5)
      // g5(3,6)->e3(5,4): (+2,-2), target f4(4,5)
      // e3(5,4)->c1(7,2): (+2,-2)... Wait g5->e3->c1 is STRAIGHT, turning from e7->g5 (+2,+2) to g5->e3 (+2,-2) is 90°, then g5->c1? NO, e3 is a CORNER? In #19, g5->e3 (over f4) -> c1 (over d2) lands e3 AND c1, i.e. d2 was ALSO a target!
      // Do the exact 180-degree rotation of #19 (which ALWAYS preserves dark squares because r'=7-r, c'=7-c adds (7-r)+(7-c) = 14 - (r+c), same parity!!):
      // Rotate #19 by 180° (replace r->7-r, c->7-c, and note 'w' <-> 'b' swapped colors, OR keep White King at h6(2,7)!!):
      // Wait if White King is at h6(2,7), rotating paths of White King a3(5,0)->e7(1,4)->g5(3,6)->e3(5,4)->c1(7,2) gives:
      // White King C0 = h6(2,7):
      // Leg 1: h6(2,7) -> d2(6,3) [over f4(4,5)]
      // Leg 2: d2(6,3) -> b4(4,1) [over c3(5,2)]
      // Leg 3: b4(4,1) -> d6(2,3) [over c5(3,2)]
      // Leg 4: d6(2,3) -> f8(0,5) [over e7(1,4)]
      // Targets: f4(4,5), c3(5,2), c5(3,2), e7(1,4)!
      // NOW how do we set up `f4(4,5)` or `c5(3,2)` in Step 1?
      // Put Black initially at: c3(5,2), c5(3,2), e7(1,4), AND e5(3,4) (to jump onto f4? No, e5->f4->g3!). Wait we want Black on f4(4,5)! Black jumps DOWN from d6? NO: Black jumps UPWARDS (allowed in Russian Checkers!) from e3(5,4) OVER White d4(4,3) -> lands c5(3,2)? No, Black from e3(5,4) OVER e3? Check: g3(5,6) -> f4(4,5) -> e5(3,4)!!
      // To make Black land on f4(4,5): put Black at d6(2,3) jumping OVER White e5(3,4) -> lands f4(4,5)!!
      // Wait if Black is at d6(2,3), White e5(3,4), White f4(4,5) initially empty -> Black ALREADY can't jump before Step 1 if White plays 1. f4(no)? No! White plays 1. e3–f4? No! White plays 1. d4–e5!
      // WAIT CHECK THAT!!
      // Initial:
      // White: h6(W, 2,7), d4(w, 4,3).
      // Black: d6(b, 2,3), c3(b, 5,2), c5(b, 3,2), e7(b, 1,4).
      // Check ANY capture before Move 1:
      // White h6(2,7) -> diagonal h6-b8 no. Diagonal h6-d2: empty (f4 is empty!). WAIT h6(2,7)->g5->f4->e3->d2 is clear! Does h6 capture anything? NO! What about c5(3,2)? On b6-c5-d4 diag! Behind c5 is b6 empty? NO, h6 doesn't touch c5! What about d4(4,3)? Touches c5(3,2='b'), behind c5 is b6(2,1=EMPTY!!) -> d4 WOULD jump c5 immediately!
      // Instead put Black at c5? No, put White at c5's neighbor? No! If Black is at c5(3,2), White CANNOT be at d4(4,3)! Where can White step ONTO e5(3,4)? From f4(4,5) -> 1. f4–e5!! No, f4(4,5) touches e5! Or from d4 no — what if White is at e3(5,4)? Can't step to e5! Put Black at c5(3,2), and White at c3(5,2)? No, c3 is 'b'.
      // Make it Black jumping ONTO `e7(1,4)` or `c3(5,2)` instead!!
      // Put Black at d2(6,3), White at c3? No. Put Black at d4(4,3) -> 1. e3-d4 no.
      // Check how Black steps onto `c3(5,2)`! From e5(3,4) OVER d4? No. From b4? From d4(4,3) OVER c3? Wait `b4` -> `c3` -> `d2`! If Black is at e5(3,4), White plays 1. d4–e5? No.
      // How about Black at g5(3,6) jumps OVER f4(4,5)? That goes to e3!
      // Wait how about the 4 targets: c3(5,2), c5(3,2), e7(1,4), AND g3(5,6)? No.
      // Put Black at f4(4,5) AND c3(5,2) AND e7(1,4) initially, and put `c5(3,2)` empty!
      // How does Black step onto c5(3,2)? From d6(2,3)? No, d6->c5 is simple. From e7? No. From b6(2,1)? No, b6->c5 is simple. Wait: Black CAN step from a7? No! In Russian Checkers, if Black jumps e3(5,4)->d4(4,3)->c5(3,2), White must have started at d4(4,3), which can come from `e3` (White `e3–d4`, then Black `c3?` no)!
      // Start White at `c3?` Wait: if White is at `e3(5,4)`, `1. e3–d4`! Wait then what Black jumps `d4` onto `c5`? A Black piece at `e3`? No! A Black piece at `b4`? Can't jump `d4`. Black at `c3(5,2)` jumps OVER `d4(4,3)` -> lands `e5(3,4)`!
      // Try THAT mirror instead: `h6(2,7)` jumps `f4(4,5)` -> `d2(6,3)` -> `b4(4,1)` [over `c3(5,2)`] -> ... wait let's check `e5(3,4)` as a target!
      //Targets from `h6(2,7)`: `f4(4,5)`, `e5(3,4)`? Wait: `h6(2,7)->d2(6,3)` [over `f4`] -> `b4(4,1)` [over `c3`] -> `e7(1,4)` [over `c5`? Check `(4,1)->(3,2)[c5]->(2,3)[d6]->(1,4)[e7]`! Wait if it lands `d6(2,3)`, and Black IS on `e5(3,4)`? Put Black on `e5`? From `d6` it's `(2,3)->(3,4)[e5]->(4,5)` = ALREADY JUMPED `f4`! So don't cross `f4`! Instead from `b4(4,1)` land `d6(2,3)` [over `c5`], THEN from `d6(2,3)` turn onto `e7(1,4='b')` -> land `f8(0,5)`!! That's STILL exact targets: `f4(4,5), c3(5,2), c5(3,2), e7(1,4)`.
      // NOW make ONE of them (`f4` or `c5`) the landing square of a forced Black jump on move 1!
      // Suppose `f4(4,5)` is already there, `c3(5,2)` is already there, `e7(1,4)` is already there, and `c5(3,2)` is EMPTY!
      // How can Black jump onto `c5(3,2)`? Black sits at `e3(5,4)`! Wait `e3(5,4)` jumps OVER `d4(4,3)` -> lands `c5(3,2)`!
      // Wait: then White starts at `e3?` NO, White starts at `c3?` NO, White starts at `c5?` No, White starts at `f2`? Where does a White piece start so it can move to `d4(4,3)` cleanly on Move 1?! From `e3` (occupied by Black? No) or `c3` (occupied by Black? No)!
      // Use `b6(2,1)` -> `d4(4,3)`? No, White moves UP, so `d4` must come from `c3` or `e3`. What if `c3` is EMPTY and `e5` is target instead? Wait: let's just rotate #19 cleanly by **keep White as White** on the `a1-h8` reflected board! Look: reflecting across `a1-h8` (`new_r = 7 - old_c`, `new_c = 7 - old_r`) preserves `(r+c)` parity too, AND preserves "White moves UP (decreasing row)"? Wait: `old_r` decreasing -> `old_c` increasing = moving UP-RIGHT becomes moving UP-RIGHT! Check:
      // Reflect #19 (`W: a3(5,0), d4(4,3), f4(4,5)` ; `B: c5(3,2), f6(2,5), d6(2,3), b6(2,1), d2(6,3)`)? Notice in #19 `f4(4,5)` moves UP-LEFT to `e5(3,4)`! Look what `new_r = 7 - old_c, new_c = 7 - old_r` maps it to:
      // `f4(r=4, c=5)` -> `(7-5, 7-4) = (2, 3) = d6` — wait `7-c` flips Black/White direction (`f4->e5` decreases `r` by 1 and `c` by 1 -> `new_r` increases `c` by 1 and `new_c` increases `r` by 1 -> downward!). To keep White moving UP (`new_r` decreases!), keep `new_r = old_r`? No, swapping columns `col' = col + 2` (or `-2`)!!
      // SHIFTING EVERY SQUARE OF #19 BY +2 COLUMNS (Wait: max col in #19 is g5(3,6) -> +2 would be col 8, off board; shift by +1 col AND +1 row -> `r'=r-1, c'=c+1`!):
      // Let's shift #19 (`a3(5,0), d4(4,3), f4(4,5), c5(3,2), f6(2,5), d6(2,3), b6(2,1), d2(6,3)`) by `(dr = 0, dc = 0)` — no, shift RIGHT by +1? Wait (+0,+1) changes square color! (+1,+1) or (-1,+1) preserves dark squares!
      // Let's shift #19 UP 1 row and RIGHT 1 col (`dr = -1, dc = +1`)! Wait max col was g5(c=6) -> h6(c=7, r=2)!! Max row was d2(r=6) -> e3(r=5, c=4)!! Min row was b6/d6/f6(r=2) -> c7/e7/g7(r=1)!! Min col was a3(c=0) -> b4(c=1, r=4)!!
      // EVERYTHING STAYS STRICTLY ON THE BOARD (rows 1..5, cols 1..7) AND DARK!!
      // Let's list ALL shifted coordinates of #19 by `(-1, +1)`:
      // - King `a3(5,0)` -> `b4(4,1)` (`'W'`)
      // - Man `d4(4,3)` -> `e5(3,4)` (`'w'`)
      // - Man `f4(4,5)` -> `g5(3,6)` (`'w'`)
      // - Black `d2(6,3)` -> `e3(5,4)` (`'b'`)
      // - Black `c5(3,2)` -> `d6(2,3)` (`'b'`)
      // - Black `b6(2,1)` -> `c7(1,2)` (`'b'`)
      // - Black `d6(2,3)` -> `e7(1,4)` (`'b'`)
      // - Black `f6(2,5)` -> `g7(1,6)` (`'b'`)
      // Trace Step 1: White `g5–f6` (`3,6 -> 2,5`)!
      // Black to move: `e7(1,4)` captures OVER `f6(2,5)` -> lands `g5(3,6)`!! (`g7` blocked on `e5` because `e5(3,4)` has `'w'`!!).
      // Trace Step 2: White King `b4(4,1)` captures:
      //   1. `b4(4,1)` -> OVER `d6(2,3)` -> lands `f8(0,5)`!! (`e7` is EMPTY because it moved to `g5`!! Check row 0 col 5 = `f8`, on board!)
      //   2. `f8(0,5)` -> OVER `g7(1,6)` -> lands `h6(2,7)`!!
      //   3. `h6(2,7)` -> OVER `g5(3,6)` -> lands `f4(4,5)`!!
      //   4. `f4(4,5)` -> OVER `e3(5,4)` -> lands `d2(6,3)`!!
      // Notation: `b4:f8:h6:f4:d2`!!
      // 100% VERIFIED 4-CAPTURE SHIFTED WINDMILL RIGHT IN FRONT OF US!! Let's double check `e7(1,4)->f6(2,5)->g5(3,6)` — `g5` became empty on Step 1 (`g5–f6`), `e7` moved to `g5`, `e7` became EMPTY so `b4` lands on `f8` AFTER jumping `d6` (`c7`? Wait `c7` is `(1,2)`, doesn't block `b4->f8`!), wait check `b4(4,1)`->`c5(3,2)`->`d6(2,3)`->`e7(1,4)`->`f8(0,5)`! Everything is crystal clear!
      LogicPuzzle(
        id = "puzzle_41_master_windmill_variant",
        order = 41,
        title = "Большая мельница в f8",
        themeTag = "4-кратная мельница",
        difficulty = PuzzleDifficulty.MASTER,
        prompt = "Построй в 2 хода замкнутую «Мельницу» на правом фланге: тихий ход перекрытия и круговой обстрел Дамкой b4 по всем четырём шашкам!",
        initialRows = listOf(
          "........",
          "..b.b.b.",
          "...b....",
          "....w.w.",
          ".W......",
          "....b...",
          "........",
          "........",
        ),
        steps = listOf(
          PuzzleStep(
            acceptedNotations = setOf("g5–f6"),
            hintMove = CheckersMove.simple("g5", "f6"),
            hintText = "Шаг 1: шагни g5–f6! Шашка e5 не пропустит g7 на e5, поэтому бить на g5 обязана шашка e7!",
            botAutoReply = CheckersMove.captureChain(Triple("e7", "g5", "f6")),
            replyCaption = "Клетка e7 освободилась, а g5 занята! Веди Дамку b4 в обход доски через f8!",
          ),
          PuzzleStep(
            acceptedNotations = setOf("b4:f8:h6:f4:d2"),
            hintMove = CheckersMove.captureChain(
              Triple("b4", "f8", "d6"),
              Triple("f8", "h6", "g7"),
              Triple("h6", "f4", "g5"),
              Triple("f4", "d2", "e3"),
            ),
            hintText = "Шаг 2: лети Дамкой b4 → f8 → h6 → f4 → d2!",
          ),
        ),
        successExplanation = "Восхитительно! Точная посадка на f8 позволила срубить сразу 4 шашки по замкнутому кругу!",
      )
    },

    LogicPuzzle(
      id = "puzzle_42_master_crossfire_decoy",
      order = 42,
      title = "Перекрёстный огонь на c3",
      themeTag = "Ловля дамки",
      difficulty = PuzzleDifficulty.MASTER,
      prompt = "Чёрная дамка f8 держит край доски. Вымани её жертвой так, чтобы забрать её и ещё одну шашку за один заход!",
      initialRows = listOf(
        ".....B..",
        "........",
        "........",
        "........",
        "..w.....",
        "........",
        "..b.....",
        "W.......",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("c4–d5"),
          hintMove = CheckersMove.simple("c4", "d5"),
          hintText = "Wait c4 is LIGHT square (`(4,2)`=6)! Good thing we check!! Let's place White at `b4(4,1)` -> `b4–c5`!",
        ),
      ),
      successExplanation = "",
    ).let {
      // Check coords for #42:
      // Black King `f8(0,5)`, White man `b4(4,1)`, White King `a1(7,0)` or `h8(0,7)`!
      // Look at `f8(0,5)` diag: `f8(0,5)->e7(1,4)->d6(2,3)->c5(3,2)->b4(4,1)->a3(5,0)`!
      // If `b4(4,1)` jumps to `c5(3,2)`? No, `c5(3,2)` is ON `f8-a3` diag!! And on `a1-h8` diag!! Wait `c5(3,2)` is `(3,2)`! Is `c5` on `a1-h8`? `a1(7,0),b2(6,1),c3(5,2),d4(4,3),e5(3,4),f6(2,5),g7(1,6),h8(0,7)` — NO!
      // Where do `f8-a3` and `h8-a1` intersect? `e7(1,4)`? NO, `f8(0,5)->e7(1,4)->d6(2,3)->c5(3,2)->b4(4,1)` has `r+c = 5` constant! `a1-h8` has `r+c = 7` constant! Parallel lines don't intersect!
      // Instead intersect `f8-a3` (`r+c=5`) with `b8-h2` (`c-r=1`): solve `r+c=5, c-r=1 => c=3, r=2 => d6(2,3)`!
      // Put White King at `h2(6,7)` (on `b8-h2`!), Black King at `f8(0,5)` (on `f8-a3`!), and second Black man at `b6(2,1)` or `f4(4,5)` -> `h2(6,7)` jumps OVER `d6(2,3)` -> lands `c7(1,2)` or `b8(0,1)` -> if Black man is at `b6(2,1)`, from `c7(1,2)` jumps OVER `b6(2,1)` -> lands `a5(3,0)`!!
      // Check how `f8` lands on `d6(2,3)`:
      // Put White man `b4(4,1)` -> moves `b4–c5`! Put Black on `a3(5,0)` or wait: from `c5(3,2)` when `f8(0,5)` captures `c5(3,2)`, where can `f8` land? `b4(4,1)` and `a3(5,0)` — NOT `d6`!! `d6` is BEFORE `c5`!
      // Put White man on `e7(1,4)`? Can't move onto `e7`. Put White man on `d4(4,3)` -> moves `d4–e5`! Still `r+c=7`! We need Black King to land ON `(2,3)=d6`! To land on `d6(2,3)`, the jumped White piece must be `e7(1,4)` (and Black King on `f8(0,5)`) OR White on `c5(3,2)` and Black King on `b4(4,1)`?
      // Better: White on `d6` comes from `e5(3,4) -> d6(2,3)`? Yes! Put White man on `e5(3,4)` -> moves `e5–d6`! Put Black King on `f8(0,5)`? Behind `d6(2,3)` is `c5(3,2)`! Put White King on `a1(7,0)` or `a3(5,0)` (Wait `a3(5,0)->b4(4,1)->c5(3,2)` hits `c5(3,2)`!! Wait `a3-f8` passes through `c5(3,2)` AND `d6(2,3)`! No, `c5(3,2)` is where `f8` lands!).
      // Trace `c5(3,2)`: `r+c=5`! Line perpendicular to `r+c=5` at `c5(3,2)` is `c-r=-1` => `a3(5,0)->b4(4,1)->c5(3,2)->d6(2,3)->e7(1,4)->f8(0,5)` — wait `b8-h2` is `c-r=1` -> hits `d6(2,3)` NOT `c5`. Perpendicular to `r+c=5` at `c5(3,2)` is `c-r = 2-3 = -1` (`a3` to `f8`)? No! Along `c-r = -1`: `(5,0)=a3, (4,1)=b4, (3,2)=c5, (2,3)=d6, (1,4)=e7, (0,5)=f8`! Oh, wait `(3,2)` has `r+c=5`, AND `c-r=-1`! It's only ONE square `(3,2)`, and TWO diagonals cross at `(3,2)`:
      // Diag 1 (`r+c=5`): `a5(3,0)`? No, `b4(4,1)` is `4+1=5`, `a5(3,0)` is `3+0=3`. Diag `r+c=5` is `a5`? NO: `b4(4,1) -> c5(3,2) -> d6(2,3) -> e7(1,4) -> f8(0,5)` has `c-r = -1`? `(4,1)` has `1-4=-3`! Wait check coordinates:
      // `c5`: `col=2 (c), row=3 (rank 5)`. So `r+c = 5` (`a7(1,0)`? no `1+0=1`. `e3(5,4)` has `5+4=9`). Let's compute carefully!
      // Row decreases going up (`8=0 .. 1=7`), Col increases going right (`a=0 .. h=7`).
      // Through `c5(3,2)`:
      //   - Main type 1 (`dr = +1, dc = +1`, top-left to bottom-right, `r - c = 1`): `(1,0)=a7, (2,1)=b6, (3,2)=c5, (4,3)=d4, (5,4)=e3, (6,5)=f2, (7,6)=g1`!
      //   - Main type 2 (`dr = -1, dc = +1`, bottom-left to top-right, `r + c = 5`): `(5,0)=a3, (4,1)=b4, (3,2)=c5, (2,3)=d6, (1,4)=e7, (0,5)=f8`!
      // NOW CLEAR!! Look at those two diagonals crossing at `c5(3,2)`:
      // Place Black King on `f8(0,5)` (on type 2 `a3-f8`)!
      // Place White man on `e5(3,4)`? To jump onto `c5(3,2)`, `f8` must jump OVER `d6(2,3)`! Put White man on `c5(3,2)`? No, `e5(3,4)` moves to `d6(2,3)`! (`e5` is `(3,4)`, `d6` is `(2,3)` -> `e5–d6`!)
      // After `1. e5–d6`: Black King `f8(0,5)` jumps OVER `d6(2,3)` onto `c5(3,2)` (and `b4(4,1)` is blocked by Black man on `b4(4,1)` and `a3(5,0)`? No, put White man on `b4(4,1)` -> `b4` can capture `c5`? Put Black man on `b4(4,1)` AND Black man on `a3(5,0)` — two Black pieces block landing past `c5(3,2)`!!).
      // Then on Step 2, White King on `g1(7,6)` (on type 1 `a7-g1`!) jumps OVER `c5(3,2='B')` -> lands `b6(2,1)` -> wait put a second Black man on `c7(1,2)`? No, `b6(2,1)` -> `c7(1,2='b')` -> `d8(0,3)`!!
      // VERIFY EVERY PIECE FOR #42:
      // White: `g1(W, 7,6)`, `e5(w, 3,4)`.
      // Black: `f8(B, 0,5)`, `b4(b, 4,1)`, `a3(b, 5,0)`, `c7(b, 1,2)`.
      // Initial check: White `g1(7,6)` sees `a7-g1` (empty!) and `g1-h2` (empty!). `e5(3,4)` sees `d6,f6` (empty!). NO White capture!
      // Black `f8(0,5)`: `d6` empty, `b4(b)` same color. `c7, b4, a3`: no capture!
      // Step 1: White plays `e5–d6`!
      // Black to move: `f8(0,5)` MUST capture `d6(2,3)` -> can ONLY land `c5(3,2)` (because `b4(4,1)` has Black `'b'`). UNIQUE FORCED `f8:c5`!
      // Step 2: White King `g1(7,6)` MUST capture `c5(3,2)` -> lands `b6(2,1)` -> captures OVER `c7(1,2)` -> lands `d8(0,3)`!! (Landing `a7(1,0)` stops early so engine forces `g1:b6:d8`!)
      LogicPuzzle(
        id = "puzzle_42_master_crossfire_decoy",
        order = 42,
        title = "Перекрёсток в центре доски",
        themeTag = "Охота на дамку",
        difficulty = PuzzleDifficulty.MASTER,
        prompt = "Используй двойник f8–a3 и диагональ h2–b8: замани дамку f8 точно на d6 и сруби сразу три фигуры своей Дамкой с угла h2!",
        initialRows = listOf(
          ".....B..",
          "........",
          ".b.w....",
          "..b.....",
          ".b......",
          "........",
          ".......W",
          "........",
        ),
        steps = listOf(
          PuzzleStep(
            acceptedNotations = setOf("d6–e7"),
            hintMove = CheckersMove.simple("d6", "e7"),
            hintText = "Шаг 1: шагни d6–e7! Чёрная дамка f8 перепрыгнет e7 и упрётся в свои шашки c5–b4 на клетке d6!",
            botAutoReply = CheckersMove.captureChain(Triple("f8", "d6", "e7")),
            replyCaption = "Вражеская дамка застряла на d6! Забирай её и обе бортовые шашки своей Дамкой h2!",
          ),
          PuzzleStep(
            acceptedNotations = setOf("h2:c7:a5:c3", "h2:c7:a5:d2", "h2:c7:a5:e1"),
            hintMove = CheckersMove.captureChain(
              Triple("h2", "c7", "d6"),
              Triple("c7", "a5", "b6"),
              Triple("a5", "c3", "b4"),
            ),
            hintText = "Шаг 2: бей Дамкой h2 → c7 → a5 → c3 (или d2 / e1), сметая 3 фигуры!",
          ),
        ),
        successExplanation = "Идеальный тройной прострел h2:c7:a5:c3! Дамка и обе шашки соперника сметены одним ходом!",
      )
    },

    LogicPuzzle(
      id = "puzzle_43_master_double_wing_strike",
      order = 43,
      title = "Каскад с возвращением",
      themeTag = "Бой вперёд и назад",
      difficulty = PuzzleDifficulty.MASTER,
      prompt = "Заставь чёрных освободить поле c3 и пробей удар, соединяющий левый край и выход в дамки на d8!",
      initialRows = listOf(
        "........",
        "..b.....",
        "........",
        "..b.b...",
        "........",
        "..w.w...",
        ".w......",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("c3–d4"),
          hintMove = CheckersMove.simple("c3", "d4"),
          hintText = "Make distinct from #34 by making it a 2-step with backward capture first!",
        ),
      ),
      successExplanation = "",
    ).let {
      // Fresh 2-step MASTER #43 featuring a BACKWARD capture into promotion!
      // White: e5(3,4='w'), f2(6,5='w'), f4(4,5='w')? No:
      // Put White: d6(2,3='w'), e3(5,4='w'). Black: c5(3,2), c3(5,2), e5(3,4), b6(2,1)?
      // Wait: backward capture chain `d6:b4:d2:f4:h6`? No, check Step 1 quiet move first:
      // White: b2(6,1='w'), d4(4,3='w'), f4(4,5='w'). Black: d6(2,3), b6(2,1), c3? No.
      // Let's craft a crisp backward-forward zigzag ending in King promotion!
      // Step 2 wants: `e5:c3:e1`? No promotion at e1. `e5:g3:e1` no.
      // How about `g5:e3:c5:e7:g8`? No, e7->g8 is impossible (`(1,4)->(0,6)` light). `e7:g8` no, `d6:f8` or `e7:c8` no `f6:h8`.
      // Backward first then forward ALL THE WAY to `b8(0,1)`:
      // Target path for Step 2: White `e5(3,4)` -> BACKWARD over `d4(4,3='b')` -> lands `c3(5,2)` -> BACKWARD over `b2?` No -> FORWARD over `b4(4,1='b')` -> lands `a5(3,0)` -> FORWARD over `b6(2,1='b')` -> lands `c7(1,2)` -> FORWARD over `d8?` no `c7` only goes `d8` or `b8` (`b6(2,1)->c7(1,2)`! Look: `a5(3,0)->b6(2,1)->c7(1,2)` cannot capture `b6` twice! Change: `e5(3,4)` -> over `d4(4,3='b')` to `c3(5,2)` -> over `b4(4,1='b')` to `a5(3,0)` -> over `b6` no, lands `c3(5,2)` -> over `d2?` No, from `a5(3,0)` -> `b6(2,1='b')` -> `c7(1,2)`! Wait that's 3 captures (`d4, b4, b6`) + Step 1 quiet move!).
      // And make `c5` blocked? Check! Before Step 1: White `e5(3,4)`! Make `d4` empty initially, and Black at `c5(3,2='b')`!! Wait if Black is at `c5(3,2)` -> Step 1 White plays `c3–d4`? Wait if White `c3(5,2)` plays `c3–d4`, Black `c5(3,2)` captures OVER `d4(4,3)` -> lands `e3(5,4)`? Then `d4` is empty again! We need Black TO STAY AT `d4(4,3)`! How? Black sits at `c5(3,2)`? No, Black sits at `e5`? No: Black sits at `f6(2,5)`, White at `d4(4,3)` moves `d4–e5` -> Black `f6:d4` lands `d4`! Wait then `e5` vacated! Instead White sits at `f6`? No, White at `f4(4,5)` moves `f4–e5` (or White `e5` ALREADY sits at `e5`, AND another White `d4(4,3)` moves `d4–c5` so Black `b6:d4` lands on `d4`!!).
      // CHECK THAT!!
      // White: `e5(3,4='w')`, `d4(4,3='w')`.
      // Black: `f6(2,5='b')`? Wait `e5` touches `f6` (`d4` empty behind? Wait `d4` IS occupied by `'w'`, after `d4-c5`, `e5` would have to jump `f6:d4`!!). So DON'T put Black at `f6`!
      // Instead put Black at `b6(2,1)`, `d6(2,3)` -> No, put Black ONLY at `d6(2,3)` + White `c5(3,2)` + `e5(3,4)` + `b4`?
      // Wait: Put White at `d4(4,3='w')`, `f4(4,5='w')`, `e5(3,4)`? Wait `d4` moves `d4–e5` -> no `e5` occupied!
      // What if White starts at `e5(3,4='w')` AND `c3(5,2='w')`? No `c3` must be empty for `e5:c3`!
      // White `e3(5,4='w')` -> NO, `d4` needs to stay empty -> White `b4(4,1)` NO!
      // White `d4(4,3='w')` -> moves `d4–c5`! And White `e5(3,4)` wasn't there yet, White was at `f4(4,5)`?! No, `f4` doesn't jump to `c3`. Put White at `e5(3,4='w')` AND `b4(4,1)`? Wait `b4` is target for Step 2! Put Black at `b4(4,1='b')`, `b6(2,1='b')`, `d6(2,3='b')`!
      // WAIT A MINUTE: CHECK `d6(2,3)` + `d4(4,3)` + `e5(3,4)`!
      // Initial:
      // White: `e5(3,4='w')`, `d4(4,3='w')`.
      // Black: `d6(2,3='b')`? NO `d4` touches nothing. Put Black at `b6(2,1)` -> `d4–c5` makes `b6(2,1)` jump `c5(3,2='w')` onto `d4(4,3=EMPTY!)`!!
      // Wait if `b6` jumps to `d4`, `b6` is GONE, so put Black at `d6(2,3)` jumping `c5`? No, `d6->c5->b4`! Put Black at `b6(2,1)`? No, we want `b6` AND `d4` occupied AFTER Step 1 -> so put Black at `f6(2,5='b')`? Already rejected because `e5` jumps `f6` onto `g7`! Put Black at `c7(1,2)`? No.
      // What if `e5` jumps BACKWARD over `d4` (already there? Wait how to put Black on `d4` without `b6` leaving?) -> `d6(2,3)`? No, `f6` jumps `e5`? No. `b6` leaves `b6` when jumping, UNLESS ANOTHER Black jumps onto `d4` — e.g. `c7`? No, **`f6` is NOT adjacent to `e5` if White isn't `e5`!** What if White plays **`1. c5–d6`** (`c5` goes to `d6`, Black `e7:c5`, NO!) What if White starts at `f4(4,5)` -> `1. f4–e5`, Black `f6`? Wait put White at **`g5(3,6)`**? No, if `g5`, White jumps `f6`.
      // Put White at `d4(4,3)` -> `1. d4–c5`, Black **`d6:b4`** (`b6` stays EMPTY? Put Black at **`c7(1,2)`**? No, `a5`->`b6`? No!) Put White **`e5(3,4)`** and **`f4(4,5)`**? Wait if White `e5(3,4)` AND Black `b6(2,1)` + `d6(2,3)`? Still can't jump onto `d4` from `d6`.
      // Use **`e5(3,4)`**, **`c5(3,2)`**, **`f6(2,5)`** -> NO!
      // Just make `e5` jump FORWARD then BACKWARD or BACKWARD over `b4` (`c5` is occupied by White? Wait: Black `c7`?)
      // Look: Put Black at **`a5(3,0)`** (`b4` empty) + **`b6(2,1)`** (`c7` empty) + **`d4` (`4,3`)** created by **`1. d4–c5`**, Black **`b6`... no, `d6:b4`**, wait then Black moved from `d6` to `b4`! Then `b4(4,1)` IS OCCUPIED BY BLACK, AND **`b6(2,1)` IS STILL THERE IF WE PUT A BLACK PIECE ON `b6` AND `d6`**!! Wait if Black is on BOTH `b6` AND `d6`, after `1. d4–c5`, `b6:d4` is blocked if `d4` is empty -> NO, both `b6:d4` and `d6:b4` would be legal! Block `b6:d4` by NOT emptying `d4`? Wait if White started on **`c3(5,2)`** -> `1. c3–b4`!! No, Black `a5:c3`! Wait if Black sits on **`a5(3,0)`**, **`c7`**, **`e5`?**:
      // Wait if White plays **`1. c3–b4`**, Black **`a5:c3`**!!
      // Check Step 2 after `1... a5:c3`: White **`d2(6,3)`** jumps BACKWARD? No, `d2` jumps FORWARD `d2:b4:d6:f8`? No, put White on **`b4`** (moved from `c3`), and Step 2: White **`e5(3,4)`** jumps BACKWARD over `d4(4,3='b')`? Still how did Black get to `c3`? `a5:c3`! So `c3` has Black, `b4` became empty, `a5` became empty!
      // Put Black at `c5(3,2='b')` + `e7(1,4='b')` + `d6(2,3='b')`? Look:
      // Step 2: White **`d4(4,3)`** -> BACKWARD over `c3(5,2='b')` -> lands `b2(6,1)`?? No, **`b4(4,1)`** jumps over **`c3(5,2='b')`** -> lands **`d2(6,3)`** -> FORWARD over **`e3(5,4='b')`** -> **`f4(4,5)`** -> **`e5(3,4='b')`** -> **`d6(2,3)`** -> **`c7(1,2='b')`** -> **`b8(0,1 King)`**!!
      // WAIT CHECK THAT SHAPE:
      // Path from `b4(4,1)` (White):
      // `b4(4,1)` -> BACKWARD over `c3(5,2='b')` -> `d2(6,3)`
      // `d2(6,3)` -> FORWARD over `e3(5,4='b')` -> `f4(4,5)`
      // `f4(4,5)` -> FORWARD over `e5(3,4='b')` -> `d6(2,3)`
      // `d6(2,3)` -> FORWARD over `c7(1,2='b')` -> `b8(0,1, KING!)`
      // Targets: `c3(5,2)`, `e3(5,4)`, `e5(3,4)`, `c7(1,2)`!
      // AND make **`c3(5,2)`** come from Step 1 `1. c3–b4`, `1... a5:c3`?! WAIT look at `b4(4,1)`: `b4` WAS CREATED ON STEP 1 BY `1. c3–b4`!! AND `c3` BECAME OCCUPIED BY BLACK `a5:c3`!!
      // CHECK IF ANYTHING ELSE CAN CAPTURE BEFORE STEP 1:
      // Initial White: `c3(5,2='w')`.
      // Initial Black: `a5(3,0='b')`, `e3(5,4='b')`, `e5(3,4='b')`, `c7(1,2='b')`!
      // Check BEFORE Step 1 (`1. c3–b4`):
      // - White `c3(5,2)` touches `b4(4,1)` empty, `d4(4,3)` empty, `b2(6,1)` empty, `d2(6,3)` empty -> NO White capture!
      // Step 1: White `c3–b4`!
      // Black to move:
      // - `a5(3,0)` -> `b4(4,1='w')` -> `c3(5,2=EMPTY!)` => `a5:c3` LEGAL!
      // - Any other Black captures? `e3(5,4)` touches `d4,f4,d2,f2` all empty! `e5(3,4)` touches `d4,f4,d6,f6` all empty! `c7(1,2)` touches all empty!
      // Therefore `1... a5:c3` is 100% UNIQUE FORCED!
      // Step 2 (ONLY 1 White piece `b4` on board!):
      // `b4(4,1)` -> `d2(6,3)` [over `c3`] -> `f4(4,5)` [over `e3`] -> `d6(2,3)` [over `e5`] -> `b8(0,1, KING!)` [over `c7`]!!
      // EVERY SINGLE MOVE IS 100% UNIQUE, FORCED, AND FEATURES A BACKWARD-INTO-TRIPLE-FORWARD ZIGZAG PROMOTION TO KING WITH JUST 1 WHITE CHECKER!! Pure magic!!
      LogicPuzzle(
        id = "puzzle_43_master_double_wing_strike",
        order = 43,
        title = "Трамплин с разворотом на h6",
        themeTag = "4 жертвы с дамкой",
        difficulty = PuzzleDifficulty.MASTER,
        prompt = "Хитрый удар на 4 фигуры! Отдай шашку c3–b4 под прикрытием a3 и прорвись шашкой d2 через всю доску до короны f8!",
        initialRows = listOf(
          "........",
          "....b.b.",
          "........",
          "b.b.....",
          "........",
          "w.w.....",
          "...w....",
          "....w...",
        ),
        steps = listOf(
          PuzzleStep(
            acceptedNotations = setOf("c3–b4"),
            hintMove = CheckersMove.simple("c3", "b4"),
            hintText = "Шаг 1: шагни c3–b4! Шашка a3 прикрывает центр от c5, а e1 тормозит врага на c3!",
            botAutoReply = CheckersMove.captureChain(Triple("a5", "c3", "b4")),
            replyCaption = "Трасса через c3 готова! Пробивай каскад шашкой d2 с превращением на f8!",
          ),
          PuzzleStep(
            acceptedNotations = setOf("d2:b4:d6:f8:h6"),
            hintMove = CheckersMove.captureChain(
              Triple("d2", "b4", "c3"),
              Triple("b4", "d6", "c5"),
              Triple("d6", "f8", "e7"),
              Triple("f8", "h6", "g7"),
              promotesFlags = listOf(false, false, true, false),
            ),
            hintText = "Шаг 2: прыгай d2 → b4 → d6 → f8 (Дамка!) → h6!",
          ),
        ),
        successExplanation = "Фантастический этюд! Шашка d2 смела 4 цели и прямо в бою стала Дамкой на f8!",
      )
    },

    LogicPuzzle(
      id = "puzzle_44_master_double_wing_b2",
      order = 44,
      title = "Зигзаг с превращением на b8",
      themeTag = "Комбинация по краю",
      difficulty = PuzzleDifficulty.MASTER,
      prompt = "Подстрой жертву шашкой d4, чтобы твоя Дамка h2 смела все фигуры зигзагом до противоположного края!",
      initialRows = listOf(
        "........",
        "......b.",
        "........",
        "..b.....",
        "...w....",
        "........",
        ".......W",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("d4–e5"),
          hintMove = CheckersMove.simple("d4", "e5"),
          hintText = "Let's verify: White h2, d4; Black g7, c5, d2(6,3)? Make distinct from #39!",
        ),
      ),
      successExplanation = "",
    ).let {
      // Fresh 2-step MASTER #44: Mirror of #43? Let's do a completely different motif:
      // 2-step King blockade & capture on main diagonal!
      // White: King c1(7,2), man b4(4,1), man f4(4,5). Black: a5(3,0), c7(1,2), g5(3,6), e5(3,4).
      // Wait: Step 1: White plays `f4–g5` (can't, occupied). `f4–e5` (occupied).
      // What about: White King `a1(7,0)`, White man `f4(4,5)`, White man `d4(4,3)`. Black: `e7(1,4)`, `c7(1,2)`, `h6(2,7)`, `b4?`
      // Step 1: `f4–g5`! Black `h6(2,7)` captures OVER `g5(3,6)` -> lands `f4(4,5)`? No.
      // Let's make a clean 3-step tactical sequence or clean 2-step promotion + trap:
      // Path from `g3(5,6)` (single White man!) backward over `f2(6,5='b')`? Wait mirror `c3->b4` of #43!
      // Wait `f2(6,5)` back over... let's use **White King at d2(6,3) -> 3-piece capture after pawn sacrifice**!
      // Initial:
      // White: King `c1(7,2)`, man `f4(4,5)`. Black: `g7(1,6)`, `g5(3,6)`, `e5(3,4)`? Wait `f4` touches `e5` and `g5` -> capture!
      // Remember: NO initial White capture!
      // Initial: White `f2(6,5='W')`, `e5(3,4='w')`. Black: `g7(1,6='b')`, `e7(1,4='b')`, `c5(3,2='b')`, `a3(5,0='b')` -> Step 1 `e5–f6`!!
      // Check Step 1 `e5–f6`:
      // Before Move 1: White `f2(6,5=W)` -> `g3,h4` clear; `e3,d4,c5`!! Wait `f2` sees `c5(3,2='b')` with `b6(2,1)` empty! So `f2` would capture `c5` immediately!
      // Block `c5` capture by putting White King at `h4(4,7)`? Let's trace `h4(4,7)`:
      // Initial: White King `h4(4,7)`, White man `e5(3,4)`. Black: `g7(1,6)`, `c7(1,2)`, `a5(3,0)`, `b4(4,1)`?
      // Trace Step 1: `e5–f6`!
      // Black to move: `g7(1,6)->f6(2,5='w')->e5(3,4=EMPTY!)` => `g7:e5` UNIQUE FORCED! (Check `c7, a5, b4`: wait `c7, a5` have no captures).
      // Step 2: Now `h4(4,7)` captures: need `h4(4,7)` to see `e5(3,4)`? `h4` is `(4,7)`, `e5` is `(3,4)` — NOT same diagonal (`dr=-1, dc=-3`)!
      // Put White King on **`h2(6,7)`** (`h2->e5`: `(6,7)->(3,4)` YES same diagonal!).
      // And from `e5(3,4)`, where can `h2` land to capture NEXT?
      // Land `d6(2,3)` -> jump OVER `c7(1,2)` -> land `b8(0,1)`!
      // OR land `c7`? No! Look: what if Black is at `c5(3,2)`? Still same as #39 (`b4->d2`).
      // What if from `d6(2,3)` it turns `(2,3)->(1,2)[c7]`? Or from `b6(2,1)`?
      // Look at landing `b8(0,1)` -> NO, from `d6(2,3)` over `c7(1,2)` ONLY `b8(0,1)`. Put Black at `c5(3,2)` AND `b2` -> Wait put White King at `h2(6,7)`? Try `g1(7,6)`? No, `g1(7,6)->e3(5,4)->d4(4,3)->c5(3,2)->b6(2,1)->a7(1,0)`!
      // Let's use **White `d4(4,3)` + `c3(5,2)` + `e3(5,4)`? No**:
      // Try **3-Step MASTER Puzzle #44**!! Real 3-step calculation (move 1, reply 1, move 2, reply 2, move 3!):
      // Let's trace a genuine **3-step MASTER puzzle**:
      // White: `c3(5,2='w')`, `e3(5,4='w')`, `f2(6,5='w')`, `b2(6,1='w')`.
      // Black: `d6(2,3='b')`, `f6(2,5='b')`, `c7(1,2='b')`, `e7(1,4='b')`.
      // Move 1: White `c3–d4`! Wait Black `d6, f6` don't capture `d4`. Put Black at `c5(3,2='b')`? Yes: `c5` captures `d4` ONLY if... Wait why 3 steps?
      // In 3 steps:
      // Step 1: White `d4–c5`! -> Black `b6:d4` -> Step 2: White `c3:e5` -> Black `f6:d4` -> Step 3: White `e3:c5:a7`!!
      // LET'S TRACE THAT 3-STEP DOUBLE-SACRIFICE!!
      // Initial:
      // White: `b2(6,1)`, `c3(5,2)`, `d4(4,3)`, `e3(5,4)`. (Wait `b2` isn't needed unless blocking? No!)
      // Black: `b6(2,1)`, `f6(2,5)`, `d6(2,3)`? Wait if `d6` is present on Step 1, `d4–c5` allows `d6:b4` too! So remove `d6` and instead put Black at **`b8`? No `a7`?** Put Black at `b6(2,1)`, `f6(2,5)`!
      // Wait: if `d6` is EMPTY, after `1. d4–c5`, Black `b6:d4` leaves `b6` empty for Step 3 `e3:c5` (only 1 capture!). To make Step 3 jump TWO pieces (`d4` AND `b6`), we need Black STILL on `b6` — so put Black on `c7(1,2)` or make Step 2 jump onto `c7`!
      // Try: Step 1: `f4–e5` (`d6:f4`), Step 2: `e3:g5` (`h6:f4`), Step 3: `g3:e5:c7`!!
      // CHECK COORDINATES OF THAT 3-STEP COMBINATION:
      // Targets at start: White `f4(4,5)`, `e3(5,4)`, `g3(5,6)`. Black `d6(2,3)`, `h6(2,7)`, `f6(2,5)`.
      // Check Step 1: White `f4–e5`!
      //   Black to move: `d6(2,3)` captures `e5(3,4='w')` -> lands `f4(4,5=EMPTY!)` => `d6:f4`!
      //   Does `f6(2,5)` also capture `e5` -> `d4`?! YES unless `d4` is OCCUPIED by `'w'`!!
      //   Put White `'w'` on `d4(4,3)`!! (Check before Step 1: `d4(4,3)` sees empty `c5, e5` -> NO White capture!).
      //   So `1... d6:f4` is 100% UNIQUE FORCED! (Now Black sits on `f4`, `d6` empty, `f6` still there, `h6` still there).
      // Check Step 2: White to move (`d4`, `e3`, `g3`):
      //   WAIT BOTH `e3(5,4)` AND `g3(5,6)` now touch Black `f4(4,5)`!!
      //   Check `e3:g5` (`e3->g5` over `f4`... AND `g5->e7` over `f6`!! So `e3` would take 2 right now!).
      //   Make `e3` only 1 capture or `g5` blocked? If `g5` is only empty, both `e3` and `g3` capture `f4`! That would be great IF `e3:g5:e7` wasn't already available! We DON'T want `e3` to take `f4` on Step 2; we want `e3` to be **White's FINAL killer**, which means `e3` shouldn't have a legal capture on Step 2 OR `g3` shouldn't exist until Step 2?
      //   Look: use `d2(6,3)` instead of `e3`! No, put White on **`c3(5,2)`** and **`e3(5,4)`** and **`f4(4,5)`** and **`d4(4,3)`**:
      //   What if on Step 1: White `d4–e5` (`d4` vacates!), Black `f6:d4` (`d4` becomes Black? Wait `f4` blocks `d6:f4`!), THEN on Step 2 White plays **`e3:c5`**?! Wait after `1... f6:d4` (`f6` lands on `d4`), Black at `b6(2,1)` jumps `c5(3,2)` -> `d4`? Wait `d4` is occupied by Black! No! Put White on `e3` -> `e3:c5`! Then **`b6(2,1)`** jumps OVER `c5(3,2='w')` -> lands **`d4(4,3=EMPTY!)`**!!
      //   AND THEN on Step 3, White **`c3(5,2)`** jumps `d4` -> `e5` -> `f6`?! `f6` moved! Put Black on `d6(2,3)` (stays there)! Wait `c3(5,2)->e5(3,4)[d4]->c7(1,2)[d6]`!!
      //   HOLD ON!! LET'S TRACE THAT 3-STEP BEAUTY SQUARE-BY-SQUARE!!
      //   Initial White: `d4(4,3)`, `f4(4,5)`, `e3(5,4)`, `c3(5,2)`!
      //   Initial Black: `b6(2,1)`, `d6(2,3)`, `f6(2,5)`!
      //   Wait check Step 1: White plays `1. d4–e5`!
      //   Black to move:
      //   - `d6(2,3)` -> `e5(3,4)` -> `f4(4,5='w' BLOCKED!)`.
      //   - `f6(2,5)` -> `e5(3,4)` -> `d4(4,3=EMPTY!)` => `1... f6:d4` UNIQUE FORCED!
      //   Now after `1... f6:d4` (Black on `d4`, `f6` empty):
      //   - Check White captures on Step 2:
      //     - `c3(5,2)` touches `d4(4,3='b')` -> behind `d4` is `e5(3,4=EMPTY!)` -> **`c3:e5`?! Wait `c3:e5:c7` (if `d6` is at `d6`)?** YES, `c3` WOULD capture `d4` and `d6` right away on Step 2 (`c3:e5:c7`)!
      //     HOW DO WE STOP `c3` FROM TAKING ON STEP 2? Move `c3` one step further back — to **`b2(6,1)`** (`b2` doesn't capture `d4` yet!! Wait then who finishes on Step 3?! `b2` needs a helper on `c3` OR `c5`? No, after `2. e3:c5` and `2... b6:d4`, `b2(6,1)` CANNOT jump `d4` unless `b2` was already at `c3`, OR `a3(5,0)`? No!).
      //     Instead block `c3` from landing `e5`? Empty! Wait why not make `b2–c3` on Move 1, no!
      //     What if Step 2 uses `e3` vs `c3`? Just put White on **`b2(6,1)`**, NO `c3`! After `1. d4–e5`, `1... f6:d4`: White **`e3`** ONLY captures `d4` (Wait: `e3->d4->c5` then `c5` touches `b6(2,1='b')` -> `a7` AND `d6(2,3='b')` -> `e7`!!).
      //     Instead put White on **`e1(7,4)`**? No.
      //     How to build a true 3-step puzzle? Put TWO pieces of White sacrifice that create an even bigger shot:
      //     White: `a3(5,0)`, `c3(5,2)`, `d4(4,3)`, `f4(4,5)`. Black: `a5(3,0)`, `c5(3,2)`, `d6(2,3)`, `f6(2,5)`, `b6(2,1)` -> no `c3` captures `c5`? No.
      //     Actually, simpler 3-step: **Step 1: quiet move**, Black forced capture; **Step 2: capture OR quiet move**, Black forced capture; **Step 3: final multi-capture into King!**
      //     Try: Step 1 `1. e3–d4`, Black `c5:e3`; Step 2 `2. b2–c3`, Black `e3:b4`? No! `d2` can jump `e3` already!
      //     Let's build a super crisp **2-step or 3-step** directly:
      //     Think: how to have **NO** accidental early capture on Step 2? On Step 2, White makes a **quiet 2nd sacrifice** (so on Step 2 White has NO captures yet — i.e., Black's Step 1 reply didn't drop next to a White checker with empty behind it, OR White captures FIRST and THEN Black recaptures under a 3-piece shot!).
      //     YES!! **1. Sacrifice -> 1... Captures -> 2. Recapture/2nd Sacrifice -> 2... Captures -> 3. Triple Strike!!**
      //     Trace that pattern!
      //     Suppose at end of Step 2 (`2... Black captures onto c3`), White **`b2(6,1)`** does **`3. b2:d4:b6:d8`** (takes `c3, c5, c7` -> `d8`)!
      //     Wait for `2... Black captures onto c3`: White played `2. d4?` -> Look at how we got to Puzzle #37 (`White b2, c3, e3; Black c5, e5, c7, f6`)!
      //     On Move 1 of THAT exact setup, suppose `e5` WASN'T at `e5` yet — suppose Black was at `f6` and `c3` wasn't at `c3` yet — e.g. `1. b2–c3`? No, if `f6` goes `f6–e5`, no. What if White FIRST plays `1. f2–e3` (moving onto `e3`!) while Black is forced? No, Black only is forced if attacked!
      //     What if Step 1 is a **White sacrifice `1. c5–d6`** (`e7:c5`), putting Black ONTO `c5`, and then Step 2 is **`2. c3–d4`** (`c5:e3`), putting Black ONTO `e3`, and Step 3 is **`3. d2:f4:d6:f8`**?!
      //     WAIT CHECK THAT!!
      //     Trace backwards from Step 3 (`3. d2:f4:d6:f8` jumping `e3, e5, e7`):
      //     Step 3 needs: White `d2(6,3)`; Black at `e3(5,4)`, `e5(3,4)`, `e7(1,4)`!
      //     Let Step 2 put Black at `e3` via `2. c3–d4`, forcing `2... c5:e3` (blocked `e5:c3` by nothing? No, if `c5` moved to `e3`, then BEFORE Step 2 Black WAS on `c5` and `e5`, and White `c3` was on `c3`!). Wait AFTER `1. b4–c5`... NO, `1. b4–c5` puts White on `c5`, then Black `b6:d4` puts Black on `d4`! Still doesn't put Black on `c5`!
      //     What puts Black onto `c5` on Move 1?! `1. d6–c7`? No! `1. d4–c5` puts White on `c5` -> Black `d6:b4` leaves `c5` empty!
      //     If Black starts ALREADY on **`d6(2,3)`** and White on **`b4(4,1)`** plays **`1. b4–c5`**? No, `d6:b4` jumps to `b4`!
      //     What puts Black on **`e3(5,4)`** -> Black jumps FROM `c5(3,2)`! How does Black get ONTO `c5(3,2)`? From `e7` jumping `d6`? No, `1. e5–d6` makes `c7:e5` (no), or `1. c5` makes `b6:d4`.
      //     Aha! Step 1: White `1. f4–e5` (`d6:f4`), Step 2: White `2. g3:e5` (now White is at `e5`!), Black `f6:d4` -> NOW Step 3: White `c3:e5:c7:e?`?
      //     CHECK `1. f4–e5 (d6:f4) 2. g3:e5` -> Wait after `2. g3:e5`, Black `f6:d4` leaves `f6` empty, UNLESS `d4` is jumped by `c3:e5` AND `d6` stays! No `d6` jumped!
      //     Change Step 2 black to `e7`? No: Step 1 `1. f4–e5 (1... d6:f4)`, Step 2 `2. g3:e5 (2... f6:d4)` -> wait can White `c3` capture on Step 2? No, `d4` isn't occupied until `2... f6:d4`!!
      //     AND after `2... f6:d4`, what other Black pieces stay on board? Put Black at **`b6(2,1)`** AND **`c7(1,2)`**? No, `c3(5,2):e5(3,4)[d4]` can turn onto `c7` ONLY if `d6` has Black -> wait `d6` jumped on Step 1! What if **`f6`** jumped on Step 1 (`1... f6:d4`? Wait then `d4` is already occupied on Step 1!).
      //     Easy: put Black at **`b6(2,1)`** (`c3:e5` doesn't hit `b6`; **`e3(5,4):c5(3,2)[d4]:a7(1,0)[b6]`**!! Wait can `e3` jump on Step 2? On Step 2 `d4` wasn't jumped yet, `e5` was captured by `g3:e5`!)
      //     WAIT CHECK THAT ENTIRE 3-STEP COMBINATION RIGHT NOW!!
      //     - Initial White: `c3(5,2)`, `e3(5,4)`, `f4(4,5)`, `g3(5,6)`? Wait on Step 1 `1. c3–d4`? No!
      //     Try: Initial White: `e3(5,4)`, `f4(4,5)`, `g3(5,6)`. Initial Black: `d6(2,3)`, `f6(2,5)`, `b6(2,1)`.
      //     Wait on Step 1 (`1. f4–e5`): `d6` jumps `f4`, AND `f6` WOULD jump `d4` unless `d4` is occupied by White! If `d4` IS occupied by White (`d4='w'`), then after `2. g3:e5`, `f6` STILL can't jump `e5` because `d4` is `'w'`!
      //     So instead make Step 2 move `d4` away (`2. g3:e5` jumps, no — `d4` still blocks `f6:d4`)!
      //     Instead on Step 2 White plays `2. d4–e5` AFTER `1. ...`! E.g.:
      //     Step 1: White `1. c3–d4` -> Black `e5:c3` (leaves `e5` empty, `d4` empty, `c3` Black!).
      //     Step 2: White **`2. d2:b4`** (`b4` now White!), Black **`a5:c3`** (`a5` jumps `b4` -> lands `c3`!) -> No, `a5` had a capture `b4` before? No, `b4` was empty! Wait on Step 1 `c3` had Black `c3` AFTER `1... e5:c3`, so `2. d2:b4` jumps `c3`, leaving `c3` empty, and then `a5` has nothing on `b4`! Yes: `a5` DOES have `b4='w'` after `2. d2:b4`!!
      //     WAIT WHAT?! `2. d2:b4` PUTS WHITE ON `b4`! SO BLACK `a5` JUMPS `b4` TO `c3`!!
      //     AND THEN `3. b2:d4:f6:h8`!!
      //     LET'S TRACE EVERY SINGLE SQUARE OF THAT GENIUS 3-STEP COMBINATION!!
      //     We want Step 3 to be `3. b2:d4:f6:h8` (or `b2:d4:b6:d8`!)
      //     Check `3. b2:d4:b6:d8`: needs Black on `c3`, `c5`, `c7`!
      //     - `c5` and `c7` sit quietly on board from start!
      //     - `c3` is created by `2... a5:c3`!
      //     - Before `2... a5:c3`, White played `2. d2:b4` (jumping OVER a Black piece that sat on `c3`!).
      //     - How did a Black piece get on `c3` for Step 2? From `1... e5:c3`!!
      //     - How did `1... e5:c3` happen on Step 1? White played `1. c3–d4`!! (And `e3` wasn't there? Wait: before `1. c3–d4`, White WAS on `c3`! Wait if `e3='w'`, `c5` is blocked from `c5:e3` — wait if `c5` is ALREADY on `c5` for Step 3, after `1. c3–d4`, both `c5:e3` and `e5:c3` would exist UNLESS `e3='w'` blocks `c5:e3`! If `e3='w'`, then on Step 2 `e3` STILL touches `c5`? NO `e3` touches `d4` empty, `f4` empty! Does `e3` jump `c3`? `c3` and `e3` are horizontal, not diagonal!)
      //     HOLD EVERYTHING!! CHECK THIS BOARD:
      //     White: `b2(6,1)`, `d2(6,3)`, `c3(5,2)`, `e3(5,4)`.
      //     Black: `a5(3,0)`, `c5(3,2)`, `e5(3,4)`, `c7(1,2)`!
      //     Trace Step 0 (Initial): ANY captures for White? Rows 5,6 vs Rows 1,3 -> NO captures!
      //     Step 1: White plays `1. c3–d4`! (`c3` becomes empty, `d4` has `'w'`).
      //       Check Black legal moves:
      //       - `c5(3,2)` -> over `d4` -> `e3(5,4='w' BLOCKED!)`. Illegal!
      //       - `e5(3,4)` -> over `d4` -> `c3(5,2=EMPTY!)` => `1... e5:c3` UNIQUE FORCED!!
      //       (Board after `1... e5:c3`: White `b2, d2, e3`; Black `a5, c5, c7, c3`!).
      //     Step 2: White to move! Does White have ANY legal captures?
      //       - `d2(6,3)` touches `c3(5,2='b')`, behind `c3` is `b4(4,1=EMPTY!)`!
      //       - Does `b2(6,1)` also touch `c3(5,2='b')` -> behind `c3` is `d4(4,3=EMPTY!)` -> AND from `d4(4,3)` over `c5(3,2='b')` -> `b6(2,1)` -> over `c7(1,2='b')` -> `d8(0,3 King)`!!
      //     Aha! On Step 2 `b2` ALREADY sees `b2:d4:b6:d8`! Good thing we traced!
      //     How do we block `b2` on Step 2 until Step 3?!
      //     We want ONLY `d2:b4` legal on Step 2 (so `b2` must wait, OR `d4` must be blocked on Step 2 until `2... a5:c3` doesn't change `d4`? Wait: on Step 2 `d4` became empty!). If instead White is on `d4`? No. Put Black `b6`? No, move **`b2` to `a1(7,0)`** (`1-step away`? Still Step 2 `d2:b4` takes `c3` AND lands `b4` -> `a5` captures `b4` -> `a1` wouldn't be at `b2`). Put White on `d4`? No! Block `b2` from landing `d4` on Step 2 by having a piece on `d4`, OR don't put `b2` at `b2` yet — have `a1–b2`? No!
      //     Just block `b2` on Step 2 by making Step 2 **`2. d2:b4`** (`b4` empty) and Step 3 **`3. a1:c3`? No `a1` doesn't jump `c3` unless `b2` occupied by `'b'`!**
      //     Put Black at `c3` -> `d2:b4` vacates `c3`, Black `a5:c3` fills `c3`, AND `b2` was already blocked because... WAIT: in **`2. d2:b4`**, `c3` WAS OCCUPIED BEFORE `2. d2:b4`!
      //     Think: What if on Step 2 `b2` couldn't jump `c3` because **`d4` WAS STILL OCCUPIED ON STEP 2**?!
      //     Yes! Suppose `e5` wasn't jumped over `d4` — suppose **`f4–e5`** happened!
      //     OR even cleaner: Step 1 `1. g5–f6`, Black `e7:g5`; Step 2 `2. e3–f4`, Black `g5:e3`; Step 3 `3. d2:f4:d6:f8`!!
      //     CHECK THAT ONE!!
      //     - Initial: White `d2(6,3)`, `e3(5,4)`, `g5(3,6)`. Black `e7(1,4)`, `e5(3,4)`, `c7?` No, `e5(3,4)`, `e7(1,4)`, `g7?` Wait after `1. g5–f6`, if `g7` isn't there (`e7:g5`), `g5` becomes Black, `e7` becomes empty (`e5` still there, `e7` empty -> so Step 3 finishes at `d6`? Add `g7(1,6)` -> Step 3 `d2:f4:d6`? Add `c7(1,2)`! `d6(2,3):b8(0,1 King)` over `c7(1,2)`!).
      //     CHECK STEP 1 (`1. g5–f6`):
      //     White: `d2(6,3)`, `e3(5,4)`, `g5(3,6)`. Black: `e7(1,4)`, `e5(3,4)`, `c7(1,2)`!
      //     - Initial White captures? `g5(3,6)` touches `f6, h6` (empty). `e3(5,4)` touches `d4, f4` (empty). `d2(6,3)` touches `c3, e3('w')`. NONE!
      //     - Move 1: White plays `1. g5–f6` (`g5` vacates, `f6` occupied by White).
      //     - Black captures: `e7(1,4)` -> over `f6(2,5='w')` -> `g5(3,6=EMPTY!)` => `1... e7:g5` UNIQUE FORCED!!
      //     - Step 2: White plays `2. e3–f4`! (`e3` vacates, `f4` occupied by White). Any White capture BEFORE `2. e3–f4`? `d2`, `e3`, `f6(moved? no g5='b', f6 empty)`: `e3` touches `d4, f4`; `d2` touches `c3, e3('w')`. NONE!
      //       After `2. e3–f4`: Black `g5(3,6)` -> over `f4(4,5='w')` -> `e3(5,4=EMPTY!)` => `2... g5:e3` UNIQUE FORCED!!
      //       Wait what about `e5(3,4)`?! Does `e5(3,4)` ALSO capture `f4(4,5='w')` -> `g3(5,6=EMPTY!)`?!
      //       YES, unless **`g3(5,6)` IS OCCUPIED BY WHITE `'w'`**!!
      //       Check putting White `'w'` on `g3(5,6)`: Does `g3` interfere with Step 1? `g3` touches `f4, h4` empty! Does `g3` interfere with Step 3 (`d2:f4:d6:b8`)? NOT ON `d2-f4-d6-b8` path! Does `g3` block `e5:g3`? YES, because `g3` is OCCUPIED by `'w'`!!
      //     - Step 3: Board after `2... g5:e3`: White `d2(6,3)`, `g3(5,6)`. Black `e3(5,4)`, `e5(3,4)`, `c7(1,2)`.
      //       White plays `3. d2:f4:d6:b8` (`d2->f4[e3] -> d6[e5] -> b8[c7]`)!! (Notice `g3(5,6)` has empty `f4, h4` — NO other capture!).
      //     OH!! 100% MATHEMATICALLY AIRTIGHT 3-STEP PUZZLE!! Look how clean that is!
      LogicPuzzle(
        id = "puzzle_44_master_double_wing_b2",
        order = 44,
        title = "Двойное завлечение в 3 хода",
        themeTag = "Комбинация в 3 хода",
        difficulty = PuzzleDifficulty.MASTER,
        prompt = "Редкая задача в 3 хода! Двумя последовательными жертвами проведи чёрную шашку e7 через всю правую сторону прямо на поле e3 под тройной удар в дамки b8!",
        initialRows = listOf(
          "........",
          "..b.b...",
          "........",
          "....b.w.",
          "........",
          "....w.w.",
          "...w....",
          "..w.....",
        ),
        steps = listOf(
          PuzzleStep(
            acceptedNotations = setOf("g5–f6"),
            hintMove = CheckersMove.simple("g5", "f6"),
            hintText = "Шаг 1: пожертвуй g5–f6! Шашка e7 обязана прыгнуть на g5.",
            botAutoReply = CheckersMove.captureChain(Triple("e7", "g5", "f6")),
            replyCaption = "Отлично! Теперь переведи шашку в основание новой приманки: e3–f4!",
          ),
          PuzzleStep(
            acceptedNotations = setOf("e3–f4"),
            hintMove = CheckersMove.simple("e3", "f4"),
            hintText = "Шаг 2: отдай e3–f4! Шашка g3 прикроет поле от e5, поэтому g5 вновь прыгнет на e3!",
            botAutoReply = CheckersMove.captureChain(Triple("g5", "e3", "f4")),
            replyCaption = "Все 3 цели выстроились! Собери цепочку шашкой d2 прямо в Дамки b8!",
          ),
          PuzzleStep(
            acceptedNotations = setOf("d2:f4:d6:b8"),
            hintMove = CheckersMove.captureChain(
              Triple("d2", "f4", "e3"),
              Triple("f4", "d6", "e5"),
              Triple("d6", "b8", "c7"),
              promotesFlags = listOf(false, false, true),
            ),
            hintText = "Шаг 3: бей d2 → f4 → d6 → b8 (Дамка)!",
          ),
        ),
        successExplanation = "Гроссмейстерский триптих! За два шага одна и та же шашка e7 дошла до поля e3 и погибла в тройном ударе!",
      )
    },

    LogicPuzzle(
      id = "puzzle_45_master_triple_step_left",
      order = 45,
      title = "Левофланговый триптих в 3 хода",
      themeTag = "3-ходовая комбинация",
      difficulty = PuzzleDifficulty.MASTER,
      prompt = "Ещё одна глубокая 3-ходовая комбинация! Перетащи чёрную шашку с c7 через всё поле на e3, чтобы пробить дорогу шашке f2 в дамки h8!",
      initialRows = listOf(
        "........",
        "..b...b.",
        "........",
        ".w..b...",
        "........",
        "..w.w...",
        ".....w..",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("b5–a6"),
          hintMove = CheckersMove.simple("b5", "a6"),
          hintText = "Check dark squares: b5 is `(3,1)` EVEN LIGHT!! Wait mirror #44 across the center columns (`col -> 7 - col` + fix parity OR `row` stays and flip only `col`? Wait col flip swaps colors! Keep #44 shifted 1 col/row? No, shift `c3,e3` etc.: White `f2(6,5)`, `e3(5,4)`, `c3(5,2)`, `a5(3,0)`. Wait `a5(3,0)`->`b6(2,1)`!! Let's verify exact mirror of #44 shifted cleanly!).",
        ),
      ),
      successExplanation = "",
    ).let {
      // Exact parity-preserving shift of #44:
      // Actually shift #44 LEFT by 2 columns?
      // `e7(1,4)->c7(1,2)`, `e5(3,4)->c5(3,2)`, `c7(1,2)->a7(1,0)` [wait b8 needs c7: c5->b8? No `f4:d6:b8` -> `d4:f6:h8`!!]
      // Wait reverse direction to finish on `h8(0,7)`!
      // Targets for `3. f2:d4:f6:h8`: `e3(5,4)`, `e5(3,4)`, `g7(1,6)`!
      //Wait in #44, `e7(1,4)` jumped `f6` to `g5(3,6)`, then `g5` jumped `f4` to `e3(5,4)`!
      // Mirror on LEFT side (`d2` -> `f2`? Wait: `e3(5,4)` stays `e3`, NO! `c3(5,2)`!):
      // Let's trace jumping onto `c3(5,2)`:
      // Step 3 wants: `3. d2:b4:d6:f8` (over `c3`, `c5`, `e7`) OR `3. b2:d4:f6:h8` (over `c3`, `e5`, `g7`)!
      // Let's use `3. b2:d4:f6:h8`! Requires Black at `c3(5,2)`, `e5(3,4)`, `g7(1,6)`!
      // Wait how does `c3(5,2)` arrive on Step 2?
      // Step 2: Black `a5(3,0)` jumps OVER `b4(4,1='w')` -> lands `c3(5,2)`!!
      // To prevent `c5(3,2)` from jumping `b4(4,1)` -> `a3(5,0)`, put White `'w'` on `a3(5,0)`!!
      // Wait `e5(3,4)` doesn't touch `b4(4,1)`. What about Step 1? How does Black arrive on `a5(3,0)`?
      // From `c7(1,2)` jumping OVER `b6(2,1='w')` -> lands `a5(3,0)`!!
      // So White starts at `a5`? NO, White starts at `a5(3,0)`? Wait `a5` jumps to `b6` (`1. a5–b6`)!
      // CHECK EVERY PIECE FOR `3. b2:d4:f6:h8`!!
      // Initial White: `b2(6,1)`, `a3(5,0)`, `c3(5,2)`, `a5(3,0)`.
      // Initial Black: `c7(1,2)`, `e5(3,4)`, `g7(1,6)`.
      // Parity check: `b2(6,1=7), a3(5,0=5), c3(5,2=7), a5(3,0=3), c7(1,2=3), e5(3,4=7), g7(1,6=7)` ALL DARK!
      // Initial White captures? NO (`a5` sees `b6` empty, `c3` sees `b4,d4` empty, `a3` sees `b4` empty, `b2` blocked).
      // Step 1: White `1. a5–b6`! (`a5` vacates, `b6` occupied by White).
      //   Black to move: `c7(1,2)` -> `b6(2,1='w')` -> `a5(3,0=EMPTY!)` => `1... c7:a5` UNIQUE FORCED!
      // Step 2: White `2. c3–b4`! (`c3` vacates, `b4` occupied by White).
      //   Black to move: `a5(3,0)` -> `b4(4,1='w')` -> `c3(5,2=EMPTY!)` => `2... a5:c3` UNIQUE FORCED! (Does `e5` touch `b4`? No).
      // Step 3: White `b2(6,1)` -> `d4(4,3)[c3]` -> `f6(2,5)[e5]` -> `h8(0,7, KING!)[g7]`!!
      // 100% VERIFIED 3-STEP COMBINATION WITH RIGHT-SIDE CORONATION AT h8!!
      LogicPuzzle(
        id = "puzzle_45_master_triple_step_left",
        order = 45,
        title = "Сквозь всю доску на h8",
        themeTag = "3-ходовая комбинация",
        difficulty = PuzzleDifficulty.MASTER,
        prompt = "Гроссмейстерский 3-ходовый расчёт: двумя жертвами переведи чёрную c7 на c3, а затем смети диагональ от b2 до самой Дамки h8!",
        initialRows = listOf(
          "........",
          "..b...b.",
          "........",
          "w...b...",
          "........",
          "w.w.....",
          ".w......",
          "w.......",
        ),
        steps = listOf(
          PuzzleStep(
            acceptedNotations = setOf("a5–b6"),
            hintMove = CheckersMove.simple("a5", "b6"),
            hintText = "Шаг 1: пожертвуй крайнюю a5–b6! Чёрная c7 прыгнет на a5.",
            botAutoReply = CheckersMove.captureChain(Triple("c7", "a5", "b6")),
            replyCaption = "Первая переправа сработала! Теперь отдай c3–b4!",
          ),
          PuzzleStep(
            acceptedNotations = setOf("c3–b4"),
            hintMove = CheckersMove.simple("c3", "b4"),
            hintText = "Шаг 2: шагни c3–b4! Чёрная шашка снова обязана прыгнуть на c3!",
            botAutoReply = CheckersMove.captureChain(Triple("a5", "c3", "b4")),
            replyCaption = "Трасса b2 → d4 → f6 → h8 открыта! Бей до угловой Дамки!",
          ),
          PuzzleStep(
            acceptedNotations = setOf("b2:d4:f6:h8"),
            hintMove = CheckersMove.captureChain(
              Triple("b2", "d4", "c3"),
              Triple("d4", "f6", "e5"),
              Triple("f6", "h8", "g7"),
              promotesFlags = listOf(false, false, true),
            ),
            hintText = "Шаг 3: бей b2 → d4 → f6 → h8 (Дамка)!",
          ),
        ),
        successExplanation = "Изумительно! За 3 хода чёрная шашка пролетела с c7 на c3 и открыла путь b2:d4:f6:h8!",
      )
    },

    LogicPuzzle(
      id = "puzzle_46_master_king_capture_choice",
      order = 46,
      title = "Выбор лучшего прострела",
      themeTag = "Превращение в бою",
      difficulty = PuzzleDifficulty.MASTER,
      prompt = "Подставь шашку на край так, чтобы другая шашка на лету стала Дамкой и за один ход взяла максимум фигур!",
      initialRows = listOf(
        "........",
        "......b.",
        ".....w..",
        "........",
        "........",
        "........",
        "........",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("f6–e7"),
          hintMove = CheckersMove.simple("f6", "e7"),
          hintText = "Let's make a clean 2-step promotion + 3-piece capture ending!",
        ),
      ),
      successExplanation = "",
    ).let {
      // Fresh 2-step MASTER #46: Step 1 quiet move -> Step 2 mid-jump promotion to b8 or f8 + 2 more King captures!
      // White: `e5(3,4='w')`, `e3(5,4='w')`? No:
      // Target Step 2: `b6(2,1) -> d8(0,3 King!) -> f6(2,5) [maybe g5] -> d2?`
      // Better: `f6(2,5) -> h8(0,7 King) -> no diag from h8!`. `d6(2,3) -> b8(0,1 King!)` -> from `b8(0,1)` diagonal goes `c7, d6, e5, f4, g3, h2`!
      // Put Black at `f4(4,5='b')` AND `g3`? No! Put Black at `e5(3,4='b')` -> lands `g3(5,6)` -> from `g3(5,6)` over `f2(6,5='b')` -> lands `e1(7,4)`!!
      // Check path `d6(2,3) -> b8(0,1 King!)` [over `c7`] -> `g3(5,6)` [over `e5`] -> `e1(7,4)` [over `f2`]!
      // Wait make `c7` NOT jumped immediately before Step 1 — have `b6(2,1)` NOT present? No: Step 1 creates `c7`? No, Step 1 creates `e5` OR frees `d6`!
      // Suppose White has `e3(5,4)` (step `e3–d4`? No), suppose White has **`c5(3,2='w')`** -> `1. c5–d6`!
      // No, if White is on `d6(2,3)` ALREADY, `d6` touches `c7`! To keep `d6` from touching `c7` initially, White starts at `e5(3,4)` -> moves `1. e5–d6`? Then `c7` jumps `e5`!
      // Instead Black jumps ONTO `c7` or `e5`!!
      // Make `e5(3,4)` come from Black `g7(1,6)` jumping OVER White `f6(2,5)` -> lands `e5(3,4)`!
      // Check: White `d6(2,3)` touches `c7(1,2)`? Still jumps `c7` on Move 1! Keep **`d6` away on `c5`? No, keep `c7` empty initially, or White plays `1. g5–f6`, Black `e7:g5`, OR White `1. e5–f6`, Black `g7:e5`, and `d6` ALSO has a helper? No, if White starts at `d6`, `d6` captures!**
      // Put White at `c5(3,2='w')` AND `g5(3,6='w')` -> NO! Look at `d6(2,3)` from `b4`:
      // White `e3(5,4)` -> no. White **`c5(3,2)`** moves **`1. c5–b6`** (NO capture!). Wait then `b6:d8` needs `c7(1,2='b')`, so put White **`b4(4,1)`** (`1. b4–c5`), no! Put Black at `d6(2,3)`, `b8`? No!
      // Wait if White starts at `b6(2,1)` (`b6` does NOT touch `e7`), and **`c7(1,2)` is EMPTY initially**, put Black at **`d8`? No, no men on 8**. Put Black at **`a5(3,0)`** + **`f6(2,5)`**?
      // Easiest! White starts at **`a5(3,0='w')`**, **`g5(3,6='w')`**. Wait if `g5–f6`, Black `e7:g5`, then White **`b4(4,1):d6`?**.
      // Look at `d6(2,3)` -> `f8(0,5, King)` -> `h6(2,7)` [over `g7`]! And how does Black step onto `e7`? From `c5`? No! From **`d8`** no.
      // What about `b6(2,1)` -> `d8(0,3 King!)` -> `h4(4,7)` [over `f6(2,5)`] -> `f2(6,5)` [over `g3(5,6)`]!
      // How do we delay `b6:d8` on Step 1?! Put Black on **`e7(1,4)`** or **`f6`**, and **`b6` IS EMPTY initially (`b4` plays `1. b4–a5`? Still `c7` empty?)** — wait! Put White **`a5(3,0)`** (`1. a5–b6`? No, Black `c7:a5` jumps it!), put White **`c5(3,2)`**? No, **`a5`** vs `c7` -> Put White **`c5`** and Black `d6`? No.
      // What if **on Move 1 White promotes WITHOUT capture (`c7–d8`) or blocks**, forcing Black to step ONTO `f6` (`g7`?), then Step 2 King captures 3?!
      // YES!! Try **`1. e5–f6`** (NOT touching `c7`), Black `g7:e5` -> Wait `d6` captures? No, White is at **`a5(3,0)`**? No, **`e5(3,4)`**, **`c5(3,2)`**, AND **`f2(6,5)`**?
      // Try `1. g5–f6`: Black `e7:g5` (leaving `e7` empty, `g5` Black!).
      // And on Step 1, why didn't White `b6(2,1)` jump `c7(1,2)`? Because `c7` isn't touched by `a5`, and **`b4(4,1)`** doesn't touch `c7`! Wait if White has **`a5`**? No, after `1... e7:g5`, White MUST start `b6:d8:g5`? If `b6(2,1)` is at `b6`, it ALREADY jumps `c7(1,2)` before Move 1!
      // Wait: if **White has `f6` empty AND `e7` jumps onto `g5`**, AND White plays **`1. g5–f6` (`1... e7:g5`)**, how does `b6` arrive on `b6`? No, **`c5(3,2)`** jumps **`d6(2,3)`** -> NO, what if **`c5(3,2='w')`** captures **`e7`?**
      // Simple solution to delay a King-flight capture: put a **White Pawn at `d8`**? No! Put White Pawn at **`b4(4,1)`**, Black **`c5(3,2)`**? No, `b4` touches `c5`!
      // Put White at **`g5(3,6)`**, White at **`a1(7,0, KING!)`**? No, White at **`b6` blocked by NOTHING?**
      // Wait if White starts at **`a3(5,0)`**, **`e3(5,4)`**, etc. OR simply **`b6` is NOT needed**: White man `c5(3,2)` can't jump `d8` unless `d6`! What if **White man `e5(3,4)` -> `1. e5–f6` (`1... g7:e5`), AND White King `h2(6,7)` wasn't blocked, wait on Step 1 why didn't `h2` capture?** Only if `e5` wasn't Black yet! Because `g7:e5` MADE `e5` Black! Wait look at `39` (`h2` captured `e5` after `g5–f6`) — we already did that!
      // For `#46`, let's do **Mid-jump promotion delayed by blocking `d8` with `c7` -> NO, `b6(2,1)` can't capture `c7(1,2)` if `d8` IS OCCUPIED BY BLACK `b`? No men on `d8`! Put Black KING `B` on `d8`!**
      // Wait if Black King is at `d8`, what moves it off `d8`? Sacrifice onto `c5`!! Wait:
      // CHECK THAT GENIUS IDEA!!
      // Black King `B` sits on `d8(0,3)` (blocking `b6:d8`!)!
      // Initial White: `b6(2,1='w')`, `e5(3,4='w')`.
      // Initial Black: `d8(0,3='B')`, `c7(1,2='b')`, `f6(2,5='b')`, `e3(5,4='b')`!
      // Check Step 0: Does `b6(2,1)` capture `c7(1,2)`? NO, `d8` is OCCUPIED by `B`! Does `e5(3,4)` capture `f6(2,5)`? Behind `f6` is `g7(1,6)` EMPTY!! Oh, `e5` would jump `f6`! Move `f6` to `g5(3,6)`? No, `e5` sees empty `f6`! Instead move White `e5` to `f4(4,5)` -> moves `1. f4–e5`? Still `d8` only jumps if `e5` is on `d8` diag? `d8-h4` passes through `e7, f6, g5, h4`!
      // Put White at `f4(4,5='w')` -> moves `1. f4–g5`!
      // `d8(0,3='B')` captures OVER `g5(3,6='w')` -> lands `h4(4,7)`! (`g5` vacates `d8`!!).
      // Wait what if `d8` lands on `f4`? `h4` can be jumped next by `b6:d8:h4`? No, if `b6:d8` becomes King on `d8`, it jumps OVER `f6(2,5)` -> lands `g5(3,6)` -> wait if `d8(B)` is at `h4(4,7)` AND `g3(5,6)` has Black? No, `g5` already removed! Put Black at `f6(2,5)`? If `f6` is there initially, `d8(B)` jumps `f6`? No, `d8` and `f6` are same color, adjacent `e7` empty, `f6(b)` blocks `d8(B)`!
      // Actually use **`f6(2,5='w')`** -> **`1. f6–g7`**? No, `g7` blocked.
      // Put White `e5(3,4)` (`f6` empty), `d8(B)`, `c7(b)` -> Wait `d8(0,3='B')` jumps OVER `f6(2,5='w')` -> lands `g5(3,6)` or `h4(4,7)`! Put White on `g5(3,6='w')`? Still how does White get to `f6` cleanly? From `e5(3,4='w')` -> `1. e5–f6`!!
      // Check! If `e5(3,4='w')`, `b6(2,1='w')`, `c7(1,2='b')`, `d8(0,3='B')`, `g5`?
      // Trace `1. e5–f6`:
      //   `d8(0,3='B')` captures OVER `f6(2,5='w')` -> lands `g5(3,6)` or `h4(4,7)`!
      //   To force `d8:g5`: put White or Black at `h4(4,7)` -> wait put Black `'b'` at `h4(4,7)`?! Then `d8` MUST land `g5(3,6)`!
      //   WAIT A SECOND! Put White at `f2`? No, if `d8:g5`, now `d8` IS EMPTY, AND `g5` HAS `'B'`, AND `h4` HAS `'b'`? No, `d8` is on `b6:d8`, then from `d8`, King jumps `g5(3,6='B')` -> lands `h4(4,7)`? If `h4` is occupied, can't land `h4`!
      //   Instead put `h4` EMPTY and put Black at **`e3(5,4='b')`** AND **`f6` vacates**, so if `d8:h4` -> lands `h4(4,7)` -> then `b6:d8` (becomes King!) -> `d8:g5`? No `g5` empty if `d8` went `h4`. What if `h4` HAS `'b'` so `d8` MUST land `g5(3,6)`, AND from `g5(3,6)`? No, from `d8`, `b6:d8` jumps `g5(3,6)` -> lands `h4`? Still blocked! What if `h4` WAS White `'w'` (No) -> what if `f4(4,5)` and **`d8` is forced onto `g5` by NOTHING on `h4`?** If `h4` is empty, `d8` can choose `g5` OR `h4`. What if `1... d8:g5` leaves `g5`? Better: put White `'w'` at `f4(4,5)`? Wait `f4` isn't on `d8-h4`, `g5(3,6)` IS! Put Black `'b'` at **`e3(5,4)`** + put **`h4(4,7)` EMPTY**, wait if `h4(4,7)` has White `'w'` -> NO captures by `h4` (`g5` empty before move 1!). After `1. e5–f6`, if `h4(4,7)` is `'w'`, `d8(B)` CANNOT land on `h4(4,7)`, so `1... d8:g5` IS UNIQUE!!Wait from `g5`, can `B` jump `h4(w)`? Behind `h4` is BORDER -> NO!
      //   Wait then on Step 2, after `1... d8:g5`, `d8` is EMPTY! So `b6(2,1)` jumps `c7(1,2)` -> lands `d8(0,3 KING!)` -> jumps `g5(3,6='B')` -> still `h4` is occupied by `'w'`! Put `b` on `g5`? No, `b6:d8` lands `d8`, and then `d8` jumps `g5` ONLY if `h4` is empty! Why did we put `h4='w'`? So `d8:h4` wouldn't happen! Put `d8` landing on `e5`?
      //   Simpler! Use **`d8` capturing OVER `e7(1,4='w')` (`1. d6–e7`)**, Black **`d8:f6`** [blocked on `g5` by `'b'` or `'w'`? If `h4` empty and `g5='b'`, Black MUST land `f6`!! And `b6:d8` becomes King on `d8` and jumps OVER `f6(2,5='B')` -> lands `g5`? NO, `g5='b'` blocks `h4` too -> put `g5` empty and `e3` empty -> Wait `d8(0,3)`->`e7(1,4)`->`f6(2,5)`->`g5(3,6)`->`h4(4,7)`: if **`g5(3,6)` IS OCCUPIED BY `'b'`, then `d8(0,3)` CANNOT land `g5` OR `h4`, SO `d8` CAN ONLY LAND `f6(2,5)`!!**
      //   AND WHY CAN `b6:d8` STILL JUMP AFTER THAT?! Because after `b6:d8`, `d8` ONLY jumps `f6` if there is space behind `f6` (`g5` was occupied!). Turn to the OTHER diagonal from `d8`?! No diag other than `d8-a5` and `d8-h4`! Look at `d8-a5`: `c7` was just jumped! So `d8` MUST jump `f6`? Yes if `g5` is empty, `d8` lands `g5` or `h4`! So don't put `d8(B)` on `f6`, put `d8(B)` on **`c5` (`d6`?)** -> **`b8(0,1='B')`**!!
      //   CHECK `b8(0,1='B')`:
      //   Initial White: `e5(3,4='w')`, `b6(2,1='w')`. Black: `b8(0,1='B')`, `c7(1,2='b')`? Wait `b8` touches `c7`!
      //   No need to force `d8` at all — remember Puzzle #46 can cleanly showcase **2-step quiet sacrifice + mid-jump promotion to King** cleanly on open squares! Let's build one WITHOUT edge complications:
      //   Suppose Step 2 is: **`2. b6:d8:g5:e3`** (jumps `c7`, promotes on `d8`, jumps `f6`, jumps `f4`)!!
      //   Check what's needed before Step 2: Black at `c7(1,2)`, `f6(2,5)`, `f4(4,5)`!
      //   Put Black at `c7(1,2)` and `f4(4,5)` initially, and make **`1. g5–f6`** -> Black `e7:g5`? No, we need Black ON `f6(2,5)`! How does Black land on `f6(2,5)`? From `d8(0,3='B')`? No, from `h8(0,7='B')` jumping `g7` -> `f6`!! Put White on `f6`? No, `1. h6–g7`, Black `h8:f6`!!
      //   CHECK THAT:
      //   White `h6(2,7='w')` -> moves **`1. h6–g7`**!
      //   Black King **`h8(0,7='B')`** captures OVER `g7(1,6='w')` -> lands `f6(2,5)`!! (Put Black `'b'` on `e5(3,4)`? Wait if `e5(3,4)` is `'b'` then `h8` MUST land `f6(2,5)`!! Wait `e5` is on `b2-h8` diag!! Wait `b6(2,1)` jumps `c7(1,2)`->`d8(0,3 King!)`->over `f6(2,5='B')`->lands `g5(3,6)`->over `f4(4,5='b')`->lands `e3(5,4)`!!).
      //   WAIT A SECOND!! WHY DIDN'T `b6` JUMP `c7` BEFORE MOVE 1?! Because `b6` WAS AT **`a5(3,0='w')` -> NO, `b4(4,1)`?** Wait `1. h6–g7` was Move 1, how does `b6` get there? If `b6` is ALREADY at `b6`, `b6:d8` would be legal on Move 1! Instead don't put `f6` until Step 1? Still `b6` captures `c7` on Move 1!
      //   Unless **`c7(1,2)` IS WHERE BLACK LANDS ON MOVE 1**!!
      //   YES!! GENIUS!! Keep `c7(1,2)` EMPTY before Move 1!!
      //   White starts at `b6(2,1='w')` AND `d6`? No, `b6` sees empty `a7, c7`!
      //   Black starts at **`e7`? No, `e5(3,4)` -> moves onto `c7` via `d8(0,3='B')` jumping `e7`?** No, `b8(0,1='B')` NO! Black **`a5`**? No, Black **`e5(3,4='b')`** doesn't jump to `c7` (`d6` -> `c7`).
      //   Wait: how can Black jump ONTO `c7(1,2)`? From `e5(3,4)` over `d6(2,3='w')`!!
      //   CHECK THAT: White has `b6(2,1='w')` AND **`c5(3,2='w')`** -> Move `1. c5–d6`!
      //   Black at `e5(3,4='b')`? If Black at `e5` and `f6` initially, `b6` sees `e5`? NO! Put Black at **`e5(3,4='b')`**, **`f6(2,5='b')`**, **`f4(4,5='b')`**!
      //   Wait after `1. c5–d6`, Black `e5(3,4)` captures OVER `d6(2,3='w')` -> lands `c7(1,2)`!!
      //   Does `f6` capture anything? `d6` empty? NO `d6` is occupied, `c5` was vacated.
      //   NOW AFTER `1... e5:c7`:
      //   Black is at **`c7(1,2)`**, **`f6(2,5)`**, **`f4(4,5)`**!
      //   Wait does `b6(2,1)` capture ALL THREE (`c7` -> `d8 King` -> `f6` -> `g5` -> `f4` -> `e3`)?!
      //   WAIT CHECK `b6(2,1)`->`d8(0,3)` [over `c7`] -> `g5(3,6)` [over `f6`] -> `e3(5,4)` [over `f4`] -> what if White puts `d2`? No, put Black on `e3`? No, `e3` empty, `d2(6,3)`? Enough — **3-capture with mid-jump promotion to King on d8 AFTER quiet sacrifice `c5–d6`!**
      //   Let's check ALL pieces BEFORE `1. c5–d6`:
      //   White: `b6(2,1)`, `c5(3,2)`.
      //   Black: `e5(3,4)`, `f6(2,5)`, `f4(4,5)`.
      //   HOLD ON! Check if White `b6(2,1)` or `c5(3,2)` has ANY captures BEFORE `1. c5–d6`:
      //   - `b6(2,1)`: touches `a7, c7` (empty), `a5` (empty), `c5('w')`. NO!
      //   - `c5(3,2)`: touches `b6('w'), d6` (empty), `b4, d4` (empty). NO!
      //   - `e5(3,4)`? Adjacent to `f6('b'), f4('b'), d6(empty), d4(empty)`.
      //   Now Step 1: White `1. c5–d6`!
      //   Check ALL Black captures after `1. c5–d6`:
      //   - `e5(3,4)` -> over `d6(2,3='w')` -> lands `c7(1,2=EMPTY!)` => `e5:c7` LEGAL!
      //   - `f6(2,5)` -> touches `g7, e7, g5, e5('b')` -> NO!
      //   - `f4(4,5)` -> touches `e5('b'), g5, e3, g3` -> NO!
      //   So `1... e5:c7` is 100% UNIQUE FORCED!!
      //   Step 2: `b6(2,1)` captures `c7(1,2)` -> promotes to King on `d8(0,3)` -> captures `f6(2,5)` -> lands `g5(3,6)` -> captures `f4(4,5)` -> lands `e3(5,4)` or `d2(6,3)`!!
      //   Wait: can `d8` land `h4(4,7)`? If it lands `h4(4,7)`, it CANNOT jump `f4(4,5)` (horizontal)! So `RussianCheckersEngine` REQUIRES landing `g5(3,6)` to continue jumping `f4(4,5)`!! Then after `f4`, it can land `e3(5,4)`, `d2(6,3)`, or `c1(7,2)`!
      //   ALL THREE NOTATIONS `b6:d8:g5:e3`, `b6:d8:g5:d2`, `b6:d8:g5:c1` ARE ACCEPTED!
      //   THAT IS ONE OF THE CLEANEST PUZZLES IN THE ENTIRE APP!
      LogicPuzzle(
        id = "puzzle_46_master_king_capture_choice",
        order = 46,
        title = "Поворот в Дамки на d8",
        themeTag = "Превращение в бою",
        difficulty = PuzzleDifficulty.MASTER,
        prompt = "Тончайшая 2-ходовая комбинация: подставь шашку c5 на d6 так, чтобы шашка b6 прорвалась на d8 и сразу дальнобойной Дамкой срубила 3 цели!",
        initialRows = listOf(
          "........",
          "........",
          ".w...b..",
          "w.w.b...",
          ".....b..",
          "........",
          "........",
          "........",
        ),
        steps = listOf(
          PuzzleStep(
            acceptedNotations = setOf("c5–d6"),
            hintMove = CheckersMove.simple("c5", "d6"),
            hintText = "Шаг 1: шагни c5–d6! Чёрная e5 обязана прыгнуть на c7 — прямо под твою b6!",
            botAutoReply = CheckersMove.captureChain(Triple("e5", "c7", "d6")),
            replyCaption = "Чёрная встала на c7! Прыгай на d8 (Дамка!), тормози на g5 и руби последнюю f4!",
          ),
          PuzzleStep(
            acceptedNotations = setOf("b6:d8:g5:e3", "b6:d8:g5:d2", "b6:d8:g5:c1"),
            hintMove = CheckersMove.captureChain(
              Triple("b6", "d8", "c7"),
              Triple("d8", "g5", "f6"),
              Triple("g5", "e3", "f4"),
              promotesFlags = listOf(true, false, false),
            ),
            hintText = "Шаг 2: бей b6 → d8 (Дамка) → g5 → e3 (или d2 / c1)!",
          ),
        ),
        successExplanation = "Высший пилотаж! Правило мгновенного превращения в Дамку на d8 принесло 3 взятия за один ход!",
      )
    },

    LogicPuzzle(
      id = "puzzle_47_master_hidden_sacrifice",
      order = 47,
      title = "Скрытый удар по большой дороге",
      themeTag = "Синтез тактики",
      difficulty = PuzzleDifficulty.MASTER,
      prompt = "Замани вражескую шашку на главную диагональ и уничтожь сразу две цели дальнобойной Дамкой с g7?",
      initialRows = listOf(
        "........",
        "......W.",
        ".....b..",
        "........",
        "........",
        "........",
        "........",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("g7–h8"),
          hintMove = CheckersMove.simple("g7", "h8"),
          hintText = "Let's craft a clean 2-step Master puzzle #47!",
        ),
      ),
      successExplanation = "",
    ).let {
      // #47: Mirror of #46 cleanly (left side promotion `f6:d8:a5:c3/d2`?!):
      // Let's trace #47:
      // Step 2: `f6(2,5) -> d8(0,3 King!)` [over `e7(1,4='b')`] -> `a5(3,0)` [over `c7(1,2='b')`? Wait `(0,3)->(1,2)[c7]->(2,1)[b6]->(3,0)[a5]`! So `b6` is target, lands `a5`!] -> `c3(5,2)` [over `b4(4,1='b')`]!!
      // Check Step 1 to put Black onto `e7(1,4)` (from `c5(3,2='b')` jumping OVER White `d6(2,3='w')`!):
      // White starts at `e5(3,4='w')` -> plays `1. e5–d6`!
      // Wait initial White: `f6(2,5)`, `e5(3,4)`. Initial Black: `c5(3,2)`, `b6(2,1)`, `b4(4,1)`.
      // Check BEFORE `1. e5–d6`:
      // - `f6(2,5)` touches `e7, g7, e5('w'), g5` -> NO!
      // - `e5(3,4)` touches `d6, f6('w'), d4, f4` -> NO!
      // Step 1: White `1. e5–d6`!
      //   Black `c5(3,2)` -> over `d6(2,3='w')` -> lands `e7(1,4=EMPTY!)` => `1... c5:e7` UNIQUE FORCED! (Does `b6` or `b4` jump? NO!).
      // Step 2: White `f6(2,5)` -> over `e7(1,4='b')` -> lands `d8(0,3, KING!)` -> over `b6(2,1='b')` -> lands `a5(3,0)` -> over `b4(4,1='b')` -> lands `c3(5,2)`, `d2(6,3)`, `e1(7,4)`!!
      // Wait notice `d8(0,3)->c7(1,2 empty)->b6(2,1='b')->a5(3,0)` -> `a5` IS THE ONLY LANDING SQUARE after `b6`! From `a5(3,0)` -> over `b4(4,1='b')` -> lands `c3/d2/e1`!
      LogicPuzzle(
        id = "puzzle_47_master_hidden_sacrifice",
        order = 47,
        title = "Зеркальный поворот на d8",
        themeTag = "Тройной удар Дамкой",
        difficulty = PuzzleDifficulty.MASTER,
        prompt = "Ещё одна вершина тактики: направь шашку c5 на e7, прорвись шашкой f6 на d8 и тут же смети левый край!",
        initialRows = listOf(
          "........",
          "........",
          ".b...w..",
          "..b.w.w.",
          ".b......",
          "........",
          "........",
          "........",
        ),
        steps = listOf(
          PuzzleStep(
            acceptedNotations = setOf("e5–d6"),
            hintMove = CheckersMove.simple("e5", "d6"),
            hintText = "Шаг 1: шагни e5–d6! Чёрная c5 обязана прыгнуть на e7!",
            botAutoReply = CheckersMove.captureChain(Triple("c5", "e7", "d6")),
            replyCaption = "Клетка e7 под прицелом! Прорвись на d8, остановись на a5 и добей b4!",
          ),
          PuzzleStep(
            acceptedNotations = setOf("f6:d8:a5:c3", "f6:d8:a5:d2", "f6:d8:a5:e1"),
            hintMove = CheckersMove.captureChain(
              Triple("f6", "d8", "e7"),
              Triple("d8", "a5", "b6"),
              Triple("a5", "c3", "b4"),
              promotesFlags = listOf(true, false, false),
            ),
            hintText = "Шаг 2: бей f6 → d8 (Дамка) → a5 → c3 (или d2 / e1)!",
          ),
        ),
        successExplanation = "Точный расчёт! Дамка d8 на одном дыхании повернула к борту a5 и забрала 3 фигуры!",
      )
    },

    LogicPuzzle(
      id = "puzzle_48_master_diamond_loop",
      order = 48,
      title = "Бриллиантовая петля",
      themeTag = "4-кратный удар дамкой",
      difficulty = PuzzleDifficulty.MASTER,
      prompt = "Подставь шашку на край и запусти дамку по полному бриллиантовому кольцу из 4 шашек!",
      initialRows = listOf(
        "........",
        "....b...",
        "...b....",
        "........",
        "........",
        "........",
        "........",
        "W.......",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("a1–d4"),
          hintMove = CheckersMove.simple("a1", "d4"),
          hintText = "Create 4-piece diamond loop with King + pawn sacrifice!",
        ),
      ),
      successExplanation = "",
    ).let {
      // Fresh 2-step 4-capture King diamond loop #48:
      // Rotate #41? Wait #41 was `b4:f8:h6:f4:d2`. What about rotating `(-2, -0)`? No, let's build a diamond around `e5, c5, c3, e3`!
      // Path from King `h8(0,7)`:
      // Leg 1: `h8(0,7)` -> over `f6(2,5)` -> lands `d4(4,3)`?
      // Better diamond corners: `g5(3,6) -> e7(1,4) -> b4(4,1) -> e1(7,4) -> g3(5,6)`!
      // Targets between corners:
      //   `g5(3,6) -> e7(1,4)`: `f6(2,5='b')`
      //   `e7(1,4) -> b4(4,1)`: `c5(3,2='b')` (or `d6(2,3='b')` — let's use `c5(3,2='b')`!)
      //   `b4(4,1) -> e1(7,4)`: `c3(5,2='b')` (or `d2(6,3='b')` — use `d2(6,3='b')`!)
      //   `e1(7,4) -> h4(4,7)`: `g3(5,6='b')` (or `f2(6,5='b')` — use `f2(6,5='b')`!)
      // Wait from `g5(3,6)` -> can King start at `h4(4,7)`?!
      // `h4(4,7) -> e7(1,4)` [over `f6`] -> `b4(4,1)` [over `c5`] -> `e1(7,4)` [over `d2`] -> `h4(4,7)` [over `f2` or `g3`]!
      // Wait `h4(4,7)` was the STARTING square! Can a King return to its starting square on the last leg? YES (`h4` is empty as soon as `h4` starts jumping!).
      // Verify NO intermediate square is crossed twice!
      //   Leg 1: `(4,7)->(3,6)->(2,5=f6)->(1,4)=e7`
      //   Leg 2: `(1,4)->(2,3)->(3,2=c5)->(4,1)=b4`
      //   Leg 3: `(4,1)->(5,2)->(6,3=d2)->(7,4)=e1`
      //   Leg 4: `(7,4)->(6,5=f2)->(5,6)->(4,7)=h4`!
      // WAIT A SECOND!! CHECK IF ANY OF THOSE FOUR TARGETS BLOCK EACH OTHER OR HAVE DOUBLE LANDINGS:
      //   Before Step 1, how do we stop `h4` from jumping immediately? Let `f6(2,5)` be EMPTY initially, and put `g7(1,6='b')` + `e5`? No, put White `'w'` on `g5(3,6)` -> moves **`1. g5–h6`? No, `1. g5–f6`**? If `g5` jumps `1. g5–f6`, Black `e7:g5` leaves `g5` occupied, blocking `h4`? No!
      //   Make `f6` come from **`1. f4–e5`** (no), make **`c5(3,2)`** come from `1. d4–c5`, Black `b6:d4`? No.
      //   Put White `'w'` at **`e7(1,4)`**? No! Put White `'w'` at **`e5(3,4='w')`** -> `1. e5–f6` -> Black `g7:e5` (wrong square!).
      //   Look: **`g7` -> `f6` -> `e5`**? No, we want Black TO LAND ON `f6(2,5)`, which means Black jumps FROM `h8` or `d8` or `e7`? No: Black jumps from **`h4`? No, `g5` -> `f6`**!! Look: White is on `g5`? If Black starts at **`h4`**? No. Black starts at **`d8(0,3='B')` -> over `e7` -> lands `f6`**!
      //   OR simply keep `f6` occupied by Black, and put **`d2(6,3)`** created by **`1. e3–d4`** -> No! Put Black on `f6(2,5)`, `c5(3,2)`, `f2(6,5)` initially, and keep **`d2(6,3)`** EMPTY initially! Wait then on Move 1 `h4` captures `f6 -> e7 -> b4 -> e1`? Still `3` captures would happen on Move 1!
      //   Why? Because `h4(4,7)` ALREADY sees `f6(2,5)`! To block `h4` from seeing `f6` on Move 1, a piece MUST sit on `g5(3,6)` or `f6` must NOT exist yet!
      //   And how can `g5(3,6)` move away on Move 1 AND still force Black? **`1. g5–f6` (`f6` empty initially!)** — wait if Black is on `e7(1,4)`, then `e7:g5` lands `g5`, STILL on `h4-e7` diag! What if Black starts on **`g7(1,6)`**? `g7:e5` (`e5` off diag -> still no `f6`)!
      //   Wait what if **`h4` WASN'T AT `h4` — White `b6(2,1)`? No, White King starts at `g5(3,6)`? No!**
      //   Wait what if **White `d4(4,3='w')` -> moves `1. d4–c5`** -> Black **`b6:d4`**... wait if `b6` moves, it's off `e7-b4`! Put Black on **`e7`**?
      //   EVEN SIMPLER! Block `h4` by **`g5(3,6='w')`** moving **`1. g5–h6`**, NO.
      //   Look at `e1`! On `h4->e7->b4->e1->h4`, there are 4 corners: `h4`, `e7`, `b4`, `e1`. Suppose **`d2(6,3)`** is occupied by `'b'`? No, **`f2(6,5)`**!
      //   Wait if **White King is at `e1(7,4)`**, and initially **`d2(6,3)` is `'w'` (blocking `e1` from jumping `c3`? No, if White moves `1. d2–c3`, Black `b4:d2` — now `d2` is BLACK, `b4` is EMPTY, and `e1(7,4)` captures BACKWARDS/FORWARDS `e1 -> a5?` over `c3`?!)**
      //   Wait check `e1(7,4)`:
      //   Initial: `e1(7,4='W')`, `d2(6,3='w')`, Black `b4(4,1='b')`, `c5(3,2='b')`... No, `d2(6,3)` blocks `e1(7,4)->c5(3,2)`!
      //   If `1. d2–e3`, then `f4(4,5='b')` jumps OVER `e3(5,4='w')` -> lands `d2(6,3)`! Wait can `d2` be jumped by `e1`? `e1` and `d2` are adjacent, so YES `e1:c3` (or `b4` or `a5`!) jumps `d2`!!
      //   LOOK! NOW WE CAN COMPLETE ALL 4 CORNERS FROM `e1(7,4)`!!
      //   Let's trace from `e1(7,4)`:
      //   - Step 1: White plays `1. d2–e3`! (`d2` vacates, `e3` becomes `'w'`).
      //   - Black `f4(4,5)` jumps OVER `e3(5,4)` -> lands `d2(6,3)`! (`f4` becomes EMPTY!!).
      //   - Step 2: White King `e1(7,4)` jumps:
      //     1. `e1(7,4)` -> over `d2(6,3='b')` -> lands `b4(4,1)` (Note: `c3` empty! Wait could it land `c3(5,2)` or `a5(3,0)`? Put next target from `b4(4,1)`: `b4(4,1)` -> over `c5(3,2='b')` -> lands `e7(1,4)`!! So ONLY `b4` continues!)
      //     2. `b4(4,1)` -> over `c5(3,2='b')` -> lands `e7(1,4)` (Put next target `f6(2,5='b')` -> lands `h4(4,7)`!)
      //     3. `e7(1,4)` -> over `f6(2,5='b')` -> lands `h4(4,7)` (or `g5(3,6)`! Add target `g3(5,6='b')` -> ONLY `h4(4,7)` continues? Check `(4,7)->(5,6)[g3]->(6,5)[f2]`! Notice `f4(4,5)` WAS vacated on Step 1 so `e7:h4` doesn't bump into `f4`! Actually wait `(1,4)->(2,5)[f6]->(3,6)->(4,7)` doesn't pass `f4` anyway, `(1,4)->(3,6)->(4,7)` hits `g3` from `h4`? No! From `h4(4,7)` -> `g3(5,6)` -> `f2(6,5)` -> `e1(7,4)`!! WAIT `h4` DOESN'T HIT `g3`, `h4(4,7)` goes `(5,6)[g3] -> (6,5)[f2]`!)
      //   VERIFY ALL 4 LEGS OF DIAMOND LOOP STARTING AT `e1(7,4)`!!
      //   Leg 1: `e1(7,4)` -> over `d2(6,3='b')` -> lands `b4(4,1)` (checks: `c3` has no continuation, `a5` has no continuation!)
      //   Leg 2: `b4(4,1)` -> over `c5(3,2='b')` -> lands `e7(1,4)` (checks: `d6` no continuation, `f8` no continuation!)
      //   Leg 3: `e7(1,4)` -> over `f6(2,5='b')` -> lands `h4(4,7)` (checks: `g5` no continuation! `h4` connects to `g3`!)
      //   Leg 4: `h4(4,7)` -> over `g3(5,6='b')` -> lands `f2(6,5)` or `e1(7,4)`!!
      //   CHECK INITIAL STATE BEFORE `1. d2–e3`:
      //   White: `e1(W, 7,4)`, `d2(w, 6,3)`.
      //   Black: `f4(b, 4,5)`, `c5(b, 3,2)`, `f6(b, 2,5)`, `g3(b, 5,6)`.
      //   Wait check if `e1(7,4)` captures `g3(5,6)` on Move 1!!
      //   `e1(7,4)->f2(6,5)->g3(5,6='b')->h4(4,7=EMPTY!)` -> OH! `e1` WOULD jump `g3` on Move 1!!
      //   Easy fix: swap `g3(5,6)` to `f2`? No, move `e1`'s block? No! Put **`f2` instead of `g3`**? Still jumped!
      //   Why not start Step 1 with **`1. d2` NOT blocking**, wait why not make **`g3` THE SQUARE BLACK LANDS ON AFTER STEP 1** (`1. f2–g3` -> `h4:f2` NO `f2` is on diag)!
      //   Make **`c5(3,2)`** or **`f6`** blocked? Put `g3` NOT on `e1-h4`: instead put `4th` target at **`f4(4,5)`** (`g5(3,6) -> e3(5,4)` — wait that hits `f4` from `g5`, which is AFTER `e7`!).
      //   TRACE `e7(1,4) -> g5(3,6)` [over `f6`]: from `g5(3,6)` -> over **`f4(4,5='b')`** -> lands **`e3(5,4)`**, `d2`, `c1`! Wait if Black starts at `e5`? No, if `f4(4,5)` is Black, does `e1(7,4)` touch `f4`? `e1(7,4)->f2->g3->h4` NO! Does `d2(6,3)` touch `f4`? NO! Does `e3(5,4)` touch `f4`? On Step 1 if White plays `1. d2–e3`, Black `f4:d2` removes `f4`! Instead make Black at **`h6`? No!**
      //   Make `4` targets: `d2(6,3)`, `c5(3,2)`, `f6(2,5)`, AND **`g5`? No: `b6`?**:
      //   Look at `e1` again: `e1(7,4)->b4(4,1)[d2] -> e7(1,4)[c5] -> g5(3,6)[f6] -> e3(5,4)` [needs `f4`]. Why did `f4` block? Because Step 1 was `1. d2–e3` (`f4:d2`). What if Step 1 is **`1. d2–c3`**, and Black **`b4:d2`**? No, `b4` needs to stay empty for landing! What if Step 1 is **`1. d2–e3`**, and Black **`c5`? No, `d4:f2`? No, put Black on `f4`... wait `f4` MUST jump to `d2` -> so `f4` leaves `f4`!**
      //   Then don't stop at `g5` — make Step 1 create `d2` via **`f4`? No, `b4` vacates**, OR simply accept a **1-step or 2-step 3-target diamond (`e1 -> b4 -> e7 -> h4`)**? NO, wait — what if on Leg 4 (`g5 -> ...`), target is **`e3`? No, `d4`?** From `g5(3,6)`: `r+c=9` (`g5(3,6), f4(4,5), e3(5,4), d2(6,3), c1(7,2)`). Notice `e7(1,4)->g5(3,6)` is `c-r = 3` (`f6(2,5)` has `5-2=3`). Turning 90° at `g5(3,6)` goes down `r+c=9` (`f4`, `e3`, `d2`, `c1`). Turning 90° at `h4(4,7)` goes down `r+c=11` (`g3`, `f2`, `e1`).
      //   Look at **Leg 1..4 REVERSE**: Start at **`h4(4,7)`** instead of `e1(7,4)`!!
      //   Let `1. g3` (`f2–g3`?):
      //   Wait: White King **`e1(7,4)`**? No, White King **`h4(4,7)`**! Wait before Step 1, **`f2` is White (`w`)**? Wait `f2` doesn't block `e7`. Put White King at **`h4(4,7)`**? Still if `f6` is empty on Move 1, put **`f6`** created by `1. g5–f6` (`e7:g5` still moves `e7`!).
      //   Wait: What if **`h4`** is NOT on `e1-h4` — White King starts at **`e1(7,4)`** and initially **`c5(3,2)` is empty**, `b6(2,1)`? Or keep White King at **`c1(7,2)`**, Step 1 `1. f2–g3` (`h4:f2`), Step 2: `c1(7,2) -> e3(5,4)` [no `f2`]? Wait `c1(7,2) -> g5(3,6)` [over `f4`? no, `e3`!] -> ...
      //   Check **`e1(7,4)`** with `3` targets (`d2` from `f4:d2`, `c5`, `f6`) -> **`e1:b4:e7:h4` (OR `g5`)**!
      //   Wait on `e1:b4:e7:g5` (3 pieces captured: `d2, c5, f6`, no `g3` blocking Move 1!), check Move 1 BEFORE `1. d2–e3`:
      //   White: `e1(W, 7,4)`, `d2(w, 6,3)`.
      //   Black: `f4(b, 4,5)`, `c5(b, 3,2)`, `f6(b, 2,5)`.
      //   ANY captures before Move 1? `e1` sees `f2, g3, h4` CLEAR, `d2('w')` blocks `c5` (`d2` and `e1` adjacent)! `d2(6,3)` sees `e3, c3` CLEAR!
      //   Wait add a 4th Black target on **`g7`** or **`h4`**? No, add a 4th target at **`f2(6,5)`**? Wait no, then `e1` captures `f2` on Move 1! Add a 4th target from **`g5`**? Can't! What if from **`e7(1,4)`** -> over `d8?` no -> over `c5` is backwards -> over `f6` to **`g5`? If `g5` turns onto `f4`, `f4` already jumped!**
      //   WAIT FROM `h4(4,7)` (`e7:h4` over `f6`), can it turn onto `g3`? Only if `g3` wasn't jumped from `e1`. And WHY was `e1` blocked? No, start White King at **`a5(3,0)`**? No, **`d2`** blocks `e1`. What if `b4` -> `e7` -> `h4` AND `b4` wasn't `e1`, it was `a3`?
      //   Actually look at `e1(7,4)`: `e1` is ALSO blocked on `c3-h8`? No. Look at `1. d2–e3`, `1... f4:d2`, `2. e1:b4:e7:h4` (OR `g5`) — AND put a 4th Black at **`h2`?** (`(6,7)` from `g5` no). Put a 4th Black at **`a3`?** (`b4->a3` no). A clean 3-capture King hunt after blocking sacrifice (`e1:b4:e7:g5` / `h4`) is ALREADY awesome! Wait can we add a 4th capture to `e1:b4:e7:h4`? After `h4`? No, on `e7(1,4)->h4(4,7)`, if you put Black at **`e5(3,4)`**? No. Put Black at **`d6`**? No. Put Black at **`g7`?** If `f6` is `f6(2,5)`: no need, 3-piece capture by King AFTER quiet blocking removal `d2–e3` (`e1:b4:e7:g5` / `e1:b4:e7:h4`) is super slick! Wait what if `d2` vacates so `e1` captures `d2` (just jumped onto `d2`!) -> `b4` -> over `c5` -> `e7` -> over `f6` -> `g5`/`h4`!
      //   Check `b4(4,1)->c5(3,2)->d6(2,3)->e7(1,4)->f8(0,5)`: could `e1` land `d6(2,3)` instead of `e7(1,4)`? Neither `d6` nor `f8` has a continuation, ONLY `e7(1,4)` continues over `f6(2,5)`!!
      //   Check `e1(7,4)->d2(6,3)->c3(5,2)->b4(4,1)->a5(3,0)`: could `e1` land `c3(5,2)` or `a5(3,0)`? Neither continues, ONLY `b4(4,1)` continues over `c5(3,2)`!!
      //   Therefore `e1:b4:e7:g5` and `e1:b4:e7:h4` are the ONLY legal captures on Step 2!
      LogicPuzzle(
        id = "puzzle_48_master_diamond_loop",
        order = 48,
        title = "Отскок заслона e1",
        themeTag = "Комбинация с дамкой",
        difficulty = PuzzleDifficulty.MASTER,
        prompt = "Своя шашка d2 закрывает дорогу Дамке e1. Убери её с жертвой так, чтобы чёрная встала ровно на d2 и открыла тройной удар через всю доску!",
        initialRows = listOf(
          "........",
          "........",
          ".....b..",
          "..b.....",
          ".....b..",
          "........",
          "...w....",
          "....W...",
        ),
        steps = listOf(
          PuzzleStep(
            acceptedNotations = setOf("d2–e3"),
            hintMove = CheckersMove.simple("d2", "e3"),
            hintText = "Шаг 1: убери заслон ходом d2–e3! Чёрная f4 прыгнет на освободившееся поле d2!",
            botAutoReply = CheckersMove.captureChain(Triple("f4", "d2", "e3")),
            replyCaption = "Заслон убран, а на d2 стоит мишень! Веди Дамку e1 через три фигуры: b4 → e7 → g5 (или h4)!",
          ),
          PuzzleStep(
            acceptedNotations = setOf("e1:b4:e7:g5", "e1:b4:e7:h4"),
            hintMove = CheckersMove.captureChain(
              Triple("e1", "b4", "d2"),
              Triple("b4", "e7", "c5"),
              Triple("e7", "h4", "f6"),
            ),
            hintText = "Шаг 2: лети Дамкой e1 → b4 → e7 → g5 (или h4)!",
          ),
        ),
        successExplanation = "Идеальный тактический отскок d2–e3! Дамка e1 смела 3 цели точными поворотами!",
      )
    },

    LogicPuzzle(
      id = "puzzle_49_master_quiet_double_sacrifice",
      order = 49,
      title = "Атака двумя жертвами на f8",
      themeTag = "3-ходовая жертва",
      difficulty = PuzzleDifficulty.MASTER,
      prompt = "Ещё одна 3-ходовая задачка Гроссмейстера: отдай две шашки по очереди и пробей сквозной прорыв до Дамки f8!",
      initialRows = listOf(
        "........",
        "....b...",
        "........",
        "..b.b...",
        "........",
        "....w...",
        "...w....",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("e3–d4"),
          hintMove = CheckersMove.simple("e3", "d4"),
          hintText = "Verify distinct clean 3-step leading to f8!",
        ),
      ),
      successExplanation = "",
    ).let {
      // Fresh 3-step MASTER #49 leading to `f8(0,5)` (`3. d2:f4:d6:f8` over `e3, e5, e7`)!
      // Just like #44 had `g5-f6 (e7:g5)` + `e3-f4 (g5:e3)` -> let's invert the direction via `c5` -> `a5` -> `c3` -> no, we did `a5-b6 (c7:a5)` -> `c3-b4 (a5:c3)` -> `b2:d4:f6:h8` in #45!
      // For #49, let's do **f2–g3?!**:
      // Target Step 3: `3. d2:f4:d6:f8` (over `e3, e5, e7`)!
      // Step 2: `2. e3–d4` -> Black `c5:e3` (blocked `e5:c3` by White `'w'` on `c3`!! Wait `c3` is empty for `d2`? Yes, `d2->e3->f4`, `c3` isn't on path!).
      // Step 1: how does Black arrive on `c5(3,2)`?!
      // Put White at `a5(3,0='w')`? No, put White at `b6` (`c7` -> `b6` -> `a5`). From `a7(1,0)` over `b6` to `c5`!!
      // Check Step 1: `1. a5–b6`? No, **`a5` is `(3,0)`** -> `a5–b6` makes `a7(1,0)` jump `b6(2,1)` -> `c5(3,2)`!
      // Wait does `c3(5,2)` touch `b6`? No! Check `c7(1,2)`? Empty! Put Black on `a7(1,0)`!
      // Trace ALL pieces for #49:
      // Initial White: `d2(6,3)`, `c3(5,2)`, `e3(5,4)`, `a5(3,0)`.
      // Initial Black: `a7(1,0)`, `e5(3,4)`, `e7(1,4)`.
      // Check Step 0: ANY White captures?
      // - `a5(3,0)` touches `b6` empty, `b4` empty. NO!
      // - `c3(5,2)` touches `b4, d4` empty. NO!
      // - `e3(5,4)` touches `d4, f4` empty. NO!
      // - `d2(6,3)` blocked by `c3, e3`. NO!
      // Step 1: White `1. a5–b6`! (`a5` vacates, `b6` occupied by White).
      //   Black to move: `a7(1,0)` -> `b6(2,1='w')` -> `c5(3,2=EMPTY!)` => `1... a7:c5` UNIQUE FORCED!!
      //   (Check `e5, e7`: empty neighbors? `d4` empty, `f6` empty! NO other capture!)
      // Step 2: White `2. e3–d4`! (`e3` vacates, `d4` occupied by White). Any White capture BEFORE `2. e3–d4`? None!
      //   Black to move:
      //   - `c5(3,2)` -> `d4(4,3='w')` -> `e3(5,4=EMPTY!)` => `2... c5:e3` LEGAL!
      //   - `e5(3,4)` -> `d4(4,3='w')` -> `c3(5,2='w' BLOCKED!)` => Illegal!
      //   So `2... c5:e3` is UNIQUE FORCED!
      // Step 3: White `3. d2:f4:d6:f8` (`d2->f4[e3] -> d6[e5] -> f8[e7]`, King on `f8`!)!!
      //   (Check `c3`: empty `b4, d4` -> no other capture!)
      LogicPuzzle(
        id = "puzzle_49_master_quiet_double_sacrifice",
        order = 49,
        title = "Маршрут через весь борт на f8",
        themeTag = "3-ходовая жертва",
        difficulty = PuzzleDifficulty.MASTER,
        prompt = "Мастерская 3-ходовая комбинация: двумя жертвами приведи крайнюю шашку a7 прямо на e3 и прорвись в Дамки на f8!",
        initialRows = listOf(
          "........",
          "b...b...",
          "........",
          "w...b...",
          "........",
          "..w.w...",
          "...w....",
          "..w.....",
        ),
        steps = listOf(
          PuzzleStep(
            acceptedNotations = setOf("a5–b6"),
            hintMove = CheckersMove.simple("a5", "b6"),
            hintText = "Шаг 1: пожертвуй крайнюю a5–b6! Чёрная a7 прыгнет в центр на c5.",
            botAutoReply = CheckersMove.captureChain(Triple("a7", "c5", "b6")),
            replyCaption = "Чёрная встала на c5! Теперь подставь шашку e3–d4 под прикрытием c3!",
          ),
          PuzzleStep(
            acceptedNotations = setOf("e3–d4"),
            hintMove = CheckersMove.simple("e3", "d4"),
            hintText = "Шаг 2: шагни e3–d4! Шашка c3 не пропустит e5, и c5 встанет на e3!",
            botAutoReply = CheckersMove.captureChain(Triple("c5", "e3", "d4")),
            replyCaption = "Цепочка выстроилась! Пробивай тройной удар d2 → f4 → d6 → f8 (Дамка)!",
          ),
          PuzzleStep(
            acceptedNotations = setOf("d2:f4:d6:f8"),
            hintMove = CheckersMove.captureChain(
              Triple("d2", "f4", "e3"),
              Triple("f4", "d6", "e5"),
              Triple("d6", "f8", "e7"),
              promotesFlags = listOf(false, false, true),
            ),
            hintText = "Шаг 3: бей d2 → f4 → d6 → f8 (Дамка)!",
          ),
        ),
        successExplanation = "Искусный расчёт на 3 хода! Две жертвы вывели шашку a7 на e3 прямо под прорыв в Дамки!",
      )
    },

    LogicPuzzle(
      id = "puzzle_50_master_grand_finale",
      order = 50,
      title = "Финал Гроссмейстера",
      themeTag = "Суперэтюд 3 хода",
      difficulty = PuzzleDifficulty.MASTER,
      prompt = "Юбилейная 50-я задача! В 3 хода проведи жертву, заставь вражескую дамку остановить полёт и сметай всё своей Дамкой с превращением!",
      initialRows = listOf(
        "........",
        "........",
        "........",
        "........",
        "........",
        "........",
        "........",
        "........",
      ),
      steps = listOf(
        PuzzleStep(
          acceptedNotations = setOf("a1–b2"),
          hintMove = CheckersMove.simple("a1", "b2"),
          hintText = "Design #50 cleanly!",
        ),
      ),
      successExplanation = "",
    ).let {
      // Grand 50th Puzzle: 3-step masterpiece combining pawn sacrifice, backward-ish / King promotion in flight (`b6:d8:h4` -> wait `b6:d8:g5:e3:c1`)!!
      // Look! Build a 3-step setup leading into 4-piece promotion-in-flight `b6:d8:g5:e3` OR `c5–d6 -> e5:c7 -> b6:d8:g5:e3:c1` (adding Black `d2`!!)!
      // Even better: 3-step where Step 1 places `c5` (via `b4–c5`), OR Step 1 is `1. g3–f4` forcing `e5:g3` no —
      // Combine a **4-piece mid-jump promotion chain** with a **3-step combo**:
      // Look at `b2:d4:b6:d8:g5:e3`: `b2(6,1)` -> `d4` [over `c3`] -> `b6` [over `c5`] -> `d8(0,3 King!)` [over `c7`] -> `g5(3,6)` [over `f6`] -> `e3(5,4)`? Wait from `g5(3,6)` over `f4(4,5)` to `e3(5,4)` or `d2(6,3)` or `c1(7,2)`!!
      // Can we force `c3` AND `f6` or `c3` AND `g5`? YES! Look at #37 (`b2:d4:b6:d8:g5`) + add Black `f4(4,5)` -> 5 pieces! Wait on #37, `e5:c3` jumped FROM `e5(3,4)`! If `f4(4,5)` was Black initially, then on Step 1 `e5(3,4)` wouldn't see `c3`, `f4` would still sit at `f4` and `e5:c3` jumps `d4`! Wait does `e3(5,4='w')` jump `f4(4,5='b')` BEFORE Move 1? YES (`g5` empty)! Unless Black is at `g5`... No, put White at `g5(3,6)`!
      // Instead start with **`1. a5–b6`** (`c7:a5`? No!), start with **`1. f4–g5`** (`h6:f4`) then **`2. c3–d4`** (`e5:c3`) -> **`3. b2:d4:b6:d8:g5`?!** Wait after `2. c3–d4`, `e5:c3` happens cleanly, and before `2. c3–d4`, `1. f4–g5` (`h6:f4`) puts Black at `f4(4,5)`!!
      // WAIT A MINUTE!! CHECK THAT 5-PIECE GRAND FINALE RIGHT NOW!!
      // Initial White: `b2(6,1)`, `c3(5,2)`, `e3(5,4)`, `f4(4,5)`.
      // Initial Black: `h6(2,7)`, `f6(2,5)`, `c7(1,2)`, `c5(3,2)`, `e5(3,4)`.
      // Check Step 0 BEFORE `1. f4–g5`:
      // - `f4(4,5)` touches `e5('b')`: behind `e5(3,4)` is `d6` empty! OH `f4` would capture `e5` immediately!!
      // Flip `h6` -> use `1. g5–f6` (`e7:g5`? No, need `f4` occupied by Black!). To put Black at `f4(4,5)` WITHOUT `f4` starting next to `e5`: put White at `g3` -> no, put Black at `d6(2,3)` jumping `e5`? No! Put White at `e5` no!
      // What if `d8`->`g5` skips `f4` and hits `d2(6,3='b')` (`e3` empty? Wait we needed `e3='w'`)?
      // Better: put **Step 1 `1. a5–b6` (`c7:a5`), NO**: just use a clean **3-step 4-piece combo WITH promotion to King**:
      // Target Step 3: `3. d2:f4:d6:f8:h6` (4 captures: `e3, e5, e7, g7`, becomes King on `f8`, lands `h6`!)
      // Look how clean Step 1 + Step 2 setup for `3. d2:f4:d6:f8:h6` is:
      // - Need `e3` from Step 2 (`2. e3–d4` -> `2... c5:e3`, blocked `e5:c3` by `c3='w'`)!
      // - Need `c5` from Step 1 (`1. a5–b6` -> `1... a7:c5`)!
      // CHECK THAT ENTIRE 50TH PUZZLE:
      // Initial White: `d2(6,3)`, `c3(5,2)`, `e3(5,4)`, `a5(3,0)`.
      // Initial Black: `a7(1,0)`, `e5(3,4)`, `e7(1,4)`, `g7(1,6)`!
      // Step 0: Any White captures? `a5` sees `b6` empty; `c3, e3` see `b4,d4,f4` empty; `d2` blocked. NONE!
      // Step 1: White `1. a5–b6` -> Black `1... a7:c5` UNIQUE FORCED!
      // Step 2: White `2. e3–d4` -> Black `2... c5:e3` (`e5:c3` blocked by `c3='w'`) UNIQUE FORCED!
      // Step 3: White `3. d2:f4:d6:f8:h6` (`d2` -> `f4` [`e3`] -> `d6` [`e5`] -> `f8 King!` [`e7`] -> `h6` [`g7`])!!
      // 3 MOVES, 2 FORCED DECOYS, 4 CAPTURES WITH MID-JUMP KING PROMOTION ON THE LAST MOVE!! True #50 Finale!
      LogicPuzzle(
        id = "puzzle_50_master_grand_finale",
        order = 50,
        title = "Корона Гроссмейстера (Финал)",
        themeTag = "3-ходовый шедевр",
        difficulty = PuzzleDifficulty.MASTER,
        prompt = "Главная 50-я задача! В 3 хода замани шашку a7 на e3, прорвись на лету в Дамки на f8 и сруби 4 шашки с разворотом на h6!",
        initialRows = listOf(
          "........",
          "b...b.b.",
          "........",
          "w...b...",
          "........",
          "..w.w...",
          "...w....",
          "..w.....",
        ),
        steps = listOf(
          PuzzleStep(
            acceptedNotations = setOf("a5–b6"),
            hintMove = CheckersMove.simple("a5", "b6"),
            hintText = "Шаг 1: пожертвуй a5–b6! Чёрная шашка a7 попадёт на c5.",
            botAutoReply = CheckersMove.captureChain(Triple("a7", "c5", "b6")),
            replyCaption = "Отлично! Второй шаг — подставь e3–d4, чтобы c5 оказалась на e3!",
          ),
          PuzzleStep(
            acceptedNotations = setOf("e3–d4"),
            hintMove = CheckersMove.simple("e3", "d4"),
            hintText = "Шаг 2: шагни e3–d4! Шашка c3 закроет обратный бой для e5, и c5 прыгнет на e3!",
            botAutoReply = CheckersMove.captureChain(Triple("c5", "e3", "d4")),
            replyCaption = "Все 4 цели на линии! Прыгай d2 → f4 → d6 → f8 (Дамка!) → h6!",
          ),
          PuzzleStep(
            acceptedNotations = setOf("d2:f4:d6:f8:h6"),
            hintMove = CheckersMove.captureChain(
              Triple("d2", "f4", "e3"),
              Triple("f4", "d6", "e5"),
              Triple("d6", "f8", "e7"),
              Triple("f8", "h6", "g7"),
              promotesFlags = listOf(false, false, true, false),
            ),
            hintText = "Шаг 3: бей d2 → f4 → d6 → f8 (Дамка) → h6!",
          ),
        ),
        successExplanation = "БРАВО! Все 50 задач пройдены! 4 шашки срублены с мгновенным превращением на f8!",
      )
    },
  )
}
