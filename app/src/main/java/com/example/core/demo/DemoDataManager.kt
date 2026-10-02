package com.example.core.demo

import com.example.data.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class DemoDataManager {

    val currentUserId = "user-214608-test"

    var currentUserProfile = UserProfile(
        id = currentUserId,
        username = "user_214608",
        displayName = "مستخدم التجربة (214608)",
        bio = "أهلاً! أستخدم تطبيق رسيل للتجربة والاختبار ✨",
        avatarUrl = "",
        isOnline = true,
        lastSeen = "2026-10-02T13:30:00Z"
    )

    val sampleUsers = mutableListOf(
        UserProfile(
            id = "usr-sara-1",
            username = "sara_ahmed",
            displayName = "سارة أحمد",
            bio = "مصممة واجهات ومتحمسة لتطبيقات المراسلة والتصميم الإبداعي ✨",
            isOnline = true,
            lastSeen = "2026-10-02T13:45:00Z"
        ),
        UserProfile(
            id = "usr-khaled-2",
            username = "khaled_ali",
            displayName = "خالد العلي",
            bio = "مطور أندرويد ومهتم بالبرمجيات الحرة والمصادر المفتوحة 🚀",
            isOnline = true,
            lastSeen = "2026-10-02T13:15:00Z"
        ),
        UserProfile(
            id = "usr-mariam-3",
            username = "mariam_n",
            displayName = "مريم النجار",
            bio = "كاتبة ومترجمة ومحبة للأدب واللغات 📚",
            isOnline = false,
            lastSeen = "2026-10-02T11:45:00Z"
        ),
        UserProfile(
            id = "usr-omar-4",
            username = "omar_f",
            displayName = "عمر فاروق",
            bio = "إدارة مشاريع تقنية وحلول سحابية 💻",
            isOnline = true,
            lastSeen = "2026-10-02T13:40:00Z"
        )
    )

    val sampleContacts = mutableListOf(
        ContactItem(
            id = "cnt-1",
            userId = "usr-sara-1",
            displayName = "سارة أحمد",
            username = "sara_ahmed",
            phoneNumber = "+966 50 123 4567",
            isOnline = true,
            bio = "مصممة واجهات ومتحمسة لتطبيقات المراسلة ✨"
        ),
        ContactItem(
            id = "cnt-2",
            userId = "usr-khaled-2",
            displayName = "خالد العلي",
            username = "khaled_ali",
            phoneNumber = "+966 55 987 6543",
            isOnline = true,
            bio = "مطور أندرويد ومهتم بالبرمجيات الحرة 🚀"
        ),
        ContactItem(
            id = "cnt-3",
            userId = "usr-mariam-3",
            displayName = "مريم النجار",
            username = "mariam_n",
            phoneNumber = "+966 54 333 2211",
            isOnline = false,
            lastSeen = "2026-10-02T11:45:00Z",
            bio = "كاتبة ومترجمة ومحبة للأدب 📚"
        ),
        ContactItem(
            id = "cnt-4",
            userId = "usr-omar-4",
            displayName = "عمر فاروق",
            username = "omar_f",
            phoneNumber = "+966 56 444 8899",
            isOnline = true,
            bio = "إدارة مشاريع تقنية 💻"
        )
    )

    val conversations = mutableListOf<Conversation>()
    val conversationMessages = mutableMapOf<String, MutableList<Message>>()
    val conversationMembers = mutableMapOf<String, MutableList<ConversationMember>>()
    val blockedUserIds = mutableSetOf<String>()
    val drafts = mutableMapOf<String, String>()

    private val _messagesFlow = MutableSharedFlow<Message>(extraBufferCapacity = 64)
    val messagesFlow: SharedFlow<Message> = _messagesFlow.asSharedFlow()

    // Typing simulation state per conversation
    private val _typingStatus = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val typingStatus: StateFlow<Map<String, Boolean>> = _typingStatus.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        setupInitialData()
    }

    private fun setupInitialData() {
        val sara = sampleUsers[0]
        val khaled = sampleUsers[1]
        val mariam = sampleUsers[2]
        val omar = sampleUsers[3]

        // 1. Direct Chat with Sara
        val convSaraId = "conv-demo-sara"
        val convSara = Conversation(
            id = convSaraId,
            type = ConversationType.DIRECT,
            title = sara.displayName,
            avatarUrl = sara.avatarUrl,
            lastMessageText = "أهلاً بك في رسيل! يسعدني تجربتك للتطبيق 👋",
            lastMessageAt = "2026-10-02T13:28:00Z",
            isPinned = true,
            isMuted = false,
            unreadCount = 1,
            pinnedMessageId = "msg-sara-1",
            directUser = sara
        )
        conversations.add(convSara)

        val saraMessages = mutableListOf(
            Message(
                id = "msg-sara-1",
                conversationId = convSaraId,
                senderId = sara.id,
                content = "السلام عليكم ورحمة الله وبركاته، مرحباً بك في الإصدار v1.000 من تطبيق رسيل!",
                messageType = MessageType.TEXT,
                status = MessageStatus.READ,
                createdAt = "2026-10-02T13:20:00Z",
                senderProfile = sara,
                isPinned = true,
                reactions = listOf(
                    MessageReaction("❤️", currentUserId, "أنت"),
                    MessageReaction("👍", sara.id, "سارة")
                )
            ),
            Message(
                id = "msg-sara-2",
                conversationId = convSaraId,
                senderId = currentUserId,
                content = "وعليكم السلام ورحمة الله، التصميم أنيق جداً وسريع الاستجابة!",
                messageType = MessageType.TEXT,
                status = MessageStatus.READ,
                createdAt = "2026-10-02T13:22:00Z",
                senderProfile = currentUserProfile,
                reactions = listOf(
                    MessageReaction("🔥", sara.id, "سارة")
                )
            ),
            Message(
                id = "msg-sara-3",
                conversationId = convSaraId,
                senderId = sara.id,
                content = "أهلاً بك في رسيل! يسعدني تجربتك للتطبيق 👋",
                messageType = MessageType.TEXT,
                status = MessageStatus.DELIVERED,
                createdAt = "2026-10-02T13:28:00Z",
                senderProfile = sara
            )
        )
        conversationMessages[convSaraId] = saraMessages

        // 2. Direct Chat with Khaled
        val convKhaledId = "conv-demo-khaled"
        val convKhaled = Conversation(
            id = convKhaledId,
            type = ConversationType.DIRECT,
            title = khaled.displayName,
            avatarUrl = khaled.avatarUrl,
            lastMessageText = "الميزة الصوتية وإرسال الصور تعملان بشكل ممتاز",
            lastMessageAt = "2026-10-02T12:40:00Z",
            isPinned = false,
            isMuted = false,
            unreadCount = 0,
            directUser = khaled
        )
        conversations.add(convKhaled)

        val khaledMessages = mutableListOf(
            Message(
                id = "msg-khaled-1",
                conversationId = convKhaledId,
                senderId = khaled.id,
                content = "مرحباً يا صديقي! هل قمت بتجربة تسجيل المقاطع الصوتية؟",
                messageType = MessageType.TEXT,
                status = MessageStatus.READ,
                createdAt = "2026-10-02T12:35:00Z",
                senderProfile = khaled
            ),
            Message(
                id = "msg-khaled-2",
                conversationId = convKhaledId,
                senderId = currentUserId,
                content = "نعم، المشغل التفاعلي مريح ومميز جداً مع الـ Waveform",
                messageType = MessageType.TEXT,
                status = MessageStatus.READ,
                createdAt = "2026-10-02T12:38:00Z",
                senderProfile = currentUserProfile
            ),
            Message(
                id = "msg-khaled-3",
                conversationId = convKhaledId,
                senderId = khaled.id,
                content = "تسجيل صوتي تجريبي",
                messageType = MessageType.AUDIO,
                attachmentUrl = "https://actions.google.com/sounds/v1/water/rain_heavy.ogg",
                attachmentName = "voice_note_12.m4a",
                attachmentSize = 145000,
                status = MessageStatus.READ,
                createdAt = "2026-10-02T12:39:00Z",
                senderProfile = khaled
            ),
            Message(
                id = "msg-khaled-4",
                conversationId = convKhaledId,
                senderId = khaled.id,
                content = "الميزة الصوتية وإرسال الصور تعملان بشكل ممتاز",
                messageType = MessageType.TEXT,
                status = MessageStatus.READ,
                createdAt = "2026-10-02T12:40:00Z",
                senderProfile = khaled
            )
        )
        conversationMessages[convKhaledId] = khaledMessages

        // 3. Direct Chat with Mariam
        val convMariamId = "conv-demo-mariam"
        val convMariam = Conversation(
            id = convMariamId,
            type = ConversationType.DIRECT,
            title = mariam.displayName,
            avatarUrl = mariam.avatarUrl,
            lastMessageText = "أرسلت لك ملف التوثيق الفني للتطبيق",
            lastMessageAt = "2026-10-02T10:15:00Z",
            isPinned = false,
            isMuted = true,
            unreadCount = 0,
            directUser = mariam
        )
        conversations.add(convMariam)

        val mariamMessages = mutableListOf(
            Message(
                id = "msg-mariam-1",
                conversationId = convMariamId,
                senderId = mariam.id,
                content = "Raseel_Documentation_v1.pdf",
                messageType = MessageType.FILE,
                attachmentName = "Raseel_Architecture_v1.000.pdf",
                attachmentSize = 2_450_000,
                attachmentUrl = "https://example.com/docs/raseel.pdf",
                status = MessageStatus.READ,
                createdAt = "2026-10-02T10:10:00Z",
                senderProfile = mariam
            ),
            Message(
                id = "msg-mariam-2",
                conversationId = convMariamId,
                senderId = mariam.id,
                content = "أرسلت لك ملف التوثيق الفني للتطبيق",
                messageType = MessageType.TEXT,
                status = MessageStatus.READ,
                createdAt = "2026-10-02T10:15:00Z",
                senderProfile = mariam
            )
        )
        conversationMessages[convMariamId] = mariamMessages

        // 4. Group Chat "فريق تطوير رسيل"
        val convGroupId = "conv-group-raseel-team"
        val groupConv = Conversation(
            id = convGroupId,
            type = ConversationType.GROUP,
            title = "فريق تطوير رسيل 🚀",
            avatarUrl = "",
            createdBy = currentUserId,
            lastMessageText = "عمر: تم اعتماد خطة الـ Offline/Local State بنجاح",
            lastMessageAt = "2026-10-02T13:30:00Z",
            isPinned = true,
            isMuted = false,
            unreadCount = 2,
            pinnedMessageId = "msg-group-1",
            groupPermissions = GroupPermissions(
                canSendMessages = true,
                canSendMedia = true,
                canAddMembers = true,
                canEditInfo = false,
                canPinMessages = true
            )
        )
        conversations.add(groupConv)

        val groupMembers = mutableListOf(
            ConversationMember("gm-1", convGroupId, currentUserId, MemberRole.OWNER, "2026-10-01T00:00:00Z", profile = currentUserProfile),
            ConversationMember("gm-2", convGroupId, sara.id, MemberRole.ADMIN, "2026-10-01T01:00:00Z", profile = sara),
            ConversationMember("gm-3", convGroupId, khaled.id, MemberRole.MEMBER, "2026-10-01T02:00:00Z", profile = khaled),
            ConversationMember("gm-4", convGroupId, omar.id, MemberRole.MEMBER, "2026-10-01T03:00:00Z", profile = omar)
        )
        conversationMembers[convGroupId] = groupMembers

        val groupMessages = mutableListOf(
            Message(
                id = "msg-group-1",
                conversationId = convGroupId,
                senderId = currentUserId,
                content = "أهلاً بالجميع في مجموعة العمل الرسمية لتطبيق رسيل v1.000",
                messageType = MessageType.TEXT,
                status = MessageStatus.READ,
                createdAt = "2026-10-02T11:00:00Z",
                senderProfile = currentUserProfile,
                isPinned = true,
                reactions = listOf(
                    MessageReaction("👍", sara.id, "سارة"),
                    MessageReaction("🔥", khaled.id, "خالد")
                )
            ),
            Message(
                id = "msg-group-2",
                conversationId = convGroupId,
                senderId = sara.id,
                content = "تم تجهيز جميع واجهات المحادثات والـ Theme المتقدم!",
                messageType = MessageType.TEXT,
                status = MessageStatus.READ,
                createdAt = "2026-10-02T12:00:00Z",
                senderProfile = sara,
                reactions = listOf(
                    MessageReaction("❤️", currentUserId, "أنت")
                )
            ),
            Message(
                id = "msg-group-3",
                conversationId = convGroupId,
                senderId = omar.id,
                content = "عمر: تم اعتماد خطة الـ Offline/Local State بنجاح",
                messageType = MessageType.TEXT,
                status = MessageStatus.READ,
                createdAt = "2026-10-02T13:30:00Z",
                senderProfile = omar
            )
        )
        conversationMessages[convGroupId] = groupMessages
    }

    fun saveDraft(conversationId: String, draftText: String) {
        if (draftText.isBlank()) {
            drafts.remove(conversationId)
        } else {
            drafts[conversationId] = draftText
        }
        val idx = conversations.indexOfFirst { it.id == conversationId }
        if (idx != -1) {
            conversations[idx] = conversations[idx].copy(draft = draftText)
        }
    }

    fun getDraft(conversationId: String): String = drafts[conversationId].orEmpty()

    fun toggleReaction(messageId: String, emoji: String, userId: String, userName: String) {
        conversationMessages.values.forEach { list ->
            val idx = list.indexOfFirst { it.id == messageId }
            if (idx != -1) {
                val msg = list[idx]
                val currentReactions = msg.reactions.toMutableList()
                val existing = currentReactions.find { it.emoji == emoji && it.userId == userId }
                if (existing != null) {
                    currentReactions.remove(existing)
                } else {
                    currentReactions.add(MessageReaction(emoji, userId, userName))
                }
                list[idx] = msg.copy(reactions = currentReactions)
            }
        }
    }

    fun toggleMessagePin(conversationId: String, messageId: String): Boolean {
        val list = conversationMessages[conversationId] ?: return false
        val idx = list.indexOfFirst { it.id == messageId }
        if (idx != -1) {
            val msg = list[idx]
            val newPinState = !msg.isPinned
            list[idx] = msg.copy(isPinned = newPinState)

            val convIdx = conversations.indexOfFirst { it.id == conversationId }
            if (convIdx != -1) {
                conversations[convIdx] = conversations[convIdx].copy(
                    pinnedMessageId = if (newPinState) messageId else null
                )
            }
            return newPinState
        }
        return false
    }

    fun editMessage(messageId: String, newContent: String): Boolean {
        conversationMessages.values.forEach { list ->
            val idx = list.indexOfFirst { it.id == messageId }
            if (idx != -1) {
                val msg = list[idx]
                list[idx] = msg.copy(content = newContent, isEdited = true)
                return true
            }
        }
        return false
    }

    fun deleteMessage(messageId: String, deleteForEveryone: Boolean): Boolean {
        conversationMessages.values.forEach { list ->
            val idx = list.indexOfFirst { it.id == messageId }
            if (idx != -1) {
                if (deleteForEveryone) {
                    val msg = list[idx]
                    list[idx] = msg.copy(
                        content = "",
                        deletedAt = "2026-10-02T13:40:00Z",
                        isDeletedForEveryone = true
                    )
                } else {
                    val msg = list[idx]
                    list[idx] = msg.copy(deletedForMe = true)
                }
                return true
            }
        }
        return false
    }

    fun forwardMessages(targetConversationIds: List<String>, message: Message) {
        targetConversationIds.forEach { targetId ->
            val forwardedMsg = message.copy(
                id = "fwd-" + UUID.randomUUID().toString(),
                conversationId = targetId,
                senderId = currentUserId,
                senderProfile = currentUserProfile,
                createdAt = "2026-10-02T13:42:00Z",
                status = MessageStatus.SENT,
                isForwarded = true,
                reactions = emptyList()
            )
            val list = conversationMessages.getOrPut(targetId) { mutableListOf() }
            list.add(forwardedMsg)

            val cIdx = conversations.indexOfFirst { it.id == targetId }
            if (cIdx != -1) {
                conversations[cIdx] = conversations[cIdx].copy(
                    lastMessageText = if (forwardedMsg.messageType == MessageType.TEXT) forwardedMsg.content else "رسالة محولة",
                    lastMessageAt = forwardedMsg.createdAt
                )
            }
        }
    }

    fun addContact(name: String, username: String, phone: String): ContactItem {
        val cleanUser = username.removePrefix("@").lowercase()
        val newContact = ContactItem(
            id = "cnt-" + UUID.randomUUID().toString(),
            userId = "usr-" + cleanUser,
            displayName = name,
            username = cleanUser,
            phoneNumber = phone,
            isOnline = true,
            bio = "مستخدم في رسيل"
        )
        sampleContacts.add(0, newContact)
        return newContact
    }

    fun createOrGetDirectConversation(user: UserProfile): Conversation {
        val existing = conversations.find { it.type == ConversationType.DIRECT && it.directUser?.id == user.id }
        if (existing != null) return existing

        val newConv = Conversation(
            id = "conv-direct-" + user.id,
            type = ConversationType.DIRECT,
            title = user.displayName,
            avatarUrl = user.avatarUrl,
            lastMessageText = "",
            lastMessageAt = "2026-10-02T13:45:00Z",
            directUser = user
        )
        conversations.add(0, newConv)
        conversationMessages[newConv.id] = mutableListOf()
        return newConv
    }

    fun triggerSimulatedReply(conversationId: String) {
        val conv = conversations.find { it.id == conversationId } ?: return
        if (conv.type != ConversationType.DIRECT) return
        val other = conv.directUser ?: return

        scope.launch {
            delay(1200)
            // Show typing indicator
            val currentMap = _typingStatus.value.toMutableMap()
            currentMap[conversationId] = true
            _typingStatus.value = currentMap

            delay(2200)
            // Hide typing
            val updatedMap = _typingStatus.value.toMutableMap()
            updatedMap[conversationId] = false
            _typingStatus.value = updatedMap

            // Send simulated reply
            val replies = listOf(
                "وصلت رسالتك! ميزة التراسل السريع ممتازة ومريحة جداً 👍",
                "أهلاً بك! سرعة الاستجابة والتصميم العربي ممتاز جداً ✨",
                "تمام، تم الاطلاع. هل جربت تغيير ألوان الواجهة من الإعدادات؟ 🎨",
                "رائع جداً! استمر في التجربة 🚀"
            )
            val replyText = replies.random()
            val replyMsg = Message(
                id = "auto-reply-" + UUID.randomUUID().toString(),
                conversationId = conversationId,
                senderId = other.id,
                content = replyText,
                messageType = MessageType.TEXT,
                status = MessageStatus.SENT,
                createdAt = "2026-10-02T13:46:00Z",
                senderProfile = other
            )

            val list = conversationMessages.getOrPut(conversationId) { mutableListOf() }
            list.add(replyMsg)

            val cIdx = conversations.indexOfFirst { it.id == conversationId }
            if (cIdx != -1) {
                conversations[cIdx] = conversations[cIdx].copy(
                    lastMessageText = replyText,
                    lastMessageAt = replyMsg.createdAt,
                    unreadCount = conversations[cIdx].unreadCount + 1
                )
            }
            _messagesFlow.emit(replyMsg)
        }
    }

    fun getOrCreateDirectConversation(otherUserId: String): Conversation {
        val user = sampleUsers.find { it.id == otherUserId } ?: UserProfile(
            id = otherUserId,
            username = "user_$otherUserId",
            displayName = "مستخدم ($otherUserId)",
            isOnline = true
        )
        return createOrGetDirectConversation(user)
    }

    fun createGroupConversation(title: String, memberUserIds: List<String>): Conversation {
        val convId = "conv-grp-" + UUID.randomUUID().toString()
        val conv = Conversation(
            id = convId,
            type = ConversationType.GROUP,
            title = title,
            createdBy = currentUserId,
            lastMessageText = "تم إنشاء المجموعة بنجاح",
            lastMessageAt = "2026-10-02T13:48:00Z"
        )
        conversations.add(0, conv)
        conversationMessages[convId] = mutableListOf()
        val members = mutableListOf(ConversationMember("gm-owner", convId, currentUserId, MemberRole.OWNER, profile = currentUserProfile))
        memberUserIds.forEach { uid ->
            val u = sampleUsers.find { it.id == uid }
            members.add(ConversationMember("gm-$uid", convId, uid, MemberRole.MEMBER, profile = u))
        }
        conversationMembers[convId] = members
        return conv
    }

    fun addMessage(
        conversationId: String,
        content: String,
        type: MessageType = MessageType.TEXT,
        attachmentUrl: String? = null,
        attachmentName: String? = null,
        attachmentSize: Long? = null,
        replyToId: String? = null
    ): Message {
        val replyMsg = if (replyToId != null) conversationMessages[conversationId]?.find { it.id == replyToId } else null
        val msg = Message(
            id = "msg-" + UUID.randomUUID().toString(),
            conversationId = conversationId,
            senderId = currentUserId,
            content = content,
            messageType = type,
            attachmentUrl = attachmentUrl,
            attachmentName = attachmentName,
            attachmentSize = attachmentSize,
            replyToMessageId = replyToId,
            replyToMessage = replyMsg,
            status = MessageStatus.SENT,
            createdAt = "2026-10-02T13:48:00Z",
            senderProfile = currentUserProfile
        )
        val list = conversationMessages.getOrPut(conversationId) { mutableListOf() }
        list.add(msg)
        val idx = conversations.indexOfFirst { it.id == conversationId }
        if (idx != -1) {
            conversations[idx] = conversations[idx].copy(
                lastMessageText = if (type == MessageType.TEXT) content else "مرفق وسائط",
                lastMessageAt = msg.createdAt
            )
        }
        return msg
    }
}
