package com.example.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.local.*
import com.example.ui.theme.VerifiedBadge

@Composable
fun SearchScreen(
    searchHistory: List<SearchHistoryEntity>,
    allUsers: List<UserEntity>,
    allPosts: List<PostEntity>,
    allGroups: List<GroupEntity>,
    allMarketplaceItems: List<MarketplaceItemEntity>,
    onQuerySearch: (String) -> Unit,
    onDeleteSearchItem: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onGroupClick: (GroupEntity) -> Unit,
    onPostClick: (Long) -> Unit,
    onBack: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "People", "Posts", "Groups", "Marketplace")

    val filteredUsers = remember(query) {
        if (query.isBlank()) emptyList()
        else allUsers.filter { it.name.contains(query, ignoreCase = true) || it.username.contains(query, ignoreCase = true) }
    }

    val filteredPosts = remember(query) {
        if (query.isBlank()) emptyList()
        else allPosts.filter { it.content.contains(query, ignoreCase = true) || it.authorName.contains(query, ignoreCase = true) }
    }

    val filteredGroups = remember(query) {
        if (query.isBlank()) emptyList()
        else allGroups.filter { it.name.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true) }
    }

    val filteredMarketplace = remember(query) {
        if (query.isBlank()) emptyList()
        else allMarketplaceItems.filter { it.title.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("search_screen_view")
    ) {
        // Search Input Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }

                    OutlinedTextField(
                        value = query,
                        onValueChange = {
                            query = it
                            if (it.isNotBlank()) onQuerySearch(it)
                        },
                        placeholder = { Text("Search MaxBook...", fontSize = 14.sp) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null)
                        },
                        trailingIcon = {
                            if (query.isNotBlank()) {
                                IconButton(onClick = { query = "" }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("global_search_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    )
                }

                // Category Chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }

        // Recent searches when query is blank
        if (query.isBlank()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Recent Searches",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (searchHistory.isEmpty()) {
                    Text(
                        text = "No recent searches. Search people, groups, posts, or marketplace items.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(searchHistory, key = { it.id }) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { query = item.query }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(text = item.query, fontSize = 14.sp)
                                }
                                IconButton(
                                    onClick = { onDeleteSearchItem(item.query) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Delete", modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Results list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // People
                if ((selectedCategory == "All" || selectedCategory == "People") && filteredUsers.isNotEmpty()) {
                    item {
                        Text(
                            text = "People (${filteredUsers.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    items(filteredUsers, key = { it.id }) { user ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onUserClick(user.id) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AsyncImage(
                                    model = user.avatarUrl,
                                    contentDescription = user.name,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        if (user.isVerified) {
                                            Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = "Verified", tint = VerifiedBadge, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                    Text(text = "@${user.username} • ${user.location}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }

                // Groups
                if ((selectedCategory == "All" || selectedCategory == "Groups") && filteredGroups.isNotEmpty()) {
                    item {
                        Text(
                            text = "Groups (${filteredGroups.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    items(filteredGroups, key = { it.id }) { grp ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onGroupClick(grp) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AsyncImage(
                                    model = grp.coverUrl,
                                    contentDescription = grp.name,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = grp.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = "${grp.category} • ${grp.membersCount} members", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }

                // Posts
                if ((selectedCategory == "All" || selectedCategory == "Posts") && filteredPosts.isNotEmpty()) {
                    item {
                        Text(
                            text = "Posts (${filteredPosts.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    items(filteredPosts, key = { it.id }) { post ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPostClick(post.id) }
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(text = post.authorName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = post.content, fontSize = 14.sp, maxLines = 2)
                            }
                        }
                    }
                }

                // Marketplace
                if ((selectedCategory == "All" || selectedCategory == "Marketplace") && filteredMarketplace.isNotEmpty()) {
                    item {
                        Text(
                            text = "Marketplace Listings (${filteredMarketplace.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    items(filteredMarketplace, key = { it.id }) { item ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AsyncImage(
                                    model = item.imageUrl,
                                    contentDescription = item.title,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "$${"%,.2f".format(item.price)} - ${item.title}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = "${item.category} • ${item.location}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
