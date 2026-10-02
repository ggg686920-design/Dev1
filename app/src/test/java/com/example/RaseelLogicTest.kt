package com.example

import com.example.core.common.DateUtils
import com.example.data.models.*
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RaseelLogicTest {

    @Test
    fun testDateUtils_parseAndFormat() {
        val iso = "2026-10-02T10:30:00Z"
        val parsed = DateUtils.parseIso(iso)
        assertNotNull(parsed)

        val relativeTimeAr = DateUtils.formatRelativeTime(iso, isArabic = true)
        assertNotNull(relativeTimeAr)
    }

    @Test
    fun testMessage_jsonParsing() {
        val json = JSONObject().apply {
            put("id", "msg-123")
            put("conversation_id", "conv-456")
            put("sender_id", "user-789")
            put("content", "مرحباً بكم في تطبيق رسيل")
            put("message_type", "TEXT")
            put("status", "READ")
            put("is_edited", true)
            put("created_at", "2026-10-02T12:00:00Z")
        }

        val message = Message.fromJson(json)
        assertEquals("msg-123", message.id)
        assertEquals("conv-456", message.conversationId)
        assertEquals("user-789", message.senderId)
        assertEquals("مرحباً بكم في تطبيق رسيل", message.content)
        assertEquals(MessageType.TEXT, message.messageType)
        assertEquals(MessageStatus.READ, message.status)
        assertTrue(message.isEdited)
        assertFalse(message.isDeletedForEveryone)
    }

    @Test
    fun testMessage_softDeletedHandling() {
        val json = JSONObject().apply {
            put("id", "msg-deleted")
            put("conversation_id", "conv-1")
            put("sender_id", "user-1")
            put("content", "secret text")
            put("deleted_at", "2026-10-02T12:05:00Z")
        }

        val message = Message.fromJson(json)
        assertTrue(message.isDeletedForEveryone)
        assertEquals("", message.content) // Content masked when deleted
    }

    @Test
    fun testUserProfile_jsonParsing() {
        val json = JSONObject().apply {
            put("id", "usr-1")
            put("username", "ahmed_k")
            put("display_name", "أحمد خالد")
            put("bio", "مطور برمجيات")
            put("avatar_url", "https://example.com/avatar.jpg")
            put("is_online", true)
        }

        val profile = UserProfile.fromJson(json)
        assertEquals("usr-1", profile.id)
        assertEquals("ahmed_k", profile.username)
        assertEquals("أحمد خالد", profile.displayName)
        assertEquals("مطور برمجيات", profile.bio)
        assertTrue(profile.isOnline)
    }

    @Test
    fun testConversationSorting_pinnedFirst() {
        val c1 = Conversation(
            id = "1",
            type = ConversationType.DIRECT,
            title = "Chat 1",
            lastMessageAt = "2026-10-02T10:00:00Z",
            isPinned = false
        )
        val c2 = Conversation(
            id = "2",
            type = ConversationType.DIRECT,
            title = "Chat 2",
            lastMessageAt = "2026-10-01T10:00:00Z",
            isPinned = true
        )

        val list = listOf(c1, c2)
        val sorted = list.sortedWith(
            compareByDescending<Conversation> { it.isPinned }
                .thenByDescending { it.lastMessageAt }
        )

        assertEquals("2", sorted[0].id) // Pinned conversation comes first
        assertEquals("1", sorted[1].id)
    }

    @Test
    fun testDemoLoginCredentials() {
        val demo = com.example.core.demo.DemoDataManager()
        assertEquals("user-214608-test", demo.currentUserId)
        assertEquals("user_214608", demo.currentUserProfile.username)
        assertTrue(demo.conversations.isNotEmpty())
        assertTrue(demo.sampleUsers.isNotEmpty())
    }
}
