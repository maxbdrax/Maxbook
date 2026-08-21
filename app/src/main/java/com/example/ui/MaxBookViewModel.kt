package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.*
import com.example.data.model.*
import com.example.data.repository.MaxBookRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class MainTab(val title: String, val iconName: String) {
    FEED("Feed", "home"),
    WATCH("Watch", "video"),
    MARKETPLACE("Market", "store"),
    GROUPS("Groups", "groups"),
    FRIENDS("Friends", "people"),
    MENU("Menu", "menu")
}

data class CreatePostDraft(
    val content: String = "",
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val feelingActivity: String? = null,
    val location: String? = null,
    val privacyLevel: PrivacyLevel = PrivacyLevel.PUBLIC,
    val pollQuestion: String? = null,
    val pollOptions: List<String> = emptyList()
)

class MaxBookViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = MaxBookRepository(database.maxBookDao())

    // --- Active UI State ---
    val currentUser = repository.currentUser.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val allUsers = repository.allUsers.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val friends = repository.friends.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val blockedUsers = repository.blockedUsers.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPosts = repository.allPosts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val savedPosts = repository.savedPosts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val videoPosts = repository.videoPosts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stories = repository.allStories.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val chatThreads = repository.chatThreads.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val notifications = repository.notifications.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val unreadNotificationCount = repository.unreadNotificationCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val groups = repository.allGroups.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val pages = repository.allPages.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val marketplaceItems = repository.marketplaceItems.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val friendRequests = repository.pendingFriendRequests.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val recentSearches = repository.recentSearches.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val reports = repository.allReports.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Financial, Monetization & Boost State ---
    val allTransactions = repository.allTransactions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val verificationRequests = repository.verificationRequests.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allBoostOrders = repository.allBoostOrders.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val monetizationRequests = repository.monetizationRequests.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Navigation & View States
    val activeTab = MutableStateFlow(MainTab.FEED)
    val isDarkMode = MutableStateFlow(false)
    val searchQuery = MutableStateFlow("")

    // Active Modal/Sheet States
    val showCreatePostSheet = MutableStateFlow(false)
    val showCreateStoryDialog = MutableStateFlow(false)
    val activeStoryViewer = MutableStateFlow<StoryEntity?>(null)
    val activeCommentsPostId = MutableStateFlow<Long?>(null)
    val activeChatThreadId = MutableStateFlow<String?>(null)
    val viewingProfileUserId = MutableStateFlow<String?>(null)
    val viewingMarketplaceItem = MutableStateFlow<MarketplaceItemEntity?>(null)
    val showCreateMarketplaceDialog = MutableStateFlow(false)
    val showCreateGroupDialog = MutableStateFlow(false)
    val showCreatePageDialog = MutableStateFlow(false)
    val showAdminDashboard = MutableStateFlow(false)
    val showSettingsScreen = MutableStateFlow(false)
    val showAuthDialog = MutableStateFlow(false)
    val showReportDialog = MutableStateFlow<Pair<String, String>?>(null) // Pair(type, id)
    val showUserSearch = MutableStateFlow(false)
    val showWalletDialog = MutableStateFlow(false)
    val showMonetizationDialog = MutableStateFlow(false)
    val showBlueVerificationDialog = MutableStateFlow(false)
    val activeBoostPost = MutableStateFlow<PostEntity?>(null)


    // SnackBar / Alert message
    val userToast = MutableStateFlow<String?>(null)

    // Feed filter
    val feedFilter = MutableStateFlow("For You") // "For You", "Following", "Trending", "Saved"

    // Dynamic Comments for active post
    fun getCommentsForActivePost(postId: Long): Flow<List<CommentEntity>> {
        return repository.getCommentsForPost(postId)
    }

    // Dynamic Messages for active thread
    fun getMessagesForActiveThread(threadId: String): Flow<List<MessageEntity>> {
        return repository.getMessagesForThread(threadId)
    }

    fun toggleDarkMode() {
        isDarkMode.value = !isDarkMode.value
    }

    fun showToast(msg: String) {
        userToast.value = msg
    }

    fun clearToast() {
        userToast.value = null
    }

    // --- Actions ---
    fun reactToPost(postId: Long, reaction: ReactionType) {
        viewModelScope.launch {
            repository.reactToPost(postId, reaction)
        }
    }

    fun toggleSavePost(postId: Long) {
        viewModelScope.launch {
            repository.toggleSavePost(postId)
            showToast("Saved to your bookmarks")
        }
    }

    fun votePoll(postId: Long, index: Int) {
        viewModelScope.launch {
            repository.votePollOption(postId, index)
        }
    }

    fun createPost(draft: CreatePostDraft) {
        viewModelScope.launch {
            repository.createPost(
                content = draft.content,
                imageUrl = draft.imageUrl,
                videoUrl = draft.videoUrl,
                feelingActivity = draft.feelingActivity,
                location = draft.location,
                privacyLevel = draft.privacyLevel,
                pollQuestion = draft.pollQuestion,
                pollOptions = draft.pollOptions
            )
            showCreatePostSheet.value = false
            showToast("Post shared to your Feed! 🚀")
        }
    }

    fun deletePost(postId: Long) {
        viewModelScope.launch {
            repository.deletePost(postId)
            showToast("Post removed")
        }
    }

    fun addComment(postId: Long, text: String, parentCommentId: Long? = null) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.addComment(postId, text.trim(), parentCommentId)
        }
    }

    fun likeComment(commentId: Long) {
        viewModelScope.launch {
            repository.likeComment(commentId)
        }
    }

    fun addStory(mediaUrl: String, caption: String) {
        viewModelScope.launch {
            repository.addStory(mediaUrl, caption)
            showCreateStoryDialog.value = false
            showToast("Story added to your 24h feed! 📸")
        }
    }

    fun sendMessage(threadId: String, text: String, mediaUrl: String? = null, isAudio: Boolean = false, audioDuration: Int = 0) {
        if (text.isBlank() && mediaUrl == null && !isAudio) return
        viewModelScope.launch {
            repository.sendMessage(threadId, text.trim(), mediaUrl, isAudio, audioDuration)
        }
    }

    fun startChatWith(user: UserEntity) {
        viewModelScope.launch {
            val threadId = repository.startChatWithUser(user)
            activeChatThreadId.value = threadId
        }
    }

    fun respondToFriendRequest(requestId: Long, requesterId: String, accept: Boolean) {
        viewModelScope.launch {
            repository.respondToFriendRequest(requestId, requesterId, accept)
            showToast(if (accept) "Friend request accepted! 🤝" else "Request removed")
        }
    }

    fun toggleFriend(userId: String, isFriend: Boolean) {
        viewModelScope.launch {
            repository.toggleFriend(userId, isFriend)
            showToast(if (isFriend) "Added to friends" else "Removed from friends")
        }
    }

    fun toggleFollow(userId: String, isFollowing: Boolean) {
        viewModelScope.launch {
            repository.toggleFollow(userId, isFollowing)
        }
    }

    fun blockUser(userId: String) {
        viewModelScope.launch {
            repository.blockUser(userId)
            showToast("User blocked")
        }
    }

    fun unblockUser(userId: String) {
        viewModelScope.launch {
            repository.unblockUser(userId)
            showToast("User unblocked")
        }
    }

    fun reportContent(type: String, id: String, reason: String, notes: String) {
        viewModelScope.launch {
            repository.reportContent(type, id, reason, notes)
            showReportDialog.value = null
            showToast("Report submitted. Our moderation team will review it. Thank you! 🙏")
        }
    }

    fun toggleJoinGroup(groupId: String, isJoined: Boolean) {
        viewModelScope.launch {
            repository.toggleJoinGroup(groupId, isJoined)
            showToast(if (isJoined) "Joined group! 🎉" else "Left group")
        }
    }

    fun toggleFollowPage(pageId: String, isFollowing: Boolean) {
        viewModelScope.launch {
            repository.toggleFollowPage(pageId, isFollowing)
        }
    }

    fun createGroup(name: String, category: String, description: String, coverUrl: String) {
        viewModelScope.launch {
            repository.createGroup(name, category, description, coverUrl)
            showCreateGroupDialog.value = false
            showToast("Group created successfully! 👥")
        }
    }

    fun createPage(name: String, category: String, bio: String, coverUrl: String, website: String) {
        viewModelScope.launch {
            repository.createPage(name, category, bio, coverUrl, website)
            showCreatePageDialog.value = false
            showToast("Page created successfully! 🌟")
        }
    }

    fun createMarketplaceItem(title: String, price: Double, category: String, location: String, description: String, imageUrl: String, condition: ItemCondition) {
        viewModelScope.launch {
            repository.createMarketplaceItem(title, price, category, location, description, imageUrl, condition)
            showCreateMarketplaceDialog.value = false
            showToast("Marketplace listing is now live! 🏷️")
        }
    }

    fun updateProfile(name: String, bio: String, workplace: String, location: String, avatarUrl: String? = null, coverUrl: String? = null) {
        viewModelScope.launch {
            repository.updateCurrentUserProfile(name, bio, workplace, location, avatarUrl, coverUrl)
            showToast("Profile updated successfully! ✨")
        }
    }

    fun switchAccount(userId: String) {
        viewModelScope.launch {
            repository.switchUserAccount(userId)
            showToast("Switched active profile")
        }
    }

    fun markNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    fun addSearchQuery(query: String) {
        viewModelScope.launch {
            repository.addSearchQuery(query)
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            repository.clearSearchHistory()
        }
    }

    // --- Authentication Actions ---
    fun register(name: String, username: String, email: String, phone: String, password: String, avatarUrl: String) {
        viewModelScope.launch {
            repository.registerUser(name, username, email, phone, password, avatarUrl)
            showAuthDialog.value = false
            showToast("স্বাগতম $name! আপনার অ্যাকাউন্ট তৈরি হয়েছে এবং ৳১০০ বোনাস যোগ হয়েছে 🎉")
        }
    }

    fun login(emailOrUsername: String, password: String) {
        viewModelScope.launch {
            val success = repository.loginUser(emailOrUsername, password)
            if (success) {
                showAuthDialog.value = false
                showToast("সফলভাবে লগইন হয়েছে! 🌟")
            } else {
                showToast("ইমেইল বা ইউজারনেম পাওয়া যায়নি")
            }
        }
    }

    // --- Wallet Actions (bKash, Nagad, Rocket) ---
    fun deposit(amount: Double, paymentMethod: String, accountNumber: String, transactionId: String) {
        viewModelScope.launch {
            repository.depositFunds(amount, paymentMethod, accountNumber, transactionId)
            showToast("ডিপোজিট রিকোয়েস্ট জমা হয়েছে (৳$amount via $paymentMethod)। অ্যাডমিন রিভিউ করে ব্যালেন্স যোগ করবেন।")
        }
    }

    fun withdraw(amount: Double, paymentMethod: String, accountNumber: String) {
        viewModelScope.launch {
            val success = repository.withdrawFunds(amount, paymentMethod, accountNumber)
            if (success) {
                showToast("উইথড্র রিকোয়েস্ট জমা হয়েছে (৳$amount via $paymentMethod)। শীঘ্রই টাকা পাঠানো হবে।")
            } else {
                showToast("পর্যাপ্ত ব্যালেন্স নেই! বর্তমান ব্যালেন্স চেক করুন।")
            }
        }
    }

    // --- Admin Wallet Actions ---
    fun adminApproveDeposit(txId: Long) {
        viewModelScope.launch {
            repository.approveDeposit(txId)
            showToast("ডিপোজিট সফলভাবে অনুমোদন করা হয়েছে এবং ইউজার ব্যালেন্স যোগ হয়েছে! ✅")
        }
    }

    fun adminRejectDeposit(txId: Long) {
        viewModelScope.launch {
            repository.rejectDeposit(txId)
            showToast("ডিপোজিট বাতিল করা হয়েছে ❌")
        }
    }

    fun adminApproveWithdrawal(txId: Long) {
        viewModelScope.launch {
            repository.approveWithdrawal(txId)
            showToast("উইথড্র অনুমোদন করা হয়েছে (পেমেন্ট পাঠানো হয়েছে) ✅")
        }
    }

    fun adminRejectWithdrawal(txId: Long) {
        viewModelScope.launch {
            repository.rejectWithdrawal(txId)
            showToast("উইথড্র বাতিল এবং ইউজারের ব্যালেন্স রিফান্ড করা হয়েছে 🔄")
        }
    }

    // --- Blue Verification Badge Actions ---
    fun applyOrBuyBlueBadge(paymentMethod: String, trxId: String = "") {
        viewModelScope.launch {
            val success = repository.applyOrPurchaseBlueVerification(paymentMethod, trxId, 500.0)
            if (success) {
                if (paymentMethod == "WALLET") {
                    showToast("অভিনন্দন! আপনার ব্লু ভেরিফাইড ব্যাজ সক্রিয় হয়েছে 🌟")
                } else {
                    showToast("ভেরিফিকেশন রিকোয়েস্ট জমা হয়েছে ($paymentMethod TrxID: $trxId)। অ্যাডমিন অনুমোদনের পর চালু হবে।")
                }
                showBlueVerificationDialog.value = false
            } else {
                showToast("ওয়ালেটে পর্যাপ্ত ব্যালেন্স নেই (৳৫০০ প্রয়োজন)। ডিপোজিট করুন।")
            }
        }
    }

    fun adminApproveVerification(reqId: Long) {
        viewModelScope.launch {
            repository.approveVerificationRequest(reqId)
            showToast("ইউজারের ব্লু ভেরিফিকেশন ব্যাজ সক্রিয় করা হয়েছে 🌟")
        }
    }

    fun adminRejectVerification(reqId: Long) {
        viewModelScope.launch {
            repository.rejectVerificationRequest(reqId)
            showToast("ভেরিফিকেশন আবেদন বাতিল করা হয়েছে")
        }
    }

    // --- Post Boost Actions ---
    fun boostPost(
        postId: Long,
        budget: Double,
        reach: Int,
        durationDays: Int,
        paymentMethod: String,
        trxId: String = ""
    ) {
        viewModelScope.launch {
            val success = repository.boostPost(postId, budget, reach, durationDays, paymentMethod, trxId)
            if (success) {
                showToast("পোস্ট সফলভাবে বুস্ট করা হয়েছে! 🚀 ($reach রিচ - $durationDays দিন)")
                activeBoostPost.value = null
            } else {
                showToast("ওয়ালেটে পর্যাপ্ত ব্যালেন্স নেই! ডিপোজিট করুন।")
            }
        }
    }

    // --- Creator Monetization (5K Followers Rule) Actions ---
    fun applyForMonetization(payoutMethod: String, payoutNumber: String) {
        viewModelScope.launch {
            val user = currentUser.value
            if ((user?.followersCount ?: 0) >= 5000) {
                repository.applyForMonetization(payoutMethod, payoutNumber)
                showToast("অভিনন্দন! আপনার অ্যাকাউন্টে ক্রিয়েটর মনিটাইজেশন সক্রিয় হয়েছে 💰")
                showMonetizationDialog.value = false
            } else {
                showToast("মনিটাইজেশনের জন্য কমপক্ষে ৫,০০০ ফলোয়ার প্রয়োজন!")
            }
        }
    }

    fun claimDailyCreatorReward() {
        viewModelScope.launch {
            val success = repository.claimDailyCreatorReward(50.0)
            if (success) {
                showToast("দৈনিক ক্রিয়েটর বোনাস ৳৫০ আপনার ওয়ালেটে যোগ হয়েছে! 🎉")
            } else {
                showToast("শুধুমাত্র মনিটাইজড ক্রিয়েটররা এই বোনাস পাবেন।")
            }
        }
    }

    // --- Admin User Control Panel Actions ---
    fun adminSetBan(userId: String, isBanned: Boolean) {
        viewModelScope.launch {
            repository.setBanStatus(userId, isBanned)
            showToast(if (isBanned) "ইউজার ব্যান করা হয়েছে 🚫" else "ইউজার আনব্যান করা হয়েছে ✅")
        }
    }

    fun adminChangeRole(userId: String, role: String) {
        viewModelScope.launch {
            repository.updateUserRole(userId, role)
            showToast("ইউজার রোল আপডেট হয়েছে ($role) 🛡️")
        }
    }

    fun adminModifyBalance(userId: String, newBalance: Double) {
        viewModelScope.launch {
            repository.adminModifyBalance(userId, newBalance)
            showToast("ইউজার ব্যালেন্স সেট করা হয়েছে: ৳$newBalance 💰")
        }
    }

    fun adminToggleVerified(userId: String, isVerified: Boolean) {
        viewModelScope.launch {
            repository.adminToggleVerified(userId, isVerified)
            showToast(if (isVerified) "ব্লু ব্যাজ প্রদান করা হয়েছে 🌟" else "ব্লু ব্যাজ প্রত্যাহার করা হয়েছে")
        }
    }

    fun adminToggleMonetized(userId: String, isMonetized: Boolean) {
        viewModelScope.launch {
            repository.adminToggleMonetized(userId, isMonetized)
            showToast(if (isMonetized) "মনিটাইজেশন সক্রিয় করা হয়েছে 💰" else "মনিটাইজেশন নিষ্ক্রিয় করা হয়েছে")
        }
    }

    fun adminDeleteUser(userId: String) {
        viewModelScope.launch {
            repository.deleteUser(userId)
            showToast("ইউজার সফলভাবে ডিলিট করা হয়েছে 🗑️")
        }
    }
}

