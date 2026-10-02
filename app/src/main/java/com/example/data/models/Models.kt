package com.example.data.models

import org.json.JSONArray
import org.json.JSONObject

data class UserProfile(
    val id: String,
    val username: String,
    val displayName: String,
    val bio: String = "",
    val avatarUrl: String = "",
    val isOnline: Boolean = false,
    val lastSeen: String = "",
    val lastSeenVisibility: String = "EVERYONE",
    val photoVisibility: String = "EVERYONE",
    val readReceiptsEnabled: Boolean = true,
    val createdAt: String = ""
) {
    companion object {
        fun fromJson(json: JSONObject): UserProfile {
            return UserProfile(
                id = json.optString("id"),
                username = json.optString("username"),
                displayName = json.optString("display_name", json.optString("username")),
                bio = json.optString("bio"),
                avatarUrl = json.optString("avatar_url"),
                isOnline = json.optBoolean("is_online", false),
                lastSeen = json.optString("last_seen"),
                lastSeenVisibility = json.optString("last_seen_visibility", "EVERYONE"),
                photoVisibility = json.optString("photo_visibility", "EVERYONE"),
                readReceiptsEnabled = json.optBoolean("read_receipts_enabled", true),
                createdAt = json.optString("created_at")
            )
        }
    }
}

enum class ConversationType {
    DIRECT,
    GROUP
}

data class GroupPermissions(
    val canSendMessages: Boolean = true,
    val canSendMedia: Boolean = true,
    val canAddMembers: Boolean = true,
    val canEditInfo: Boolean = false,
    val canPinMessages: Boolean = false
)

data class Conversation(
    val id: String,
    val type: ConversationType,
    val title: String,
    val avatarUrl: String = "",
    val createdBy: String? = null,
    val lastMessageText: String = "",
    val lastMessageAt: String = "",
    val lastMessageSenderId: String? = null,
    val lastMessageType: MessageType = MessageType.TEXT,
    val isPinned: Boolean = false,
    val isMuted: Boolean = false,
    val isArchived: Boolean = false,
    val unreadCount: Int = 0,
    val draft: String = "",
    val pinnedMessageId: String? = null,
    val groupPermissions: GroupPermissions = GroupPermissions(),
    val directUser: UserProfile? = null
) {
    companion object {
        fun fromJson(json: JSONObject, directUser: UserProfile? = null): Conversation {
            val typeStr = json.optString("type", "DIRECT")
            val type = if (typeStr.equals("GROUP", ignoreCase = true)) ConversationType.GROUP else ConversationType.DIRECT
            return Conversation(
                id = json.optString("id"),
                type = type,
                title = json.optString("title", directUser?.displayName ?: "Chat"),
                avatarUrl = json.optString("avatar_url", directUser?.avatarUrl ?: ""),
                createdBy = json.optString("created_by").ifBlank { null },
                lastMessageText = json.optString("last_message_text"),
                lastMessageAt = json.optString("last_message_at"),
                lastMessageSenderId = json.optString("last_message_sender_id").ifBlank { null },
                isPinned = json.optBoolean("is_pinned", false),
                isMuted = json.optBoolean("is_muted", false),
                isArchived = json.optBoolean("is_archived", false),
                unreadCount = json.optInt("unread_count", 0),
                draft = json.optString("draft", ""),
                pinnedMessageId = json.optString("pinned_message_id").ifBlank { null },
                directUser = directUser
            )
        }
    }
}

enum class MemberRole {
    OWNER,
    ADMIN,
    MEMBER
}

data class ConversationMember(
    val id: String,
    val conversationId: String,
    val userId: String,
    val role: MemberRole,
    val joinedAt: String = "",
    val isPinned: Boolean = false,
    val isMuted: Boolean = false,
    val unreadCount: Int = 0,
    val profile: UserProfile? = null
) {
    companion object {
        fun fromJson(json: JSONObject, profile: UserProfile? = null): ConversationMember {
            val roleStr = json.optString("role", "MEMBER").uppercase()
            val role = when (roleStr) {
                "OWNER" -> MemberRole.OWNER
                "ADMIN" -> MemberRole.ADMIN
                else -> MemberRole.MEMBER
            }
            return ConversationMember(
                id = json.optString("id"),
                conversationId = json.optString("conversation_id"),
                userId = json.optString("user_id"),
                role = role,
                joinedAt = json.optString("joined_at"),
                isPinned = json.optBoolean("is_pinned", false),
                isMuted = json.optBoolean("is_muted", false),
                unreadCount = json.optInt("unread_count", 0),
                profile = profile
            )
        }
    }
}

enum class MessageType {
    TEXT,
    IMAGE,
    FILE,
    AUDIO,
    SYSTEM
}

enum class MessageStatus {
    SENT,
    DELIVERED,
    READ
}

data class MessageReaction(
    val emoji: String,
    val userId: String,
    val userName: String = ""
)

data class Message(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val content: String,
    val messageType: MessageType = MessageType.TEXT,
    val attachmentUrl: String? = null,
    val attachmentName: String? = null,
    val attachmentSize: Long? = null,
    val replyToMessageId: String? = null,
    val replyToMessage: Message? = null,
    val reactions: List<MessageReaction> = emptyList(),
    val isEdited: Boolean = false,
    val isPinned: Boolean = false,
    val isForwarded: Boolean = false,
    val deletedForMe: Boolean = false,
    val deletedAt: String? = null,
    val isDeletedForEveryone: Boolean = !deletedAt.isNullOrBlank(),
    val status: MessageStatus = MessageStatus.SENT,
    val createdAt: String = "",
    val senderProfile: UserProfile? = null
) {
    companion object {
        fun fromJson(json: JSONObject, senderProfile: UserProfile? = null, replyTo: Message? = null): Message {
            val typeStr = json.optString("message_type", "TEXT").uppercase()
            val messageType = try {
                MessageType.valueOf(typeStr)
            } catch (_: Exception) {
                MessageType.TEXT
            }

            val statusStr = json.optString("status", "SENT").uppercase()
            val status = try {
                MessageStatus.valueOf(statusStr)
            } catch (_: Exception) {
                MessageStatus.SENT
            }

            val deletedAt = json.optString("deleted_at").ifBlank { null }

            return Message(
                id = json.optString("id"),
                conversationId = json.optString("conversation_id"),
                senderId = json.optString("sender_id"),
                content = if (deletedAt != null) "" else json.optString("content"),
                messageType = messageType,
                attachmentUrl = json.optString("attachment_url").ifBlank { null },
                attachmentName = json.optString("attachment_name").ifBlank { null },
                attachmentSize = if (json.has("attachment_size") && !json.isNull("attachment_size")) json.optLong("attachment_size") else null,
                replyToMessageId = json.optString("reply_to_message_id").ifBlank { null },
                replyToMessage = replyTo,
                isEdited = json.optBoolean("is_edited", false),
                isPinned = json.optBoolean("is_pinned", false),
                isForwarded = json.optBoolean("is_forwarded", false),
                deletedAt = deletedAt,
                isDeletedForEveryone = deletedAt != null,
                status = status,
                createdAt = json.optString("created_at"),
                senderProfile = senderProfile
            )
        }
    }
}

data class ContactItem(
    val id: String,
    val userId: String,
    val displayName: String,
    val username: String,
    val phoneNumber: String = "",
    val avatarUrl: String = "",
    val isOnline: Boolean = false,
    val lastSeen: String = "",
    val bio: String = ""
)

data class BlockRecord(
    val id: String,
    val blockerId: String,
    val blockedId: String,
    val createdAt: String
)
