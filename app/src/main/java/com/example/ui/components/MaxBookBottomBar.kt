package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainTab
import com.example.ui.theme.Slate400

@Composable
fun MaxBookBottomBar(
    selectedTab: MainTab,
    friendRequestsCount: Int,
    onTabSelected: (MainTab) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            thickness = 0.5.dp
        )
        NavigationBar(
            windowInsets = WindowInsets.navigationBars,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp
        ) {
            val tabs = listOf(
                MainTab.FEED,
                MainTab.WATCH,
                MainTab.MARKETPLACE,
                MainTab.GROUPS,
                MainTab.FRIENDS,
                MainTab.MENU
            )

            tabs.forEach { tab ->
                val isSelected = selectedTab == tab

                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onTabSelected(tab) },
                    modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}"),
                    icon = {
                        BadgedBox(
                            badge = {
                                if (tab == MainTab.FRIENDS && friendRequestsCount > 0) {
                                    Badge {
                                        Text(
                                            text = "$friendRequestsCount",
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = when (tab) {
                                    MainTab.FEED -> if (isSelected) Icons.Filled.Home else Icons.Outlined.Home
                                    MainTab.WATCH -> if (isSelected) Icons.Filled.OndemandVideo else Icons.Outlined.OndemandVideo
                                    MainTab.MARKETPLACE -> if (isSelected) Icons.Filled.Storefront else Icons.Outlined.Storefront
                                    MainTab.GROUPS -> if (isSelected) Icons.Filled.Groups else Icons.Outlined.Groups
                                    MainTab.FRIENDS -> if (isSelected) Icons.Filled.People else Icons.Outlined.PeopleOutline
                                    MainTab.MENU -> if (isSelected) Icons.Filled.Menu else Icons.Outlined.Menu
                                },
                                contentDescription = tab.title,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = tab.title,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400
                    )
                )
            }
        }
    }
}

