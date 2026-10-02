package com.example.data.repository

import android.util.Log
import com.example.core.common.Resource
import com.example.core.demo.DemoDataManager
import com.example.core.network.RealtimeChangeEvent
import com.example.core.network.SupabaseClient
import com.example.core.network.SupabaseConfig
import com.example.core.network.SupabaseRealtimeManager
import com.example.data.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class AuthRepository(
    private val client: SupabaseClient,
    private val config: SupabaseConfig,
    private val demo: DemoDataManager
) {
    fun getCurrentUserId(): String? = config.getCurrentUserId()
    fun isLoggedIn(): Boolean = !config.getAccessToken().isNullOrBlank()

    suspend fun signUp(
        email: String,
        password: String,
        username: String,
        displayName: String
    ): Resource<UserProfile> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim()
        val cleanPass = password.trim()

        // Check if demo credentials
        if ((cleanEmail == "214608" || cleanEmail.startsWith("214608@") || cleanEmail.equals("214608@test.com", ignoreCase = true)) && cleanPass == "206305") {
            config.saveSession("demo-token-214608", "demo-refresh-214608", SupabaseConfig.DEMO_USER_ID)
            return@withContext Resource.Success(demo.currentUserProfile)
        }

        if (!config.isConfigured()) {
            config.saveSession("demo-token-local", "demo-refresh-local", SupabaseConfig.DEMO_USER_ID)
            val customDemoProfile = UserProfile(
                id = SupabaseConfig.DEMO_USER_ID,
                username = username.trim().removePrefix("@").lowercase(),
                displayName = displayName.trim(),
                isOnline = true
            )
            demo.currentUserProfile = customDemoProfile
            demo.registerOrUpdateUser(customDemoProfile)
            return@withContext Resource.Success(customDemoProfile)
        }

        try {
            val cleanUsername = username.trim().removePrefix("@").lowercase()
            val metadata = mapOf(
                "username" to cleanUsername,
                "display_name" to displayName.trim()
            )

            val authResponse = client.signUp(cleanEmail, cleanPass, metadata)
            val accessToken = authResponse.optString("access_token")
            val refreshToken = authResponse.optString("refresh_token")
            val userObj = authResponse.optJSONObject("user")
            val userId = userObj?.optString("id") ?: authResponse.optString("id")

            if (userId.isNotBlank()) {
                config.saveSession(accessToken, refreshToken, userId)

                val profilePayload = JSONObject().apply {
                    put("id", userId)
                    put("username", cleanUsername)
                    put("display_name", displayName.trim())
                    put("bio", "")
                    put("avatar_url", "")
                    put("is_online", true)
                }

                try {
                    client.postgrestPost("profiles", profilePayload)
                } catch (e: Exception) {
                    Log.w("AuthRepo", "Profile insert: ${e.message}")
                }

                val profile = UserProfile(
                    id = userId,
                    username = cleanUsername,
                    displayName = displayName.trim(),
                    isOnline = true
                )
                demo.registerOrUpdateUser(profile)
                Resource.Success(profile)
            } else {
                Resource.Error("Could not retrieve user ID from response")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Registration failed", e)
        }
    }

    suspend fun signIn(email: String, password: String): Resource<UserProfile> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim()
        val cleanPass = password.trim()

        // 1. Direct login for requested test credentials 214608 / 206305
        if ((cleanEmail == "214608" || cleanEmail.startsWith("214608@") || cleanEmail.equals("214608@test.com", ignoreCase = true)) && cleanPass == "206305") {
            config.saveSession("demo-token-214608", "demo-refresh-214608", SupabaseConfig.DEMO_USER_ID)
            return@withContext Resource.Success(demo.currentUserProfile)
        }

        // 2. If configured with live Supabase
        if (config.isConfigured()) {
            try {
                val authResponse = client.signInWithPassword(cleanEmail, cleanPass)
                val accessToken = authResponse.optString("access_token")
                val refreshToken = authResponse.optString("refresh_token")
                val userObj = authResponse.optJSONObject("user")
                val userId = userObj?.optString("id") ?: authResponse.optString("id")

                if (userId.isNotBlank()) {
                    config.saveSession(accessToken, refreshToken, userId)

                    val profileArray = client.postgrestGet("profiles?id=eq.$userId&select=*")
                    val profile = if (profileArray.length() > 0) {
                        UserProfile.fromJson(profileArray.getJSONObject(0))
                    } else {
                        UserProfile(id = userId, username = cleanEmail.substringBefore("@"), displayName = cleanEmail.substringBefore("@"))
                    }

                    val onlineUpdate = JSONObject().apply { put("is_online", true) }
                    runCatching { client.postgrestPatch("profiles?id=eq.$userId", onlineUpdate) }

                    return@withContext Resource.Success(profile)
                }
            } catch (e: Exception) {
                return@withContext Resource.Error(e.message ?: "Sign-in failed", e)
            }
        } else {
            // Local fallback for quick test
            config.saveSession("demo-token-local", "demo-refresh-local", SupabaseConfig.DEMO_USER_ID)
            return@withContext Resource.Success(demo.currentUserProfile)
        }

        Resource.Error("Invalid credentials")
    }

    suspend fun signOut(): Boolean = withContext(Dispatchers.IO) {
        val userId = config.getCurrentUserId()
        if (userId != null && config.isConfigured()) {
            val offlineUpdate = JSONObject().apply { put("is_online", false) }
            runCatching { client.postgrestPatch("profiles?id=eq.$userId", offlineUpdate) }
            client.signOut()
        }
        config.clearSession()
        true
    }
}

class ProfileRepository(
    private val client: SupabaseClient,
    private val config: SupabaseConfig,
    private val demo: DemoDataManager
) {
    suspend fun getProfile(userId: String): Resource<UserProfile> = withContext(Dispatchers.IO) {
        if (config.isDemoMode()) {
            if (userId == config.getCurrentUserId() || userId == SupabaseConfig.DEMO_USER_ID) {
                return@withContext Resource.Success(demo.currentUserProfile)
            }
            val found = demo.sampleUsers.find { it.id == userId }
            if (found != null) return@withContext Resource.Success(found)
        }

        try {
            val array = client.postgrestGet("profiles?id=eq.$userId&select=*")
            if (array.length() > 0) {
                Resource.Success(UserProfile.fromJson(array.getJSONObject(0)))
            } else {
                Resource.Error("Profile not found")
            }
        } catch (e: Exception) {
            if (config.isDemoMode()) {
                Resource.Success(demo.currentUserProfile)
            } else {
                Resource.Error(e.message ?: "Failed to get profile", e)
            }
        }
    }

    suspend fun searchUsers(query: String): Resource<List<UserProfile>> = withContext(Dispatchers.IO) {
        val clean = query.trim().removePrefix("@").lowercase()
        if (clean.isBlank()) return@withContext Resource.Success(emptyList())

        val currentUserId = config.getCurrentUserId()
        val allUsers = (demo.sampleUsers + listOf(demo.currentUserProfile)).distinctBy { it.id }

        if (config.isDemoMode()) {
            val matched = allUsers.filter { user ->
                user.id != currentUserId && (
                    user.username.lowercase().contains(clean) ||
                    user.displayName.lowercase().contains(clean) ||
                    clean.contains(user.username.lowercase())
                )
            }
            return@withContext Resource.Success(matched)
        }

        try {
            val array = client.postgrestGet(
                "profiles?or=(username.ilike.*$clean*,display_name.ilike.*$clean*)&id=neq.$currentUserId&limit=25"
            )
            val users = mutableListOf<UserProfile>()
            for (i in 0 until array.length()) {
                users.add(UserProfile.fromJson(array.getJSONObject(i)))
            }
            if (users.isEmpty()) {
                val matched = allUsers.filter { user ->
                    user.id != currentUserId && (
                        user.username.lowercase().contains(clean) ||
                        user.displayName.lowercase().contains(clean)
                    )
                }
                Resource.Success(matched)
            } else {
                Resource.Success(users)
            }
        } catch (e: Exception) {
            val matched = allUsers.filter { user ->
                user.id != currentUserId && (
                    user.username.lowercase().contains(clean) ||
                    user.displayName.lowercase().contains(clean)
                )
            }
            Resource.Success(matched)
        }
    }

    suspend fun updateProfile(
        username: String = "",
        displayName: String,
        bio: String,
        avatarUrl: String,
        lastSeenVisibility: String = "EVERYONE",
        photoVisibility: String = "EVERYONE",
        readReceipts: Boolean = true
    ): Resource<UserProfile> = withContext(Dispatchers.IO) {
        val cleanUsername = username.trim().removePrefix("@").lowercase().ifBlank { demo.currentUserProfile.username }
        if (config.isDemoMode()) {
            demo.currentUserProfile = demo.currentUserProfile.copy(
                username = cleanUsername,
                displayName = displayName.trim(),
                bio = bio.trim(),
                avatarUrl = avatarUrl.trim(),
                lastSeenVisibility = lastSeenVisibility,
                photoVisibility = photoVisibility,
                readReceiptsEnabled = readReceipts
            )
            demo.registerOrUpdateUser(demo.currentUserProfile)
            return@withContext Resource.Success(demo.currentUserProfile)
        }

        val currentUserId = config.getCurrentUserId() ?: return@withContext Resource.Error("User not logged in")
        try {
            val payload = JSONObject().apply {
                if (cleanUsername.isNotBlank()) put("username", cleanUsername)
                put("display_name", displayName.trim())
                put("bio", bio.trim())
                if (avatarUrl.isNotBlank()) put("avatar_url", avatarUrl)
                put("last_seen_visibility", lastSeenVisibility)
                put("photo_visibility", photoVisibility)
                put("read_receipts_enabled", readReceipts)
            }
            val resArray = client.postgrestPatch("profiles?id=eq.$currentUserId", payload)
            if (resArray.length() > 0) {
                val updated = UserProfile.fromJson(resArray.getJSONObject(0))
                demo.registerOrUpdateUser(updated)
                Resource.Success(updated)
            } else {
                getProfile(currentUserId)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update profile", e)
        }
    }

    suspend fun updatePresence(isOnline: Boolean) = withContext(Dispatchers.IO) {
        val currentUserId = config.getCurrentUserId() ?: return@withContext
        if (config.isConfigured()) {
            val payload = JSONObject().apply { put("is_online", isOnline) }
            runCatching { client.postgrestPatch("profiles?id=eq.$currentUserId", payload) }
        }
    }
}

class ChatRepository(
    private val client: SupabaseClient,
    private val config: SupabaseConfig,
    private val demo: DemoDataManager
) {
    suspend fun getConversations(): Resource<List<Conversation>> = withContext(Dispatchers.IO) {
        if (config.isDemoMode()) {
            val sorted = demo.conversations.sortedWith(
                compareByDescending<Conversation> { it.isPinned }
                    .thenByDescending { it.lastMessageAt }
            )
            return@withContext Resource.Success(sorted)
        }

        val currentUserId = config.getCurrentUserId() ?: return@withContext Resource.Error("Not logged in")
        try {
            val membersArray = client.postgrestGet("conversation_members?user_id=eq.$currentUserId&select=*")
            if (membersArray.length() == 0) {
                return@withContext Resource.Success(emptyList())
            }

            val convMemberMap = mutableMapOf<String, JSONObject>()
            val convIds = mutableListOf<String>()
            for (i in 0 until membersArray.length()) {
                val item = membersArray.getJSONObject(i)
                val cId = item.getString("conversation_id")
                convIds.add(cId)
                convMemberMap[cId] = item
            }

            val inFilter = convIds.joinToString(",")
            val convsArray = client.postgrestGet("conversations?id=in.($inFilter)&order=last_message_at.desc")

            val directConvOtherUserIds = mutableMapOf<String, String>()
            val directOtherMembersArray = client.postgrestGet("conversation_members?conversation_id=in.($inFilter)&user_id=neq.$currentUserId&select=*")
            for (i in 0 until directOtherMembersArray.length()) {
                val item = directOtherMembersArray.getJSONObject(i)
                directConvOtherUserIds[item.getString("conversation_id")] = item.getString("user_id")
            }

            val otherUserIds = directConvOtherUserIds.values.distinct()
            val profilesMap = mutableMapOf<String, UserProfile>()
            if (otherUserIds.isNotEmpty()) {
                val profileFilter = otherUserIds.joinToString(",")
                val profilesArray = client.postgrestGet("profiles?id=in.($profileFilter)&select=*")
                for (i in 0 until profilesArray.length()) {
                    val p = UserProfile.fromJson(profilesArray.getJSONObject(i))
                    profilesMap[p.id] = p
                }
            }

            val conversations = mutableListOf<Conversation>()
            for (i in 0 until convsArray.length()) {
                val convJson = convsArray.getJSONObject(i)
                val cId = convJson.getString("id")
                val memberJson = convMemberMap[cId]

                val otherUserId = directConvOtherUserIds[cId]
                val otherProfile = otherUserId?.let { profilesMap[it] }

                val type = if (convJson.optString("type") == "GROUP") ConversationType.GROUP else ConversationType.DIRECT
                val title = if (type == ConversationType.DIRECT) {
                    otherProfile?.displayName ?: otherProfile?.username ?: convJson.optString("title", "Chat")
                } else {
                    convJson.optString("title", "Group")
                }
                val avatar = if (type == ConversationType.DIRECT) {
                    otherProfile?.avatarUrl ?: ""
                } else {
                    convJson.optString("avatar_url")
                }

                conversations.add(
                    Conversation(
                        id = cId,
                        type = type,
                        title = title,
                        avatarUrl = avatar,
                        createdBy = convJson.optString("created_by").ifBlank { null },
                        lastMessageText = convJson.optString("last_message_text"),
                        lastMessageAt = convJson.optString("last_message_at"),
                        lastMessageSenderId = convJson.optString("last_message_sender_id").ifBlank { null },
                        isPinned = memberJson?.optBoolean("is_pinned", false) ?: false,
                        isMuted = memberJson?.optBoolean("is_muted", false) ?: false,
                        unreadCount = memberJson?.optInt("unread_count", 0) ?: 0,
                        directUser = otherProfile
                    )
                )
            }

            val sorted = conversations.sortedWith(
                compareByDescending<Conversation> { it.isPinned }
                    .thenByDescending { it.lastMessageAt }
            )

            Resource.Success(sorted)
        } catch (e: Exception) {
            val sorted = demo.conversations.sortedWith(
                compareByDescending<Conversation> { it.isPinned }
                    .thenByDescending { it.lastMessageAt }
            )
            Resource.Success(sorted)
        }
    }

    suspend fun getOrCreateDirectConversation(otherUserId: String): Resource<Conversation> = withContext(Dispatchers.IO) {
        if (config.isDemoMode()) {
            val conv = demo.getOrCreateDirectConversation(otherUserId)
            return@withContext Resource.Success(conv)
        }

        val currentUserId = config.getCurrentUserId() ?: return@withContext Resource.Error("Not logged in")
        try {
            val myMemberships = client.postgrestGet("conversation_members?user_id=eq.$currentUserId&select=conversation_id")
            val myConvIds = mutableListOf<String>()
            for (i in 0 until myMemberships.length()) {
                myConvIds.add(myMemberships.getJSONObject(i).getString("conversation_id"))
            }

            if (myConvIds.isNotEmpty()) {
                val filter = myConvIds.joinToString(",")
                val otherMemberships = client.postgrestGet("conversation_members?conversation_id=in.($filter)&user_id=eq.$otherUserId&select=conversation_id")
                if (otherMemberships.length() > 0) {
                    val existingConvId = otherMemberships.getJSONObject(0).getString("conversation_id")
                    val convArray = client.postgrestGet("conversations?id=eq.$existingConvId&select=*")
                    if (convArray.length() > 0) {
                        val cJson = convArray.getJSONObject(0)
                        if (cJson.optString("type") == "DIRECT") {
                            val otherProfileArray = client.postgrestGet("profiles?id=eq.$otherUserId&select=*")
                            val otherProfile = if (otherProfileArray.length() > 0) UserProfile.fromJson(otherProfileArray.getJSONObject(0)) else null
                            return@withContext Resource.Success(Conversation.fromJson(cJson, otherProfile))
                        }
                    }
                }
            }

            val convPayload = JSONObject().apply {
                put("type", "DIRECT")
                put("created_by", currentUserId)
                put("last_message_text", "")
            }
            val createdArray = client.postgrestPost("conversations", convPayload)
            val newConvJson = createdArray.getJSONObject(0)
            val newConvId = newConvJson.getString("id")

            val member1 = JSONObject().apply {
                put("conversation_id", newConvId)
                put("user_id", currentUserId)
                put("role", "MEMBER")
            }
            val member2 = JSONObject().apply {
                put("conversation_id", newConvId)
                put("user_id", otherUserId)
                put("role", "MEMBER")
            }
            client.postgrestPost("conversation_members", member1, false)
            client.postgrestPost("conversation_members", member2, false)

            val otherProfileArray = client.postgrestGet("profiles?id=eq.$otherUserId&select=*")
            val otherProfile = if (otherProfileArray.length() > 0) UserProfile.fromJson(otherProfileArray.getJSONObject(0)) else null

            Resource.Success(Conversation.fromJson(newConvJson, otherProfile))
        } catch (e: Exception) {
            val conv = demo.getOrCreateDirectConversation(otherUserId)
            Resource.Success(conv)
        }
    }

    suspend fun createGroupConversation(
        title: String,
        avatarUrl: String,
        memberUserIds: List<String>
    ): Resource<Conversation> = withContext(Dispatchers.IO) {
        if (config.isDemoMode()) {
            val conv = demo.createGroupConversation(title, memberUserIds)
            return@withContext Resource.Success(conv)
        }

        val currentUserId = config.getCurrentUserId() ?: return@withContext Resource.Error("Not logged in")
        try {
            val convPayload = JSONObject().apply {
                put("type", "GROUP")
                put("title", title.trim())
                put("avatar_url", avatarUrl.trim())
                put("created_by", currentUserId)
                put("last_message_text", "Created group $title")
            }
            val createdArray = client.postgrestPost("conversations", convPayload)
            val newConvJson = createdArray.getJSONObject(0)
            val newConvId = newConvJson.getString("id")

            val ownerMember = JSONObject().apply {
                put("conversation_id", newConvId)
                put("user_id", currentUserId)
                put("role", "OWNER")
            }
            client.postgrestPost("conversation_members", ownerMember, false)

            for (uid in memberUserIds) {
                if (uid != currentUserId) {
                    val m = JSONObject().apply {
                        put("conversation_id", newConvId)
                        put("user_id", uid)
                        put("role", "MEMBER")
                    }
                    client.postgrestPost("conversation_members", m, false)
                }
            }

            Resource.Success(Conversation.fromJson(newConvJson))
        } catch (e: Exception) {
            val conv = demo.createGroupConversation(title, memberUserIds)
            Resource.Success(conv)
        }
    }

    suspend fun togglePin(conversationId: String, currentPinStatus: Boolean): Resource<Boolean> = withContext(Dispatchers.IO) {
        if (config.isDemoMode()) {
            val idx = demo.conversations.indexOfFirst { it.id == conversationId }
            if (idx != -1) {
                demo.conversations[idx] = demo.conversations[idx].copy(isPinned = !currentPinStatus)
            }
            return@withContext Resource.Success(!currentPinStatus)
        }

        val currentUserId = config.getCurrentUserId() ?: return@withContext Resource.Error("Not logged in")
        try {
            val payload = JSONObject().apply { put("is_pinned", !currentPinStatus) }
            client.postgrestPatch("conversation_members?conversation_id=eq.$conversationId&user_id=eq.$currentUserId", payload)
            Resource.Success(!currentPinStatus)
        } catch (e: Exception) {
            val idx = demo.conversations.indexOfFirst { it.id == conversationId }
            if (idx != -1) {
                demo.conversations[idx] = demo.conversations[idx].copy(isPinned = !currentPinStatus)
            }
            Resource.Success(!currentPinStatus)
        }
    }

    suspend fun toggleMute(conversationId: String, currentMuteStatus: Boolean): Resource<Boolean> = withContext(Dispatchers.IO) {
        if (config.isDemoMode()) {
            val idx = demo.conversations.indexOfFirst { it.id == conversationId }
            if (idx != -1) {
                demo.conversations[idx] = demo.conversations[idx].copy(isMuted = !currentMuteStatus)
            }
            return@withContext Resource.Success(!currentMuteStatus)
        }

        val currentUserId = config.getCurrentUserId() ?: return@withContext Resource.Error("Not logged in")
        try {
            val payload = JSONObject().apply { put("is_muted", !currentMuteStatus) }
            client.postgrestPatch("conversation_members?conversation_id=eq.$conversationId&user_id=eq.$currentUserId", payload)
            Resource.Success(!currentMuteStatus)
        } catch (e: Exception) {
            val idx = demo.conversations.indexOfFirst { it.id == conversationId }
            if (idx != -1) {
                demo.conversations[idx] = demo.conversations[idx].copy(isMuted = !currentMuteStatus)
            }
            Resource.Success(!currentMuteStatus)
        }
    }

    suspend fun getGroupMembers(conversationId: String): Resource<List<ConversationMember>> = withContext(Dispatchers.IO) {
        if (config.isDemoMode()) {
            val list = demo.conversationMembers[conversationId] ?: emptyList()
            return@withContext Resource.Success(list)
        }

        try {
            val membersArray = client.postgrestGet("conversation_members?conversation_id=eq.$conversationId&select=*")
            val userIds = mutableListOf<String>()
            val membersRaw = mutableListOf<JSONObject>()
            for (i in 0 until membersArray.length()) {
                val item = membersArray.getJSONObject(i)
                membersRaw.add(item)
                userIds.add(item.getString("user_id"))
            }

            val profilesMap = mutableMapOf<String, UserProfile>()
            if (userIds.isNotEmpty()) {
                val filter = userIds.joinToString(",")
                val profArray = client.postgrestGet("profiles?id=in.($filter)&select=*")
                for (i in 0 until profArray.length()) {
                    val p = UserProfile.fromJson(profArray.getJSONObject(i))
                    profilesMap[p.id] = p
                }
            }

            val result = membersRaw.map { raw ->
                val uid = raw.getString("user_id")
                ConversationMember.fromJson(raw, profilesMap[uid])
            }
            Resource.Success(result)
        } catch (e: Exception) {
            val list = demo.conversationMembers[conversationId] ?: emptyList()
            Resource.Success(list)
        }
    }

    suspend fun addMember(conversationId: String, userId: String, role: MemberRole = MemberRole.MEMBER): Resource<Boolean> = withContext(Dispatchers.IO) {
        if (config.isDemoMode()) {
            val user = demo.sampleUsers.find { it.id == userId }
            val list = demo.conversationMembers.getOrPut(conversationId) { mutableListOf() }
            list.add(
                ConversationMember(
                    id = "mem-${UUID.randomUUID()}",
                    conversationId = conversationId,
                    userId = userId,
                    role = role,
                    profile = user
                )
            )
            return@withContext Resource.Success(true)
        }

        try {
            val payload = JSONObject().apply {
                put("conversation_id", conversationId)
                put("user_id", userId)
                put("role", role.name)
            }
            client.postgrestPost("conversation_members", payload, false)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add member", e)
        }
    }

    suspend fun removeMember(conversationId: String, userId: String): Resource<Boolean> = withContext(Dispatchers.IO) {
        if (config.isDemoMode()) {
            demo.conversationMembers[conversationId]?.removeAll { it.userId == userId }
            return@withContext Resource.Success(true)
        }

        try {
            client.postgrestDelete("conversation_members?conversation_id=eq.$conversationId&user_id=eq.$userId")
            Resource.Success(true)
        } catch (e: Exception) {
            demo.conversationMembers[conversationId]?.removeAll { it.userId == userId }
            Resource.Success(true)
        }
    }

    suspend fun updateMemberRole(conversationId: String, userId: String, newRole: MemberRole): Resource<Boolean> = withContext(Dispatchers.IO) {
        if (config.isDemoMode()) {
            val list = demo.conversationMembers[conversationId]
            val idx = list?.indexOfFirst { it.userId == userId } ?: -1
            if (idx != -1 && list != null) {
                list[idx] = list[idx].copy(role = newRole)
            }
            return@withContext Resource.Success(true)
        }

        try {
            val payload = JSONObject().apply { put("role", newRole.name) }
            client.postgrestPatch("conversation_members?conversation_id=eq.$conversationId&user_id=eq.$userId", payload)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update role", e)
        }
    }

    suspend fun toggleArchive(conversationId: String, currentArchived: Boolean): Resource<Boolean> = withContext(Dispatchers.IO) {
        val target = !currentArchived
        val idx = demo.conversations.indexOfFirst { it.id == conversationId }
        if (idx != -1) {
            demo.conversations[idx] = demo.conversations[idx].copy(isArchived = target)
        }
        Resource.Success(target)
    }

    suspend fun deleteConversation(conversationId: String): Resource<Boolean> = withContext(Dispatchers.IO) {
        demo.conversations.removeAll { it.id == conversationId }
        demo.conversationMessages.remove(conversationId)
        demo.conversationMembers.remove(conversationId)
        Resource.Success(true)
    }

    fun saveDraft(conversationId: String, text: String) {
        demo.saveDraft(conversationId, text)
    }

    fun getDraft(conversationId: String): String = demo.getDraft(conversationId)

    suspend fun getContacts(): Resource<List<ContactItem>> = withContext(Dispatchers.IO) {
        Resource.Success(demo.sampleContacts.toList())
    }

    suspend fun addContact(name: String, username: String, phone: String): Resource<ContactItem> = withContext(Dispatchers.IO) {
        val contact = demo.addContact(name, username, phone)
        Resource.Success(contact)
    }

    suspend fun updateGroupPermissions(conversationId: String, permissions: GroupPermissions): Resource<Boolean> = withContext(Dispatchers.IO) {
        val idx = demo.conversations.indexOfFirst { it.id == conversationId }
        if (idx != -1) {
            demo.conversations[idx] = demo.conversations[idx].copy(groupPermissions = permissions)
        }
        Resource.Success(true)
    }
}

class MessageRepository(
    private val client: SupabaseClient,
    private val config: SupabaseConfig,
    private val realtime: SupabaseRealtimeManager,
    private val demo: DemoDataManager
) {
    fun observeRealtimeMessages(): Flow<RealtimeChangeEvent> {
        return realtime.eventsFlow.filter { it.table == "messages" }
    }

    suspend fun getMessages(conversationId: String, limit: Int = 50): Resource<List<Message>> = withContext(Dispatchers.IO) {
        if (config.isDemoMode()) {
            val msgs = demo.conversationMessages[conversationId]?.toList() ?: emptyList()
            return@withContext Resource.Success(msgs)
        }

        try {
            val array = client.postgrestGet("messages?conversation_id=eq.$conversationId&order=created_at.asc&limit=$limit")
            val senderIds = mutableListOf<String>()
            val rawList = mutableListOf<JSONObject>()
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                rawList.add(item)
                senderIds.add(item.getString("sender_id"))
            }

            val profilesMap = mutableMapOf<String, UserProfile>()
            val distinctSenders = senderIds.distinct()
            if (distinctSenders.isNotEmpty()) {
                val filter = distinctSenders.joinToString(",")
                val profArray = client.postgrestGet("profiles?id=in.($filter)&select=*")
                for (i in 0 until profArray.length()) {
                    val p = UserProfile.fromJson(profArray.getJSONObject(i))
                    profilesMap[p.id] = p
                }
            }

            val messages = rawList.map { raw ->
                val sid = raw.getString("sender_id")
                Message.fromJson(raw, profilesMap[sid])
            }
            Resource.Success(messages)
        } catch (e: Exception) {
            val msgs = demo.conversationMessages[conversationId]?.toList() ?: emptyList()
            Resource.Success(msgs)
        }
    }

    suspend fun sendMessage(
        conversationId: String,
        content: String,
        messageType: MessageType = MessageType.TEXT,
        attachmentUrl: String? = null,
        attachmentName: String? = null,
        attachmentSize: Long? = null,
        replyToMessageId: String? = null
    ): Resource<Message> = withContext(Dispatchers.IO) {
        if (config.isDemoMode()) {
            val msg = demo.addMessage(
                conversationId = conversationId,
                content = content,
                type = messageType,
                attachmentUrl = attachmentUrl,
                attachmentName = attachmentName,
                attachmentSize = attachmentSize,
                replyToId = replyToMessageId
            )

            // Simulate automatic friendly reply if communicating with Sara
            if (conversationId == "conv-demo-sara" && messageType == MessageType.TEXT) {
                CoroutineScope(Dispatchers.IO).launch {
                    delay(2000)
                    val sara = demo.sampleUsers[0]
                    val reply = Message(
                        id = "msg-${UUID.randomUUID()}",
                        conversationId = conversationId,
                        senderId = sara.id,
                        content = "تم استلام رسالتك: \"$content\" بنجاح! 👍",
                        messageType = MessageType.TEXT,
                        status = MessageStatus.READ,
                        createdAt = "2026-10-02T13:36:00Z",
                        senderProfile = sara
                    )
                    demo.conversationMessages[conversationId]?.add(reply)
                    val idx = demo.conversations.indexOfFirst { it.id == conversationId }
                    if (idx != -1) {
                        demo.conversations[idx] = demo.conversations[idx].copy(
                            lastMessageText = reply.content,
                            lastMessageAt = reply.createdAt
                        )
                    }
                }
            }

            return@withContext Resource.Success(msg)
        }

        val currentUserId = config.getCurrentUserId() ?: return@withContext Resource.Error("Not logged in")
        try {
            val payload = JSONObject().apply {
                put("conversation_id", conversationId)
                put("sender_id", currentUserId)
                put("content", content.trim())
                put("message_type", messageType.name)
                attachmentUrl?.let { put("attachment_url", it) }
                attachmentName?.let { put("attachment_name", it) }
                attachmentSize?.let { put("attachment_size", it) }
                replyToMessageId?.let { put("reply_to_message_id", it) }
                put("status", "SENT")
            }

            val created = client.postgrestPost("messages", payload)
            val msgJson = created.getJSONObject(0)

            val snippet = when (messageType) {
                MessageType.IMAGE -> "📷 Image"
                MessageType.FILE -> "📎 File: $attachmentName"
                MessageType.AUDIO -> "🎤 Voice note"
                MessageType.SYSTEM -> content
                MessageType.TEXT -> content.trim()
            }
            val convUpdate = JSONObject().apply {
                put("last_message_text", snippet)
                put("last_message_sender_id", currentUserId)
            }
            runCatching { client.postgrestPatch("conversations?id=eq.$conversationId", convUpdate) }

            Resource.Success(Message.fromJson(msgJson))
        } catch (e: Exception) {
            val msg = demo.addMessage(
                conversationId = conversationId,
                content = content,
                type = messageType,
                attachmentUrl = attachmentUrl,
                attachmentName = attachmentName,
                attachmentSize = attachmentSize,
                replyToId = replyToMessageId
            )
            Resource.Success(msg)
        }
    }

    suspend fun editMessage(messageId: String, newContent: String): Resource<Boolean> = withContext(Dispatchers.IO) {
        if (config.isDemoMode()) {
            demo.conversationMessages.values.forEach { list ->
                val idx = list.indexOfFirst { it.id == messageId }
                if (idx != -1) {
                    list[idx] = list[idx].copy(content = newContent, isEdited = true)
                }
            }
            return@withContext Resource.Success(true)
        }

        val currentUserId = config.getCurrentUserId() ?: return@withContext Resource.Error("Not logged in")
        try {
            val payload = JSONObject().apply {
                put("content", newContent.trim())
                put("is_edited", true)
            }
            client.postgrestPatch("messages?id=eq.$messageId&sender_id=eq.$currentUserId", payload)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to edit message", e)
        }
    }

    suspend fun deleteMessage(messageId: String, deleteForEveryone: Boolean): Resource<Boolean> = withContext(Dispatchers.IO) {
        if (config.isDemoMode()) {
            demo.conversationMessages.values.forEach { list ->
                val idx = list.indexOfFirst { it.id == messageId }
                if (idx != -1) {
                    if (deleteForEveryone) {
                        list[idx] = list[idx].copy(deletedAt = "deleted", isDeletedForEveryone = true, content = "")
                    } else {
                        list.removeAt(idx)
                    }
                }
            }
            return@withContext Resource.Success(true)
        }

        val currentUserId = config.getCurrentUserId() ?: return@withContext Resource.Error("Not logged in")
        try {
            val payload = JSONObject().apply {
                put("deleted_at", "now()")
            }
            client.postgrestPatch("messages?id=eq.$messageId", payload)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete message", e)
        }
    }

    suspend fun markAsRead(conversationId: String) = withContext(Dispatchers.IO) {
        if (config.isDemoMode()) {
            val list = demo.conversationMessages[conversationId]
            list?.forEachIndexed { index, message ->
                if (message.senderId != config.getCurrentUserId()) {
                    list[index] = message.copy(status = MessageStatus.READ)
                }
            }
            val idx = demo.conversations.indexOfFirst { it.id == conversationId }
            if (idx != -1) {
                demo.conversations[idx] = demo.conversations[idx].copy(unreadCount = 0)
            }
            return@withContext
        }

        val currentUserId = config.getCurrentUserId() ?: return@withContext
        try {
            val payload = JSONObject().apply {
                put("status", "READ")
            }
            client.postgrestPatch("messages?conversation_id=eq.$conversationId&sender_id=neq.$currentUserId&status=neq.READ", payload)
            val memberUpdate = JSONObject().apply { put("unread_count", 0) }
            client.postgrestPatch("conversation_members?conversation_id=eq.$conversationId&user_id=eq.$currentUserId", memberUpdate)
        } catch (_: Exception) {}
    }

    suspend fun toggleReaction(messageId: String, emoji: String, userId: String, userName: String): Resource<Boolean> = withContext(Dispatchers.IO) {
        demo.toggleReaction(messageId, emoji, userId, userName)
        Resource.Success(true)
    }

    suspend fun togglePinMessage(conversationId: String, messageId: String): Resource<Boolean> = withContext(Dispatchers.IO) {
        val newState = demo.toggleMessagePin(conversationId, messageId)
        Resource.Success(newState)
    }

    suspend fun forwardMessage(targetConversationIds: List<String>, message: Message): Resource<Boolean> = withContext(Dispatchers.IO) {
        demo.forwardMessages(targetConversationIds, message)
        Resource.Success(true)
    }

    fun getTypingStatus() = demo.typingStatus

    fun triggerAutoReply(conversationId: String) {
        demo.triggerSimulatedReply(conversationId)
    }
}

class StorageRepository(
    private val client: SupabaseClient,
    private val config: SupabaseConfig
) {
    suspend fun uploadMedia(
        bucket: String,
        fileName: String,
        fileBytes: ByteArray,
        mimeType: String,
        folder: String? = null
    ): Resource<String> = withContext(Dispatchers.IO) {
        try {
            val extension = fileName.substringAfterLast(".", "")
            val uniqueName = "${UUID.randomUUID()}.$extension"
            val path = listOfNotNull(folder?.trim('/')?.takeIf { it.isNotBlank() }, uniqueName).joinToString("/")
            val url = if (bucket == "avatars") {
                client.uploadFile(bucket, path, fileBytes, mimeType)
            } else {
                client.uploadFile(bucket, path, fileBytes, mimeType)
                client.createSignedUrl(bucket, path)
            }
            Resource.Success(url)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to upload file", e)
        }
    }
}

class BlockRepository(
    private val client: SupabaseClient,
    private val config: SupabaseConfig,
    private val demo: DemoDataManager
) {
    suspend fun blockUser(userId: String): Resource<Boolean> = withContext(Dispatchers.IO) {
        if (config.isDemoMode()) {
            demo.blockedUserIds.add(userId)
            return@withContext Resource.Success(true)
        }

        val currentUserId = config.getCurrentUserId() ?: return@withContext Resource.Error("Not logged in")
        try {
            val payload = JSONObject().apply {
                put("blocker_id", currentUserId)
                put("blocked_id", userId)
            }
            client.postgrestPost("blocks", payload, false)
            Resource.Success(true)
        } catch (e: Exception) {
            demo.blockedUserIds.add(userId)
            Resource.Success(true)
        }
    }

    suspend fun unblockUser(userId: String): Resource<Boolean> = withContext(Dispatchers.IO) {
        if (config.isDemoMode()) {
            demo.blockedUserIds.remove(userId)
            return@withContext Resource.Success(true)
        }

        val currentUserId = config.getCurrentUserId() ?: return@withContext Resource.Error("Not logged in")
        try {
            client.postgrestDelete("blocks?blocker_id=eq.$currentUserId&blocked_id=eq.$userId")
            Resource.Success(true)
        } catch (e: Exception) {
            demo.blockedUserIds.remove(userId)
            Resource.Success(true)
        }
    }

    suspend fun getBlockedUserIds(): Resource<List<String>> = withContext(Dispatchers.IO) {
        if (config.isDemoMode()) {
            return@withContext Resource.Success(demo.blockedUserIds.toList())
        }

        val currentUserId = config.getCurrentUserId() ?: return@withContext Resource.Error("Not logged in")
        try {
            val array = client.postgrestGet("blocks?blocker_id=eq.$currentUserId&select=blocked_id")
            val ids = mutableListOf<String>()
            for (i in 0 until array.length()) {
                ids.add(array.getJSONObject(i).getString("blocked_id"))
            }
            Resource.Success(ids)
        } catch (e: Exception) {
            Resource.Success(demo.blockedUserIds.toList())
        }
    }
}
