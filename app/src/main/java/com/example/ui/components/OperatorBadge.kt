package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Operator

@Composable
fun OperatorBadge(
    operator: Operator,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 72.dp
) {
    Column(
        modifier = modifier
            .testTag("operator_${operator.code.lowercase()}")
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Bright orange circle with white bold italic text matching Screenshot 4
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .border(2.dp, Color(0xFFF4511E), CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF7043),
                            Color(0xFFE64A19)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = operator.code,
                color = Color.White,
                fontSize = if (operator.code.length <= 3) 20.sp else 16.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = operator.name,
            color = Color(0xFFE65100),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 2,
            modifier = Modifier.widthIn(max = 84.dp)
        )
    }
}
