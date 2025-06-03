package com.example.clock

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.graphics.toArgb
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.unit.sp

@Composable
fun ThemeToggleButton(autoColor: Boolean, onToggle: () -> Unit) {
    Button(
        onClick = onToggle,
        modifier = Modifier.padding(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (autoColor) Color.White else Color.Black,
            contentColor = if (autoColor) Color.Black else Color.White
        )
    ) {
        Text(
            text = if (autoColor) "白天" else "黑天",
            style = TextStyle(color = if (autoColor) Color.Black else Color.White)
        )
    }
}

@Composable
fun AnalogClock() {
    var currentTimeMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var autoColor by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTimeMillis = System.currentTimeMillis()
            delay(8)
        }
    }

    // 获取当前时间（小时、分钟、秒，带小数）
    val calendar = java.util.Calendar.getInstance().apply {
        timeInMillis = currentTimeMillis
    }

    val milli = calendar.get(java.util.Calendar.MILLISECOND)
    val second = calendar.get(java.util.Calendar.SECOND) + milli / 1000f
    val minute = calendar.get(java.util.Calendar.MINUTE) + second / 60f
    val hour = calendar.get(java.util.Calendar.HOUR) + minute / 60f

    val handColor by animateColorAsState(
        targetValue = if (autoColor) Color(0xFF000000) else Color(0xFFFFFFFF)
    )

    val secondHandColor by animateColorAsState(
        targetValue = if (autoColor) Color(0xFFFFA500) else Color(0xFFFFA500)
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (autoColor) Color(0xFFFFFFFF) else Color(0xFF555555)
    )

    val numberColor by animateColorAsState(
        targetValue = if (autoColor) Color(0xFF000000) else Color(0xFFFFFFFF)
    )

    val lineColor by animateColorAsState(
        targetValue = if (autoColor) Color(0xFF000000) else Color(0xFFFFFFFF)
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("时钟", fontSize = 32.sp, color = Color.White)
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ThemeToggleButton(autoColor = autoColor) {
            autoColor = !autoColor
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box (
            modifier = Modifier
                .size(280.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.Black)
                .padding(15.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(250.dp)) {
                val radius = size.minDimension / 2
                val center = this.center

                // 表盘圆圈
                drawCircle(color = backgroundColor, radius = radius, center = center)

                // 刻度
                for (i in 0 until 60) {
                    val angle = i * 6.0
                    val radian = Math.toRadians(angle - 90)
                    val lineLength = 5.dp.toPx()

                    val offsetFromEdge = 10.dp.toPx()
                    val startX = center.x + cos(radian) * (radius - offsetFromEdge - lineLength)
                    val startY = center.y + sin(radian) * (radius - offsetFromEdge - lineLength)
                    val endX = center.x + cos(radian) * (radius - offsetFromEdge)
                    val endY = center.y + sin(radian) * (radius - offsetFromEdge)

                    drawLine(
                        color = if (i % 5 == 0) lineColor else Color.Gray,
                        start = Offset(startX.toFloat(), startY.toFloat()),
                        end = Offset(endX.toFloat(), endY.toFloat()),
                        strokeWidth = 10f,
                        cap = StrokeCap.Round
                    )
                }

                for (i in 1..12) {
                    val angle = Math.toRadians((i * 30 - 90).toDouble())
                    val textRadius = radius - 30.dp.toPx()

                    val x = center.x + cos(angle) * textRadius
                    val y = center.y + sin(angle) * textRadius + 6.dp.toPx()  // 用 dp 微调更清晰

                    drawText(
                        text = i.toString(),
                        x = x.toFloat(),
                        y = y.toFloat(),
                        textSize = 20.dp.toPx(),
                        color = numberColor.toArgb(),
                        typeface = Typeface.DEFAULT_BOLD
                    )
                }
                // 时针（每小时30度）
                drawHand(
                    center,
                    angle = hour * 30f,
                    lengthRatio = 0.5f,
                    color = handColor,
                    strokeWidth = 20f
                )
                // 分针（每分钟6度）
                drawHand(
                    center,
                    angle = minute * 6f,
                    lengthRatio = 0.7f,
                    color = handColor,
                    strokeWidth = 20f
                )
                // 秒针（每秒6度）
                drawHand(
                    center,
                    angle = second * 6f,
                    lengthRatio = 0.9f,
                    color = secondHandColor,
                    strokeWidth = 5f
                )

                // 中心点
                drawCircle(color = handColor, radius = 6.dp.toPx(), center = center)
                drawCircle(color = secondHandColor, radius = 4.dp.toPx(), center = center)

            }
        }
    }
}

// 表针绘制函数
fun DrawScope.drawHand(
    center: Offset,
    angle: Float,
    lengthRatio: Float,
    color: Color,
    strokeWidth: Float
) {
    val radian = Math.toRadians(angle.toDouble() - 90)
    val endX = center.x + cos(radian) * size.minDimension / 2 * lengthRatio
    val endY = center.y + sin(radian) * size.minDimension / 2 * lengthRatio

    drawLine(
        color = color,
        start = center,
        end = Offset(endX.toFloat(), endY.toFloat()),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )
}

fun DrawScope.drawText(
    text: String,
    x: Float,
    y: Float,
    color: Int = android.graphics.Color.BLACK,
    textSize: Float = 16f,
    typeface: Typeface = Typeface.DEFAULT_BOLD
) {
    val paint = Paint().apply {
        this.color = color
        this.textSize = textSize
        this.isAntiAlias = true
        this.textAlign = Paint.Align.CENTER
        this.typeface = typeface
    }

    drawContext.canvas.nativeCanvas.drawText(text, x, y, paint)
}