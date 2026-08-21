package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class MaxBookRepository(private val dao: MaxBookDao) {

    // --- Users ---
    val currentUser: Flow<UserEntity?> = dao.getCurrentUser()
    val allUsers: Flow<List<UserEntity>> = dao.getAllUsers()
    val friends: Flow<List<UserEntity>> = dao.getFriends()
    val blockedUsers: Flow<List<UserEntity>> = dao.getBlockedUsers()

    fun getUserById(userId: String): Flow<UserEntity?> = dao.getUserById(userId)

    suspend fun updateCurrentUserProfile(
        name: String,
        bio: String,
        workplace: String,
        location: String,
        avatarUrl: String? = null,
        coverPhotoUrl: String? = null
    ) {
        val current = dao.getCurrentUserDirect() ?: return
        val updated = current.copy(
            name = name,
            bio = bio,
            workplace = workplace,
            location = location,
            avatarUrl = avatarUrl ?: current.avatarUrl,
            coverPhotoUrl = coverPhotoUrl ?: current.coverPhotoUrl
        )
        dao.updateUser(updated)
    }

    suspend fun switchUserAccount(userId: String) {
        dao.clearCurrentUserFlag()
        dao.setCurrentUser(userId)
    }

    suspend fun toggleFriend(userId: String, isFriend: Boolean) {
        dao.setFriendStatus(userId, isFriend)
    }

    suspend fun toggleFollow(userId: String, isFollowing: Boolean) {
        dao.setFollowingStatus(userId, isFollowing)
    }

    suspend fun blockUser(userId: String) {
        dao.setBlockedStatus(userId, true)
    }

    suspend fun unblockUser(userId: String) {
        dao.setBlockedStatus(userId, false)
    }

    // --- Posts ---
    val allPosts: Flow<List<PostEntity>> = dao.getAllPosts()
    val savedPosts: Flow<List<PostEntity>> = dao.getSavedPosts()
    val videoPosts: Flow<List<PostEntity>> = dao.getVideoPosts()

    fun getPostsByAuthor(userId: String): Flow<List<PostEntity>> = dao.getPostsByAuthor(userId)
    fun getGroupPosts(groupId: String): Flow<List<PostEntity>> = dao.getGroupPosts(groupId)
    fun getPostById(postId: Long): Flow<PostEntity?> = dao.getPostById(postId)
    fun searchPosts(query: String): Flow<List<PostEntity>> = dao.searchPosts(query)

    suspend fun createPost(
        content: String,
        imageUrl: String? = null,
        videoUrl: String? = null,
        feelingActivity: String? = null,
        location: String? = null,
        privacyLevel: PrivacyLevel = PrivacyLevel.PUBLIC,
        groupId: String? = null,
        groupName: String? = null,
        pageId: String? = null,
        pageName: String? = null,
        pollQuestion: String? = null,
        pollOptions: List<String>? = null
    ): Long {
        val user = dao.getCurrentUserDirect() ?: return -1
        val pollOptionsStr = pollOptions?.joinToString(":::")
        val pollVotesStr = pollOptions?.map { "0" }?.joinToString(":::")

        val post = PostEntity(
            authorId = user.id,
            authorName = user.name,
            authorAvatar = user.avatarUrl,
            authorUsername = user.username,
            isVerified = user.isVerified,
            content = content,
            imageUrl = imageUrl,
            videoUrl = videoUrl,
            feelingActivity = feelingActivity,
            location = location,
            privacyLevel = privacyLevel,
            timestamp = System.currentTimeMillis(),
            groupId = groupId,
            groupName = groupName,
            pageId = pageId,
            pageName = pageName,
            pollQuestion = pollQuestion,
            pollOptionsJson = pollOptionsStr,
            pollVotesJson = pollVotesStr
        )
        return dao.insertPost(post)
    }

    suspend fun deletePost(postId: Long) {
        dao.deletePostById(postId)
    }

    suspend fun reactToPost(postId: Long, reaction: ReactionType) {
        val post = dao.getPostByIdDirect(postId) ?: return
        val previousReaction = post.userReaction
        val newReaction = if (previousReaction == reaction) ReactionType.NONE else reaction

        val likesDelta = when {
            previousReaction == ReactionType.NONE && newReaction != ReactionType.NONE -> 1
            previousReaction != ReactionType.NONE && newReaction == ReactionType.NONE -> -1
            else -> 0
        }

        val updated = post.copy(
            userReaction = newReaction,
            likesCount = (post.likesCount + likesDelta).coerceAtLeast(0)
        )
        dao.updatePost(updated)
    }

    suspend fun toggleSavePost(postId: Long) {
        val post = dao.getPostByIdDirect(postId) ?: return
        val newSaveState = !post.isSaved
        dao.toggleSavePost(postId, newSaveState)
    }

    suspend fun votePollOption(postId: Long, optionIndex: Int) {
        val post = dao.getPostByIdDirect(postId) ?: return
        val currentVotes = post.pollVotesJson?.split(":::")?.map { it.toIntOrNull() ?: 0 }?.toMutableList() ?: return

        if (optionIndex in currentVotes.indices) {
            val previousVote = post.selectedPollOptionIndex
            if (previousVote != -1 && previousVote < currentVotes.size) {
                currentVotes[previousVote] = (currentVotes[previousVote] - 1).coerceAtLeast(0)
            }
            if (previousVote != optionIndex) {
                currentVotes[optionIndex] = currentVotes[optionIndex] + 1
                val updated = post.copy(
                    pollVotesJson = currentVotes.joinToString(":::"),
                    selectedPollOptionIndex = optionIndex
                )
                dao.updatePost(updated)
            }
        }
    }

    // --- Comments ---
    fun getCommentsForPost(postId: Long): Flow<List<CommentEntity>> = dao.getCommentsForPost(postId)

    suspend fun addComment(postId: Long, text: String, parentCommentId: Long? = null): Long {
        val user = dao.getCurrentUserDirect() ?: return -1
        val comment = CommentEntity(
            postId = postId,
            authorId = user.id,
            authorName = user.name,
            authorAvatar = user.avatarUrl,
            isVerified = user.isVerified,
            text = text,
            timestamp = System.currentTimeMillis(),
            parentCommentId = parentCommentId
        )
        val commentId = dao.insertComment(comment)

        // Increment post comments count
        val post = dao.getPostByIdDirect(postId)
        if (post != null) {
            dao.updatePost(post.copy(commentsCount = post.commentsCount + 1))
        }
        return commentId
    }

    suspend fun likeComment(commentId: Long) {
        dao.likeComment(commentId)
    }

    // --- Stories ---
    val allStories: Flow<List<StoryEntity>> = dao.getAllStories()

    suspend fun addStory(mediaUrl: String, caption: String = ""): Long {
        val user = dao.getCurrentUserDirect() ?: return -1
        val story = StoryEntity(
            authorId = user.id,
            authorName = user.name,
            authorAvatar = user.avatarUrl,
            mediaUrl = mediaUrl,
            caption = caption,
            timestamp = System.currentTimeMillis(),
            isUserStory = true
        )
        return dao.insertStory(story)
    }

    // --- Messages & Threads ---
    val chatThreads: Flow<List<ChatThreadEntity>> = dao.getAllChatThreads()

    fun getMessagesForThread(threadId: String): Flow<List<MessageEntity>> = dao.getMessagesForThread(threadId)

    suspend fun sendMessage(threadId: String, text: String, mediaUrl: String? = null, isAudioNote: Boolean = false, audioDurationSec: Int = 0): Long {
        val user = dao.getCurrentUserDirect() ?: return -1
        val now = System.currentTimeMillis()
        val message = MessageEntity(
            threadId = threadId,
            senderId = user.id,
            senderName = user.name,
            senderAvatar = user.avatarUrl,
            text = text,
            mediaUrl = mediaUrl,
            isAudioNote = isAudioNote,
            audioDurationSec = audioDurationSec,
            timestamp = now,
            isMine = true,
            status = "SENT"
        )
        val id = dao.insertMessage(message)
        dao.updateThreadLastMessage(threadId, text, now)
        return id
    }

    suspend fun startChatWithUser(user: UserEntity): String {
        val threadId = "thread_${user.id}"
        val thread = ChatThreadEntity(
            id = threadId,
            participantId = user.id,
            participantName = user.name,
            participantAvatar = user.avatarUrl,
            isOnline = user.isOnline,
            lastMessage = "Started a conversation",
            lastMessageTimestamp = System.currentTimeMillis(),
            unreadCount = 0
        )
        dao.insertChatThread(thread)
        return threadId
    }

    // --- Notifications ---
    val notifications: Flow<List<NotificationEntity>> = dao.getAllNotifications()
    val unreadNotificationCount: Flow<Int> = dao.getUnreadNotificationCount()

    suspend fun markAllNotificationsAsRead() {
        dao.markAllNotificationsAsRead()
    }

    // --- Groups & Pages ---
    val allGroups: Flow<List<GroupEntity>> = dao.getAllGroups()
    val joinedGroups: Flow<List<GroupEntity>> = dao.getJoinedGroups()
    val allPages: Flow<List<PageEntity>> = dao.getAllPages()

    suspend fun toggleJoinGroup(groupId: String, isJoined: Boolean) {
        dao.setGroupJoinStatus(groupId, isJoined)
    }

    suspend fun toggleFollowPage(pageId: String, isFollowing: Boolean) {
        dao.setPageFollowStatus(pageId, isFollowing)
    }

    suspend fun createGroup(name: String, category: String, description: String, coverUrl: String, privacy: String = "Public Group") {
        val group = GroupEntity(
            id = "grp_${System.currentTimeMillis()}",
            name = name,
            coverUrl = coverUrl,
            avatarUrl = coverUrl,
            description = description,
            privacy = privacy,
            membersCount = 1,
            isJoined = true,
            isModerator = true,
            category = category
        )
        dao.insertGroup(group)
    }

    suspend fun createPage(name: String, category: String, bio: String, coverUrl: String, website: String = "") {
        val page = PageEntity(
            id = "page_${System.currentTimeMillis()}",
            name = name,
            category = category,
            avatarUrl = coverUrl,
            coverUrl = coverUrl,
            bio = bio,
            likesCount = 1,
            followersCount = 1,
            isFollowing = true,
            website = website
        )
        dao.insertPage(page)
    }

    // --- Marketplace ---
    val marketplaceItems: Flow<List<MarketplaceItemEntity>> = dao.getAllMarketplaceItems()

    fun getMarketplaceByCategory(category: String): Flow<List<MarketplaceItemEntity>> = dao.getMarketplaceItemsByCategory(category)

    suspend fun createMarketplaceItem(
        title: String,
        price: Double,
        category: String,
        location: String,
        description: String,
        imageUrl: String,
        condition: ItemCondition
    ): Long {
        val user = dao.getCurrentUserDirect() ?: return -1
        val item = MarketplaceItemEntity(
            title = title,
            price = price,
            category = category,
            location = location,
            description = description,
            imageUrl = imageUrl,
            sellerId = user.id,
            sellerName = user.name,
            sellerAvatar = user.avatarUrl,
            condition = condition,
            timestamp = System.currentTimeMillis()
        )
        return dao.insertMarketplaceItem(item)
    }

    // --- Friend Requests ---
    val pendingFriendRequests: Flow<List<FriendRequestEntity>> = dao.getPendingFriendRequests()

    suspend fun respondToFriendRequest(requestId: Long, requesterId: String, accept: Boolean) {
        val status = if (accept) "ACCEPTED" else "DECLINED"
        dao.updateFriendRequestStatus(requestId, status)
        if (accept) {
            dao.setFriendStatus(requesterId, true)
        }
    }

    // --- Search & History ---
    val recentSearches: Flow<List<SearchHistoryEntity>> = dao.getRecentSearches()

    suspend fun addSearchQuery(query: String) {
        if (query.isNotBlank()) {
            dao.insertSearchQuery(SearchHistoryEntity(query = query.trim()))
        }
    }

    suspend fun clearSearchHistory() {
        dao.clearSearchHistory()
    }

    // --- Reports ---
    val allReports: Flow<List<ReportEntity>> = dao.getAllReports()

    suspend fun reportContent(targetType: String, targetId: String, reason: String, notes: String = ""): Long {
        val user = dao.getCurrentUserDirect()
        val report = ReportEntity(
            targetType = targetType,
            targetId = targetId,
            reportedByUserId = user?.id ?: "anonymous",
            reason = reason,
            notes = notes,
            timestamp = System.currentTimeMillis()
        )
        return dao.insertReport(report)
    }

    suspend fun resolveReport(reportId: Long, action: String) {
        dao.updateReportStatus(reportId, action)
    }

    // --- Authentication & User Accounts ---
    suspend fun registerUser(
        name: String,
        username: String,
        email: String,
        phone: String,
        password: String,
        avatarUrl: String
    ): UserEntity {
        val newUserId = "user_${System.currentTimeMillis()}"
        val newUser = UserEntity(
            id = newUserId,
            name = name,
            username = username.lowercase().replace(" ", "_"),
            email = email,
            phone = phone,
            password = password,
            avatarUrl = avatarUrl.ifBlank { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400" },
            coverPhotoUrl = "https://images.unsplash.com/photo-1707343843437-caacff5cfa74?w=1200",
            bio = "MaxBook Member",
            role = if (email.contains("admin", ignoreCase = true)) "SUPER_ADMIN" else "USER",
            balance = 100.0, // Welcome signup bonus ৳100
            followersCount = 0,
            isCurrentUser = true
        )
        dao.clearCurrentUserFlag()
        dao.insertUser(newUser)
        return newUser
    }

    suspend fun loginUser(emailOrUsername: String, password: String): Boolean {
        val existing = dao.getUserByEmail(emailOrUsername)
        if (existing != null) {
            dao.clearCurrentUserFlag()
            dao.setCurrentUser(existing.id)
            return true
        }
        return false
    }

    // --- Wallet (bKash, Nagad, Rocket) ---
    val allTransactions: Flow<List<WalletTransactionEntity>> = dao.getAllTransactions()

    fun getTransactionsForCurrentUser(userId: String): Flow<List<WalletTransactionEntity>> =
        dao.getTransactionsForUser(userId)

    suspend fun depositFunds(
        amount: Double,
        paymentMethod: String,
        accountNumber: String,
        transactionId: String
    ): Long {
        val user = dao.getCurrentUserDirect() ?: return -1
        val tx = WalletTransactionEntity(
            userId = user.id,
            userName = user.name,
            userPhone = accountNumber,
            type = "DEPOSIT",
            amount = amount,
            paymentMethod = paymentMethod,
            accountNumber = accountNumber,
            transactionId = transactionId,
            status = "PENDING",
            timestamp = System.currentTimeMillis(),
            note = "Deposit via $paymentMethod"
        )
        return dao.insertTransaction(tx)
    }

    suspend fun withdrawFunds(
        amount: Double,
        paymentMethod: String,
        accountNumber: String
    ): Boolean {
        val user = dao.getCurrentUserDirect() ?: return false
        if (user.balance < amount) return false

        // Deduct from user balance
        dao.addBalance(user.id, -amount)

        val tx = WalletTransactionEntity(
            userId = user.id,
            userName = user.name,
            userPhone = accountNumber,
            type = "WITHDRAWAL",
            amount = amount,
            paymentMethod = paymentMethod,
            accountNumber = accountNumber,
            transactionId = "WD-${System.currentTimeMillis() % 1000000}",
            status = "PENDING",
            timestamp = System.currentTimeMillis(),
            note = "Withdrawal request to $accountNumber via $paymentMethod"
        )
        dao.insertTransaction(tx)
        return true
    }

    suspend fun approveDeposit(transactionId: Long) {
        val tx = dao.getTransactionById(transactionId) ?: return
        if (tx.status != "APPROVED") {
            dao.updateTransactionStatus(transactionId, "APPROVED")
            dao.addBalance(tx.userId, tx.amount)
        }
    }

    suspend fun rejectDeposit(transactionId: Long) {
        dao.updateTransactionStatus(transactionId, "REJECTED")
    }

    suspend fun approveWithdrawal(transactionId: Long) {
        dao.updateTransactionStatus(transactionId, "APPROVED")
    }

    suspend fun rejectWithdrawal(transactionId: Long) {
        val tx = dao.getTransactionById(transactionId) ?: return
        if (tx.status != "REJECTED") {
            dao.updateTransactionStatus(transactionId, "REJECTED")
            // Refund balance back to user
            dao.addBalance(tx.userId, tx.amount)
        }
    }

    // --- Blue Verification Badge ---
    val verificationRequests: Flow<List<VerificationRequestEntity>> = dao.getAllVerificationRequests()

    suspend fun applyOrPurchaseBlueVerification(
        paymentMethod: String,
        trxId: String = "",
        fee: Double = 500.0
    ): Boolean {
        val user = dao.getCurrentUserDirect() ?: return false
        if (paymentMethod == "WALLET") {
            if (user.balance < fee) return false
            dao.addBalance(user.id, -fee)
            dao.updateUserVerification(user.id, true)

            // Record transaction
            dao.insertTransaction(
                WalletTransactionEntity(
                    userId = user.id,
                    userName = user.name,
                    type = "VERIFY_PAYMENT",
                    amount = fee,
                    paymentMethod = "WALLET",
                    accountNumber = user.phone,
                    transactionId = "VERIFY-${System.currentTimeMillis() % 1000000}",
                    status = "APPROVED",
                    timestamp = System.currentTimeMillis(),
                    note = "Blue Badge Verification Purchased via Wallet"
                )
            )
            return true
        } else {
            // bKash, Nagad, Rocket external payment
            val req = VerificationRequestEntity(
                userId = user.id,
                userName = user.name,
                userAvatar = user.avatarUrl,
                followersCount = user.followersCount,
                paymentMethod = paymentMethod,
                trxId = trxId,
                amountPaid = fee,
                status = "PENDING"
            )
            dao.insertVerificationRequest(req)
            return true
        }
    }

    suspend fun approveVerificationRequest(reqId: Long) {
        val req = dao.getVerificationRequestById(reqId) ?: return
        dao.updateVerificationRequestStatus(reqId, "APPROVED")
        dao.updateUserVerification(req.userId, true)
    }

    suspend fun rejectVerificationRequest(reqId: Long) {
        dao.updateVerificationRequestStatus(reqId, "REJECTED")
    }

    // --- Post Boosting ---
    val allBoostOrders: Flow<List<BoostOrderEntity>> = dao.getAllBoostOrders()

    suspend fun boostPost(
        postId: Long,
        budget: Double,
        reach: Int,
        durationDays: Int,
        paymentMethod: String,
        trxId: String = ""
    ): Boolean {
        val user = dao.getCurrentUserDirect() ?: return false
        if (paymentMethod == "WALLET") {
            if (user.balance < budget) return false
            dao.addBalance(user.id, -budget)
        }

        val expiresAt = System.currentTimeMillis() + (durationDays * 24 * 60 * 60 * 1000L)
        dao.markPostBoosted(postId, reach, budget, expiresAt)

        dao.insertBoostOrder(
            BoostOrderEntity(
                postId = postId,
                userId = user.id,
                userName = user.name,
                targetReach = reach,
                durationDays = durationDays,
                amountPaid = budget,
                paymentMethod = paymentMethod,
                trxId = trxId,
                status = "ACTIVE"
            )
        )

        dao.insertTransaction(
            WalletTransactionEntity(
                userId = user.id,
                userName = user.name,
                type = "BOOST_PAYMENT",
                amount = budget,
                paymentMethod = paymentMethod,
                accountNumber = user.phone,
                transactionId = if (trxId.isNotBlank()) trxId else "BOOST-${postId}",
                status = "APPROVED",
                timestamp = System.currentTimeMillis(),
                note = "Post #$postId Boosted ($reach Reach for $durationDays days)"
            )
        )
        return true
    }

    // --- Creator Monetization (5K Followers Rule) ---
    val monetizationRequests: Flow<List<MonetizationRequestEntity>> = dao.getAllMonetizationRequests()

    suspend fun applyForMonetization(payoutMethod: String, payoutNumber: String): Boolean {
        val user = dao.getCurrentUserDirect() ?: return false
        if (user.followersCount >= 5000) {
            dao.updateUserMonetization(user.id, true)
            dao.updateUserRole(user.id, "CREATOR")
            dao.insertMonetizationRequest(
                MonetizationRequestEntity(
                    userId = user.id,
                    userName = user.name,
                    userAvatar = user.avatarUrl,
                    followersCount = user.followersCount,
                    payoutMethod = payoutMethod,
                    payoutNumber = payoutNumber,
                    status = "APPROVED"
                )
            )
            return true
        } else {
            return false
        }
    }

    suspend fun claimDailyCreatorReward(amount: Double = 50.0): Boolean {
        val user = dao.getCurrentUserDirect() ?: return false
        if (!user.isMonetized) return false

        dao.creditCreatorEarnings(user.id, amount)
        dao.insertTransaction(
            WalletTransactionEntity(
                userId = user.id,
                userName = user.name,
                type = "EARNING",
                amount = amount,
                paymentMethod = "WALLET",
                accountNumber = user.phone,
                transactionId = "EARN-${System.currentTimeMillis() % 1000000}",
                status = "APPROVED",
                timestamp = System.currentTimeMillis(),
                note = "Daily Creator Monetization Engagement Reward (৳$amount)"
            )
        )
        return true
    }

    // --- Admin User Management ---
    suspend fun setBanStatus(userId: String, isBanned: Boolean) {
        dao.setBannedStatus(userId, isBanned)
    }

    suspend fun updateUserRole(userId: String, role: String) {
        dao.updateUserRole(userId, role)
    }

    suspend fun adminModifyBalance(userId: String, newBalance: Double) {
        dao.setBalance(userId, newBalance)
    }

    suspend fun adminToggleVerified(userId: String, isVerified: Boolean) {
        dao.updateUserVerification(userId, isVerified)
    }

    suspend fun adminToggleMonetized(userId: String, isMonetized: Boolean) {
        dao.updateUserMonetization(userId, isMonetized)
    }

    suspend fun deleteUser(userId: String) {
        dao.deleteUser(userId)
    }
}

