package com.alanwine.quizland.api

import com.google.gson.annotations.SerializedName

// JSON shapes returned by https://opentdb.com/

data class TriviaCategory(
    val id: Int,
    val name: String
)

data class CategoryResponse(
    @SerializedName("trivia_categories") val triviaCategories: List<TriviaCategory>
)

data class TriviaQuestion(
    val category: String,
    val difficulty: String,
    val question: String,
    @SerializedName("correct_answer") val correctAnswer: String,
    @SerializedName("incorrect_answers") val incorrectAnswers: List<String>
)

data class QuestionResponse(
    // 0 = success, 5 = rate limited (1 request per 5 seconds per IP)
    @SerializedName("response_code") val responseCode: Int,
    val results: List<TriviaQuestion>
)
