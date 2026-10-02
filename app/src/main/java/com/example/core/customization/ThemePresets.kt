package com.example.core.customization

object ThemePresets {

    // ----------------------------------------------------
    // 32+ Distinct Wallpaper Presets
    // ----------------------------------------------------
    val builtInWallpapers: List<WallpaperItem> = listOf(
        // Technology & Cyber
        WallpaperItem("slate_cyber", "Cyber Grid Matrix", "Technology", "#0B1120", "#0284C7", "#38BDF8", "GRID", true),
        WallpaperItem("tech_circuit", "Quantum Circuits", "Technology", "#030712", "#1E1B4B", "#6366F1", "CIRCUITS", true),
        WallpaperItem("dev_binary", "Binary Code Stream", "Technology", "#022C22", "#064E3B", "#10B981", "DOTS", true),

        // Dark & AMOLED
        WallpaperItem("amoled_pure", "Pure Pitch Black", "AMOLED", "#000000", "#000000", "#38BDF8", "SOLID", true),
        WallpaperItem("amoled_hex", "Obsidian Hexagons", "AMOLED", "#050505", "#18181B", "#A1A1AA", "HEXAGONS", true),
        WallpaperItem("midnight_fog", "Midnight Fog", "Dark", "#0F172A", "#1E293B", "#64748B", "LINEAR_GRADIENT", true),
        WallpaperItem("charcoal_minimal", "Deep Charcoal", "Dark", "#18181B", "#27272A", "#71717A", "RADIAL_GRADIENT", true),

        // Space & Cosmic
        WallpaperItem("space_nebula", "Orion Deep Nebula", "Space", "#0B051B", "#2E0854", "#A855F7", "STARS", true),
        WallpaperItem("cosmic_stardust", "Cosmic Stardust", "Space", "#020617", "#1E1B4B", "#E0E7FF", "STARS", true),
        WallpaperItem("supernova", "Golden Supernova", "Space", "#1C1917", "#451A03", "#F59E0B", "RADIAL_GRADIENT", true),

        // Abstract & Gradient
        WallpaperItem("aurora_borealis", "Nordic Aurora", "Gradient", "#022C22", "#0F766E", "#2DD4BF", "WAVES", true),
        WallpaperItem("sunset_bliss", "Miami Sunset", "Gradient", "#4A044E", "#831843", "#F43F5E", "LINEAR_GRADIENT", true),
        WallpaperItem("ocean_depths", "Pacific Abyssal", "Abstract", "#082F49", "#0C4A6E", "#0284C7", "WAVES", true),
        WallpaperItem("liquid_mesh", "Neon Liquid Mesh", "Abstract", "#311042", "#130924", "#C084FC", "MESH", true),
        WallpaperItem("prism_light", "Crystal Prism Flow", "Abstract", "#1E1B4B", "#312E81", "#818CF8", "MESH", true),

        // Gaming & Cyberpunk
        WallpaperItem("cyberpunk_night", "Night City 2077", "Gaming", "#180324", "#581C87", "#F43F5E", "GRID", true),
        WallpaperItem("gaming_rgb", "Viper RGB Strike", "Gaming", "#022C22", "#064E3B", "#22C55E", "CIRCUITS", true),
        WallpaperItem("synthwave_sun", "Retrowave Horizon", "Gaming", "#2E0854", "#701A75", "#F472B6", "GRID", true),

        // Nature & Organic
        WallpaperItem("emerald_forest", "Amazon Rainforest", "Nature", "#052E16", "#14532D", "#22C55E", "WAVES", true),
        WallpaperItem("mountain_mist", "Alpine Mountain Mist", "Nature", "#1E293B", "#334155", "#94A3B8", "LINEAR_GRADIENT", true),
        WallpaperItem("desert_dunes", "Sahara Golden Dunes", "Nature", "#451A03", "#78350F", "#F59E0B", "WAVES", true),
        WallpaperItem("ocean_breeze", "Turquoise Lagoon", "Nature", "#083344", "#155E75", "#06B6D4", "RADIAL_GRADIENT", true),

        // Luxury & Gold
        WallpaperItem("luxury_gold", "Royal Onyx & Gold", "Luxury", "#171717", "#262626", "#FBBF24", "HEXAGONS", true),
        WallpaperItem("velvet_royalty", "Imperial Velvet", "Luxury", "#3B0764", "#581C87", "#D8B4FE", "MESH", true),
        WallpaperItem("platinum_marble", "Titanium Marble", "Luxury", "#18181B", "#27272A", "#E4E4E7", "GRID", true),

        // Minimal & Clean
        WallpaperItem("minimal_dots", "Soft Geometric Dots", "Minimal", "#0F172A", "#1E293B", "#38BDF8", "DOTS", true),
        WallpaperItem("clean_paper", "Clean Slate Fabric", "Minimal", "#F8FAFC", "#F1F5F9", "#64748B", "DOTS", false),
        WallpaperItem("zen_sand", "Zen Stone White", "Minimal", "#FAFAF9", "#F5F5F4", "#78716C", "SOLID", false),

        // Colorful & Retro
        WallpaperItem("retro_cassette", "Vintage 80s Cassette", "Retro", "#3F1D38", "#532E52", "#E879F9", "GRID", true),
        WallpaperItem("sakura_petals", "Sakura Blossom Pink", "Colorful", "#4C0519", "#881337", "#FB7185", "WAVES", true),
        WallpaperItem("candy_pop", "Bubblegum Pop", "Colorful", "#500724", "#831843", "#F472B6", "RADIAL_GRADIENT", true),
        WallpaperItem("rainbow_burst", "Spectrum Aurora", "Colorful", "#172554", "#1E1B4B", "#60A5FA", "MESH", true),

        // Dynamic Video Wallpaper Simulation
        WallpaperItem("video_matrix_rain", "Quantum Code Stream (Live)", "Technology", "#022C22", "#064E3B", "#10B981", "CIRCUITS", true, isVideoLoop = true),
        WallpaperItem("video_nebula_drift", "Deep Space Nebula (Live)", "Space", "#0B051B", "#2E0854", "#A855F7", "STARS", true, isVideoLoop = true)
    )

    // ----------------------------------------------------
    // 32+ Complete, Deeply-Crafted Built-in Themes
    // ----------------------------------------------------
    val builtInThemes: List<ThemeConfig> = listOf(
        // 1. dev Modern (Default)
        ThemeConfig(
            id = "dev_default",
            name = "dev Cyber Modern",
            description = "The quintessential sleek dark UI for dev with glowing cyan accents.",
            category = "Modern",
            isDark = true,
            primaryColorHex = "#0284C7",
            secondaryColorHex = "#0F172A",
            backgroundColorHex = "#0B1120",
            surfaceColorHex = "#111827",
            surfaceVariantColorHex = "#1E293B",
            textColorHex = "#F8FAFC",
            textSecondaryHex = "#94A3B8",
            accentColorHex = "#38BDF8",
            cardColorHex = "#1E293B",
            cardRadiusDp = 16,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.ROUNDED,
                radiusDp = 18,
                incomingBgHex = "#1E293B",
                outgoingBgHex = "#0284C7",
                incomingTextHex = "#F8FAFC",
                outgoingTextHex = "#FFFFFF",
                timestampColorHex = "#94A3B8",
                hasTail = true,
                hasShadow = true
            ),
            navBarStyle = NavBarStyle.FLOATING,
            inputBarStyle = InputBarStyle.PILL,
            buttonStyle = ButtonStyle.PILL,
            iconStyle = IconStyle.ROUNDED,
            animationSpeed = AnimationSpeed.SMOOTH,
            decorationStyle = DecorationStyle.TECH_DOTS,
            typography = TypographyConfig(fontFamilyType = FontFamilyType.SANS_SERIF),
            effects = VisualEffectsConfig(glassmorphism = true, glowEnabled = true),
            wallpaperId = "slate_cyber"
        ),

        // 2. AMOLED True Black
        ThemeConfig(
            id = "amoled_black",
            name = "AMOLED Pitch Black",
            description = "100% black pixels for battery saving and striking contrast.",
            category = "AMOLED",
            isDark = true,
            primaryColorHex = "#38BDF8",
            secondaryColorHex = "#000000",
            backgroundColorHex = "#000000",
            surfaceColorHex = "#09090B",
            surfaceVariantColorHex = "#18181B",
            textColorHex = "#FFFFFF",
            textSecondaryHex = "#71717A",
            accentColorHex = "#38BDF8",
            cardColorHex = "#0D0D10",
            cardRadiusDp = 12,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.PILL,
                radiusDp = 20,
                borderWidthDp = 1,
                hasBorder = true,
                borderColorHex = "#27272A",
                incomingBgHex = "#09090B",
                outgoingBgHex = "#0284C7",
                incomingTextHex = "#FAFAFA",
                outgoingTextHex = "#FFFFFF"
            ),
            navBarStyle = NavBarStyle.MINIMAL,
            inputBarStyle = InputBarStyle.PILL,
            buttonStyle = ButtonStyle.ROUNDED,
            iconStyle = IconStyle.MINIMAL,
            effects = VisualEffectsConfig(glassmorphism = false, subtleShadows = false),
            wallpaperId = "amoled_pure"
        ),

        // 3. Cyberpunk Neon
        ThemeConfig(
            id = "cyberpunk_neon",
            name = "Cyberpunk 2077",
            description = "High-voltage neon pink and cyan against dystopian dark violet.",
            category = "Cyberpunk",
            isDark = true,
            primaryColorHex = "#F43F5E",
            secondaryColorHex = "#180324",
            backgroundColorHex = "#0E0217",
            surfaceColorHex = "#1B052E",
            surfaceVariantColorHex = "#2D0A4E",
            textColorHex = "#FFF1F2",
            textSecondaryHex = "#F472B6",
            accentColorHex = "#22D3EE",
            cardColorHex = "#24063E",
            cardRadiusDp = 8,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.SHARP,
                radiusDp = 6,
                borderWidthDp = 2,
                hasBorder = true,
                borderColorHex = "#F43F5E",
                incomingBgHex = "#1B052E",
                outgoingBgHex = "#E11D48",
                incomingTextHex = "#FFE4E6",
                outgoingTextHex = "#FFFFFF"
            ),
            navBarStyle = NavBarStyle.GLASS,
            inputBarStyle = InputBarStyle.ROUNDED,
            buttonStyle = ButtonStyle.NEON,
            iconStyle = IconStyle.NEON,
            animationSpeed = AnimationSpeed.DYNAMIC,
            decorationStyle = DecorationStyle.GLOW_FRAMES,
            effects = VisualEffectsConfig(glowEnabled = true, glassmorphism = true),
            wallpaperId = "cyberpunk_night"
        ),

        // 4. Matrix Hacker
        ThemeConfig(
            id = "matrix_code",
            name = "Matrix Green Code",
            description = "Terminal green glow with monospace developer aesthetics.",
            category = "Gaming",
            isDark = true,
            primaryColorHex = "#10B981",
            secondaryColorHex = "#022C22",
            backgroundColorHex = "#011611",
            surfaceColorHex = "#03231B",
            surfaceVariantColorHex = "#064E3B",
            textColorHex = "#D1FAE5",
            textSecondaryHex = "#6EE7B7",
            accentColorHex = "#34D399",
            cardColorHex = "#042F24",
            cardRadiusDp = 6,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.SHARP,
                radiusDp = 4,
                hasBorder = true,
                borderWidthDp = 1,
                borderColorHex = "#10B981",
                incomingBgHex = "#022C22",
                outgoingBgHex = "#059669",
                incomingTextHex = "#A7F3D0",
                outgoingTextHex = "#FFFFFF"
            ),
            navBarStyle = NavBarStyle.CLASSIC,
            inputBarStyle = InputBarStyle.MINIMAL,
            buttonStyle = ButtonStyle.OUTLINED,
            iconStyle = IconStyle.SHARP,
            typography = TypographyConfig(fontFamilyType = FontFamilyType.MONOSPACE, isMonospace = true),
            wallpaperId = "tech_circuit"
        ),

        // 5. Gaming Esports RGB
        ThemeConfig(
            id = "gaming_rgb",
            name = "Gaming Apex RGB",
            description = "Electric lime and purple battle station vibe.",
            category = "Gaming",
            isDark = true,
            primaryColorHex = "#8B5CF6",
            secondaryColorHex = "#090A0F",
            backgroundColorHex = "#06070B",
            surfaceColorHex = "#0F111A",
            surfaceVariantColorHex = "#1E2235",
            textColorHex = "#F3F4F6",
            textSecondaryHex = "#A78BFA",
            accentColorHex = "#10B981",
            cardColorHex = "#141724",
            cardRadiusDp = 14,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.MODERN_LEAF,
                radiusDp = 16,
                incomingBgHex = "#1E2235",
                outgoingBgHex = "#7C3AED",
                incomingTextHex = "#EDE9FE",
                outgoingTextHex = "#FFFFFF"
            ),
            navBarStyle = NavBarStyle.FLOATING,
            inputBarStyle = InputBarStyle.FLOATING,
            buttonStyle = ButtonStyle.GRADIENT,
            iconStyle = IconStyle.GAMING,
            animationSpeed = AnimationSpeed.BOUNCY,
            wallpaperId = "gaming_rgb"
        ),

        // 6. Liquid Glass
        ThemeConfig(
            id = "liquid_glass",
            name = "Liquid Glassmorphic",
            description = "Frosted glass translucency with soft reflections and ambient blur.",
            category = "Glass",
            isDark = true,
            primaryColorHex = "#60A5FA",
            secondaryColorHex = "#1E1B4B",
            backgroundColorHex = "#0F172A",
            surfaceColorHex = "#1E293B",
            surfaceVariantColorHex = "#334155",
            textColorHex = "#F8FAFC",
            textSecondaryHex = "#CBD5E1",
            accentColorHex = "#38BDF8",
            cardColorHex = "#1E293B",
            cardRadiusDp = 20,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.ROUNDED,
                radiusDp = 22,
                transparency = 0.85f,
                incomingBgHex = "#1E293B",
                outgoingBgHex = "#2563EB",
                incomingTextHex = "#F8FAFC",
                outgoingTextHex = "#FFFFFF"
            ),
            navBarStyle = NavBarStyle.GLASS,
            inputBarStyle = InputBarStyle.GLASS,
            buttonStyle = ButtonStyle.GLASS,
            iconStyle = IconStyle.GLASS,
            effects = VisualEffectsConfig(glassmorphism = true, blurEnabled = true),
            wallpaperId = "prism_light"
        ),

        // 7. Clean Minimal Light
        ThemeConfig(
            id = "minimal_light",
            name = "Clean Minimal White",
            description = "Distraction-free pure white interface with crisp typography.",
            category = "Light",
            isDark = false,
            primaryColorHex = "#0F172A",
            secondaryColorHex = "#F8FAFC",
            backgroundColorHex = "#FFFFFF",
            surfaceColorHex = "#F8FAFC",
            surfaceVariantColorHex = "#F1F5F9",
            textColorHex = "#0F172A",
            textSecondaryHex = "#64748B",
            accentColorHex = "#2563EB",
            cardColorHex = "#FFFFFF",
            cardRadiusDp = 14,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.ROUNDED,
                radiusDp = 16,
                incomingBgHex = "#F1F5F9",
                outgoingBgHex = "#0F172A",
                incomingTextHex = "#0F172A",
                outgoingTextHex = "#FFFFFF",
                timestampColorHex = "#64748B"
            ),
            navBarStyle = NavBarStyle.CLASSIC,
            inputBarStyle = InputBarStyle.ROUNDED,
            buttonStyle = ButtonStyle.ROUNDED,
            iconStyle = IconStyle.MINIMAL,
            wallpaperId = "clean_paper"
        ),

        // 8. Luxury Gold & Velvet
        ThemeConfig(
            id = "luxury_gold",
            name = "Imperial Onyx & Gold",
            description = "Prestige dark slate with opulent gold foil highlights.",
            category = "Luxury",
            isDark = true,
            primaryColorHex = "#F59E0B",
            secondaryColorHex = "#18181B",
            backgroundColorHex = "#09090B",
            surfaceColorHex = "#121215",
            surfaceVariantColorHex = "#27272A",
            textColorHex = "#FEF3C7",
            textSecondaryHex = "#FBBF24",
            accentColorHex = "#FCD34D",
            cardColorHex = "#18181B",
            cardRadiusDp = 18,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.ROUNDED,
                radiusDp = 18,
                hasBorder = true,
                borderWidthDp = 1,
                borderColorHex = "#D97706",
                incomingBgHex = "#18181B",
                outgoingBgHex = "#D97706",
                incomingTextHex = "#FEF3C7",
                outgoingTextHex = "#000000"
            ),
            navBarStyle = NavBarStyle.PILL,
            inputBarStyle = InputBarStyle.PILL,
            buttonStyle = ButtonStyle.PILL,
            iconStyle = IconStyle.OUTLINE,
            decorationStyle = DecorationStyle.STARS,
            wallpaperId = "luxury_gold"
        ),

        // 9. Nature Emerald
        ThemeConfig(
            id = "nature_emerald",
            name = "Amazon Emerald",
            description = "Deep soothing rainforest tones for mindful messaging.",
            category = "Nature",
            isDark = true,
            primaryColorHex = "#10B981",
            secondaryColorHex = "#064E3B",
            backgroundColorHex = "#022C22",
            surfaceColorHex = "#064E3B",
            surfaceVariantColorHex = "#047857",
            textColorHex = "#ECFDF5",
            textSecondaryHex = "#A7F3D0",
            accentColorHex = "#34D399",
            cardColorHex = "#065F46",
            cardRadiusDp = 18,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.CHAT_TAIL,
                radiusDp = 16,
                incomingBgHex = "#064E3B",
                outgoingBgHex = "#059669",
                incomingTextHex = "#ECFDF5",
                outgoingTextHex = "#FFFFFF"
            ),
            navBarStyle = NavBarStyle.FLOATING,
            inputBarStyle = InputBarStyle.PILL,
            buttonStyle = ButtonStyle.PILL,
            iconStyle = IconStyle.ROUNDED,
            decorationStyle = DecorationStyle.FLOWERS,
            wallpaperId = "emerald_forest"
        ),

        // 10. Deep Space Cosmic
        ThemeConfig(
            id = "space_cosmic",
            name = "Cosmic Galaxy Nebula",
            description = "Deep galactic indigo, stardust particles, and violet energy.",
            category = "Space",
            isDark = true,
            primaryColorHex = "#A855F7",
            secondaryColorHex = "#1E1B4B",
            backgroundColorHex = "#0B051B",
            surfaceColorHex = "#170B3B",
            surfaceVariantColorHex = "#2E1065",
            textColorHex = "#FAF5FF",
            textSecondaryHex = "#C084FC",
            accentColorHex = "#E879F9",
            cardColorHex = "#1E0E4A",
            cardRadiusDp = 20,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.ROUNDED,
                radiusDp = 20,
                incomingBgHex = "#1E0E4A",
                outgoingBgHex = "#9333EA",
                incomingTextHex = "#FAF5FF",
                outgoingTextHex = "#FFFFFF"
            ),
            navBarStyle = NavBarStyle.GLASS,
            inputBarStyle = InputBarStyle.GLASS,
            buttonStyle = ButtonStyle.GRADIENT,
            iconStyle = IconStyle.GLASS,
            decorationStyle = DecorationStyle.STARS,
            wallpaperId = "space_nebula"
        ),

        // 11. Pacific Ocean
        ThemeConfig(
            id = "pacific_ocean",
            name = "Pacific Deep Blue",
            description = "Cool deep oceanic blues inspired by midnight maritime depths.",
            category = "Ocean",
            isDark = true,
            primaryColorHex = "#0284C7",
            secondaryColorHex = "#082F49",
            backgroundColorHex = "#031926",
            surfaceColorHex = "#082F49",
            surfaceVariantColorHex = "#0C4A6E",
            textColorHex = "#F0F9FF",
            textSecondaryHex = "#7DD3FC",
            accentColorHex = "#38BDF8",
            cardColorHex = "#0A3B5C",
            cardRadiusDp = 16,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.ROUNDED,
                radiusDp = 18,
                incomingBgHex = "#0C4A6E",
                outgoingBgHex = "#0284C7",
                incomingTextHex = "#F0F9FF",
                outgoingTextHex = "#FFFFFF"
            ),
            navBarStyle = NavBarStyle.FLOATING,
            inputBarStyle = InputBarStyle.PILL,
            buttonStyle = ButtonStyle.PILL,
            iconStyle = IconStyle.FILLED,
            decorationStyle = DecorationStyle.ABSTRACT,
            wallpaperId = "ocean_depths"
        ),

        // 12. Miami Sunset
        ThemeConfig(
            id = "sunset_miami",
            name = "Miami Sunset Glow",
            description = "Warm magenta, glowing crimson, and radiant golden hour hues.",
            category = "Sunset",
            isDark = true,
            primaryColorHex = "#F43F5E",
            secondaryColorHex = "#4A044E",
            backgroundColorHex = "#2E0332",
            surfaceColorHex = "#4A044E",
            surfaceVariantColorHex = "#701A75",
            textColorHex = "#FFF1F2",
            textSecondaryHex = "#FDA4AF",
            accentColorHex = "#F59E0B",
            cardColorHex = "#5E0764",
            cardRadiusDp = 16,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.PILL,
                radiusDp = 20,
                incomingBgHex = "#4A044E",
                outgoingBgHex = "#E11D48",
                incomingTextHex = "#FFF1F2",
                outgoingTextHex = "#FFFFFF"
            ),
            navBarStyle = NavBarStyle.PILL,
            inputBarStyle = InputBarStyle.PILL,
            buttonStyle = ButtonStyle.GRADIENT,
            iconStyle = IconStyle.GRADIENT,
            decorationStyle = DecorationStyle.HEARTS,
            wallpaperId = "sunset_bliss"
        ),

        // 13. Nordic Forest
        ThemeConfig(
            id = "nordic_forest",
            name = "Nordic Pine Forest",
            description = "Earthy deep forest green and natural timber moss.",
            category = "Forest",
            isDark = true,
            primaryColorHex = "#059669",
            secondaryColorHex = "#14532D",
            backgroundColorHex = "#0A2016",
            surfaceColorHex = "#113825",
            surfaceVariantColorHex = "#1A4E35",
            textColorHex = "#F0FDF4",
            textSecondaryHex = "#86EFAC",
            accentColorHex = "#34D399",
            cardColorHex = "#14442E",
            cardRadiusDp = 14,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.ROUNDED,
                radiusDp = 16,
                incomingBgHex = "#1A4E35",
                outgoingBgHex = "#059669",
                incomingTextHex = "#F0FDF4",
                outgoingTextHex = "#FFFFFF"
            ),
            navBarStyle = NavBarStyle.CLASSIC,
            inputBarStyle = InputBarStyle.ROUNDED,
            buttonStyle = ButtonStyle.ROUNDED,
            iconStyle = IconStyle.ROUNDED,
            wallpaperId = "emerald_forest"
        ),

        // 14. Royal Purple
        ThemeConfig(
            id = "royal_purple",
            name = "Royal Amethyst",
            description = "Regal purple with amethyst reflections and velvet surfaces.",
            category = "Purple",
            isDark = true,
            primaryColorHex = "#7C3AED",
            secondaryColorHex = "#2E1065",
            backgroundColorHex = "#120529",
            surfaceColorHex = "#240B52",
            surfaceVariantColorHex = "#3B1482",
            textColorHex = "#FAF5FF",
            textSecondaryHex = "#D8B4FE",
            accentColorHex = "#C084FC",
            cardColorHex = "#2B0E61",
            cardRadiusDp = 18,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.ROUNDED,
                radiusDp = 18,
                incomingBgHex = "#3B1482",
                outgoingBgHex = "#7C3AED",
                incomingTextHex = "#FAF5FF",
                outgoingTextHex = "#FFFFFF"
            ),
            navBarStyle = NavBarStyle.FLOATING,
            inputBarStyle = InputBarStyle.PILL,
            buttonStyle = ButtonStyle.PILL,
            iconStyle = IconStyle.GLASS,
            decorationStyle = DecorationStyle.STARS,
            wallpaperId = "velvet_royalty"
        ),

        // 15. Sapphire Blue
        ThemeConfig(
            id = "sapphire_blue",
            name = "Sapphire Blue Gem",
            description = "Vivid royal cobalt and electric sapphire brilliance.",
            category = "Blue",
            isDark = true,
            primaryColorHex = "#2563EB",
            secondaryColorHex = "#172554",
            backgroundColorHex = "#0B132B",
            surfaceColorHex = "#131F42",
            surfaceVariantColorHex = "#1E2F5E",
            textColorHex = "#EFF6FF",
            textSecondaryHex = "#93C5FD",
            accentColorHex = "#60A5FA",
            cardColorHex = "#172754",
            cardRadiusDp = 16,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.CHAT_TAIL,
                radiusDp = 16,
                incomingBgHex = "#1E2F5E",
                outgoingBgHex = "#2563EB",
                incomingTextHex = "#EFF6FF",
                outgoingTextHex = "#FFFFFF"
            ),
            navBarStyle = NavBarStyle.FLOATING,
            inputBarStyle = InputBarStyle.PILL,
            buttonStyle = ButtonStyle.PILL,
            iconStyle = IconStyle.FILLED,
            wallpaperId = "ocean_depths"
        ),

        // 16. Crimson Ruby
        ThemeConfig(
            id = "crimson_ruby",
            name = "Crimson Ruby Red",
            description = "Bold, passionate ruby red with dark garnet shadows.",
            category = "Red",
            isDark = true,
            primaryColorHex = "#E11D48",
            secondaryColorHex = "#4C0519",
            backgroundColorHex = "#1F020A",
            surfaceColorHex = "#350412",
            surfaceVariantColorHex = "#54081E",
            textColorHex = "#FFF1F2",
            textSecondaryHex = "#FDA4AF",
            accentColorHex = "#FB7185",
            cardColorHex = "#3F0516",
            cardRadiusDp = 14,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.ROUNDED,
                radiusDp = 16,
                incomingBgHex = "#54081E",
                outgoingBgHex = "#E11D48",
                incomingTextHex = "#FFF1F2",
                outgoingTextHex = "#FFFFFF"
            ),
            navBarStyle = NavBarStyle.FLOATING,
            inputBarStyle = InputBarStyle.ROUNDED,
            buttonStyle = ButtonStyle.ROUNDED,
            iconStyle = IconStyle.FILLED,
            wallpaperId = "candy_pop"
        ),

        // 17. Sakura Pink
        ThemeConfig(
            id = "sakura_pink",
            name = "Sakura Cherry Blossom",
            description = "Delicate Japanese cherry blossom aesthetic with soft pastel rose.",
            category = "Pink",
            isDark = false,
            primaryColorHex = "#E11D48",
            secondaryColorHex = "#FFE4E6",
            backgroundColorHex = "#FFF1F2",
            surfaceColorHex = "#FFFFFF",
            surfaceVariantColorHex = "#FFE4E6",
            textColorHex = "#881337",
            textSecondaryHex = "#BE185D",
            accentColorHex = "#F43F5E",
            cardColorHex = "#FFFFFF",
            cardRadiusDp = 20,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.PILL,
                radiusDp = 22,
                incomingBgHex = "#FFE4E6",
                outgoingBgHex = "#F43F5E",
                incomingTextHex = "#881337",
                outgoingTextHex = "#FFFFFF"
            ),
            navBarStyle = NavBarStyle.PILL,
            inputBarStyle = InputBarStyle.PILL,
            buttonStyle = ButtonStyle.PILL,
            iconStyle = IconStyle.ROUNDED,
            decorationStyle = DecorationStyle.FLOWERS,
            wallpaperId = "sakura_petals"
        ),

        // 18. Vintage Sepia Retro
        ThemeConfig(
            id = "retro_sepia",
            name = "Vintage 70s Sepia",
            description = "Nostalgic warm typewriter paper, brass accents, and serif font.",
            category = "Retro",
            isDark = false,
            primaryColorHex = "#78350F",
            secondaryColorHex = "#FEF3C7",
            backgroundColorHex = "#FDF6EC",
            surfaceColorHex = "#F7EBD9",
            surfaceVariantColorHex = "#EFE0C9",
            textColorHex = "#451A03",
            textSecondaryHex = "#92400E",
            accentColorHex = "#D97706",
            cardColorHex = "#F9EFE1",
            cardRadiusDp = 10,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.ROUNDED,
                radiusDp = 12,
                incomingBgHex = "#EFE0C9",
                outgoingBgHex = "#92400E",
                incomingTextHex = "#451A03",
                outgoingTextHex = "#FEF3C7"
            ),
            navBarStyle = NavBarStyle.CLASSIC,
            inputBarStyle = InputBarStyle.ROUNDED,
            buttonStyle = ButtonStyle.OUTLINED,
            iconStyle = IconStyle.CLASSIC,
            typography = TypographyConfig(fontFamilyType = FontFamilyType.SERIF),
            wallpaperId = "desert_dunes"
        ),

        // 19. Aurora Gradient
        ThemeConfig(
            id = "aurora_gradient",
            name = "Aurora Northern Lights",
            description = "Mesmerizing emerald to cyan gradients shimmering on dark ice.",
            category = "Gradient",
            isDark = true,
            primaryColorHex = "#2DD4BF",
            secondaryColorHex = "#042F2E",
            backgroundColorHex = "#021A1A",
            surfaceColorHex = "#042F2E",
            surfaceVariantColorHex = "#0D4D47",
            textColorHex = "#F0FDFA",
            textSecondaryHex = "#5EEAD4",
            accentColorHex = "#38BDF8",
            cardColorHex = "#073B37",
            cardRadiusDp = 18,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.MODERN_LEAF,
                radiusDp = 18,
                isGradient = true,
                incomingBgHex = "#0D4D47",
                outgoingBgHex = "#0D9488",
                incomingTextHex = "#F0FDFA",
                outgoingTextHex = "#FFFFFF"
            ),
            navBarStyle = NavBarStyle.GLASS,
            inputBarStyle = InputBarStyle.GLASS,
            buttonStyle = ButtonStyle.GRADIENT,
            iconStyle = IconStyle.GRADIENT,
            decorationStyle = DecorationStyle.ABSTRACT,
            wallpaperId = "aurora_borealis"
        ),

        // 20. Crystal Prism
        ThemeConfig(
            id = "crystal_prism",
            name = "Crystal Prism Ice",
            description = "Refractive crystalline blue with frosted transparent panels.",
            category = "Crystal",
            isDark = true,
            primaryColorHex = "#38BDF8",
            secondaryColorHex = "#0C4A6E",
            backgroundColorHex = "#041726",
            surfaceColorHex = "#0A2840",
            surfaceVariantColorHex = "#133D60",
            textColorHex = "#F0F9FF",
            textSecondaryHex = "#BAE6FD",
            accentColorHex = "#7DD3FC",
            cardColorHex = "#0E3452",
            cardRadiusDp = 22,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.PILL,
                radiusDp = 22,
                hasBorder = true,
                borderWidthDp = 1,
                borderColorHex = "#38BDF8",
                incomingBgHex = "#133D60",
                outgoingBgHex = "#0284C7",
                incomingTextHex = "#F0F9FF",
                outgoingTextHex = "#FFFFFF"
            ),
            navBarStyle = NavBarStyle.GLASS,
            inputBarStyle = InputBarStyle.GLASS,
            buttonStyle = ButtonStyle.GLASS,
            iconStyle = IconStyle.GLASS,
            effects = VisualEffectsConfig(glassmorphism = true, glowEnabled = true),
            wallpaperId = "prism_light"
        ),

        // 21. Monokai Dev Code
        ThemeConfig(
            id = "monokai_dev",
            name = "Monokai Pro Developer",
            description = "Beloved code editor palette: dark charcoal with yellow, green and pink syntax highlights.",
            category = "Modern",
            isDark = true,
            primaryColorHex = "#E6DB74",
            secondaryColorHex = "#272822",
            backgroundColorHex = "#1E1F1C",
            surfaceColorHex = "#272822",
            surfaceVariantColorHex = "#3E3D32",
            textColorHex = "#F8F8F2",
            textSecondaryHex = "#A6E22E",
            accentColorHex = "#F92672",
            cardColorHex = "#2C2D26",
            cardRadiusDp = 10,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.SHARP,
                radiusDp = 8,
                incomingBgHex = "#3E3D32",
                outgoingBgHex = "#66D9EF",
                incomingTextHex = "#F8F8F2",
                outgoingTextHex = "#1E1F1C"
            ),
            navBarStyle = NavBarStyle.MINIMAL,
            inputBarStyle = InputBarStyle.MINIMAL,
            buttonStyle = ButtonStyle.PILL,
            iconStyle = IconStyle.MINIMAL,
            typography = TypographyConfig(fontFamilyType = FontFamilyType.TECH_CODE),
            wallpaperId = "tech_circuit"
        ),

        // 22. Matcha Green Minimal
        ThemeConfig(
            id = "matcha_green",
            name = "Matcha Green Tea",
            description = "Earthy soothing Japanese green tea palette with natural calmness.",
            category = "Green",
            isDark = false,
            primaryColorHex = "#4D7C0F",
            secondaryColorHex = "#ECFCCB",
            backgroundColorHex = "#F7FEE7",
            surfaceColorHex = "#FFFFFF",
            surfaceVariantColorHex = "#ECFCCB",
            textColorHex = "#365314",
            textSecondaryHex = "#65A30D",
            accentColorHex = "#84CC16",
            cardColorHex = "#FFFFFF",
            cardRadiusDp = 16,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.ROUNDED,
                radiusDp = 18,
                incomingBgHex = "#ECFCCB",
                outgoingBgHex = "#65A30D",
                incomingTextHex = "#365314",
                outgoingTextHex = "#FFFFFF"
            ),
            navBarStyle = NavBarStyle.PILL,
            inputBarStyle = InputBarStyle.PILL,
            buttonStyle = ButtonStyle.PILL,
            iconStyle = IconStyle.ROUNDED,
            wallpaperId = "clean_paper"
        ),

        // 23. Synthwave 80s Sunset
        ThemeConfig(
            id = "synthwave_80s",
            name = "Synthwave 1984",
            description = "Retro neon laser grids, outrun hot pink, and digital violet dusk.",
            category = "Retro",
            isDark = true,
            primaryColorHex = "#F472B6",
            secondaryColorHex = "#2E0854",
            backgroundColorHex = "#17022B",
            surfaceColorHex = "#2E0854",
            surfaceVariantColorHex = "#4A044E",
            textColorHex = "#FDF2F8",
            textSecondaryHex = "#F472B6",
            accentColorHex = "#38BDF8",
            cardColorHex = "#3B086C",
            cardRadiusDp = 12,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.PILL,
                radiusDp = 20,
                hasBorder = true,
                borderColorHex = "#F472B6",
                borderWidthDp = 1,
                incomingBgHex = "#3B086C",
                outgoingBgHex = "#DB2777",
                incomingTextHex = "#FDF2F8",
                outgoingTextHex = "#FFFFFF"
            ),
            navBarStyle = NavBarStyle.GLASS,
            inputBarStyle = InputBarStyle.ROUNDED,
            buttonStyle = ButtonStyle.NEON,
            iconStyle = IconStyle.NEON,
            animationSpeed = AnimationSpeed.DYNAMIC,
            decorationStyle = DecorationStyle.GLOW_FRAMES,
            wallpaperId = "synthwave_sun"
        ),

        // 24. Solar Flare Orange
        ThemeConfig(
            id = "solar_flare",
            name = "Solar Flare Radiant",
            description = "High intensity cosmic amber, lava orange, and radiant heat.",
            category = "Sunset",
            isDark = true,
            primaryColorHex = "#F97316",
            secondaryColorHex = "#431407",
            backgroundColorHex = "#1C0803",
            surfaceColorHex = "#330F05",
            surfaceVariantColorHex = "#541C0C",
            textColorHex = "#FFF7ED",
            textSecondaryHex = "#FDBA74",
            accentColorHex = "#FB923C",
            cardColorHex = "#3D1307",
            cardRadiusDp = 16,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.ROUNDED,
                radiusDp = 16,
                incomingBgHex = "#541C0C",
                outgoingBgHex = "#EA580C",
                incomingTextHex = "#FFF7ED",
                outgoingTextHex = "#FFFFFF"
            ),
            navBarStyle = NavBarStyle.FLOATING,
            inputBarStyle = InputBarStyle.PILL,
            buttonStyle = ButtonStyle.PILL,
            iconStyle = IconStyle.ROUNDED,
            wallpaperId = "supernova"
        ),

        // 25. Titanium Dark
        ThemeConfig(
            id = "titanium_dark",
            name = "Titanium Dark Shield",
            description = "Industrial aerospace titanium and gunmetal brushed finish.",
            category = "Dark",
            isDark = true,
            primaryColorHex = "#94A3B8",
            secondaryColorHex = "#1E293B",
            backgroundColorHex = "#0F172A",
            surfaceColorHex = "#1E293B",
            surfaceVariantColorHex = "#334155",
            textColorHex = "#F8FAFC",
            textSecondaryHex = "#94A3B8",
            accentColorHex = "#CBD5E1",
            cardColorHex = "#1E293B",
            cardRadiusDp = 14,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.SHARP,
                radiusDp = 10,
                incomingBgHex = "#334155",
                outgoingBgHex = "#475569",
                incomingTextHex = "#F8FAFC",
                outgoingTextHex = "#FFFFFF"
            ),
            navBarStyle = NavBarStyle.CLASSIC,
            inputBarStyle = InputBarStyle.ROUNDED,
            buttonStyle = ButtonStyle.ROUNDED,
            iconStyle = IconStyle.SHARP,
            wallpaperId = "midnight_fog"
        ),

        // 26. Arctic Glacier Light
        ThemeConfig(
            id = "arctic_glacier",
            name = "Arctic Glacier Mist",
            description = "Crisp pale glacial blues with ultra-clean frosty clarity.",
            category = "Light",
            isDark = false,
            primaryColorHex = "#0284C7",
            secondaryColorHex = "#E0F2FE",
            backgroundColorHex = "#F0F9FF",
            surfaceColorHex = "#FFFFFF",
            surfaceVariantColorHex = "#E0F2FE",
            textColorHex = "#0C4A6E",
            textSecondaryHex = "#0369A1",
            accentColorHex = "#38BDF8",
            cardColorHex = "#FFFFFF",
            cardRadiusDp = 18,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.PILL,
                radiusDp = 20,
                incomingBgHex = "#E0F2FE",
                outgoingBgHex = "#0284C7",
                incomingTextHex = "#0C4A6E",
                outgoingTextHex = "#FFFFFF"
            ),
            navBarStyle = NavBarStyle.PILL,
            inputBarStyle = InputBarStyle.PILL,
            buttonStyle = ButtonStyle.PILL,
            iconStyle = IconStyle.ROUNDED,
            wallpaperId = "clean_paper"
        ),

        // 27. Tokyo Midnight Neon
        ThemeConfig(
            id = "tokyo_midnight",
            name = "Tokyo Midnight Drift",
            description = "Midnight Shinjuku electric cyan and magenta signs in the rain.",
            category = "Cyberpunk",
            isDark = true,
            primaryColorHex = "#06B6D4",
            secondaryColorHex = "#111827",
            backgroundColorHex = "#030712",
            surfaceColorHex = "#111827",
            surfaceVariantColorHex = "#1F2937",
            textColorHex = "#F9FAFB",
            textSecondaryHex = "#67E8F9",
            accentColorHex = "#EC4899",
            cardColorHex = "#161F33",
            cardRadiusDp = 16,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.MODERN_LEAF,
                radiusDp = 18,
                hasBorder = true,
                borderColorHex = "#06B6D4",
                incomingBgHex = "#1F2937",
                outgoingBgHex = "#0891B2",
                incomingTextHex = "#F9FAFB",
                outgoingTextHex = "#FFFFFF"
            ),
            navBarStyle = NavBarStyle.FLOATING,
            inputBarStyle = InputBarStyle.PILL,
            buttonStyle = ButtonStyle.NEON,
            iconStyle = IconStyle.NEON,
            animationSpeed = AnimationSpeed.DYNAMIC,
            decorationStyle = DecorationStyle.TECH_DOTS,
            wallpaperId = "slate_cyber"
        ),

        // 28. Golden Velvet
        ThemeConfig(
            id = "golden_velvet",
            name = "Golden Champagne",
            description = "Warm champagne sparkle with warm beige and golden sand.",
            category = "Gold",
            isDark = false,
            primaryColorHex = "#B45309",
            secondaryColorHex = "#FEF3C7",
            backgroundColorHex = "#FFFBEB",
            surfaceColorHex = "#FFFFFF",
            surfaceVariantColorHex = "#FEF3C7",
            textColorHex = "#78350F",
            textSecondaryHex = "#B45309",
            accentColorHex = "#F59E0B",
            cardColorHex = "#FFFFFF",
            cardRadiusDp = 18,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.ROUNDED,
                radiusDp = 18,
                incomingBgHex = "#FEF3C7",
                outgoingBgHex = "#D97706",
                incomingTextHex = "#78350F",
                outgoingTextHex = "#FFFFFF"
            ),
            navBarStyle = NavBarStyle.PILL,
            inputBarStyle = InputBarStyle.PILL,
            buttonStyle = ButtonStyle.PILL,
            iconStyle = IconStyle.OUTLINE,
            decorationStyle = DecorationStyle.STARS,
            wallpaperId = "clean_paper"
        ),

        // 29. Lavender Dream
        ThemeConfig(
            id = "lavender_dream",
            name = "Lavender Dreamscape",
            description = "Ethereal pastel lavender and lilac calming cloud aesthetic.",
            category = "Purple",
            isDark = false,
            primaryColorHex = "#8B5CF6",
            secondaryColorHex = "#F3E8FF",
            backgroundColorHex = "#FAF5FF",
            surfaceColorHex = "#FFFFFF",
            surfaceVariantColorHex = "#F3E8FF",
            textColorHex = "#581C87",
            textSecondaryHex = "#7E22CE",
            accentColorHex = "#A855F7",
            cardColorHex = "#FFFFFF",
            cardRadiusDp = 20,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.PILL,
                radiusDp = 22,
                incomingBgHex = "#F3E8FF",
                outgoingBgHex = "#8B5CF6",
                incomingTextHex = "#581C87",
                outgoingTextHex = "#FFFFFF"
            ),
            navBarStyle = NavBarStyle.PILL,
            inputBarStyle = InputBarStyle.PILL,
            buttonStyle = ButtonStyle.PILL,
            iconStyle = IconStyle.ROUNDED,
            decorationStyle = DecorationStyle.FLOWERS,
            wallpaperId = "clean_paper"
        ),

        // 30. Abstract Nebula
        ThemeConfig(
            id = "abstract_nebula",
            name = "Prismatic Nebula Fluid",
            description = "Vibrant multi-gradient fluid swirls across dark space canvas.",
            category = "Abstract",
            isDark = true,
            primaryColorHex = "#8B5CF6",
            secondaryColorHex = "#1E1B4B",
            backgroundColorHex = "#0C0721",
            surfaceColorHex = "#180F3B",
            surfaceVariantColorHex = "#2B1B61",
            textColorHex = "#F5F3FF",
            textSecondaryHex = "#C4B5FD",
            accentColorHex = "#F43F5E",
            cardColorHex = "#1D1245",
            cardRadiusDp = 18,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.ROUNDED,
                radiusDp = 18,
                isGradient = true,
                incomingBgHex = "#2B1B61",
                outgoingBgHex = "#7C3AED",
                incomingTextHex = "#F5F3FF",
                outgoingTextHex = "#FFFFFF"
            ),
            navBarStyle = NavBarStyle.GLASS,
            inputBarStyle = InputBarStyle.GLASS,
            buttonStyle = ButtonStyle.GRADIENT,
            iconStyle = IconStyle.GRADIENT,
            decorationStyle = DecorationStyle.ABSTRACT,
            wallpaperId = "liquid_mesh"
        ),

        // 31. Brutalist Concrete
        ThemeConfig(
            id = "brutalist_concrete",
            name = "Brutalist Monochrome",
            description = "High-contrast architectural brutalism with thick geometric borders.",
            category = "Minimal",
            isDark = false,
            primaryColorHex = "#000000",
            secondaryColorHex = "#E5E5E5",
            backgroundColorHex = "#EEEEEE",
            surfaceColorHex = "#FFFFFF",
            surfaceVariantColorHex = "#D4D4D4",
            textColorHex = "#000000",
            textSecondaryHex = "#525252",
            accentColorHex = "#000000",
            cardColorHex = "#FFFFFF",
            cardRadiusDp = 0,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.SHARP,
                radiusDp = 0,
                borderWidthDp = 2,
                hasBorder = true,
                borderColorHex = "#000000",
                incomingBgHex = "#E5E5E5",
                outgoingBgHex = "#000000",
                incomingTextHex = "#000000",
                outgoingTextHex = "#FFFFFF"
            ),
            navBarStyle = NavBarStyle.CLASSIC,
            inputBarStyle = InputBarStyle.MINIMAL,
            buttonStyle = ButtonStyle.OUTLINED,
            iconStyle = IconStyle.SHARP,
            wallpaperId = "zen_sand"
        ),

        // 32. Cyber Void (Deep Violet)
        ThemeConfig(
            id = "cyber_void",
            name = "Cyber Void Ultramarine",
            description = "Electric ultramarine blue fused with deep ultraviolet void.",
            category = "Modern",
            isDark = true,
            primaryColorHex = "#3B82F6",
            secondaryColorHex = "#1E1B4B",
            backgroundColorHex = "#070719",
            surfaceColorHex = "#0E102E",
            surfaceVariantColorHex = "#1A1D4D",
            textColorHex = "#EEF2FF",
            textSecondaryHex = "#A5B4FC",
            accentColorHex = "#6366F1",
            cardColorHex = "#121538",
            cardRadiusDp = 16,
            bubbleConfig = BubbleConfig(
                shape = BubbleShape.ROUNDED,
                radiusDp = 18,
                incomingBgHex = "#1A1D4D",
                outgoingBgHex = "#2563EB",
                incomingTextHex = "#EEF2FF",
                outgoingTextHex = "#FFFFFF"
            ),
            navBarStyle = NavBarStyle.FLOATING,
            inputBarStyle = InputBarStyle.PILL,
            buttonStyle = ButtonStyle.PILL,
            iconStyle = IconStyle.ROUNDED,
            decorationStyle = DecorationStyle.TECH_DOTS,
            wallpaperId = "cosmic_stardust"
        )
    )

    // Store sample items for the Online Theme Store
    val storeItems: List<StoreThemeItem> = builtInThemes.take(16).mapIndexed { index, theme ->
        StoreThemeItem(
            id = "store_${theme.id}",
            title = theme.name,
            author = if (index % 2 == 0) "dev Studio" else "Community Master",
            category = theme.category,
            downloadsCount = "${(12 + index * 4)}.${(index * 7) % 9}K",
            rating = (4.7f + (index % 4) * 0.1f).coerceAtMost(5.0f),
            isFeatured = index < 4,
            isTrending = index in 2..7,
            isNew = index > 10,
            previewPrimaryHex = theme.primaryColorHex,
            previewBgHex = theme.backgroundColorHex,
            previewAccentHex = theme.accentColorHex,
            themeConfig = theme
        )
    }
}
