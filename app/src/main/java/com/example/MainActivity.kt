package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.core.settings.AppSettingsManager
import com.example.presentation.auth.AuthViewModel
import com.example.presentation.auth.BackendConfigScreen
import com.example.presentation.auth.LoginScreen
import com.example.presentation.auth.RegisterScreen
import com.example.presentation.chat.ChatInfoScreen
import com.example.presentation.chat.ChatScreen
import com.example.presentation.chat.ChatViewModel
import com.example.presentation.contacts.ContactsScreen
import com.example.presentation.contacts.ContactsViewModel
import com.example.presentation.groups.CreateGroupScreen
import com.example.presentation.groups.CreateGroupViewModel
import com.example.presentation.groups.GroupInfoScreen
import com.example.presentation.groups.GroupInfoViewModel
import com.example.presentation.home.HomeScreen
import com.example.presentation.home.HomeViewModel
import com.example.presentation.profile.EditProfileScreen
import com.example.presentation.profile.ProfileViewModel
import com.example.presentation.search.SearchViewModel
import com.example.presentation.settings.*
import com.example.presentation.customization.*
import com.example.ui.theme.RaseelTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as RaseelApplication
        val container = app.container
        val initialConversationId = intent?.getStringExtra("conversation_id")

        setContent {
            val appSettings by container.settingsManager.settings.collectAsState()
            val themeConfig by container.customizationManager.currentTheme.collectAsState()
            val layoutDirection = if (appSettings.isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                RaseelTheme(appSettings = appSettings, themeConfig = themeConfig) {
                    val navController = rememberNavController()

                    val isLoggedIn = container.authRepository.isLoggedIn()
                    val startDestination = if (isLoggedIn) "home" else "login"

                    // ViewModels initialized with dependency container
                    val authViewModel = remember { AuthViewModel(container) }
                    val homeViewModel = remember { HomeViewModel(container) }
                    val searchViewModel = remember { SearchViewModel(container) }
                    val profileViewModel = remember { ProfileViewModel(container) }
                    val contactsViewModel = remember { ContactsViewModel(container) }

                    LaunchedEffect(initialConversationId) {
                        if (isLoggedIn && !initialConversationId.isNullOrBlank()) {
                            navController.navigate("chat/$initialConversationId")
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable("login") {
                            LoginScreen(
                                viewModel = authViewModel,
                                onNavigateToRegister = { navController.navigate("register") },
                                onNavigateToConfig = { navController.navigate("backend_config") },
                                onLoginSuccess = {
                                    homeViewModel.loadData()
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("register") {
                            RegisterScreen(
                                viewModel = authViewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onRegisterSuccess = {
                                    homeViewModel.loadData()
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("backend_config") {
                            BackendConfigScreen(
                                container = container,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("home") {
                            HomeScreen(
                                homeViewModel = homeViewModel,
                                searchViewModel = searchViewModel,
                                profileViewModel = profileViewModel,
                                contactsViewModel = contactsViewModel,
                                isDarkTheme = appSettings.themeMode == com.example.core.settings.ThemeMode.DARK,
                                onToggleDarkTheme = { isDark ->
                                    container.settingsManager.updateThemeMode(
                                        if (isDark) com.example.core.settings.ThemeMode.DARK else com.example.core.settings.ThemeMode.LIGHT
                                    )
                                },
                                isArabic = appSettings.isArabic,
                                onToggleLanguage = { container.settingsManager.updateLanguage(it) },
                                onOpenConversation = { conversationId ->
                                    navController.navigate("chat/$conversationId")
                                },
                                onNavigateToCreateGroup = {
                                    navController.navigate("create_group")
                                },
                                onNavigateToEditProfile = {
                                    navController.navigate("edit_profile")
                                },
                                onNavigateToCustomizationStudio = {
                                    navController.navigate("customization_studio")
                                },
                                onNavigateToPrivacy = {
                                    navController.navigate("privacy_settings")
                                },
                                onNavigateToAppearance = {
                                    navController.navigate("appearance_settings")
                                },
                                onNavigateToStorage = {
                                    navController.navigate("storage_settings")
                                },
                                onNavigateToNotifications = {
                                    navController.navigate("notification_settings")
                                },
                                onNavigateToAbout = {
                                    navController.navigate("about_app")
                                },
                                onNavigateToBackendConfig = {
                                    navController.navigate("backend_config")
                                },
                                onSignedOut = {
                                    authViewModel.resetState()
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(
                            route = "chat/{conversationId}",
                            arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val cId = backStackEntry.arguments?.getString("conversationId").orEmpty()
                            val chatViewModel = remember(cId) {
                                ChatViewModel(cId, container)
                            }
                            ChatScreen(
                                viewModel = chatViewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToGroupInfo = { groupId ->
                                    navController.navigate("group_info/$groupId")
                                },
                                onNavigateToChatInfo = { convId ->
                                    navController.navigate("chat_info/$convId")
                                }
                            )
                        }

                        composable(
                            route = "chat_info/{conversationId}",
                            arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val cId = backStackEntry.arguments?.getString("conversationId").orEmpty()
                            ChatInfoScreen(
                                conversationId = cId,
                                container = container,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("create_group") {
                            val createGroupViewModel = remember { CreateGroupViewModel(container) }
                            CreateGroupScreen(
                                viewModel = createGroupViewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onGroupCreated = { newConvId ->
                                    navController.navigate("chat/$newConvId") {
                                        popUpTo("create_group") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(
                            route = "group_info/{conversationId}",
                            arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val cId = backStackEntry.arguments?.getString("conversationId").orEmpty()
                            val groupInfoViewModel = remember(cId) {
                                GroupInfoViewModel(cId, container)
                            }
                            GroupInfoScreen(
                                viewModel = groupInfoViewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onLeftGroup = {
                                    homeViewModel.loadData()
                                    navController.popBackStack("home", inclusive = false)
                                }
                            )
                        }

                        composable("edit_profile") {
                            EditProfileScreen(
                                viewModel = profileViewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("privacy_settings") {
                            PrivacySettingsScreen(
                                viewModel = profileViewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToBlockedUsers = {
                                    navController.navigate("blocked_users")
                                }
                            )
                        }

                        composable("blocked_users") {
                            BlockedUsersScreen(
                                viewModel = profileViewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("appearance_settings") {
                            AppearanceScreen(
                                settingsManager = container.settingsManager,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("storage_settings") {
                            StorageSettingsScreen(
                                settingsManager = container.settingsManager,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("notification_settings") {
                            NotificationSettingsScreen(
                                settingsManager = container.settingsManager,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("about_app") {
                            AboutScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("customization_studio") {
                            CustomizationCenterScreen(
                                customizationManager = container.customizationManager,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToOnlineStore = { navController.navigate("online_asset_store") },
                                onNavigateToHomeDesigner = { navController.navigate("home_screen_designer") },
                                onNavigateToChatDesigner = { navController.navigate("advanced_chat_designer") },
                                onNavigateToColorTypographyStudio = { navController.navigate("color_typography_studio") },
                                onNavigateToIconStore = { navController.navigate("icon_store_and_editor") },
                                onNavigateToWallpaperStore = { navController.navigate("wallpaper_store_and_editor") }
                            )
                        }

                        composable("online_asset_store") {
                            OnlineAssetStoreScreen(
                                assetManager = container.assetManager,
                                customizationManager = container.customizationManager,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("home_screen_designer") {
                            HomeScreenDesignerScreen(
                                customizationManager = container.customizationManager,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("advanced_chat_designer") {
                            AdvancedChatDesignerScreen(
                                customizationManager = container.customizationManager,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("color_typography_studio") {
                            ColorAndTypographyStudioScreen(
                                customizationManager = container.customizationManager,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("icon_store_and_editor") {
                            IconStoreAndEditorScreen(
                                customizationManager = container.customizationManager,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("wallpaper_store_and_editor") {
                            WallpaperStoreAndEditorScreen(
                                customizationManager = container.customizationManager,
                                chatOverridesManager = container.chatOverridesManager,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("contacts") {
                            ContactsScreen(
                                viewModel = contactsViewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onOpenConversation = { convId ->
                                    navController.navigate("chat/$convId")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
