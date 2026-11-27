package com.focusgram.client.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusgram.client.ui.theme.IosSwitchTrackOffDark
import com.focusgram.client.ui.theme.IosSwitchTrackOffLight
import com.focusgram.client.ui.theme.LocalFocusThemeTokens

/**
 * Tactile iOS 26 Liquid Glass pill toggle switch (50x30 dp) with theme-aware active track
 * and spring thumb animation.
 */
@Composable
fun CupertinoSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val tokens = LocalFocusThemeTokens.current
    val trackColor by animateColorAsState(
        targetValue = if (checked) {
            tokens.activeSwitchTrack
        } else {
            if (isDark) IosSwitchTrackOffDark.copy(alpha = 0.85f) else IosSwitchTrackOffLight
        },
        animationSpec = tween(durationMillis = 180),
        label = "cupertino_switch_track"
    )
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 20.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = 0.70f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cupertino_switch_thumb"
    )

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier
            .size(width = 50.dp, height = 30.dp)
            .clip(CircleShape)
            .background(trackColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Switch
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onCheckedChange(!checked)
            }
            .padding(2.dp)
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(26.dp)
                .shadow(elevation = 3.dp, shape = CircleShape, clip = false)
                .background(Color.White, CircleShape)
        )
    }
}

/**
 * iOS Inset Grouped section header (uppercase, secondary label).
 */
@Composable
fun IosSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(
            letterSpacing = 0.7.sp,
            fontWeight = FontWeight.SemiBold
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(start = 14.dp, bottom = 7.dp)
    )
}

/**
 * Authentic iOS 26 Liquid Glass / themed frosted card container with diagonal specular
 * refraction border and subtle top glass sheen.
 */
@Composable
fun IosGroupedCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val tokens = LocalFocusThemeTokens.current
    val shape = RoundedCornerShape(tokens.cardCornerRadius)

    Surface(
        shape = shape,
        color = tokens.glassSurface,
        border = BorderStroke(
            width = 0.85.dp,
            brush = tokens.specularBorderBrush
        ),
        shadowElevation = if (tokens.isDark) 4.dp else 6.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(tokens.topSheenBrush),
            content = content
        )
    }
}

/**
 * Ultra-thin 0.5dp iOS separator line, optionally indented to align with row text.
 */
@Composable
fun IosHairlineDivider(
    startIndent: Dp = 0.dp,
    modifier: Modifier = Modifier
) {
    HorizontalDivider(
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.22f),
        modifier = modifier.padding(start = startIndent)
    )
}

/**
 * Glossy iOS Settings colored squircle icon badge (30dp) with top specular sheen.
 */
@Composable
fun IosIconBadge(
    icon: ImageVector,
    tint: Color,
    iconTint: Color = Color.White,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(31.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(tint)
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.28f), Color.Transparent)
                )
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(17.dp)
        )
    }
}

/**
 * Liquid Glass capsule segmented control with specular active glass pill.
 */
@Composable
fun <T> IosSegmentedControl(
    items: List<T>,
    selectedItem: T?,
    labelProvider: (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val tokens = LocalFocusThemeTokens.current

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.52f),
        border = BorderStroke(0.5.dp, tokens.glassBorderMid),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp)
        ) {
            items.forEach { item ->
                val isSelected = item == selectedItem
                Surface(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onSelect(item)
                    },
                    shape = RoundedCornerShape(11.dp),
                    color = if (isSelected) {
                        tokens.glassElevated
                    } else {
                        Color.Transparent
                    },
                    shadowElevation = if (isSelected) 2.dp else 0.dp,
                    border = if (isSelected) {
                        BorderStroke(0.7.dp, tokens.specularBorderBrush)
                    } else {
                        null
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp)
                    ) {
                        Text(
                            text = labelProvider(item),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                            ),
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

/**
 * Compact iOS Settings toggle row inside an IosGroupedCard.
 */
@Composable
fun IosToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isDark: Boolean,
    icon: ImageVector? = null,
    iconTint: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 14.dp, vertical = 11.dp)
    ) {
        if (icon != null) {
            IosIconBadge(icon = icon, tint = iconTint)
            Spacer(modifier = Modifier.width(12.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        CupertinoSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            isDark = isDark
        )
    }
}
