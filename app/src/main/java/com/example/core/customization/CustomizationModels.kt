package com.example.core.customization

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.json.JSONObject

enum class IconStyle(val id: String, val titleAr: String, val titleEn: String) {
    MINIMAL("minimal", "مينيمال بسيط", "Minimal"),
    OUTLINE("outline", "مفرغ ناعم", "Outline"),
    FILLED("filled", "ممتلئ واضح", "Filled"),
    ROUNDED("rounded", "دائري لطيف", "Rounded"),
    SHARP("sharp", "حواف حادة", "Sharp"),
    GLASS("glass", "زجاجي لامع", "Glass"),
    NEON("neon", "نيون متوهج", "Neon Glow"),
    GRADIENT("gradient", "تدرج لوني", "Gradient"),
    GAMING("gaming", "جيمنج مستقبلي", "Gaming Cyber"),
    CLASSIC("classic", "كلاسيكي أصيل", "Classic")
}

enum class NavBarStyle(val id: String, val titleAr: String, val titleEn: String) {
    CLASSIC("classic", "كلاسيكي ثابت", "Classic"),
    FLOATING("floating", "عائم حديث", "Floating Bar"),
    PILL("pill", "كبسولة منحنية", "Pill Shape"),
    GLASS("glass", "زجاجي شفاف", "Glassmorphic"),
    MINIMAL("minimal", "بسيط بدون نصوص", "Minimal"),
    COMPACT("compact", "مدمج صغير", "Compact"),
    LARGE_ICONS("large_icons", "أيقونات بارزة", "Large Icons"),
    TEXT_ICONS("text_icons", "أيقونات مع تسميات", "Text + Icons"),
    ICONS_ONLY("icons_only", "أيقونات فقط", "Icons Only")
}

enum class InputBarStyle(val id: String, val titleAr: String, val titleEn: String) {
    ROUNDED("rounded", "منحني كلاسيكي", "Rounded"),
    PILL("pill", "كبسولة كاملة", "Full Pill"),
    GLASS("glass", "زجاج شفاف", "Frosted Glass"),
    FLOATING("floating", "شريط عائم", "Floating Dock"),
    MINIMAL("minimal", "خط سفلي خفيف", "Minimal Flat"),
    COMPACT("compact", "شريط مدمج", "Compact")
}

enum class ButtonStyle(val id: String, val titleAr: String, val titleEn: String) {
    FILLED("filled", "ممتلئ أساسي", "Filled"),
    OUTLINED("outlined", "حد خارجي أنيق", "Outlined"),
    GHOST("ghost", "خلفية شفافة", "Ghost"),
    PILL("pill", "كبسولي مستدير", "Pill"),
    ROUNDED("rounded", "حواف منحنية", "Rounded"),
    GLASS("glass", "زجاجي مضيء", "Glass"),
    GRADIENT("gradient", "تدرج براق", "Gradient Vibrant"),
    NEON("neon", "نيون سيبراني", "Cyber Neon")
}

enum class AnimationSpeed(val id: String, val titleAr: String, val titleEn: String, val factor: Float) {
    NONE("none", "بدون حركة (سريع)", "None (Instant)", 0f),
    MINIMAL("minimal", "حركة خفيفة (150ms)", "Minimal", 0.5f),
    SMOOTH("smooth", "سلس ناعم (300ms)", "Smooth", 1.0f),
    DYNAMIC("dynamic", "ديناميكي حيوي", "Dynamic", 1.3f),
    BOUNCY("bouncy", "نطاط مرح", "Bouncy Spring", 1.6f)
}

enum class DecorationStyle(val id: String, val titleAr: String, val titleEn: String) {
    NONE("none", "بدون زخارف", "None"),
    STARS("stars", "نجوم كونية ✨", "Cosmic Stars"),
    HEARTS("hearts", "قلوب لطيفة 💖", "Hearts"),
    FLOWERS("flowers", "زهور ربيعية 🌸", "Floral"),
    GEOMETRIC("geometric", "أشكال هندسية 📐", "Geometric"),
    TECH_DOTS("tech_dots", "نقاط رقمية 💻", "Tech Matrix"),
    ABSTRACT("abstract", "أمواج تجريدية 🌊", "Abstract Waves"),
    GLOW_FRAMES("glow_frames", "إطارات مضيئة ⚡", "Neon Frames")
}

enum class FontFamilyType(val id: String, val titleAr: String, val titleEn: String) {
    SYSTEM("system", "خط النظام الافتراضي", "System Default"),
    SANS_SERIF("sans", "سانس حديث (Cairo/Inter)", "Modern Sans"),
    SERIF("serif", "سيريف تقليدي أنيق", "Classic Serif"),
    MONOSPACE("mono", "أحادي المسافة كود", "Code Monospace"),
    MODERN_ROUNDED("rounded", "منحني ناعم وودود", "Modern Rounded"),
    TECH_CODE("tech", "تقني مطورين Dev", "Dev Tech")
}

enum class BubbleShape(val id: String, val titleAr: String, val titleEn: String) {
    ROUNDED("rounded", "منحني كلاسيكي", "Rounded"),
    PILL("pill", "كبسولة كاملة", "Full Pill"),
    CHAT_TAIL("chat_tail", "مع ذيل المحادثة", "With Chat Tail"),
    MODERN_LEAF("modern_leaf", "ورقة عصرية متباينة", "Modern Leaf"),
    SHARP("sharp", "زوايا هندسية حادة", "Sharp Geometric")
}

data class BubbleConfig(
    val shape: BubbleShape = BubbleShape.ROUNDED,
    val radiusDp: Int = 18,
    val paddingDp: Int = 12,
    val borderWidthDp: Int = 0,
    val hasBorder: Boolean = false,
    val borderColorHex: String = "#38BDF8",
    val hasShadow: Boolean = true,
    val isGradient: Boolean = false,
    val transparency: Float = 0.95f,
    val incomingBgHex: String = "#1E293B",
    val outgoingBgHex: String = "#0284C7",
    val incomingTextHex: String = "#F8FAFC",
    val outgoingTextHex: String = "#FFFFFF",
    val timestampColorHex: String = "#94A3B8",
    val hasTail: Boolean = true,
    val spacingDp: Int = 8
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("shape", shape.id)
        put("radiusDp", radiusDp)
        put("paddingDp", paddingDp)
        put("borderWidthDp", borderWidthDp)
        put("hasBorder", hasBorder)
        put("borderColorHex", borderColorHex)
        put("hasShadow", hasShadow)
        put("isGradient", isGradient)
        put("transparency", transparency.toDouble())
        put("incomingBgHex", incomingBgHex)
        put("outgoingBgHex", outgoingBgHex)
        put("incomingTextHex", incomingTextHex)
        put("outgoingTextHex", outgoingTextHex)
        put("timestampColorHex", timestampColorHex)
        put("hasTail", hasTail)
        put("spacingDp", spacingDp)
    }

    companion object {
        fun fromJson(json: JSONObject): BubbleConfig {
            val shapeStr = json.optString("shape", BubbleShape.ROUNDED.id)
            val shape = BubbleShape.entries.find { it.id == shapeStr } ?: BubbleShape.ROUNDED
            return BubbleConfig(
                shape = shape,
                radiusDp = json.optInt("radiusDp", 18),
                paddingDp = json.optInt("paddingDp", 12),
                borderWidthDp = json.optInt("borderWidthDp", 0),
                hasBorder = json.optBoolean("hasBorder", false),
                borderColorHex = json.optString("borderColorHex", "#38BDF8"),
                hasShadow = json.optBoolean("hasShadow", true),
                isGradient = json.optBoolean("isGradient", false),
                transparency = json.optDouble("transparency", 0.95).toFloat(),
                incomingBgHex = json.optString("incomingBgHex", "#1E293B"),
                outgoingBgHex = json.optString("outgoingBgHex", "#0284C7"),
                incomingTextHex = json.optString("incomingTextHex", "#F8FAFC"),
                outgoingTextHex = json.optString("outgoingTextHex", "#FFFFFF"),
                timestampColorHex = json.optString("timestampColorHex", "#94A3B8"),
                hasTail = json.optBoolean("hasTail", true),
                spacingDp = json.optInt("spacingDp", 8)
            )
        }
    }
}

data class TypographyConfig(
    val fontFamilyType: FontFamilyType = FontFamilyType.SANS_SERIF,
    val fontSizeScale: Float = 1.0f,
    val fontWeightBold: Boolean = false,
    val isItalic: Boolean = false,
    val isUnderline: Boolean = false,
    val isMonospace: Boolean = false,
    val hasGlow: Boolean = false,
    val hasShadow: Boolean = false,
    val letterSpacingSp: Float = 0f,
    val lineHeightMultiplier: Float = 1.25f
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("fontFamilyType", fontFamilyType.id)
        put("fontSizeScale", fontSizeScale.toDouble())
        put("fontWeightBold", fontWeightBold)
        put("isItalic", isItalic)
        put("isUnderline", isUnderline)
        put("isMonospace", isMonospace)
        put("hasGlow", hasGlow)
        put("hasShadow", hasShadow)
        put("letterSpacingSp", letterSpacingSp.toDouble())
        put("lineHeightMultiplier", lineHeightMultiplier.toDouble())
    }

    companion object {
        fun fromJson(json: JSONObject): TypographyConfig {
            val fontStr = json.optString("fontFamilyType", FontFamilyType.SANS_SERIF.id)
            val font = FontFamilyType.entries.find { it.id == fontStr } ?: FontFamilyType.SANS_SERIF
            return TypographyConfig(
                fontFamilyType = font,
                fontSizeScale = json.optDouble("fontSizeScale", 1.0).toFloat(),
                fontWeightBold = json.optBoolean("fontWeightBold", false),
                isItalic = json.optBoolean("isItalic", false),
                isUnderline = json.optBoolean("isUnderline", false),
                isMonospace = json.optBoolean("isMonospace", false),
                hasGlow = json.optBoolean("hasGlow", false),
                hasShadow = json.optBoolean("hasShadow", false),
                letterSpacingSp = json.optDouble("letterSpacingSp", 0.0).toFloat(),
                lineHeightMultiplier = json.optDouble("lineHeightMultiplier", 1.25).toFloat()
            )
        }
    }
}

data class VisualEffectsConfig(
    val blurEnabled: Boolean = true,
    val glassmorphism: Boolean = true,
    val glowEnabled: Boolean = false,
    val subtleShadows: Boolean = true,
    val noiseEnabled: Boolean = false,
    val transparencyLevel: Float = 0.90f,
    val reduceMotion: Boolean = false
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("blurEnabled", blurEnabled)
        put("glassmorphism", glassmorphism)
        put("glowEnabled", glowEnabled)
        put("subtleShadows", subtleShadows)
        put("noiseEnabled", noiseEnabled)
        put("transparencyLevel", transparencyLevel.toDouble())
        put("reduceMotion", reduceMotion)
    }

    companion object {
        fun fromJson(json: JSONObject): VisualEffectsConfig = VisualEffectsConfig(
            blurEnabled = json.optBoolean("blurEnabled", true),
            glassmorphism = json.optBoolean("glassmorphism", true),
            glowEnabled = json.optBoolean("glowEnabled", false),
            subtleShadows = json.optBoolean("subtleShadows", true),
            noiseEnabled = json.optBoolean("noiseEnabled", false),
            transparencyLevel = json.optDouble("transparencyLevel", 0.90).toFloat(),
            reduceMotion = json.optBoolean("reduceMotion", false)
        )
    }
}

data class WallpaperItem(
    val id: String,
    val title: String,
    val category: String,
    val primaryHex: String,
    val secondaryHex: String,
    val accentHex: String = "#38BDF8",
    val patternType: String = "LINEAR_GRADIENT", // SOLID, LINEAR_GRADIENT, RADIAL_GRADIENT, MESH, GRID, DOTS, STARS, HEXAGONS, WAVES, CIRCUITS
    val isDark: Boolean = true,
    val isVideoLoop: Boolean = false
)

data class ThemeConfig(
    val id: String,
    val name: String,
    val description: String = "",
    val category: String = "Modern",
    val isDark: Boolean = true,
    val primaryColorHex: String = "#0284C7",
    val secondaryColorHex: String = "#0F172A",
    val backgroundColorHex: String = "#0B1120",
    val surfaceColorHex: String = "#111827",
    val surfaceVariantColorHex: String = "#1E293B",
    val textColorHex: String = "#F8FAFC",
    val textSecondaryHex: String = "#94A3B8",
    val accentColorHex: String = "#38BDF8",
    val cardColorHex: String = "#1E293B",
    val cardRadiusDp: Int = 16,
    val bubbleConfig: BubbleConfig = BubbleConfig(),
    val navBarStyle: NavBarStyle = NavBarStyle.FLOATING,
    val inputBarStyle: InputBarStyle = InputBarStyle.PILL,
    val buttonStyle: ButtonStyle = ButtonStyle.PILL,
    val iconStyle: IconStyle = IconStyle.ROUNDED,
    val animationSpeed: AnimationSpeed = AnimationSpeed.SMOOTH,
    val decorationStyle: DecorationStyle = DecorationStyle.NONE,
    val typography: TypographyConfig = TypographyConfig(),
    val effects: VisualEffectsConfig = VisualEffectsConfig(),
    val wallpaperId: String = "slate_cyber",
    val isCustom: Boolean = false,
    val isFavorite: Boolean = false,
    val author: String = "dev Core"
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("name", name)
        put("description", description)
        put("category", category)
        put("isDark", isDark)
        put("primaryColorHex", primaryColorHex)
        put("secondaryColorHex", secondaryColorHex)
        put("backgroundColorHex", backgroundColorHex)
        put("surfaceColorHex", surfaceColorHex)
        put("surfaceVariantColorHex", surfaceVariantColorHex)
        put("textColorHex", textColorHex)
        put("textSecondaryHex", textSecondaryHex)
        put("accentColorHex", accentColorHex)
        put("cardColorHex", cardColorHex)
        put("cardRadiusDp", cardRadiusDp)
        put("bubbleConfig", bubbleConfig.toJson())
        put("navBarStyle", navBarStyle.id)
        put("inputBarStyle", inputBarStyle.id)
        put("buttonStyle", buttonStyle.id)
        put("iconStyle", iconStyle.id)
        put("animationSpeed", animationSpeed.id)
        put("decorationStyle", decorationStyle.id)
        put("typography", typography.toJson())
        put("effects", effects.toJson())
        put("wallpaperId", wallpaperId)
        put("isCustom", isCustom)
        put("isFavorite", isFavorite)
        put("author", author)
    }

    companion object {
        fun fromJson(json: JSONObject): ThemeConfig {
            val navStr = json.optString("navBarStyle", NavBarStyle.FLOATING.id)
            val inputStr = json.optString("inputBarStyle", InputBarStyle.PILL.id)
            val btnStr = json.optString("buttonStyle", ButtonStyle.PILL.id)
            val iconStr = json.optString("iconStyle", IconStyle.ROUNDED.id)
            val animStr = json.optString("animationSpeed", AnimationSpeed.SMOOTH.id)
            val decorStr = json.optString("decorationStyle", DecorationStyle.NONE.id)

            val bubbleObj = json.optJSONObject("bubbleConfig") ?: JSONObject()
            val typoObj = json.optJSONObject("typography") ?: JSONObject()
            val effObj = json.optJSONObject("effects") ?: JSONObject()

            return ThemeConfig(
                id = json.optString("id", "custom_${System.currentTimeMillis()}"),
                name = json.optString("name", "Custom Theme"),
                description = json.optString("description", ""),
                category = json.optString("category", "Custom"),
                isDark = json.optBoolean("isDark", true),
                primaryColorHex = json.optString("primaryColorHex", "#0284C7"),
                secondaryColorHex = json.optString("secondaryColorHex", "#0F172A"),
                backgroundColorHex = json.optString("backgroundColorHex", "#0B1120"),
                surfaceColorHex = json.optString("surfaceColorHex", "#111827"),
                surfaceVariantColorHex = json.optString("surfaceVariantColorHex", "#1E293B"),
                textColorHex = json.optString("textColorHex", "#F8FAFC"),
                textSecondaryHex = json.optString("textSecondaryHex", "#94A3B8"),
                accentColorHex = json.optString("accentColorHex", "#38BDF8"),
                cardColorHex = json.optString("cardColorHex", "#1E293B"),
                cardRadiusDp = json.optInt("cardRadiusDp", 16),
                bubbleConfig = BubbleConfig.fromJson(bubbleObj),
                navBarStyle = NavBarStyle.entries.find { it.id == navStr } ?: NavBarStyle.FLOATING,
                inputBarStyle = InputBarStyle.entries.find { it.id == inputStr } ?: InputBarStyle.PILL,
                buttonStyle = ButtonStyle.entries.find { it.id == btnStr } ?: ButtonStyle.PILL,
                iconStyle = IconStyle.entries.find { it.id == iconStr } ?: IconStyle.ROUNDED,
                animationSpeed = AnimationSpeed.entries.find { it.id == animStr } ?: AnimationSpeed.SMOOTH,
                decorationStyle = DecorationStyle.entries.find { it.id == decorStr } ?: DecorationStyle.NONE,
                typography = TypographyConfig.fromJson(typoObj),
                effects = VisualEffectsConfig.fromJson(effObj),
                wallpaperId = json.optString("wallpaperId", "slate_cyber"),
                isCustom = json.optBoolean("isCustom", false),
                isFavorite = json.optBoolean("isFavorite", false),
                author = json.optString("author", "dev User")
            )
        }
    }
}

data class StoreThemeItem(
    val id: String,
    val title: String,
    val author: String,
    val category: String,
    val downloadsCount: String,
    val rating: Float,
    val isFeatured: Boolean = false,
    val isTrending: Boolean = false,
    val isNew: Boolean = false,
    val previewPrimaryHex: String,
    val previewBgHex: String,
    val previewAccentHex: String,
    val themeConfig: ThemeConfig
)

fun parseHexColor(hex: String, fallback: Color = Color.Gray): Color {
    return try {
        val clean = hex.removePrefix("#").trim()
        val longVal = when (clean.length) {
            6 -> 0xFF000000 or clean.toLong(16)
            8 -> clean.toLong(16)
            3 -> {
                val r = clean.substring(0, 1).repeat(2)
                val g = clean.substring(1, 2).repeat(2)
                val b = clean.substring(2, 3).repeat(2)
                0xFF000000 or "$r$g$b".toLong(16)
            }
            else -> return fallback
        }
        Color(longVal)
    } catch (_: Exception) {
        fallback
    }
}
