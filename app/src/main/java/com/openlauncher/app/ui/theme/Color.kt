package com.openlauncher.app.ui.theme

import androidx.compose.ui.graphics.Color

// ── Core neutrals ───────────────────────────────────────────────────────────
// Moved off pure #000 to a deep, slightly cool charcoal so cards, dividers and
// elevation read as real depth instead of flat black-on-black.
val Black       = Color(0xFF07080A)   // app background (near-black, cool)
val White       = Color(0xFFF5F7FA)   // primary text (soft white, not harsh)
val DimSurface  = Color(0xFF101217)   // recessed / variant surface
val CardSurface = Color(0xFF15181E)   // elevated widget surface (card base)
val CardSurfaceHi = Color(0xFF1C2028) // card gradient top (subtle highlight)
val DividerGray = Color(0xFF23272F)   // hairline dividers / outlines
val TextMuted   = Color(0xFF8A919C)   // secondary / muted text

// ── Glass surfaces (cinematic home over a wallpaper) ────────────────────────
val GlassFill   = Color(0x14FFFFFF)   // translucent white film on a glass card
val GlassStroke = Color(0x1AFFFFFF)   // hairline highlight edge
val GlassDark   = Color(0xCC0C0E12)   // dark glass over bright wallpaper areas
val ScrimTop    = Color(0x66000000)   // top gradient scrim for status-bar legibility
val ScrimBottom = Color(0xB305070A)   // bottom scrim behind the media card

// ── Preset accent colours ───────────────────────────────────────────────────
// Modern, vivid-but-calm accents tuned to sit well on the dark charcoal base.
val AccentAmberWarm = Color(0xFFFF9D42)  // primary warm amber (default accent)
val AccentWhite  = Color(0xFFF5F7FA)
val AccentBlue   = Color(0xFF4C8DFF)  // electric blue
val AccentCyan   = Color(0xFF22D3EE)  // teal / cyan
val AccentViolet = Color(0xFF8B5CF6)  // violet
val AccentGreen  = Color(0xFF22E079)  // signal green
val AccentAmber  = Color(0xFFFFB020)  // warm amber
val AccentRed    = Color(0xFFFF5C5C)  // coral red
val AccentBlack  = Color(0xFF0A0B0D)  // stealth (near-black accent)

val accentPresets      = listOf(AccentBlue, AccentCyan, AccentViolet, AccentGreen, AccentAmber, AccentRed, AccentWhite, AccentBlack)
val accentPresetLabels = listOf("Blue", "Cyan", "Violet", "Green", "Amber", "Coral", "White", "Stealth")
