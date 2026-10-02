package com.example.core.di

import android.content.Context
import com.example.core.media.AudioPlayerManager
import com.example.core.media.AudioRecorderManager
import com.example.core.network.SupabaseClient
import com.example.core.network.SupabaseConfig
import com.example.core.network.SupabaseRealtimeManager
import com.example.core.notifications.NotificationHelper
import com.example.data.repository.*

class AppContainer(val context: Context) {
    val config: SupabaseConfig by lazy { SupabaseConfig(context) }
    val client: SupabaseClient by lazy { SupabaseClient(config) }
    val realtime: SupabaseRealtimeManager by lazy { SupabaseRealtimeManager(config, client.okHttpClient) }
    val notificationHelper: NotificationHelper by lazy { NotificationHelper(context) }
    val audioRecorder: AudioRecorderManager by lazy { AudioRecorderManager(context) }
    val audioPlayer: AudioPlayerManager by lazy { AudioPlayerManager() }
    val demoDataManager: com.example.core.demo.DemoDataManager by lazy { com.example.core.demo.DemoDataManager() }
    val settingsManager: com.example.core.settings.AppSettingsManager by lazy { com.example.core.settings.AppSettingsManager(context) }

    val authRepository: AuthRepository by lazy { AuthRepository(client, config, demoDataManager) }
    val profileRepository: ProfileRepository by lazy { ProfileRepository(client, config, demoDataManager) }
    val chatRepository: ChatRepository by lazy { ChatRepository(client, config, demoDataManager) }
    val messageRepository: MessageRepository by lazy { MessageRepository(client, config, realtime, demoDataManager) }
    val storageRepository: StorageRepository by lazy { StorageRepository(client) }
    val blockRepository: BlockRepository by lazy { BlockRepository(client, config, demoDataManager) }
}
