package com.example.core.customization

data class BubblePresetItem(
    val id: String,
    val name: String,
    val description: String,
    val config: BubbleConfig
)

object ExpandedPresets {

    // ----------------------------------------------------
    // 20+ Advanced Handcrafted Bubble Presets
    // ----------------------------------------------------
    val bubblePresets: List<BubblePresetItem> = listOf(
        BubblePresetItem("b_classic_rounded", "كلاسيكي متناسق (18dp)", "حواف مستديرة ناعمة تلائم معظم الثيمات", BubbleConfig(shape = BubbleShape.ROUNDED, radiusDp = 18, paddingDp = 12, hasShadow = true)),
        BubblePresetItem("b_full_pill", "كبسولة عصرية كاملة (24dp)", "شكل كبسولي دائري بالكامل وودود", BubbleConfig(shape = BubbleShape.PILL, radiusDp = 24, paddingDp = 14, hasShadow = true)),
        BubblePresetItem("b_speech_tail", "مع ذيل المحادثة الأصيل", "شكل المحادثة التقليدي مع ذيل موجه للمرسل", BubbleConfig(shape = BubbleShape.CHAT_TAIL, radiusDp = 16, paddingDp = 12, hasTail = true)),
        BubblePresetItem("b_modern_leaf", "ورقة عصرية متباينة", "زوايا متناظرة متباينة بتصميم فني حديث", BubbleConfig(shape = BubbleShape.MODERN_LEAF, radiusDp = 20, paddingDp = 12)),
        BubblePresetItem("b_sharp_tech", "هندسي حاد Dev (4dp)", "زوايا هندسية تقنية تناسب مطوري البرمجيات", BubbleConfig(shape = BubbleShape.SHARP, radiusDp = 4, paddingDp = 10, hasBorder = true, borderWidthDp = 1, borderColorHex = "#0284C7")),
        BubblePresetItem("b_cyber_neon", "نيون سيبراني متوهج", "حدود خارجية مضيئة باللون الفيروزي", BubbleConfig(shape = BubbleShape.ROUNDED, radiusDp = 16, hasBorder = true, borderWidthDp = 2, borderColorHex = "#38BDF8", outgoingBgHex = "#0369A1")),
        BubblePresetItem("b_glass_frost", "زجاجي ثلجي شفاف", "شفافية عالية بنسبة 80% مع انعكاس ضوئي", BubbleConfig(shape = BubbleShape.PILL, radiusDp = 22, transparency = 0.82f, hasShadow = true)),
        BubblePresetItem("b_floating_card", "بطاقة عائمة عميقة", "ظلال واضحة تعطي إحساساً بالعمق والبروز", BubbleConfig(shape = BubbleShape.ROUNDED, radiusDp = 20, paddingDp = 14, hasShadow = true, spacingDp = 12)),
        BubblePresetItem("b_compact_dense", "مدمج صغير (6dp)", "أقصى استغلال للمساحة في الشاشات الطويلة", BubbleConfig(shape = BubbleShape.ROUNDED, radiusDp = 8, paddingDp = 8, spacingDp = 4)),
        BubblePresetItem("b_luxury_gold", "إطار ذهبي ملكي", "حدود ذهبية فاخرة للرسائل المميزة", BubbleConfig(shape = BubbleShape.ROUNDED, radiusDp = 16, hasBorder = true, borderWidthDp = 1, borderColorHex = "#F59E0B", outgoingBgHex = "#D97706")),
        BubblePresetItem("b_emerald_forest", "غابة الزمرد المنحنية", "ألوان طبيعية هادئة بدرجات الأخضر الغني", BubbleConfig(shape = BubbleShape.CHAT_TAIL, radiusDp = 18, incomingBgHex = "#064E3B", outgoingBgHex = "#059669")),
        BubblePresetItem("b_amethyst_cloud", "سحابة الجمشت الملكية", "بنفسجي ملكي هادئ بتدرجات راقية", BubbleConfig(shape = BubbleShape.ROUNDED, radiusDp = 20, incomingBgHex = "#3B1482", outgoingBgHex = "#7C3AED")),
        BubblePresetItem("b_rose_petal", "بتلات الورد الوردية", "كبسولة ناعمة وردية خفيفة", BubbleConfig(shape = BubbleShape.PILL, radiusDp = 24, incomingBgHex = "#FFE4E6", outgoingBgHex = "#F43F5E", incomingTextHex = "#881337")),
        BubblePresetItem("b_retro_typewriter", "ورق الآلة الكاتبة ريترو", "بني دافئ كلاسيكي بورق قديم", BubbleConfig(shape = BubbleShape.ROUNDED, radiusDp = 10, incomingBgHex = "#EFE0C9", outgoingBgHex = "#92400E", incomingTextHex = "#451A03")),
        BubblePresetItem("b_brutalist_mono", "بروتاليست حاد جريء", "أبيض وأسود مع حدود سميكة سوداء", BubbleConfig(shape = BubbleShape.SHARP, radiusDp = 0, hasBorder = true, borderWidthDp = 2, borderColorHex = "#000000", incomingBgHex = "#EEEEEE", outgoingBgHex = "#000000")),
        BubblePresetItem("b_tokyo_cyan", "طوكيو سيان الليلي", "سماوي مضيء على خلفية رمادية فاحمة", BubbleConfig(shape = BubbleShape.MODERN_LEAF, radiusDp = 18, hasBorder = true, borderWidthDp = 1, borderColorHex = "#06B6D4", outgoingBgHex = "#0891B2")),
        BubblePresetItem("b_solar_amber", "توهج شمس الأصيل", "برتقالي كهرماني دافئ مشرق", BubbleConfig(shape = BubbleShape.ROUNDED, radiusDp = 16, incomingBgHex = "#431407", outgoingBgHex = "#EA580C")),
        BubblePresetItem("b_nordic_pine", "صنوبر إسكندنافي هادئ", "أخضر زيتوني رمادي للراحة البصرية", BubbleConfig(shape = BubbleShape.ROUNDED, radiusDp = 14, incomingBgHex = "#1F2937", outgoingBgHex = "#10B981")),
        BubblePresetItem("b_ultra_flat", "فلات بسيط ناعم", "بدون حدود أو ظلال، بسيط جداً", BubbleConfig(shape = BubbleShape.ROUNDED, radiusDp = 14, hasShadow = false, hasBorder = false)),
        BubblePresetItem("b_supernova_star", "سوبرنوفا سديم الفضاء", "توهج أرجواني متدرج", BubbleConfig(shape = BubbleShape.PILL, radiusDp = 20, isGradient = true, incomingBgHex = "#2B1B61", outgoingBgHex = "#9333EA"))
    )

    // ----------------------------------------------------
    // Expanded Button Presets
    // ----------------------------------------------------
    val buttonPresets = listOf(
        Triple("btn_pill", "كبسولي ناعم Pill", ButtonStyle.PILL),
        Triple("btn_rounded", "منحني قياسي Rounded", ButtonStyle.ROUNDED),
        Triple("btn_neon", "نيون سيبراني متوهج", ButtonStyle.NEON),
        Triple("btn_glass", "زجاجي شفاف Frosted", ButtonStyle.GLASS),
        Triple("btn_gradient", "تدرج لوني براق", ButtonStyle.GRADIENT),
        Triple("btn_outlined", "حدود خارجية فقط Outlined", ButtonStyle.OUTLINED),
        Triple("btn_ghost", "شبه شفاف Ghost", ButtonStyle.GHOST),
        Triple("btn_filled", "ممتلئ صلب Filled", ButtonStyle.FILLED)
    )

    // ----------------------------------------------------
    // Expanded Navigation Presets
    // ----------------------------------------------------
    val navPresets = listOf(
        Triple("nav_floating", "شريط عائم حديث Floating", NavBarStyle.FLOATING),
        Triple("nav_pill", "كبسولة منحنية Pill", NavBarStyle.PILL),
        Triple("nav_glass", "زجاجي شفاف Glass", NavBarStyle.GLASS),
        Triple("nav_classic", "كلاسيكي ثابت Classic", NavBarStyle.CLASSIC),
        Triple("nav_minimal", "بسيط ومدمج Minimal", NavBarStyle.MINIMAL),
        Triple("nav_compact", "صغير للشاشات الضيقة", NavBarStyle.COMPACT),
        Triple("nav_large_icons", "أيقونات بارزة وسريعة", NavBarStyle.LARGE_ICONS),
        Triple("nav_icons_only", "أيقونات فقط بدون نصوص", NavBarStyle.ICONS_ONLY)
    )

    // ----------------------------------------------------
    // Expanded Input Bar Presets
    // ----------------------------------------------------
    val inputPresets = listOf(
        Triple("in_pill", "كبسولة كاملة Full Pill", InputBarStyle.PILL),
        Triple("in_rounded", "منحني كلاسيكي Rounded", InputBarStyle.ROUNDED),
        Triple("in_glass", "زجاجي شفاف Glass", InputBarStyle.GLASS),
        Triple("in_floating", "شريط عائم Floating Dock", InputBarStyle.FLOATING),
        Triple("in_minimal", "خط سفلي خفيف Minimal", InputBarStyle.MINIMAL),
        Triple("in_compact", "شريط مدمج Compact", InputBarStyle.COMPACT)
    )
}
