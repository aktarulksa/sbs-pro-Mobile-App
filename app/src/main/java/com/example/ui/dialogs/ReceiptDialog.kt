package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.material.icons.filled.HourglassTop
import com.example.model.Transaction
import com.example.model.TransactionStatus
import com.example.ui.theme.SbsOrange
import com.example.ui.theme.SbsPurplePrimary
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReceiptDialog(
    transaction: Transaction,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val isPending = transaction.status == TransactionStatus.PENDING
                val isFailed = transaction.status == TransactionStatus.FAILED

                // Status Icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isPending -> Color(0xFFFFF3E0)
                                isFailed -> Color(0xFFFFEBEE)
                                else -> Color(0xFFE8F5E9)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when {
                            isPending -> Icons.Default.HourglassTop
                            isFailed -> Icons.Default.Close
                            else -> Icons.Default.Check
                        },
                        contentDescription = "Status",
                        tint = when {
                            isPending -> SbsOrange
                            isFailed -> Color(0xFFC62828)
                            else -> Color(0xFF2E7D32)
                        },
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = when {
                        isPending -> "Request Submitted (Pending)"
                        isFailed -> "Transaction Rejected"
                        else -> "Transaction Successful!"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = when {
                        isPending -> SbsOrange
                        isFailed -> Color(0xFFC62828)
                        else -> Color(0xFF2E7D32)
                    }
                )

                Text(
                    text = when {
                        isPending -> "Awaiting Admin Approval & Verification"
                        isFailed -> "Funds refunded to your balance"
                        else -> "SBS Pro Digital Billing System"
                    },
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(12.dp))

                ReceiptRow(
                    label = "Approval Status",
                    value = when {
                        isPending -> "PENDING (Admin Approval)"
                        isFailed -> "REJECTED (Refunded)"
                        else -> "APPROVED"
                    },
                    isBold = true,
                    valueColor = when {
                        isPending -> SbsOrange
                        isFailed -> Color(0xFFC62828)
                        else -> Color(0xFF2E7D32)
                    }
                )
                ReceiptRow(label = "Transaction ID", value = transaction.trxId, isBold = true)
                ReceiptRow(label = "Service / Operator", value = transaction.operatorOrProvider)
                ReceiptRow(label = "Recipient Number", value = transaction.recipient)
                ReceiptRow(label = "Amount", value = "৳ ${"%,.2f".format(transaction.amount)}", isBold = true, valueColor = Color(0xFF1976D2))
                ReceiptRow(label = "Commission", value = "+ ৳ ${"%.2f".format(transaction.commission)}", valueColor = Color(0xFF2E7D32))
                ReceiptRow(label = "Remaining Balance", value = "৳ ${"%,.2f".format(transaction.balanceAfter)}")
                val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm:ss a", Locale.getDefault()).format(Date(transaction.timestamp))
                ReceiptRow(label = "Date & Time", value = dateStr)

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
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share")
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("receipt_done_button"),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Done", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    valueColor: Color = Color(0xFF333333)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = Color(0xFF757575))
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = valueColor,
            textAlign = TextAlign.End
        )
    }
}
