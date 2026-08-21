package com.example.ui.screens.monetization

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.UserEntity
import com.example.ui.screens.wallet.PaymentMethodChip
import com.example.ui.theme.MaxBookBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonetizationDialog(
    currentUser: UserEntity?,
    onDismiss: () -> Unit,
    onApply: (payoutMethod: String, payoutNumber: String) -> Unit,
    onClaimDailyReward: () -> Unit,
    onOpenWallet: () -> Unit
) {
    val followersCount = currentUser?.followersCount ?: 0
    val targetFollowers = 5000
    val isEligible = followersCount >= targetFollowers
    val isMonetized = currentUser?.isMonetized == true

    val progress = (followersCount.toFloat() / targetFollowers.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")

    var selectedPayoutMethod by remember { mutableStateOf("BKASH") }
    var payoutNumber by remember { mutableStateOf(currentUser?.phone ?: "") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .padding(vertical = 16.dp)
                .testTag("monetization_dialog_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
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
                                .background(Color(0xFFF59E0B).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "Monetization",
                                tint = Color(0xFFF59E0B)
                            )
                        }
                        Column {
                            Text(
                                text = "ক্রিয়েটর ইনকাম হাব (Monetization)",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "৫,০০০ ফলোয়ার হলেই ইনকাম চালু",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Status Hero Banner
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (isMonetized) {
                                            Brush.horizontalGradient(listOf(Color(0xFF059669), Color(0xFF10B981)))
                                        } else if (isEligible) {
                                            Brush.horizontalGradient(listOf(Color(0xFFD97706), Color(0xFFF59E0B)))
                                        } else {
                                            Brush.horizontalGradient(listOf(Color(0xFF374151), Color(0xFF4B5563)))
                                        }
                                    )
                                    .padding(20.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (isMonetized) "ক্রিয়েটর স্ট্যাটাস: সক্রিয় ✅" else if (isEligible) "মনিটাইজেশনের জন্য প্রস্তুত 🎉" else "যোগ্যতা অর্জনের প্রক্রিয়া চলছে ⏳",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color.White.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "৫K রুল",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = if (isMonetized) "মোট উপার্জন: ৳ ${String.format("%,.2f", currentUser?.totalEarnings ?: 0.0)}" else "${String.format("%,d", followersCount)} / 5,000 ফলোয়ার",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    LinearProgressIndicator(
                                        progress = { animatedProgress },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(CircleShape),
                                        color = Color.White,
                                        trackColor = Color.White.copy(alpha = 0.3f),
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = if (isMonetized) "প্রতিটি লাইক, কমেন্ট ও ভিউ থেকে নিয়মিত ইনকাম আপনার ওয়ালেটে জমা হচ্ছে" else if (isEligible) "আপনার ৫,০০০ ফলোয়ার পূর্ণ হয়েছে! নিচে আবেদন করে ইনকাম শুরু করুন।" else "ইনকাম চালু করতে আরও ${String.format("%,d", targetFollowers - followersCount)} জন ফলোয়ার প্রয়োজন",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }
                    }

                    if (isMonetized) {
                        // Active Creator Earning Tools
                        item {
                            Text(
                                text = "ক্রিয়েটর বেনিফিট ও দৈনিক বোনাস:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }

                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(text = "🎁 দৈনিক এনগেজমেন্ট রিওয়ার্ড", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                            Text(text = "প্রতিদিন ফ্রিতে ৳ ৫০ ক্রিয়েটর বোনাস ক্লেইম করুন", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = onClaimDailyReward,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("claim_daily_bonus_btn"),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                    ) {
                                        Icon(imageVector = Icons.Default.CardGiftcard, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("ক্লেইম করুন (৳ ৫০)", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(text = "আপনার ইনকামের পরিসংখ্যান:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        EarningMetric("পোস্ট ভিউ ইনকাম", "৳ ${String.format("%,.0f", (currentUser?.totalEarnings ?: 0.0) * 0.6)}")
                                        EarningMetric("এনগেজমেন্ট বোনাস", "৳ ${String.format("%,.0f", (currentUser?.totalEarnings ?: 0.0) * 0.4)}")
                                        EarningMetric("ওয়ালেট ব্যালেন্স", "৳ ${String.format("%,.0f", currentUser?.balance ?: 0.0)}")
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    OutlinedButton(
                                        onClick = {
                                            onDismiss()
                                            onOpenWallet()
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("উইথড্র করতে ওয়ালেটে যান (বিকাশ / নগদ / রকেট)")
                                    }
                                }
                            }
                        }
                    } else if (isEligible) {
                        // Apply for Monetization Form
                        item {
                            Text(
                                text = "মনিটাইজেশন পেমেন্ট তথ্য দিন:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }

                        item {
                            Text(text = "উপার্জনের টাকা যে মাধ্যমে গ্রহণ করবেন:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                PaymentMethodChip(
                                    name = "bKash (বিকাশ)",
                                    selected = selectedPayoutMethod == "BKASH",
                                    brandColor = Color(0xFFD12053),
                                    onClick = { selectedPayoutMethod = "BKASH" },
                                    modifier = Modifier.weight(1f)
                                )
                                PaymentMethodChip(
                                    name = "Nagad (নগদ)",
                                    selected = selectedPayoutMethod == "NAGAD",
                                    brandColor = Color(0xFFF7941D),
                                    onClick = { selectedPayoutMethod = "NAGAD" },
                                    modifier = Modifier.weight(1f)
                                )
                                PaymentMethodChip(
                                    name = "Rocket (রকেট)",
                                    selected = selectedPayoutMethod == "ROCKET",
                                    brandColor = Color(0xFF8C3494),
                                    onClick = { selectedPayoutMethod = "ROCKET" },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = payoutNumber,
                                onValueChange = { payoutNumber = it },
                                label = { Text("আপনার $selectedPayoutMethod অ্যাকাউন্ট নম্বর") },
                                leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = {
                                    if (payoutNumber.isNotBlank()) {
                                        onApply(selectedPayoutMethod, payoutNumber)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("apply_monetization_btn"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                            ) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("মনিটাইজেশন সক্রিয় করুন", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    } else {
                        // Guide to reach 5K followers
                        item {
                            Text(
                                text = "কীভাবে দ্রুত ৫,০০০ ফলোয়ার পূর্ণ করবেন?",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }

                        item {
                            MonetizationTipCard(
                                icon = Icons.Default.RocketLaunch,
                                title = "পোস্ট বুস্ট করুন",
                                desc = "আপনার সেরা পোস্টগুলো বুস্ট করে সহজেই হাজার হাজার মানুষের কাছে পৌঁছান।"
                            )
                        }

                        item {
                            MonetizationTipCard(
                                icon = Icons.Default.Verified,
                                title = "ব্লু ভেরিফাইড ব্যাজ নিন",
                                desc = "ভেরিফাইড প্রোফাইল অন্যদের কাছে বেশি বিশ্বাসযোগ্য এবং অ্যালগরিদম অগ্রাধিকার পায়।"
                            )
                        }

                        item {
                            MonetizationTipCard(
                                icon = Icons.Default.VideoLibrary,
                                title = "ভিডিও ও রিলস আপলোড করুন",
                                desc = "ভিডিও কন্টেন্ট সবচেয়ে দ্রুত ভাইরাল হয় এবং ফলোয়ার বৃদ্ধি করে।"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EarningMetric(title: String, amount: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = amount, fontWeight = FontWeight.Black, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
        Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun MonetizationTipCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, desc: String) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(MaxBookBlue.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = MaxBookBlue, modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(text = desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
