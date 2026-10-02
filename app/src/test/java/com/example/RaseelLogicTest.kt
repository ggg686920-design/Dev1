package com.example

import com.example.core.common.DateUtils
import com.example.core.customization.*
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
            put("content", "مرحباً بكم في تطبيق dev")
            put("message_type", "TEXT")
            put("status", "READ")
            put("is_edited", true)
            put("created_at", "2026-10-02T12:00:00Z")
        }

        val message = Message.fromJson(json)
        assertEquals("msg-123", message.id)
        assertEquals("conv-456", message.conversationId)
        assertEquals("user-789", message.senderId)
        assertEquals("مرحباً بكم في تطبيق dev", message.content)
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
        assertEquals("", message.content)
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

        assertEquals("2", sorted[0].id)
        assertEquals("1", sorted[1].id)
    }

    @Test
    fun testThemesCount_atLeast30Themes() {
        val themes = ThemePresets.builtInThemes
        assertTrue("Theme library must contain at least 30 themes", themes.size >= 30)
        assertTrue(themes.any { it.id == "dev_default" })
        assertTrue(themes.any { it.category == "AMOLED" })
        assertTrue(themes.any { it.category == "Cyberpunk" })
        assertTrue(themes.any { it.category == "Gaming" })
        assertTrue(themes.any { it.category == "Glass" })
        assertTrue(themes.any { it.category == "Luxury" })
    }

    @Test
    fun testWallpapersCount_atLeast30Wallpapers() {
        val wallpapers = ThemePresets.builtInWallpapers
        assertTrue("Wallpaper library must contain at least 30 wallpapers", wallpapers.size >= 30)
        assertTrue(wallpapers.any { it.isVideoLoop })
    }

    @Test
    fun testIconStylesCount_atLeast10Styles() {
        val iconStyles = IconStyle.entries
        assertTrue("Icon styles must be at least 10", iconStyles.size >= 10)
    }

    @Test
    fun testThemeSerialization_roundTrip() {
        val original = ThemePresets.builtInThemes.first()
        val json = original.toJson()
        val restored = ThemeConfig.fromJson(json)

        assertEquals(original.id, restored.id)
        assertEquals(original.name, restored.name)
        assertEquals(original.primaryColorHex, restored.primaryColorHex)
        assertEquals(original.bubbleConfig.radiusDp, restored.bubbleConfig.radiusDp)
        assertEquals(original.navBarStyle, restored.navBarStyle)
        assertEquals(original.iconStyle, restored.iconStyle)
    }

    @Test
    fun testExportImportDevTheme() {
        val context = org.robolectric.RuntimeEnvironment.getApplication()
        val manager = CustomizationManager(context)
        val original = ThemePresets.builtInThemes[2]

        val exportedJson = manager.exportThemeToJson(original)
        assertTrue(exportedJson.contains("devtheme"))

        val importResult = manager.importThemeFromJson(exportedJson)
        assertTrue(importResult.isSuccess)
        val imported = importResult.getOrNull()
        assertNotNull(imported)
        assertEquals(original.name, imported?.name)
    }
}
