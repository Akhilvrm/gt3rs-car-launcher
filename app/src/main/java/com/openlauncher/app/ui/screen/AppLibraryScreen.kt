package com.openlauncher.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.openlauncher.app.model.AppInfo
import com.openlauncher.app.ui.theme.LocalDayMode

private enum class AppFilter { USER, SYSTEM, ALL }

private val TILE_RADIUS = RoundedCornerShape(20.dp)
private val PILL_RADIUS = RoundedCornerShape(22.dp)
private val CHIP_RADIUS = RoundedCornerShape(14.dp)

// Shared design tokens for the dark cinematic / amber glass look.
private val DarkScreenBg   = Color(0xFF07080A)
private val GlassFillDark   = Color.White.copy(alpha = 0.06f)
private val GlassBorderDark = Color.White.copy(alpha = 0.06f)
private val SearchFillDark  = Color.White.copy(alpha = 0.05f)
private val SearchBorderD   = Color.White.copy(alpha = 0.08f)

@Composable
fun AppLibraryScreen(
    apps: List<AppInfo>,
    isLoading: Boolean,
    isPickerMode: Boolean,
    pickerSlot: Int?,
    isCarPlayPickerMode: Boolean,
    carPlayPickerLabel: String = "CHOOSE CARPLAY APP",
    accent: Color,
    onAppClick: (AppInfo) -> Unit,
    onPickerSelect: (Int, AppInfo) -> Unit,
    onCarPlaySelect: (AppInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDayMode     = LocalDayMode.current
    val screenBg      = if (isDayMode) MaterialTheme.colorScheme.background else DarkScreenBg
    val headerColor   = if (isDayMode) Color(0xFF1A1A1A) else Color.White.copy(alpha = 0.92f)
    val placeholderC  = if (isDayMode) Color(0xFF999999) else Color.White.copy(alpha = 0.45f)
    val dividerColor  = if (isDayMode) Color(0xFFE2E2E2) else Color.White.copy(alpha = 0.06f)
    val emptyColor    = if (isDayMode) Color(0xFF888888) else Color.White.copy(alpha = 0.55f)
    val fieldTextC    = if (isDayMode) Color(0xFF1A1A1A) else Color.White.copy(alpha = 0.92f)
    val searchFill    = if (isDayMode) Color(0xFFF2F2F2) else SearchFillDark
    val searchBorderU = if (isDayMode) Color(0xFFDDDDDD) else SearchBorderD

    val anyPickerMode = isPickerMode || isCarPlayPickerMode
    var query     by remember { mutableStateOf("") }
    var appFilter by remember { mutableStateOf(AppFilter.USER) }

    val filtered = remember(apps, query, appFilter, anyPickerMode) {
        val byName = if (query.isBlank()) apps
                     else apps.filter { it.appName.contains(query, ignoreCase = true) }
        // In picker mode always show everything so shortcuts can be set to any app
        if (anyPickerMode) byName
        else when (appFilter) {
            AppFilter.USER   -> byName.filter { !it.isSystemApp }
            AppFilter.SYSTEM -> byName.filter { it.isSystemApp }
            AppFilter.ALL    -> byName
        }
    }

    Column(modifier = modifier.fillMaxSize().background(screenBg)) {
        // ── Header ─────────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text          = when {
                    isCarPlayPickerMode -> carPlayPickerLabel
                    anyPickerMode       -> "Choose app"
                    else                -> "Apps"
                },
                style         = MaterialTheme.typography.titleLarge,
                color         = if (anyPickerMode) accent else headerColor,
                fontWeight    = FontWeight.SemiBold,
                letterSpacing = 0.5.sp,
                fontSize      = 20.sp
            )
            if (!anyPickerMode) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppFilter.entries.forEach { filter ->
                        val selected = appFilter == filter
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .clip(CHIP_RADIUS)
                                .background(
                                    if (selected) accent.copy(alpha = if (isDayMode) 1f else 0.18f)
                                    else if (isDayMode) Color(0xFFF0F0F0) else GlassFillDark
                                )
                                .border(
                                    1.dp,
                                    if (selected) accent.copy(alpha = if (isDayMode) 1f else 0.55f) else GlassBorderDark,
                                    CHIP_RADIUS
                                )
                                .clickable { appFilter = filter }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = when (filter) {
                                    AppFilter.USER   -> "Installed"
                                    AppFilter.SYSTEM -> "System"
                                    AppFilter.ALL    -> "All"
                                },
                                color = when {
                                    selected && isDayMode -> Color.Black
                                    selected              -> accent
                                    else                  -> placeholderC
                                },
                                fontSize = 12.sp,
                                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                                letterSpacing = 0.3.sp
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            var searchFocused by remember { mutableStateOf(false) }
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .width(240.dp)
                    .height(44.dp)
                    .clip(PILL_RADIUS)
                    .background(searchFill)
                    .border(1.dp, if (searchFocused) accent.copy(alpha = 0.7f) else searchBorderU, PILL_RADIUS)
                    .padding(horizontal = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(
                        Icons.Default.Search,
                        null,
                        tint = if (searchFocused) accent else placeholderC,
                        modifier = Modifier.size(18.dp)
                    )
                    BasicTextField(
                        value         = query,
                        onValueChange = { query = it },
                        singleLine    = true,
                        textStyle     = TextStyle(color = fieldTextC, fontSize = 15.sp),
                        cursorBrush   = SolidColor(accent),
                        modifier      = Modifier
                            .weight(1f)
                            .onFocusChanged { searchFocused = it.isFocused },
                        decorationBox = { inner ->
                            Box {
                                if (query.isEmpty()) Text("Search apps", color = placeholderC, fontSize = 15.sp)
                                inner()
                            }
                        }
                    )
                }
            }
        }

        HorizontalDivider(color = dividerColor)

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = accent, modifier = Modifier.size(28.dp), strokeWidth = 2.dp)
            }
            return@Column
        }

        if (filtered.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No apps found", color = emptyColor, letterSpacing = 0.5.sp, fontSize = 15.sp)
            }
            return@Column
        }

        // ── App grid ────────────────────────────────────────────────────────────
        LazyVerticalGrid(
            columns               = GridCells.Adaptive(minSize = 108.dp),
            contentPadding        = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement   = Arrangement.spacedBy(14.dp),
            modifier              = Modifier.fillMaxSize()
        ) {
            items(filtered, key = { it.packageName }) { app ->
                AppTile(
                    app     = app,
                    accent  = accent,
                    onClick = {
                        when {
                            isCarPlayPickerMode            -> onCarPlaySelect(app)
                            isPickerMode && pickerSlot != null -> onPickerSelect(pickerSlot, app)
                            else                           -> onAppClick(app)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun AppTile(
    app: AppInfo,
    accent: Color,
    onClick: () -> Unit
) {
    val isDayMode  = LocalDayMode.current
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    val tileBg = if (isDayMode) {
        SolidColor(Color(0xFFFFFFFF))
    } else {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.07f),
                Color.White.copy(alpha = 0.04f)
            )
        )
    }
    val tileBorder = if (pressed) {
        accent.copy(alpha = 0.6f)
    } else if (isDayMode) {
        Color(0xFFE2E2E2)
    } else {
        GlassBorderDark
    }
    val labelColor = if (isDayMode) Color(0xFF555555) else Color.White.copy(alpha = 0.82f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .aspectRatio(1f)
            .clip(TILE_RADIUS)
            .background(tileBg)
            .border(1.dp, tileBorder, TILE_RADIUS)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(14.dp)
    ) {
        val bmp = remember(app.packageName) {
            try { app.icon.toBitmap(96, 96) } catch (_: Exception) { null }
        }
        if (bmp != null) {
            androidx.compose.foundation.Image(
                painter            = BitmapPainter(bmp.asImageBitmap()),
                contentDescription = app.appName,
                modifier           = Modifier.size(52.dp)
            )
        } else {
            Box(
                Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accent.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Text(app.appName.take(1).uppercase(), color = accent, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text          = app.appName,
            style         = MaterialTheme.typography.labelMedium,
            color         = labelColor,
            maxLines      = 1,
            overflow      = TextOverflow.Ellipsis,
            textAlign     = TextAlign.Center,
            letterSpacing = 0.2.sp,
            fontSize      = 12.sp
        )
    }
}
