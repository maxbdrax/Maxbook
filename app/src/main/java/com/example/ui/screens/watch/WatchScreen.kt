package com.example.ui.screens.watch

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.PostEntity
import com.example.data.local.UserEntity
import com.example.data.model.ReactionType
import com.example.ui.components.formatTimeAgo
import com.example.ui.theme.MaxBookBlue
import com.example.ui.theme.NotificationBadge
import com.example.ui.theme.VerifiedBadge

@Composable
fun WatchScreen(
    videoPosts: List<PostEntity>,
    currentUser: UserEntity?,
    onReact: (Long, ReactionType) -> Unit,
    onCommentClick: (Long) -> Unit,
    onShareClick: (PostEntity) -> Unit,
    onSaveClick: (Long) -> Unit,
    onAuthorClick: (String) -> Unit
) {
    var selectedWatchTab by remember { mutableStateOf(0) }
    val tabs = listOf("For You", "Live Streams", "Reels", "Saved")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("watch_screen_view")
    ) {
        // Watch Section Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "MaxBook Watch",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { /* Search videos */ },
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search videos",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Filter Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedWatchTab,
                    edgePadding = 12.dp,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedWatchTab == index,
                            onClick = { selectedWatchTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedWatchTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
        }

        // Watch Feed
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Live Stream Spotlight when Live Streams tab is selected or first
            if (selectedWatchTab == 1 || selectedWatchTab == 0) {
                item {
                    LiveStreamCard(
                        title = "🔴 LIVE: Next-Gen AI & Robotics Showcase 2026",
                        creatorName = "Marcus Chen (AeroDynamics)",
                        creatorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
                        thumbnailUrl = "https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=1000",
                        viewersCount = 3840
                    )
                }
            }

            // Video Posts
            items(videoPosts, key = { it.id }) { post ->
                VideoCardItem(
                    post = post,
                    currentUser = currentUser,
                    onReact = { reaction -> onReact(post.id, reaction) },
                    onCommentClick = { onCommentClick(post.id) },
                    onShareClick = { onShareClick(post) },
                    onSaveClick = { onSaveClick(post.id) },
                    onAuthorClick = { onAuthorClick(post.authorId) }
                )
            }
        }
    }
}

@Composable
fun VideoCardItem(
    post: PostEntity,
    currentUser: UserEntity?,
    onReact: (ReactionType) -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    onSaveClick: () -> Unit,
    onAuthorClick: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0.35f) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Creator Header
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
                    modifier = Modifier.clickable { onAuthorClick() }
                ) {
                    AsyncImage(
                        model = post.authorAvatar,
                        contentDescription = post.authorName,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = post.authorName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            if (post.isVerified) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Verified",
                                    tint = VerifiedBadge,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Text(
                            text = "${formatTimeAgo(post.timestamp)} • 🎬 Video",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Button(
                    onClick = { /* Follow */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Follow",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Description
            if (post.content.isNotBlank()) {
                Text(
                    text = post.content,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            // Video Player Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(Color.Black)
                    .clickable { isPlaying = !isPlaying },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = post.imageUrl ?: "https://images.unsplash.com/photo-1527977966376-1c8408f9f108?w=1000",
                    contentDescription = "Video frame",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Overlay Controls
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = if (isPlaying) 0.05f else 0.4f))
                )

                // Play / Pause Icon
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Volume & Duration overlay
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { isMuted = !isMuted },
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = "Volume",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color.Black.copy(alpha = 0.7f)
                    ) {
                        Text(
                            text = post.videoDuration ?: "0:45",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Scrub Slider Bar at bottom
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .height(3.dp),
                    color = MaxBookBlue,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
            }

            // Video Engagement Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${post.likesCount} reactions • ${post.commentsCount} comments",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(onClick = { onReact(ReactionType.LIKE) }) {
                    Icon(
                        imageVector = if (post.userReaction != ReactionType.NONE) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                        contentDescription = "Like",
                        tint = if (post.userReaction != ReactionType.NONE) MaxBookBlue else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (post.userReaction != ReactionType.NONE) post.userReaction.label else "Like",
                        color = if (post.userReaction != ReactionType.NONE) MaxBookBlue else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                TextButton(onClick = onCommentClick) {
                    Icon(imageVector = Icons.Outlined.ChatBubbleOutline, contentDescription = "Comment")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Comment")
                }

                TextButton(onClick = onShareClick) {
                    Icon(imageVector = Icons.Outlined.Share, contentDescription = "Share")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share")
                }

                IconButton(onClick = onSaveClick) {
                    Icon(
                        imageVector = if (post.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Save",
                        tint = if (post.isSaved) MaxBookBlue else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun LiveStreamCard(
    title: String,
    creatorName: String,
    creatorAvatar: String,
    thumbnailUrl: String,
    viewersCount: Int
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                AsyncImage(
                    model = thumbnailUrl,
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent, Color.Black.copy(alpha = 0.7f))
                            )
                        )
                )

                // LIVE Badge & Viewers Tag
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = NotificationBadge
                    ) {
                        Text(
                            text = "LIVE",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color.Black.copy(alpha = 0.7f)
                    ) {
                        Text(
                            text = "👁️ ${viewersCount} watching",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Title & Creator on bottom of video
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AsyncImage(
                            model = creatorAvatar,
                            contentDescription = creatorName,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = creatorName,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
