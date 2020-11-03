package com.edu.mvvmtutorial.data.model

import com.google.gson.annotations.SerializedName

data class User(

    @SerializedName("name")
    var name: String = "",
    @SerializedName("email")
    var email: String = "",

    var uid: String = "",
    var photoUrl: String? = null,
    var firebaseToken: String? = null,
    var online: Boolean = false
)