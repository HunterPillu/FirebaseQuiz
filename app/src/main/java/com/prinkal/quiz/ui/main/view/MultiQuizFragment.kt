package com.prinkal.quiz.ui.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.prinkal.quiz.R
import com.prinkal.quiz.data.model.GameRoom
import com.prinkal.quiz.ui.base.QuizViewModelFactory
import com.prinkal.quiz.ui.main.viewmodel.MultiQuizViewModel
import com.prinkal.quiz.utils.Const
import com.prinkal.quiz.utils.CustomLog
import com.prinkal.quiz.utils.Status
import kotlinx.android.synthetic.main.pq_body_multi_quiz.*
import kotlinx.android.synthetic.main.pq_fragment_multi_quiz.*
import kotlinx.android.synthetic.main.pq_header_multi_quiz.*

class MultiQuizFragment : Fragment() {

    private lateinit var roomId: String
    private lateinit var viewModel: MultiQuizViewModel

    companion object {
        internal val TAG = MultiQuizFragment::class.java.name
        fun newInstance(roomId: String): MultiQuizFragment {
            val args = Bundle()
            args.putString("roomId", roomId)
            val fragment = MultiQuizFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        roomId = arguments?.getString("roomId")!!
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.pq_fragment_multi_quiz, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupRoomObserver()
        setupQuestionObserver()
    }


    private fun setupRoomObserver() {

        /* viewModel.getElapsedTime().observe(viewLifecycleOwner, {
             tvWait.text = getString(R.string.pq_wait, it)
             pbProgress.progress =
                 100 - ((it * 100 * 1000) / Config.INVITATION_EXPIRE_TIME).toInt()
         })*/


        viewModel.getRoom().observe(viewLifecycleOwner, {

            CustomLog.e(TAG, "Room observer ${it.status.name}")
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { room ->
                        onRoomDataUpdated(room)
                    }
                }
                Status.ERROR -> {

                }
                Status.LOADING -> {
                    pbLoader.visibility = View.VISIBLE
                    cvHeader.visibility = View.GONE
                    cvBody.visibility = View.GONE
                }
                else -> {
                    //idle
                }
            }
        })


    }

    private fun setupQuestionObserver() {

        viewModel.getQuestion().observe(viewLifecycleOwner, {

            CustomLog.d(TAG, "Question observer")

            tvQuestion.text = it.question
            tvOptA.text = it.optA
            tvOptB.text = it.optB
            tvOptC.text = it.optC
            tvOptD.text = it.optD
        })
    }


    private fun onRoomDataUpdated(room: GameRoom) {

        //STATUS_PREPARED means both the player are ready, so start the quiz
        if (room.status == Const.STATUS_PREPARED) {

            //show view
            pbLoader.visibility = View.GONE
            cvHeader.visibility = View.VISIBLE
            cvBody.visibility = View.VISIBLE

            if (viewModel.hasInvitationReceived()) {
                tvPlayerA.text = room.playerBName
                tvPlayerB.text = room.playerAName
            } else {
                tvPlayerA.text = room.playerAName
                tvPlayerB.text = room.playerBName
            }

            //start quiz
            viewModel.startFirstQuestion()
        } /*else if (room.status == Const.STATUS_PREPARING) {

        }*/
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            QuizViewModelFactory(roomId)
        ).get(MultiQuizViewModel::class.java)
    }

}