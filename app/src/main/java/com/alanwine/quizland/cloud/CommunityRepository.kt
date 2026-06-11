package com.alanwine.quizland.cloud

import com.alanwine.quizland.data.Flashcard
import com.alanwine.quizland.data.FlashcardSet
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

data class CommunityCard(
    val question: String,
    val answer: String
)

data class CommunitySet(
    val docId: String,
    val title: String,
    val description: String,
    val author: String,
    val cards: List<CommunityCard>
)

class CommunityRepository {

    private val collection = Firebase.firestore.collection("community_sets")

    // Pushes a local set (and its cards) to Firestore; returns the new document id.
    suspend fun upload(set: FlashcardSet, cards: List<Flashcard>): String {
        val doc = hashMapOf(
            "title" to set.title,
            "description" to set.description,
            "author" to set.author,
            "cardCount" to cards.size,
            "cards" to cards.map { mapOf("question" to it.question, "answer" to it.answer) },
            "createdAt" to FieldValue.serverTimestamp()
        )
        return collection.add(doc).await().id
    }

    suspend fun fetchAll(): List<CommunitySet> =
        collection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(50)
            .get()
            .await()
            .documents
            .mapNotNull { it.toCommunitySet() }

    suspend fun fetchById(docId: String): CommunitySet? =
        collection.document(docId).get().await().toCommunitySet()

    private fun DocumentSnapshot.toCommunitySet(): CommunitySet? {
        val title = getString("title") ?: return null
        @Suppress("UNCHECKED_CAST")
        val rawCards = get("cards") as? List<Map<String, Any?>> ?: emptyList()
        return CommunitySet(
            docId = id,
            title = title,
            description = getString("description") ?: "",
            author = getString("author") ?: "unknown",
            cards = rawCards.map {
                CommunityCard(
                    question = it["question"] as? String ?: "",
                    answer = it["answer"] as? String ?: ""
                )
            }
        )
    }
}
