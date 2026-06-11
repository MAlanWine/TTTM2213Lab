package com.alanwine.quizland.screens.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.alanwine.quizland.api.TriviaCategory
import com.alanwine.quizland.api.TriviaQuestion
import com.alanwine.quizland.unionVerticalPaddingValue

@Composable
fun DiscoverScreen(
    discoverViewModel: DiscoverViewModel,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) { discoverViewModel.loadCategories() }

    val selected = discoverViewModel.selectedCategory

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = unionVerticalPaddingValue)
            .padding(top = 24.dp)
    ) {
        if (selected == null) {
            CategoryList(discoverViewModel)
        } else {
            QuestionList(discoverViewModel, selected)
        }
    }
}

@Composable
private fun CategoryList(viewModel: DiscoverViewModel) {
    Text(
        text = "Discover",
        style = MaterialTheme.typography.headlineMedium
    )
    Text(
        text = "Live quiz topics from the Open Trivia DB web API — pick one to preview its questions.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.size(16.dp))

    when {
        viewModel.isLoading && viewModel.categories.isEmpty() -> LoadingBox()
        viewModel.loadError != null && viewModel.categories.isEmpty() ->
            ErrorBox(viewModel.loadError!!) { viewModel.loadCategories() }
        else -> LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(viewModel.categories) { category ->
                CategoryRow(category) { viewModel.selectCategory(category) }
            }
            item { Spacer(Modifier.size(14.dp)) }
        }
    }
}

@Composable
private fun CategoryRow(category: TriviaCategory, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick),
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
                    imageVector = Icons.Default.Public,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.size(14.dp))
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

@Composable
private fun QuestionList(viewModel: DiscoverViewModel, category: TriviaCategory) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { viewModel.clearSelection() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back to categories"
            )
        }
        Text(
            text = category.name,
            style = MaterialTheme.typography.titleLarge
        )
    }
    Spacer(Modifier.size(8.dp))

    when {
        viewModel.isLoading -> LoadingBox()
        viewModel.loadError != null ->
            ErrorBox(viewModel.loadError!!) { viewModel.selectCategory(category) }
        else -> Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(viewModel.questions) { QuestionCard(it) }
                item { Spacer(Modifier.size(4.dp)) }
            }

            val imported = viewModel.importedSetTitle != null
            Button(
                onClick = { viewModel.importToLibrary() },
                enabled = !imported && viewModel.questions.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .height(56.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text(
                    text = if (imported) {
                        "Imported ✓ — check your Library"
                    } else {
                        "Import ${viewModel.questions.size} cards to Library"
                    },
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

@Composable
private fun QuestionCard(question: TriviaQuestion) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
            Text(
                text = question.question,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.size(6.dp))
            Text(
                text = "Answer: ${question.correctAnswer}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun LoadingBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorBox(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.size(8.dp))
        TextButton(onClick = onRetry) { Text("Retry") }
    }
}
