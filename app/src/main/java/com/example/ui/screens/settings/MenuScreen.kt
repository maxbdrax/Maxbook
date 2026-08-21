package com.example.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.UserEntity
import com.example.ui.MainTab
import com.example.ui.theme.MaxBookBlue
import com.example.ui.theme.VerifiedBadge

@Composable
fun MenuScreen(
    currentUser: UserEntity?,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onProfileClick: () -> Unit,
    onNavigateTab: (MainTab) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAdminDashboard: () -> Unit,
    onOpenWallet: () -> Unit,
    onOpenMonetization: () -> Unit,
    onOpenBlueVerification: () -> Unit,
    onOpenSavedPosts: () -> Unit,
    onLogout: () -> Unit
) {
    val isAdmin = currentUser?.role == "SUPER_ADMIN" || currentUser?.role == "ADMIN" || currentUser?.id == "user_admin"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("menu_screen_view"),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "মেন্যু ও সার্ভিস (Menu)",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = onOpenSettings,
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Profile Shortcut Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onProfileClick() }
                    .testTag("menu_profile_shortcut"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AsyncImage(
                        model = currentUser?.avatarUrl ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400",
                        contentDescription = "User Avatar",
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = currentUser?.name ?: "MaxBook User",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            if (currentUser?.isVerified == true) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Verified",
                                    tint = VerifiedBadge,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = "@${currentUser?.username ?: "user"} • ${currentUser?.followersCount ?: 0} ফলোয়ার",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Go",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Wallet Balance Feature Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onOpenWallet() }
                    .testTag("menu_wallet_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B)))
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981)
                                )
                                Text(
                                    text = "বিকাশ • নগদ • রকেট ওয়ালেট",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "ডিপোজিট ও উইথড্র",
                                    color = Color(0xFF10B981),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "বর্তমান ব্যালেন্স", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                                Text(
                                    text = "৳ ${String.format("%,.2f", currentUser?.balance ?: 0.0)}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 22.sp
                                )
                            }

                            Button(
                                onClick = onOpenWallet,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                            ) {
                                Text("ওয়ালেট খুলুন", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Admin Panel Highlight Card (if Admin)
        if (isAdmin) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { onOpenAdminDashboard() }
                        .testTag("menu_admin_dashboard_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(listOf(Color(0xFF991B1B), Color(0xFFDC2626)))
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AdminPanelSettings,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                                Column {
                                    Text(
                                        text = "অ্যাডমিন কন্ট্রোল প্যানেল 🛡️",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "ইউজার • ব্যালেন্স • ডিপোজিট/উইথড্র অনুমোদন",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                }
                            }
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }
        }

        // Feature Shortcuts Grid
        item {
            Text(
                text = "ফিচার ও সেবা সমূহ",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            val shortcuts = listOf(
                ShortcutItem("ওয়ালেট ও লেনদেন", Icons.Default.AccountBalanceWallet, Color(0xFF10B981)) { onOpenWallet() },
                ShortcutItem("ক্রিয়েটর ইনকাম (5K)", Icons.Default.MonetizationOn, Color(0xFFF59E0B)) { onOpenMonetization() },
                ShortcutItem("ব্লু ভেরিফাইড ব্যাজ", Icons.Default.Verified, MaxBookBlue) { onOpenBlueVerification() },
                ShortcutItem("মার্কেটপ্লেস", Icons.Default.Storefront, Color(0xFF1877F2)) { onNavigateTab(MainTab.MARKETPLACE) },
                ShortcutItem("গ্রুপস", Icons.Default.Groups, Color(0xFF00A884)) { onNavigateTab(MainTab.GROUPS) },
                ShortcutItem("ভিডিও ও রিলস", Icons.Default.OndemandVideo, Color(0xFFF3425F)) { onNavigateTab(MainTab.WATCH) },
                ShortcutItem("ফ্রেন্ডস ও ফলোয়ার", Icons.Default.People, Color(0xFF1877F2)) { onNavigateTab(MainTab.FRIENDS) },
                ShortcutItem("সেভ করা পোস্ট", Icons.Default.Bookmark, Color(0xFF9C27B0)) { onOpenSavedPosts() },
                ShortcutItem(if (isDarkMode) "লাইট মোড" else "ডার্ক মোড", if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode, Color(0xFFFF9800)) { onToggleDarkMode() },
                ShortcutItem("সেটিংস ও প্রাইভেসি", Icons.Default.Security, Color(0xFF607D8B)) { onOpenSettings() }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                shortcuts.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowItems.forEach { item ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { item.onClick() },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title,
                                        tint = item.color,
                                        modifier = Modifier.size(26.dp)
                                    )
                                    Text(
                                        text = item.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Log out button
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { onLogout() }
                    .testTag("menu_logout_button"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Log out",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "অ্যাকাউন্ট থেকে লগআউট করুন",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

private data class ShortcutItem(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

@Composable
fun SettingsScreen(
    currentUser: UserEntity?,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onBack: () -> Unit
) {
    var is2FAEnabled by remember { mutableStateOf(false) }
    var allowSearchIndex by remember { mutableStateOf(true) }
    var activityStatusOnline by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("settings_screen_view")
    ) {
        // Header
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
                Text(
                    text = "সেটিংস ও প্রাইভেসি (Settings)",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(text = "অ্যাকাউন্ট প্রেফারেন্স", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "ডার্ক মোড (Dark Theme)", fontSize = 14.sp)
                            Switch(checked = isDarkMode, onCheckedChange = { onToggleDarkMode() })
                        }
                        HorizontalDivider(thickness = 0.5.dp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "অনলাইন অ্যাক্টিভ স্ট্যাটাস", fontSize = 14.sp)
                            Switch(checked = activityStatusOnline, onCheckedChange = { activityStatusOnline = it })
                        }
                    }
                }
            }

            item {
                Text(text = "নিরাপত্তা ও প্রাইভেসি", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "টু-ফ্যাক্টর অথেনটিকেশন (2FA)", fontSize = 14.sp)
                            Switch(checked = is2FAEnabled, onCheckedChange = { is2FAEnabled = it })
                        }
                        HorizontalDivider(thickness = 0.5.dp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "সার্চ ইঞ্জিনে প্রোফাইল ইনডেক্সিং", fontSize = 14.sp)
                            Switch(checked = allowSearchIndex, onCheckedChange = { allowSearchIndex = it })
                        }
                    }
                }
            }
        }
    }
}
