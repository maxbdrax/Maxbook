package com.example.data.model

enum class ReactionType(val emoji: String, val label: String, val colorHex: Long) {
    NONE("", "Like", 0xFF65676B),
    LIKE("👍", "Like", 0xFF1877F2),
    LOVE("❤️", "Love", 0xFFF02849),
    HAHA("😆", "Haha", 0xFFF7B125),
    WOW("😮", "Wow", 0xFFF7B125),
    SAD("😢", "Sad", 0xFFF7B125),
    ANGRY("😡", "Angry", 0xFFE44520)
}

enum class PrivacyLevel(val label: String, val iconName: String) {
    PUBLIC("Public", "public"),
    FRIENDS("Friends", "group"),
    ONLY_ME("Only Me", "lock")
}

enum class ItemCondition(val label: String) {
    NEW("Brand New"),
    LIKE_NEW("Used - Like New"),
    GOOD("Used - Good"),
    FAIR("Used - Fair")
}

enum class NotificationType(val label: String) {
    LIKE("liked your post"),
    COMMENT("commented on your post"),
    FRIEND_REQUEST("sent you a friend request"),
    FRIEND_ACCEPT("accepted your friend request"),
    SHARE("shared your post"),
    GROUP_INVITE("invited you to a group"),
    SYSTEM("sent a security notice")
}

enum class ReportReason(val title: String, val description: String) {
    SPAM("Spam", "Repeated unwanted commercial content or scams"),
    HARASSMENT("Harassment or Bullying", "Disrespectful, threatening, or abusive behavior"),
    HATE_SPEECH("Hate Speech", "Direct attacks on protected characteristics"),
    MISINFORMATION("False Information", "Demonstrably false news or deceptive health/financial claims"),
    VIOLENCE("Violence or Dangerous Content", "Threats, violence, or dangerous organizations"),
    OTHER("Other Issue", "Something else violates MaxBook Community Standards")
}
