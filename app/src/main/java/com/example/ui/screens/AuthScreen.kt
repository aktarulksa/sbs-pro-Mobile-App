package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserAccountEntity
import com.example.ui.theme.SbsOrange
import com.example.ui.theme.SbsPurpleDark
import com.example.ui.theme.SbsPurplePrimary

@Composable
fun AuthScreen(
    savedUsers: List<UserAccountEntity>,
    onLoginSubmit: (mobile: String, pin: String) -> Unit,
    onRegisterSubmit: (
        mobile: String,
        name: String,
        shopName: String,
        pin: String,
        initialBalance: Double,
        tier: String
    ) -> Unit,
    isProcessing: Boolean,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    var isRegisterMode by remember { mutableStateOf(false) }

    // Login state
    var loginMobile by remember { mutableStateOf("01712345678") }
    var loginPin by remember { mutableStateOf("1234") }
    var showPin by remember { mutableStateOf(false) }

    // Register state
    var regMobile by remember { mutableStateOf("") }
    var regName by remember { mutableStateOf("") }
    var regShop by remember { mutableStateOf("") }
    var regPin by remember { mutableStateOf("") }
    var regConfirmPin by remember { mutableStateOf("") }
    var regBalance by remember { mutableStateOf(25000.0) }
    var regTier by remember { mutableStateOf("Master Reseller") }

    val detectedOperator = remember(if (isRegisterMode) regMobile else loginMobile) {
        val num = if (isRegisterMode) regMobile else loginMobile
        when {
            num.startsWith("017") || num.startsWith("013") -> "Grameenphone (GP)"
            num.startsWith("018") -> "Robi Axiata"
            num.startsWith("019") || num.startsWith("014") -> "Banglalink"
            num.startsWith("015") -> "TeleTalk BD"
            num.startsWith("016") -> "Airtel"
            else -> null
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Faint watermark background matching Screenshot 2
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.04f)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("bKash", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("UCash", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    Text("রকেট", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("FlexiLoad", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("মোবিক্যাশ", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("TeleTalk", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // SBS Pro Stylized Monogram
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(SbsPurplePrimary),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(42.dp)
                            .height(14.dp)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 3.dp))
                            .background(Color.White)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Box(
                        modifier = Modifier
                            .width(42.dp)
                            .height(14.dp)
                            .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp, topEnd = 3.dp))
                            .background(SbsOrange)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "SBS Pro",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )

            Text(
                text = "Mobile Account Authentication",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Switch Mode Tab: Login vs Create Account
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEEEEEE), RoundedCornerShape(24.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (!isRegisterMode) SbsPurplePrimary else Color.Transparent)
                        .clickable { isRegisterMode = false }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Login",
                        color = if (!isRegisterMode) Color.White else Color(0xFF666666),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isRegisterMode) SbsPurplePrimary else Color.Transparent)
                        .clickable { isRegisterMode = true }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Create Account",
                        color = if (isRegisterMode) Color.White else Color(0xFF666666),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!isRegisterMode) {
                // LOGIN WITH MOBILE NUMBER
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Login with Mobile Number",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF333333)
                        )

                        // Mobile Number Input
                        OutlinedTextField(
                            value = loginMobile,
                            onValueChange = { loginMobile = it },
                            label = { Text("Mobile Number (e.g. 01712345678)") },
                            leadingIcon = {
                                Text(
                                    "🇧🇩 +88",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_mobile_input")
                        )

                        if (detectedOperator != null) {
                            Text(
                                text = "Detected SIM: $detectedOperator",
                                fontSize = 11.sp,
                                color = SbsOrange,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // PIN Input
                        OutlinedTextField(
                            value = loginPin,
                            onValueChange = { if (it.length <= 6) loginPin = it },
                            label = { Text("4-Digit Security PIN") },
                            visualTransformation = if (showPin) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_pin_input"),
                            trailingIcon = {
                                IconButton(onClick = { showPin = !showPin }) {
                                    Icon(
                                        imageVector = if (showPin) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = Color.Gray
                                    )
                                }
                            }
                        )

                        if (errorMessage != null) {
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }

                        Button(
                            onClick = { onLoginSubmit(loginMobile, loginPin) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("login_button"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SbsPurplePrimary),
                            enabled = !isProcessing && loginMobile.length >= 10 && loginPin.isNotEmpty()
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                            } else {
                                Text("Log In", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                            }
                        }

                        // Quick fill saved accounts
                        if (savedUsers.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Saved Accounts on Device:", fontSize = 11.sp, color = Color.Gray)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                savedUsers.forEach { user ->
                                    val roleTag = if (user.isAdmin) "Admin" else "Agent"
                                    AssistChip(
                                        onClick = {
                                            loginMobile = user.mobileNumber
                                            loginPin = user.pin
                                        },
                                        label = { Text("${user.mobileNumber} ($roleTag: ${user.name})", fontSize = 11.sp) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = if (user.isAdmin) Icons.Default.AdminPanelSettings else Icons.Default.AccountCircle,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // CREATE USER ACCOUNT (ONLY MOBILE NUMBER & PROFILE)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Create Account with Mobile Number",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF333333)
                        )

                        OutlinedTextField(
                            value = regMobile,
                            onValueChange = { regMobile = it },
                            label = { Text("Mobile Number (11 digits)") },
                            leadingIcon = {
                                Text(
                                    "🇧🇩 +88",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_mobile_input")
                        )

                        if (detectedOperator != null) {
                            Text(
                                text = "Operator: $detectedOperator",
                                fontSize = 11.sp,
                                color = SbsOrange,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        OutlinedTextField(
                            value = regName,
                            onValueChange = { regName = it },
                            label = { Text("Agent / Owner Full Name") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_name_input")
                        )

                        OutlinedTextField(
                            value = regShop,
                            onValueChange = { regShop = it },
                            label = { Text("Shop / Business Name") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_shop_input")
                        )

                        OutlinedTextField(
                            value = regPin,
                            onValueChange = { if (it.length <= 6) regPin = it },
                            label = { Text("Set 4-Digit Security PIN") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_pin_input")
                        )

                        OutlinedTextField(
                            value = regConfirmPin,
                            onValueChange = { if (it.length <= 6) regConfirmPin = it },
                            label = { Text("Confirm 4-Digit Security PIN") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_confirm_pin_input")
                        )

                        Text("Account Tier:", fontSize = 12.sp, color = Color.Gray)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Retailer", "Reseller Agent", "Master Reseller").forEach { tier ->
                                FilterChip(
                                    selected = regTier == tier,
                                    onClick = { regTier = tier },
                                    label = { Text(tier, fontSize = 11.sp) }
                                )
                            }
                        }

                        Text("Initial Demo Capital:", fontSize = 12.sp, color = Color.Gray)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(10000.0, 25000.0, 50000.0).forEach { bal ->
                                FilterChip(
                                    selected = regBalance == bal,
                                    onClick = { regBalance = bal },
                                    label = { Text("৳ ${bal.toInt()}", fontSize = 11.sp) }
                                )
                            }
                        }

                        if (errorMessage != null) {
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }

                        Button(
                            onClick = {
                                if (regPin != regConfirmPin) {
                                    // PIN mismatch
                                } else {
                                    onRegisterSubmit(regMobile, regName, regShop, regPin, regBalance, regTier)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("register_button"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SbsOrange),
                            enabled = !isProcessing && regMobile.length >= 10 && regPin.length >= 4 && regPin == regConfirmPin
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                            } else {
                                Text("Create Account", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
