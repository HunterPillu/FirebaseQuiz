package com.prinkal.quiz.data.repository

import com.prinkal.quiz.data.api.ApiHelper

class MainRepository(private val apiHelper: ApiHelper) {

    suspend fun getUsers() = apiHelper.getUsers()
}