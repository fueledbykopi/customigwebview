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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.focusgram.client.data.InstagramSection
import com.focusgram.client.ui.theme.LocalFocusThemeTokens

private data class NavTabItem(
    val section: InstagramSection,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private fun buildNavTabs(showReels: Boolean): List<NavTabItem> {
    val tabs = mutableListOf(
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
        )
    )
    if (showReels) {
        tabs.add(
            NavTabItem(
                section = InstagramSection.REELS,
                label = "Reels",
                selectedIcon = FocusIcons.ReelsPlayFilled,
                unselectedIcon = FocusIcons.ReelsPlayOutline
            )
        )
    }
    tabs.add(
        NavTabItem(
            section = InstagramSection.DIRECT,
            label = "Pesan",
            selectedIcon = FocusIcons.PaperplaneFilled,
            unselectedIcon = FocusIcons.PaperplaneOutline
        )
    )
    if (!showReels) {
        tabs.add(
            NavTabItem(
                section = InstagramSection.ACTIVITY,
                label = "Aktivitas",
                selectedIcon = FocusIcons.BellFilled,
                unselectedIcon = FocusIcons.BellOutline
            )
        )
    }
    tabs.add(
        NavTabItem(
            section = InstagramSection.PROFILE,
            label = "Profil",
            selectedIcon = FocusIcons.PersonFilled,
            unselectedIcon = FocusIcons.PersonOutline
        )
    )
    return tabs
}

/**
 * Latest iOS 26 Liquid-Glass floating capsule dock (optional — OFF by default).
 * Dynamically includes the Reels tab when the user enables Reels.
 */
@Composable
fun FocusBottomNavBar(
    currentSection: InstagramSection,
    isDarkTheme: Boolean,
    showReels: Boolean,
    onSelectSection: (InstagramSection) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val tokens = LocalFocusThemeTokens.current
    val dockShape = RoundedCornerShape(tokens.dockCornerRadius)
    val tabs = remember(showReels) { buildNavTabs(showReels) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Surface(
            shape = dockShape,
            color = tokens.glassElevated,
            border = BorderStroke(0.95.dp, tokens.specularBorderBrush),
            shadowElevation = if (isDarkTheme) 10.dp else 12.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(tokens.topSheenBrush)
                    .padding(horizontal = 6.dp, vertical = 5.dp)
            ) {
                tabs.forEach { tab ->
                    val selected = currentSection == tab.section
                    val scale by animateFloatAsState(
                        targetValue = if (selected) 1.05f else 1f,
                        animationSpec = spring(
                            dampingRatio = 0.62f,
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
                                alpha = if (isDarkTheme) 0.20f else 0.13f
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
                            .clip(RoundedCornerShape(tokens.dockCornerRadius - 6.dp))
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
