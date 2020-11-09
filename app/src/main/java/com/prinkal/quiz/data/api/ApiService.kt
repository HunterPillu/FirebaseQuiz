package com.prinkal.quiz.data.api

import com.prinkal.quiz.data.model.User
import retrofit2.http.GET

interface ApiService {

    @GET("users")
    suspend fun getUsers(): List<User>

}