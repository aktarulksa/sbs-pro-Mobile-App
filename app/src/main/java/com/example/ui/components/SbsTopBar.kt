package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserAccountEntity

@Composable
fun SbsTopBar(
    userAccount: UserAccountEntity?,
    activeTab: String,
    onTabSelected: (String) -> Unit,
    onHomeClick: () -> Unit,
    balanceExpanded: Boolean,
    onToggleBalance: () -> Unit,
    onShieldClick: () -> Unit,
    onAccountClick: () -> Unit,
    onThemeClick: () -> Unit,
    onAdminClick: () -> Unit = { onTabSelected("ADMIN") },
    pendingApprovalsCount: Int = 0,
    modifier: Modifier = Modifier
) {
    val mainBal = userAccount?.mainBalance ?: 0.0
    val driveBal = userAccount?.driveBalance ?: 0.0
    val bankBal = userAccount?.bankBalance ?: 0.0
    val barColor = MaterialTheme.colorScheme.primary
    val accentColor = MaterialTheme.colorScheme.secondary

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(barColor)
    ) {
        // App Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onHomeClick,
                    modifier = Modifier
                        .testTag("home_button")
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Column {
                    Text(
                        text = "SBS Pro",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    if (userAccount != null) {
                        Text(
                            text = userAccount.mobileNumber,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Orange Shield Badge matching screenshot
                Box(
                    modifier = Modifier
                        .testTag("security_shield")
                        .size(28.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onShieldClick),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(22.dp)) {
                        val w = size.width
                        val h = size.height
                        val shield = Path().apply {
                            moveTo(w / 2f, 0f)
                            lineTo(w, h * 0.25f)
                            lineTo(w * 0.85f, h * 0.75f)
                            lineTo(w / 2f, h)
                            lineTo(w * 0.15f, h * 0.75f)
                            lineTo(0f, h * 0.25f)
                            close()
                        }
                        drawPath(shield, Color(0xFFFF6F00))
                        drawCircle(Color.White, radius = w * 0.22f, center = Offset(w / 2f, h * 0.45f))
                        drawCircle(Color(0xFFFF6F00), radius = w * 0.16f, center = Offset(w / 2f, h * 0.45f))
                        drawLine(
                            Color.White,
                            start = Offset(w * 0.38f, h * 0.45f),
                            end = Offset(w * 0.62f, h * 0.45f),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Quick Balance Pill
                Surface(
                    onClick = onToggleBalance,
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.18f),
                    modifier = Modifier.testTag("balance_toggle_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (balanceExpanded) "৳ ${"%,.2f".format(mainBal)}" else "Tap for Balance",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Admin Console Button with Pending Badge (ONLY visible to Administrator)
                if (userAccount?.isAdmin == true) {
                    IconButton(
                        onClick = onAdminClick,
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("admin_panel_top_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin Panel",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            if (pendingApprovalsCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFF5252))
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }

                // Color Palette Button
                IconButton(
                    onClick = onThemeClick,
                    modifier = Modifier
                        .size(34.dp)
                        .testTag("change_color_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Change Theme Color",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Account Avatar button
                IconButton(
                    onClick = onAccountClick,
                    modifier = Modifier
                        .size(34.dp)
                        .testTag("account_profile_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Account Profile",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Expandable Balance Panel (Main, Drive, Bank)
        AnimatedVisibility(visible = balanceExpanded) {
            Surface(
                color = Color.Black.copy(alpha = 0.25f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    BalanceItem(title = "Main", amount = mainBal, color = Color(0xFF81C784))
                    BalanceItem(title = "Drive", amount = driveBal, color = Color(0xFFFFB74D))
                    BalanceItem(title = "Bank", amount = bankBal, color = Color(0xFF64B5F6))
                }
            }
        }

        // Top Navigation Tabs (Regular users see only agent tabs; Admins see Admin Control Panel)
        val visibleTabs = if (userAccount?.isAdmin == true) {
            listOf("RECHARGE", "REPORTS", "ADMIN", "CHANGE PIN")
        } else {
            listOf("RECHARGE", "REPORTS", "CHANGE PIN")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            visibleTabs.forEach { tab ->
                val isSelected = activeTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("tab_${tab.lowercase().replace(" ", "_")}")
                        .clickable { onTabSelected(tab) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = tab,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) barColor else Color(0xFF555555)
                            )
                            if (tab == "ADMIN" && pendingApprovalsCount > 0) {
                                Spacer(modifier = Modifier.width(3.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color(0xFFE53935))
                                        .padding(horizontal = 5.dp, vertical = 1.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$pendingApprovalsCount",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(3.dp)
                                    .background(barColor, RoundedCornerShape(2.dp))
                            )
                        } else {
                            Spacer(modifier = Modifier.height(3.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BalanceItem(title: String, amount: Double, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "৳ ${"%,.2f".format(amount)}",
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}
