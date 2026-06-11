package com.alanwine.quizland.screens.profile

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alanwine.quizland.data.AppDatabase
import com.alanwine.quizland.data.ProfileDao
import com.alanwine.quizland.data.UserProfileEntity
import kotlinx.coroutines.launch

class UserProfileViewModel(private val dao: ProfileDao) : ViewModel() {

    var profile by mutableStateOf(
        UserProfile(
            nickname = "Alan",
            bio = "Mobile programming student @ UKM",
            email = "waalanwine@outlook.com"
        )
    )
        private set

    init {
        // Load the saved profile from Room once at startup, so edits
        // survive not just rotation but full app restarts.
        viewModelScope.launch {
            dao.get()?.let {
                profile = UserProfile(nickname = it.nickname, bio = it.bio, email = it.email)
            }
        }
    }

    fun updateNickname(value: String) {
        profile = profile.copy(nickname = value)
        persist()
    }

    fun updateBio(value: String) {
        profile = profile.copy(bio = value)
        persist()
    }

    fun updateEmail(value: String) {
        profile = profile.copy(email = value)
        persist()
    }

    private fun persist() {
        val p = profile
        viewModelScope.launch {
            dao.upsert(UserProfileEntity(nickname = p.nickname, bio = p.bio, email = p.email))
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val dao = AppDatabase.getInstance(context).profileDao()
                    return UserProfileViewModel(dao) as T
                }
            }
    }
}
