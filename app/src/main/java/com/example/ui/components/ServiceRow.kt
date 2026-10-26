package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ServiceType

@Composable
fun ServiceRow(
    selectedService: ServiceType,
    onServiceSelected: (ServiceType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 12.dp, horizontal = 8.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ServiceItem(
            type = ServiceType.RECHARGE,
            isSelected = selectedService == ServiceType.RECHARGE,
            onClick = { onServiceSelected(ServiceType.RECHARGE) }
        ) {
            // Globe icon in teal/green
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .border(1.5.dp, Color(0xFF2E7D32), CircleShape)
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Recharge",
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        ServiceItem(
            type = ServiceType.BKASH,
            isSelected = selectedService == ServiceType.BKASH,
            onClick = { onServiceSelected(ServiceType.BKASH) }
        ) {
            // bKash pink origami bird badge
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .border(1.5.dp, Color(0xFFE2136E), CircleShape)
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFCE4EC)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(24.dp)) {
                    val w = size.width
                    val h = size.height
                    val bird = Path().apply {
                        moveTo(w * 0.15f, h * 0.7f)
                        lineTo(w * 0.65f, h * 0.2f)
                        lineTo(w * 0.85f, h * 0.45f)
                        lineTo(w * 0.55f, h * 0.85f)
                        close()
                        moveTo(w * 0.65f, h * 0.2f)
                        lineTo(w * 0.95f, h * 0.25f)
                        lineTo(w * 0.85f, h * 0.45f)
                        close()
                    }
                    drawPath(bird, Color(0xFFE2136E))
                }
            }
        }

        ServiceItem(
            type = ServiceType.ROCKET,
            isSelected = selectedService == ServiceType.ROCKET,
            onClick = { onServiceSelected(ServiceType.ROCKET) }
        ) {
            // Rocket DBBL emblem (concentric colored arcs)
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .border(1.5.dp, Color(0xFF8E24AA), CircleShape)
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEDE7F6)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(24.dp)) {
                    val w = size.width
                    val h = size.height
                    // Rainbow swirl arcs
                    val colors = listOf(Color(0xFFD32F2F), Color(0xFFFF9800), Color(0xFF4CAF50), Color(0xFF2196F3), Color(0xFF9C27B0))
                    colors.forEachIndexed { i, c ->
                        drawCircle(
                            color = c,
                            radius = (w * 0.45f) - (i * 2.dp.toPx()),
                            center = Offset(w / 2f, h * 0.65f),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx())
                        )
                    }
                }
            }
        }

        ServiceItem(
            type = ServiceType.NAGAD,
            isSelected = selectedService == ServiceType.NAGAD,
            onClick = { onServiceSelected(ServiceType.NAGAD) }
        ) {
            // Nagad orange / red icon
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .border(1.5.dp, Color(0xFFE65100), CircleShape)
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF3E0)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "নগদ",
                    color = Color(0xFFE65100),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        ServiceItem(
            type = ServiceType.BILL_PAY_ELECTRIC,
            isSelected = selectedService == ServiceType.BILL_PAY_ELECTRIC,
            onClick = { onServiceSelected(ServiceType.BILL_PAY_ELECTRIC) }
        ) {
            // Bill Pay Blue
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00838F)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "BIL",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    fontSize = 14.sp
                )
            }
        }

        ServiceItem(
            type = ServiceType.BILL_PAY_GAS,
            isSelected = selectedService == ServiceType.BILL_PAY_GAS,
            onClick = { onServiceSelected(ServiceType.BILL_PAY_GAS) }
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00695C)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "BIL",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun ServiceItem(
    type: ServiceType,
    isSelected: Boolean,
    onClick: () -> Unit,
    iconContent: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .testTag("service_${type.name.lowercase()}")
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        iconContent()
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = type.displayName,
            color = if (isSelected) Color(0xFF1976D2) else Color(0xFF555555),
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}
