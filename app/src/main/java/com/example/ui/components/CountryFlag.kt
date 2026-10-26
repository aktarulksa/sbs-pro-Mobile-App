package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Country
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CountryFlag(
    flagId: String,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .border(2.dp, Color(0xFFE53935), CircleShape) // Red circular ring matching screenshot
            .padding(2.dp)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height

            when (flagId) {
                "flag_bd" -> {
                    // Bangladesh: Bottle green background with offset red sun
                    drawRect(Color(0xFF006A4E))
                    drawCircle(
                        color = Color(0xFFF42A41),
                        radius = w * 0.28f,
                        center = Offset(w * 0.45f, h * 0.5f)
                    )
                }
                "flag_in" -> {
                    // India: Saffron, White, Green tricolor + Ashoka Chakra
                    drawRect(Color(0xFFFF9933), topLeft = Offset(0f, 0f), size = Size(w, h / 3f))
                    drawRect(Color.White, topLeft = Offset(0f, h / 3f), size = Size(w, h / 3f))
                    drawRect(Color(0xFF138808), topLeft = Offset(0f, 2 * h / 3f), size = Size(w, h / 3f))
                    // Navy blue wheel
                    drawCircle(
                        color = Color(0xFF000080),
                        radius = h * 0.12f,
                        center = Offset(w / 2f, h / 2f),
                        style = Stroke(width = 2.dp.toPx())
                    )
                    for (i in 0 until 12) {
                        val angle = i * (Math.PI / 6.0)
                        val r = h * 0.12f
                        drawLine(
                            color = Color(0xFF000080),
                            start = Offset(w / 2f, h / 2f),
                            end = Offset(
                                (w / 2f + r * cos(angle)).toFloat(),
                                (h / 2f + r * sin(angle)).toFloat()
                            ),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                }
                "flag_in_dth" -> {
                    // India DTH: Dish antenna emblem
                    drawRect(Color(0xFFFAFAFA))
                    // Satellite dish in blue/red
                    val path = Path().apply {
                        moveTo(w * 0.25f, h * 0.35f)
                        cubicTo(w * 0.35f, h * 0.65f, w * 0.65f, h * 0.65f, w * 0.75f, h * 0.35f)
                        lineTo(w * 0.68f, h * 0.30f)
                        cubicTo(w * 0.60f, h * 0.55f, w * 0.40f, h * 0.55f, w * 0.32f, h * 0.30f)
                        close()
                    }
                    drawPath(path, Color(0xFF0D47A1))
                    // Antenna stand & signal waves
                    drawLine(Color(0xFF1565C0), Offset(w * 0.5f, h * 0.55f), Offset(w * 0.5f, h * 0.85f), strokeWidth = 3.dp.toPx())
                    drawLine(Color(0xFF1565C0), Offset(w * 0.35f, h * 0.85f), Offset(w * 0.65f, h * 0.85f), strokeWidth = 3.dp.toPx())
                    // Signal arc in red
                    drawArc(
                        color = Color(0xFFD32F2F),
                        startAngle = 210f,
                        sweepAngle = 120f,
                        useCenter = false,
                        topLeft = Offset(w * 0.35f, h * 0.12f),
                        size = Size(w * 0.3f, h * 0.25f),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
                "flag_pk" -> {
                    // Pakistan: Dark green, white bar on left, white crescent & star
                    drawRect(Color(0xFF01411C))
                    drawRect(Color.White, topLeft = Offset(0f, 0f), size = Size(w * 0.25f, h))
                    // White crescent
                    drawCircle(Color.White, radius = w * 0.22f, center = Offset(w * 0.65f, h * 0.5f))
                    drawCircle(Color(0xFF01411C), radius = w * 0.19f, center = Offset(w * 0.71f, h * 0.44f))
                    // Star
                    drawCircle(Color.White, radius = w * 0.05f, center = Offset(w * 0.70f, h * 0.36f))
                }
                "flag_np" -> {
                    // Nepal: Double pennant shape on white
                    drawRect(Color.White)
                    val crimsonPath = Path().apply {
                        moveTo(w * 0.2f, h * 0.15f)
                        lineTo(w * 0.75f, h * 0.45f)
                        lineTo(w * 0.4f, h * 0.45f)
                        lineTo(w * 0.75f, h * 0.85f)
                        lineTo(w * 0.2f, h * 0.85f)
                        close()
                    }
                    drawPath(crimsonPath, Color(0xFFDC143C))
                    // Blue border
                    drawPath(crimsonPath, Color(0xFF003893), style = Stroke(width = 2.dp.toPx()))
                }
                "flag_mm" -> {
                    // Myanmar: Yellow, Green, Red + White Star
                    drawRect(Color(0xFFFECB00), topLeft = Offset(0f, 0f), size = Size(w, h / 3f))
                    drawRect(Color(0xFF34B233), topLeft = Offset(0f, h / 3f), size = Size(w, h / 3f))
                    drawRect(Color(0xFFEA2839), topLeft = Offset(0f, 2 * h / 3f), size = Size(w, h / 3f))
                    drawCircle(Color.White, radius = w * 0.18f, center = Offset(w / 2f, h / 2f))
                }
                "flag_lk" -> {
                    // Sri Lanka: Green/Orange stripes on left, Maroon field on right
                    drawRect(Color(0xFFFFBE29)) // Gold background
                    drawRect(Color(0xFF00534E), topLeft = Offset(w * 0.1f, h * 0.1f), size = Size(w * 0.15f, h * 0.8f))
                    drawRect(Color(0xFFEB7400), topLeft = Offset(w * 0.28f, h * 0.1f), size = Size(w * 0.15f, h * 0.8f))
                    drawRect(Color(0xFF8D153A), topLeft = Offset(w * 0.47f, h * 0.1f), size = Size(w * 0.43f, h * 0.8f))
                }
                "flag_sg" -> {
                    // Singapore: Red top, White bottom, white crescent + 5 stars
                    drawRect(Color(0xFFED2939), topLeft = Offset(0f, 0f), size = Size(w, h / 2f))
                    drawRect(Color.White, topLeft = Offset(0f, h / 2f), size = Size(w, h / 2f))
                    // Crescent in red field
                    drawCircle(Color.White, radius = w * 0.12f, center = Offset(w * 0.25f, h * 0.25f))
                    drawCircle(Color(0xFFED2939), radius = w * 0.10f, center = Offset(w * 0.28f, h * 0.25f))
                }
                "flag_my" -> {
                    // Malaysia: Red/White stripes, Blue canton, yellow crescent
                    for (i in 0 until 6) {
                        val color = if (i % 2 == 0) Color(0xFFCC0000) else Color.White
                        drawRect(color, topLeft = Offset(0f, i * h / 6f), size = Size(w, h / 6f))
                    }
                    drawRect(Color(0xFF000066), topLeft = Offset(0f, 0f), size = Size(w * 0.5f, h * 0.5f))
                    drawCircle(Color(0xFFFFCC00), radius = w * 0.12f, center = Offset(w * 0.25f, h * 0.25f))
                }
                "flag_ph" -> {
                    // Philippines: Blue top, red bottom, white triangle
                    drawRect(Color(0xFF0038A8), topLeft = Offset(0f, 0f), size = Size(w, h / 2f))
                    drawRect(Color(0xFFCE1126), topLeft = Offset(0f, h / 2f), size = Size(w, h / 2f))
                    val tri = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(w * 0.45f, h * 0.5f)
                        lineTo(0f, h)
                        close()
                    }
                    drawPath(tri, Color.White)
                    drawCircle(Color(0xFFFCD116), radius = w * 0.08f, center = Offset(w * 0.15f, h * 0.5f))
                }
                "flag_ai" -> {
                    // Anguilla: Blue background with three orange dolphins emblem
                    drawRect(Color(0xFF00247D))
                    drawCircle(Color.White, radius = w * 0.25f, center = Offset(w * 0.65f, h * 0.5f))
                    drawCircle(Color(0xFFFF7900), radius = w * 0.14f, center = Offset(w * 0.65f, h * 0.5f), style = Stroke(width = 3.dp.toPx()))
                }
                "flag_ar" -> {
                    // Argentina: Sky blue, white, sky blue + sun
                    drawRect(Color(0xFF74ACDF), topLeft = Offset(0f, 0f), size = Size(w, h / 3f))
                    drawRect(Color.White, topLeft = Offset(0f, h / 3f), size = Size(w, h / 3f))
                    drawRect(Color(0xFF74ACDF), topLeft = Offset(0f, 2 * h / 3f), size = Size(w, h / 3f))
                    drawCircle(Color(0xFFF6B40E), radius = h * 0.11f, center = Offset(w / 2f, h / 2f))
                }
                else -> {
                    // Default globe
                    drawRect(Color(0xFF1976D2))
                    drawCircle(Color(0xFF64B5F6), radius = w * 0.35f, center = Offset(w / 2f, h / 2f))
                }
            }
        }
    }
}
