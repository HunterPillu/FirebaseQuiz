package com.edu.mvvmtutorial.data.model

data class User(

    var name: String = "",
    var email: String = "",
    var uid: String = "",
    var photoUrl: String? = null,
    var firebaseToken: String? = null,
    var online: Boolean = false
)