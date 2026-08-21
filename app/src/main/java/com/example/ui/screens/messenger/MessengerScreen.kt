package com.example.ui.screens.messenger

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.ChatThreadEntity
import com.example.data.local.MessageEntity
import com.example.data.local.UserEntity
import com.example.ui.components.formatTimeAgo
import com.example.ui.theme.MaxBookBlue
import com.example.ui.theme.NotificationBadge
import com.example.ui.theme.OnlineGreen

@Composable
fun MessengerScreen(
    threads: List<ChatThreadEntity>,
    allUsers: List<UserEntity>,
    onSelectThread: (String) -> Unit,
    onStartNewChat: (UserEntity) -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredThreads = if (searchQuery.isBlank()) threads else threads.filter {
        it.participantName.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("messenger_screen_view")
    ) {
        // Top Header
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = "Chats",
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = { /* New message */ },
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "New Message",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Search Chats Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search Messenger...", fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            singleLine = true
        )

        // Active Now Contacts Carousel
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            Text(
                text = "Active Now",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(allUsers.filter { it.isOnline }) { user ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onStartNewChat(user) }
                            .width(60.dp)
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            AsyncImage(
                                model = user.avatarUrl,
                                contentDescription = user.name,
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(OnlineGreen)
                                    .border(2.dp, MaterialTheme.colorScheme.background, CircleShape)
                            )
                        }
                        Text(
                            text = user.name.split(" ").firstOrNull() ?: user.name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
            modifier = Modifier.padding(top = 8.dp)
        )

        // Thread List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(filteredThreads, key = { it.id }) { thread ->
                ChatThreadItem(
                    thread = thread,
                    onClick = { onSelectThread(thread.id) }
                )
            }
        }
    }
}

@Composable
fun ChatThreadItem(
    thread: ChatThreadEntity,
    onClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("chat_thread_${thread.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = thread.participantAvatar,
                    contentDescription = thread.participantName,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                if (thread.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(OnlineGreen)
                            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = thread.participantName,
                        fontWeight = if (thread.unreadCount > 0) FontWeight.Bold else FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = formatTimeAgo(thread.lastMessageTimestamp),
                        fontSize = 12.sp,
                        color = if (thread.unreadCount > 0) MaxBookBlue else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = thread.lastMessage,
                        fontSize = 13.sp,
                        fontWeight = if (thread.unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (thread.unreadCount > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (thread.unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(MaxBookBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${thread.unreadCount}",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), thickness = 0.5.dp)
}

@Composable
fun ChatDetailScreen(
    thread: ChatThreadEntity?,
    messages: List<MessageEntity>,
    currentUser: UserEntity?,
    onBack: () -> Unit,
    onSendMessage: (String, String?, Boolean, Int) -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("chat_detail_screen")
    ) {
        // Conversation Top Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }

                    Box(contentAlignment = Alignment.BottomEnd) {
                        AsyncImage(
                            model = thread?.participantAvatar ?: "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400",
                            contentDescription = thread?.participantName,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        if (thread?.isOnline == true) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(OnlineGreen)
                                    .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = thread?.participantName ?: "Chat",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (thread?.isOnline == true) "Active now" else "Offline",
                            fontSize = 11.sp,
                            color = if (thread?.isOnline == true) OnlineGreen else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    IconButton(onClick = { /* Call */ }) {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = "Call", tint = MaxBookBlue)
                    }
                    IconButton(onClick = { /* Video Call */ }) {
                        Icon(imageVector = Icons.Default.Videocam, contentDescription = "Video Call", tint = MaxBookBlue)
                    }
                    IconButton(onClick = { /* Info */ }) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = "Details", tint = MaxBookBlue)
                    }
                }
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                MessageBubble(message = msg)
            }
        }

        // Message Composer
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Voice note simulation button
                IconButton(
                    onClick = {
                        onSendMessage("🎙️ Voice note (0:12)", null, true, 12)
                    },
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Note",
                        tint = MaxBookBlue
                    )
                }

                // Photo preset send button
                IconButton(
                    onClick = {
                        onSendMessage(
                            "Sent a photo",
                            "https://images.unsplash.com/photo-1519681393784-d120267933ba?w=800",
                            false,
                            0
                        )
                    },
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Send Photo",
                        tint = MaxBookBlue
                    )
                }

                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Aa", fontSize = 14.sp) },
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_message_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    maxLines = 4
                )

                if (messageText.isNotBlank()) {
                    IconButton(
                        onClick = {
                            onSendMessage(messageText, null, false, 0)
                            messageText = ""
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaxBookBlue)
                            .testTag("send_message_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else {
                    IconButton(
                        onClick = {
                            onSendMessage("👍", null, false, 0)
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Text(text = "👍", fontSize = 22.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: MessageEntity) {
    val isMine = message.isMine

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMine) 16.dp else 4.dp,
                bottomEnd = if (isMine) 4.dp else 16.dp
            ),
            color = if (isMine) MaxBookBlue else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                // Photo message
                if (!message.mediaUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = message.mediaUrl,
                        contentDescription = "Message Photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Audio voice note simulation
                if (message.isAudioNote) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play Audio",
                            tint = if (isMine) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf(12, 24, 18, 30, 20, 14, 28, 22, 10, 16, 26, 15).forEach { h ->
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(h.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(if (isMine) Color.White else MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                        Text(
                            text = "0:${message.audioDurationSec.toString().padStart(2, '0')}",
                            fontSize = 11.sp,
                            color = if (isMine) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Text(
                        text = message.text,
                        color = if (isMine) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Sub text: time & receipt
        Row(
            modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatTimeAgo(message.timestamp),
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (isMine) {
                Icon(
                    imageVector = when (message.status) {
                        "SEEN" -> Icons.Filled.DoneAll
                        "DELIVERED" -> Icons.Outlined.DoneAll
                        else -> Icons.Default.Done
                    },
                    contentDescription = message.status,
                    tint = if (message.status == "SEEN") MaxBookBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
