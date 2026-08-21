package com.example.ui.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.NotificationEntity
import com.example.data.model.NotificationType
import com.example.ui.components.formatTimeAgo
import com.example.ui.theme.MaxBookBlue

@Composable
fun NotificationsScreen(
    notifications: List<NotificationEntity>,
    onNotificationClick: (NotificationEntity) -> Unit,
    onMarkAllRead: () -> Unit,
    onBack: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Unread", "Mentions")

    val filteredList = when (selectedFilter) {
        "Unread" -> notifications.filter { !it.isRead }
        "Mentions" -> notifications.filter { it.type == NotificationType.COMMENT || it.type == NotificationType.FRIEND_REQUEST }
        else -> notifications
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("notifications_screen_view")
    ) {
        // Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
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
                            text = "Notifications",
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    TextButton(onClick = onMarkAllRead) {
                        Text("Mark all read", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Filter tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filters.forEach { f ->
                        val isSelected = selectedFilter == f
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = f },
                            label = { Text(f, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }

        // List
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "🔔", fontSize = 44.sp)
                    Text(text = "No notifications right now", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "You're all caught up!", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredList, key = { it.id }) { notif ->
                    NotificationItem(
                        notification = notif,
                        onClick = { onNotificationClick(notif) }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: NotificationEntity,
    onClick: () -> Unit
) {
    Surface(
        color = if (notification.isRead) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("notification_item_${notification.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar with Type Badge
            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = notification.senderAvatar,
                    contentDescription = notification.senderName,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                // Type Icon Badge
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(
                            when (notification.type) {
                                NotificationType.LIKE -> Color(0xFF1877F2)
                                NotificationType.COMMENT -> Color(0xFF45BD62)
                                NotificationType.FRIEND_REQUEST -> Color(0xFF9C27B0)
                                else -> Color(0xFFF7B125)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (notification.type) {
                            NotificationType.LIKE -> Icons.Default.ThumbUp
                            NotificationType.COMMENT -> Icons.Default.ChatBubble
                            NotificationType.FRIEND_REQUEST -> Icons.Default.PersonAdd
                            else -> Icons.Default.Notifications
                        },
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${notification.senderName} ${notification.content}",
                    fontSize = 14.sp,
                    fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = formatTimeAgo(notification.timestamp),
                    fontSize = 11.sp,
                    color = if (notification.isRead) MaterialTheme.colorScheme.onSurfaceVariant else MaxBookBlue,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            if (!notification.isRead) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaxBookBlue)
                )
            }
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), thickness = 0.5.dp)
}
