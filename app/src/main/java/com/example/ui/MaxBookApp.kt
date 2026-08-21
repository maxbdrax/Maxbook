package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.*
import com.example.data.model.ItemCondition
import com.example.ui.components.*
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.auth.AuthDialog
import com.example.ui.screens.feed.FeedScreen
import com.example.ui.screens.friends.FriendsScreen
import com.example.ui.screens.groups.GroupsScreen
import com.example.ui.screens.marketplace.CreateListingDialog
import com.example.ui.screens.marketplace.MarketplaceScreen
import com.example.ui.screens.messenger.ChatDetailScreen
import com.example.ui.screens.messenger.MessengerScreen
import com.example.ui.screens.monetization.MonetizationDialog
import com.example.ui.screens.notifications.NotificationsScreen
import com.example.ui.screens.profile.EditProfileDialog
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.search.SearchScreen
import com.example.ui.screens.settings.MenuScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.wallet.WalletDialog
import com.example.ui.screens.watch.WatchScreen
import kotlinx.coroutines.launch

enum class Screen {
    MAIN,
    PROFILE,
    MESSENGER,
    CHAT_DETAIL,
    NOTIFICATIONS,
    SEARCH,
    ADMIN_DASHBOARD,
    SETTINGS
}

@Composable
fun MaxBookApp(
    viewModel: MaxBookViewModel,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // State Collection
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val posts by viewModel.allPosts.collectAsStateWithLifecycle()
    val stories by viewModel.stories.collectAsStateWithLifecycle()
    val threads by viewModel.chatThreads.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val groups by viewModel.groups.collectAsStateWithLifecycle()
    val pages by viewModel.pages.collectAsStateWithLifecycle()
    val marketplaceItems by viewModel.marketplaceItems.collectAsStateWithLifecycle()
    val friendRequests by viewModel.friendRequests.collectAsStateWithLifecycle()
    val searchHistory by viewModel.recentSearches.collectAsStateWithLifecycle()
    val reports by viewModel.reports.collectAsStateWithLifecycle()
    val unreadNotifsCount by viewModel.unreadNotificationCount.collectAsStateWithLifecycle()

    // Financial & Management State
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val verificationRequests by viewModel.verificationRequests.collectAsStateWithLifecycle()
    val allBoostOrders by viewModel.allBoostOrders.collectAsStateWithLifecycle()
    val monetizationRequests by viewModel.monetizationRequests.collectAsStateWithLifecycle()

    val showWalletDialog by viewModel.showWalletDialog.collectAsStateWithLifecycle()
    val showMonetizationDialog by viewModel.showMonetizationDialog.collectAsStateWithLifecycle()
    val showBlueVerificationDialog by viewModel.showBlueVerificationDialog.collectAsStateWithLifecycle()
    val activeBoostPost by viewModel.activeBoostPost.collectAsStateWithLifecycle()

    val userToast by viewModel.userToast.collectAsStateWithLifecycle()

    // Listen to toasts
    LaunchedEffect(userToast) {
        userToast?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.userToast.value = null
        }
    }

    // Navigation & Screen State
    var currentScreen by remember { mutableStateOf(Screen.MAIN) }
    var selectedTab by remember { mutableStateOf(MainTab.FEED) }
    var selectedProfileUserId by remember { mutableStateOf<String?>(null) }
    var selectedThreadId by remember { mutableStateOf<String?>(null) }

    // Active Thread messages flow
    val activeThreadMessages by remember(selectedThreadId) {
        if (selectedThreadId != null) {
            viewModel.getMessagesForActiveThread(selectedThreadId!!)
        } else {
            kotlinx.coroutines.flow.flowOf(emptyList())
        }
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    // Modals State
    var showCreatePostSheet by remember { mutableStateOf(false) }
    var activeStoryViewer by remember { mutableStateOf<StoryEntity?>(null) }
    var activeCommentsPostId by remember { mutableStateOf<Long?>(null) }

    val activePostComments by remember(activeCommentsPostId) {
        if (activeCommentsPostId != null) {
            viewModel.getCommentsForActivePost(activeCommentsPostId!!)
        } else {
            kotlinx.coroutines.flow.flowOf(emptyList())
        }
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    var showReportDialog by remember { mutableStateOf(false) }
    var reportTargetInfo by remember { mutableStateOf<Pair<String, String>?>(null) } // (Type, ID)
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showCreateListingDialog by remember { mutableStateOf(false) }
    val showAuthDialog by viewModel.showAuthDialog.collectAsStateWithLifecycle()
    var feedFilter by remember { mutableStateOf("For You") }

    fun showToast(msg: String) {
        scope.launch {
            snackbarHostState.showSnackbar(msg)
        }
    }

    // Back handling
    BackHandler(enabled = currentScreen != Screen.MAIN || selectedTab != MainTab.FEED) {
        if (currentScreen != Screen.MAIN) {
            currentScreen = Screen.MAIN
        } else if (selectedTab != MainTab.FEED) {
            selectedTab = MainTab.FEED
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("maxbook_scaffold"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (currentScreen == Screen.MAIN && selectedTab != MainTab.MENU) {
                MaxBookTopBar(
                    currentUser = currentUser,
                    unreadNotifications = unreadNotifsCount,
                    unreadMessages = threads.sumOf { it.unreadCount },
                    isDarkMode = isDarkMode,
                    onSearchClick = { currentScreen = Screen.SEARCH },
                    onMessengerClick = { currentScreen = Screen.MESSENGER },
                    onNotificationsClick = { currentScreen = Screen.NOTIFICATIONS },
                    onCreatePostClick = { showCreatePostSheet = true },
                    onToggleDarkMode = onToggleDarkMode,
                    onProfileClick = {
                        selectedProfileUserId = currentUser?.id
                        currentScreen = Screen.PROFILE
                    }
                )
            }
        },
        bottomBar = {
            if (currentScreen == Screen.MAIN) {
                MaxBookBottomBar(
                    selectedTab = selectedTab,
                    friendRequestsCount = friendRequests.size,
                    onTabSelected = { selectedTab = it }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (currentScreen) {
                Screen.MAIN -> {
                    when (selectedTab) {
                        MainTab.FEED -> {
                            FeedScreen(
                                currentUser = currentUser,
                                stories = stories,
                                posts = posts,
                                feedFilter = feedFilter,
                                onFilterChange = { feedFilter = it },
                                onCreatePostClick = { showCreatePostSheet = true },
                                onCreateStoryClick = {
                                    viewModel.addStory(
                                        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800",
                                        "Chasing sunsets! 🌅"
                                    )
                                    showToast("Story shared!")
                                },
                                onStoryClick = { story -> activeStoryViewer = story },
                                onReactToPost = { postId, reaction -> viewModel.reactToPost(postId, reaction) },
                                onCommentClick = { postId -> activeCommentsPostId = postId },
                                onShareClick = { post ->
                                    viewModel.createPost(
                                        CreatePostDraft(
                                            content = "Shared post from @${post.authorUsername}: ${post.content}",
                                            imageUrl = post.imageUrl,
                                            videoUrl = post.videoUrl
                                        )
                                    )
                                    showToast("Post shared to your timeline!")
                                },
                                onSaveClick = { postId -> viewModel.toggleSavePost(postId) },
                                onAuthorClick = { authorId ->
                                    selectedProfileUserId = authorId
                                    currentScreen = Screen.PROFILE
                                },
                                onPollVote = { postId, optionIndex -> viewModel.votePoll(postId, optionIndex) },
                                onDeletePost = { postId -> viewModel.deletePost(postId) },
                                onReportPost = { postId ->
                                    reportTargetInfo = Pair("POST", postId.toString())
                                    showReportDialog = true
                                },
                                onBlockUser = { userId -> viewModel.blockUser(userId) },
                                onBoostPost = { post -> viewModel.activeBoostPost.value = post }
                            )
                        }

                        MainTab.WATCH -> {
                            WatchScreen(
                                videoPosts = posts.filter { !it.videoUrl.isNullOrBlank() || it.imageUrl != null },
                                currentUser = currentUser,
                                onReact = { postId, reaction -> viewModel.reactToPost(postId, reaction) },
                                onCommentClick = { postId -> activeCommentsPostId = postId },
                                onShareClick = { post ->
                                    viewModel.createPost(
                                        CreatePostDraft(
                                            content = "Shared video: ${post.content}",
                                            videoUrl = post.videoUrl,
                                            imageUrl = post.imageUrl
                                        )
                                    )
                                    showToast("Video shared!")
                                },
                                onSaveClick = { postId -> viewModel.toggleSavePost(postId) },
                                onAuthorClick = { authorId ->
                                    selectedProfileUserId = authorId
                                    currentScreen = Screen.PROFILE
                                }
                            )
                        }

                        MainTab.MARKETPLACE -> {
                            MarketplaceScreen(
                                items = marketplaceItems,
                                currentUser = currentUser,
                                onItemClick = { /* Click */ },
                                onCreateListingClick = { showCreateListingDialog = true },
                                onContactSeller = { sellerId ->
                                    val targetUser = allUsers.firstOrNull { it.id == sellerId }
                                    if (targetUser != null) {
                                        viewModel.startChatWith(targetUser)
                                        selectedThreadId = "chat_${currentUser?.id}_${targetUser.id}"
                                        currentScreen = Screen.CHAT_DETAIL
                                    }
                                }
                            )
                        }

                        MainTab.GROUPS -> {
                            GroupsScreen(
                                groups = groups,
                                pages = pages,
                                onToggleJoinGroup = { groupId, join -> viewModel.toggleJoinGroup(groupId, join) },
                                onToggleFollowPage = { pageId, follow -> viewModel.toggleFollowPage(pageId, follow) },
                                onCreateGroupClick = {
                                    viewModel.createGroup("New MaxBook Tribe", "Community", "Discussion & innovation community", "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=800")
                                },
                                onCreatePageClick = {
                                    viewModel.createPage("Official Creator Studio", "Creator", "Digital Creator and Media Studio", "https://images.unsplash.com/photo-1519681393784-d120267933ba?w=800", "https://maxbook.app")
                                },
                                onGroupClick = { /* Group click */ },
                                onPageClick = { /* Page click */ }
                            )
                        }

                        MainTab.FRIENDS -> {
                            FriendsScreen(
                                friendRequests = friendRequests,
                                suggestedUsers = allUsers.filter { it.id != currentUser?.id && !it.isFriend },
                                friends = allUsers.filter { it.isFriend },
                                onAcceptRequest = { req ->
                                    viewModel.respondToFriendRequest(req.id, req.requesterId, true)
                                },
                                onDeclineRequest = { req ->
                                    viewModel.respondToFriendRequest(req.id, req.requesterId, false)
                                },
                                onAddFriend = { userId ->
                                    viewModel.toggleFriend(userId, true)
                                },
                                onMessageUser = { user ->
                                    viewModel.startChatWith(user)
                                    selectedThreadId = "chat_${currentUser?.id}_${user.id}"
                                    currentScreen = Screen.CHAT_DETAIL
                                },
                                onProfileClick = { userId ->
                                    selectedProfileUserId = userId
                                    currentScreen = Screen.PROFILE
                                }
                            )
                        }

                        MainTab.MENU -> {
                            MenuScreen(
                                currentUser = currentUser,
                                isDarkMode = isDarkMode,
                                onToggleDarkMode = onToggleDarkMode,
                                onProfileClick = {
                                    selectedProfileUserId = currentUser?.id
                                    currentScreen = Screen.PROFILE
                                },
                                onNavigateTab = { tab -> selectedTab = tab },
                                onOpenSettings = { currentScreen = Screen.SETTINGS },
                                onOpenAdminDashboard = { currentScreen = Screen.ADMIN_DASHBOARD },
                                onOpenWallet = { viewModel.showWalletDialog.value = true },
                                onOpenMonetization = { viewModel.showMonetizationDialog.value = true },
                                onOpenBlueVerification = { viewModel.showBlueVerificationDialog.value = true },
                                onOpenSavedPosts = {
                                    feedFilter = "Saved"
                                    selectedTab = MainTab.FEED
                                },
                                onLogout = { viewModel.showAuthDialog.value = true }
                            )
                        }
                    }
                }

                Screen.PROFILE -> {
                    val profileUser = allUsers.firstOrNull { it.id == selectedProfileUserId } ?: currentUser
                    if (profileUser != null) {
                        ProfileScreen(
                            user = profileUser,
                            currentUser = currentUser,
                            userPosts = posts.filter { it.authorId == profileUser.id },
                            allFriends = allUsers.filter { it.isFriend },
                            onEditProfileClick = { showEditProfileDialog = true },
                            onAddStoryClick = {
                                viewModel.addStory(
                                    "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=800",
                                    "Exploring the heights! ⛰️"
                                )
                            },
                            onMessageClick = { user ->
                                viewModel.startChatWith(user)
                                selectedThreadId = "chat_${currentUser?.id}_${user.id}"
                                currentScreen = Screen.CHAT_DETAIL
                            },
                            onToggleFriend = { userId, friend ->
                                viewModel.toggleFriend(userId, friend)
                            },
                            onToggleFollow = { userId, follow ->
                                viewModel.toggleFollow(userId, follow)
                            },
                            onBlockUser = { userId ->
                                viewModel.blockUser(userId)
                                currentScreen = Screen.MAIN
                            },
                            onReportUser = { userId ->
                                reportTargetInfo = Pair("USER", userId)
                                showReportDialog = true
                            },
                            onAdminClick = { currentScreen = Screen.ADMIN_DASHBOARD },
                            onReactToPost = { postId, reaction -> viewModel.reactToPost(postId, reaction) },
                            onCommentClick = { postId -> activeCommentsPostId = postId },
                            onShareClick = { post ->
                                viewModel.createPost(
                                    CreatePostDraft(
                                        content = "Shared: ${post.content}",
                                        imageUrl = post.imageUrl,
                                        videoUrl = post.videoUrl
                                    )
                                )
                            },
                            onSaveClick = { postId -> viewModel.toggleSavePost(postId) },
                            onDeletePost = { postId -> viewModel.deletePost(postId) },
                            onPollVote = { postId, index -> viewModel.votePoll(postId, index) },
                            onOpenWallet = { viewModel.showWalletDialog.value = true },
                            onOpenMonetization = { viewModel.showMonetizationDialog.value = true },
                            onOpenBlueVerification = { viewModel.showBlueVerificationDialog.value = true },
                            onBoostPost = { post -> viewModel.activeBoostPost.value = post }
                        )
                    }
                }

                Screen.MESSENGER -> {
                    MessengerScreen(
                        threads = threads,
                        allUsers = allUsers,
                        onSelectThread = { threadId ->
                            selectedThreadId = threadId
                            currentScreen = Screen.CHAT_DETAIL
                        },
                        onStartNewChat = { user ->
                            viewModel.startChatWith(user)
                            selectedThreadId = "chat_${currentUser?.id}_${user.id}"
                            currentScreen = Screen.CHAT_DETAIL
                        },
                        onBack = { currentScreen = Screen.MAIN }
                    )
                }

                Screen.CHAT_DETAIL -> {
                    val currentThread = threads.firstOrNull { it.id == selectedThreadId }
                    ChatDetailScreen(
                        thread = currentThread,
                        messages = activeThreadMessages,
                        currentUser = currentUser,
                        onBack = { currentScreen = Screen.MESSENGER },
                        onSendMessage = { text, mediaUrl, isAudio, duration ->
                            selectedThreadId?.let { threadId ->
                                viewModel.sendMessage(threadId, text, mediaUrl, isAudio, duration)
                            }
                        }
                    )
                }

                Screen.NOTIFICATIONS -> {
                    NotificationsScreen(
                        notifications = notifications,
                        onNotificationClick = { notif ->
                            currentScreen = Screen.MAIN
                        },
                        onMarkAllRead = {
                            viewModel.markNotificationsRead()
                            showToast("All notifications marked as read")
                        },
                        onBack = { currentScreen = Screen.MAIN }
                    )
                }

                Screen.SEARCH -> {
                    SearchScreen(
                        searchHistory = searchHistory,
                        allUsers = allUsers,
                        allPosts = posts,
                        allGroups = groups,
                        allMarketplaceItems = marketplaceItems,
                        onQuerySearch = { query -> viewModel.addSearchQuery(query) },
                        onDeleteSearchItem = { query -> /* delete */ },
                        onUserClick = { userId ->
                            selectedProfileUserId = userId
                            currentScreen = Screen.PROFILE
                        },
                        onGroupClick = { _ ->
                            selectedTab = MainTab.GROUPS
                            currentScreen = Screen.MAIN
                        },
                        onPostClick = { _ ->
                            selectedTab = MainTab.FEED
                            currentScreen = Screen.MAIN
                        },
                        onBack = { currentScreen = Screen.MAIN }
                    )
                }

                Screen.ADMIN_DASHBOARD -> {
                    AdminDashboardScreen(
                        allUsers = allUsers,
                        allTransactions = allTransactions,
                        verificationRequests = verificationRequests,
                        boostOrders = allBoostOrders,
                        monetizationRequests = monetizationRequests,
                        reports = reports,
                        currentUser = currentUser,
                        onApproveDeposit = { txId -> viewModel.adminApproveDeposit(txId) },
                        onRejectDeposit = { txId -> viewModel.adminRejectDeposit(txId) },
                        onApproveWithdrawal = { txId -> viewModel.adminApproveWithdrawal(txId) },
                        onRejectWithdrawal = { txId -> viewModel.adminRejectWithdrawal(txId) },
                        onApproveVerification = { reqId -> viewModel.adminApproveVerification(reqId) },
                        onRejectVerification = { reqId -> viewModel.adminRejectVerification(reqId) },
                        onToggleBanUser = { userId, ban -> viewModel.adminSetBan(userId, ban) },
                        onChangeUserRole = { userId, role -> viewModel.adminChangeRole(userId, role) },
                        onModifyUserBalance = { userId, bal -> viewModel.adminModifyBalance(userId, bal) },
                        onToggleUserVerified = { userId, ver -> viewModel.adminToggleVerified(userId, ver) },
                        onToggleUserMonetized = { userId, mon -> viewModel.adminToggleMonetized(userId, mon) },
                        onResolveReport = { reportId, status ->
                            showToast("Report resolved: $status")
                        },
                        onBack = { currentScreen = Screen.MAIN }
                    )
                }

                Screen.SETTINGS -> {
                    SettingsScreen(
                        currentUser = currentUser,
                        isDarkMode = isDarkMode,
                        onToggleDarkMode = onToggleDarkMode,
                        onBack = { currentScreen = Screen.MAIN }
                    )
                }
            }
        }
    }

    // Modal Sheets & Dialogs

    // Create Post Sheet
    if (showCreatePostSheet) {
        CreatePostSheet(
            currentUser = currentUser,
            onDismiss = { showCreatePostSheet = false },
            onSubmitPost = { draft ->
                viewModel.createPost(draft)
                showCreatePostSheet = false
            }
        )
    }

    // Story Viewer Dialog
    activeStoryViewer?.let { story ->
        StoryViewerDialog(
            story = story,
            onDismiss = { activeStoryViewer = null },
            onSendReaction = { emoji ->
                showToast("Sent $emoji reaction to ${story.authorName}")
            }
        )
    }

    // Comments Bottom Sheet
    activeCommentsPostId?.let { postId ->
        CommentSheet(
            postId = postId,
            comments = activePostComments,
            currentUser = currentUser,
            onDismiss = { activeCommentsPostId = null },
            onAddComment = { text, parentId ->
                viewModel.addComment(postId, text, parentId)
            },
            onLikeComment = { commentId ->
                viewModel.likeComment(commentId)
            }
        )
    }

    // Report Dialog
    if (showReportDialog && reportTargetInfo != null) {
        val (targetType, targetId) = reportTargetInfo!!
        ReportDialog(
            targetType = targetType,
            targetId = targetId,
            onDismiss = {
                showReportDialog = false
                reportTargetInfo = null
            },
            onSubmitReport = { reason, notes ->
                viewModel.reportContent(targetType, targetId, reason, notes)
                showReportDialog = false
                reportTargetInfo = null
            }
        )
    }

    // Edit Profile Dialog
    if (showEditProfileDialog && currentUser != null) {
        EditProfileDialog(
            user = currentUser!!,
            onDismiss = { showEditProfileDialog = false },
            onSave = { name, bio, workplace, location, avatar, cover ->
                viewModel.updateProfile(name, bio, workplace, location, avatar, cover)
            }
        )
    }

    // Create Marketplace Listing Dialog
    if (showCreateListingDialog) {
        CreateListingDialog(
            currentUser = currentUser,
            onDismiss = { showCreateListingDialog = false },
            onSubmitListing = { title, price, category, condition, location, desc, img ->
                viewModel.createMarketplaceItem(
                    title = title,
                    price = price,
                    category = category,
                    location = location,
                    description = desc,
                    imageUrl = img,
                    condition = ItemCondition.LIKE_NEW
                )
            }
        )
    }

    // Auth / Login / Register Dialog
    if (showAuthDialog) {
        AuthDialog(
            allUsers = allUsers,
            onDismiss = { viewModel.showAuthDialog.value = false },
            onLogin = { emailOrUser, pass ->
                viewModel.login(emailOrUser, pass)
            },
            onRegister = { name, username, email, phone, pass, avatar ->
                viewModel.register(name, username, email, phone, pass, avatar)
            }
        )
    }

    // Wallet Dialog (bKash / Nagad / Rocket)
    if (showWalletDialog) {
        val userTransactions = allTransactions.filter { it.userId == currentUser?.id }
        WalletDialog(
            currentUser = currentUser,
            userTransactions = userTransactions,
            onDismiss = { viewModel.showWalletDialog.value = false },
            onDeposit = { amount, method, senderNumber, trxId ->
                viewModel.deposit(amount, method, senderNumber, trxId)
            },
            onWithdraw = { amount, method, recipientNumber ->
                viewModel.withdraw(amount, method, recipientNumber)
            }
        )
    }

    // Monetization Hub Dialog (5K followers requirement & payout setup)
    if (showMonetizationDialog) {
        MonetizationDialog(
            currentUser = currentUser,
            onDismiss = { viewModel.showMonetizationDialog.value = false },
            onApply = { payoutMethod, payoutNumber ->
                viewModel.applyForMonetization(payoutMethod, payoutNumber)
            },
            onClaimDailyReward = {
                viewModel.claimDailyCreatorReward()
            },
            onOpenWallet = {
                viewModel.showMonetizationDialog.value = false
                viewModel.showWalletDialog.value = true
            }
        )
    }

    // Blue Verification Badge Dialog (৳500)
    if (showBlueVerificationDialog) {
        BlueVerificationDialog(
            currentUser = currentUser,
            onDismiss = { viewModel.showBlueVerificationDialog.value = false },
            onBuyOrApply = { method, trxId ->
                viewModel.applyOrBuyBlueBadge(method, trxId)
            },
            onOpenWallet = {
                viewModel.showBlueVerificationDialog.value = false
                viewModel.showWalletDialog.value = true
            }
        )
    }

    // Boost Post Dialog
    activeBoostPost?.let { post ->
        BoostPostDialog(
            post = post,
            currentUser = currentUser,
            onDismiss = { viewModel.activeBoostPost.value = null },
            onConfirmBoost = { budget, reach, days, method, trxId ->
                viewModel.boostPost(post.id, budget, reach, days, method, trxId)
            },
            onOpenWallet = {
                viewModel.activeBoostPost.value = null
                viewModel.showWalletDialog.value = true
            }
        )
    }
}
