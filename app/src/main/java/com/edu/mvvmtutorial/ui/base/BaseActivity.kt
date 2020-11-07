package com.edu.mvvmtutorial.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.edu.mvvmtutorial.R
import com.edu.mvvmtutorial.data.api.FirebaseApi
import com.edu.mvvmtutorial.data.model.GameRoom
import com.edu.mvvmtutorial.data.model.User
import com.edu.mvvmtutorial.ui.callbacks.FragmentEventListener
import com.edu.mvvmtutorial.ui.firebase.FirebaseData
import com.edu.mvvmtutorial.utils.ConnectionLiveData
import com.edu.mvvmtutorial.utils.Const
import com.edu.mvvmtutorial.utils.CustomLog
import com.edu.mvvmtutorial.utils.showMsg
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


open class BaseActivity : AppCompatActivity(), FragmentEventListener {
    private var dataListener: ListenerRegistration? = null
    private val TAG = "BaseActivity"

    private var callRef: DocumentReference? = null
    protected lateinit var connectionLiveData: ConnectionLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //automatic initialize Firebase database for game event listener : Game-Invitation
        connectionLiveData = ConnectionLiveData(this)
        if (null != Firebase.auth.currentUser) {
            firebaseInitGame()
        }
    }

    override fun onBackPressed() {
        val c = supportFragmentManager.backStackEntryCount
        CustomLog.d(TAG, "total entry = $c")
        if (c > 1) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
    }

    fun handleNavigation(ivBack: AppCompatImageView) {
        ivBack.setOnClickListener { onBackPressed() }
    }

    fun showSnackbar(stringRes: Int) {
        Snackbar.make(findViewById(R.id.root)!!, stringRes, Snackbar.LENGTH_LONG).show()
    }

    private fun getFragmentCount(): Int {
        return supportFragmentManager.backStackEntryCount
    }

    override fun openFragment(fragment: BaseFragment) {
        //this.fragment = fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment, Integer.toString(getFragmentCount()))
            .addToBackStack(null).commit()
    }

    private fun getFragmentAt(index: Int): BaseFragment? {
        return (if (getFragmentCount() > 0) supportFragmentManager.findFragmentByTag(
            Integer.toString(
                index
            )
        ) as BaseFragment? else null)
    }

    fun getCurrentFragment(): BaseFragment? {
        return getFragmentAt(getFragmentCount() - 1)
    }

    override fun updateToolbarTitle(title: String) {
        //handle this on child class
    }

    //Firebase game listeners

    override fun onInviteOpponent(opponent: User) {
        if (!Const.CAN_REQUEST_IF_OFFLINE && !opponent.online) {
            showSnackbar(R.string.user_offline)
            return
        }
        FirebaseData.setItem(opponent)
        // set my game status to "IN_GAME"
        updateDbValue(
            hashMapOf<String,Any?>().apply {
                this["status"]=Const.STATUS_IN_GAME
                this["id"]=""
            },
            FirebaseData.getRoomStatusReference(FirebaseData.myID)
        )

        //set opponent game status to "IDLE"
        //save current user id to opponent's room so that he knows who is inviting him
        updateDbValue(
            hashMapOf<String,Any?>().apply {
                this["status"]=Const.STATUS_IDLE
                this["id"]=FirebaseData.myID
            },
            FirebaseData.getRoomStatusReference(opponent.uid)
        )

        //onDisconnect will be called if activity got destroyed : in that case , remove all game status of opponent
        //todo : remove opponent data on disconnect
        //FirebaseData.getRoomIdReference(opponent.uid).onDisconnect().removeValue()
        //VideoCallActivity.startCall(this, item.first)
    }

    override fun getConnectionObject(): ConnectionLiveData = connectionLiveData


    fun updateDbValue(map: Map<String, Any?>, ref: DocumentReference) {
        GlobalScope.launch(Dispatchers.IO) {
            FirebaseApi.updateDbValue(map, ref)
        }
    }

    fun updateDbValue(field: String, value: Any, ref: DocumentReference) {
        val map = hashMapOf<String, Any?>()
        map[field] = value
        GlobalScope.launch(Dispatchers.IO) {
            FirebaseApi.updateDbValue(map, ref)
        }
    }

    fun receiveVideoCall(key: String) {
        //show invitation dialog
        showMsg(this, "receiveVideoCall $key")
        //VideoCallActivity.receiveCall(this, key)
    }

    /*override fun onStartCallClicked(item: Pair<String, User?>) {
        startVideoCall(item)
    }*/

    override fun onResume() {
        super.onResume()
        //if (null != Firebase.auth.currentUser)
        dataListener = callRef?.addSnapshotListener(callListener)
    }

    override fun onPause() {
        //if (null != Firebase.auth.currentUser)
        dataListener?.remove()
        super.onPause()

    }

    override fun firebaseInitGame() {
        // initialize Firebase variables
        FirebaseData.init()
        //listen for game invitation
        callRef = FirebaseData.getRoomIdReference(FirebaseData.myID)
    }

    private val callListener = object : EventListener<DocumentSnapshot> {
        override fun onEvent(snapshot: DocumentSnapshot?, e: FirebaseFirestoreException?) {
            if (e != null) {
                CustomLog.e(TAG, "Listen failed.", e)
                return
            }

            if (snapshot != null && snapshot.exists()) {
                CustomLog.d(TAG, "Current data: ${snapshot.data}")
                val room = snapshot.toObject(GameRoom::class.java)
                if (null != room && room.id.isNotEmpty())
                    receiveVideoCall(room.id)
                //callRef?.delete()
            } else {
                CustomLog.d(TAG, "Current data: null")
            }
        }
    }
}