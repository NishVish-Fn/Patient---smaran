package net.kibotu.geofencerelay.ui.auth

import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import net.kibotu.geofencerelay.R
import net.kibotu.geofencerelay.features.ai.ui.theme.GoogleColors
import net.kibotu.geofencerelay.features.ai.ui.theme.IosColors
import net.kibotu.geofencerelay.features.ai.ui.theme.IosDimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleSignInScreen(
    appTitle: String = "Smaran",
    appSubtitle: String = "Assistive Sentinel & Cognitive AI",
    isTrackerMode: Boolean = false,
    onSignInSuccess: (email: String) -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var detectedAccounts by remember { mutableStateOf<List<String>>(emptyList()) }

    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
    }
    val googleSignInClient: GoogleSignInClient = remember {
        GoogleSignIn.getClient(context, gso)
    }

    fun refreshDetectedAccounts() {
        try {
            val am = AccountManager.get(context)
            val googleAccounts = am.getAccountsByType("com.google")
            detectedAccounts = googleAccounts.map { it.name.lowercase() }
        } catch (_: Exception) {}
    }

    LaunchedEffect(Unit) {
        val savedEmail = prefs.getString("user_google_email", null)
        if (!savedEmail.isNullOrBlank()) {
            onSignInSuccess(savedEmail)
            return@LaunchedEffect
        }
        val lastAccount = GoogleSignIn.getLastSignedInAccount(context)
        if (lastAccount != null && !lastAccount.email.isNullOrBlank()) {
            val clean = lastAccount.email!!.trim().lowercase()
            prefs.edit().putString("user_google_email", clean).apply()
            onSignInSuccess(clean)
            return@LaunchedEffect
        }
        refreshDetectedAccounts()
    }

    val accountChooserLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isLoading = false
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val accountName = result.data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
            if (!accountName.isNullOrBlank()) {
                val clean = accountName.trim().lowercase()
                prefs.edit().putString("user_google_email", clean).apply()
                onSignInSuccess(clean)
            }
        }
        refreshDetectedAccounts()
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isLoading = false
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            val email = account.email
            if (!email.isNullOrBlank()) {
                val clean = email.trim().lowercase()
                prefs.edit().putString("user_google_email", clean).apply()
                onSignInSuccess(clean)
            } else {
                errorMessage = "Google Account did not return an email address."
            }
        } catch (e: ApiException) {
            Log.e("GoogleSignIn", "Play Services sign-in code: ${e.statusCode}", e)
            try {
                val intent = AccountManager.newChooseAccountIntent(
                    null, null, arrayOf("com.google"), null, null, null, null
                )
                accountChooserLauncher.launch(intent)
            } catch (ex: Exception) {
                errorMessage = "Google Sign-In was cancelled or not configured."
            }
        }
    }

    fun launchDirectGoogleSignIn() {
        isLoading = true
        errorMessage = null
        try {
            googleSignInClient.signOut().addOnCompleteListener {
                val signInIntent: Intent = googleSignInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
            }
        } catch (e: Exception) {
            isLoading = false
            try {
                val intent = AccountManager.newChooseAccountIntent(
                    null, null, arrayOf("com.google"), null, null, null, null
                )
                accountChooserLauncher.launch(intent)
            } catch (ex: Exception) {
                errorMessage = "Unable to start sign in: ${e.message}"
            }
        }
    }

    // Regional Artistic Palette
    val saffronGold = Color(0xFFFFB300)
    val warmTerracotta = Color(0xFFFF7043)
    val deepEmerald = Color(0xFF00C853)
    val richIndigo = Color(0xFF3D5AFE)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0B0914),
                        Color(0xFF141124),
                        Color(0xFF0D121B)
                    )
                )
            )
    ) {
        // Subtle Regional Folk-Art & Sacred Geometry Background Motif
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val strokeColor = saffronGold.copy(alpha = 0.045f)

            // Radiating concentric artistic rings from top-center
            drawCircle(
                color = strokeColor,
                radius = w * 0.42f,
                center = Offset(w * 0.5f, h * 0.16f)
            )
            drawCircle(
                color = strokeColor,
                radius = w * 0.62f,
                center = Offset(w * 0.5f, h * 0.16f)
            )
            drawCircle(
                color = strokeColor,
                radius = w * 0.84f,
                center = Offset(w * 0.5f, h * 0.16f)
            )

            // Traditional diamond / rhombus weave motifs across corners
            val diamondSize = 36f
            for (i in 0..4) {
                val x = 24f + i * 48f
                val y = h - 60f
                drawCircle(color = richIndigo.copy(alpha = 0.035f), radius = diamondSize, center = Offset(x, y))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Artistic Branded Header & Logo Emblem
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Soft Radiant Medallion (No harsh white block!)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .shadow(16.dp, CircleShape, spotColor = saffronGold.copy(alpha = 0.35f))
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(
                                    saffronGold,
                                    warmTerracotta,
                                    deepEmerald,
                                    richIndigo,
                                    saffronGold
                                )
                            )
                        )
                        .padding(3.dp) // Outer ornamental border
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color(0xFF1E1A2C))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.smaran_logo),
                            contentDescription = "Smaran Logo",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(14.dp))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title with Sanskrit & Regional Heritage Honor
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Smaran",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "स्मरण",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = saffronGold.copy(alpha = 0.85f),
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Dementia Care & Regional Heritage Companion",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = saffronGold.copy(alpha = 0.95f),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "AI Neuro-Telemetry • Cultural Reminiscence • Safe GPS Radar",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Artistic Regional Heritage & Clinical Sentinel Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161324)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(
                            listOf(
                                saffronGold.copy(alpha = 0.6f),
                                warmTerracotta.copy(alpha = 0.5f),
                                richIndigo.copy(alpha = 0.6f)
                            )
                        ),
                        width = 1.2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Traditional ornamental badge header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("✦", fontSize = 11.sp, color = saffronGold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "REGIONAL HERITAGE & COGNITIVE SENTINEL",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = saffronGold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("✦", fontSize = 11.sp, color = saffronGold)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Rooted in rich regional traditions and clinical neuroscience. Preserving cherished memories, family connections, and 24/7 patient boundary safety.",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.80f),
                            textAlign = TextAlign.Center,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 4 Artistic Regional Feature Chips (2x2 Grid)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Chip 1: Regional Memories
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(saffronGold.copy(alpha = 0.12f))
                                    .border(1.dp, saffronGold.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "🌸 Regional Roots",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = saffronGold,
                                    maxLines = 1
                                )
                            }

                            // Chip 2: Clinical Neuro-Care
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(richIndigo.copy(alpha = 0.15f))
                                    .border(1.dp, richIndigo.copy(alpha = 0.40f), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "🧠 Neuro-Games",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF82B1FF),
                                    maxLines = 1
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Chip 3: Geofence Radar
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(deepEmerald.copy(alpha = 0.12f))
                                    .border(1.dp, deepEmerald.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "🛡️ GPS Sentinel",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = deepEmerald,
                                    maxLines = 1
                                )
                            }

                            // Chip 4: Home Safe Zone
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(warmTerracotta.copy(alpha = 0.12f))
                                    .border(1.dp, warmTerracotta.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "🏡 Take Me Home",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = warmTerracotta,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // Apple iOS Central Sign-In Squircle Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161324)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(22.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 0.15f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    ),
                    width = 1.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Sign In",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Detected accounts 1-tap select list
                    if (detectedAccounts.isNotEmpty()) {
                        detectedAccounts.take(2).forEach { acc ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF0F0D1B))
                                    .border(1.dp, Color(0xFF26213A), RoundedCornerShape(14.dp))
                                    .clickable {
                                        prefs.edit().putString("user_google_email", acc).apply()
                                        onSignInSuccess(acc)
                                    }
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(richIndigo, saffronGold)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        acc.take(1).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 15.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(acc, fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                                    Text("1-Tap Sign In", fontSize = 10.sp, color = deepEmerald, fontWeight = FontWeight.Medium)
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White.copy(alpha = 0.4f))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Apple-styled Google Sign-In Button
                    Button(
                        onClick = { launchDirectGoogleSignIn() },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoogleColors.Blue
                        ),
                        shape = RoundedCornerShape(14.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Signing In...",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("G", fontWeight = FontWeight.Black, color = GoogleColors.Blue, fontSize = 15.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    if (detectedAccounts.isNotEmpty()) "Choose Another Account" else "Continue with Google",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }

                    AnimatedVisibility(visible = errorMessage != null) {
                        errorMessage?.let { msg ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                color = IosColors.SystemRed.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, IosColors.SystemRed)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = IosColors.SystemRed, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(msg, color = IosColors.SystemRed, fontSize = 11.sp, lineHeight = 15.sp)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
