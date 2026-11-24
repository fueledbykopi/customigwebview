package com.focusgram.client.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
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
        selectedIcon = Icons.Rounded.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    NavTabItem(
        section = InstagramSection.SEARCH,
        label = "Cari",
        selectedIcon = Icons.Rounded.Search,
        unselectedIcon = Icons.Outlined.Search
    ),
    NavTabItem(
        section = InstagramSection.DIRECT,
        label = "Pesan",
        selectedIcon = Icons.AutoMirrored.Rounded.Send,
        unselectedIcon = Icons.AutoMirrored.Outlined.Send
    ),
    NavTabItem(
        section = InstagramSection.ACTIVITY,
        label = "Aktivitas",
        selectedIcon = Icons.Rounded.Notifications,
        unselectedIcon = Icons.Outlined.Notifications
    ),
    NavTabItem(
        section = InstagramSection.PROFILE,
        label = "Profil",
        selectedIcon = Icons.Rounded.Person,
        unselectedIcon = Icons.Outlined.Person
    )
)

@Composable
fun FocusBottomNavBar(
    currentSection: InstagramSection,
    onSelectSection: (InstagramSection) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        navTabs.forEach { tab ->
            val selected = currentSection == tab.section
            NavigationBarItem(
                selected = selected,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onSelectSection(tab.section)
                },
                icon = {
                    Icon(
                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                        contentDescription = tab.label
                    )
                },
                label = {
                    Text(
                        text = tab.label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                alwaysShowLabel = true
            )
        }
    }
}
