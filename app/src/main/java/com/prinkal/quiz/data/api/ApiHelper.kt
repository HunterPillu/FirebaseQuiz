package com.prinkal.quiz.data.api

import com.prinkal.quiz.data.model.PQNotification

class ApiHelper(private val apiService: ApiService) {


    suspend fun getUsers() = apiService.getUsers()
    suspend fun sendInviteNotification(data: PQNotification) = apiService.sendInviteNotification(data)

}