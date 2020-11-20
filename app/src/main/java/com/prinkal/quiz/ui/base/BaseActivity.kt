package com.prinkal.quiz.ui.base

import androidx.appcompat.app.AppCompatActivity
import com.prinkal.quiz.R
import com.prinkal.quiz.data.model.User
import com.prinkal.quiz.ui.callbacks.FragmentEventListener
import com.prinkal.quiz.ui.main.view.HomeActivity
import com.prinkal.quiz.ui.main.view.dialog.InvitationDialog
import com.prinkal.quiz.ui.main.viewmodel.HomeActivityViewModel
import com.prinkal.quiz.utils.CustomLog
import com.prinkal.quiz.utils.GameEvent
import com.prinkal.quiz.utils.showMsg


abstract class BaseActivity : AppCompatActivity(), FragmentEventListener {
    private val TAG = BaseActivity::class.java.name

    //protected lateinit var connectionLiveData: ConnectionLiveData

    abstract fun fetchViewModel(): HomeActivityViewModel


    override fun onCustomBackPressed() {
        val c = supportFragmentManager.backStackEntryCount
        CustomLog.d(TAG, "total entry = $c")
        if (c > 1) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
    }

    /* fun handleNavigation(ivBack: AppCompatImageView) {
         ivBack.setOnClickListener { onBackPressed() }
     }*/

    private fun getFragmentCount(): Int {
        return supportFragmentManager.backStackEntryCount
    }

    override fun openFragment(fragment: BaseFragment, canBeStacked: Boolean) {
        if (canBeStacked) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment, fragment::class.java.name)
                .addToBackStack(null)
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment, fragment::class.java.name)
                //.addToBackStack(null)
                .commit()
        }
    }

    private fun getFragmentAt(index: Int): BaseFragment? {
        return (if (getFragmentCount() > 0) supportFragmentManager.findFragmentByTag(
            Integer.toString(
                index
            )
        ) as BaseFragment? else null)
    }

    /*fun getCurrentFragment(): BaseFragment? {
        return getFragmentAt(getFragmentCount() - 1)
    }*/

    //invoked when user want to sent invite to opponent
    override fun onInviteOpponent(opponent: User, quizId: String) {
        fetchViewModel().onInviteOpponent(opponent, quizId)
    }

    //show invite dialog
    private fun showInvitedDialog(opponent: User, quizId: String) {
        val dialog = InvitationDialog.newInstance(opponent, quizId)
        dialog.show(supportFragmentManager, dialog.tag)
    }

    //show receivedInvite dialog
    private fun showInviteResponseDialog(uid: String) {
        val dialog = InvitationDialog.newInstance(uid)
        dialog.show(supportFragmentManager, dialog.tag)
    }

    //listen for invitation events INVITE_RECEIVED,INVITE_SENT,INVITE_ERROR
    fun setupInvitationObserver() {
        fetchViewModel().getInvitation().observe(this, {

            CustomLog.e(HomeActivity.TAG, "data observer ${it.status.name}")
            when (it.status) {
                GameEvent.INVITE_RECEIVED -> {
                    //invitation received,show dialog
                    //here keyId is opponentUserId
                    onInvitationReceived(it.keyId!!)
                }
                GameEvent.INVITE_SENT -> {
                    //invitation sent,show dialog
                    //here keyId is quizId
                    showInvitedDialog(it.data!!, it.keyId!!)
                    fetchViewModel().sendInviteNotification(
                        it.data.firebaseToken,
                        getString(R.string.pq_invitation_received),
                        getString(R.string.pq_invitation_received_push)
                    )
                }
                GameEvent.INVITE_ERROR -> {
                    //invitation sent,show dialog
                    //here keyId is quizId
                    showMsg(this, getString(it.errorMsg, it.keyId!!))
                }
            }
        })
    }

    //override fun getConnectionObject(): ConnectionLiveData = connectionLiveData

    private fun onInvitationReceived(key: String) {
        //show invitation dialog
        CustomLog.d(TAG, "receive Invitation from $key")
        showInviteResponseDialog(key)
    }

    override fun onResume() {
        super.onResume()
        fetchViewModel().addSnapshotListener()
    }

    override fun onPause() {
        fetchViewModel().removeSnapshotListener()
        super.onPause()
    }

    override fun firebaseInitGame() {
        fetchViewModel().firebaseInitGame()
    }

}