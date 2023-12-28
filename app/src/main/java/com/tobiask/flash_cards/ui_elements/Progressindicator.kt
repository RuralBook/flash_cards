package com.tobiask.flash_cards.ui_elements

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin


@Composable
fun CircularProgressBar(
    modifier: Modifier = Modifier,
    value: Int,
    maxValue: Int,
    fillColor: Color,
    backgroundColor: Color,
    strokeWidth: Dp
) {
    val textColor = MaterialTheme.colors.onBackground.toArgb()
    val percentage = (value.toFloat()/maxValue)
    Canvas(
        modifier = modifier
            .size(150.dp)
            .padding(10.dp)
    ) {
        // Background Line
        drawArc(
            color = backgroundColor,
            140f,
            260f,
            false,
            style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round),
            size = Size(size.width, size.height)
        )

        drawArc(
            color = fillColor,
            140f,
            percentage * 260f,
            false,
            style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round),
            size = Size(size.width, size.height)
        )


        val textPaint = Paint().apply {
            textSize = 22.sp.toPx()
            textAlign = Paint.Align.CENTER
            color = textColor
            isFakeBoldText = true
        }
        val textX = size.width / 2
        val textY = size.height / 2 + (textPaint.textSize / 2)

        drawContext.canvas.nativeCanvas.apply {
            drawText(
                "$value/$maxValue",
                textX,
                textY,
                textPaint
            )
        }


        val angleInDegrees = (percentage * 260.0) + 50.0
        val radius = (size.height / 2)
        val x = -(radius * sin(Math.toRadians(angleInDegrees))).toFloat() + (size.width / 2)
        val y = (radius * cos(Math.toRadians(angleInDegrees))).toFloat() + (size.height / 2)

        drawCircle(
            color = Color.White,
            radius = 5f,
            center = Offset(x,  y)
        )
    }
}

@Preview
@Composable
fun PreviewPorgressBar() {
    CircularProgressBar(
        value = 50,
        maxValue = 365,
        fillColor = Color(android.graphics.Color.parseColor("#4DB6AC")),
        backgroundColor = Color(android.graphics.Color.parseColor("#90A4AE")),
        strokeWidth = 10.dp
    )
}