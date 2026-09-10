package net.kibotu.geofencerelay.features.ai.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.kibotu.geofencerelay.features.ai.ui.theme.IosColors
import net.kibotu.geofencerelay.features.ai.ui.theme.IosDimensions

/**
 * High-fidelity Apple iOS Assistive Access Springboard Tile.
 * Directly replicates the layout, dimensions, and typography of the reference photo.
 */
@Composable
fun IosSpringboardCard(
    title: String,
    icon: ImageVector,
    iconBgColor: Color,
    iconTint: Color = Color.White,
    customIconContent: (@Composable () -> Unit)? = null,
    badgeText: String? = null,
    badgeIcon: ImageVector? = null,
    badgeColor: Color = IosColors.SystemBlue,
    statusSubtitle: String? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 400f),
        label = "cardPressScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(IosDimensions.CardCornerRadius))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = if (badgeText != null) 10.dp else 0.dp),
            shape = RoundedCornerShape(IosDimensions.CardCornerRadius),
            colors = CardDefaults.cardColors(containerColor = IosColors.SecondarySystemGroupedBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = IosDimensions.CardElevation),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.verticalGradient(
                    listOf(IosColors.CardBorder, IosColors.CardBorder.copy(alpha = 0.6f))
                ),
                width = 1.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Large iOS Squircle App Icon
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(RoundedCornerShape(IosDimensions.IconSquircleCornerRadius))
                        .background(iconBgColor)
                        .border(
                            width = 0.5.dp,
                            color = Color(0x22000000),
                            shape = RoundedCornerShape(IosDimensions.IconSquircleCornerRadius)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (customIconContent != null) {
                        customIconContent()
                    } else {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = iconTint,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bold Clean iOS SF Pro Label
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = IosColors.LabelPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )

                if (statusSubtitle != null) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = statusSubtitle,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = IosColors.LabelSecondary,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
        }

        // Apple iOS Assistive Access Pill Badge (e.g. "🔔 New" badge from reference photo)
        if (badgeText != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .clip(RoundedCornerShape(IosDimensions.PillCornerRadius))
                    .background(badgeColor)
                    .border(1.5.dp, Color.White, RoundedCornerShape(IosDimensions.PillCornerRadius))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (badgeIcon != null) {
                        Icon(
                            imageVector = badgeIcon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = badgeText,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.3.sp
                    )
                }
            }
        }
    }
}
