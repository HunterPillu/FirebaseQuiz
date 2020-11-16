package com.prinkal.quiz.ui.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.card.MaterialCardView
import com.prinkal.quiz.R
import com.prinkal.quiz.data.model.GameRoom
import com.prinkal.quiz.data.model.Question
import com.prinkal.quiz.ui.base.BaseFragment
import com.prinkal.quiz.ui.base.QuizViewModelFactory
import com.prinkal.quiz.ui.main.viewmodel.MultiQuizViewModel
import com.prinkal.quiz.utils.*
import kotlinx.android.synthetic.main.pq_body_multi_quiz.*
import kotlinx.android.synthetic.main.pq_fragment_multi_quiz.*
import kotlinx.android.synthetic.main.pq_header_multi_quiz.*

class MultiQuizFragment : BaseFragment() {

    private var isGamePaused: Boolean = false
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

    override fun onBackPressed() {
        CustomLog.d(TAG, "child onBackPressed invoked")
        //intercept back button and show confirm dialog
        showConfirmQuitDialog()
    }

    private fun showConfirmQuitDialog() {
        val builder = AlertDialog.Builder(requireContext())
        //set title for alert dialog
        builder.setTitle(R.string.pq_leave_game_dialog)
        //set message for alert dialog
        builder.setMessage(R.string.pq_leave_game_dialog_desc)
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(R.string.pq_yes) { dialogInterface, which ->
            viewModel.onPlayerAbandoned()
            super.removedOnBackCallback()
            openFragment(ResultMultiQuizFragment.newInstance(roomId))
        }
        //performing negative action
        builder.setNegativeButton(R.string.pq_no) { dialogInterface, which ->
            dialogInterface.dismiss()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    private fun showOppAbandonedDialog(opponentName: String) {
        val builder = AlertDialog.Builder(requireContext())
        //set title for alert dialog
        builder.setTitle(R.string.pq_abandoned_game_dialog)
        //set message for alert dialog
        builder.setMessage(getString(R.string.pq_abandoned_game_dialog_desc, opponentName))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(R.string.pq_yes) { dialogInterface, which ->
            super.removedOnBackCallback()
            viewModel.onGameFinished()
        }
        /*//performing negative action
        builder.setNegativeButton(R.string.pq_no) { dialogInterface, which ->
            dialogInterface.dismiss()
        }*/
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.pq_fragment_multi_quiz, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupViewModel()
        setupRoomObserver()
        setupQuestionObserver()
    }

    override fun onPause() {
        super.onPause()
        isGamePaused = true
        viewModel.pauseTimer()
    }

    override fun onStart() {
        super.onStart()
        if (isGamePaused) {
            isGamePaused = false
            viewModel.skipCurrentQuestion()
        }
    }

    private val optionClickListener = View.OnClickListener { v ->
        val selectedOption = updateCardColorAsSelected(v)
        viewModel.onAnswerSubmitted(selectedOption)

    }

    //for optimization , every option card is tagged with A,b,C,D . It will remove if else checks here
    private fun updateCardColorAsSelected(v: View): String {
        val textView = v.findViewWithTag<AppCompatTextView>("tvOpt${v.tag}")
        changeCardViewColor(
            v as MaterialCardView,
            ContextCompat.getColor(requireContext(), R.color.gray)
        )
        changeTextViewColor(
            textView,
            ContextCompat.getColor(requireContext(), R.color.pq_option_color_selected)
        )
        return textView.text.toString()
    }

    //for optimization , every option card is tagged with A,b,C,D . It will remove if else checks here
    private fun updateCardColorAsCorrect(v: MaterialCardView, tv: AppCompatTextView) {

        changeCardViewColor(
            v, ContextCompat.getColor(requireContext(), R.color.pq_card_color_correct)
        )
        changeTextViewColor(
            tv,
            ContextCompat.getColor(requireContext(), R.color.pq_option_color_selected)
        )
    }

    //for optimization , every option card is tagged with A,b,C,D . It will remove if else checks here
    private fun updateCardColorAsInCorrect(v: MaterialCardView, tv: AppCompatTextView) {

        changeCardViewColor(
            v, ContextCompat.getColor(requireContext(), R.color.pq_card_color_incorrect)
        )
        changeTextViewColor(
            tv,
            ContextCompat.getColor(requireContext(), R.color.pq_option_color_selected)
        )
    }

    private fun changeTextViewColor(tv: AppCompatTextView, colorInt: Int) {
        tv.setTextColor(colorInt)
    }

    private fun changeCardViewColor(v: MaterialCardView, colorInt: Int) {
        v.setCardBackgroundColor(colorInt)
    }

    private fun updateCardsToDefault() {
        enableDisableCardClicks(true)
        val cardDefault = ContextCompat.getColor(requireContext(), R.color.pq_card_color_default)
        val textDefault = ContextCompat.getColor(requireContext(), R.color.pq_option_color_default)

        cvOptA.setCardBackgroundColor(cardDefault)
        cvOptB.setCardBackgroundColor(cardDefault)
        cvOptC.setCardBackgroundColor(cardDefault)
        cvOptD.setCardBackgroundColor(cardDefault)

        tvOptA.setTextColor(textDefault)
        tvOptB.setTextColor(textDefault)
        tvOptC.setTextColor(textDefault)
        tvOptD.setTextColor(textDefault)
    }

    private fun updateCorrectOptionColor(selectedOption: String, answer: String) {

        //highlight correct answer
        when (answer) {
            tvOptA.text.toString() -> {
                updateCardColorAsCorrect(cvOptA, tvOptA)
            }
            tvOptB.text.toString() -> {
                updateCardColorAsCorrect(cvOptB, tvOptB)
            }
            tvOptC.text.toString() -> {
                updateCardColorAsCorrect(cvOptC, tvOptC)
            }
            tvOptD.text.toString() -> {
                updateCardColorAsCorrect(cvOptD, tvOptD)
            }
        }

        //highlight in-correct answer
        if (selectedOption != answer) {

            when (selectedOption) {
                tvOptA.text.toString() -> {
                    updateCardColorAsInCorrect(cvOptA, tvOptA)
                }
                tvOptB.text.toString() -> {
                    updateCardColorAsInCorrect(cvOptB, tvOptB)
                }
                tvOptC.text.toString() -> {
                    updateCardColorAsInCorrect(cvOptC, tvOptC)
                }
                tvOptD.text.toString() -> {
                    updateCardColorAsInCorrect(cvOptD, tvOptD)
                }
            }
        }
    }


    private fun setupUI() {
        cvOptA.setOnClickListener(optionClickListener)
        cvOptB.setOnClickListener(optionClickListener)
        cvOptC.setOnClickListener(optionClickListener)
        cvOptD.setOnClickListener(optionClickListener)
    }

    private fun updateScores(playerAScore: Int, playerBScore: Int) {
        if (viewModel.hasInvitationReceived()) {
            tvPlayerAScore.text = "$playerBScore"
            tvPlayerBScore.text = "$playerAScore"
        } else {
            tvPlayerAScore.text = "$playerAScore"
            tvPlayerBScore.text = "$playerBScore"
        }
    }


    private fun setupRoomObserver() {

        viewModel.getElapsedTime().observe(viewLifecycleOwner, {
            CustomLog.e(TAG, "getElapsedTime = $it")
            tvTimer.text = it.timeStr
            pbHeader.progress = it.progress

        })


        viewModel.getRoom().observe(viewLifecycleOwner, {

            CustomLog.e(TAG, "Room observer ${it.status.name}")
            when (it.status) {
                Status.SUCCESS -> {
                    onRoomDataUpdated(it.data!!)
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

            CustomLog.d(TAG, "Question observer ${it.status.name}")
            when (it.status) {
                QuestionEvent.QUESTION -> {
                    updateQuestionView(it.data!!)
                }
                QuestionEvent.WAITING -> {
                    enableDisableCardClicks(false)
                }
                QuestionEvent.LOADER_CORRECT -> {
                    showMsg(requireContext(), "Correct answer")
                }
                QuestionEvent.LOADER_INCORRECT -> {
                    showMsg(requireContext(), "Incorrect answer")

                }
                QuestionEvent.ANSWER -> {
                    updateCorrectOptionColor(it.selectedOption, it.correctAnswer)
                }

                QuestionEvent.FINISHED -> {
                    CustomLog.d(TAG, "finished")
                    //all question finished ,one of the player finished the quiz
                    openFragment(ResultMultiQuizFragment.newInstance(roomId))

                }
                QuestionEvent.ABANDONED -> {
                    CustomLog.d(TAG, "ABANDONED")
                    //all question finished ,one of the player finished the quiz
                    //here "selectedOption" is Opponent's name
                    showOppAbandonedDialog(it.selectedOption)
                }
            }
        })
    }

    //while waiting user will not be able to click options
    private fun enableDisableCardClicks(enable: Boolean) {
        cvOptA.isEnabled = enable
        cvOptB.isEnabled = enable
        cvOptC.isEnabled = enable
        cvOptD.isEnabled = enable
    }

    private fun updateQuestionView(ques: Question) {
        updateCardsToDefault()

        tvPower.visibility = if (ques.powerQuestion) VISIBLE else GONE

        tvQuestion.text = ques.question
        //get all options as shuffled list
        val optList = ques.fetchOptionAsShuffledList()
        tvOptA.text = optList[0]
        tvOptB.text = optList[1]
        tvOptC.text = optList[2]
        tvOptD.text = optList[3]
    }


    private fun onRoomDataUpdated(room: GameRoom) {

        //STATUS_PREPARED means both the player are ready, so start the quiz
        if (room.status == Const.STATUS_PREPARED) {

            //show view
            pbLoader.visibility = GONE
            cvHeader.visibility = VISIBLE
            cvBody.visibility = VISIBLE

            if (viewModel.hasInvitationReceived()) {
                tvPlayerA.text = room.playerB?.name
                tvPlayerB.text = room.playerA?.name
            } else {
                tvPlayerA.text = room.playerA?.name
                tvPlayerB.text = room.playerB?.name
            }

            //start quiz
            viewModel.startFirstQuestion()
        } else if (room.status == Const.STATUS_IN_GAME) {
            updateScores(room.playerAScore, room.playerBScore)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            QuizViewModelFactory(roomId)
        ).get(MultiQuizViewModel::class.java)
    }

}