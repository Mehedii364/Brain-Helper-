package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.VioletHighlight
import com.example.ui.theme.VioletPrimary
import com.example.viewmodel.ScreenTab

@Composable
fun ObsidianBottomNavigation(
    currentTab: ScreenTab,
    onTabSelected: (ScreenTab) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("obsidian_bottom_nav"),
        containerColor = ObsidianSurface,
        contentColor = TextPrimary,
        tonalElevation = 0.dp
    ) {
        val items = listOf(
            Triple(ScreenTab.HOME, R.string.nav_home, Pair(Icons.Filled.Home, Icons.Outlined.Home)),
            Triple(ScreenTab.TASKS, R.string.nav_tasks, Pair(Icons.Filled.Checklist, Icons.Outlined.Checklist)),
            Triple(ScreenTab.CHAT, R.string.nav_chat, Pair(Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome)),
            Triple(ScreenTab.MEMORY, R.string.nav_memory, Pair(Icons.Filled.Psychology, Icons.Outlined.Psychology)),
            Triple(ScreenTab.SETTINGS, R.string.nav_settings, Pair(Icons.Filled.Settings, Icons.Outlined.Settings))
        )

        items.forEach { (tab, labelRes, iconPair) ->
            val isSelected = currentTab == tab
            val icon = if (isSelected) iconPair.first else iconPair.second

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = stringResource(id = labelRes),
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = labelRes),
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = VioletHighlight,
                    selectedTextColor = VioletHighlight,
                    indicatorColor = Color(0x337367F0), // Subtle violet pill
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                ),
                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
            )
        }
    }
}
