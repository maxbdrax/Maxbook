package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ItemCondition
import com.example.data.model.NotificationType
import com.example.data.model.PrivacyLevel
import com.example.data.model.ReactionType

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val username: String,
    val email: String,
    val avatarUrl: String,
    val coverPhotoUrl: String = "",
    val bio: String = "",
    val location: String = "",
    val workplace: String = "",
    val joinedDate: String = "Today",
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val friendsCount: Int = 0,
    val isVerified: Boolean = false,
    val isOnline: Boolean = true,
    val isCurrentUser: Boolean = false,
    val isFriend: Boolean = false,
    val isFollowing: Boolean = false,
    val isBlocked: Boolean = false,
    val role: String = "USER", // SUPER_ADMIN, ADMIN, CREATOR, USER
    val balance: Double = 0.0, // In BDT (৳)
    val totalEarnings: Double = 0.0, // Lifetime earnings in BDT (৳)
    val isMonetized: Boolean = false, // Eligible and activated when followers >= 5000
    val monetizationApplied: Boolean = false,
    val phone: String = "",
    val password: String = "123456",
    val isBanned: Boolean = false
)

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val authorId: String,
    val authorName: String,
    val authorAvatar: String,
    val authorUsername: String,
    val isVerified: Boolean = false,
    val content: String,
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val videoDuration: String? = null,
    val feelingActivity: String? = null,
    val location: String? = null,
    val privacyLevel: PrivacyLevel = PrivacyLevel.PUBLIC,
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val userReaction: ReactionType = ReactionType.NONE,
    val isSaved: Boolean = false,
    val isPinned: Boolean = false,
    val groupId: String? = null,
    val groupName: String? = null,
    val pageId: String? = null,
    val pageName: String? = null,
    val pollQuestion: String? = null,
    val pollOptionsJson: String? = null, // e.g. "Option1:::Option2:::Option3"
    val pollVotesJson: String? = null,   // e.g. "12:::45:::8"
    val selectedPollOptionIndex: Int = -1,
    val isBoosted: Boolean = false,
    val boostTargetReach: Int = 0,
    val boostBudget: Double = 0.0,
    val boostExpiresAt: Long = 0L,
    val viewsCount: Int = 0,
    val earningsGenerated: Double = 0.0
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val postId: Long,
    val authorId: String,
    val authorName: String,
    val authorAvatar: String,
    val isVerified: Boolean = false,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val parentCommentId: Long? = null
)

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val authorId: String,
    val authorName: String,
    val authorAvatar: String,
    val mediaUrl: String,
    val caption: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isViewed: Boolean = false,
    val isUserStory: Boolean = false
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val threadId: String,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String,
    val text: String,
    val mediaUrl: String? = null,
    val isAudioNote: Boolean = false,
    val audioDurationSec: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = true,
    val isMine: Boolean = true,
    val status: String = "SENT" // SENT, DELIVERED, SEEN
)

@Entity(tableName = "chat_threads")
data class ChatThreadEntity(
    @PrimaryKey val id: String,
    val participantId: String,
    val participantName: String,
    val participantAvatar: String,
    val isOnline: Boolean = true,
    val lastMessage: String = "",
    val lastMessageTimestamp: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0,
    val isGroupChat: Boolean = false
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: NotificationType,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String,
    val content: String,
    val targetPostId: Long? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey val id: String,
    val name: String,
    val coverUrl: String,
    val avatarUrl: String,
    val description: String,
    val privacy: String = "Public Group",
    val membersCount: Int,
    val isJoined: Boolean = false,
    val isModerator: Boolean = false,
    val category: String
)

@Entity(tableName = "pages")
data class PageEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val avatarUrl: String,
    val coverUrl: String,
    val bio: String,
    val likesCount: Int,
    val followersCount: Int,
    val isFollowing: Boolean = false,
    val website: String = ""
)

@Entity(tableName = "marketplace_items")
data class MarketplaceItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val price: Double,
    val category: String,
    val location: String,
    val description: String,
    val imageUrl: String,
    val sellerId: String,
    val sellerName: String,
    val sellerAvatar: String,
    val condition: ItemCondition = ItemCondition.LIKE_NEW,
    val isSold: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "friend_requests")
data class FriendRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val requesterId: String,
    val requesterName: String,
    val requesterAvatar: String,
    val mutualFriendsCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "PENDING" // PENDING, ACCEPTED, DECLINED
)

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val query: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetType: String, // POST, USER, COMMENT, MARKETPLACE
    val targetId: String,
    val reportedByUserId: String,
    val reason: String,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "PENDING" // PENDING, REVIEWED, ACTION_TAKEN
)

@Entity(tableName = "wallet_transactions")
data class WalletTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val userName: String,
    val userPhone: String = "",
    val type: String, // DEPOSIT, WITHDRAWAL, BOOST_PAYMENT, VERIFY_PAYMENT, EARNING, BONUS
    val amount: Double, // in BDT (৳)
    val paymentMethod: String, // BKASH, NAGAD, ROCKET, WALLET
    val accountNumber: String, // Mobile number used for payment/payout
    val transactionId: String, // TrxID from bKash/Nagad/Rocket
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)

@Entity(tableName = "boost_orders")
data class BoostOrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val postId: Long,
    val userId: String,
    val userName: String,
    val targetReach: Int,
    val durationDays: Int,
    val amountPaid: Double,
    val paymentMethod: String, // WALLET, BKASH, NAGAD, ROCKET
    val trxId: String = "",
    val status: String = "ACTIVE", // ACTIVE, EXPIRED, PENDING
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "verification_requests")
data class VerificationRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val userName: String,
    val userAvatar: String,
    val followersCount: Int,
    val category: String = "Public Figure",
    val documentUrl: String = "",
    val paymentMethod: String = "WALLET", // WALLET, BKASH, NAGAD, ROCKET
    val trxId: String = "",
    val amountPaid: Double = 500.0,
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "monetization_requests")
data class MonetizationRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val userName: String,
    val userAvatar: String,
    val followersCount: Int,
    val payoutMethod: String = "BKASH", // BKASH, NAGAD, ROCKET
    val payoutNumber: String = "",
    val status: String = "APPROVED", // PENDING, APPROVED, REJECTED
    val timestamp: Long = System.currentTimeMillis()
)

