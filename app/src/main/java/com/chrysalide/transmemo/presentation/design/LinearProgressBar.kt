package com.chrysalide.transmemo.presentation.design

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinearProgressBar(
    progress: () -> Float,
    color: Color = ProgressIndicatorDefaults.linearColor,
    trackColor: Color = ProgressIndicatorDefaults.linearTrackColor.copy(alpha = 0.5f),
    strokeCap: StrokeCap = StrokeCap.Butt,
    gapSize: Dp = 8.dp,
    height: Dp
) {
    val coercedProgress = { progress().coerceIn(0f, 1f) }
    Canvas(
        Modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val strokeWidth = size.height
        val adjustedGapSize =
            if (strokeCap == StrokeCap.Butt || size.height > size.width) {
                gapSize
            } else {
                gapSize + strokeWidth.toDp()
            }
        val gapSizeFraction = adjustedGapSize / size.width.toDp()
        val currentCoercedProgress = coercedProgress()

        // track
        val trackStartFraction =
            currentCoercedProgress + min(currentCoercedProgress, gapSizeFraction)
        if (trackStartFraction <= 1f) {
            drawLinearIndicator(trackStartFraction, 1f, trackColor, strokeWidth, strokeCap)
        }
        drawLinearIndicator(0f, currentCoercedProgress, color, strokeWidth, strokeCap)
    }
}

private fun DrawScope.drawLinearIndicator(
    startFraction: Float,
    endFraction: Float,
    color: Color,
    strokeWidth: Float,
    strokeCap: StrokeCap,
) {
    val width = size.width
    val height = size.height
    // Start drawing from the vertical center of the stroke
    val yOffset = height / 2

    val isLtr = layoutDirection == LayoutDirection.Ltr
    val barStart = (if (isLtr) startFraction else 1f - endFraction) * width
    val barEnd = (if (isLtr) endFraction else 1f - startFraction) * width

    // if there isn't enough space to draw the stroke caps, fall back to StrokeCap.Butt
    if (strokeCap == StrokeCap.Butt || height > width) {
        // Progress line
        drawLine(color, Offset(barStart, yOffset), Offset(barEnd, yOffset), strokeWidth)
    } else {
        // need to adjust barStart and barEnd for the stroke caps
        val strokeCapOffset = strokeWidth / 2
        val coerceRange = strokeCapOffset..(width - strokeCapOffset)
        val adjustedBarStart = barStart.coerceIn(coerceRange)
        val adjustedBarEnd = barEnd.coerceIn(coerceRange)

        if (abs(endFraction - startFraction) > 0) {
            // Progress line
            drawLine(
                color,
                Offset(adjustedBarStart, yOffset),
                Offset(adjustedBarEnd, yOffset),
                strokeWidth,
                strokeCap,
            )
        }
    }
}
