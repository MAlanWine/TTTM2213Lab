package com.alanwine.quizland.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(set: FlashcardSet): Long

    @Insert
    suspend fun insertCards(cards: List<Flashcard>)

    // Inserts the set and its cards atomically; cards get the generated set id.
    @Transaction
    suspend fun insertSetWithCards(set: FlashcardSet, cards: List<Flashcard>): Long {
        val setId = insert(set)
        insertCards(cards.map { it.copy(setId = setId) })
        return setId
    }

    @Update
    suspend fun update(set: FlashcardSet)

    @Delete
    suspend fun delete(set: FlashcardSet)

    @Query("SELECT * FROM flashcard_sets ORDER BY id DESC")
    fun getAll(): Flow<List<FlashcardSet>>

    @Query("SELECT * FROM flashcard_sets WHERE id = :id")
    fun observeSet(id: Long): Flow<FlashcardSet?>

    @Query("SELECT * FROM flashcard_sets WHERE id = :id")
    suspend fun getSet(id: Long): FlashcardSet?

    @Query("SELECT * FROM flashcards WHERE setId = :setId ORDER BY id")
    fun observeCards(setId: Long): Flow<List<Flashcard>>

    @Query("SELECT * FROM flashcards WHERE setId = :setId ORDER BY id")
    suspend fun getCards(setId: Long): List<Flashcard>

    @Query("UPDATE flashcard_sets SET remoteId = :remoteId WHERE id = :id")
    suspend fun setRemoteId(id: Long, remoteId: String)
}
