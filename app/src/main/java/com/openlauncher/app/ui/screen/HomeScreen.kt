package com.openlauncher.app.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.openlauncher.app.R
import com.openlauncher.app.data.AppSettings
import com.openlauncher.app.data.ClockStyle
import com.openlauncher.app.data.SoundPadConfig
import com.openlauncher.app.data.UnitSystem
import com.openlauncher.app.model.NowPlayingState
import com.openlauncher.app.model.WeatherState
import com.openlauncher.app.ui.theme.GlassStroke
import com.openlauncher.app.ui.theme.ScrimBottom
import com.openlauncher.app.ui.theme.ScrimTop
import com.openlauncher.app.ui.widget.ClockWidget
import com.openlauncher.app.ui.widget.NowPlayingWidget
import com.openlauncher.app.util.LocationData
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Cinematic home screen modelled on the target reference:
 *   wallpaper → status bar (date · BT/Wi-Fi/signal/weather) → large clock → glass media card.
 *
 * The public signature is kept identical to the previous grid-based home so the
 * caller (MainActivity) is unaffected; the extra callbacks are retained even
 * where this layout no longer surfaces them.
 */
@Composable
fun HomeScreen(
    settings: AppSettings,
    weather: WeatherState?,
    nowPlaying: NowPlayingState?,
    location: LocationData?,
    bearing: Float,
    isWifi: Boolean,
    isData: Boolean,
    isDayMode: Boolean = false,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onLaunchCarPlay: () -> Unit,
    onLaunchAndroidAuto: () -> Unit,
    onAssignCarPlay: () -> Unit,
    onAssignAndroidAuto: () -> Unit,
    onClearCarPlay: () -> Unit,
    onClearAndroidAuto: () -> Unit,
    onAssignPip: () -> Unit,
    onClearPip: () -> Unit,
    onLaunchPip: () -> Unit,
    onTapNowPlaying: () -> Unit,
    onUpdateWidget: (id: String, spanX: Int, spanY: Int) -> Unit,
    onMoveWidget: (id: String, gridX: Int, gridY: Int) -> Unit,
    onAddWidget: (id: String) -> Unit,
    onRemoveWidget: (id: String) -> Unit,
    onSetClockStyle: (ClockStyle) -> Unit,
    onSetVitalsAsBars: (Boolean) -> Unit = {},
    onSetSpeedometerDigitalOnly: (Boolean) -> Unit = {},
    onUpdateSoundPad: (index: Int, pad: SoundPadConfig) -> Unit = { _, _ -> },
    hardwareRadio: com.openlauncher.app.viewmodel.LauncherViewModel.HardwareRadioState? = null,
    onLaunchHardwareRadio: () -> Unit = {},
    onStopHardwareRadio: () -> Unit = {},
    onRadioSeekUp: () -> Unit = {},
    onRadioSeekDown: () -> Unit = {},
    onRadioCycleFm: () -> Unit = {},
    onRadioSwitchAm: () -> Unit = {},
    onRadioTune: (band: String, freq: Float) -> Unit = { _, _ -> },
    onAssignRadio: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val accent = Color(settings.accentColor)

    Box(modifier = modifier.fillMaxSize()) {

        // ── Wallpaper ───────────────────────────────────────────────────────
        HomeBackground(settings)

        // ── Legibility scrim (top for status bar, bottom for media card) ─────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to ScrimTop,
                        0.22f to Color.Transparent,
                        0.62f to Color.Transparent,
                        1.0f to ScrimBottom
                    )
                )
        )

        // ── Content ──────────────────────────────────────────────────────────
        Column(modifier = Modifier.fillMaxSize()) {

            HomeStatusBar(
                settings = settings,
                weather  = weather,
                isWifi   = isWifi,
                isData   = isData,
                accent   = accent
            )

            Spacer(Modifier.height(10.dp))

            // Large clock, upper-left over the wallpaper
            ClockWidget(
                style     = settings.clockStyle,
                accent    = accent,
                isDayMode = false,
                modifier  = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(start = 26.dp)
            )

            Spacer(Modifier.weight(1f))

            // Glass media card, pinned to the bottom
            GlassMediaCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 18.dp)
                    .height(176.dp)
            ) {
                NowPlayingWidget(
                    state               = nowPlaying,
                    accent              = accent,
                    carPlayPackage      = settings.carPlayPackage,
                    androidAutoPackage  = settings.androidAutoPackage,
                    onPlayPause         = onPlayPause,
                    onNext              = onNext,
                    onPrev              = onPrev,
                    onLaunchCarPlay     = onLaunchCarPlay,
                    onLaunchAndroidAuto = onLaunchAndroidAuto,
                    onTapToOpenApp      = onTapNowPlaying,
                    modifier            = Modifier.fillMaxSize(),
                    isEditing           = false,
                    isDayMode           = false,
                    hardwareRadio         = hardwareRadio,
                    onLaunchHardwareRadio = onLaunchHardwareRadio,
                    onStopHardwareRadio   = onStopHardwareRadio,
                    onRadioSeekUp         = onRadioSeekUp,
                    onRadioSeekDown       = onRadioSeekDown,
                    onRadioCycleFm        = onRadioCycleFm,
                    onRadioSwitchAm       = onRadioSwitchAm,
                    onRadioTune           = onRadioTune,
                    onAssignRadio         = onAssignRadio
                )
            }
        }
    }
}

@Composable
private fun HomeBackground(settings: AppSettings) {
    if (settings.wallpaperUri.isNotEmpty()) {
        AsyncImage(
            model              = settings.wallpaperUri,
            contentDescription = null,
            contentScale       = ContentScale.Crop,
            modifier           = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = settings.wallpaperDim.coerceIn(0f, 1f)))
        )
    } else {
        // Bundled GT3 RS wallpaper (default)
        Image(
            painter            = painterResource(R.drawable.default_wallpaper),
            contentDescription = null,
            contentScale       = ContentScale.Crop,
            modifier           = Modifier.fillMaxSize()
        )
        // Legibility scrims (match the reference: dark on the left + bottom)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0x8C000000), Color(0x26000000), Color.Transparent)
                    )
                )
        )
    }
}

@Composable
private fun HomeStatusBar(
    settings: AppSettings,
    weather: WeatherState?,
    isWifi: Boolean,
    isData: Boolean,
    accent: Color
) {
    var now by remember { mutableStateOf(Date()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = Date()
            delay(30_000)
        }
    }
    val dateStr = remember(now.time / 60000) {
        SimpleDateFormat("EEE, d MMM", Locale.getDefault()).format(now)
    }

    val iconColor = Color.White.copy(alpha = 0.85f)
    val textColor = Color.White.copy(alpha = 0.92f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 26.dp, end = 26.dp, top = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text       = dateStr,
            color      = textColor,
            fontSize   = 15.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.weight(1f))

        Icon(Icons.Default.Bluetooth, "Bluetooth", tint = iconColor, modifier = Modifier.size(18.dp))
        if (isWifi) {
            Spacer(Modifier.width(14.dp))
            Icon(Icons.Default.Wifi, "Wi-Fi", tint = iconColor, modifier = Modifier.size(18.dp))
        }
        if (isData) {
            Spacer(Modifier.width(14.dp))
            Icon(Icons.Default.SignalCellularAlt, "Signal", tint = iconColor, modifier = Modifier.size(18.dp))
        }
        if (weather != null) {
            Spacer(Modifier.width(16.dp))
            val sunny = weather.weatherCode == 0
            Icon(
                imageVector        = if (sunny) Icons.Default.WbSunny else Icons.Default.Cloud,
                contentDescription = "Weather",
                tint               = Color(0xFFFCD34D).copy(alpha = 0.9f),
                modifier           = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text     = weather.temperatureDisplay(settings.unitSystem == UnitSystem.METRIC),
                color    = textColor,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
private fun GlassMediaCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(28.dp)
    Box(
        modifier = modifier
            .clip(shape)
            // Matches Stitch's .glass-player radial fill (warm top-right → deep dark)
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xA61E1814), Color(0xD90D0E12), Color(0xF008090C)),
                    center = Offset(0.78f * 2400f, 0.12f * 600f),
                    radius = 1600f
                )
            )
            .border(1.dp, GlassStroke, shape)
    ) {
        content()
    }
}
