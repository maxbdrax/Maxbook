package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.local.StoryEntity
import com.example.data.local.UserEntity
import com.example.ui.theme.MaxBookBlue
import com.example.ui.theme.StoryBorderGradientEnd
import com.example.ui.theme.StoryBorderGradientStart
import kotlinx.coroutines.delay

@Composable
fun StoryCarousel(
    currentUser: UserEntity?,
    stories: List<StoryEntity>,
    onCreateStoryClick: () -> Unit,
    onStoryClick: (StoryEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // "Create Story" Card
        item {
            CreateStoryCard(
                currentUser = currentUser,
                onClick = onCreateStoryClick
            )
        }

        // Friend Stories
        items(stories, key = { it.id }) { story ->
            StoryCard(
                story = story,
                onClick = { onStoryClick(story) }
            )
        }
    }
}

@Composable
fun CreateStoryCard(
    currentUser: UserEntity?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(105.dp)
            .height(170.dp)
            .clickable { onClick() }
            .testTag("create_story_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f)
            ) {
                AsyncImage(
                    model = currentUser?.avatarUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400",
                    contentDescription = "Create Story Avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.3f)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                // Plus Badge
                Box(
                    modifier = Modifier
                        .offset(y = (-16).dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaxBookBlue)
                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Story",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "Create story",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 6.dp)
                )
            }
        }
    }
}

@Composable
fun StoryCard(
    story: StoryEntity,
    onClick: () -> Unit
) {
    val gradient = Brush.linearGradient(
        colors = listOf(StoryBorderGradientStart, StoryBorderGradientEnd)
    )

    Card(
        modifier = Modifier
            .width(105.dp)
            .height(170.dp)
            .clickable { onClick() }
            .testTag("story_card_${story.id}"),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Story Media
            AsyncImage(
                model = story.mediaUrl,
                contentDescription = story.authorName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Scrim overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.4f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )

            // Author Avatar with gradient ring
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopStart)
                    .size(36.dp)
                    .border(2.dp, gradient, CircleShape)
                    .padding(2.dp)
            ) {
                AsyncImage(
                    model = story.authorAvatar,
                    contentDescription = story.authorName,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            // Author Name
            Text(
                text = story.authorName,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun StoryViewerDialog(
    story: StoryEntity,
    onDismiss: () -> Unit,
    onSendReaction: (String) -> Unit
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(story) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 6000, easing = LinearEasing)
        )
        onDismiss()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .testTag("story_viewer_modal")
        ) {
            // Story Image Media
            AsyncImage(
                model = story.mediaUrl,
                contentDescription = story.authorName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )

            // Top Header: Progress bar + Author info + Close button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Animated Progress Bar
                LinearProgressIndicator(
                    progress = { progress.value },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AsyncImage(
                            model = story.authorAvatar,
                            contentDescription = story.authorName,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Column {
                            Text(
                                text = story.authorName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = formatTimeAgo(story.timestamp),
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_story_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Story",
                            tint = Color.White
                        )
                    }
                }
            }

            // Bottom Caption & Quick Reactions
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (story.caption.isNotBlank()) {
                    Text(
                        text = story.caption,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .background(
                                Color.Black.copy(alpha = 0.5f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("❤️", "🔥", "😂", "👏", "😍", "🎉").forEach { emoji ->
                        Button(
                            onClick = {
                                onSendReaction(emoji)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.2f)
                            ),
                            shape = CircleShape,
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Text(text = emoji, fontSize = 20.sp)
                        }
                    }
                }
            }
        }
    }
}
