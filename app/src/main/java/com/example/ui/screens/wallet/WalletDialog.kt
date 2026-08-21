package com.example.ui.screens.wallet

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.UserEntity
import com.example.data.local.WalletTransactionEntity
import com.example.ui.components.formatTimeAgo
import com.example.ui.theme.MaxBookBlue

// Official Payment Method Brand Colors
val BkashPink = Color(0xFFD12053)
val NagadOrange = Color(0xFFF7941D)
val RocketPurple = Color(0xFF8C3494)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletDialog(
    currentUser: UserEntity?,
    userTransactions: List<WalletTransactionEntity>,
    onDismiss: () -> Unit,
    onDeposit: (amount: Double, method: String, senderNumber: String, trxId: String) -> Unit,
    onWithdraw: (amount: Double, method: String, recipientNumber: String) -> Unit
) {
    val context = LocalContext.current
    var activeTab by remember { mutableStateOf(0) } // 0: Overview/History, 1: Deposit, 2: Withdraw

    // Deposit Form State
    var depositMethod by remember { mutableStateOf("BKASH") } // BKASH, NAGAD, ROCKET
    var depositAmount by remember { mutableStateOf("500") }
    var senderNumber by remember { mutableStateOf(currentUser?.phone ?: "") }
    var transactionId by remember { mutableStateOf("") }

    // Withdraw Form State
    var withdrawMethod by remember { mutableStateOf("BKASH") }
    var withdrawAmount by remember { mutableStateOf("500") }
    var recipientNumber by remember { mutableStateOf(currentUser?.phone ?: "") }

    val quickAmounts = listOf("100", "200", "500", "1000", "2000", "5000")

    val paymentNumbers = mapOf(
        "BKASH" to "01712-345678 (Merchant / Send Money)",
        "NAGAD" to "01812-345678 (Merchant / Send Money)",
        "ROCKET" to "01912-345678 (Personal / Cash Out)"
    )

    fun copyToClipboard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("MaxBook Payment Number", text.split(" ")[0])
        clipboard.setPrimaryClip(clip)
    }

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
                .testTag("wallet_dialog_modal")
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
                                .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = "Wallet",
                                tint = Color(0xFF10B981)
                            )
                        }
                        Column {
                            Text(
                                text = "আমার ওয়ালেট (Wallet)",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "বিকাশ • নগদ • রকেট ব্যালেন্স",
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

                // Balance Hero Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF1E3A8A), Color(0xFF3B82F6))
                                )
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
                                    text = "সর্বমোট ব্যালেন্স (Available Balance)",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontWeight = FontWeight.Medium
                                )
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "BDT (৳)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "৳ ${String.format("%,.2f", currentUser?.balance ?: 0.0)}",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { activeTab = 1 },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = Color(0xFF1E3A8A)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("deposit_tab_btn")
                                ) {
                                    Icon(imageVector = Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("ডিপোজিট", fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = { activeTab = 2 },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White.copy(alpha = 0.2f),
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("withdraw_tab_btn")
                                ) {
                                    Icon(imageVector = Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("উইথড্র", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Navigation Tabs
                PrimaryTabRow(
                    selectedTabIndex = activeTab,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = { Text("লেনদেন ইতিহাস", fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal) }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = { Text("টাকা জমা (Deposit)", fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal) }
                    )
                    Tab(
                        selected = activeTab == 2,
                        onClick = { activeTab = 2 },
                        text = { Text("টাকা উত্তোলন (Withdraw)", fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Normal) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Content View
                when (activeTab) {
                    0 -> {
                        // Transactions History
                        if (userTransactions.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.ReceiptLong,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "কোনো লেনদেনের ইতিহাস পাওয়া যায়নি",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(userTransactions) { tx ->
                                    TransactionItemCard(tx)
                                }
                            }
                        }
                    }
                    1 -> {
                        // Deposit Form
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Text(
                                    text = "১. পেমেন্ট মেথড নির্বাচন করুন:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    PaymentMethodChip(
                                        name = "bKash (বিকাশ)",
                                        selected = depositMethod == "BKASH",
                                        brandColor = BkashPink,
                                        onClick = { depositMethod = "BKASH" },
                                        modifier = Modifier.weight(1f)
                                    )
                                    PaymentMethodChip(
                                        name = "Nagad (নগদ)",
                                        selected = depositMethod == "NAGAD",
                                        brandColor = NagadOrange,
                                        onClick = { depositMethod = "NAGAD" },
                                        modifier = Modifier.weight(1f)
                                    )
                                    PaymentMethodChip(
                                        name = "Rocket (রকেট)",
                                        selected = depositMethod == "ROCKET",
                                        brandColor = RocketPurple,
                                        onClick = { depositMethod = "ROCKET" },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            item {
                                // Official Number Info Card
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "আমাদের অফিসিয়াল $depositMethod নম্বর:",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = paymentNumbers[depositMethod] ?: "",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        FilledTonalIconButton(
                                            onClick = { copyToClipboard(paymentNumbers[depositMethod] ?: "") },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }

                            item {
                                Text(
                                    text = "২. টাকার পরিমাণ (Amount in BDT):",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(quickAmounts) { amt ->
                                        SuggestionChip(
                                            onClick = { depositAmount = amt },
                                            label = { Text("৳ $amt", fontWeight = if (depositAmount == amt) FontWeight.Bold else FontWeight.Normal) },
                                            colors = SuggestionChipDefaults.suggestionChipColors(
                                                containerColor = if (depositAmount == amt) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                            )
                                        )
                                    }
                                }
                                OutlinedTextField(
                                    value = depositAmount,
                                    onValueChange = { depositAmount = it },
                                    label = { Text("টাকার পরিমাণ (৳)") },
                                    prefix = { Text("৳ ") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            item {
                                Text(
                                    text = "৩. আপনার পেমেন্টের বিবরণ দিন:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = senderNumber,
                                    onValueChange = { senderNumber = it },
                                    label = { Text("যে নম্বর থেকে টাকা পাঠিয়েছেন (Sender Phone)") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = transactionId,
                                    onValueChange = { transactionId = it },
                                    label = { Text("ট্রানজেকশন আইডি (TrxID)") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.ConfirmationNumber, contentDescription = null) },
                                    placeholder = { Text("e.g. BKH8721639") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            item {
                                Button(
                                    onClick = {
                                        val amt = depositAmount.toDoubleOrNull() ?: 0.0
                                        if (amt > 0 && senderNumber.isNotBlank() && transactionId.isNotBlank()) {
                                            onDeposit(amt, depositMethod, senderNumber, transactionId)
                                            activeTab = 0
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("submit_deposit_btn"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = when (depositMethod) {
                                            "BKASH" -> BkashPink
                                            "NAGAD" -> NagadOrange
                                            else -> RocketPurple
                                        }
                                    )
                                ) {
                                    Icon(imageVector = Icons.Default.Send, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("ডিপোজিট রিকোয়েস্ট জমা দিন (৳ $depositAmount)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }
                            }
                        }
                    }
                    2 -> {
                        // Withdraw Form
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Text(
                                    text = "১. উত্তোলনের মাধ্যম নির্বাচন করুন:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    PaymentMethodChip(
                                        name = "bKash (বিকাশ)",
                                        selected = withdrawMethod == "BKASH",
                                        brandColor = BkashPink,
                                        onClick = { withdrawMethod = "BKASH" },
                                        modifier = Modifier.weight(1f)
                                    )
                                    PaymentMethodChip(
                                        name = "Nagad (নগদ)",
                                        selected = withdrawMethod == "NAGAD",
                                        brandColor = NagadOrange,
                                        onClick = { withdrawMethod = "NAGAD" },
                                        modifier = Modifier.weight(1f)
                                    )
                                    PaymentMethodChip(
                                        name = "Rocket (রকেট)",
                                        selected = withdrawMethod == "ROCKET",
                                        brandColor = RocketPurple,
                                        onClick = { withdrawMethod = "ROCKET" },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            item {
                                Text(
                                    text = "২. উত্তোলনের পরিমাণ (Minimum ৳100):",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = withdrawAmount,
                                    onValueChange = { withdrawAmount = it },
                                    label = { Text("টাকার পরিমাণ (৳)") },
                                    prefix = { Text("৳ ") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Text(
                                    text = "বর্তমান ব্যালেন্স: ৳ ${String.format("%,.2f", currentUser?.balance ?: 0.0)}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                                )
                            }

                            item {
                                Text(
                                    text = "৩. টাকা গ্রহণের নম্বর:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = recipientNumber,
                                    onValueChange = { recipientNumber = it },
                                    label = { Text("আপনার $withdrawMethod নম্বর (Personal)") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.PhoneAndroid, contentDescription = null) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            item {
                                Button(
                                    onClick = {
                                        val amt = withdrawAmount.toDoubleOrNull() ?: 0.0
                                        if (amt >= 100 && recipientNumber.isNotBlank()) {
                                            onWithdraw(amt, withdrawMethod, recipientNumber)
                                            activeTab = 0
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("submit_withdraw_btn"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF10B981)
                                    )
                                ) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("উইথড্র কনফার্ম করুন (৳ $withdrawAmount)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentMethodChip(
    name: String,
    selected: Boolean,
    brandColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (selected) brandColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) brandColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = modifier
            .clickable { onClick() }
            .testTag("method_chip_$name")
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (selected) brandColor else Color.Transparent)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = name,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) brandColor else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun TransactionItemCard(tx: WalletTransactionEntity) {
    val isPositive = tx.type in listOf("DEPOSIT", "EARNING", "BONUS")
    val (icon, color, label) = when (tx.type) {
        "DEPOSIT" -> Triple(Icons.Default.AddCircle, Color(0xFF10B981), "টাকা জমা (${tx.paymentMethod})")
        "WITHDRAWAL" -> Triple(Icons.Default.ArrowOutward, Color(0xFFEF4444), "উত্তোলন (${tx.paymentMethod})")
        "BOOST_PAYMENT" -> Triple(Icons.Default.RocketLaunch, Color(0xFF8B5CF6), "পোস্ট বুস্টিং পেমেন্ট")
        "VERIFY_PAYMENT" -> Triple(Icons.Default.Verified, MaxBookBlue, "ব্লু ভেরিফিকেশন পেমেন্ট")
        "EARNING" -> Triple(Icons.Default.MonetizationOn, Color(0xFFF59E0B), "ক্রিয়েটর মনিটাইজেশন ইনকাম")
        else -> Triple(Icons.Default.Payment, Color.Gray, tx.type)
    }

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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                }
                Column {
                    Text(text = label, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text(
                        text = "${formatTimeAgo(tx.timestamp)} • Trx: ${tx.transactionId.take(10)}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isPositive) "+" else "-"} ৳ ${String.format("%,.0f", tx.amount)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (isPositive) Color(0xFF10B981) else Color(0xFFEF4444)
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (tx.status) {
                        "APPROVED" -> Color(0xFF10B981).copy(alpha = 0.15f)
                        "REJECTED" -> Color(0xFFEF4444).copy(alpha = 0.15f)
                        else -> Color(0xFFF59E0B).copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = when (tx.status) {
                            "APPROVED" -> "সম্পন্ন ✅"
                            "REJECTED" -> "বাতিল ❌"
                            else -> "প্রক্রিয়াধীন ⏳"
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (tx.status) {
                            "APPROVED" -> Color(0xFF10B981)
                            "REJECTED" -> Color(0xFFEF4444)
                            else -> Color(0xFFF59E0B)
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
