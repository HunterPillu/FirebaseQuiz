package com.edu.mvvmtutorial.data.repository

import com.edu.mvvmtutorial.data.api.ApiHelper

class MainRepository(private val apiHelper: ApiHelper) {

    suspend fun getUsers() = apiHelper.getUsers()
}