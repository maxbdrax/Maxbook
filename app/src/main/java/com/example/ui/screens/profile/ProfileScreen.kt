package com.example.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.local.PostEntity
import com.example.data.local.UserEntity
import com.example.data.model.ReactionType
import com.example.ui.components.PostCard
import com.example.ui.theme.MaxBookBlue
import com.example.ui.theme.OnlineGreen
import com.example.ui.theme.VerifiedBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: UserEntity,
    currentUser: UserEntity?,
    userPosts: List<PostEntity>,
    allFriends: List<UserEntity>,
    onEditProfileClick: () -> Unit,
    onAddStoryClick: () -> Unit,
    onMessageClick: (UserEntity) -> Unit,
    onToggleFriend: (String, Boolean) -> Unit,
    onToggleFollow: (String, Boolean) -> Unit,
    onBlockUser: (String) -> Unit,
    onReportUser: (String) -> Unit,
    onAdminClick: () -> Unit,
    onReactToPost: (Long, ReactionType) -> Unit,
    onCommentClick: (Long) -> Unit,
    onShareClick: (PostEntity) -> Unit,
    onSaveClick: (Long) -> Unit,
    onDeletePost: (Long) -> Unit,
    onPollVote: (Long, Int) -> Unit,
    onOpenWallet: () -> Unit = {},
    onOpenMonetization: () -> Unit = {},
    onOpenBlueVerification: () -> Unit = {},
    onBoostPost: (PostEntity) -> Unit = {}
) {
    val isMe = user.id == currentUser?.id
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Posts", "Photos", "About", "Friends")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("profile_screen_view"),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Cover Photo & Avatar Header
        item {
            Card(
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Cover Photo Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        AsyncImage(
                            model = user.coverPhotoUrl,
                            contentDescription = "Cover Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f))
                                    )
                                )
                        )

                        if (isMe) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(12.dp)
                                    .clickable { onEditProfileClick() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Edit Cover",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(text = "Edit Cover", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Avatar & Profile Info
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .offset(y = (-40).dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Large Avatar
                            Box(contentAlignment = Alignment.BottomEnd) {
                                AsyncImage(
                                    model = user.avatarUrl,
                                    contentDescription = user.name,
                                    modifier = Modifier
                                        .size(96.dp)
                                        .clip(CircleShape)
                                        .border(4.dp, MaterialTheme.colorScheme.surface, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                if (user.isOnline) {
                                    Box(
                                        modifier = Modifier
                                            .offset(x = (-4).dp, y = (-4).dp)
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(OnlineGreen)
                                            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Name & Verified Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = user.name,
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (user.isVerified) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Verified Profile",
                                    tint = VerifiedBadge,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Text(
                            text = "@${user.username}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (user.bio.isNotBlank()) {
                            Text(
                                text = user.bio,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        // Workplace & Location details
                        Column(
                            modifier = Modifier.padding(top = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (user.workplace.isNotBlank()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Work,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = user.workplace,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (user.location.isNotBlank()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.LocationOn,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Lives in ${user.location}",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarMonth,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Joined MaxBook in ${user.joinedDate}",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Stats Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 14.dp),
                            horizontalArrangement = Arrangement.spacedBy(18.dp)
                        ) {
                            Column {
                                Text(
                                    text = "${user.friendsCount}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Friends",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Column {
                                Text(
                                    text = "${user.followersCount}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Followers",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Column {
                                Text(
                                    text = "${userPosts.size}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Posts",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Profile Action Buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (isMe) {
                                Button(
                                    onClick = onAddStoryClick,
                                    colors = ButtonDefaults.buttonColors(containerColor = MaxBookBlue),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("profile_add_story_btn")
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add to story")
                                }

                                OutlinedButton(
                                    onClick = onEditProfileClick,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("profile_edit_btn")
                                ) {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Edit profile")
                                }

                                IconButton(
                                    onClick = onAdminClick,
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                                        .size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Dashboard,
                                        contentDescription = "Admin Dashboard",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            } else {
                                Button(
                                    onClick = { onToggleFriend(user.id, !user.isFriend) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (user.isFriend) MaterialTheme.colorScheme.surfaceVariant else MaxBookBlue
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (user.isFriend) Icons.Default.PersonRemove else Icons.Default.PersonAdd,
                                        contentDescription = null,
                                        tint = if (user.isFriend) MaterialTheme.colorScheme.onSurface else Color.White
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (user.isFriend) "Friends" else "Add Friend",
                                        color = if (user.isFriend) MaterialTheme.colorScheme.onSurface else Color.White
                                    )
                                }

                                Button(
                                    onClick = { onMessageClick(user) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Default.ChatBubbleOutline, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Message", color = MaterialTheme.colorScheme.onPrimaryContainer)
                                }

                                IconButton(
                                    onClick = { onReportUser(user.id) },
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                                        .size(40.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.MoreHoriz, contentDescription = "More")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Profile Tab Bar
        item {
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
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

        // Tab Content
        when (selectedTab) {
            0 -> { // Posts Tab
                if (userPosts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No posts shared yet.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    items(userPosts, key = { it.id }) { post ->
                        Box(modifier = Modifier.padding(horizontal = 12.dp)) {
                            PostCard(
                                post = post,
                                currentUserId = currentUser?.id,
                                onReact = { reaction -> onReactToPost(post.id, reaction) },
                                onCommentClick = { onCommentClick(post.id) },
                                onShareClick = { onShareClick(post) },
                                onSaveClick = { onSaveClick(post.id) },
                                onAuthorClick = {},
                                onPollVote = { index -> onPollVote(post.id, index) },
                                onDeleteClick = { onDeletePost(post.id) },
                                onReportClick = { onReportUser(post.authorId) },
                                onBlockUserClick = { onBlockUser(post.authorId) }
                            )
                        }
                    }
                }
            }
            1 -> { // Photos Tab
                val photos = userPosts.mapNotNull { it.imageUrl }.distinct()
                if (photos.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "No photos uploaded yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Photos (${photos.size})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    photos.chunked(3).forEach { rowPhotos ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            rowPhotos.forEach { photoUrl ->
                                                AsyncImage(
                                                    model = photoUrl,
                                                    contentDescription = "Photo",
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(110.dp)
                                                        .clip(RoundedCornerShape(8.dp)),
                                                    contentScale = ContentScale.Crop
                                                )
                                            }
                                            // Fill empty spaces
                                            repeat(3 - rowPhotos.size) {
                                                Spacer(modifier = Modifier.weight(1f))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            2 -> { // About Tab
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "About & Overview",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Text(text = user.email, fontSize = 14.sp)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Text(text = "Location: ${user.location}", fontSize = 14.sp)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(Icons.Default.Work, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Text(text = "Profession: ${user.workplace}", fontSize = 14.sp)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Text(text = if (user.isVerified) "Verified Creator Account" else "Standard Account", fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
            3 -> { // Friends Grid Tab
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Friends (${allFriends.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            allFriends.chunked(3).forEach { rowFriends ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowFriends.forEach { friend ->
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { onMessageClick(friend) },
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            AsyncImage(
                                                model = friend.avatarUrl,
                                                contentDescription = friend.name,
                                                modifier = Modifier
                                                    .size(70.dp)
                                                    .clip(RoundedCornerShape(8.dp)),
                                                contentScale = ContentScale.Crop
                                            )
                                            Text(
                                                text = friend.name,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 1,
                                                modifier = Modifier.padding(top = 4.dp)
                                            )
                                        }
                                    }
                                    repeat(3 - rowFriends.size) {
                                        Spacer(modifier = Modifier.weight(1f))
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

@Composable
fun EditProfileDialog(
    user: UserEntity,
    onDismiss: () -> Unit,
    onSave: (name: String, bio: String, workplace: String, location: String, avatarUrl: String?, coverUrl: String?) -> Unit
) {
    var name by remember { mutableStateOf(user.name) }
    var bio by remember { mutableStateOf(user.bio) }
    var workplace by remember { mutableStateOf(user.workplace) }
    var location by remember { mutableStateOf(user.location) }
    var selectedAvatar by remember { mutableStateOf(user.avatarUrl) }
    var selectedCover by remember { mutableStateOf(user.coverPhotoUrl) }

    val presetAvatars = listOf(
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400",
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
        "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400",
        "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400",
        "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("edit_profile_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Edit Profile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Avatar Presets
                Text(text = "Choose Avatar", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(presetAvatars) { avatarUrl ->
                        val isSelected = selectedAvatar == avatarUrl
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "Avatar Preset",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .border(
                                    if (isSelected) 3.dp else 1.dp,
                                    if (isSelected) MaxBookBlue else Color.Transparent,
                                    CircleShape
                                )
                                .clickable { selectedAvatar = avatarUrl },
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Display Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = workplace,
                    onValueChange = { workplace = it },
                    label = { Text("Workplace / Role") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSave(name, bio, workplace, location, selectedAvatar, selectedCover)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaxBookBlue),
                        modifier = Modifier.testTag("save_profile_btn")
                    ) {
                        Text("Save Changes")
                    }
                }
            }
        }
    }
}
