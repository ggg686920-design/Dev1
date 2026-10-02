package com.example.core.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtils {
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    fun parseIso(isoString: String?): Date? {
        if (isoString.isNullOrBlank()) return null
        return try {
            val clean = if (isoString.contains(".")) {
                isoString.substringBefore(".") + "Z"
            } else if (isoString.endsWith("Z")) {
                isoString
            } else {
                isoString
            }
            isoFormat.parse(clean.replace("Z", ""))
        } catch (_: Exception) {
            try {
                Date(isoString.toLong())
            } catch (_: Exception) {
                Date()
            }
        }
    }

    fun formatMessageTime(date: Date?): String {
        if (date == null) return ""
        return timeFormat.format(date)
    }

    fun formatMessageTime(isoString: String?): String {
        val date = parseIso(isoString) ?: return ""
        return timeFormat.format(date)
    }

    fun formatRelativeTime(isoString: String?, isArabic: Boolean = true): String {
        val date = parseIso(isoString) ?: return ""
        val now = System.currentTimeMillis()
        val diffMs = now - date.time
        val diffMin = diffMs / (60 * 1000)
        val diffHours = diffMin / 60
        val diffDays = diffHours / 24

        return when {
            diffMin < 1 -> if (isArabic) "الآن" else "Just now"
            diffMin < 60 -> if (isArabic) "منذ $diffMin دقيقة" else "$diffMin min ago"
            diffHours < 24 -> timeFormat.format(date)
            diffDays == 1L -> if (isArabic) "أمس" else "Yesterday"
            diffDays < 7 -> if (isArabic) "منذ $diffDays أيام" else "$diffDays days ago"
            else -> dateFormat.format(date)
        }
    }
}
