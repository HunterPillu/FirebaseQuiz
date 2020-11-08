package com.edu.mvvmtutorial.ui.firebase

import com.edu.mvvmtutorial.data.api.FirebaseApi
import com.edu.mvvmtutorial.data.model.User
import com.edu.mvvmtutorial.utils.Const
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


object FirebaseData {

    //opponent info injected
    private lateinit var item: User

    // my UID : current user id
    var myID: String = ""

    //private val database = Firebase.firestore.collection(Const.TABLE_ROOM)

    fun getPlayerReference(id: String) =
        Firebase.firestore.collection(Const.TABLE_USERS).document(id)


    fun init() {
        if (myID.isNotEmpty()) {
            // Firebase data is already initailzed
            return
        }
        val auth = FirebaseAuth.getInstance()
        auth.currentUser?.let {
            myID = it.uid
            // already handled online status on AppLifecycleObserver

            //database.getReference("$USERS/$myID/online").onDisconnect().setValue(false)
            //database.getReference("$USERS/$myID/online").setValue(true)

            //todo experimental value "onlineStatus" , it can be used to show last online time
            // database.getReference("$USERS/$myID/onlineStatus").onDisconnect().setValue("${SystemClock.currentThreadTimeMillis()}")
            //database.getReference("$USERS/$myID/onlineStatus").setValue("Online")
        }
    }

    /*fun clearRoomIdReference() {
        getRoomIdReference(item.uid).delete()
    }*/


    fun updateToken() {
        GlobalScope.launch(Dispatchers.IO) {
            FirebaseApi.updateFirebaseToken()
        }
    }

    fun setItem(item: User) {
        this.item = item
    }
}