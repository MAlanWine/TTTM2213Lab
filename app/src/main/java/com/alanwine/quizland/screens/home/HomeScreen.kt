package com.alanwine.quizland.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alanwine.quizland.CusTitle
import com.alanwine.quizland.CustomCard2
import com.alanwine.quizland.CustomEasyCard
import com.alanwine.quizland.data.FlashcardSetViewModel
import com.alanwine.quizland.screens.profile.UserProfileViewModel
import com.alanwine.quizland.unionVerticalPaddingValue

@Composable
fun HomeScreen(
    userProfileViewModel: UserProfileViewModel,
    flashcardSetViewModel: FlashcardSetViewModel,
    onSetClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val sectionSpacing = 46.dp
    val titleToContentSpacing = 14.dp

    val nickname = userProfileViewModel.profile.nickname
    val sets by flashcardSetViewModel.sets.collectAsState()
    val featured = sets.firstOrNull()

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = unionVerticalPaddingValue)
            .padding(top = 20.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(sectionSpacing)
    ) {
        // Greeting — pulls nickname from the shared UserProfileViewModel
        Column {
            Text(
                text = "Hello, $nickname",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.size(4.dp))
            Text(
                text = "${sets.size} sets in your library — keep learning!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Jump back in — featured set from shared ViewModel
        if (featured != null) {
            Column {
                CusTitle("Jump back in")
                Spacer(Modifier.size(titleToContentSpacing))
                Card(
                    modifier = Modifier
                        .clip(RoundedCornerShape(23.dp))
                        .clickable { onSetClick(featured.id) }
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(23.dp)
                ) {
                    Column(modifier = Modifier.padding(27.dp)) {
                        Text(
                            text = featured.title,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.size(36.dp))
                        LinearProgressIndicator(
                            progress = { 1.0f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(14.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = ProgressIndicatorDefaults.linearTrackColor,
                            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                        )
                        Spacer(Modifier.size(4.dp))
                        Text(text = "${featured.cardCount}/${featured.cardCount} cards sorted")
                        Spacer(Modifier.size(24.dp))
                        Button(
                            onClick = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            content = {
                                Text(
                                    text = "Start questions",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                        )
                    }
                }
            }
        }

        // Recents — list of latest sets
        Column {
            CusTitle("Recents")
            Spacer(Modifier.size(titleToContentSpacing))
            sets.take(3).forEach { set ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSetClick(set.id) }
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(16.dp),
                            contentDescription = "Cards",
                            imageVector = Icons.Default.Style,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(Modifier.size(10.dp))
                        Column {
                            Text(
                                text = set.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.size(2.dp))
                            Text(
                                text = "${set.cardCount} cards · by ${set.author}",
                                fontSize = 12.sp,
                            )
                        }
                    }
                }
            }
        }

        // For your next study session
        Column {
            CusTitle("For your next study session")
            Spacer(Modifier.size(titleToContentSpacing))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CustomCard2(
                    title = "Common Quizland questions",
                    cardCount = 15,
                    author = "Quizland",
                    studierCount = 112,
                    modifier = Modifier
                        .clip(RoundedCornerShape(23.dp))
                        .clickable {}
                )
                CustomCard2(
                    title = "Quizland basics",
                    cardCount = 11,
                    author = "Quizland",
                    studierCount = 497,
                    modifier = Modifier
                        .clip(RoundedCornerShape(23.dp))
                        .clickable {}
                )
                CustomCard2(
                    title = "Effective study strategies",
                    cardCount = 6,
                    author = "Quizland",
                    studierCount = 812,
                    modifier = Modifier
                        .clip(RoundedCornerShape(23.dp))
                        .clickable {}
                )
            }
        }

        // Try out these flashcard sets
        Column {
            CusTitle("Try out these flashcard sets")
            Spacer(Modifier.size(titleToContentSpacing))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CustomEasyCard(
                    title = "Quizland basics",
                    cardCount = 11,
                    author = "Quizland",
                    modifier = Modifier
                        .width(240.dp)
                        .clip(RoundedCornerShape(23.dp))
                        .clickable {}
                )
                CustomEasyCard(
                    title = "Common Quizland questions",
                    cardCount = 15,
                    author = "Quizland",
                    modifier = Modifier
                        .width(240.dp)
                        .clip(RoundedCornerShape(23.dp))
                        .clickable {}
                )
                CustomEasyCard(
                    title = "Effective study stratefies",
                    cardCount = 12,
                    author = "Quizland",
                    modifier = Modifier
                        .width(240.dp)
                        .clip(RoundedCornerShape(23.dp))
                        .clickable {}
                )
            }
        }
    }
}
