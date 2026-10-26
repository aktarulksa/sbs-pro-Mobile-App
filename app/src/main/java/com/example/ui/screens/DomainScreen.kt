package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SbsOrange
import com.example.ui.theme.SbsPurpleDark
import com.example.ui.theme.SbsPurplePrimary

@Composable
fun DomainScreen(
    domainInput: String,
    onDomainChange: (String) -> Unit,
    onSubmit: () -> Unit,
    isProcessing: Boolean,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        // Faint watermark background logos matching Screenshot 2
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.06f)
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
                    Text("SMS", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("মোবিক্যাশ", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("এম ক্যাশ", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    Text("TeleTalk", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("FlexiLoad", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                }
            }
        }

        // Center Content Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // SBS Pro Stylized Logo matching Screenshot 2
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4A154B)),
                contentAlignment = Alignment.Center
            ) {
                // Curved "S" monogram
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(54.dp)
                            .height(18.dp)
                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 4.dp))
                            .background(Color.White)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(54.dp)
                            .height(18.dp)
                            .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp, topEnd = 4.dp))
                            .background(SbsOrange)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SBS Pro Title
            Text(
                text = "SBS Pro",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF424242),
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            // "Please Enter Domain" text
            Text(
                text = "Please Enter Domain",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = Color(0xFF555555)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // http:// Domain input line
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.5.dp,
                        color = if (errorMessage != null) MaterialTheme.colorScheme.error else Color(0xFF1A237E),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Domain URL",
                    tint = Color(0xFF5E35B1),
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "http://",
                    fontSize = 15.sp,
                    color = Color(0xFF666666),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.width(4.dp))

                OutlinedTextField(
                    value = domainInput,
                    onValueChange = onDomainChange,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("domain_input"),
                    placeholder = { Text("Domain", color = Color.Gray, fontSize = 15.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { onSubmit() })
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Purple Continue Button matching Screenshot 2
            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("continue_button"),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A154B)
                ),
                enabled = !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Continue",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Preset domains for quick test
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Quick connect:", fontSize = 12.sp, color = Color.Gray)
                SuggestionChip(
                    onClick = { onDomainChange("server.sbspro.com") },
                    label = { Text("server.sbspro.com", fontSize = 11.sp) }
                )
            }
        }
    }
}
