package com.alanwine.quizland.screens.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.alanwine.quizland.cloud.CommunitySet
import com.alanwine.quizland.cloud.CommunityViewModel
import com.alanwine.quizland.unionVerticalPaddingValue

@Composable
fun CommunityScreen(
    communityViewModel: CommunityViewModel,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        if (communityViewModel.communitySets.isEmpty()) communityViewModel.refresh()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = unionVerticalPaddingValue)
            .padding(top = 24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Community",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { communityViewModel.refresh() }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh community sets"
                )
            }
        }
        Text(
            text = "Flashcard sets shared by everyone, synced from Firebase Firestore. Share yours from a set's detail page.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.size(16.dp))

        when {
            communityViewModel.isLoading && communityViewModel.communitySets.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            communityViewModel.loadError != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = communityViewModel.loadError!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.size(8.dp))
                    TextButton(onClick = { communityViewModel.refresh() }) { Text("Retry") }
                }
            }
            communityViewModel.communitySets.isEmpty() -> {
                Text(
                    text = "Nothing shared yet — be the first! Open one of your sets and tap Share.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 32.dp)
                )
            }
            else -> LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(communityViewModel.communitySets) { set ->
                    CommunityRow(
                        set = set,
                        imported = set.docId in communityViewModel.importedIds,
                        onGet = { communityViewModel.import(set) }
                    )
                }
                item { Spacer(Modifier.size(14.dp)) }
            }
        }
    }
}

@Composable
private fun CommunityRow(
    set: CommunitySet,
    imported: Boolean,
    onGet: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.size(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = set.title,
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(Modifier.size(2.dp))
                Text(
                    text = "${set.cards.size} cards · by ${set.author}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.size(10.dp))
            Button(
                onClick = onGet,
                enabled = !imported,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(if (imported) "Saved ✓" else "Get")
            }
        }
    }
}
