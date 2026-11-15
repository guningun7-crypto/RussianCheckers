package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.checkers.CheckersBoard
import com.example.domain.checkers.CheckersMove
import com.example.domain.checkers.Piece
import com.example.domain.checkers.PieceColor
import com.example.domain.checkers.Square
import com.example.ui.theme.BlackPieceFill
import com.example.ui.theme.BlackPieceRim
import com.example.ui.theme.BoardDarkSquare
import com.example.ui.theme.BoardFrameHighlight
import com.example.ui.theme.BoardFrameWood
import com.example.ui.theme.BoardLightSquare
import com.example.ui.theme.CrownGold
import com.example.ui.theme.ForestEmerald
import com.example.ui.theme.HintGlowGold
import com.example.ui.theme.TerracottaOrange
import com.example.ui.theme.WhitePieceFill
import com.example.ui.theme.WhitePieceRim
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun CheckersBoardView(
  board: CheckersBoard,
  modifier: Modifier = Modifier,
  selectedSquare: Square? = null,
  targetSquares: Set<Square> = emptySet(),
  mandatoryCaptureSources: Set<Square> = emptySet(),
  highlightedSquares: Set<Square> = emptySet(),
  lastMove: CheckersMove? = null,
  animatedHint: CheckersMove? = null,
  showCelebration: Boolean = false,
  interactive: Boolean = true,
  flipped: Boolean = false,
  onSquareClick: (Square) -> Unit = {},
) {
  val hasPulse = targetSquares.isNotEmpty() ||
    mandatoryCaptureSources.isNotEmpty() ||
    highlightedSquares.isNotEmpty() ||
    animatedHint != null

  val pulseProgress = if (hasPulse) {
    val transition = rememberInfiniteTransition(label = "boardPulse")
    val anim by transition.animateFloat(
      initialValue = 0f,
      targetValue = 1f,
      animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Reverse,
      ),
      label = "pulse",
    )
    anim
  } else {
    0f
  }

  val hintTraveller = if (animatedHint != null) {
    val transition = rememberInfiniteTransition(label = "boardHintTravel")
    val anim by transition.animateFloat(
      initialValue = 0f,
      targetValue = 1f,
      animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 1500, easing = LinearEasing),
        repeatMode = RepeatMode.Restart,
      ),
      label = "hintTraveller",
    )
    anim
  } else {
    0f
  }

  val celebrationAlpha by animateFloatAsState(
    targetValue = if (showCelebration) 1f else 0f,
    animationSpec = spring(),
    label = "celebrationAlpha",
  )

  BoxWithConstraints(
    modifier = modifier
      .widthIn(max = 520.dp)
      .fillMaxWidth()
      .aspectRatio(1f)
      .shadow(10.dp, RoundedCornerShape(22.dp))
      .clip(RoundedCornerShape(22.dp))
      .background(
        Brush.linearGradient(
          colors = listOf(BoardFrameHighlight, BoardFrameWood)
        )
      )
      .padding(12.dp),
    contentAlignment = Alignment.Center,
  ) {
    val labelThickness = 18.dp
    val density = LocalDensity.current
    var gridSizePx by remember { mutableStateOf(IntSize.Zero) }

    Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      // Top file labels
      FileLabelsRow(
        flipped = flipped,
        modifier = Modifier
          .fillMaxWidth()
          .height(labelThickness)
          .padding(horizontal = labelThickness),
      )

      Row(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        // Left rank labels
        RankLabelsColumn(
          flipped = flipped,
          modifier = Modifier
            .width(labelThickness)
            .fillMaxHeight(),
        )

        // Centered square playable container with border outside the 8x8 inner box
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
          contentAlignment = Alignment.Center,
        ) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .aspectRatio(1f)
              .clip(RoundedCornerShape(8.dp))
              .border(2.dp, Color(0xFF3A2218), RoundedCornerShape(8.dp))
              .padding(2.dp),
          ) {
            // Exact borderless 8x8 inner coordinate grid shared by squares, pieces, and hints
            Box(
              modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { gridSizePx = it },
            ) {
              // 1) Draw all 64 squares + cell overlays in a single pixel-accurate Canvas
              Canvas(modifier = Modifier.fillMaxSize()) {
                val cellW = size.width / 8f
                val cellH = size.height / 8f
                val rBase = minOf(cellW, cellH) / 2f

                for (vr in 0..7) {
                  for (vc in 0..7) {
                    val r = if (flipped) 7 - vr else vr
                    val c = if (flipped) 7 - vc else vc
                    val sq = Square(r, c)
                    val isDark = sq.isPlayable
                    val isSelected = selectedSquare == sq
                    val isTarget = sq in targetSquares
                    val isMandatorySource = sq in mandatoryCaptureSources
                    val isHighlighted = sq in highlightedSquares
                    val isLastMoveEnd = lastMove != null && (lastMove.from == sq || lastMove.to == sq)
                    val isHintEnd = animatedHint != null && (animatedHint.from == sq || animatedHint.to == sq)

                    val squareBg = when {
                      !isDark -> BoardLightSquare
                      isSelected -> Color(0xFF6B8E4E)
                      isLastMoveEnd -> Color(0xFFA06B43)
                      else -> BoardDarkSquare
                    }

                    val left = vc * cellW
                    val top = vr * cellH
                    drawRect(
                      color = squareBg,
                      topLeft = Offset(left, top),
                      size = Size(cellW + 0.75f, cellH + 0.75f),
                    )

                    if (isDark) {
                      val center = Offset(left + cellW / 2f, top + cellH / 2f)

                      if (isMandatorySource && !isSelected) {
                        drawCircle(
                          color = TerracottaOrange.copy(alpha = 0.55f + 0.35f * pulseProgress),
                          radius = rBase * (0.82f + 0.08f * pulseProgress),
                          center = center,
                          style = Stroke(width = minOf(cellW, cellH) * 0.08f),
                        )
                      }

                      if (isHighlighted) {
                        drawCircle(
                          color = HintGlowGold.copy(alpha = 0.45f + 0.35f * pulseProgress),
                          radius = rBase * 0.78f,
                          center = center,
                          style = Stroke(width = minOf(cellW, cellH) * 0.09f),
                        )
                      }

                      if (isHintEnd) {
                        drawCircle(
                          color = HintGlowGold.copy(alpha = 0.30f + 0.35f * pulseProgress),
                          radius = rBase * 0.88f,
                          center = center,
                        )
                      }

                      if (isTarget && board[sq] == null) {
                        drawCircle(
                          color = ForestEmerald.copy(alpha = 0.88f),
                          radius = rBase * (0.30f + 0.06f * pulseProgress),
                          center = center,
                        )
                        drawCircle(
                          color = Color.White.copy(alpha = 0.75f),
                          radius = rBase * 0.14f,
                          center = center,
                        )
                      }
                    }
                  }
                }
              }

              // 2) Transparent tap/semantics grid aligned identically with the 8x8 Canvas
              Column(modifier = Modifier.fillMaxSize()) {
                for (vr in 0..7) {
                  Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    for (vc in 0..7) {
                      val r = if (flipped) 7 - vr else vr
                      val c = if (flipped) 7 - vc else vc
                      val sq = Square(r, c)
                      val isDark = sq.isPlayable
                      Box(
                        modifier = Modifier
                          .weight(1f)
                          .fillMaxHeight()
                          .testTag("board_square_${sq.notation}")
                          .semantics {
                            contentDescription = "Клетка ${sq.notation}"
                          }
                          .then(
                            if (interactive && isDark) {
                              Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                role = Role.Button,
                                onClick = { onSquareClick(sq) },
                              )
                            } else {
                              Modifier
                            }
                          ),
                      )
                    }
                  }
                }
              }

              // 3) Checkers Pieces placed dead-center using exact measured cell pixel bounds
              if (gridSizePx.width > 0 && gridSizePx.height > 0) {
                val cellW = gridSizePx.width / 8f
                val cellH = gridSizePx.height / 8f

                for (r in 0..7) {
                  for (c in 0..7) {
                    val sq = Square(r, c)
                    val piece = board[sq] ?: continue
                    val isSelected = selectedSquare == sq
                    val scaleBoost by animateFloatAsState(
                      targetValue = if (isSelected) 1.12f else 1.0f,
                      animationSpec = spring(dampingRatio = 0.55f, stiffness = 420f),
                      label = "pieceScale_${r}_$c",
                    )

                    val visualRow = if (flipped) 7 - r else r
                    val visualCol = if (flipped) 7 - c else c
                    val x0 = (visualCol * cellW).roundToInt()
                    val y0 = (visualRow * cellH).roundToInt()
                    val x1 = ((visualCol + 1) * cellW).roundToInt()
                    val y1 = ((visualRow + 1) * cellH).roundToInt()
                    val cellWidthDp = with(density) { (x1 - x0).coerceAtLeast(1).toDp() }
                    val cellHeightDp = with(density) { (y1 - y0).coerceAtLeast(1).toDp() }

                    Box(
                      modifier = Modifier
                        .offset { IntOffset(x = x0, y = y0) }
                        .size(width = cellWidthDp, height = cellHeightDp),
                      contentAlignment = Alignment.Center,
                    ) {
                      CheckerPieceCanvas(
                        piece = piece,
                        scale = scaleBoost,
                        isSelected = isSelected,
                        modifier = Modifier.fillMaxSize(0.80f),
                      )
                    }
                  }
                }
              }

              // 4) Animated Hint Overlay (Golden dashed path + arrowheads + travelling spark)
              if (animatedHint != null) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                  drawAnimatedHintOverlay(
                    move = animatedHint,
                    cellSizePx = size.width / 8f,
                    pulse = pulseProgress,
                    traveller = hintTraveller,
                    flipped = flipped,
                  )
                }
              }

              // 5) Celebration Sparkle Burst on solving lesson/puzzle
              if (celebrationAlpha > 0.02f) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                  drawCelebrationBurst(
                    alpha = celebrationAlpha,
                    phase = pulseProgress,
                  )
                }
              }
            }
          }
        }

        // Right rank labels
        RankLabelsColumn(
          flipped = flipped,
          modifier = Modifier
            .width(labelThickness)
            .fillMaxHeight(),
        )
      }

      // Bottom file labels
      FileLabelsRow(
        flipped = flipped,
        modifier = Modifier
          .fillMaxWidth()
          .height(labelThickness)
          .padding(horizontal = labelThickness),
      )
    }
  }
}

@Composable
private fun FileLabelsRow(
  flipped: Boolean,
  modifier: Modifier = Modifier,
) {
  val files = if (flipped) ('H' downTo 'A') else ('A'..'H')
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    files.forEach { ch ->
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight(),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = ch.toString(),
          fontSize = 10.sp,
          lineHeight = 11.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFFF3DEC0),
        )
      }
    }
  }
}

@Composable
private fun RankLabelsColumn(
  flipped: Boolean,
  modifier: Modifier = Modifier,
) {
  val ranks = if (flipped) (1..8) else (8 downTo 1)
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    ranks.forEach { rank ->
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth(),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = rank.toString(),
          fontSize = 10.sp,
          lineHeight = 11.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFFF3DEC0),
        )
      }
    }
  }
}

@Composable
fun CheckerPieceCanvas(
  piece: Piece,
  scale: Float = 1f,
  isSelected: Boolean = false,
  modifier: Modifier = Modifier,
) {
  Canvas(modifier = modifier) {
    val radius = (size.minDimension / 2f) * scale.coerceAtMost(1.16f)
    val center = Offset(size.width / 2f, size.height / 2f)

    // Soft drop shadow
    drawCircle(
      color = Color.Black.copy(alpha = 0.28f),
      radius = radius,
      center = center + Offset(0f, size.minDimension * 0.04f),
    )

    if (isSelected) {
      drawCircle(
        color = HintGlowGold.copy(alpha = 0.55f),
        radius = radius * 1.13f,
        center = center,
      )
    }

    val fill = if (piece.color == PieceColor.WHITE) WhitePieceFill else BlackPieceFill
    val rim = if (piece.color == PieceColor.WHITE) WhitePieceRim else BlackPieceRim
    val ridge = if (piece.color == PieceColor.WHITE) Color(0xFFE7D3B0) else Color(0xFF473732)

    // Outer wooden rim
    drawCircle(
      color = rim,
      radius = radius,
      center = center,
    )

    // Main body
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          fill,
          if (piece.color == PieceColor.WHITE) Color(0xFFF2E2C4) else Color(0xFF1F1714),
        ),
        center = center - Offset(radius * 0.25f, radius * 0.25f),
        radius = radius * 1.15f,
      ),
      radius = radius * 0.88f,
      center = center,
    )

    // Concentric carved checker ridges
    drawCircle(
      color = ridge,
      radius = radius * 0.68f,
      center = center,
      style = Stroke(width = radius * 0.08f),
    )
    drawCircle(
      color = ridge.copy(alpha = 0.7f),
      radius = radius * 0.44f,
      center = center,
      style = Stroke(width = radius * 0.06f),
    )

    // King Golden Crown ("Дамка")
    if (piece.isKing) {
      drawCrownEmblem(center = center, radius = radius * 0.50f)
    }
  }
}

private fun DrawScope.drawCrownEmblem(center: Offset, radius: Float) {
  val crownPath = Path().apply {
    val left = center.x - radius * 0.85f
    val right = center.x + radius * 0.85f
    val bottom = center.y + radius * 0.55f
    val top = center.y - radius * 0.60f
    val midY = center.y + radius * 0.05f
    moveTo(left, bottom)
    lineTo(left - radius * 0.10f, top + radius * 0.25f)
    lineTo(center.x - radius * 0.38f, midY)
    lineTo(center.x, top)
    lineTo(center.x + radius * 0.38f, midY)
    lineTo(right + radius * 0.10f, top + radius * 0.25f)
    lineTo(right, bottom)
    close()
  }
  drawPath(
    path = crownPath,
    color = CrownGold,
  )
  drawPath(
    path = crownPath,
    color = Color(0xFF8B5E00),
    style = Stroke(width = radius * 0.14f, join = StrokeJoin.Round),
  )
}

private fun DrawScope.drawAnimatedHintOverlay(
  move: CheckersMove,
  cellSizePx: Float,
  pulse: Float,
  traveller: Float,
  flipped: Boolean = false,
) {
  val points = listOf(move.from) + move.steps.map { it.to }
  val centers = points.map { sq ->
    val vc = if (flipped) 7 - sq.col else sq.col
    val vr = if (flipped) 7 - sq.row else sq.row
    Offset(
      x = (vc + 0.5f) * cellSizePx,
      y = (vr + 0.5f) * cellSizePx,
    )
  }

  for (i in 0 until centers.lastIndex) {
    val start = centers[i]
    val end = centers[i + 1]

    // Glowing under-line
    drawLine(
      color = HintGlowGold.copy(alpha = 0.35f + 0.25f * pulse),
      start = start,
      end = end,
      strokeWidth = cellSizePx * 0.20f,
      cap = StrokeCap.Round,
    )

    // Dashed golden path
    drawLine(
      color = Color(0xFFFFE066),
      start = start,
      end = end,
      strokeWidth = cellSizePx * 0.10f,
      cap = StrokeCap.Round,
      pathEffect = PathEffect.dashPathEffect(floatArrayOf(cellSizePx * 0.22f, cellSizePx * 0.14f), phase = -traveller * cellSizePx),
    )

    // Arrowhead at segment end
    val angle = atan2(end.y - start.y, end.x - start.x)
    val headLen = cellSizePx * 0.28f
    val leftWing = Offset(
      x = end.x - headLen * cos(angle - 0.55f),
      y = end.y - headLen * sin(angle - 0.55f),
    )
    val rightWing = Offset(
      x = end.x - headLen * cos(angle + 0.55f),
      y = end.y - headLen * sin(angle + 0.55f),
    )
    drawLine(
      color = Color(0xFFFFD54F),
      start = end,
      end = leftWing,
      strokeWidth = cellSizePx * 0.11f,
      cap = StrokeCap.Round,
    )
    drawLine(
      color = Color(0xFFFFD54F),
      start = end,
      end = rightWing,
      strokeWidth = cellSizePx * 0.11f,
      cap = StrokeCap.Round,
    )
  }

  // Animated spark traveling along all segments
  if (centers.size >= 2) {
    val totalSegments = centers.size - 1
    val scaled = traveller * totalSegments
    val segIndex = scaled.toInt().coerceIn(0, totalSegments - 1)
    val localT = (scaled - segIndex).coerceIn(0f, 1f)
    val p0 = centers[segIndex]
    val p1 = centers[segIndex + 1]
    val sparkPos = Offset(
      x = p0.x + (p1.x - p0.x) * localT,
      y = p0.y + (p1.y - p0.y) * localT,
    )
    drawCircle(
      color = Color.White.copy(alpha = 0.9f),
      radius = cellSizePx * 0.18f,
      center = sparkPos,
    )
    drawCircle(
      color = HintGlowGold,
      radius = cellSizePx * 0.11f,
      center = sparkPos,
    )
  }
}

private fun DrawScope.drawCelebrationBurst(alpha: Float, phase: Float) {
  val colors = listOf(CrownGold, ForestEmerald, TerracottaOrange, Color(0xFF42A5F5))
  val center = Offset(size.width / 2f, size.height / 2f)
  val count = 16
  for (i in 0 until count) {
    val angle = (i.toFloat() / count) * (Math.PI * 2.0).toFloat()
    val dist = size.minDimension * (0.18f + 0.28f * phase)
    val pos = Offset(
      x = center.x + cos(angle) * dist,
      y = center.y + sin(angle) * dist,
    )
    drawCircle(
      color = colors[i % colors.size].copy(alpha = alpha * 0.8f),
      radius = size.minDimension * (0.022f + 0.01f * (i % 3)),
      center = pos,
    )
  }
}
