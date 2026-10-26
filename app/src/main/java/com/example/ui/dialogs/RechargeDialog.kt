package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.Operator
import com.example.model.PackageOffer
import com.example.model.RechargeNumberType
import com.example.ui.components.OperatorBadge
import com.example.ui.theme.SbsOrange
import com.example.ui.theme.SbsPurplePrimary

@Composable
fun RechargeDialog(
    operator: Operator,
    packageOffers: List<PackageOffer>,
    onDismiss: () -> Unit,
    onSubmitRecharge: (
        number: String,
        type: RechargeNumberType,
        amount: Double,
        pin: String,
        selectedPackage: PackageOffer?
    ) -> Unit,
    isProcessing: Boolean,
    errorMessage: String?
) {
    var recipientNumber by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(RechargeNumberType.PREPAID) }
    var amountText by remember { mutableStateOf("") }
    var pinText by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) } // 0: Regular, 1: Drive Offers
    var selectedPackage by remember { mutableStateOf<PackageOffer?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(12.dp)),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header with operator badge & close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE65100)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = operator.code,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "${operator.name} Recharge",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF212121)
                            )
                            Text(
                                text = "FlexiLoad / EasyLoad Portal",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_recharge_dialog")) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Number Type Selector (Prepaid, Postpaid, Skitto)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    RechargeNumberType.values().forEach { type ->
                        val isSelected = selectedType == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) SbsPurplePrimary else Color.Transparent)
                                .clickable { selectedType = type }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = type.label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color(0xFF555555)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Recipient Mobile Input
                OutlinedTextField(
                    value = recipientNumber,
                    onValueChange = { recipientNumber = it },
                    label = { Text("Recipient Mobile Number") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = SbsPurplePrimary)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("recharge_number_input")
                )

                // Quick contacts chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("01712-449102", "01823-991204", "01911-300412", "01552-881920").forEach { num ->
                        AssistChip(
                            onClick = { recipientNumber = num },
                            label = { Text(num, fontSize = 10.sp) },
                            modifier = Modifier.height(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Mode Tabs: Regular Amount vs Special / Drive Packs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = SbsPurplePrimary
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            selectedPackage = null
                        },
                        text = { Text("Regular Amount", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocalOffer, contentDescription = null, modifier = Modifier.size(14.dp), tint = SbsOrange)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Drive Offers", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SbsOrange)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (selectedTab == 0) {
                    // Regular Amount input & Preset chips
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = {
                            amountText = it
                            selectedPackage = null
                        },
                        label = { Text("Recharge Amount (৳)") },
                        leadingIcon = { Text("৳", fontWeight = FontWeight.Bold, color = SbsOrange, fontSize = 18.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("recharge_amount_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Quick Amount:", fontSize = 11.sp, color = Color.Gray)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf(20, 50, 100, 200, 500, 1000).forEach { amt ->
                            SuggestionChip(
                                onClick = { amountText = amt.toString() },
                                label = { Text("৳$amt", fontSize = 11.sp) }
                            )
                        }
                    }
                } else {
                    // Drive / Special Offers List
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("drive_offers_list"),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(packageOffers) { offer ->
                            val isSelected = selectedPackage?.id == offer.id
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedPackage = offer
                                        amountText = offer.price.toInt().toString()
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFFFFF3E0) else Color(0xFFF9F9F9)
                                ),
                                border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SbsOrange)) else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(offer.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(
                                            "${offer.dataVolume} • ${offer.minutes} • ${offer.validity}",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                        if (offer.cashback > 0) {
                                            Text(
                                                "Cashback: ৳ ${offer.cashback.toInt()}",
                                                fontSize = 11.sp,
                                                color = Color(0xFF2E7D32),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Text(
                                        "৳ ${offer.price.toInt()}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = SbsOrange
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // PIN Input
                OutlinedTextField(
                    value = pinText,
                    onValueChange = { if (it.length <= 6) pinText = it },
                    label = { Text("Agent PIN (Default: 1234)") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("recharge_pin_input")
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Commission Note & Submit Button
                val amtVal = amountText.toDoubleOrNull() ?: 0.0
                val commEstimate = if (selectedPackage != null && selectedPackage!!.cashback > 0) {
                    selectedPackage!!.cashback
                } else {
                    amtVal * 0.028
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Commission:", fontSize = 12.sp, color = Color.Gray)
                    Text("৳ ${"%.2f".format(commEstimate)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        onSubmitRecharge(recipientNumber, selectedType, amtVal, pinText, selectedPackage)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_recharge_button"),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SbsPurplePrimary),
                    enabled = !isProcessing && amtVal > 0 && recipientNumber.isNotBlank()
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    } else {
                        Text("Confirm Recharge ৳ ${"%.0f".format(amtVal)}", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
