package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.PostEntity
import com.example.data.local.UserEntity
import com.example.ui.screens.wallet.PaymentMethodChip
import com.example.ui.theme.MaxBookBlue

data class BoostPackage(
    val title: String,
    val reach: Int,
    val reachLabel: String,
    val durationDays: Int,
    val priceBdt: Double,
    val isPopular: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoostPostDialog(
    post: PostEntity,
    currentUser: UserEntity?,
    onDismiss: () -> Unit,
    onConfirmBoost: (budget: Double, reach: Int, durationDays: Int, paymentMethod: String, trxId: String) -> Unit,
    onOpenWallet: () -> Unit
) {
    val packages = listOf(
        BoostPackage("🌟 স্টারটার প্যাকেজ", 5000, "৫,০০০+ রিচ", 1, 200.0),
        BoostPackage("🚀 পপুলার ভাইরাল", 15000, "১৫,০০০+ রিচ", 3, 500.0, isPopular = true),
        BoostPackage("👑 প্রো সেলিব্রিটি", 40000, "৪০,০০০+ রিচ", 7, 1000.0)
    )

    var selectedPkgIndex by remember { mutableStateOf(1) }
    var paymentMethod by remember { mutableStateOf("WALLET") } // WALLET, BKASH, NAGAD, ROCKET
    var trxId by remember { mutableStateOf("") }

    val currentPkg = packages[selectedPkgIndex]
    val userBalance = currentUser?.balance ?: 0.0
    val hasEnoughBalance = userBalance >= currentPkg.priceBdt

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
                .testTag("boost_post_dialog_modal")
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
                                .background(Color(0xFF8B5CF6).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.RocketLaunch,
                                contentDescription = "Boost",
                                tint = Color(0xFF8B5CF6)
                            )
                        }
                        Column {
                            Text(
                                text = "পোস্ট বুস্ট করুন (Boost Post)",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "১০ গুণ বেশি ফলোয়ার ও লাইক পান",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Post Snippet Preview
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Article, contentDescription = null, tint = MaxBookBlue)
                        Text(
                            text = post.content.take(60) + if (post.content.length > 60) "..." else "",
                            fontSize = 12.sp,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "১. বুস্টিং প্যাকেজ নির্বাচন করুন:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                // Package List
                packages.forEachIndexed { index, pkg ->
                    val isSelected = selectedPkgIndex == index
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) Color(0xFF8B5CF6).copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        border = BorderStroke(
                            if (isSelected) 2.dp else 1.dp,
                            if (isSelected) Color(0xFF8B5CF6) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPkgIndex = index }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(text = pkg.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    if (pkg.isPopular) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFFF59E0B)
                                        ) {
                                            Text(
                                                text = "জনপ্রিয় 🔥",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "টার্গেট: ${pkg.reachLabel} • মেয়াদ: ${pkg.durationDays} দিন",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "৳ ${pkg.priceBdt.toInt()}",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = Color(0xFF8B5CF6)
                            )
                        }
                    }
                }

                Text(
                    text = "২. পেমেন্ট মেথড:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                // Payment options (Wallet / bKash / Nagad / Rocket)
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
                        label = { Text("$paymentMethod TrxID প্রদান করুন") },
                        placeholder = { Text("e.g. TR8291823") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else if (!hasEnoughBalance) {
                    Text(
                        text = "⚠️ ওয়ালেটে পর্যাপ্ত ব্যালেন্স নেই। অনুগ্রহ করে টাকা ডিপোজিট করুন।",
                        color = Color(0xFFEF4444),
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = {
                        if (paymentMethod == "WALLET") {
                            if (hasEnoughBalance) {
                                onConfirmBoost(currentPkg.priceBdt, currentPkg.reach, currentPkg.durationDays, "WALLET", "")
                            } else {
                                onDismiss()
                                onOpenWallet()
                            }
                        } else {
                            if (trxId.isNotBlank()) {
                                onConfirmBoost(currentPkg.priceBdt, currentPkg.reach, currentPkg.durationDays, paymentMethod, trxId)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("confirm_boost_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (paymentMethod == "WALLET" && !hasEnoughBalance) Color(0xFFEF4444) else Color(0xFF8B5CF6)
                    )
                ) {
                    Icon(imageVector = Icons.Default.RocketLaunch, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (paymentMethod == "WALLET" && !hasEnoughBalance) "ডিপোজিট করতে ওয়ালেটে যান" else "বুস্ট কনফার্ম করুন (৳ ${currentPkg.priceBdt.toInt()})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
