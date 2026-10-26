package com.example.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.UserAccountEntity
import androidx.compose.material.icons.filled.Lock
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.ui.theme.SbsOrange

@Composable
fun SwitchAccountDialog(
    currentUser: UserAccountEntity?,
    allUsers: List<UserAccountEntity>,
    onSelectUser: (mobile: String) -> Unit,
    onAddNewAccount: () -> Unit,
    onOpenThemeDialog: () -> Unit,
    onOpenAdminPanel: () -> Unit = {},
    onUnlockAdmin: (pin: String) -> Boolean = { false },
    onLogout: () -> Unit,
    onDismiss: () -> Unit
) {
    var showAdminPinDialog by remember { mutableStateOf(false) }
    var adminPinInput by remember { mutableStateOf("") }
    var adminPinError by remember { mutableStateOf<String?>(null) }
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Agent Account Profile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF212121)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (currentUser != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(currentUser.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Mobile: ${currentUser.mobileNumber}", fontSize = 12.sp, color = SbsOrange, fontWeight = FontWeight.SemiBold)
                                Text("${currentUser.shopName} • ${currentUser.accountType}", fontSize = 11.sp, color = Color.Gray)
                                Text("Balance: ৳ ${"%,.2f".format(currentUser.mainBalance)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Switch Mobile Account:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

                Spacer(modifier = Modifier.height(6.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 140.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(allUsers) { user ->
                        val isCurrent = user.mobileNumber == currentUser?.mobileNumber
                        val isTargetAdmin = user.isAdmin
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    if (isTargetAdmin && currentUser?.isAdmin != true) {
                                        adminPinInput = ""
                                        adminPinError = null
                                        showAdminPinDialog = true
                                    } else {
                                        onSelectUser(user.mobileNumber)
                                    }
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isTargetAdmin) Icons.Default.AdminPanelSettings else Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = if (isTargetAdmin) MaterialTheme.colorScheme.primary else Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (isTargetAdmin) "${user.mobileNumber} (Admin HQ)" else user.mobileNumber,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "${user.name} • ${user.accountType}",
                                        fontSize = 11.sp,
                                        color = if (isTargetAdmin) MaterialTheme.colorScheme.primary else Color.Gray
                                    )
                                }
                            }
                            if (isCurrent) {
                                Icon(Icons.Default.Check, contentDescription = "Active", tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Admin Control Panel Button - ONLY shown to logged-in Administrator
                if (currentUser?.isAdmin == true) {
                    OutlinedButton(
                        onClick = onOpenAdminPanel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_admin_panel_button"),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Admin Approval & Control Console")
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                } else {
                    // Discreet admin login option for HQ administrators on agent device
                    OutlinedButton(
                        onClick = {
                            adminPinInput = ""
                            adminPinError = null
                            showAdminPinDialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_admin_unlock_button"),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF616161))
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Admin Console Login (PIN Required)", fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                OutlinedButton(
                    onClick = onOpenThemeDialog,
                    modifier = Modifier.fillMaxWidth().testTag("profile_change_color_button"),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Customize App Theme & Colors")
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedButton(
                    onClick = onAddNewAccount,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add / Create Another Mobile Account")
                }

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("logout_button"),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Log Out", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showAdminPinDialog) {
        AlertDialog(
            onDismissRequest = { showAdminPinDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Admin Access Verification", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        text = "The Admin Control Panel is restricted to system administrators. Enter Server Master PIN to authenticate (Default: 7788).",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = adminPinInput,
                        onValueChange = { if (it.length <= 6) adminPinInput = it },
                        label = { Text("Admin Master PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("admin_pin_input")
                    )
                    if (adminPinError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(adminPinError!!, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val unlocked = onUnlockAdmin(adminPinInput)
                        if (unlocked) {
                            showAdminPinDialog = false
                            onDismiss()
                        } else {
                            adminPinError = "Incorrect Administrator PIN. Access Denied."
                        }
                    },
                    modifier = Modifier.testTag("admin_pin_unlock_confirm_button")
                ) {
                    Text("Unlock & Open Console")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdminPinDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
