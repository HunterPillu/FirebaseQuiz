package com.prinkal.quiz.data.model

data class User(

    var name: String = "",
    var email: String = "",
    var uid: String = "",
    var photoUrl: String? = null,
    var firebaseToken: String? = null,
    var online: Boolean = false,

    //invitation variables
    var opponentId: String = "",
    var status: Int = 0,
    var ts: Long = 0

) {

    fun fetchInvitation(): Invite = Invite(opponentId, status, ts)
}