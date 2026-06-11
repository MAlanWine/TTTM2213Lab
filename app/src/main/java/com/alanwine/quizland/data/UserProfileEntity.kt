package com.alanwine.quizland.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Single-row table: the one local user always lives at id = 0.
@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 0,
    val nickname: String,
    val bio: String,
    val email: String
)
