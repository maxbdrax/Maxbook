package com.example.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.*
import com.example.ui.components.formatTimeAgo
import com.example.ui.screens.wallet.BkashPink
import com.example.ui.screens.wallet.NagadOrange
import com.example.ui.screens.wallet.RocketPurple
import com.example.ui.theme.MaxBookBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    allUsers: List<UserEntity>,
    allTransactions: List<WalletTransactionEntity>,
    verificationRequests: List<VerificationRequestEntity>,
    boostOrders: List<BoostOrderEntity>,
    monetizationRequests: List<MonetizationRequestEntity>,
    reports: List<ReportEntity>,
    currentUser: UserEntity?,
    onApproveDeposit: (Long) -> Unit,
    onRejectDeposit: (Long) -> Unit,
    onApproveWithdrawal: (Long) -> Unit,
    onRejectWithdrawal: (Long) -> Unit,
    onApproveVerification: (Long) -> Unit,
    onRejectVerification: (Long) -> Unit,
    onToggleBanUser: (String, Boolean) -> Unit,
    onChangeUserRole: (String, String) -> Unit,
    onModifyUserBalance: (String, Double) -> Unit,
    onToggleUserVerified: (String, Boolean) -> Unit,
    onToggleUserMonetized: (String, Boolean) -> Unit,
    onResolveReport: (Long, String) -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var userSearchQuery by remember { mutableStateOf("") }
    var selectedUserForEdit by remember { mutableStateOf<UserEntity?>(null) }
    var editBalanceText by remember { mutableStateOf("") }

    val pendingDeposits = allTransactions.filter { it.type == "DEPOSIT" && it.status == "PENDING" }
    val pendingWithdrawals = allTransactions.filter { it.type == "WITHDRAWAL" && it.status == "PENDING" }
    val pendingVerifications = verificationRequests.filter { it.status == "PENDING" }

    val totalDeposits = allTransactions.filter { it.type == "DEPOSIT" && it.status == "APPROVED" }.sumOf { it.amount }
    val totalWithdrawals = allTransactions.filter { it.type == "WITHDRAWAL" && it.status == "APPROVED" }.sumOf { it.amount }
    val totalBoostRevenue = allTransactions.filter { it.type == "BOOST_PAYMENT" }.sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("admin_dashboard_screen")
    ) {
        // Top Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "MaxBook অ্যাডমিন কন্ট্রোল প্যানেল 🛡️",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "ইউজার • বিকাশ-নগদ ওয়ালেট • মনিটাইজেশন • ব্লু ব্যাজ",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Summary Metric Grid
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AdminMetricCard(
                        title = "মোট ইউজার",
                        value = "${allUsers.size} জন",
                        icon = Icons.Default.People,
                        color = MaxBookBlue,
                        modifier = Modifier.weight(1f)
                    )
                    AdminMetricCard(
                        title = "মোট ডিপোজিট",
                        value = "৳ ${String.format("%,.0f", totalDeposits)}",
                        icon = Icons.Default.AddCircle,
                        color = Color(0xFF10B981),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AdminMetricCard(
                        title = "মোট উইথড্র",
                        value = "৳ ${String.format("%,.0f", totalWithdrawals)}",
                        icon = Icons.Default.ArrowOutward,
                        color = Color(0xFFEF4444),
                        modifier = Modifier.weight(1f)
                    )
                    AdminMetricCard(
                        title = "বুস্টিং রেভিনিউ",
                        value = "৳ ${String.format("%,.0f", totalBoostRevenue)}",
                        icon = Icons.Default.RocketLaunch,
                        color = Color(0xFF8B5CF6),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Tabs Row
            item {
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 0.dp,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("ইউজার (${allUsers.size})", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("ডিপোজিট", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal)
                                if (pendingDeposits.isNotEmpty()) {
                                    Badge { Text("${pendingDeposits.size}") }
                                }
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("উইথড্র", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal)
                                if (pendingWithdrawals.isNotEmpty()) {
                                    Badge { Text("${pendingWithdrawals.size}") }
                                }
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("ব্লু ব্যাজ", fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal)
                                if (pendingVerifications.isNotEmpty()) {
                                    Badge { Text("${pendingVerifications.size}") }
                                }
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 4,
                        onClick = { selectedTab = 4 },
                        text = { Text("বুস্টিং ও মনিটাইজেশন", fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> {
                    // User Management Tab
                    item {
                        OutlinedTextField(
                            value = userSearchQuery,
                            onValueChange = { userSearchQuery = it },
                            placeholder = { Text("ইউজার খুঁজুন (নাম বা ইমেইল)...") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    val filteredUsers = allUsers.filter {
                        it.name.contains(userSearchQuery, ignoreCase = true) ||
                        it.email.contains(userSearchQuery, ignoreCase = true) ||
                        it.username.contains(userSearchQuery, ignoreCase = true)
                    }

                    if (filteredUsers.isEmpty()) {
                        item {
                            Text(text = "কোনো ইউজার পাওয়া যায়নি", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(16.dp))
                        }
                    } else {
                        items(filteredUsers) { user ->
                            AdminUserCard(
                                user = user,
                                onEditBalance = {
                                    selectedUserForEdit = user
                                    editBalanceText = user.balance.toInt().toString()
                                },
                                onToggleBan = { onToggleBanUser(user.id, !user.isBanned) },
                                onChangeRole = { newRole -> onChangeUserRole(user.id, newRole) },
                                onToggleVerified = { onToggleUserVerified(user.id, !user.isVerified) },
                                onToggleMonetized = { onToggleUserMonetized(user.id, !user.isMonetized) }
                            )
                        }
                    }
                }

                1 -> {
                    // Deposit Requests Tab (bKash, Nagad, Rocket)
                    if (pendingDeposits.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("কোনো পেন্ডিং ডিপোজিট রিকোয়েস্ট নেই ✅", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    } else {
                        items(pendingDeposits) { tx ->
                            AdminTransactionCard(
                                tx = tx,
                                isDeposit = true,
                                onApprove = { onApproveDeposit(tx.id) },
                                onReject = { onRejectDeposit(tx.id) }
                            )
                        }
                    }
                }

                2 -> {
                    // Withdrawal Requests Tab
                    if (pendingWithdrawals.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("কোনো পেন্ডিং উইথড্র রিকোয়েস্ট নেই ✅", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    } else {
                        items(pendingWithdrawals) { tx ->
                            AdminTransactionCard(
                                tx = tx,
                                isDeposit = false,
                                onApprove = { onApproveWithdrawal(tx.id) },
                                onReject = { onRejectWithdrawal(tx.id) }
                            )
                        }
                    }
                }

                3 -> {
                    // Blue Verification Requests Tab
                    if (verificationRequests.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("কোনো ভেরিফিকেশন আবেদন নেই", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    } else {
                        items(verificationRequests) { req ->
                            AdminVerificationCard(
                                req = req,
                                onApprove = { onApproveVerification(req.id) },
                                onReject = { onRejectVerification(req.id) }
                            )
                        }
                    }
                }

                4 -> {
                    // Boost Orders & Monetization Requests Tab
                    item {
                        Text(text = "🔥 সকল পোস্ট বুস্টিং ক্যাম্পেইন (${boostOrders.size}):", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    if (boostOrders.isEmpty()) {
                        item {
                            Text("কোনো সক্রিয় বুস্টিং অর্ডার নেই", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                        }
                    } else {
                        items(boostOrders) { order ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(text = "পোস্ট #${order.postId} • ${order.userName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(text = "টার্গেট রিচ: ${order.targetReach} • পেমেন্ট: ৳${order.amountPaid.toInt()} (${order.paymentMethod})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFF8B5CF6).copy(alpha = 0.15f)
                                    ) {
                                        Text("সক্রিয় 🚀", color = Color(0xFF8B5CF6), fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "💰 ৫K ক্রিয়েটর মনিটাইজেশন আবেদন (${monetizationRequests.size}):", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    if (monetizationRequests.isEmpty()) {
                        item {
                            Text("কোনো মনিটাইজেশন রিকোয়েস্ট নেই", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                        }
                    } else {
                        items(monetizationRequests) { req ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(text = "${req.userName} (${req.followersCount} ফলোয়ার)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(text = "উত্তোলন নম্বর: ${req.payoutNumber} (${req.payoutMethod})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFF10B981).copy(alpha = 0.15f)
                                    ) {
                                        Text("অনুমোদিত ✅", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit User Balance Dialog
    if (selectedUserForEdit != null) {
        AlertDialog(
            onDismissRequest = { selectedUserForEdit = null },
            title = { Text("ব্যালেন্স এডিট করুন: ${selectedUserForEdit?.name}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("ইউজারের নতুন ব্যালেন্স নির্ধারণ করুন (BDT ৳):", fontSize = 13.sp)
                    OutlinedTextField(
                        value = editBalanceText,
                        onValueChange = { editBalanceText = it },
                        label = { Text("ব্যালেন্স (৳)") },
                        prefix = { Text("৳ ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newBal = editBalanceText.toDoubleOrNull() ?: 0.0
                        onModifyUserBalance(selectedUserForEdit!!.id, newBal)
                        selectedUserForEdit = null
                    }
                ) {
                    Text("সেভ করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedUserForEdit = null }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@Composable
fun AdminMetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(text = value, fontWeight = FontWeight.Black, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun AdminUserCard(
    user: UserEntity,
    onEditBalance: () -> Unit,
    onToggleBan: () -> Unit,
    onChangeRole: (String) -> Unit,
    onToggleVerified: () -> Unit,
    onToggleMonetized: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AsyncImage(
                        model = user.avatarUrl,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            if (user.isVerified) {
                                Icon(imageVector = Icons.Default.Verified, contentDescription = "Verified", tint = MaxBookBlue, modifier = Modifier.size(16.dp))
                            }
                        }
                        Text(text = "${user.email} • ${user.followersCount} ফলোয়ার", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (user.isBanned) Color(0xFFEF4444).copy(alpha = 0.15f) else Color(0xFF10B981).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (user.isBanned) "নিষিদ্ধ (BANNED)" else user.role,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (user.isBanned) Color(0xFFEF4444) else Color(0xFF10B981),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            // User Financial & Stats Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ব্যালেন্স: ৳ ${String.format("%,.2f", user.balance)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF10B981)
                )
                Text(
                    text = "ইনকাম: ৳ ${String.format("%,.0f", user.totalEarnings)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (user.isMonetized) "মনিটাইজেশন: চালু 💰" else "মনিটাইজেশন: বন্ধ",
                    fontSize = 12.sp,
                    color = if (user.isMonetized) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    onClick = onEditBalance,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("ব্যালেন্স এডিট", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onToggleVerified,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (user.isVerified) "ব্লু ব্যাজ রিমুভ" else "ব্লু ব্যাজ দিন", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onToggleMonetized,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (user.isMonetized) "ইনকাম অফ" else "ইনকাম অন", fontSize = 11.sp)
                }

                Button(
                    onClick = onToggleBan,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (user.isBanned) Color(0xFF10B981) else Color(0xFFEF4444)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (user.isBanned) "আনব্যান" else "ব্যান", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun AdminTransactionCard(
    tx: WalletTransactionEntity,
    isDeposit: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val methodColor = when (tx.paymentMethod) {
        "BKASH" -> BkashPink
        "NAGAD" -> NagadOrange
        else -> RocketPurple
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = methodColor
                    ) {
                        Text(
                            text = tx.paymentMethod,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Text(
                        text = if (isDeposit) "ডিপোজিট রিকোয়েস্ট" else "উইথড্র রিকোয়েস্ট",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Text(
                    text = "৳ ${String.format("%,.0f", tx.amount)}",
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    color = if (isDeposit) Color(0xFF10B981) else Color(0xFFEF4444)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = "ইউজার: ${tx.userName} (ID: ${tx.userId})", fontSize = 12.sp)
                Text(text = "নম্বর: ${tx.accountNumber} • সময়: ${formatTimeAgo(tx.timestamp)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (tx.transactionId.isNotBlank()) {
                    Text(text = "TrxID: ${tx.transactionId}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isDeposit) "অনুমোদন করুন (ব্যালেন্স যোগ)" else "পেমেন্ট পাঠানো হয়েছে")
                }
                OutlinedButton(
                    onClick = onReject,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(0.7f)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("বাতিল")
                }
            }
        }
    }
}

@Composable
fun AdminVerificationCard(
    req: VerificationRequestEntity,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AsyncImage(
                        model = req.userAvatar,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Column {
                        Text(text = req.userName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = "${req.followersCount} ফলোয়ার • ক্যাটাগরি: ${req.category}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaxBookBlue.copy(alpha = 0.15f)
                ) {
                    Text("৳ ${req.amountPaid.toInt()}", color = MaxBookBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }

            Text(text = "পেমেন্ট মাধ্যম: ${req.paymentMethod} • TrxID: ${req.trxId.ifBlank { "Wallet Balance" }}", fontSize = 12.sp)

            if (req.status == "PENDING") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = MaxBookBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ব্লু ব্যাজ অনুমোদন দিন")
                    }
                    OutlinedButton(
                        onClick = onReject,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(0.6f)
                    ) {
                        Text("বাতিল")
                    }
                }
            } else {
                Text(text = "স্ট্যাটাস: ${req.status}", fontWeight = FontWeight.Bold, color = if (req.status == "APPROVED") Color(0xFF10B981) else Color(0xFFEF4444))
            }
        }
    }
}
