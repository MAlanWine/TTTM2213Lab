package com.alanwine.quizland.data

import kotlinx.coroutines.flow.Flow

class FlashcardRepository(private val dao: FlashcardDao) {

    val allSets: Flow<List<FlashcardSet>> = dao.getAll()

    suspend fun add(set: FlashcardSet): Long = dao.insert(set)

    suspend fun addWithCards(set: FlashcardSet, cards: List<Flashcard>): Long =
        dao.insertSetWithCards(set, cards)

    fun observeSet(id: Long): Flow<FlashcardSet?> = dao.observeSet(id)

    suspend fun getSet(id: Long): FlashcardSet? = dao.getSet(id)

    fun observeCards(setId: Long): Flow<List<Flashcard>> = dao.observeCards(setId)

    suspend fun getCards(setId: Long): List<Flashcard> = dao.getCards(setId)

    suspend fun setRemoteId(id: Long, remoteId: String) = dao.setRemoteId(id, remoteId)

    suspend fun update(set: FlashcardSet) = dao.update(set)

    suspend fun delete(set: FlashcardSet) = dao.delete(set)
}
