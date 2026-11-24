package com.focusgram.client.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.focusgram.client.data.InstagramSection

private data class NavTabItem(
    val section: InstagramSection,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val navTabs = listOf(
    NavTabItem(
        section = InstagramSection.HOME,
        label = "Beranda",
        selectedIcon = FocusIcons.HomeFilled,
        unselectedIcon = FocusIcons.HomeOutline
    ),
    NavTabItem(
        section = InstagramSection.SEARCH,
        label = "Cari",
        selectedIcon = FocusIcons.SearchFilled,
        unselectedIcon = FocusIcons.SearchOutline
    ),
    NavTabItem(
        section = InstagramSection.DIRECT,
        label = "Pesan",
        selectedIcon = FocusIcons.PaperplaneFilled,
        unselectedIcon = FocusIcons.PaperplaneOutline
    ),
    NavTabItem(
        section = InstagramSection.ACTIVITY,
        label = "Aktivitas",
        selectedIcon = FocusIcons.BellFilled,
        unselectedIcon = FocusIcons.BellOutline
    ),
    NavTabItem(
        section = InstagramSection.PROFILE,
        label = "Profil",
        selectedIcon = FocusIcons.PersonFilled,
        unselectedIcon = FocusIcons.PersonOutline
    )
)

/**
 * Latest iOS Liquid-Glass inspired floating capsule dock.
 * Uses lightweight translucent surface + specular gradient + hairline border
 * (zero GPU RenderEffect blur overhead for 60-120fps & low memory).
 */
@Composable
fun FocusBottomNavBar(
    currentSection: InstagramSection,
    isDarkTheme: Boolean,
    onSelectSection: (InstagramSection) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val dockShape = RoundedCornerShape(28.dp)

    val glassBase = if (isDarkTheme) {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.90f)
    } else {
        Color.White.copy(alpha = 0.92f)
    }
    val glassBorder = if (isDarkTheme) {
        Color.White.copy(alpha = 0.14f)
    } else {
        Color.Black.copy(alpha = 0.08f)
    }
    val topSpecular = if (isDarkTheme) {
        Brush.verticalGradient(
            colors = listOf(Color.White.copy(alpha = 0.08f), Color.Transparent)
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(Color.White.copy(alpha = 0.70f), Color.Transparent)
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Surface(
            shape = dockShape,
            color = glassBase,
            border = BorderStroke(0.6.dp, glassBorder),
            shadowElevation = if (isDarkTheme) 6.dp else 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(topSpecular)
                    .padding(horizontal = 6.dp, vertical = 5.dp)
            ) {
                navTabs.forEach { tab ->
                    val selected = currentSection == tab.section
                    val scale by animateFloatAsState(
                        targetValue = if (selected) 1.04f else 1f,
                        animationSpec = spring(
                            dampingRatio = 0.65f,
                            stiffness = Spring.StiffnessMediumLow
                        ),
                        label = "tab_scale"
                    )
                    val tint by animateColorAsState(
                        targetValue = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        animationSpec = tween(durationMillis = 160),
                        label = "tab_tint"
                    )
                    val pillColor by animateColorAsState(
                        targetValue = if (selected) {
                            MaterialTheme.colorScheme.primary.copy(
                                alpha = if (isDarkTheme) 0.16f else 0.10f
                            )
                        } else {
                            Color.Transparent
                        },
                        animationSpec = tween(durationMillis = 180),
                        label = "tab_pill"
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(22.dp))
                            .background(pillColor)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onSelectSection(tab.section)
                            }
                            .padding(vertical = 6.dp, horizontal = 2.dp)
                            .scale(scale)
                    ) {
                        Icon(
                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                            contentDescription = tab.label,
                            tint = tint,
                            modifier = Modifier.size(21.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tab.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
                            ),
                            color = tint,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
