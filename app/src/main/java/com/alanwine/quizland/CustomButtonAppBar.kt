package com.alanwine.quizland

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alanwine.quizland.navigation.TabScreen

data class BottomBarItem(
    val icon: ImageVector,
    val contentDescription: String,
    val label: String,
    val route: String
)

@Composable
fun BottomBarItemView(
    item: BottomBarItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconSize = 26.dp
    val textFontSize = 12.sp

    val tint: Color = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(iconSize),
            imageVector = item.icon,
            contentDescription = item.contentDescription,
            tint = tint
        )
        Spacer(Modifier.size(4.dp))
        Text(
            fontSize = textFontSize,
            text = item.label,
            color = tint,
            maxLines = 1
        )
    }
}

@Composable
fun CustomBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomBarItem(
            icon = Icons.Default.Home,
            contentDescription = "Home page",
            label = "Home",
            route = TabScreen.Home.route
        ),
        BottomBarItem(
            icon = Icons.Default.Explore,
            contentDescription = "Discover online sets",
            label = "Discover",
            route = TabScreen.Discover.route
        ),
        BottomBarItem(
            icon = Icons.Default.Add,
            contentDescription = "Create",
            label = "Create",
            route = TabScreen.Create.route
        ),
        BottomBarItem(
            icon = Icons.Default.FolderOpen,
            contentDescription = "Library",
            label = "Library",
            route = TabScreen.Library.route
        ),
        BottomBarItem(
            icon = Icons.Default.Groups,
            contentDescription = "Community sets",
            label = "Community",
            route = TabScreen.Community.route
        ),
    )

    Row(modifier = modifier.fillMaxSize()) {
        items.forEach { item ->
            BottomBarItemView(
                item = item,
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
