package com.prinkal.quiz.ui.main.view

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.prinkal.quiz.R
import com.prinkal.quiz.data.model.GameMeta
import com.prinkal.quiz.data.model.GameRoom
import com.prinkal.quiz.ui.base.BaseFragment
import com.prinkal.quiz.ui.base.QuizViewModelFactory
import com.prinkal.quiz.ui.callbacks.LottieAnimationListener
import com.prinkal.quiz.ui.firebase.FirebaseData
import com.prinkal.quiz.ui.main.viewmodel.ResultMultiQuizViewModel
import com.prinkal.quiz.utils.Const
import com.prinkal.quiz.utils.CustomLog
import com.prinkal.quiz.utils.Status
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.pq_result_multi_fragment.*
import kotlin.math.abs


class ResultMultiQuizFragment : BaseFragment() {

    private var isInvitationReceived: Boolean = false
    private lateinit var roomId: String
    private lateinit var viewModel: ResultMultiQuizViewModel

    companion object {

        internal val TAG = ResultMultiQuizViewModel::class.java.name

        fun newInstance(roomId: String): ResultMultiQuizFragment {
            val args = Bundle()
            args.putString("roomId", roomId)
            val fragment = ResultMultiQuizFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        roomId = arguments?.getString("roomId")!!
        isInvitationReceived = (FirebaseData.myID != roomId)

    }

    override fun onBackPressed() {
        //super.removedOnBackCallback()
        //requireActivity().supportFragmentManager.findFragmentByTag(HomeFragment::class.java.name)
        requireActivity().supportFragmentManager.beginTransaction()
            .remove(this)
            //.show(requireActivity().supportFragmentManager.findFragmentByTag(HomeFragment::class.java.name)!!)
            //.addToBackStack(null)
            .commit()
        super.onBackPressed()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.pq_result_multi_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupViewModel()
        setupRoomObserver()
    }

    private fun setupUI() {
        tvTitle.text = getString(R.string.pq_result)
        lavBg.addAnimatorListener(object : LottieAnimationListener() {
            override fun onAnimationEnd(animation: Animator?) {
                lavBg.visibility = GONE
            }
        })

        ivBack.setOnClickListener {
            onBackPressed()
        }
        bHome.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRoomObserver() {
        viewModel.getRoom().observe(viewLifecycleOwner, {

            CustomLog.e(TAG, "Room observer ${it.status.name}")
            when (it.status) {
                Status.SUCCESS -> {
                    onRoomDataUpdated(it.data!!)
                }
                Status.LOADING -> {
                    pbMain.visibility = VISIBLE
                    cvHeader.visibility = GONE
                    cvWonBy.visibility = GONE
                    cBottom.visibility = GONE
                }
                else -> {
                    //idle
                }
            }
        })
    }

    private fun onRoomDataUpdated(room: GameRoom) {

        showHideView()
        var myGameMeta = room.playerA!!
        var oppGameMeta = room.playerB!!
        if (isInvitationReceived) {
            myGameMeta = room.playerB!!
            oppGameMeta = room.playerA!!
        }

        if (room.status == Const.STATUS_IN_GAME && oppGameMeta.status == Const.STATUS_FINISHED) {
            viewModel.updateRoomStatusToFinished()
        }

        if (room.status == Const.STATUS_ABANDONED && oppGameMeta.status == Const.STATUS_ABANDONED) {
            viewModel.updateRoomStatusToFinished()
        }

        updateGameStatsUI(myGameMeta)
        if (Const.STATUS_FINISHED == room.status) {

            tvScoreA.text = getString(R.string.pq_your_score_, myGameMeta.score)
            tvScoreB.text = getString(R.string.pq_opp_score, oppGameMeta.name, oppGameMeta.score)

            val diffScore = myGameMeta.score - oppGameMeta.score
            val winStatus = diffScore > 0
            tvDiffScore.text = "${abs(diffScore)}"
            if (winStatus) {
                tvCongrats.text = getString(R.string.pq_congratulation)
                tvWonBy.text = getString(R.string.pq_won_by_user_, oppGameMeta.name)
                lavCongrats.setAnimationFromUrl("https://assets7.lottiefiles.com/packages/lf20_LoU6vj.json")
                //lavCongrats.setAnimation(R.raw.roger)
                lavHeader.setAnimationFromUrl("https://assets9.lottiefiles.com/packages/lf20_u4yrau.json")
            } else {
                tvCongrats.text = getString(R.string.pq_oops)
                tvWonBy.text = getString(R.string.pq_lose_by_user_, oppGameMeta.name)
                lavCongrats.setAnimation(R.raw.sad_emogi)
                lavHeader.setAnimationFromUrl("https://assets9.lottiefiles.com/packages/lf20_u4yrau.json")
            }
            pbWait.visibility = GONE
            lavCongrats.visibility = VISIBLE
            lavHeader.visibility = VISIBLE
            if (!lavCongrats.isAnimating) {
                lavHeader.playAnimation()
                lavCongrats.playAnimation()
            }
            lavBg.visibility = VISIBLE
            if (!lavBg.isAnimating) {
                lavBg.setAnimationFromUrl("https://assets7.lottiefiles.com/datafiles/T11VsOdRDtsaJlw/data.json")
                lavBg.playAnimation()
            }

        }
    }

    private fun showHideView() {
        if (pbMain.visibility == VISIBLE) {
            pbMain.visibility = GONE
            cvHeader.visibility = VISIBLE
            cvWonBy.visibility = VISIBLE
            cBottom.visibility = VISIBLE
        }
    }

    private fun updateGameStatsUI(gameMeta: GameMeta) {
        tvTotalQues.text = "${gameMeta.totalQuestion}"
        tvAttempted.text = "${gameMeta.totalAttempted}"
        tvCorrected.text = "${gameMeta.totalCorrect}"
        tvIncorrect.text = "${gameMeta.totalIncorrect}"
        tvLifeline.text = "${gameMeta.lifelineUsed}"
        tvDuration.text = "${gameMeta.totalTime}"
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            QuizViewModelFactory(roomId)
        ).get(ResultMultiQuizViewModel::class.java)
    }

    fun clearStack() {
        //Here we are clearing back stack fragment entries
        val fragmanager = requireActivity().supportFragmentManager
        val backStackEntry: Int = fragmanager.backStackEntryCount
        if (backStackEntry > 0) {
            for (i in 0 until backStackEntry) {
                fragmanager.popBackStackImmediate()
            }
        }

        //Here we are removing all the fragment that are shown here
        if (fragmanager.getFragments().size > 0
        ) {
            for (i in 0 until fragmanager.fragments.size) {
                val mFragment = fragmanager.fragments.get(i)
                if (mFragment != null) {
                    fragmanager.beginTransaction().remove(mFragment).commit()
                }
            }
        }
    }


}