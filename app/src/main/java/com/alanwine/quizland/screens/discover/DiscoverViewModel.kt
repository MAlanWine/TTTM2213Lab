package com.alanwine.quizland.screens.discover

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alanwine.quizland.api.TriviaApi
import com.alanwine.quizland.api.TriviaCategory
import com.alanwine.quizland.api.TriviaQuestion
import com.alanwine.quizland.data.AppDatabase
import com.alanwine.quizland.data.Flashcard
import com.alanwine.quizland.data.FlashcardRepository
import com.alanwine.quizland.data.FlashcardSet
import kotlinx.coroutines.launch

class DiscoverViewModel(
    private val repository: FlashcardRepository
) : ViewModel() {

    var categories by mutableStateOf<List<TriviaCategory>>(emptyList())
        private set
    var selectedCategory by mutableStateOf<TriviaCategory?>(null)
        private set
    var questions by mutableStateOf<List<TriviaQuestion>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var loadError by mutableStateOf<String?>(null)
        private set

    // Title of the set just saved to Room, used for the "Imported ✓" banner.
    var importedSetTitle by mutableStateOf<String?>(null)
        private set

    fun loadCategories() {
        if (categories.isNotEmpty() || isLoading) return
        viewModelScope.launch {
            isLoading = true
            loadError = null
            try {
                categories = TriviaApi.service.getCategories().triviaCategories
            } catch (e: Exception) {
                loadError = "Couldn't load categories: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun selectCategory(category: TriviaCategory) {
        selectedCategory = category
        importedSetTitle = null
        questions = emptyList()
        viewModelScope.launch {
            isLoading = true
            loadError = null
            try {
                val response = TriviaApi.service.getQuestions(categoryId = category.id)
                when {
                    response.responseCode == 5 ->
                        loadError = "Rate limited — Open Trivia DB allows 1 request every 5 seconds. Try again shortly."
                    response.results.isEmpty() ->
                        loadError = "No questions available for this category."
                    else ->
                        questions = response.results.map { it.decodeHtml() }
                }
            } catch (e: Exception) {
                loadError = "Couldn't load questions: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun clearSelection() {
        selectedCategory = null
        questions = emptyList()
        loadError = null
        importedSetTitle = null
    }

    // Persists the fetched questions into Room as a new flashcard set,
    // so the live API data stays available offline.
    fun importToLibrary() {
        val category = selectedCategory ?: return
        val toImport = questions
        if (toImport.isEmpty()) return
        viewModelScope.launch {
            repository.addWithCards(
                FlashcardSet(
                    title = category.name,
                    description = "${toImport.size} trivia questions fetched live from Open Trivia DB.",
                    author = "Open Trivia DB",
                    cardCount = toImport.size,
                    source = FlashcardSet.SOURCE_API
                ),
                toImport.map { Flashcard(setId = 0, question = it.question, answer = it.correctAnswer) }
            )
            importedSetTitle = category.name
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val dao = AppDatabase.getInstance(context).flashcardDao()
                    return DiscoverViewModel(FlashcardRepository(dao)) as T
                }
            }
    }
}

// Open Trivia DB escapes text as HTML entities (&quot; etc.) — decode for display.
private fun TriviaQuestion.decodeHtml(): TriviaQuestion = copy(
    question = HtmlCompat.fromHtml(question, HtmlCompat.FROM_HTML_MODE_LEGACY).toString(),
    correctAnswer = HtmlCompat.fromHtml(correctAnswer, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
)
