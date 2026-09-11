package net.kibotu.geofencerelay.features.ai.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.kibotu.geofencerelay.features.ai.localization.MultilingualManager
import net.kibotu.geofencerelay.features.ai.reminiscence.MemoryCard
import net.kibotu.geofencerelay.features.ai.reminiscence.ReminiscenceManager
import net.kibotu.geofencerelay.features.ai.ui.components.IosBackPillButton
import net.kibotu.geofencerelay.features.ai.ui.theme.GoogleColors
import net.kibotu.geofencerelay.features.ai.ui.theme.IosColors
import net.kibotu.geofencerelay.features.ai.ui.theme.IosDimensions

/**
 * Apple iOS Photos-styled Reminiscence Memory Vault.
 * Provides autobiographical memory recall cards and allows family members to upload custom questions.
 */
@Composable
fun MemoryVaultPanel(
    selectedLanguageCode: String = "en",
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var cards by remember { mutableStateOf(ReminiscenceManager.loadAllCards(context)) }
    var currentCardIndex by remember { mutableStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var showAffirmation by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    val currentCard = if (cards.isNotEmpty()) cards[currentCardIndex.coerceIn(0, cards.size - 1)] else null

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = MultilingualManager.tr("memory_title", selectedLanguageCode),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = IosColors.LabelPrimary
                    )
                    Text(
                        text = MultilingualManager.tr("memory_subtitle", selectedLanguageCode),
                        fontSize = 12.sp,
                        color = IosColors.LabelSecondary
                    )
                }

                // Family Upload Question Button
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = GoogleColors.Blue),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(MultilingualManager.tr("btn_add_memory", selectedLanguageCode), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (currentCard != null) {
                // Main Memory Card
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
                        // Category Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(IosDimensions.PillCornerRadius))
                                .background(IosColors.SystemIndigo.copy(alpha = 0.12f))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${currentCard.iconEmoji} ${currentCard.category}",
                                color = IosColors.SystemIndigo,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = currentCard.iconEmoji,
                            fontSize = 48.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = currentCard.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = IosColors.LabelPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = currentCard.question,
                            fontSize = 14.sp,
                            color = IosColors.LabelPrimary,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )

                        if (currentCard.cueText.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(IosColors.SystemGroupedBackground)
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "💡 Clue: ${currentCard.cueText}",
                                    fontSize = 12.sp,
                                    color = IosColors.LabelSecondary,
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 4 Answer Options
                        currentCard.options.forEachIndexed { optIdx, optText ->
                            val isSelected = selectedOptionIndex == optIdx
                            val isCorrect = optIdx == currentCard.correctIndex
                            val showFeedback = showAffirmation

                            val optBgColor = when {
                                showFeedback && isCorrect -> GoogleColors.Green.copy(alpha = 0.2f)
                                showFeedback && isSelected && !isCorrect -> GoogleColors.Red.copy(alpha = 0.15f)
                                isSelected -> GoogleColors.Blue.copy(alpha = 0.15f)
                                else -> IosColors.SystemGroupedBackground
                            }

                            val optBorderColor = when {
                                showFeedback && isCorrect -> GoogleColors.Green
                                showFeedback && isSelected && !isCorrect -> GoogleColors.Red
                                isSelected -> GoogleColors.Blue
                                else -> IosColors.CardBorder
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp)
                                    .clickable(enabled = !showAffirmation) {
                                        selectedOptionIndex = optIdx
                                        showAffirmation = true
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = optBgColor),
                                border = androidx.compose.foundation.BorderStroke(1.dp, optBorderColor)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${('A' + optIdx)}.",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = IosColors.LabelPrimary
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = optText,
                                        fontSize = 13.sp,
                                        color = IosColors.LabelPrimary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (showFeedback && isCorrect) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GoogleColors.Green, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        }

                        // Affirmation & Next Button
                        if (showAffirmation) {
                            Spacer(modifier = Modifier.height(14.dp))
                            val wasCorrect = selectedOptionIndex == currentCard.correctIndex
                            Text(
                                text = if (wasCorrect) "🎉 Wonderful memory! You remembered correctly."
                                else "❤️ Beautiful memory. That's always close to our hearts.",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (wasCorrect) GoogleColors.Green else GoogleColors.Blue,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    selectedOptionIndex = null
                                    showAffirmation = false
                                    currentCardIndex = (currentCardIndex + 1) % cards.size
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoogleColors.Blue),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(46.dp)
                            ) {
                                Text(MultilingualManager.tr("mv_next_memory", selectedLanguageCode), fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            } else {
                // Empty State: Invite family members to add the first question from scratch!
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(IosDimensions.CardCornerRadius),
                    colors = CardDefaults.cardColors(containerColor = IosColors.SecondarySystemGroupedBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(GoogleColors.Blue.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👨‍👩‍👧‍👦", fontSize = 38.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = MultilingualManager.tr("lbl_no_memories", selectedLanguageCode),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = IosColors.LabelPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = MultilingualManager.tr("lbl_no_memories_desc", selectedLanguageCode),
                            fontSize = 13.sp,
                            color = IosColors.LabelSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = MultilingualManager.tr("mv_default_themes", selectedLanguageCode),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = IosColors.LabelPrimary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        ReminiscenceManager.defaultThemes.forEach { theme ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(theme.second, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(theme.first, fontSize = 13.sp, color = IosColors.LabelPrimary, fontWeight = FontWeight.Medium)
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = GoogleColors.Blue),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(MultilingualManager.tr("mv_add_first", selectedLanguageCode), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                        }
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

        // Family Custom Memory Upload Dialog
        if (showAddDialog) {
            FamilyAddMemoryDialog(
                selectedLanguageCode = selectedLanguageCode,
                onDismiss = { showAddDialog = false },
                onSave = { newCard ->
                    ReminiscenceManager.saveCustomCard(context, newCard)
                    cards = ReminiscenceManager.loadAllCards(context)
                    currentCardIndex = cards.size - 1
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
private fun FamilyAddMemoryDialog(
    selectedLanguageCode: String = "en",
    onDismiss: () -> Unit,
    onSave: (MemoryCard) -> Unit
) {
    val themes = ReminiscenceManager.defaultThemes
    var selectedThemeIdx by remember { mutableStateOf(0) }
    var title by remember { mutableStateOf("") }
    var question by remember { mutableStateOf("") }
    var hint by remember { mutableStateOf("") }
    var opt1 by remember { mutableStateOf("") }
    var opt2 by remember { mutableStateOf("") }
    var opt3 by remember { mutableStateOf("") }
    var opt4 by remember { mutableStateOf("") }
    var correctOptIdx by remember { mutableStateOf(0) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val tfColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedContainerColor = Color(0xFFF2F2F7),
        unfocusedContainerColor = Color(0xFFF2F2F7),
        focusedBorderColor = GoogleColors.Blue,
        unfocusedBorderColor = IosColors.CardBorder,
        cursorColor = GoogleColors.Blue
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(MultilingualManager.tr("mv_upload_title", selectedLanguageCode), fontWeight = FontWeight.Bold, fontSize = 17.sp, color = IosColors.LabelPrimary)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(MultilingualManager.tr("mv_select_theme", selectedLanguageCode), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IosColors.LabelPrimary)
                Spacer(modifier = Modifier.height(6.dp))

                // Theme Chips
                Column {
                    themes.forEachIndexed { idx, pair ->
                        val isSel = selectedThemeIdx == idx
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) GoogleColors.Blue.copy(alpha = 0.12f) else Color.Transparent)
                                .clickable { selectedThemeIdx = idx }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(pair.second, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = pair.first,
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) GoogleColors.Blue else IosColors.LabelPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(MultilingualManager.tr("mv_title_label", selectedLanguageCode)) },
                    placeholder = { Text(MultilingualManager.tr("mv_title_hint", selectedLanguageCode), color = Color.Gray) },
                    singleLine = true,
                    colors = tfColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = question,
                    onValueChange = { question = it },
                    label = { Text(MultilingualManager.tr("mv_question_label", selectedLanguageCode)) },
                    placeholder = { Text(MultilingualManager.tr("mv_question_hint", selectedLanguageCode), color = Color.Gray) },
                    colors = tfColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = hint,
                    onValueChange = { hint = it },
                    label = { Text(MultilingualManager.tr("mv_cue_label", selectedLanguageCode)) },
                    placeholder = { Text(MultilingualManager.tr("mv_cue_hint", selectedLanguageCode), color = Color.Gray) },
                    colors = tfColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(MultilingualManager.tr("mv_options_title", selectedLanguageCode), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IosColors.LabelPrimary)
                Spacer(modifier = Modifier.height(6.dp))

                listOf(
                    Pair(opt1, { s: String -> opt1 = s }),
                    Pair(opt2, { s: String -> opt2 = s }),
                    Pair(opt3, { s: String -> opt3 = s }),
                    Pair(opt4, { s: String -> opt4 = s })
                ).forEachIndexed { i, pair ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                        RadioButton(
                            selected = correctOptIdx == i,
                            onClick = { correctOptIdx = i }
                        )
                        OutlinedTextField(
                            value = pair.first,
                            onValueChange = pair.second,
                            placeholder = { Text("Option ${i + 1}", color = Color.Gray) },
                            singleLine = true,
                            colors = tfColors,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(errorMsg!!, color = IosColors.SystemRed, fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank() || question.isBlank() || opt1.isBlank() || opt2.isBlank()) {
                        errorMsg = "Please fill in title, question, and at least 2 options."
                    } else {
                        val chosenTheme = themes[selectedThemeIdx]
                        val options = listOf(opt1, opt2, opt3, opt4).filter { it.isNotBlank() }
                        val card = MemoryCard(
                            id = "custom_${System.currentTimeMillis()}",
                            title = title.trim(),
                            category = chosenTheme.first,
                            question = question.trim(),
                            cueText = hint.trim(),
                            iconEmoji = chosenTheme.second,
                            options = options,
                            correctIndex = correctOptIdx.coerceIn(0, options.size - 1),
                            isCustom = true
                        )
                        onSave(card)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoogleColors.Blue),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(MultilingualManager.tr("mv_save", selectedLanguageCode), color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(MultilingualManager.tr("mv_cancel", selectedLanguageCode), color = IosColors.LabelSecondary)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}
