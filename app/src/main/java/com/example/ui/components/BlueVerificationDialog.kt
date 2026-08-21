package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.UserEntity
import com.example.ui.screens.wallet.PaymentMethodChip
import com.example.ui.theme.MaxBookBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlueVerificationDialog(
    currentUser: UserEntity?,
    onDismiss: () -> Unit,
    onBuyOrApply: (paymentMethod: String, trxId: String) -> Unit,
    onOpenWallet: () -> Unit
) {
    val fee = 500.0
    val userBalance = currentUser?.balance ?: 0.0
    val hasEnoughBalance = userBalance >= fee
    val isAlreadyVerified = currentUser?.isVerified == true

    var paymentMethod by remember { mutableStateOf("WALLET") } // WALLET, BKASH, NAGAD, ROCKET
    var trxId by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(16.dp)
                .testTag("blue_verification_dialog_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaxBookBlue.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = MaxBookBlue
                            )
                        }
                        Column {
                            Text(
                                text = "MaxBook ব্লু ভেরিফাইড (Blue Badge)",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "অফিসিয়াল ভেরিফিকেশন ব্যাজ সক্রিয় করুন",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Hero Badge Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(listOf(Color(0xFF1D4ED8), Color(0xFF3B82F6)))
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                            Column {
                                Text(
                                    text = if (isAlreadyVerified) "আপনার অ্যাকাউন্ট ইতিমধ্যেই ভেরিফাইড ✅" else "ব্লু ব্যাজ ফি: ৳ ৫০০ (এককালীন / মাসিক)",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "প্রোফাইল নামের পাশে স্থায়ী ব্লু চেকমার্ক ও অগ্রাধিকার",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // Benefits List
                Text(text = "ব্লু ভেরিফিকেশনের সুবিধাসমূহ:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    BenefitRow("🌟 নামের পাশে ভেরিফাইড নীল টিক চিহ্ন (Blue Checkmark)")
                    BenefitRow("🚀 ফিড ও কমেন্টে শীর্ষ স্থানে অগ্রাধিকার ও উচ্চতর রিচ")
                    BenefitRow("🛡️ ভুয়া অ্যাকাউন্ট ও প্রতারণা থেকে সুরক্ষা")
                    BenefitRow("💰 ৫K ফলোয়ার সম্পন্ন হলে দ্রুত মনিটাইজেশন অনুমোদন")
                }

                if (!isAlreadyVerified) {
                    Text(text = "পেমেন্ট মাধ্যম নির্বাচন করুন:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PaymentMethodChip(
                            name = "ওয়ালেট (৳${userBalance.toInt()})",
                            selected = paymentMethod == "WALLET",
                            brandColor = Color(0xFF10B981),
                            onClick = { paymentMethod = "WALLET" },
                            modifier = Modifier.weight(1f)
                        )
                        PaymentMethodChip(
                            name = "bKash",
                            selected = paymentMethod == "BKASH",
                            brandColor = Color(0xFFD12053),
                            onClick = { paymentMethod = "BKASH" },
                            modifier = Modifier.weight(1f)
                        )
                        PaymentMethodChip(
                            name = "Nagad",
                            selected = paymentMethod == "NAGAD",
                            brandColor = Color(0xFFF7941D),
                            onClick = { paymentMethod = "NAGAD" },
                            modifier = Modifier.weight(1f)
                        )
                        PaymentMethodChip(
                            name = "Rocket",
                            selected = paymentMethod == "ROCKET",
                            brandColor = Color(0xFF8C3494),
                            onClick = { paymentMethod = "ROCKET" },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (paymentMethod != "WALLET") {
                        OutlinedTextField(
                            value = trxId,
                            onValueChange = { trxId = it },
                            label = { Text("$paymentMethod TrxID লিখুন (৳ ৫০০ পাঠানোর পর)") },
                            placeholder = { Text("e.g. BK8912389") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else if (!hasEnoughBalance) {
                        Text(
                            text = "⚠️ ওয়ালেটে পর্যাপ্ত ব্যালেন্স নেই (৳ ৫০০ প্রয়োজন)। ডিপোজিট করুন।",
                            color = Color(0xFFEF4444),
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = {
                            if (paymentMethod == "WALLET") {
                                if (hasEnoughBalance) {
                                    onBuyOrApply("WALLET", "")
                                } else {
                                    onDismiss()
                                    onOpenWallet()
                                }
                            } else {
                                if (trxId.isNotBlank()) {
                                    onBuyOrApply(paymentMethod, trxId)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("confirm_buy_blue_badge_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (paymentMethod == "WALLET" && !hasEnoughBalance) Color(0xFFEF4444) else MaxBookBlue
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Verified, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (paymentMethod == "WALLET" && !hasEnoughBalance) "ডিপোজিট করতে ওয়ালেটে যান" else "ব্লু ব্যাজ কিনুন (৳ ৫০০)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                } else {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("ঠিক আছে")
                    }
                }
            }
        }
    }
}

@Composable
fun BenefitRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
        Text(text = text, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}
