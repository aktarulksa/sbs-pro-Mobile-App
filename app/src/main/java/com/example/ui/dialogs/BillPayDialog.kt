package com.example.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.SbsPurplePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillPayDialog(
    onDismiss: () -> Unit,
    onSubmit: (provider: String, accountNo: String, amount: Double, month: String, pin: String) -> Unit,
    isProcessing: Boolean,
    errorMessage: String?
) {
    val providers = listOf(
        "DESCO Electricity",
        "DPDC Electricity",
        "BREB Rural Electrification",
        "NESCO Electricity",
        "Titas Gas Postpaid",
        "Dhaka WASA Water"
    )

    var selectedProvider by remember { mutableStateOf(providers.first()) }
    var accountNo by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var monthText by remember { mutableStateOf("October 2026") }
    var pinText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

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
                        text = "Utility Bill Payment",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF212121)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedProvider,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Biller / Organization") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        providers.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p, fontSize = 13.sp) },
                                onClick = {
                                    selectedProvider = p
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = accountNo,
                    onValueChange = { accountNo = it },
                    label = { Text("Customer Account / Meter Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("bill_account_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Bill Amount (৳)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("bill_amount_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = pinText,
                    onValueChange = { pinText = it },
                    label = { Text("Agent PIN") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth().testTag("bill_pin_input")
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                val amtVal = amountText.toDoubleOrNull() ?: 0.0
                Button(
                    onClick = { onSubmit(selectedProvider, accountNo, amtVal, monthText, pinText) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_billpay_button"),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SbsPurplePrimary),
                    enabled = !isProcessing && amtVal > 0 && accountNo.isNotBlank()
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    } else {
                        Text("Pay Bill ৳ ${"%.0f".format(amtVal)}", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
