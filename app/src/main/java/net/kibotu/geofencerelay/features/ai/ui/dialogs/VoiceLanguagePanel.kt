package net.kibotu.geofencerelay.features.ai.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.kibotu.geofencerelay.features.ai.localization.MultilingualManager
import net.kibotu.geofencerelay.features.ai.ui.components.IosBackPillButton
import net.kibotu.geofencerelay.features.ai.ui.theme.IosColors
import net.kibotu.geofencerelay.features.ai.ui.theme.IosDimensions

/**
 * Apple iOS Settings-styled Language & Voice Guidance Panel.
 * Supports English, Hindi, Assamese, Mizo, Khasi, Manipuri, and Nagamese.
 */
@Composable
fun VoiceLanguagePanel(
    selectedLanguageCode: String,
    onLanguageSelected: (String) -> Unit,
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
                text = MultilingualManager.tr("tile_voice_title", selectedLanguageCode),
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = IosColors.LabelPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = MultilingualManager.tr("tile_voice_sub", selectedLanguageCode),
                fontSize = 13.sp,
                color = IosColors.LabelSecondary
            )

            Spacer(modifier = Modifier.height(18.dp))

            // iOS Grouped List of Supported Languages
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(IosDimensions.CardCornerRadius),
                colors = CardDefaults.cardColors(containerColor = IosColors.SecondarySystemGroupedBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    MultilingualManager.supportedLanguages.forEachIndexed { index, lang ->
                        val isSelected = lang.code == selectedLanguageCode

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onLanguageSelected(lang.code)
                                    val confirmMsg = MultilingualManager.getVoiceConfirmation(lang.code)
                                    MultilingualManager.speak(confirmMsg, lang.code)
                                }
                                .padding(horizontal = 18.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(lang.flagEmoji, fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = lang.nativeName,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = IosColors.LabelPrimary
                                    )
                                    Text(
                                        text = lang.displayName,
                                        fontSize = 12.sp,
                                        color = IosColors.LabelSecondary
                                    )
                                }
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = IosColors.SystemBlue,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        if (index < MultilingualManager.supportedLanguages.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 18.dp),
                                color = IosColors.Separator
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Voice Test Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(IosDimensions.CardCornerRadius),
                colors = CardDefaults.cardColors(containerColor = IosColors.SecondarySystemGroupedBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = MultilingualManager.tr("voice_preview_title", selectedLanguageCode),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = IosColors.LabelPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = MultilingualManager.getEncouragement("encouraging", selectedLanguageCode),
                        fontSize = 12.sp,
                        color = IosColors.LabelSecondary,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val msg = MultilingualManager.getEncouragement("celebratory", selectedLanguageCode)
                            MultilingualManager.speak(msg, selectedLanguageCode)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IosColors.SystemTeal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(MultilingualManager.tr("voice_preview_btn", selectedLanguageCode), fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Apple iOS Assistive Access Back Button
        IosBackPillButton(
            label = MultilingualManager.tr("btn_back", selectedLanguageCode),
            onClick = onBack
        )
    }
}
