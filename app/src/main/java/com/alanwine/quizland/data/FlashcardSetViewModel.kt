package com.alanwine.quizland.data

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FlashcardSetViewModel(
    private val repository: FlashcardRepository
) : ViewModel() {

    val sets: StateFlow<List<FlashcardSet>> = repository.allSets
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun observeSet(id: Long): Flow<FlashcardSet?> = repository.observeSet(id)

    fun observeCards(setId: Long): Flow<List<Flashcard>> = repository.observeCards(setId)

    fun addSet(title: String, description: String, author: String, cardCount: Int) {
        viewModelScope.launch {
            repository.add(
                FlashcardSet(
                    title = title.trim(),
                    description = description.trim(),
                    author = author.ifBlank { "you" },
                    cardCount = cardCount
                )
            )
        }
    }

    fun update(set: FlashcardSet) {
        viewModelScope.launch { repository.update(set) }
    }

    fun delete(set: FlashcardSet) {
        viewModelScope.launch { repository.delete(set) }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val dao = AppDatabase.getInstance(context).flashcardDao()
                    return FlashcardSetViewModel(FlashcardRepository(dao)) as T
                }
            }
    }
}
