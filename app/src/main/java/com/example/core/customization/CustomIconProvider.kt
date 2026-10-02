package com.example.core.customization

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.sharp.*
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class AppIconAction {
    HOME,
    CHATS,
    CONTACTS,
    SEARCH,
    SETTINGS,
    SEND,
    VOICE,
    CAMERA,
    GALLERY,
    FILES,
    LOCATION,
    CONTACT,
    BACK,
    MORE,
    ATTACHMENT,
    EMOJI,
    CUSTOMIZATION
}

object CustomIconProvider {

    fun getVector(action: AppIconAction, style: IconStyle): ImageVector {
        return when (style) {
            IconStyle.MINIMAL -> when (action) {
                AppIconAction.HOME -> Icons.Outlined.Home
                AppIconAction.CHATS -> Icons.Outlined.ChatBubbleOutline
                AppIconAction.CONTACTS -> Icons.Outlined.PeopleOutline
                AppIconAction.SEARCH -> Icons.Outlined.Search
                AppIconAction.SETTINGS -> Icons.Outlined.Tune
                AppIconAction.SEND -> Icons.AutoMirrored.Outlined.Send
                AppIconAction.VOICE -> Icons.Outlined.MicNone
                AppIconAction.CAMERA -> Icons.Outlined.PhotoCamera
                AppIconAction.GALLERY -> Icons.Outlined.Image
                AppIconAction.FILES -> Icons.Outlined.InsertDriveFile
                AppIconAction.LOCATION -> Icons.Outlined.LocationOn
                AppIconAction.CONTACT -> Icons.Outlined.Person
                AppIconAction.BACK -> Icons.AutoMirrored.Outlined.ArrowBack
                AppIconAction.MORE -> Icons.Outlined.MoreVert
                AppIconAction.ATTACHMENT -> Icons.Outlined.AttachFile
                AppIconAction.EMOJI -> Icons.Outlined.SentimentSatisfied
                AppIconAction.CUSTOMIZATION -> Icons.Outlined.Palette
            }
            IconStyle.OUTLINE -> when (action) {
                AppIconAction.HOME -> Icons.Outlined.Home
                AppIconAction.CHATS -> Icons.Outlined.Chat
                AppIconAction.CONTACTS -> Icons.Outlined.Contacts
                AppIconAction.SEARCH -> Icons.Outlined.Search
                AppIconAction.SETTINGS -> Icons.Outlined.Settings
                AppIconAction.SEND -> Icons.AutoMirrored.Outlined.Send
                AppIconAction.VOICE -> Icons.Outlined.Mic
                AppIconAction.CAMERA -> Icons.Outlined.CameraAlt
                AppIconAction.GALLERY -> Icons.Outlined.PhotoLibrary
                AppIconAction.FILES -> Icons.Outlined.Folder
                AppIconAction.LOCATION -> Icons.Outlined.Place
                AppIconAction.CONTACT -> Icons.Outlined.AccountCircle
                AppIconAction.BACK -> Icons.AutoMirrored.Outlined.ArrowBack
                AppIconAction.MORE -> Icons.Outlined.MoreVert
                AppIconAction.ATTACHMENT -> Icons.Outlined.AttachFile
                AppIconAction.EMOJI -> Icons.Outlined.Mood
                AppIconAction.CUSTOMIZATION -> Icons.Outlined.ColorLens
            }
            IconStyle.FILLED -> when (action) {
                AppIconAction.HOME -> Icons.Filled.Home
                AppIconAction.CHATS -> Icons.Filled.Chat
                AppIconAction.CONTACTS -> Icons.Filled.People
                AppIconAction.SEARCH -> Icons.Filled.Search
                AppIconAction.SETTINGS -> Icons.Filled.Settings
                AppIconAction.SEND -> Icons.AutoMirrored.Filled.Send
                AppIconAction.VOICE -> Icons.Filled.Mic
                AppIconAction.CAMERA -> Icons.Filled.CameraAlt
                AppIconAction.GALLERY -> Icons.Filled.PhotoLibrary
                AppIconAction.FILES -> Icons.Filled.Folder
                AppIconAction.LOCATION -> Icons.Filled.LocationOn
                AppIconAction.CONTACT -> Icons.Filled.AccountCircle
                AppIconAction.BACK -> Icons.AutoMirrored.Filled.ArrowBack
                AppIconAction.MORE -> Icons.Filled.MoreVert
                AppIconAction.ATTACHMENT -> Icons.Filled.AttachFile
                AppIconAction.EMOJI -> Icons.Filled.SentimentSatisfiedAlt
                AppIconAction.CUSTOMIZATION -> Icons.Filled.Palette
            }
            IconStyle.ROUNDED -> when (action) {
                AppIconAction.HOME -> Icons.Rounded.Home
                AppIconAction.CHATS -> Icons.Rounded.ChatBubble
                AppIconAction.CONTACTS -> Icons.Rounded.People
                AppIconAction.SEARCH -> Icons.Rounded.Search
                AppIconAction.SETTINGS -> Icons.Rounded.Settings
                AppIconAction.SEND -> Icons.Rounded.Send
                AppIconAction.VOICE -> Icons.Rounded.Mic
                AppIconAction.CAMERA -> Icons.Rounded.PhotoCamera
                AppIconAction.GALLERY -> Icons.Rounded.Image
                AppIconAction.FILES -> Icons.Rounded.Folder
                AppIconAction.LOCATION -> Icons.Rounded.LocationOn
                AppIconAction.CONTACT -> Icons.Rounded.AccountCircle
                AppIconAction.BACK -> Icons.AutoMirrored.Filled.ArrowBack
                AppIconAction.MORE -> Icons.Rounded.MoreVert
                AppIconAction.ATTACHMENT -> Icons.Rounded.AttachFile
                AppIconAction.EMOJI -> Icons.Rounded.Mood
                AppIconAction.CUSTOMIZATION -> Icons.Rounded.Palette
            }
            IconStyle.SHARP -> when (action) {
                AppIconAction.HOME -> Icons.Sharp.Home
                AppIconAction.CHATS -> Icons.Sharp.Chat
                AppIconAction.CONTACTS -> Icons.Sharp.People
                AppIconAction.SEARCH -> Icons.Sharp.Search
                AppIconAction.SETTINGS -> Icons.Sharp.Settings
                AppIconAction.SEND -> Icons.Sharp.Send
                AppIconAction.VOICE -> Icons.Sharp.Mic
                AppIconAction.CAMERA -> Icons.Sharp.PhotoCamera
                AppIconAction.GALLERY -> Icons.Sharp.Image
                AppIconAction.FILES -> Icons.Sharp.Folder
                AppIconAction.LOCATION -> Icons.Sharp.LocationOn
                AppIconAction.CONTACT -> Icons.Sharp.AccountBox
                AppIconAction.BACK -> Icons.AutoMirrored.Filled.ArrowBack
                AppIconAction.MORE -> Icons.Sharp.MoreVert
                AppIconAction.ATTACHMENT -> Icons.Sharp.AttachFile
                AppIconAction.EMOJI -> Icons.Sharp.Face
                AppIconAction.CUSTOMIZATION -> Icons.Sharp.Colorize
            }
            IconStyle.GLASS -> when (action) {
                AppIconAction.HOME -> Icons.Rounded.Home
                AppIconAction.CHATS -> Icons.Rounded.Forum
                AppIconAction.CONTACTS -> Icons.Rounded.SupervisedUserCircle
                AppIconAction.SEARCH -> Icons.Rounded.Search
                AppIconAction.SETTINGS -> Icons.Rounded.DisplaySettings
                AppIconAction.SEND -> Icons.Rounded.Send
                AppIconAction.VOICE -> Icons.Rounded.MicNone
                AppIconAction.CAMERA -> Icons.Rounded.Camera
                AppIconAction.GALLERY -> Icons.Rounded.Collections
                AppIconAction.FILES -> Icons.Rounded.SnippetFolder
                AppIconAction.LOCATION -> Icons.Rounded.Explore
                AppIconAction.CONTACT -> Icons.Rounded.AccountCircle
                AppIconAction.BACK -> Icons.AutoMirrored.Filled.ArrowBack
                AppIconAction.MORE -> Icons.Rounded.MoreHoriz
                AppIconAction.ATTACHMENT -> Icons.Rounded.Attachment
                AppIconAction.EMOJI -> Icons.Rounded.EmojiEmotions
                AppIconAction.CUSTOMIZATION -> Icons.Rounded.AutoAwesome
            }
            IconStyle.NEON, IconStyle.GRADIENT, IconStyle.GAMING -> when (action) {
                AppIconAction.HOME -> Icons.Filled.Home
                AppIconAction.CHATS -> Icons.Filled.Forum
                AppIconAction.CONTACTS -> Icons.Filled.Groups
                AppIconAction.SEARCH -> Icons.Filled.Search
                AppIconAction.SETTINGS -> Icons.Filled.SettingsSuggest
                AppIconAction.SEND -> Icons.AutoMirrored.Filled.Send
                AppIconAction.VOICE -> Icons.Filled.KeyboardVoice
                AppIconAction.CAMERA -> Icons.Filled.CameraAlt
                AppIconAction.GALLERY -> Icons.Filled.PhotoLibrary
                AppIconAction.FILES -> Icons.Filled.FolderSpecial
                AppIconAction.LOCATION -> Icons.Filled.MyLocation
                AppIconAction.CONTACT -> Icons.Filled.Badge
                AppIconAction.BACK -> Icons.AutoMirrored.Filled.ArrowBack
                AppIconAction.MORE -> Icons.Filled.MoreVert
                AppIconAction.ATTACHMENT -> Icons.Filled.AttachFile
                AppIconAction.EMOJI -> Icons.Filled.AddReaction
                AppIconAction.CUSTOMIZATION -> Icons.Filled.AutoFixHigh
            }
            IconStyle.CLASSIC -> when (action) {
                AppIconAction.HOME -> Icons.Filled.Home
                AppIconAction.CHATS -> Icons.Filled.QuestionAnswer
                AppIconAction.CONTACTS -> Icons.Filled.Contacts
                AppIconAction.SEARCH -> Icons.Filled.Search
                AppIconAction.SETTINGS -> Icons.Filled.Settings
                AppIconAction.SEND -> Icons.AutoMirrored.Filled.Send
                AppIconAction.VOICE -> Icons.Filled.Mic
                AppIconAction.CAMERA -> Icons.Filled.CameraAlt
                AppIconAction.GALLERY -> Icons.Filled.Image
                AppIconAction.FILES -> Icons.Filled.Folder
                AppIconAction.LOCATION -> Icons.Filled.PinDrop
                AppIconAction.CONTACT -> Icons.Filled.PermIdentity
                AppIconAction.BACK -> Icons.AutoMirrored.Filled.ArrowBack
                AppIconAction.MORE -> Icons.Filled.MoreVert
                AppIconAction.ATTACHMENT -> Icons.Filled.AttachFile
                AppIconAction.EMOJI -> Icons.Filled.Mood
                AppIconAction.CUSTOMIZATION -> Icons.Filled.Brush
            }
        }
    }
}

@Composable
fun ThemedIcon(
    action: AppIconAction,
    style: IconStyle,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    contentDescription: String? = null,
    size: Dp = 24.dp
) {
    val vector = CustomIconProvider.getVector(action, style)

    when (style) {
        IconStyle.GLASS -> {
            Box(
                modifier = modifier
                    .size(size + 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(tint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = vector,
                    contentDescription = contentDescription,
                    tint = tint,
                    modifier = Modifier.size(size)
                )
            }
        }
        IconStyle.NEON -> {
            Box(
                modifier = modifier
                    .size(size + 6.dp)
                    .shadow(elevation = 4.dp, shape = CircleShape, spotColor = tint)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = vector,
                    contentDescription = contentDescription,
                    tint = tint,
                    modifier = Modifier.size(size)
                )
            }
        }
        else -> {
            Icon(
                imageVector = vector,
                contentDescription = contentDescription,
                tint = tint,
                modifier = modifier.size(size)
            )
        }
    }
}
