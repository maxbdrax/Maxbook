package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MaxBookDao {

    // --- Users ---
    @Query("SELECT * FROM users WHERE isCurrentUser = 1 LIMIT 1")
    fun getCurrentUser(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE isCurrentUser = 1 LIMIT 1")
    suspend fun getCurrentUserDirect(): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE isBlocked = 0")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE isFriend = 1 AND isBlocked = 0")
    fun getFriends(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE isBlocked = 1")
    fun getBlockedUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isCurrentUser = 0")
    suspend fun clearCurrentUserFlag()

    @Query("UPDATE users SET isCurrentUser = 1 WHERE id = :userId")
    suspend fun setCurrentUser(userId: String)

    @Query("UPDATE users SET isBlocked = :isBlocked WHERE id = :userId")
    suspend fun setBlockedStatus(userId: String, isBlocked: Boolean)

    @Query("UPDATE users SET isFriend = :isFriend WHERE id = :userId")
    suspend fun setFriendStatus(userId: String, isFriend: Boolean)

    @Query("UPDATE users SET isFollowing = :isFollowing WHERE id = :userId")
    suspend fun setFollowingStatus(userId: String, isFollowing: Boolean)

    // --- Posts ---
    @Query("SELECT * FROM posts ORDER BY isPinned DESC, timestamp DESC")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE authorId = :userId ORDER BY timestamp DESC")
    fun getPostsByAuthor(userId: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE isSaved = 1 ORDER BY timestamp DESC")
    fun getSavedPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE videoUrl IS NOT NULL ORDER BY timestamp DESC")
    fun getVideoPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE groupId = :groupId ORDER BY timestamp DESC")
    fun getGroupPosts(groupId: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :postId")
    fun getPostById(postId: Long): Flow<PostEntity?>

    @Query("SELECT * FROM posts WHERE id = :postId")
    suspend fun getPostByIdDirect(postId: Long): PostEntity?

    @Query("SELECT * FROM posts WHERE content LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchPosts(query: String): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Update
    suspend fun updatePost(post: PostEntity)

    @Delete
    suspend fun deletePost(post: PostEntity)

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePostById(postId: Long)

    @Query("UPDATE posts SET isSaved = :isSaved WHERE id = :postId")
    suspend fun toggleSavePost(postId: Long, isSaved: Boolean)

    // --- Comments ---
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY timestamp ASC")
    fun getCommentsForPost(postId: Long): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentEntity>)

    @Delete
    suspend fun deleteComment(comment: CommentEntity)

    @Query("UPDATE comments SET likesCount = likesCount + 1, isLiked = 1 WHERE id = :commentId")
    suspend fun likeComment(commentId: Long)

    // --- Stories ---
    @Query("SELECT * FROM stories ORDER BY timestamp DESC")
    fun getAllStories(): Flow<List<StoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: StoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<StoryEntity>)

    // --- Messages & Threads ---
    @Query("SELECT * FROM chat_threads ORDER BY lastMessageTimestamp DESC")
    fun getAllChatThreads(): Flow<List<ChatThreadEntity>>

    @Query("SELECT * FROM messages WHERE threadId = :threadId ORDER BY timestamp ASC")
    fun getMessagesForThread(threadId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatThread(thread: ChatThreadEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatThreads(threads: List<ChatThreadEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Query("UPDATE chat_threads SET lastMessage = :lastMsg, lastMessageTimestamp = :timestamp WHERE id = :threadId")
    suspend fun updateThreadLastMessage(threadId: String, lastMsg: String, timestamp: Long)

    // --- Notifications ---
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadNotificationCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()

    // --- Groups ---
    @Query("SELECT * FROM groups")
    fun getAllGroups(): Flow<List<GroupEntity>>

    @Query("SELECT * FROM groups WHERE isJoined = 1")
    fun getJoinedGroups(): Flow<List<GroupEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: GroupEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroups(groups: List<GroupEntity>)

    @Query("UPDATE groups SET isJoined = :isJoined, membersCount = membersCount + CASE WHEN :isJoined = 1 THEN 1 ELSE -1 END WHERE id = :groupId")
    suspend fun setGroupJoinStatus(groupId: String, isJoined: Boolean)

    // --- Pages ---
    @Query("SELECT * FROM pages")
    fun getAllPages(): Flow<List<PageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPage(page: PageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPages(pages: List<PageEntity>)

    @Query("UPDATE pages SET isFollowing = :isFollowing, followersCount = followersCount + CASE WHEN :isFollowing = 1 THEN 1 ELSE -1 END WHERE id = :pageId")
    suspend fun setPageFollowStatus(pageId: String, isFollowing: Boolean)

    // --- Marketplace ---
    @Query("SELECT * FROM marketplace_items WHERE isSold = 0 ORDER BY timestamp DESC")
    fun getAllMarketplaceItems(): Flow<List<MarketplaceItemEntity>>

    @Query("SELECT * FROM marketplace_items WHERE category = :category AND isSold = 0 ORDER BY timestamp DESC")
    fun getMarketplaceItemsByCategory(category: String): Flow<List<MarketplaceItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarketplaceItem(item: MarketplaceItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarketplaceItems(items: List<MarketplaceItemEntity>)

    // --- Friend Requests ---
    @Query("SELECT * FROM friend_requests WHERE status = 'PENDING' ORDER BY timestamp DESC")
    fun getPendingFriendRequests(): Flow<List<FriendRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriendRequest(req: FriendRequestEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriendRequests(requests: List<FriendRequestEntity>)

    @Query("UPDATE friend_requests SET status = :status WHERE id = :requestId")
    suspend fun updateFriendRequestStatus(requestId: Long, status: String)

    // --- Search History ---
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 10")
    fun getRecentSearches(): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchQuery(search: SearchHistoryEntity)

    @Query("DELETE FROM search_history")
    suspend fun clearSearchHistory()

    // --- Reports ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity): Long

    @Query("SELECT * FROM reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<ReportEntity>>

    @Query("UPDATE reports SET status = :status WHERE id = :reportId")
    suspend fun updateReportStatus(reportId: Long, status: String)

    // --- User Management & Admin Actions ---
    @Query("UPDATE users SET balance = balance + :amount WHERE id = :userId")
    suspend fun addBalance(userId: String, amount: Double)

    @Query("UPDATE users SET balance = :balance WHERE id = :userId")
    suspend fun setBalance(userId: String, balance: Double)

    @Query("UPDATE users SET role = :role WHERE id = :userId")
    suspend fun updateUserRole(userId: String, role: String)

    @Query("UPDATE users SET isVerified = :isVerified WHERE id = :userId")
    suspend fun updateUserVerification(userId: String, isVerified: Boolean)

    @Query("UPDATE users SET isMonetized = :isMonetized, monetizationApplied = 1 WHERE id = :userId")
    suspend fun updateUserMonetization(userId: String, isMonetized: Boolean)

    @Query("UPDATE users SET totalEarnings = totalEarnings + :amount, balance = balance + :amount WHERE id = :userId")
    suspend fun creditCreatorEarnings(userId: String, amount: Double)

    @Query("UPDATE users SET isBanned = :isBanned WHERE id = :userId")
    suspend fun setBannedStatus(userId: String, isBanned: Boolean)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: String)

    // --- Wallet Transactions (bKash, Nagad, Rocket) ---
    @Query("SELECT * FROM wallet_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<WalletTransactionEntity>>

    @Query("SELECT * FROM wallet_transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTransactionsForUser(userId: String): Flow<List<WalletTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: WalletTransactionEntity): Long

    @Query("UPDATE wallet_transactions SET status = :status WHERE id = :txId")
    suspend fun updateTransactionStatus(txId: Long, status: String)

    @Query("SELECT * FROM wallet_transactions WHERE id = :txId LIMIT 1")
    suspend fun getTransactionById(txId: Long): WalletTransactionEntity?

    // --- Post Boosting ---
    @Query("UPDATE posts SET isBoosted = 1, boostTargetReach = :reach, boostBudget = :budget, boostExpiresAt = :expiresAt WHERE id = :postId")
    suspend fun markPostBoosted(postId: Long, reach: Int, budget: Double, expiresAt: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoostOrder(order: BoostOrderEntity): Long

    @Query("SELECT * FROM boost_orders ORDER BY timestamp DESC")
    fun getAllBoostOrders(): Flow<List<BoostOrderEntity>>

    // --- Verification Requests ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerificationRequest(req: VerificationRequestEntity): Long

    @Query("SELECT * FROM verification_requests ORDER BY timestamp DESC")
    fun getAllVerificationRequests(): Flow<List<VerificationRequestEntity>>

    @Query("UPDATE verification_requests SET status = :status WHERE id = :reqId")
    suspend fun updateVerificationRequestStatus(reqId: Long, status: String)

    @Query("SELECT * FROM verification_requests WHERE id = :reqId LIMIT 1")
    suspend fun getVerificationRequestById(reqId: Long): VerificationRequestEntity?

    // --- Monetization Requests ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonetizationRequest(req: MonetizationRequestEntity): Long

    @Query("SELECT * FROM monetization_requests ORDER BY timestamp DESC")
    fun getAllMonetizationRequests(): Flow<List<MonetizationRequestEntity>>

    @Query("UPDATE monetization_requests SET status = :status WHERE id = :reqId")
    suspend fun updateMonetizationRequestStatus(reqId: Long, status: String)

    @Query("SELECT * FROM monetization_requests WHERE id = :reqId LIMIT 1")
    suspend fun getMonetizationRequestById(reqId: Long): MonetizationRequestEntity?
}

