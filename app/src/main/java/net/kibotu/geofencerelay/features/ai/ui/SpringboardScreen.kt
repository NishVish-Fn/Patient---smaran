package net.kibotu.geofencerelay.features.ai.ui

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.kibotu.geofencerelay.R
import net.kibotu.geofencerelay.features.ai.localization.MultilingualManager
import net.kibotu.geofencerelay.features.ai.model.CpsAssessmentResult
import net.kibotu.geofencerelay.features.ai.reminder.GameReminderManager
import net.kibotu.geofencerelay.features.ai.ui.components.IosSpringboardCard
import net.kibotu.geofencerelay.features.ai.ui.dialogs.*
import net.kibotu.geofencerelay.features.ai.ui.theme.GoogleColors
import net.kibotu.geofencerelay.features.ai.ui.theme.IosColors
import net.kibotu.geofencerelay.features.ai.ui.theme.IosDimensions
import net.kibotu.geofencerelay.service.TrackerForegroundService
import java.text.SimpleDateFormat
import java.util.*

sealed class SpringboardDestination {
    object Home : SpringboardDestination()
    object Beacon : SpringboardDestination()
    object Exercises : SpringboardDestination()
    object Health : SpringboardDestination()
    object Safety : SpringboardDestination()
    object Voice : SpringboardDestination()
    object Memory : SpringboardDestination()
}

/**
 * Apple iOS Assistive Access Springboard Home Screen.
 * Fully localized across 7 regional languages with clear feature icons and genuine score computation.
 */
@Composable
fun SpringboardScreen(
    userEmail: String,
    initialDestination: SpringboardDestination = SpringboardDestination.Home,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current

    // Initialize Multilingual & TTS support
    LaunchedEffect(Unit) {
        MultilingualManager.initTts(context)
    }

    DisposableEffect(Unit) {
        onDispose {
            MultilingualManager.shutdown()
        }
    }

    val prefs = remember { context.getSharedPreferences("app_settings", Context.MODE_PRIVATE) }
    var activeDestination by remember { mutableStateOf<SpringboardDestination>(initialDestination) }
    var selectedLanguageCode by remember {
        mutableStateOf(prefs.getString("selected_language", "en") ?: "en")
    }

    // Start with unassessed state: NO fake score displayed until the user plays a game!
    var currentAssessment by remember { mutableStateOf<CpsAssessmentResult?>(null) }

    var isAlarmPopping by remember { mutableStateOf(GameReminderManager.isAlarmFiring(context)) }

    LaunchedEffect(Unit) {
        while (true) {
            isAlarmPopping = GameReminderManager.isAlarmFiring(context)
            kotlinx.coroutines.delay(1200)
        }
    }

    val isServiceRunning by TrackerForegroundService.serviceRunning.collectAsState()
    val isBroadcasting = isServiceRunning || TrackerForegroundService.isRunning(context)

    val currentTimeStr = remember {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        sdf.format(Date())
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(IosColors.SystemGroupedBackground)
    ) {
        AnimatedContent(
            targetState = activeDestination,
            transitionSpec = {
                if (targetState == SpringboardDestination.Home) {
                    slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                } else {
                    slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                }
            },
            label = "springboardNav"
        ) { dest ->
            when (dest) {
                SpringboardDestination.Home -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // iOS Status Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currentTimeStr,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = IosColors.LabelPrimary
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isBroadcasting) IosColors.SystemGreen else IosColors.LabelSecondary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isBroadcasting)
                                        MultilingualManager.tr("beacon_live", selectedLanguageCode)
                                    else
                                        MultilingualManager.tr("beacon_standby", selectedLanguageCode),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isBroadcasting) IosColors.SystemGreen else IosColors.LabelSecondary
                                )
                            }
                        }

                        // App Title & Patient Info Bar
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = IosColors.SecondarySystemGroupedBackground),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(IosColors.CardBorder),
                                width = 1.dp
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(id = R.drawable.smaran_logo),
                                        contentDescription = "Smaran Logo",
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color.White)
                                            .border(1.dp, Color(0x18000000), RoundedCornerShape(10.dp))
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = MultilingualManager.tr("app_title", selectedLanguageCode),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 16.sp,
                                            color = IosColors.LabelPrimary,
                                            letterSpacing = 0.5.sp
                                        )
                                        Text(
                                            userEmail,
                                            fontSize = 11.sp,
                                            color = IosColors.LabelSecondary
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = onSignOut,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(IosColors.SystemGroupedBackground)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                        contentDescription = "Sign Out",
                                        tint = IosColors.LabelSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // 2-Column Apple iOS Assistive Access Grid with REAL feature icons and labels
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            // 1. GPS Beacon Tile (Real Location icon)
                            item {
                                IosSpringboardCard(
                                    title = MultilingualManager.tr("tile_gps_title", selectedLanguageCode),
                                    icon = Icons.Default.LocationOn,
                                    iconBgColor = IosColors.SystemGreen,
                                    statusSubtitle = if (isBroadcasting)
                                        MultilingualManager.tr("tile_gps_sub_broadcasting", selectedLanguageCode)
                                    else
                                        MultilingualManager.tr("tile_gps_sub_standby", selectedLanguageCode),
                                    badgeText = if (isBroadcasting) "Live" else null,
                                    badgeIcon = Icons.Default.Sensors,
                                    badgeColor = IosColors.SystemGreen,
                                    onClick = { activeDestination = SpringboardDestination.Beacon }
                                )
                            }

                            // 2. Brain Games Hub Tile (Real Gamepad icon)
                            item {
                                IosSpringboardCard(
                                    title = MultilingualManager.tr("tile_games_title", selectedLanguageCode),
                                    icon = Icons.Default.SportsEsports,
                                    iconBgColor = IosColors.SystemOrange,
                                    statusSubtitle = MultilingualManager.tr("tile_games_sub", selectedLanguageCode),
                                    onClick = { activeDestination = SpringboardDestination.Exercises }
                                )
                            }

                            // 3. Cognitive Health Tile (Real Brain/Psychology icon, with genuine score!)
                            item {
                                val scoreText = currentAssessment?.cpsScore?.toInt()?.let { "$it CPS" }
                                IosSpringboardCard(
                                    title = MultilingualManager.tr("tile_score_title", selectedLanguageCode),
                                    icon = Icons.Default.Psychology,
                                    iconBgColor = IosColors.SystemPink,
                                    badgeText = scoreText ?: "--",
                                    badgeColor = if (currentAssessment != null) IosColors.SystemPink else IosColors.LabelSecondary,
                                    statusSubtitle = if (currentAssessment != null)
                                        MultilingualManager.tr("tile_score_sub_tested", selectedLanguageCode)
                                    else
                                        MultilingualManager.tr("tile_score_sub_untested", selectedLanguageCode),
                                    onClick = { activeDestination = SpringboardDestination.Health }
                                )
                            }

                            // 4. Safety & Hazard Alerts Tile (Real Shield icon with "🔔 New" badge)
                            item {
                                IosSpringboardCard(
                                    title = MultilingualManager.tr("tile_safety_title", selectedLanguageCode),
                                    icon = Icons.Default.Shield,
                                    iconBgColor = IosColors.SystemRed,
                                    badgeText = "New",
                                    badgeIcon = Icons.Default.Notifications,
                                    badgeColor = IosColors.SystemBlue,
                                    statusSubtitle = MultilingualManager.tr("tile_safety_sub", selectedLanguageCode),
                                    onClick = { activeDestination = SpringboardDestination.Safety }
                                )
                            }

                            // 5. Languages & Voice Guidance Tile (Real Translation Globe icon)
                            item {
                                IosSpringboardCard(
                                    title = MultilingualManager.tr("tile_voice_title", selectedLanguageCode),
                                    icon = Icons.Default.Translate,
                                    iconBgColor = IosColors.SystemTeal,
                                    statusSubtitle = MultilingualManager.tr("tile_voice_sub", selectedLanguageCode),
                                    onClick = { activeDestination = SpringboardDestination.Voice }
                                )
                            }

                            // 6. Memory Vault Tile (Real Photo/Memories icon)
                            item {
                                IosSpringboardCard(
                                    title = MultilingualManager.tr("tile_memory_title", selectedLanguageCode),
                                    icon = Icons.Default.CollectionsBookmark,
                                    iconBgColor = IosColors.SystemIndigo,
                                    statusSubtitle = MultilingualManager.tr("tile_memory_sub", selectedLanguageCode),
                                    onClick = { activeDestination = SpringboardDestination.Memory }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                SpringboardDestination.Beacon -> {
                    BeaconTrackerPanel(
                        userEmail = userEmail,
                        selectedLanguageCode = selectedLanguageCode,
                        onBack = { activeDestination = SpringboardDestination.Home }
                    )
                }

                SpringboardDestination.Exercises -> {
                    BrainExerciseGamePanel(
                        selectedLanguageCode = selectedLanguageCode,
                        onAssessmentUpdated = { updated -> currentAssessment = updated },
                        onBack = { activeDestination = SpringboardDestination.Home }
                    )
                }

                SpringboardDestination.Health -> {
                    CognitiveHealthPanel(
                        assessment = currentAssessment,
                        selectedLanguageCode = selectedLanguageCode,
                        onLaunchGame = { activeDestination = SpringboardDestination.Exercises },
                        onBack = { activeDestination = SpringboardDestination.Home }
                    )
                }

                SpringboardDestination.Safety -> {
                    SafetyAlertsPanel(
                        assessment = currentAssessment,
                        selectedLanguageCode = selectedLanguageCode,
                        onBack = { activeDestination = SpringboardDestination.Home }
                    )
                }

                SpringboardDestination.Voice -> {
                    VoiceLanguagePanel(
                        selectedLanguageCode = selectedLanguageCode,
                        onLanguageSelected = { code ->
                            selectedLanguageCode = code
                            prefs.edit().putString("selected_language", code).apply()
                        },
                        onBack = { activeDestination = SpringboardDestination.Home }
                    )
                }

                SpringboardDestination.Memory -> {
                    MemoryVaultPanel(
                        selectedLanguageCode = selectedLanguageCode,
                        onBack = { activeDestination = SpringboardDestination.Home }
                    )
                }
            }
        }

        // Popping Colors Alarm Reminder Dialog (Stable bounds, localized texts)
        if (isAlarmPopping) {
            AlertDialog(
                onDismissRequest = {
                    // Do NOT dismiss alarm on outside touch or accidental gestures
                },
                properties = androidx.compose.ui.window.DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                ),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🌟 ", fontSize = 24.sp)
                        Text(
                            text = MultilingualManager.tr("alarm_title", selectedLanguageCode),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = GoogleColors.Red
                        )
                    }
                },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            listOf(GoogleColors.Blue, GoogleColors.Red, GoogleColors.Yellow, GoogleColors.Green).forEach { col ->
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp)
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(col)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = MultilingualManager.tr("alarm_desc", selectedLanguageCode),
                            fontSize = 13.sp,
                            color = IosColors.LabelPrimary,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            GameReminderManager.dismissAlarm(context)
                            isAlarmPopping = false
                            activeDestination = SpringboardDestination.Exercises
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoogleColors.Green),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.SportsEsports, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(MultilingualManager.tr("alarm_btn_play", selectedLanguageCode), fontWeight = FontWeight.Bold, color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            GameReminderManager.dismissAlarm(context)
                            GameReminderManager.scheduleNextAlarm(context, 10L)
                            isAlarmPopping = false
                        }
                    ) {
                        Text(MultilingualManager.tr("alarm_btn_snooze", selectedLanguageCode), color = IosColors.LabelSecondary, fontWeight = FontWeight.SemiBold)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(22.dp)
            )
        }
    }
}
