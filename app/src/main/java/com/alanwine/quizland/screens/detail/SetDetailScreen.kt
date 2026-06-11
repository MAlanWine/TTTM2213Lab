package com.alanwine.quizland.screens.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.alanwine.quizland.cloud.CommunityViewModel
import com.alanwine.quizland.data.Flashcard
import com.alanwine.quizland.data.FlashcardSet
import com.alanwine.quizland.data.FlashcardSetViewModel
import com.alanwine.quizland.util.generateQrBitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetDetailScreen(
    setId: Long,
    flashcardSetViewModel: FlashcardSetViewModel,
    communityViewModel: CommunityViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val set by flashcardSetViewModel.observeSet(setId).collectAsState(initial = null)
    val cards by flashcardSetViewModel.observeCards(setId).collectAsState(initial = emptyList())

    var isSharing by remember { mutableStateOf(false) }
    var shareError by remember { mutableStateOf<String?>(null) }
    var qrDocId by remember { mutableStateOf<String?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = set?.title ?: "Set",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )

        val current = set
        if (current == null) {
            Text(
                text = "This set no longer exists.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(24.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item { SetInfoCard(current) }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = {
                                isSharing = true
                                shareError = null
                                communityViewModel.share(setId) { docId ->
                                    isSharing = false
                                    if (docId == null) {
                                        shareError = "Upload failed — check your internet connection."
                                    } else {
                                        qrDocId = docId
                                    }
                                }
                            },
                            enabled = !isSharing,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            )
                        ) {
                            if (isSharing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.QrCode2,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.size(8.dp))
                                Text("Share QR")
                            }
                        }
                        OutlinedButton(
                            onClick = { showDeleteConfirm = true },
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.size(8.dp))
                            Text("Delete")
                        }
                    }
                }

                shareError?.let { message ->
                    item {
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                if (cards.isEmpty()) {
                    item {
                        Text(
                            text = "No card contents yet — sets created by hand only store their info, while sets imported from Discover, Community or a QR code carry real cards.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                } else {
                    item {
                        Text(
                            text = "Cards",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    items(cards) { CardRow(it) }
                }

                item { Spacer(Modifier.size(14.dp)) }
            }
        }
    }

    qrDocId?.let { docId ->
        val qrBitmap = remember(docId) {
            generateQrBitmap(CommunityViewModel.QR_PREFIX + docId)
        }
        AlertDialog(
            onDismissRequest = { qrDocId = null },
            confirmButton = {
                TextButton(onClick = { qrDocId = null }) { Text("Done") }
            },
            title = { Text("Share this set") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "QR code for this set",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )
                    Spacer(Modifier.size(8.dp))
                    Text(
                        text = "Uploaded to the community cloud. A friend can scan this code from Library → Scan to import the set.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete this set?") },
            text = { Text("\"${set?.title}\" and its cards will be removed from your library.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        set?.let { flashcardSetViewModel.delete(it) }
                        onBack()
                    }
                ) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun SetInfoCard(set: FlashcardSet) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
            if (set.description.isNotBlank()) {
                Text(
                    text = set.description,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.size(6.dp))
            }
            Text(
                text = "${set.cardCount} cards · by ${set.author} · source: ${set.source}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CardRow(card: Flashcard) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
            Text(
                text = card.question,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.size(6.dp))
            Text(
                text = "Answer: ${card.answer}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
