package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.ServiceType
import com.example.ui.theme.SbsPurplePrimary

@Composable
fun MfsDialog(
    serviceType: ServiceType,
    onDismiss: () -> Unit,
    onSubmit: (mfsName: String, action: String, number: String, amount: Double, pin: String) -> Unit,
    isProcessing: Boolean,
    errorMessage: String?
) {
    val mfsName = when (serviceType) {
        ServiceType.BKASH -> "bKash"
        ServiceType.ROCKET -> "Rocket"
        ServiceType.NAGAD -> "Nagad"
        else -> "MFS"
    }

    var selectedAction by remember { mutableStateOf("Cash In") }
    var numberText by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var pinText by remember { mutableStateOf("") }

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
                        text = "$mfsName Agent Banking",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF212121)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action selector: Cash In / Send Money
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEEEEEE), RoundedCornerShape(8.dp))
                        .padding(3.dp)
                ) {
                    listOf("Cash In", "Send Money", "Cash Out").forEach { action ->
                        val isSelected = selectedAction == action
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) SbsPurplePrimary else Color.Transparent)
                                .clickable { selectedAction = action }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = action,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color(0xFF555555)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = numberText,
                    onValueChange = { numberText = it },
                    label = { Text("Customer $mfsName Number") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth().testTag("mfs_number_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount (৳)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("mfs_amount_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = pinText,
                    onValueChange = { pinText = it },
                    label = { Text("Agent PIN") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth().testTag("mfs_pin_input")
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                val amtVal = amountText.toDoubleOrNull() ?: 0.0
                Button(
                    onClick = { onSubmit(mfsName, selectedAction, numberText, amtVal, pinText) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_mfs_button"),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SbsPurplePrimary),
                    enabled = !isProcessing && amtVal > 0
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    } else {
                        Text("Confirm $selectedAction ৳ ${"%.0f".format(amtVal)}", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
