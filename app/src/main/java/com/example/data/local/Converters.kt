package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.ItemCondition
import com.example.data.model.NotificationType
import com.example.data.model.PrivacyLevel
import com.example.data.model.ReactionType

class Converters {
    @TypeConverter
    fun fromReactionType(value: ReactionType?): String {
        return value?.name ?: ReactionType.NONE.name
    }

    @TypeConverter
    fun toReactionType(value: String?): ReactionType {
        return value?.let {
            try { ReactionType.valueOf(it) } catch (e: Exception) { ReactionType.NONE }
        } ?: ReactionType.NONE
    }

    @TypeConverter
    fun fromPrivacyLevel(value: PrivacyLevel?): String {
        return value?.name ?: PrivacyLevel.PUBLIC.name
    }

    @TypeConverter
    fun toPrivacyLevel(value: String?): PrivacyLevel {
        return value?.let {
            try { PrivacyLevel.valueOf(it) } catch (e: Exception) { PrivacyLevel.PUBLIC }
        } ?: PrivacyLevel.PUBLIC
    }

    @TypeConverter
    fun fromNotificationType(value: NotificationType?): String {
        return value?.name ?: NotificationType.LIKE.name
    }

    @TypeConverter
    fun toNotificationType(value: String?): NotificationType {
        return value?.let {
            try { NotificationType.valueOf(it) } catch (e: Exception) { NotificationType.LIKE }
        } ?: NotificationType.LIKE
    }

    @TypeConverter
    fun fromItemCondition(value: ItemCondition?): String {
        return value?.name ?: ItemCondition.LIKE_NEW.name
    }

    @TypeConverter
    fun toItemCondition(value: String?): ItemCondition {
        return value?.let {
            try { ItemCondition.valueOf(it) } catch (e: Exception) { ItemCondition.LIKE_NEW }
        } ?: ItemCondition.LIKE_NEW
    }
}
