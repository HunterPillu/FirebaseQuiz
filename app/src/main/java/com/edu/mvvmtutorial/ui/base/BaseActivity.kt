package com.edu.mvvmtutorial.ui.base

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.edu.mvvmtutorial.R
import com.edu.mvvmtutorial.data.api.FirebaseApi
import com.edu.mvvmtutorial.data.model.Invite
import com.edu.mvvmtutorial.data.model.User
import com.edu.mvvmtutorial.ui.callbacks.FragmentEventListener
import com.edu.mvvmtutorial.ui.firebase.FirebaseData
import com.edu.mvvmtutorial.utils.Const
import com.edu.mvvmtutorial.utils.CustomLog
import com.edu.mvvmtutorial.utils.Utils
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
    private val TAG = BaseActivity::class.java.name

    //protected lateinit var connectionLiveData: ConnectionLiveData
    private var callRef: DocumentReference? = null


    fun initParent() {
        //automatic initialize Firebase database for game event listener : Game-Invitation
        //connectionLiveData = ConnectionLiveData(this)
        firebaseInitGame()

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
        //check for valid invite

        //can invite if player offline
        if (!Const.CAN_REQUEST_IF_OFFLINE && !opponent.online) {
            showMsg(this, getString(R.string.pq_user_offline, opponent.name))
            return
        }

        //check if player is already in game
        if (opponent.status == Const.STATUS_IN_GAME) {
            showMsg(this, getString(R.string.pq_user_in_game, opponent.name))
            return
        }

        if (opponent.status == Const.STATUS_INVITATION_RECEIVED
            && Utils.hasInvitationExpired(opponent.ts)
        ) {
            showMsg(this, getString(R.string.pq_user_invited, opponent.name))
            return
        }

        //check if player is already in game
        if (opponent.status == Const.STATUS_WAITING &&
            Utils.hasInvitationExpired(opponent.ts)
        ) {
            showMsg(this, getString(R.string.pq_user_waiting, opponent.name))
            return
        }

        FirebaseData.setItem(opponent)
        // set my game status to "WAITING"
        updateDbValue(
            hashMapOf<String, Any?>().apply {
                this["status"] = Const.STATUS_WAITING
                this["opponentId"] = ""
                this["ts"] = Utils.getCurrentTimeInMillis()
            },
            FirebaseData.getPlayerReference(FirebaseData.myID)
        )

        //set opponent game status to "IDLE"
        //save current user id to opponent's room so that he knows who is inviting him
        updateDbValue(
            hashMapOf<String, Any?>().apply {
                this["status"] = Const.STATUS_INVITATION_RECEIVED
                this["opponentId"] = FirebaseData.myID
                this["ts"] = Utils.getCurrentTimeInMillis()
            },
            FirebaseData.getPlayerReference(opponent.uid)
        )

        //onDisconnect will be called if activity got destroyed : in that case , remove all game status of opponent
        //todo : remove opponent data on disconnect
        //FirebaseData.getRoomIdReference(opponent.uid).onDisconnect().removeValue()
        //VideoCallActivity.startCall(this, item.first)
    }

    //override fun getConnectionObject(): ConnectionLiveData = connectionLiveData


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
        if (null == Firebase.auth.currentUser) {
            return
        }
        // initialize Firebase variables
        FirebaseData.init()
        //listen for game invitation
        callRef = FirebaseData.getPlayerReference(FirebaseData.myID)
    }

    private val callListener = object : EventListener<DocumentSnapshot> {
        override fun onEvent(snapshot: DocumentSnapshot?, e: FirebaseFirestoreException?) {
            if (e != null) {
                CustomLog.e(TAG, "Listen failed.", e)
                return
            }

            if (snapshot != null && snapshot.exists()) {
                CustomLog.d(TAG, "Current data: ${snapshot.data}")
                val invitation = snapshot.toObject(Invite::class.java)
                if (null != invitation && invitation.opponentId.isNotEmpty())
                    receiveVideoCall(invitation.opponentId)
                //callRef?.delete()
            } else {
                CustomLog.d(TAG, "Current data: null")
            }
        }
    }
}