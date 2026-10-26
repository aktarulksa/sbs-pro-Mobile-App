package com.example.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.UserAccountEntity
import com.example.ui.theme.SbsOrange
import com.example.ui.theme.SbsPurplePrimary

@Composable
fun SecurityInfoDialog(
    userAccount: UserAccountEntity?,
    onDismiss: () -> Unit,
    onSwitchDomain: () -> Unit
) {
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = SbsOrange, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SBS Pro Security Status", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                val domain = userAccount?.domain ?: "server.sbspro.com"
                val name = userAccount?.name ?: "Guest"
                val mobile = userAccount?.mobileNumber ?: "N/A"
                val tier = userAccount?.accountType ?: "Reseller"

                InfoItem(icon = Icons.Default.Dns, label = "Server Domain", value = "http://$domain")
                InfoItem(icon = Icons.Default.Person, label = "Reseller Account", value = "$name ($tier)")
                InfoItem(icon = Icons.Default.Phone, label = "Registered Mobile", value = mobile)
                InfoItem(icon = Icons.Default.Lock, label = "Security State", value = "Device Bound & SSL Encrypted")

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        onDismiss()
                        onSwitchDomain()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("switch_domain_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Change Server Domain / Logout", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun InfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = SbsPurplePrimary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(label, fontSize = 11.sp, color = Color.Gray)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF333333))
        }
    }
}
