package com.alanwine.quizland.cloud

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alanwine.quizland.data.AppDatabase
import com.alanwine.quizland.data.Flashcard
import com.alanwine.quizland.data.FlashcardRepository
import com.alanwine.quizland.data.FlashcardSet
import kotlinx.coroutines.launch

// State machine for the QR-scan import flow on the Scan screen.
sealed interface QrImportState {
    data object Idle : QrImportState
    data object Fetching : QrImportState
    data class Success(val title: String, val cardCount: Int) : QrImportState
    data class Error(val message: String) : QrImportState
}

class CommunityViewModel(
    private val local: FlashcardRepository,
    private val cloud: CommunityRepository
) : ViewModel() {

    var communitySets by mutableStateOf<List<CommunitySet>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var loadError by mutableStateOf<String?>(null)
        private set

    // Doc ids imported during this session, used to flip the "Get" button.
    var importedIds by mutableStateOf<Set<String>>(emptySet())
        private set

    var qrImportState by mutableStateOf<QrImportState>(QrImportState.Idle)
        private set

    fun refresh() {
        viewModelScope.launch {
            isLoading = true
            loadError = null
            try {
                communitySets = cloud.fetchAll()
            } catch (e: Exception) {
                loadError = e.message ?: "Could not reach the community cloud."
            } finally {
                isLoading = false
            }
        }
    }

    // Saves a community set into the local Room library.
    fun import(set: CommunitySet) {
        viewModelScope.launch {
            local.addWithCards(
                FlashcardSet(
                    title = set.title,
                    description = set.description,
                    author = set.author,
                    cardCount = set.cards.size,
                    source = FlashcardSet.SOURCE_COMMUNITY,
                    remoteId = set.docId
                ),
                set.cards.map { Flashcard(setId = 0, question = it.question, answer = it.answer) }
            )
            importedIds = importedIds + set.docId
        }
    }

    // Uploads a local set (reusing the doc id if already shared) and hands
    // back the Firestore document id that the share QR code encodes.
    fun share(setId: Long, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val set = local.getSet(setId)
                if (set == null) {
                    onResult(null)
                    return@launch
                }
                if (set.remoteId != null) {
                    onResult(set.remoteId)
                    return@launch
                }
                val docId = cloud.upload(set, local.getCards(setId))
                local.setRemoteId(setId, docId)
                onResult(docId)
            } catch (e: Exception) {
                onResult(null)
            }
        }
    }

    fun importFromQr(content: String) {
        // Ignore further frames while one code is being handled.
        if (qrImportState != QrImportState.Idle) return
        if (!content.startsWith(QR_PREFIX)) {
            qrImportState = QrImportState.Error("Not a Quizland QR code.")
            return
        }
        val docId = content.removePrefix(QR_PREFIX)
        qrImportState = QrImportState.Fetching
        viewModelScope.launch {
            try {
                val set = cloud.fetchById(docId)
                if (set == null) {
                    qrImportState = QrImportState.Error("This set no longer exists in the community cloud.")
                } else {
                    local.addWithCards(
                        FlashcardSet(
                            title = set.title,
                            description = set.description,
                            author = set.author,
                            cardCount = set.cards.size,
                            source = FlashcardSet.SOURCE_COMMUNITY,
                            remoteId = set.docId
                        ),
                        set.cards.map { Flashcard(setId = 0, question = it.question, answer = it.answer) }
                    )
                    qrImportState = QrImportState.Success(set.title, set.cards.size)
                }
            } catch (e: Exception) {
                qrImportState = QrImportState.Error(e.message ?: "Network error while fetching the set.")
            }
        }
    }

    fun resetQrImport() {
        qrImportState = QrImportState.Idle
    }

    companion object {
        const val QR_PREFIX = "quizland://set/"

        fun factory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val dao = AppDatabase.getInstance(context).flashcardDao()
                    return CommunityViewModel(FlashcardRepository(dao), CommunityRepository()) as T
                }
            }
    }
}
