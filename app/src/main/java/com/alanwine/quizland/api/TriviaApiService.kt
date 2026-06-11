package com.alanwine.quizland.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface TriviaApiService {

    @GET("api_category.php")
    suspend fun getCategories(): CategoryResponse

    @GET("api.php")
    suspend fun getQuestions(
        @Query("category") categoryId: Int,
        @Query("amount") amount: Int = 10,
        @Query("type") type: String = "multiple"
    ): QuestionResponse
}

object TriviaApi {

    private const val BASE_URL = "https://opentdb.com/"

    val service: TriviaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TriviaApiService::class.java)
    }
}
