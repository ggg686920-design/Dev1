package com.example.core.customization

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class ExtendedIconAction {
    HOME, CHATS, CONTACTS, SEARCH, SETTINGS, PROFILE,
    SEND, VOICE, CAMERA, GALLERY, FILES, LOCATION, CONTACT,
    ATTACHMENT, EMOJI, BACK, MORE, DELETE, EDIT, PIN, MUTE,
    ARCHIVE, FORWARD, REPLY, DOWNLOAD, SHARE, CALL, VIDEO_CALL,
    FAVORITE, STAR, REFRESH, THEME
}

data class CustomIconEditorConfig(
    val shapeRadiusDp: Int = 12,
    val borderWidthDp: Int = 0,
    val borderColorHex: String = "#38BDF8",
    val backgroundColorHex: String = "#1E293B",
    val iconColorHex: String = "#38BDF8",
    val backgroundOpacity: Float = 1.0f,
    val iconSizeDp: Int = 24,
    val rotationAngle: Float = 0f,
    val hasGlow: Boolean = false,
    val hasShadow: Boolean = false,
    val selectedSymbolName: String = "CHAT"
)

object IconPackEngine {

    fun resolveIcon(action: ExtendedIconAction, style: IconStyle): ImageVector {
        return when (style) {
            IconStyle.MINIMAL, IconStyle.OUTLINE -> when (action) {
                ExtendedIconAction.HOME -> Icons.Outlined.Home
                ExtendedIconAction.CHATS -> Icons.Outlined.ChatBubbleOutline
                ExtendedIconAction.CONTACTS -> Icons.Outlined.PeopleOutline
                ExtendedIconAction.SEARCH -> Icons.Outlined.Search
                ExtendedIconAction.SETTINGS -> Icons.Outlined.Tune
                ExtendedIconAction.PROFILE -> Icons.Outlined.PersonOutline
                ExtendedIconAction.SEND -> Icons.AutoMirrored.Outlined.Send
                ExtendedIconAction.VOICE -> Icons.Outlined.MicNone
                ExtendedIconAction.CAMERA -> Icons.Outlined.PhotoCamera
                ExtendedIconAction.GALLERY -> Icons.Outlined.Image
                ExtendedIconAction.FILES -> Icons.Outlined.InsertDriveFile
                ExtendedIconAction.LOCATION -> Icons.Outlined.LocationOn
                ExtendedIconAction.CONTACT -> Icons.Outlined.AccountBox
                ExtendedIconAction.ATTACHMENT -> Icons.Outlined.AttachFile
                ExtendedIconAction.EMOJI -> Icons.Outlined.SentimentSatisfied
                ExtendedIconAction.BACK -> Icons.AutoMirrored.Outlined.ArrowBack
                ExtendedIconAction.MORE -> Icons.Outlined.MoreVert
                ExtendedIconAction.DELETE -> Icons.Outlined.Delete
                ExtendedIconAction.EDIT -> Icons.Outlined.Edit
                ExtendedIconAction.PIN -> Icons.Outlined.PushPin
                ExtendedIconAction.MUTE -> Icons.AutoMirrored.Outlined.VolumeOff
                ExtendedIconAction.ARCHIVE -> Icons.Outlined.Archive
                ExtendedIconAction.FORWARD -> Icons.AutoMirrored.Outlined.Forward
                ExtendedIconAction.REPLY -> Icons.AutoMirrored.Outlined.Reply
                ExtendedIconAction.DOWNLOAD -> Icons.Outlined.Download
                ExtendedIconAction.SHARE -> Icons.Outlined.Share
                ExtendedIconAction.CALL -> Icons.Outlined.Phone
                ExtendedIconAction.VIDEO_CALL -> Icons.Outlined.Videocam
                ExtendedIconAction.FAVORITE -> Icons.Outlined.FavoriteBorder
                ExtendedIconAction.STAR -> Icons.Outlined.StarBorder
                ExtendedIconAction.REFRESH -> Icons.Outlined.Refresh
                ExtendedIconAction.THEME -> Icons.Outlined.Palette
            }

            IconStyle.ROUNDED -> when (action) {
                ExtendedIconAction.HOME -> Icons.Rounded.Home
                ExtendedIconAction.CHATS -> Icons.Rounded.ChatBubble
                ExtendedIconAction.CONTACTS -> Icons.Rounded.People
                ExtendedIconAction.SEARCH -> Icons.Rounded.Search
                ExtendedIconAction.SETTINGS -> Icons.Rounded.Settings
                ExtendedIconAction.PROFILE -> Icons.Rounded.Person
                ExtendedIconAction.SEND -> Icons.Rounded.Send
                ExtendedIconAction.VOICE -> Icons.Rounded.Mic
                ExtendedIconAction.CAMERA -> Icons.Rounded.PhotoCamera
                ExtendedIconAction.GALLERY -> Icons.Rounded.Image
                ExtendedIconAction.FILES -> Icons.Rounded.Folder
                ExtendedIconAction.LOCATION -> Icons.Rounded.LocationOn
                ExtendedIconAction.CONTACT -> Icons.Rounded.AccountCircle
                ExtendedIconAction.ATTACHMENT -> Icons.Rounded.AttachFile
                ExtendedIconAction.EMOJI -> Icons.Rounded.Mood
                ExtendedIconAction.BACK -> Icons.AutoMirrored.Filled.ArrowBack
                ExtendedIconAction.MORE -> Icons.Rounded.MoreVert
                ExtendedIconAction.DELETE -> Icons.Rounded.Delete
                ExtendedIconAction.EDIT -> Icons.Rounded.Edit
                ExtendedIconAction.PIN -> Icons.Rounded.PushPin
                ExtendedIconAction.MUTE -> Icons.AutoMirrored.Filled.VolumeOff
                ExtendedIconAction.ARCHIVE -> Icons.Rounded.Archive
                ExtendedIconAction.FORWARD -> Icons.AutoMirrored.Filled.Forward
                ExtendedIconAction.REPLY -> Icons.AutoMirrored.Filled.Reply
                ExtendedIconAction.DOWNLOAD -> Icons.Rounded.Download
                ExtendedIconAction.SHARE -> Icons.Rounded.Share
                ExtendedIconAction.CALL -> Icons.Rounded.Phone
                ExtendedIconAction.VIDEO_CALL -> Icons.Rounded.Videocam
                ExtendedIconAction.FAVORITE -> Icons.Rounded.Favorite
                ExtendedIconAction.STAR -> Icons.Rounded.Star
                ExtendedIconAction.REFRESH -> Icons.Rounded.Refresh
                ExtendedIconAction.THEME -> Icons.Rounded.Palette
            }

            IconStyle.SHARP -> when (action) {
                ExtendedIconAction.HOME -> Icons.Sharp.Home
                ExtendedIconAction.CHATS -> Icons.Sharp.Chat
                ExtendedIconAction.CONTACTS -> Icons.Sharp.People
                ExtendedIconAction.SEARCH -> Icons.Sharp.Search
                ExtendedIconAction.SETTINGS -> Icons.Sharp.Settings
                ExtendedIconAction.PROFILE -> Icons.Sharp.Person
                ExtendedIconAction.SEND -> Icons.Sharp.Send
                ExtendedIconAction.VOICE -> Icons.Sharp.Mic
                ExtendedIconAction.CAMERA -> Icons.Sharp.PhotoCamera
                ExtendedIconAction.GALLERY -> Icons.Sharp.Image
                ExtendedIconAction.FILES -> Icons.Sharp.Folder
                ExtendedIconAction.LOCATION -> Icons.Sharp.LocationOn
                ExtendedIconAction.CONTACT -> Icons.Sharp.AccountBox
                ExtendedIconAction.ATTACHMENT -> Icons.Sharp.AttachFile
                ExtendedIconAction.EMOJI -> Icons.Sharp.Face
                ExtendedIconAction.BACK -> Icons.AutoMirrored.Filled.ArrowBack
                ExtendedIconAction.MORE -> Icons.Sharp.MoreVert
                ExtendedIconAction.DELETE -> Icons.Sharp.Delete
                ExtendedIconAction.EDIT -> Icons.Sharp.Edit
                ExtendedIconAction.PIN -> Icons.Sharp.PushPin
                ExtendedIconAction.MUTE -> Icons.AutoMirrored.Filled.VolumeOff
                ExtendedIconAction.ARCHIVE -> Icons.Sharp.Archive
                ExtendedIconAction.FORWARD -> Icons.AutoMirrored.Filled.Forward
                ExtendedIconAction.REPLY -> Icons.AutoMirrored.Filled.Reply
                ExtendedIconAction.DOWNLOAD -> Icons.Sharp.Download
                ExtendedIconAction.SHARE -> Icons.Sharp.Share
                ExtendedIconAction.CALL -> Icons.Sharp.Phone
                ExtendedIconAction.VIDEO_CALL -> Icons.Sharp.Videocam
                ExtendedIconAction.FAVORITE -> Icons.Sharp.Favorite
                ExtendedIconAction.STAR -> Icons.Sharp.Star
                ExtendedIconAction.REFRESH -> Icons.Sharp.Refresh
                ExtendedIconAction.THEME -> Icons.Sharp.Colorize
            }

            else -> when (action) { // FILLED, NEON, GRADIENT, GLASS, GAMING, CLASSIC fallback
                ExtendedIconAction.HOME -> Icons.Filled.Home
                ExtendedIconAction.CHATS -> Icons.Filled.Chat
                ExtendedIconAction.CONTACTS -> Icons.Filled.People
                ExtendedIconAction.SEARCH -> Icons.Filled.Search
                ExtendedIconAction.SETTINGS -> Icons.Filled.Settings
                ExtendedIconAction.PROFILE -> Icons.Filled.Person
                ExtendedIconAction.SEND -> Icons.AutoMirrored.Filled.Send
                ExtendedIconAction.VOICE -> Icons.Filled.Mic
                ExtendedIconAction.CAMERA -> Icons.Filled.CameraAlt
                ExtendedIconAction.GALLERY -> Icons.Filled.PhotoLibrary
                ExtendedIconAction.FILES -> Icons.Filled.Folder
                ExtendedIconAction.LOCATION -> Icons.Filled.LocationOn
                ExtendedIconAction.CONTACT -> Icons.Filled.AccountCircle
                ExtendedIconAction.ATTACHMENT -> Icons.Filled.AttachFile
                ExtendedIconAction.EMOJI -> Icons.Filled.SentimentSatisfiedAlt
                ExtendedIconAction.BACK -> Icons.AutoMirrored.Filled.ArrowBack
                ExtendedIconAction.MORE -> Icons.Filled.MoreVert
                ExtendedIconAction.DELETE -> Icons.Filled.Delete
                ExtendedIconAction.EDIT -> Icons.Filled.Edit
                ExtendedIconAction.PIN -> Icons.Filled.PushPin
                ExtendedIconAction.MUTE -> Icons.AutoMirrored.Filled.VolumeOff
                ExtendedIconAction.ARCHIVE -> Icons.Filled.Archive
                ExtendedIconAction.FORWARD -> Icons.AutoMirrored.Filled.Forward
                ExtendedIconAction.REPLY -> Icons.AutoMirrored.Filled.Reply
                ExtendedIconAction.DOWNLOAD -> Icons.Filled.Download
                ExtendedIconAction.SHARE -> Icons.Filled.Share
                ExtendedIconAction.CALL -> Icons.Filled.Phone
                ExtendedIconAction.VIDEO_CALL -> Icons.Filled.Videocam
                ExtendedIconAction.FAVORITE -> Icons.Filled.Favorite
                ExtendedIconAction.STAR -> Icons.Filled.Star
                ExtendedIconAction.REFRESH -> Icons.Filled.Refresh
                ExtendedIconAction.THEME -> Icons.Filled.Palette
            }
        }
    }
}

@Composable
fun RenderCustomIcon(
    config: CustomIconEditorConfig,
    modifier: Modifier = Modifier
) {
    val bg = parseHexColor(config.backgroundColorHex).copy(alpha = config.backgroundOpacity)
    val iconColor = parseHexColor(config.iconColorHex)
    val shape = RoundedCornerShape(config.shapeRadiusDp.dp)

    Box(
        modifier = modifier
            .size(config.iconSizeDp.dp + 16.dp)
            .then(if (config.hasShadow) Modifier.shadow(4.dp, shape) else Modifier)
            .clip(shape)
            .background(bg)
            .then(
                if (config.borderWidthDp > 0) Modifier.border(config.borderWidthDp.dp, parseHexColor(config.borderColorHex), shape)
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        val vector = when (config.selectedSymbolName) {
            "CHAT" -> Icons.Default.ChatBubble
            "SEND" -> Icons.AutoMirrored.Filled.Send
            "CAMERA" -> Icons.Default.PhotoCamera
            "HEART" -> Icons.Default.Favorite
            "STAR" -> Icons.Default.Star
            "ROCKET" -> Icons.Default.RocketLaunch
            "CODE" -> Icons.Default.Code
            "FIRE" -> Icons.Default.LocalFireDepartment
            else -> Icons.Default.Chat
        }

        Icon(
            imageVector = vector,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier
                .size(config.iconSizeDp.dp)
                .rotate(config.rotationAngle)
        )
    }
}
