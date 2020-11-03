package com.edu.mvvmtutorial.data.api

import com.edu.mvvmtutorial.data.model.User
import io.reactivex.Single
import retrofit2.http.GET

interface ApiService {

    @GET("users")
    suspend fun getUsers(): List<User>

}