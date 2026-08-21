package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.PostEntity
import com.example.data.model.PrivacyLevel
import com.example.data.model.ReactionType
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PostCard(
    post: PostEntity,
    currentUserId: String?,
    onReact: (ReactionType) -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    onSaveClick: () -> Unit,
    onAuthorClick: (String) -> Unit,
    onPollVote: (Int) -> Unit,
    onDeleteClick: () -> Unit,
    onReportClick: () -> Unit,
    onBlockUserClick: () -> Unit,
    onBoostClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    var showReactionPicker by remember { mutableStateOf(false) }
    var isContentExpanded by remember { mutableStateOf(false) }
    var isPlayingVideo by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("post_card_${post.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // Pinned indicator if applicable
            if (post.isPinned) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Pinned",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Pinned Announcement",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            } else if (post.isBoosted) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF8B5CF6).copy(alpha = 0.08f))
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RocketLaunch,
                        contentDescription = "Boosted",
                        tint = Color(0xFF8B5CF6),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "স্পন্সরড / বুস্ট করা পোস্ট (Sponsored Boost)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8B5CF6)
                    )
                }
                HorizontalDivider(color = Color(0xFF8B5CF6).copy(alpha = 0.2f))
            }


            // Header: Author Avatar, Name, Badges, Timestamp & Menu
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onAuthorClick(post.authorId) }
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        AsyncImage(
                            model = post.authorAvatar,
                            contentDescription = post.authorName,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = post.authorName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (post.isVerified) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Verified",
                                    tint = VerifiedBadge,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            if (!post.groupName.isNullOrBlank()) {
                                Text(
                                    text = "in ${post.groupName}",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Subtitle: Feeling/Location + Timestamp + Privacy
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (!post.feelingActivity.isNullOrBlank()) {
                                Text(
                                    text = "${post.feelingActivity} •",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = formatTimeAgo(post.timestamp),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "•",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Icon(
                                imageVector = when (post.privacyLevel) {
                                    PrivacyLevel.PUBLIC -> Icons.Default.Public
                                    PrivacyLevel.FRIENDS -> Icons.Default.People
                                    PrivacyLevel.ONLY_ME -> Icons.Default.Lock
                                },
                                contentDescription = post.privacyLevel.label,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(12.dp)
                            )
                            if (!post.location.isNullOrBlank()) {
                                Text(
                                    text = "• ${post.location}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // 3-dots Menu Button
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.testTag("post_menu_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = "Post Options",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(if (post.isSaved) "Remove from Saved" else "Save Post")
                            },
                            onClick = {
                                showMenu = false
                                onSaveClick()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (post.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = null
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Copy Link") },
                            onClick = {
                                showMenu = false
                                onShareClick()
                            },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Link, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Report Post") },
                            onClick = {
                                showMenu = false
                                onReportClick()
                            },
                            leadingIcon = {
                                Icon(imageVector = Icons.Outlined.Flag, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            }
                        )
                        if (onBoostClick != null) {
                            DropdownMenuItem(
                                text = { Text("Boost Post (বুস্ট করুন)", color = Color(0xFF8B5CF6), fontWeight = FontWeight.Bold) },
                                onClick = {
                                    showMenu = false
                                    onBoostClick()
                                },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.RocketLaunch, contentDescription = null, tint = Color(0xFF8B5CF6))
                                }
                            )
                        }
                        if (post.authorId != currentUserId) {
                            DropdownMenuItem(
                                text = { Text("Block ${post.authorName}") },
                                onClick = {
                                    showMenu = false
                                    onBlockUserClick()
                                },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Outlined.Block, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                }
                            )
                        }
                        if (post.authorId == currentUserId || currentUserId == "user_admin" || currentUserId == "user_me") {
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Delete Post", color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    showMenu = false
                                    onDeleteClick()
                                },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                }
                            )
                        }
                    }
                }
            }

            // Post Text Content
            if (post.content.isNotBlank()) {
                Text(
                    text = post.content,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = if (isContentExpanded) Int.MAX_VALUE else 5,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clickable { isContentExpanded = !isContentExpanded }
                )
                if (!isContentExpanded && post.content.length > 220) {
                    Text(
                        text = "See more",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 2.dp)
                            .clickable { isContentExpanded = true }
                    )
                }
            }

            // Interactive Poll (if present)
            if (!post.pollQuestion.isNullOrBlank() && !post.pollOptionsJson.isNullOrBlank()) {
                PollView(
                    question = post.pollQuestion,
                    options = post.pollOptionsJson.split(":::"),
                    votes = post.pollVotesJson?.split(":::")?.map { it.toIntOrNull() ?: 0 } ?: emptyList(),
                    selectedIndex = post.selectedPollOptionIndex,
                    onVote = onPollVote,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Media Preview (Video or High-Res Image)
            if (!post.videoUrl.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .padding(vertical = 6.dp)
                        .background(Color.Black)
                        .clickable { isPlayingVideo = !isPlayingVideo },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = post.imageUrl ?: "https://images.unsplash.com/photo-1527977966376-1c8408f9f108?w=800",
                        contentDescription = "Video Thumbnail",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = if (isPlayingVideo) 0.1f else 0.4f))
                    )

                    // Video Play / Pause button
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.7f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlayingVideo) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = "Play Video",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Video duration tag
                    post.videoDuration?.let { duration ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color.Black.copy(alpha = 0.75f),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = if (isPlayingVideo) "Playing • $duration" else duration,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            } else if (!post.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = "Post Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 180.dp, max = 360.dp)
                        .padding(vertical = 6.dp),
                    contentScale = ContentScale.Crop
                )
            }

            // Stats / Counts Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Reactions stack & count
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (post.likesCount > 0) {
                        Row(horizontalArrangement = Arrangement.spacedBy((-4).dp)) {
                            Text("👍", fontSize = 14.sp)
                            Text("❤️", fontSize = 14.sp)
                            if (post.likesCount > 5) Text("🔥", fontSize = 14.sp)
                        }
                        Text(
                            text = "${post.likesCount}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Comments & Shares count
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (post.commentsCount > 0) {
                        Text(
                            text = "${post.commentsCount} comments",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.clickable { onCommentClick() }
                        )
                    }
                    if (post.sharesCount > 0) {
                        Text(
                            text = "${post.sharesCount} shares",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                thickness = 0.5.dp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            // Floating Reaction Picker
            Box(modifier = Modifier.fillMaxWidth()) {
                ReactionPicker(
                    visible = showReactionPicker,
                    onReactionSelected = { reaction ->
                        showReactionPicker = false
                        onReact(reaction)
                    },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = 16.dp, y = (-50).dp)
                )
            }

            // Action Buttons Bar: Like, Comment, Share, Save
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Like Button (with toggle & long-press reaction picker)
                val currentReaction = post.userReaction
                val isLiked = currentReaction != ReactionType.NONE

                TextButton(
                    onClick = {
                        if (showReactionPicker) {
                            showReactionPicker = false
                        } else if (isLiked) {
                            onReact(ReactionType.NONE)
                        } else {
                            onReact(ReactionType.LIKE)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("post_like_button_${post.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (isLiked) {
                            Text(text = currentReaction.emoji, fontSize = 16.sp)
                            Text(
                                text = currentReaction.label,
                                color = Color(currentReaction.colorHex),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.ThumbUp,
                                contentDescription = "Like",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Like",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // Reaction dock trigger helper
                IconButton(
                    onClick = { showReactionPicker = !showReactionPicker },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddReaction,
                        contentDescription = "Reactions Dock",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Comment Button
                TextButton(
                    onClick = onCommentClick,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("post_comment_button_${post.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Comment",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Comment",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }

                // Share Button
                TextButton(
                    onClick = onShareClick,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("post_share_button_${post.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Share",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }

                // Save Bookmark Button
                IconButton(
                    onClick = onSaveClick,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("post_save_button_${post.id}")
                ) {
                    Icon(
                        imageVector = if (post.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Save",
                        tint = if (post.isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PollView(
    question: String,
    options: List<String>,
    votes: List<Int>,
    selectedIndex: Int,
    onVote: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalVotes = votes.sum().coerceAtLeast(1)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Poll,
                    contentDescription = "Poll",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = question,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            options.forEachIndexed { index, option ->
                val optionVotes = votes.getOrElse(index) { 0 }
                val percentage = (optionVotes.toFloat() / totalVotes * 100).toInt()
                val isSelected = selectedIndex == index
                val progress by animateFloatAsState(
                    targetValue = optionVotes.toFloat() / totalVotes,
                    label = "poll_progress"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { onVote(index) }
                        .padding(2.dp)
                ) {
                    // Progress fill
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction = progress)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                    )

                    // Label & percentage row
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Voted",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = option,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "$percentage% ($optionVotes)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Text(
                text = "${votes.sum()} total votes • Tap an option to vote",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

fun formatTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        days < 7 -> "${days}d ago"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
    }
}
