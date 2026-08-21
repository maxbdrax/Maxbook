package com.example.ui.screens.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.data.local.PostEntity
import com.example.data.local.StoryEntity
import com.example.data.local.UserEntity
import com.example.data.model.ReactionType
import com.example.ui.components.PostCard
import com.example.ui.components.StoryCarousel

@Composable
fun FeedScreen(
    currentUser: UserEntity?,
    stories: List<StoryEntity>,
    posts: List<PostEntity>,
    feedFilter: String,
    onFilterChange: (String) -> Unit,
    onCreatePostClick: () -> Unit,
    onCreateStoryClick: () -> Unit,
    onStoryClick: (StoryEntity) -> Unit,
    onReactToPost: (Long, ReactionType) -> Unit,
    onCommentClick: (Long) -> Unit,
    onShareClick: (PostEntity) -> Unit,
    onSaveClick: (Long) -> Unit,
    onAuthorClick: (String) -> Unit,
    onPollVote: (Long, Int) -> Unit,
    onDeletePost: (Long) -> Unit,
    onReportPost: (Long) -> Unit,
    onBlockUser: (String) -> Unit,
    onBoostPost: ((PostEntity) -> Unit)? = null
) {
    val filters = listOf("For You", "Following", "Trending", "Saved")

    val filteredPosts = when (feedFilter) {
        "Following" -> posts.filter { it.authorId != "user_me" }
        "Trending" -> posts.sortedByDescending { it.likesCount + it.commentsCount }
        "Saved" -> posts.filter { it.isSaved }
        else -> posts
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("feed_screen_list"),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // "What's on your mind?" Post Composer Trigger Bar
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCreatePostClick() },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AsyncImage(
                            model = currentUser?.avatarUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400",
                            contentDescription = "User Avatar",
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "What's on your mind, ${currentUser?.name?.split(" ")?.firstOrNull() ?: "there"}?",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp)
                            )
                        }

                        IconButton(
                            onClick = onCreatePostClick,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Add photo",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 0.5.dp
                    )

                    // Quick Action triggers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Row(
                            modifier = Modifier
                                .clickable { onCreatePostClick() }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LiveTv,
                                contentDescription = "Live",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Live",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(
                            modifier = Modifier
                                .clickable { onCreatePostClick() }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Photo",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Photo",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(
                            modifier = Modifier
                                .clickable { onCreatePostClick() }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mood,
                                contentDescription = "Feeling",
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Feeling",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Stories Carousel
        item {
            StoryCarousel(
                currentUser = currentUser,
                stories = stories,
                onCreateStoryClick = onCreateStoryClick,
                onStoryClick = onStoryClick
            )
        }

        // Feed Filter Tabs
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { filter ->
                    val isSelected = feedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { onFilterChange(filter) },
                        label = {
                            Text(
                                text = filter,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }

        // Posts List
        if (filteredPosts.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "📭", fontSize = 40.sp)
                        Text(
                            text = "No posts to show",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Try switching the filter or create your own post to get started!",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(filteredPosts, key = { it.id }) { post ->
                Box(modifier = Modifier.padding(horizontal = 12.dp)) {
                    PostCard(
                        post = post,
                        currentUserId = currentUser?.id,
                        onReact = { reaction -> onReactToPost(post.id, reaction) },
                        onCommentClick = { onCommentClick(post.id) },
                        onShareClick = { onShareClick(post) },
                        onSaveClick = { onSaveClick(post.id) },
                        onAuthorClick = onAuthorClick,
                        onPollVote = { index -> onPollVote(post.id, index) },
                        onDeleteClick = { onDeletePost(post.id) },
                        onReportClick = { onReportPost(post.id) },
                        onBlockUserClick = { onBlockUser(post.authorId) },
                        onBoostClick = { onBoostPost?.invoke(post) }
                    )
                }
            }
        }
    }
}
