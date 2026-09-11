package net.kibotu.geofencerelay.features.ai.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.kibotu.geofencerelay.features.ai.localization.MultilingualManager
import net.kibotu.geofencerelay.features.ai.model.CpsAssessmentResult
import net.kibotu.geofencerelay.features.ai.ui.components.IosBackPillButton
import net.kibotu.geofencerelay.features.ai.ui.theme.IosColors
import net.kibotu.geofencerelay.features.ai.ui.theme.IosDimensions

/**
 * Apple iOS Health-styled Cognitive Assessment Dashboard.
 * Accurately displays unassessed state if no games have been played yet today.
 */
@Composable
fun CognitiveHealthPanel(
    assessment: CpsAssessmentResult?,
    selectedLanguageCode: String,
    onLaunchGame: () -> Unit,
    onBack: () -> Unit
) {
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
                text = MultilingualManager.tr("tile_score_title", selectedLanguageCode),
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = IosColors.LabelPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = MultilingualManager.tr("health_subtitle", selectedLanguageCode),
                fontSize = 13.sp,
                color = IosColors.LabelSecondary
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (assessment == null) {
                // Unassessed State Card (No fake score shown!)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(IosDimensions.CardCornerRadius),
                    colors = CardDefaults.cardColors(containerColor = IosColors.SecondarySystemGroupedBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(IosColors.SystemPink.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = IosColors.SystemPink,
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = MultilingualManager.tr("lbl_untested", selectedLanguageCode),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = IosColors.LabelPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = MultilingualManager.tr("lbl_untested_desc", selectedLanguageCode),
                            fontSize = 13.sp,
                            color = IosColors.LabelSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 19.sp
                        )

                        Spacer(modifier = Modifier.height(22.dp))

                        Button(
                            onClick = onLaunchGame,
                            colors = ButtonDefaults.buttonColors(containerColor = IosColors.SystemOrange),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Icon(Icons.Default.SportsEsports, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = MultilingualManager.tr("btn_start_test", selectedLanguageCode),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            } else {
                // Calculated CPS Score Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(IosDimensions.CardCornerRadius),
                    colors = CardDefaults.cardColors(containerColor = IosColors.SecondarySystemGroupedBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(130.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(IosColors.SystemPink.copy(alpha = 0.15f), Color.Transparent)
                                    )
                                )
                                .border(6.dp, IosColors.SystemPink, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${assessment.cpsScore.toInt()}",
                                    fontSize = 42.sp,
                                    fontWeight = FontWeight.Black,
                                    color = IosColors.SystemPink
                                )
                                Text(
                                    text = MultilingualManager.tr("health_cps_score", selectedLanguageCode),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IosColors.LabelSecondary,
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${assessment.functionalCognitiveAge.toInt()} yrs",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IosColors.SystemBlue
                                )
                                Text(MultilingualManager.tr("health_cog_age", selectedLanguageCode), fontSize = 11.sp, color = IosColors.LabelSecondary)
                            }
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(32.dp)
                                    .background(IosColors.Separator)
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${assessment.biologicalAge} yrs",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IosColors.LabelPrimary
                                )
                                Text(MultilingualManager.tr("health_bio_age", selectedLanguageCode), fontSize = 11.sp, color = IosColors.LabelSecondary)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sub-domain Breakdown Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(IosDimensions.CardCornerRadius),
                    colors = CardDefaults.cardColors(containerColor = IosColors.SecondarySystemGroupedBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = MultilingualManager.tr("health_subdomains", selectedLanguageCode),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = IosColors.LabelPrimary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        SubScoreRow(MultilingualManager.tr("sub_memory", selectedLanguageCode), assessment.subScores.memoryRetentionIndex, IosColors.SystemBlue)
                        SubScoreRow(MultilingualManager.tr("sub_executive", selectedLanguageCode), assessment.subScores.executiveFunctionIndex, IosColors.SystemPurple)
                        SubScoreRow(MultilingualManager.tr("sub_reaction", selectedLanguageCode), assessment.subScores.reactionLatencyScore, IosColors.SystemGreen)
                        SubScoreRow(MultilingualManager.tr("sub_autobio", selectedLanguageCode), assessment.subScores.autobiographicalReminiscence, IosColors.SystemOrange)
                        SubScoreRow(MultilingualManager.tr("sub_recovery", selectedLanguageCode), assessment.subScores.errorRecoveryRate, IosColors.SystemTeal)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 30 & 90 Days Projections Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(IosDimensions.CardCornerRadius),
                    colors = CardDefaults.cardColors(containerColor = IosColors.SecondarySystemGroupedBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = IosColors.SystemGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${MultilingualManager.tr("health_forecast", selectedLanguageCode)}: ${assessment.trajectoryStatus}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = IosColors.LabelPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(MultilingualManager.tr("health_30days", selectedLanguageCode), fontSize = 12.sp, color = IosColors.LabelSecondary)
                                Text("${assessment.projectedCps30Days} CPS", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = IosColors.SystemGreen)
                            }
                            Column {
                                Text(MultilingualManager.tr("health_90days", selectedLanguageCode), fontSize = 12.sp, color = IosColors.LabelSecondary)
                                Text("${assessment.projectedCps90Days} CPS", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = IosColors.SystemBlue)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = assessment.caregiverReminiscencePlan,
                            fontSize = 12.sp,
                            color = IosColors.LabelSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        IosBackPillButton(
            label = MultilingualManager.tr("btn_back", selectedLanguageCode),
            onClick = onBack
        )
    }
}

@Composable
private fun SubScoreRow(label: String, score: Double, color: Color) {
    Column(modifier = Modifier.padding(vertical = 5.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = IosColors.LabelPrimary)
            Text("${score.toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (score / 100.0).toFloat().coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = IosColors.SystemGroupedBackground
        )
    }
}
