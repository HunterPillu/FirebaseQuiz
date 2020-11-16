package com.prinkal.quiz.ui.firebase

import com.google.firebase.auth.FirebaseAuth


object FirebaseData {

    // my UID : current user id
    var myID: String = ""

    fun init() {
        if (myID.isNotEmpty()) {
            // Firebase data is already initailzed
            return
        }
        val auth = FirebaseAuth.getInstance()
        auth.currentUser?.let {
            myID = it.uid
        }
    }
}