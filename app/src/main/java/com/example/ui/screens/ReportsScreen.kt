package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.ReportTile
import com.example.ui.components.TileInfo
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReportsScreen(
    transactions: List<Transaction>,
    smsLogs: List<SmsLog>,
    activeDetailCategory: ReportCategory?,
    onTileClick: (ReportCategory) -> Unit,
    onBackToGrid: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (activeDetailCategory != null) {
        // Detailed drill-down view for clicked category
        ReportDetailView(
            category = activeDetailCategory,
            transactions = transactions,
            smsLogs = smsLogs,
            onBack = onBackToGrid,
            modifier = modifier
        )
    } else {
        // SCREENSHOT 3 Tile Grid Layout
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Section 1: History
                item {
                    Text(
                        text = "History",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF757575),
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )

                    val historyTiles = listOf(
                        TileInfo(ReportCategory.HISTORY, "History", TileHistoryBlue, Icons.Default.History),
                        TileInfo(ReportCategory.SMS, "SMS", TileSmsRed, Icons.AutoMirrored.Filled.Chat),
                        TileInfo(ReportCategory.PREPAID, "Prepaid", TilePrepaidOrange, Icons.Default.SimCard),
                        TileInfo(ReportCategory.BILLPAY, "BillPay", TileBillPayBrown, Icons.Default.CreditCard),
                        TileInfo(ReportCategory.RECEIVE, "Receive", TileReceiveCyan, Icons.Default.CallReceived),
                        TileInfo(ReportCategory.PAYMENT, "Payment", TilePaymentGreen, Icons.Default.Payment),
                        TileInfo(ReportCategory.RECENT, "Recent", TileRecentPurple, Icons.Default.Update)
                    )

                    TileRowGrid(tiles = historyTiles, onTileClick = onTileClick)
                }

                item {
                    HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                }

                // Section 2: Report
                item {
                    Text(
                        text = "Report",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF757575),
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )

                    val reportTiles = listOf(
                        TileInfo(ReportCategory.PROFIT_LOSS, "Profit Loss", TileProfitLossPink, Icons.Default.BarChart),
                        TileInfo(ReportCategory.DAILY_REPORT, "Daily Report", TileDailyReportBlue, Icons.Default.BarChart),
                        TileInfo(ReportCategory.DAYS_30, "30 Days", Tile30DaysLilac, Icons.Default.BarChart),
                        TileInfo(ReportCategory.THIS_MONTH, "This Month", TileThisMonthSlate, Icons.Default.BarChart),
                        TileInfo(ReportCategory.LAST_MONTH, "Last Month", TileLastMonthPurple, Icons.Default.BarChart),
                        TileInfo(ReportCategory.THREE_MONTH, "3 Month", Tile3MonthDeepPurple, Icons.Default.BarChart),
                        TileInfo(ReportCategory.YEARLY, "Yearly", TileYearlyGreen, Icons.Default.BarChart),
                        TileInfo(ReportCategory.CUSTOM_RANGE, "Custom Range", TileCustomRangeViolet, Icons.Default.DateRange),
                        TileInfo(ReportCategory.RESELLER, "Reseller", TileResellerBrown, Icons.Default.Group)
                    )

                    TileRowGrid(tiles = reportTiles, onTileClick = onTileClick)
                }
            }
        }
    }
}

@Composable
private fun TileRowGrid(
    tiles: List<TileInfo>,
    onTileClick: (ReportCategory) -> Unit
) {
    // 3 items per row matching Screenshot 3
    val chunked = tiles.chunked(3)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        chunked.forEach { rowTiles ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                rowTiles.forEach { tile ->
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        ReportTile(
                            info = tile,
                            onClick = { onTileClick(tile.category) }
                        )
                    }
                }
                // Fill remainder of row if incomplete
                if (rowTiles.size < 3) {
                    repeat(3 - rowTiles.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun ReportDetailView(
    category: ReportCategory,
    transactions: List<Transaction>,
    smsLogs: List<SmsLog>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title = when (category) {
        ReportCategory.HISTORY -> "Transaction History"
        ReportCategory.SMS -> "Operator SMS Inbox"
        ReportCategory.PREPAID -> "Prepaid Recharge Logs"
        ReportCategory.BILLPAY -> "Bill Pay Logs"
        ReportCategory.RECEIVE -> "Received Fund Logs"
        ReportCategory.PAYMENT -> "Payment & Deposit Logs"
        ReportCategory.RECENT -> "Recent Operations"
        ReportCategory.PROFIT_LOSS -> "Profit & Loss Analytics"
        ReportCategory.DAILY_REPORT -> "Today's Financial Report"
        ReportCategory.DAYS_30 -> "30 Days Summary"
        ReportCategory.THIS_MONTH -> "This Month Performance"
        ReportCategory.LAST_MONTH -> "Last Month Archives"
        ReportCategory.THREE_MONTH -> "Quarterly Report (3 Month)"
        ReportCategory.YEARLY -> "Annual Performance Report"
        ReportCategory.CUSTOM_RANGE -> "Custom Date Range Filter"
        ReportCategory.RESELLER -> "Sub-Reseller Management"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Header with back navigation
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("back_from_report_detail")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF333333)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
            }
        }

        when (category) {
            ReportCategory.SMS -> {
                // SMS Logs View
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(smsLogs, key = { it.id }) { sms ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = sms.sender,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE53935),
                                        fontSize = 14.sp
                                    )
                                    val time = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(sms.timestamp))
                                    Text(text = time, color = Color.Gray, fontSize = 11.sp)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = sms.message,
                                    fontSize = 13.sp,
                                    color = Color(0xFF333333),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
            ReportCategory.PROFIT_LOSS, ReportCategory.DAILY_REPORT, ReportCategory.DAYS_30,
            ReportCategory.THIS_MONTH, ReportCategory.LAST_MONTH, ReportCategory.THREE_MONTH,
            ReportCategory.YEARLY -> {
                // Analytics Summary View
                AnalyticsDashboard(category = category, transactions = transactions)
            }
            ReportCategory.RESELLER -> {
                // Reseller List
                ResellerManagementView()
            }
            else -> {
                // Transactions List (Prepaid, BillPay, Receive, Payment, History, Recent)
                val filtered = remember(category, transactions) {
                    when (category) {
                        ReportCategory.PREPAID -> transactions.filter { it.type.contains("Recharge") }
                        ReportCategory.BILLPAY -> transactions.filter { it.type == "BillPay" }
                        ReportCategory.PAYMENT -> transactions.filter { it.type == "Payment" || it.type == "Rocket" || it.type == "bKash" }
                        ReportCategory.RECENT -> transactions.take(10)
                        else -> transactions
                    }
                }

                if (filtered.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No records found", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filtered, key = { it.id }) { txn ->
                            TransactionItemCard(txn)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionItemCard(txn: Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = txn.operatorOrProvider,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF212121)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        val badgeColor = when (txn.status) {
                            TransactionStatus.SUCCESS -> Color(0xFF2E7D32)
                            TransactionStatus.PENDING -> SbsOrange
                            TransactionStatus.FAILED -> Color(0xFFC62828)
                        }
                        val badgeText = when (txn.status) {
                            TransactionStatus.SUCCESS -> "APPROVED"
                            TransactionStatus.PENDING -> "PENDING"
                            TransactionStatus.FAILED -> "REJECTED"
                        }
                        Surface(
                            color = badgeColor.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = badgeText,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeColor,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = "To: ${txn.recipient}",
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "৳ ${"%,.2f".format(txn.amount)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1976D2)
                    )
                    Text(
                        text = "Comm: ৳ ${"%.2f".format(txn.commission)}",
                        fontSize = 11.sp,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "TrxID: ${txn.trxId}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                val dateStr = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).format(Date(txn.timestamp))
                Text(
                    text = dateStr,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun AnalyticsDashboard(category: ReportCategory, transactions: List<Transaction>) {
    val totalVolume = transactions.sumOf { it.amount }
    val totalCommission = transactions.sumOf { it.commission }
    val totalRecharge = transactions.filter { it.type.contains("Recharge") }.sumOf { it.amount }
    val totalMfs = transactions.filter { it.type == "bKash" || it.type == "Rocket" }.sumOf { it.amount }
    val totalBillPay = transactions.filter { it.type == "BillPay" }.sumOf { it.amount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF4A154B)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Net Commission Earned", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "৳ ${"%,.2f".format(totalCommission)}",
                        color = Color(0xFFFFB74D),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Turn-over", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                            Text("৳ ${"%,.2f".format(totalVolume)}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column {
                            Text("Completed Trx", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                            Text("${transactions.size} Items", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        item {
            Text("Service-wise Turnover Breakdown", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF333333))
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ServiceProgressItem("Mobile Recharge & Drive", totalRecharge, totalVolume, Color(0xFFFB8C00))
                    ServiceProgressItem("Mobile Banking (bKash & Rocket)", totalMfs, totalVolume, Color(0xFFE2136E))
                    ServiceProgressItem("Utility Bill Payment", totalBillPay, totalVolume, Color(0xFF00838F))
                }
            }
        }
    }
}

@Composable
private fun ServiceProgressItem(label: String, amount: Double, total: Double, color: Color) {
    val fraction = if (total > 0) (amount / total).toFloat().coerceIn(0f, 1f) else 0f
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 12.sp, color = Color(0xFF424242))
            Text("৳ ${"%,.2f".format(amount)} (${(fraction * 100).toInt()}%)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
    }
}

@Composable
private fun ResellerManagementView() {
    val subAgents = listOf(
        Triple("Sub-Agent 01 (Dhaka)", "01711-229988", 5400.0),
        Triple("Sub-Agent 02 (Chittagong)", "01819-334455", 3850.0),
        Triple("Sub-Agent 03 (Sylhet)", "01912-778899", 8200.0),
        Triple("Sub-Agent 04 (Rajshahi)", "01552-110022", 1950.0)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(subAgents) { agent ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(agent.first, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(agent.second, fontSize = 12.sp, color = Color.Gray)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Balance", fontSize = 10.sp, color = Color.Gray)
                        Text("৳ ${"%,.2f".format(agent.third)}", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
