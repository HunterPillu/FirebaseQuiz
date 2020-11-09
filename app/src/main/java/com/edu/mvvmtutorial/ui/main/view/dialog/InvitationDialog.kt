package com.edu.mvvmtutorial.ui.main.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.edu.mvvmtutorial.R
import com.edu.mvvmtutorial.data.model.GameRoom
import com.edu.mvvmtutorial.data.model.User
import com.edu.mvvmtutorial.ui.base.InviteViewModelFactory
import com.edu.mvvmtutorial.ui.main.viewmodel.InviteViewModel
import com.edu.mvvmtutorial.utils.Const
import com.edu.mvvmtutorial.utils.CustomLog
import com.edu.mvvmtutorial.utils.Status
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.pq_bs_send_invite.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

class InvitationDialog : BottomSheetDialogFragment() {

    private lateinit var viewModel: InviteViewModel
    private var isInviteReceived: Boolean = false
    private lateinit var quizId: String
    private lateinit var player: User

    companion object {

        internal val TAG = InvitationDialog::class.java.simpleName

        fun newInstance(player: User, quizId: String): InvitationDialog {
            val frag = InvitationDialog()
            frag.player = player
            frag.quizId = quizId
            return frag
        }

        fun newInstance(opponentId: String): InvitationDialog {
            val frag = InvitationDialog()
            frag.player = User()
            frag.player.uid = opponentId
            frag.quizId = ""
            return frag
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.pq_bs_send_invite, container, false)

    }

    private fun setupUI() {
        isInviteReceived = quizId == ""
        if (isInviteReceived) {
            //show accept/reject dialog
            tvTitle.text = getString(R.string.pq_invitation_received)
            tvDesc.text = getString(R.string.pq_invitation_received_desc)
            bCancel.text = getString(R.string.pq_reject)
            bAccept.visibility = VISIBLE

            bAccept.setOnClickListener {
                viewModel.invitationAccepted()
            }
            bCancel.setOnClickListener {
                viewModel.invitationRejected()
                dialog?.dismiss()
            }

        } else {
            // show waiting dialog
            tvTitle.text = getString(R.string.pq_invitation_sent)
            tvDesc.text = getString(R.string.pq_invitation_sent_desc, player.name)
            bCancel.text = getString(R.string.pq_cancel)
            tvWait.visibility = VISIBLE
            bCancel.setOnClickListener {
                viewModel.cancelInvitation()
                dialog?.dismiss()
            }

        }
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        viewModel = ViewModelProvider(this, InviteViewModelFactory(player, quizId)).get(
            InviteViewModel::class.java
        )
        setupRoomObserver()
    }

    private fun setupRoomObserver() {

        viewModel.getRoom().observe(viewLifecycleOwner, {

            CustomLog.e(TAG, "course observer ${it.status.name}")
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { room ->
                        updateView(room)
                    }
                    tvDesc.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    dialog?.dismiss()
                }
                else -> {
                    //idle
                }
            }
        })


    }

    private fun updateView(room: GameRoom) {
        if (isInviteReceived) {
            //tvDesc.text=getString()
        } else {
            if (room.status == Const.STATUS_REJECT) {
                viewModel.invitationRejectedByOpponent()
                dialog?.dismiss()
            }
        }
    }

    private fun openNextScreen() {
        /*val intent: Intent = when {
            FirebaseAuth.getInstance().currentUser != null -> {
                Intent(context, MainActivity::class.java)
            }
            Const.SHOW_INTRO_ALWAYS -> {
                Intent(context, OnBoardingActivity::class.java)
            }
            else -> {
                Intent(context, LoginActivity::class.java)
            }
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)*/
    }


}