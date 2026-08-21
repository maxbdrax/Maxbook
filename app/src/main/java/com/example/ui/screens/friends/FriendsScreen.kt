package com.example.ui.screens.friends

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.FriendRequestEntity
import com.example.data.local.UserEntity
import com.example.ui.components.formatTimeAgo
import com.example.ui.theme.MaxBookBlue
import com.example.ui.theme.VerifiedBadge

@Composable
fun FriendsScreen(
    friendRequests: List<FriendRequestEntity>,
    suggestedUsers: List<UserEntity>,
    friends: List<UserEntity>,
    onAcceptRequest: (FriendRequestEntity) -> Unit,
    onDeclineRequest: (FriendRequestEntity) -> Unit,
    onAddFriend: (String) -> Unit,
    onMessageUser: (UserEntity) -> Unit,
    onProfileClick: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Requests (${friendRequests.size})", "Suggestions", "All Friends (${friends.size})")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("friends_screen_view")
    ) {
        // Top Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Friends & Connections",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 12.dp,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }
            }
        }

        // Tab Content
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when (selectedTab) {
                0 -> { // Requests
                    if (friendRequests.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(text = "🤝", fontSize = 44.sp)
                                    Text(text = "No pending friend requests", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text(text = "When people send you a request, it will appear here.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    } else {
                        items(friendRequests, key = { it.id }) { req ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    AsyncImage(
                                        model = req.requesterAvatar,
                                        contentDescription = req.requesterName,
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                            .clickable { onProfileClick(req.requesterId) },
                                        contentScale = ContentScale.Crop
                                    )

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = req.requesterName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            )
                                            Text(
                                                text = formatTimeAgo(req.timestamp),
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        if (req.mutualFriendsCount > 0) {
                                            Text(
                                                text = "${req.mutualFriendsCount} mutual friends",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(bottom = 6.dp)
                                            )
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = { onAcceptRequest(req) },
                                                colors = ButtonDefaults.buttonColors(containerColor = MaxBookBlue),
                                                shape = RoundedCornerShape(16.dp),
                                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                                                modifier = Modifier.testTag("confirm_friend_request_${req.id}")
                                            ) {
                                                Text("Confirm", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            }

                                            OutlinedButton(
                                                onClick = { onDeclineRequest(req) },
                                                shape = RoundedCornerShape(16.dp),
                                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                                            ) {
                                                Text("Delete", fontSize = 13.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> { // Suggestions
                    items(suggestedUsers, key = { it.id }) { user ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                AsyncImage(
                                    model = user.avatarUrl,
                                    contentDescription = user.name,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .clickable { onProfileClick(user.id) },
                                    contentScale = ContentScale.Crop
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = user.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        if (user.isVerified) {
                                            Icon(
                                                imageVector = Icons.Filled.CheckCircle,
                                                contentDescription = "Verified",
                                                tint = VerifiedBadge,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = "${user.location} • ${user.friendsCount} friends",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = { onAddFriend(user.id) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (user.isFriend) MaterialTheme.colorScheme.surfaceVariant else MaxBookBlue
                                            ),
                                            shape = RoundedCornerShape(16.dp),
                                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (user.isFriend) Icons.Default.Check else Icons.Default.PersonAdd,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (user.isFriend) "Requested" else "Add Friend",
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
                2 -> { // All Friends
                    items(friends, key = { it.id }) { friend ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
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
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onProfileClick(friend.id) }
                                ) {
                                    AsyncImage(
                                        model = friend.avatarUrl,
                                        contentDescription = friend.name,
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    Column {
                                        Text(
                                            text = friend.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = friend.workplace.ifBlank { friend.location },
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    IconButton(
                                        onClick = { onMessageUser(friend) },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ChatBubbleOutline,
                                            contentDescription = "Message",
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
