package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        PostEntity::class,
        CommentEntity::class,
        StoryEntity::class,
        MessageEntity::class,
        ChatThreadEntity::class,
        NotificationEntity::class,
        GroupEntity::class,
        PageEntity::class,
        MarketplaceItemEntity::class,
        FriendRequestEntity::class,
        SearchHistoryEntity::class,
        ReportEntity::class,
        WalletTransactionEntity::class,
        BoostOrderEntity::class,
        VerificationRequestEntity::class,
        MonetizationRequestEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun maxBookDao(): MaxBookDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "maxbook_clean_v2_db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateCleanInitialData(database.maxBookDao())
                    }
                }
            }
        }

        suspend fun populateCleanInitialData(dao: MaxBookDao) {
            val now = System.currentTimeMillis()

            // 1. Single Master Super Admin User (No demo spam accounts)
            val adminUser = UserEntity(
                id = "admin_master_01",
                name = "Max Admin",
                username = "maxadmin",
                email = "developermaxbd@gmail.com",
                avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
                coverPhotoUrl = "https://images.unsplash.com/photo-1707343843437-caacff5cfa74?w=1200&auto=format&fit=crop&q=80",
                bio = "MaxBook Official Admin 👑 Managing users, monetization, verified badges, and wallet transactions.",
                location = "Dhaka, Bangladesh",
                workplace = "Founder & Super Admin at MaxBook",
                joinedDate = "August 2026",
                followersCount = 5200, // Eligible for 5K Monetization rule
                followingCount = 12,
                friendsCount = 350,
                isVerified = true,
                isOnline = true,
                isCurrentUser = true,
                role = "SUPER_ADMIN",
                balance = 2500.0, // ৳2,500 initial admin test balance
                totalEarnings = 1500.0,
                isMonetized = true,
                monetizationApplied = true,
                phone = "01700000000",
                password = "admin"
            )
            dao.insertUser(adminUser)

            // 2. Sample Official Community Groups
            dao.insertGroups(
                listOf(
                    GroupEntity(
                        id = "group_tech",
                        name = "Bangladesh Developers & Creators",
                        coverUrl = "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=1200",
                        avatarUrl = "https://images.unsplash.com/photo-1531482615713-2afd69097998?w=400",
                        description = "Community for content creators, developers & entrepreneurs.",
                        privacy = "Public Group",
                        membersCount = 1420,
                        isJoined = true,
                        isModerator = true,
                        category = "Technology"
                    ),
                    GroupEntity(
                        id = "group_marketplace",
                        name = "Buy & Sell Bangladesh",
                        coverUrl = "https://images.unsplash.com/photo-1472851294608-062f824d29cc?w=1200",
                        avatarUrl = "https://images.unsplash.com/photo-1556742049-0a67c5574f73?w=400",
                        description = "Official buy, sell, exchange and classifieds group.",
                        privacy = "Public Group",
                        membersCount = 3800,
                        isJoined = true,
                        category = "Marketplace"
                    )
                )
            )

            // 3. Sample Official Page
            dao.insertPage(
                PageEntity(
                    id = "page_maxbook",
                    name = "MaxBook Official",
                    category = "Social Network & Tech",
                    avatarUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=400",
                    coverUrl = "https://images.unsplash.com/photo-1579546929518-9e396f3cc809?w=1200",
                    bio = "Official MaxBook Page. Monetize your content with 5k followers, deposit & withdraw via bKash/Nagad/Rocket!",
                    likesCount = 12500,
                    followersCount = 15800,
                    isFollowing = true,
                    website = "https://maxbook.com"
                )
            )

            // 4. Initial Welcome Post from Admin
            dao.insertPost(
                PostEntity(
                    id = 1L,
                    authorId = adminUser.id,
                    authorName = adminUser.name,
                    authorAvatar = adminUser.avatarUrl,
                    authorUsername = adminUser.username,
                    isVerified = true,
                    content = "🎉 স্বাগতম MaxBook-এ!\n\n✨ নতুন ফিচারসমূহ:\n১. ৫,০০০ ফলোয়ার হলে ক্রিয়েটর মনিটাইজেশন ও ইনকাম হাব 💰\n২. বিকাশ, নগদ ও রকেটের মাধ্যমে ইনস্ট্যান্ট ডিপোজিট ও উইথড্র 💳\n৩. ব্লু ভেরিফিকেশন ব্যাজ (Blue Verified Badge) 🌟\n৪. পোস্ট বুস্টিং সিস্টেম (১০ গুণ বেশি মানুষের কাছে পৌঁছান) 🚀\n৫. সম্পূর্ণ অ্যাডমিন প্যানেল দিয়ে ইউজার ও ট্রানজেকশন ম্যানেজমেন্ট 🛡️",
                    imageUrl = "https://images.unsplash.com/photo-1551836022-d5d88e9218df?w=1200",
                    privacyLevel = PrivacyLevel.PUBLIC,
                    timestamp = now - 60000,
                    likesCount = 45,
                    commentsCount = 8,
                    sharesCount = 14,
                    isPinned = true,
                    isBoosted = true,
                    boostTargetReach = 15000,
                    boostBudget = 500.0,
                    viewsCount = 1240
                )
            )

            // 5. Initial Wallet Transaction Sample for Admin
            dao.insertTransaction(
                WalletTransactionEntity(
                    id = 1L,
                    userId = adminUser.id,
                    userName = adminUser.name,
                    userPhone = "01712345678",
                    type = "DEPOSIT",
                    amount = 2500.0,
                    paymentMethod = "BKASH",
                    accountNumber = "01712345678",
                    transactionId = "BKH9281728X",
                    status = "APPROVED",
                    timestamp = now - 3600000,
                    note = "Initial system test deposit via bKash"
                )
            )
        }
    }
}
