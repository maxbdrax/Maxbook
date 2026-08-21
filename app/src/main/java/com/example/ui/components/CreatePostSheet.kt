package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.UserEntity
import com.example.data.model.PrivacyLevel
import com.example.ui.CreatePostDraft
import com.example.ui.theme.MaxBookBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostSheet(
    currentUser: UserEntity?,
    onDismiss: () -> Unit,
    onSubmitPost: (CreatePostDraft) -> Unit
) {
    var content by remember { mutableStateOf("") }
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }
    var selectedVideoUrl by remember { mutableStateOf<String?>(null) }
    var selectedFeeling by remember { mutableStateOf<String?>(null) }
    var selectedLocation by remember { mutableStateOf<String?>(null) }
    var privacyLevel by remember { mutableStateOf(PrivacyLevel.PUBLIC) }

    // Poll mode
    var isPollMode by remember { mutableStateOf(false) }
    var pollQuestion by remember { mutableStateOf("") }
    var pollOptions by remember { mutableStateOf(listOf("", "")) }

    var showPrivacyMenu by remember { mutableStateOf(false) }
    var showFeelingSheet by remember { mutableStateOf(false) }
    var showLocationInput by remember { mutableStateOf(false) }
    var locationInputText by remember { mutableStateOf("") }
    var showPhotoPicker by remember { mutableStateOf(false) }

    val presetImages = listOf(
        "https://images.unsplash.com/photo-1519681393784-d120267933ba?w=800",
        "https://images.unsplash.com/photo-1541701494587-cb58502866ab?w=800",
        "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=800",
        "https://images.unsplash.com/photo-1518770660439-4636190af475?w=800",
        "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=800",
        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("create_post_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }

                Text(
                    text = "Create Post",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Button(
                    onClick = {
                        val validPollOptions = if (isPollMode) pollOptions.filter { it.isNotBlank() } else emptyList()
                        onSubmitPost(
                            CreatePostDraft(
                                content = content,
                                imageUrl = selectedImageUrl,
                                videoUrl = selectedVideoUrl,
                                feelingActivity = selectedFeeling,
                                location = selectedLocation,
                                privacyLevel = privacyLevel,
                                pollQuestion = if (isPollMode && pollQuestion.isNotBlank()) pollQuestion else null,
                                pollOptions = validPollOptions
                            )
                        )
                    },
                    enabled = content.isNotBlank() || selectedImageUrl != null || (isPollMode && pollQuestion.isNotBlank()),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaxBookBlue
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("submit_post_action_button")
                ) {
                    Text(text = "POST", fontWeight = FontWeight.Bold)
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // User Info & Privacy Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = currentUser?.avatarUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400",
                    contentDescription = "Your Avatar",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = currentUser?.name ?: "You",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Privacy Level Selector Chip
                    Box {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { showPrivacyMenu = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = when (privacyLevel) {
                                        PrivacyLevel.PUBLIC -> Icons.Default.Public
                                        PrivacyLevel.FRIENDS -> Icons.Default.People
                                        PrivacyLevel.ONLY_ME -> Icons.Default.Lock
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = privacyLevel.label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showPrivacyMenu,
                            onDismissRequest = { showPrivacyMenu = false }
                        ) {
                            PrivacyLevel.values().forEach { level ->
                                DropdownMenuItem(
                                    text = { Text(level.label) },
                                    onClick = {
                                        privacyLevel = level
                                        showPrivacyMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = when (level) {
                                                PrivacyLevel.PUBLIC -> Icons.Default.Public
                                                PrivacyLevel.FRIENDS -> Icons.Default.People
                                                PrivacyLevel.ONLY_ME -> Icons.Default.Lock
                                            },
                                            contentDescription = null
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Feeling / Location tags preview
            if (!selectedFeeling.isNullOrBlank() || !selectedLocation.isNullOrBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectedFeeling?.let { feeling ->
                        InputChip(
                            selected = true,
                            onClick = { selectedFeeling = null },
                            label = { Text(feeling, fontSize = 12.sp) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove feeling",
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        )
                    }

                    selectedLocation?.let { loc ->
                        InputChip(
                            selected = true,
                            onClick = { selectedLocation = null },
                            label = { Text("📍 $loc", fontSize = 12.sp) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove location",
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        )
                    }
                }
            }

            // Main Content Input
            TextField(
                value = content,
                onValueChange = { content = it },
                placeholder = {
                    Text(
                        text = "What's on your mind, ${currentUser?.name?.split(" ")?.firstOrNull() ?: "there"}?",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("create_post_text_field"),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                minLines = 4
            )

            // Attached Image Preview
            selectedImageUrl?.let { url ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    AsyncImage(
                        model = url,
                        contentDescription = "Attached photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { selectedImageUrl = null },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove Photo",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Attached Video Preview
            selectedVideoUrl?.let {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(180.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = "Video attached",
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = "Video attached (Sample 60FPS Video)",
                                color = Color.White,
                                fontSize = 13.sp
                            )
                        }
                        IconButton(
                            onClick = { selectedVideoUrl = null },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove video",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            // Interactive Poll Creator
            AnimatedVisibility(visible = isPollMode) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Create a Poll",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            IconButton(onClick = { isPollMode = false }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Close Poll")
                            }
                        }

                        OutlinedTextField(
                            value = pollQuestion,
                            onValueChange = { pollQuestion = it },
                            placeholder = { Text("Ask a question...") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )

                        pollOptions.forEachIndexed { index, option ->
                            OutlinedTextField(
                                value = option,
                                onValueChange = { updated ->
                                    val newOptions = pollOptions.toMutableList()
                                    newOptions[index] = updated
                                    pollOptions = newOptions
                                },
                                placeholder = { Text("Option ${index + 1}") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        if (pollOptions.size < 5) {
                            TextButton(
                                onClick = { pollOptions = pollOptions + "" }
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Option")
                            }
                        }
                    }
                }
            }

            // Photo Preset Picker Tray (Expandable)
            AnimatedVisibility(visible = showPhotoPicker) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Choose Photo Preset",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(presetImages) { url ->
                            AsyncImage(
                                model = url,
                                contentDescription = "Preset Photo",
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        selectedImageUrl = url
                                        showPhotoPicker = false
                                    },
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            // Location Input Dialog/Sheet
            AnimatedVisibility(visible = showLocationInput) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = locationInputText,
                        onValueChange = { locationInputText = it },
                        placeholder = { Text("Enter city or place...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Button(
                        onClick = {
                            if (locationInputText.isNotBlank()) {
                                selectedLocation = locationInputText
                                showLocationInput = false
                                locationInputText = ""
                            }
                        }
                    ) {
                        Text("Add")
                    }
                }
            }

            // Bottom Add to Post Toolbar
            Surface(
                tonalElevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Add to your post",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        // Photo
                        IconButton(onClick = { showPhotoPicker = !showPhotoPicker }) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Photo",
                                tint = Color(0xFF45BD62)
                            )
                        }
                        // Video
                        IconButton(onClick = {
                            selectedVideoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
                        }) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = "Video",
                                tint = Color(0xFFF3425F)
                            )
                        }
                        // Feelings
                        IconButton(onClick = {
                            selectedFeeling = listOf(
                                "feeling energized ⚡",
                                "feeling grateful 🙏",
                                "feeling inspired 💡",
                                "celebrating 🚀",
                                "traveling ✈️"
                            ).random()
                        }) {
                            Icon(
                                imageVector = Icons.Default.EmojiEmotions,
                                contentDescription = "Feeling/Activity",
                                tint = Color(0xFFF7B125)
                            )
                        }
                        // Location
                        IconButton(onClick = { showLocationInput = !showLocationInput }) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = Color(0xFFFA383E)
                            )
                        }
                        // Poll
                        IconButton(onClick = { isPollMode = !isPollMode }) {
                            Icon(
                                imageVector = Icons.Default.Poll,
                                contentDescription = "Poll",
                                tint = Color(0xFF1877F2)
                            )
                        }
                    }
                }
            }
        }
    }
}
