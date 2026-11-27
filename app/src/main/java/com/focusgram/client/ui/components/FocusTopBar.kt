package com.focusgram.client.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.focusgram.client.R
import com.focusgram.client.data.InstagramSection
import com.focusgram.client.ui.theme.LocalFocusThemeTokens

@Composable
fun FocusTopBar(
    section: InstagramSection,
    canGoBack: Boolean,
    isLoading: Boolean,
    loadingProgress: Int,
    activeFilterCount: Int,
    isDarkTheme: Boolean,
    customAppIcon: Bitmap?,
    onBackClick: () -> Unit,
    onTitleClick: () -> Unit,
    onFocusControlsClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onCopyLinkClick: () -> Unit,
    onShareLinkClick: () -> Unit,
    onOpenInBrowserClick: () -> Unit,
    onOpenSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val tokens = LocalFocusThemeTokens.current
    val customIconBitmap = remember(customAppIcon) { customAppIcon?.asImageBitmap() }

    val glassPillFill = if (isDarkTheme) {
        Color.White.copy(alpha = 0.09f)
    } else {
        Color.Black.copy(alpha = 0.055f)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(tokens.glassElevated)
            .background(tokens.topSheenBrush)
            .statusBarsPadding()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .padding(horizontal = 12.dp)
        ) {
            if (canGoBack && section == InstagramSection.OTHER) {
                IosCircleIconButton(
                    icon = FocusIcons.ChevronLeft,
                    contentDescription = stringResource(R.string.cd_back),
                    fillColor = glassPillFill,
                    onClick = onBackClick
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            // Title, optional custom app icon & section glass badge (tap to smooth-scroll top)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onTitleClick)
                    .padding(horizontal = 4.dp, vertical = 4.dp)
            ) {
                if (customIconBitmap != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(0.6.dp, tokens.specularBorderBrush),
                        modifier = Modifier.size(26.dp)
                    ) {
                        Image(
                            bitmap = customIconBitmap,
                            contentDescription = "Ikon Aplikasi",
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = "FocusGram",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(7.dp))
                Surface(
                    shape = CircleShape,
                    color = glassPillFill,
                    border = BorderStroke(0.5.dp, tokens.glassBorderMid)
                ) {
                    Text(
                        text = section.title,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Liquid Glass Focus Shield Capsule
            Surface(
                onClick = onFocusControlsClick,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(
                    alpha = if (isDarkTheme) 0.20f else 0.12f
                ),
                contentColor = MaterialTheme.colorScheme.primary,
                border = BorderStroke(
                    width = 0.7.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.36f)
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = FocusIcons.ShieldFilled,
                        contentDescription = stringResource(R.string.cd_focus_controls),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = activeFilterCount.toString(),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            IosCircleIconButton(
                icon = FocusIcons.Refresh,
                contentDescription = stringResource(R.string.cd_refresh),
                fillColor = glassPillFill,
                onClick = onRefreshClick
            )

            Spacer(modifier = Modifier.width(6.dp))

            Box {
                IosCircleIconButton(
                    icon = FocusIcons.Ellipsis,
                    contentDescription = stringResource(R.string.cd_more_options),
                    fillColor = glassPillFill,
                    onClick = { menuExpanded = true }
                )

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    shape = RoundedCornerShape(18.dp),
                    containerColor = tokens.glassSheet
                ) {
                    DropdownMenuItem(
                        text = { Text("Salin Tautan", style = MaterialTheme.typography.bodyLarge) },
                        leadingIcon = {
                            Icon(
                                imageVector = FocusIcons.Copy,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onCopyLinkClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Bagikan Halaman", style = MaterialTheme.typography.bodyLarge) },
                        leadingIcon = {
                            Icon(
                                imageVector = FocusIcons.Share,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onShareLinkClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Buka di Browser", style = MaterialTheme.typography.bodyLarge) },
                        leadingIcon = {
                            Icon(
                                imageVector = FocusIcons.Compass,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onOpenInBrowserClick()
                        }
                    )
                    IosHairlineDivider(modifier = Modifier.padding(vertical = 4.dp))
                    DropdownMenuItem(
                        text = { Text("Pengaturan & Tema", style = MaterialTheme.typography.bodyLarge) },
                        leadingIcon = {
                            Icon(
                                imageVector = FocusIcons.Gear,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onOpenSettingsClick()
                        }
                    )
                }
            }
        }

        // Ultra-slim iOS Safari-style progress bar or crisp luminous hairline
        AnimatedVisibility(
            visible = isLoading && loadingProgress in 1..99,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LinearProgressIndicator(
                progress = { loadingProgress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.Transparent,
                strokeCap = StrokeCap.Round
            )
        }
        if (!isLoading || loadingProgress !in 1..99) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.6.dp)
                    .background(tokens.specularBorderBrush)
            )
        }
    }
}

@Composable
private fun IosCircleIconButton(
    icon: ImageVector,
    contentDescription: String,
    fillColor: Color,
    onClick: () -> Unit
) {
    val tokens = LocalFocusThemeTokens.current
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = fillColor,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(0.65.dp, tokens.specularBorderBrush),
        modifier = Modifier.size(34.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
