package net.kibotu.geofencerelay.features.ai.ui.dialogs

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import net.kibotu.geofencerelay.features.ai.localization.MultilingualManager
import net.kibotu.geofencerelay.features.ai.ui.components.IosBackPillButton
import net.kibotu.geofencerelay.features.ai.ui.theme.GoogleColors
import net.kibotu.geofencerelay.features.ai.ui.theme.IosColors
import net.kibotu.geofencerelay.features.ai.ui.theme.IosDimensions
import net.kibotu.geofencerelay.model.LocationPing
import net.kibotu.geofencerelay.service.TrackerForegroundService
import net.kibotu.geofencerelay.util.BatteryUtils
import net.kibotu.geofencerelay.util.LocationUtils
import java.util.Locale

/**
 * Robust GPS Sentinel Beacon controller panel.
 * Features 1-tap permission resolution, Google Play Services GPS hardware enablement,
 * live satellite telemetry feed (coordinates, address, battery, speed), and authorized caregivers management.
 */
@Composable
fun BeaconTrackerPanel(
    userEmail: String,
    selectedLanguageCode: String = "en",
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val servicePrefs = remember {
        context.getSharedPreferences(TrackerForegroundService.PREFS_NAME, Context.MODE_PRIVATE)
    }

    val isBroadcastingFromService by TrackerForegroundService.serviceRunning.collectAsState()
    var isBroadcasting by remember {
        mutableStateOf(TrackerForegroundService.isRunning(context))
    }
    LaunchedEffect(isBroadcastingFromService) {
        isBroadcasting = isBroadcastingFromService || TrackerForegroundService.isRunning(context)
    }

    val latestPing by TrackerForegroundService.latestDevicePing.collectAsState()

    var authorizedEmails by remember {
        val set = TrackerForegroundService.getAuthorizedEmails(context)
        mutableStateOf(if (set.isNotEmpty()) set.toList() else listOf(userEmail))
    }

    LaunchedEffect(userEmail) {
        if (userEmail.isNotBlank()) {
            TrackerForegroundService.addAuthorizedEmail(context, userEmail)
            authorizedEmails = TrackerForegroundService.getAuthorizedEmails(context).toList()
        }
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var newEmailInput by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var showBackgroundPermissionDialog by remember { mutableStateOf(false) }

    val deviceName = remember { LocationUtils.getFriendlyDeviceName(Build.MODEL) }

    fun checkLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    fun checkBackgroundPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    var hasLocationPermission by remember { mutableStateOf(checkLocationPermission()) }
    var hasBackgroundPermission by remember { mutableStateOf(checkBackgroundPermission()) }

    // Check system location/GPS hardware
    val locationManager = remember { context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager }
    var isGpsHardwareEnabled by remember {
        mutableStateOf(locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true ||
                locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true)
    }

    // Google Play Services Dialog Launcher to turn on GPS with 1 tap
    val gpsResolutionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            isGpsHardwareEnabled = true
            TrackerForegroundService.start(context)
            isBroadcasting = true
        } else {
            isGpsHardwareEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true ||
                    locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true
            TrackerForegroundService.start(context)
            isBroadcasting = true
        }
    }

    fun promptEnableGps(onGpsReady: () -> Unit) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L).build()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
        val client = LocationServices.getSettingsClient(context)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            isGpsHardwareEnabled = true
            onGpsReady()
        }
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution.intentSender).build()
                    gpsResolutionLauncher.launch(intentSenderRequest)
                } catch (_: Exception) {
                    onGpsReady()
                }
            } else {
                onGpsReady()
            }
        }
    }

    val backgroundPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasBackgroundPermission = granted || checkBackgroundPermission()
        promptEnableGps {
            TrackerForegroundService.start(context)
            isBroadcasting = true
        }
    }

    val fineLocationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) ||
                (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true)
        hasLocationPermission = granted || checkLocationPermission()
        if (granted) {
            promptEnableGps {
                TrackerForegroundService.start(context)
                isBroadcasting = true
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !checkBackgroundPermission()) {
                showBackgroundPermissionDialog = true
            }
        }
    }

    fun saveEmails(list: List<String>) {
        authorizedEmails = list
        servicePrefs.edit().putStringSet(TrackerForegroundService.KEY_AUTHORIZED_EMAILS, list.toSet()).apply()
        TrackerForegroundService.notifyAuthorizedEmailsChanged(context)
    }

    // Pulsing radar animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.30f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IosColors.SystemGroupedBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = MultilingualManager.tr("beacon_title", selectedLanguageCode),
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = IosColors.LabelPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = MultilingualManager.tr("beacon_subtitle", selectedLanguageCode),
                fontSize = 13.sp,
                color = IosColors.LabelSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            // GPS Disabled Warning Banner
            if (!isGpsHardwareEnabled) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .clickable { promptEnableGps { isGpsHardwareEnabled = true } },
                    colors = CardDefaults.cardColors(containerColor = GoogleColors.Yellow.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(14.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GoogleColors.Yellow))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOff, contentDescription = null, tint = GoogleColors.Yellow, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(MultilingualManager.tr("btn_turn_on_gps", selectedLanguageCode), fontWeight = FontWeight.Bold, color = GoogleColors.Yellow, fontSize = 13.sp)
                            Text("Tap here to turn on Google High-Accuracy GPS with one tap.", color = IosColors.LabelSecondary, fontSize = 11.sp)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = GoogleColors.Yellow)
                    }
                }
            }

            // Background Location Permission Banner
            if (!hasBackgroundPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .clickable { showBackgroundPermissionDialog = true },
                    colors = CardDefaults.cardColors(containerColor = GoogleColors.Blue.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(14.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GoogleColors.Blue))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = GoogleColors.Blue, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Background Permission Needed", fontWeight = FontWeight.Bold, color = GoogleColors.Blue, fontSize = 13.sp)
                            Text("Tap to set 'Allow all the time' for continuous 24/7 tracking.", color = IosColors.LabelSecondary, fontSize = 11.sp)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = GoogleColors.Blue)
                    }
                }
            } else if (hasBackgroundPermission) {
                Row(
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(GoogleColors.Green.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GoogleColors.Green, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Background Location: Allowed All the Time", fontSize = 11.sp, color = GoogleColors.Green, fontWeight = FontWeight.SemiBold)
                }
            }

            // Main Radar Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(IosDimensions.CardCornerRadius),
                colors = CardDefaults.cardColors(containerColor = IosColors.SecondarySystemGroupedBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
                        if (isBroadcasting) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .scale(pulseScale)
                                    .clip(CircleShape)
                                    .background(GoogleColors.Green.copy(alpha = pulseAlpha))
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(if (isBroadcasting) GoogleColors.Green else IosColors.CameraIconBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isBroadcasting) Icons.Default.Sensors else Icons.Default.SensorsOff,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (isBroadcasting) MultilingualManager.tr("btn_stop_broadcast", selectedLanguageCode).uppercase() else "BROADCAST PAUSED",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isBroadcasting) GoogleColors.Green else IosColors.LabelSecondary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isBroadcasting)
                            "Broadcasting coordinates to ${authorizedEmails.size} authorized Google accounts"
                        else
                            "Tap below to begin sending background GPS coordinates",
                        fontSize = 12.sp,
                        color = IosColors.LabelSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Primary Action Button (Safely requests permissions if missing, prompts GPS, starts FGS)
                    Button(
                        onClick = {
                            if (isBroadcasting) {
                                TrackerForegroundService.stop(context)
                                isBroadcasting = false
                            } else {
                                hasLocationPermission = checkLocationPermission()
                                if (hasLocationPermission) {
                                    promptEnableGps {
                                        TrackerForegroundService.start(context)
                                        isBroadcasting = true
                                    }
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !checkBackgroundPermission()) {
                                        showBackgroundPermissionDialog = true
                                    }
                                } else {
                                    val perms = mutableListOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        perms.add(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                    fineLocationLauncher.launch(perms.toTypedArray())
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when {
                                isBroadcasting -> GoogleColors.Red
                                !hasLocationPermission -> GoogleColors.Blue
                                else -> GoogleColors.Green
                            }
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when {
                                    isBroadcasting -> Icons.Default.Stop
                                    !hasLocationPermission -> Icons.Default.Security
                                    else -> Icons.Default.PlayArrow
                                },
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when {
                                    isBroadcasting -> MultilingualManager.tr("btn_stop_broadcast", selectedLanguageCode)
                                    !hasLocationPermission -> MultilingualManager.tr("btn_grant_perms", selectedLanguageCode)
                                    else -> MultilingualManager.tr("btn_start_broadcast", selectedLanguageCode)
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Live Telemetry Fix Card (Coordinates, Address, Battery, Speed - restored like before today)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(IosDimensions.CardCornerRadius),
                colors = CardDefaults.cardColors(containerColor = IosColors.SecondarySystemGroupedBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MyLocation, contentDescription = null, tint = GoogleColors.Blue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = MultilingualManager.tr("lbl_coordinates", selectedLanguageCode),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = IosColors.LabelPrimary
                            )
                        }
                        if (isBroadcasting) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(GoogleColors.Green))
                                Spacer(modifier = Modifier.width(5.dp))
                                Text("LIVE", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = GoogleColors.Green)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val ping = latestPing
                    if (ping != null) {
                        Text(
                            text = "📍 ${ping.address}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = IosColors.LabelPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = String.format(Locale.US, "GPS: %.5f, %.5f (±%dm)", ping.latitude, ping.longitude, ping.accuracy.toInt()),
                            fontSize = 12.sp,
                            color = IosColors.LabelSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (ping.isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                                contentDescription = null,
                                tint = GoogleColors.Green,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${MultilingualManager.tr("lbl_battery", selectedLanguageCode)}: ${ping.batteryLevel}%", fontSize = 12.sp, color = IosColors.LabelSecondary)
                            Spacer(modifier = Modifier.width(14.dp))
                            Icon(Icons.Default.Speed, contentDescription = null, tint = GoogleColors.Yellow, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${MultilingualManager.tr("lbl_speed", selectedLanguageCode)}: ${LocationUtils.formatSpeed(ping.speed)}", fontSize = 12.sp, color = IosColors.LabelSecondary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Last Broadcast: ${LocationUtils.formatTime(ping.timestamp)}",
                            fontSize = 11.sp,
                            color = IosColors.LabelSecondary
                        )
                        if (ping.isBreach) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GoogleColors.Red.copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = GoogleColors.Red, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${MultilingualManager.tr("status_breach", selectedLanguageCode)} (${LocationUtils.formatDistance(ping.distanceFromCenter)})",
                                    color = GoogleColors.Red,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else if (isBroadcasting) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = GoogleColors.Blue)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Acquiring exact GPS satellite fix...", fontSize = 13.sp, color = IosColors.LabelSecondary)
                        }
                    } else {
                        Text(
                            text = "Start broadcasting to view live GPS coordinates and send real-time pings to caregivers.",
                            fontSize = 12.sp,
                            color = IosColors.LabelSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Hardware & Privacy Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(IosDimensions.CardCornerRadius),
                colors = CardDefaults.cardColors(containerColor = IosColors.SecondarySystemGroupedBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = GoogleColors.Blue)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(deviceName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = IosColors.LabelPrimary)
                            Text("100% On-Device GPS Sentinel â€¢ Zero Cloud Tracking", fontSize = 11.sp, color = IosColors.LabelSecondary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Authorized Caregivers Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(IosDimensions.CardCornerRadius),
                colors = CardDefaults.cardColors(containerColor = IosColors.SecondarySystemGroupedBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Authorized Caregivers (${authorizedEmails.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = IosColors.LabelPrimary
                        )

                        IconButton(
                            onClick = {
                                newEmailInput = ""
                                emailError = null
                                showAddDialog = true
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(GoogleColors.Blue)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    authorizedEmails.forEach { email ->
                        val isSelf = email.equals(userEmail, ignoreCase = true)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(IosColors.SystemGroupedBackground)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(email, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = IosColors.LabelPrimary)
                                Text(if (isSelf) "Owner (This Phone)" else "Authorized Guardian", fontSize = 10.sp, color = IosColors.LabelSecondary)
                            }
                            if (!isSelf) {
                                IconButton(
                                    onClick = { saveEmails(authorizedEmails.filter { it != email }) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Revoke", tint = GoogleColors.Red, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Apple iOS Assistive Access Back Button
        IosBackPillButton(onClick = onBack)

        // Background Permission Dialog
        if (showBackgroundPermissionDialog) {
            AlertDialog(
                onDismissRequest = { showBackgroundPermissionDialog = false },
                title = { Text("Background Location Permission", fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "To enable 24/7 continuous location broadcasting even when the screen is locked or app is minimized, please choose 'Allow all the time' in the system prompt.",
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showBackgroundPermissionDialog = false
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                backgroundPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoogleColors.Blue)
                    ) {
                        Text("Continue", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showBackgroundPermissionDialog = false }) {
                        Text("Later", color = Color.Gray)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
        }

        // Add Caregiver Dialog
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Grant Access to Caregiver", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Enter Google Account email of your family or caregiver:", fontSize = 12.sp, color = IosColors.LabelSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newEmailInput,
                            onValueChange = {
                                newEmailInput = it.trim()
                                emailError = null
                            },
                            placeholder = { Text("caregiver@gmail.com", color = Color.Gray) },
                            isError = emailError != null,
                            supportingText = emailError?.let { { Text(it, color = GoogleColors.Red) } },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedContainerColor = Color(0xFFF2F2F7),
                                unfocusedContainerColor = Color(0xFFF2F2F7),
                                focusedBorderColor = GoogleColors.Blue,
                                unfocusedBorderColor = IosColors.CardBorder,
                                cursorColor = GoogleColors.Blue
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val clean = newEmailInput.trim().lowercase()
                            if (!clean.contains("@") || !clean.contains(".")) {
                                emailError = "Enter a valid email"
                            } else if (authorizedEmails.contains(clean)) {
                                emailError = "Already authorized"
                            } else {
                                saveEmails(authorizedEmails + clean)
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoogleColors.Blue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Add Caregiver", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel", color = IosColors.LabelSecondary)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}