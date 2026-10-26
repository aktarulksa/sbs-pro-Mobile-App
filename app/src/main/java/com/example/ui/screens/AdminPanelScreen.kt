package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.UserAccountEntity
import com.example.model.Transaction
import com.example.model.TransactionStatus
import com.example.ui.theme.SbsGreen
import com.example.ui.theme.SbsOrange
import com.example.ui.theme.SbsPurplePrimary
import com.example.ui.theme.SbsRed
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminPanelScreen(
    pendingTransactions: List<Transaction>,
    allTransactions: List<Transaction>,
    allAgents: List<UserAccountEntity>,
    activeSubTab: String,
    onSubTabChanged: (String) -> Unit,
    onApproveTransaction: (String) -> Unit,
    onRejectTransactionClick: (Transaction) -> Unit,
    onApproveAllPending: () -> Unit,
    onOpenAddBalance: (UserAccountEntity) -> Unit,
    isProcessing: Boolean,
    rtdbStatus: String = "Connected",
    isRtdbConnected: Boolean = true,
    onTestRtdbSync: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf("ALL") } // "ALL", "PENDING", "SUCCESS", "FAILED"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7F9))
    ) {
        // Admin Header Banner
        Surface(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Admin Control Panel",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Transaction Approval & Reseller Central",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    if (pendingTransactions.isNotEmpty()) {
                        Button(
                            onClick = onApproveAllPending,
                            enabled = !isProcessing,
                            colors = ButtonDefaults.buttonColors(containerColor = SbsGreen),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("admin_approve_all_button")
                        ) {
                            Icon(
                                Icons.Default.DoneAll,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Approve All",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Statistics Row
                val pendingTotal = pendingTransactions.sumOf { it.amount }
                val approvedTotal = allTransactions.filter { it.status == TransactionStatus.SUCCESS }.sumOf { it.amount }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AdminMetricCard(
                        title = "Pending Queue",
                        value = "${pendingTransactions.size} Req",
                        subValue = "৳ ${"%,.0f".format(pendingTotal)}",
                        icon = Icons.Default.PendingActions,
                        containerColor = if (pendingTransactions.isNotEmpty()) SbsOrange else Color.White.copy(alpha = 0.15f),
                        contentColor = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    AdminMetricCard(
                        title = "Approved Volume",
                        value = "৳ ${"%,.0f".format(approvedTotal)}",
                        subValue = "${allTransactions.count { it.status == TransactionStatus.SUCCESS }} completed",
                        icon = Icons.Default.CheckCircle,
                        containerColor = Color.White.copy(alpha = 0.15f),
                        contentColor = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    AdminMetricCard(
                        title = "Agents",
                        value = "${allAgents.size} Resellers",
                        subValue = "Active",
                        icon = Icons.Default.People,
                        containerColor = Color.White.copy(alpha = 0.15f),
                        contentColor = Color.White,
                        modifier = Modifier.weight(0.9f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Firebase Realtime Database Status Card
                Surface(
                    color = Color.Black.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isRtdbConnected) Color(0xFF4CAF50) else Color(0xFFFFB300))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Firebase RTDB: ",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = if (isRtdbConnected) "Live Cloud Sync" else "Offline Cache Active",
                                        fontSize = 11.sp,
                                        color = if (isRtdbConnected) Color(0xFF81C784) else Color(0xFFFFE082)
                                    )
                                }
                                Text(
                                    text = "https://gen-lang-client-0892038385-default-rtdb.firebaseio.com/",
                                    fontSize = 9.sp,
                                    color = Color.White.copy(alpha = 0.75f),
                                    maxLines = 1
                                )
                            }
                        }

                        Button(
                            onClick = onTestRtdbSync,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.2f),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(
                                Icons.Default.Sync,
                                contentDescription = "Sync Ping",
                                modifier = Modifier.size(13.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Ping", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Sub Navigation Tabs
        Surface(
            color = Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                listOf(
                    SubTab("PENDING", "Pending (${pendingTransactions.size})", Icons.Default.HourglassTop),
                    SubTab("ALL_TXN", "All Transactions", Icons.Default.ReceiptLong),
                    SubTab("RESELLERS", "Reseller Agents", Icons.Default.Group)
                ).forEach { tab ->
                    val isSelected = activeSubTab == tab.id
                    val primaryColor = MaterialTheme.colorScheme.primary

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) primaryColor.copy(alpha = 0.1f) else Color.Transparent)
                            .clickable { onSubTabChanged(tab.id) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = null,
                                tint = if (isSelected) primaryColor else Color(0xFF757575),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tab.label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) primaryColor else Color(0xFF555555)
                            )
                        }
                    }
                }
            }
        }

        // Content Area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            when (activeSubTab) {
                "PENDING" -> {
                    PendingApprovalsList(
                        pendingTransactions = pendingTransactions,
                        onApprove = onApproveTransaction,
                        onReject = onRejectTransactionClick,
                        isProcessing = isProcessing
                    )
                }
                "ALL_TXN" -> {
                    AllTransactionsAuditList(
                        allTransactions = allTransactions,
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it },
                        statusFilter = statusFilter,
                        onStatusFilterChange = { statusFilter = it }
                    )
                }
                "RESELLERS" -> {
                    ResellerAgentsList(
                        agents = allAgents,
                        onAddBalance = onOpenAddBalance
                    )
                }
            }
        }
    }
}

private data class SubTab(val id: String, val label: String, val icon: ImageVector)

@Composable
private fun AdminMetricCard(
    title: String,
    value: String,
    subValue: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = contentColor.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor.copy(alpha = 0.8f),
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Text(
                text = subValue,
                fontSize = 10.sp,
                color = contentColor.copy(alpha = 0.75f)
            )
        }
    }
}

@Composable
private fun PendingApprovalsList(
    pendingTransactions: List<Transaction>,
    onApprove: (String) -> Unit,
    onReject: (Transaction) -> Unit,
    isProcessing: Boolean
) {
    if (pendingTransactions.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Cleared",
                        tint = SbsGreen,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "No Pending Transactions",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF2E7D32)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "All recharge, agent banking, and bill payment requests have been reviewed and approved.",
                    fontSize = 12.sp,
                    color = Color(0xFF757575),
                    lineHeight = 17.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transactions Requiring Admin Approval (${pendingTransactions.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF424242)
                    )
                    Text(
                        text = "Manual Verification",
                        fontSize = 11.sp,
                        color = SbsOrange,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            items(pendingTransactions, key = { it.id }) { txn ->
                PendingTransactionCard(
                    txn = txn,
                    onApprove = { onApprove(txn.id) },
                    onReject = { onReject(txn) },
                    isProcessing = isProcessing
                )
            }
        }
    }
}

@Composable
private fun PendingTransactionCard(
    txn: Transaction,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    isProcessing: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Type badge & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = txn.type,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = txn.operatorOrProvider,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                }

                Surface(
                    color = Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(SbsOrange)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "PENDING",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SbsOrange
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Body: Recipient & Amount Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Recipient Number / Acct:", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        text = txn.recipient,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2)
                    )
                    if (txn.userMobile.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Agent: ${txn.userMobile}",
                            fontSize = 11.sp,
                            color = Color(0xFF616161)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Requested Amount:", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        text = "৳ ${"%,.2f".format(txn.amount)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    Text(
                        text = "Comm: + ৳ ${"%.2f".format(txn.commission)}",
                        fontSize = 11.sp,
                        color = SbsGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(8.dp))

            // Footer: TrxID, Timestamp, and Admin Approval Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "TrxID: ${txn.trxId}", fontSize = 10.sp, color = Color.Gray)
                    val time = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(txn.timestamp))
                    Text(text = time, fontSize = 10.sp, color = Color.Gray)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onReject,
                        enabled = !isProcessing,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SbsRed),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("reject_txn_${txn.id}")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Reject", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onApprove,
                        enabled = !isProcessing,
                        colors = ButtonDefaults.buttonColors(containerColor = SbsGreen),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("approve_txn_${txn.id}")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Approve", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun AllTransactionsAuditList(
    allTransactions: List<Transaction>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    statusFilter: String,
    onStatusFilterChange: (String) -> Unit
) {
    val filtered = allTransactions.filter { tx ->
        val matchesQuery = searchQuery.isBlank() ||
                tx.recipient.contains(searchQuery, ignoreCase = true) ||
                tx.trxId.contains(searchQuery, ignoreCase = true) ||
                tx.userMobile.contains(searchQuery, ignoreCase = true) ||
                tx.operatorOrProvider.contains(searchQuery, ignoreCase = true)
        val matchesStatus = when (statusFilter) {
            "PENDING" -> tx.status == TransactionStatus.PENDING
            "SUCCESS" -> tx.status == TransactionStatus.SUCCESS
            "FAILED" -> tx.status == TransactionStatus.FAILED
            else -> true
        }
        matchesQuery && matchesStatus
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search & Filter Controls
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search by Mobile, TrxID, or Service...", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("admin_search_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Status Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                "ALL" to "All (${allTransactions.size})",
                "PENDING" to "Pending (${allTransactions.count { it.status == TransactionStatus.PENDING }})",
                "SUCCESS" to "Approved (${allTransactions.count { it.status == TransactionStatus.SUCCESS }})",
                "FAILED" to "Rejected (${allTransactions.count { it.status == TransactionStatus.FAILED }})"
            ).forEach { (key, label) ->
                val isSelected = statusFilter == key
                FilterChip(
                    selected = isSelected,
                    onClick = { onStatusFilterChange(key) },
                    label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No transactions match the selected filter.", color = Color.Gray, fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filtered, key = { it.id }) { txn ->
                    AuditTransactionItem(txn = txn)
                }
            }
        }
    }
}

@Composable
private fun AuditTransactionItem(txn: Transaction) {
    val statusColor = when (txn.status) {
        TransactionStatus.SUCCESS -> SbsGreen
        TransactionStatus.PENDING -> SbsOrange
        TransactionStatus.FAILED -> SbsRed
    }

    val statusText = when (txn.status) {
        TransactionStatus.SUCCESS -> "APPROVED"
        TransactionStatus.PENDING -> "PENDING"
        TransactionStatus.FAILED -> "REJECTED"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = txn.operatorOrProvider, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = "Recipient: ${txn.recipient}", fontSize = 12.sp, color = Color(0xFF616161))
                }

                Surface(
                    color = statusColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = statusText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "৳ ${"%,.2f".format(txn.amount)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1976D2)
                )

                val time = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).format(Date(txn.timestamp))
                Text(text = "TrxID: ${txn.trxId} • $time", fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun ResellerAgentsList(
    agents: List<UserAccountEntity>,
    onAddBalance: (UserAccountEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Registered Mobile Reseller Agents (${agents.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF424242)
            )
        }

        items(agents, key = { it.mobileNumber }) { agent ->
            AgentCard(
                agent = agent,
                onAddBalance = { onAddBalance(agent) }
            )
        }
    }
}

@Composable
private fun AgentCard(
    agent: UserAccountEntity,
    onAddBalance: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = agent.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = agent.shopName, fontSize = 12.sp, color = Color.Gray)
                }

                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = agent.accountType,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Balances Breakdown
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF9F9F9), RoundedCornerShape(6.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Main Balance", fontSize = 10.sp, color = Color.Gray)
                    Text("৳ ${"%,.2f".format(agent.mainBalance)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Drive Balance", fontSize = 10.sp, color = Color.Gray)
                    Text("৳ ${"%,.2f".format(agent.driveBalance)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SbsOrange)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Bank Balance", fontSize = 10.sp, color = Color.Gray)
                    Text("৳ ${"%,.2f".format(agent.bankBalance)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SbsGreen)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mobile: ${agent.mobileNumber} (${agent.operator})",
                    fontSize = 11.sp,
                    color = Color(0xFF616161)
                )

                Button(
                    onClick = onAddBalance,
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("admin_add_balance_${agent.mobileNumber}")
                ) {
                    Icon(Icons.Default.AddCard, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Balance", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Dialog for Admin to credit balance to an agent
@Composable
fun AdminAddBalanceDialog(
    agent: UserAccountEntity,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, balanceType: String, remarks: String) -> Unit,
    isProcessing: Boolean
) {
    var amountText by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Main") }
    var remarksText by remember { mutableStateOf("Admin Cash Deposit") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Credit Agent Balance",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Text(
                    text = "Agent: ${agent.name} (${agent.mobileNumber})",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Select Balance Account:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Main", "Drive", "Bank").forEach { type ->
                        val isSelected = selectedType == type
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedType = type },
                            label = { Text("$type Balance", fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount (৳)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("admin_amount_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = remarksText,
                    onValueChange = { remarksText = it },
                    label = { Text("Admin Remarks / Reference") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                val amtVal = amountText.toDoubleOrNull() ?: 0.0
                Button(
                    onClick = { onConfirm(amtVal, selectedType, remarksText) },
                    enabled = !isProcessing && amtVal > 0,
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("confirm_admin_add_balance"),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SbsGreen)
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Confirm Credit ৳ ${"%,.2f".format(amtVal)}", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

// Dialog for Admin to reject transaction with reason
@Composable
fun AdminRejectDialog(
    txn: Transaction,
    onDismiss: () -> Unit,
    onConfirm: (reason: String) -> Unit,
    isProcessing: Boolean
) {
    var reasonText by remember { mutableStateOf("Invalid customer number / Gateway busy") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Reject Transaction & Refund",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = SbsRed
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Transaction ${txn.trxId} of ৳ ${"%,.2f".format(txn.amount)} for ${txn.recipient} will be marked as FAILED and the funds will be automatically refunded to agent ${txn.userMobile}.",
                    fontSize = 12.sp,
                    color = Color(0xFF555555),
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = reasonText,
                    onValueChange = { reasonText = it },
                    label = { Text("Reason for Rejection") },
                    modifier = Modifier.fillMaxWidth().testTag("reject_reason_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = { onConfirm(reasonText) },
                        enabled = !isProcessing,
                        colors = ButtonDefaults.buttonColors(containerColor = SbsRed),
                        modifier = Modifier.weight(1f).testTag("confirm_reject_button"),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("Confirm Reject", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
