package com.prinkal.quiz.data.api

import com.prinkal.quiz.data.model.PQNotification
import com.prinkal.quiz.data.model.User
import com.prinkal.quiz.utils.Config
import org.json.JSONObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @GET("users")
    suspend fun getUsers(): List<User>

    @Headers(
        "Authorization: key=" + Config.PUSH_NOTIFICATION_SERVER_KEY,
        "Content-Type:application/json"
    )
    @POST("https://fcm.googleapis.com/fcm/send")
    suspend fun sendInviteNotification(@Body data: PQNotification): JSONObject?

}