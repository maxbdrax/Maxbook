package com.example.ui.screens.groups

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.GroupEntity
import com.example.data.local.PageEntity
import com.example.ui.theme.MaxBookBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    groups: List<GroupEntity>,
    pages: List<PageEntity>,
    onToggleJoinGroup: (String, Boolean) -> Unit,
    onToggleFollowPage: (String, Boolean) -> Unit,
    onCreateGroupClick: () -> Unit,
    onCreatePageClick: () -> Unit,
    onGroupClick: (GroupEntity) -> Unit,
    onPageClick: (PageEntity) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Groups", "Pages", "Discover")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("groups_screen_view")
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
                        text = "Communities & Pages",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Button(
                        onClick = {
                            if (selectedTab == 1) onCreatePageClick() else onCreateGroupClick()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaxBookBlue),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (selectedTab == 1) "Create Page" else "Create Group", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

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
        }

        // List Content
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when (selectedTab) {
                0 -> { // Groups Tab
                    val joinedGroups = groups.filter { it.isJoined }
                    val discoverGroups = groups.filter { !it.isJoined }

                    if (joinedGroups.isNotEmpty()) {
                        item {
                            Text(
                                text = "Your Groups (${joinedGroups.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        items(joinedGroups, key = { it.id }) { group ->
                            GroupCard(
                                group = group,
                                onToggleJoin = { onToggleJoinGroup(group.id, !group.isJoined) },
                                onClick = { onGroupClick(group) }
                            )
                        }
                    }

                    if (discoverGroups.isNotEmpty()) {
                        item {
                            Text(
                                text = "Suggested Groups",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                            )
                        }
                        items(discoverGroups, key = { it.id }) { group ->
                            GroupCard(
                                group = group,
                                onToggleJoin = { onToggleJoinGroup(group.id, !group.isJoined) },
                                onClick = { onGroupClick(group) }
                            )
                        }
                    }
                }
                1 -> { // Pages Tab
                    items(pages, key = { it.id }) { page ->
                        PageCard(
                            page = page,
                            onToggleFollow = { onToggleFollowPage(page.id, !page.isFollowing) },
                            onClick = { onPageClick(page) }
                        )
                    }
                }
                2 -> { // Discover Tab
                    item {
                        Text(
                            text = "Explore Trending Communities",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    items(groups, key = { "discover_grp_${it.id}" }) { group ->
                        GroupCard(
                            group = group,
                            onToggleJoin = { onToggleJoinGroup(group.id, !group.isJoined) },
                            onClick = { onGroupClick(group) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GroupCard(
    group: GroupEntity,
    onToggleJoin: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                AsyncImage(
                    model = group.coverUrl,
                    contentDescription = group.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.65f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = group.privacy,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = group.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${"%,d".format(group.membersCount)} members • ${group.category}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = group.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onToggleJoin,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (group.isJoined) MaterialTheme.colorScheme.surfaceVariant else MaxBookBlue
                    ),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (group.isJoined) "Joined" else "Join",
                        color = if (group.isJoined) MaterialTheme.colorScheme.onSurface else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PageCard(
    page: PageEntity,
    onToggleFollow: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                modifier = Modifier.weight(1f)
            ) {
                AsyncImage(
                    model = page.avatarUrl,
                    contentDescription = page.name,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Column {
                    Text(
                        text = page.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "${page.category} • ${"%,d".format(page.followersCount)} followers",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = page.bio,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onToggleFollow,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (page.isFollowing) MaterialTheme.colorScheme.surfaceVariant else MaxBookBlue
                ),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (page.isFollowing) "Following" else "Follow",
                    color = if (page.isFollowing) MaterialTheme.colorScheme.onSurface else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
