package com.example.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.local.UserEntity
import com.example.ui.theme.MaxBookBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthDialog(
    allUsers: List<UserEntity>,
    onDismiss: () -> Unit,
    onLogin: (emailOrUsername: String, password: String) -> Unit,
    onRegister: (name: String, username: String, email: String, phone: String, password: String, avatarUrl: String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedAvatar by remember { mutableStateOf("https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400") }

    val presetAvatars = listOf(
        "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400",
        "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=400",
        "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400",
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp)
                .testTag("auth_dialog_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "MAXBOOK",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = MaxBookBlue,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "কানেক্ট করুন • ইনকাম করুন • গ্রো করুন",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Tab Switcher
                PrimaryTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("লগইন (Log In)", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("রেজিস্ট্রেশন (Register)", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
                    )
                }

                if (selectedTab == 0) {
                    // Log In Form
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("ইমেইল বা ইউজারনেম") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("পাসওয়ার্ড") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Button(
                            onClick = {
                                if (email.isNotBlank()) {
                                    onLogin(email, password)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaxBookBlue),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("login_submit_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("লগইন করুন", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                } else {
                    // Registration Form
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.12f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(imageVector = Icons.Default.CardGiftcard, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                                Text("নতুন একাউন্ট খুললেই পাচ্ছেন ৳১০০ ওয়েলকাম বোনাস!", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                            }
                        }

                        Text("প্রোফাইল ছবি নির্বাচন করুন:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(presetAvatars) { avatar ->
                                AsyncImage(
                                    model = avatar,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .border(
                                            width = if (selectedAvatar == avatar) 2.dp else 1.dp,
                                            color = if (selectedAvatar == avatar) MaxBookBlue else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable { selectedAvatar = avatar },
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("আপনার পুরো নাম (Full Name)") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("ইউজারনেম (@username)") },
                            leadingIcon = { Icon(imageVector = Icons.Default.AlternateEmail, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("বিকাশ/নগদ ফোন নম্বর (Phone)") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("ইমেইল অ্যাড্রেস") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("পাসওয়ার্ড") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Button(
                            onClick = {
                                if (name.isNotBlank() && username.isNotBlank()) {
                                    onRegister(name, username, email, phone, password, selectedAvatar)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaxBookBlue),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("register_submit_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("একাউন্ট তৈরি করুন (৳১০০ বোনাস)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
