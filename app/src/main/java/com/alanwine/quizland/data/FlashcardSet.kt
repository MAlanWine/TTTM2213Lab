package com.alanwine.quizland.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flashcard_sets")
data class FlashcardSet(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val description: String,
    val author: String,
    val cardCount: Int,
    val source: String = SOURCE_LOCAL,
    // Firestore document id once this set has been shared to the community.
    val remoteId: String? = null
) {
    companion object {
        const val SOURCE_LOCAL = "local"
        const val SOURCE_API = "api"
        const val SOURCE_COMMUNITY = "community"
    }
}
