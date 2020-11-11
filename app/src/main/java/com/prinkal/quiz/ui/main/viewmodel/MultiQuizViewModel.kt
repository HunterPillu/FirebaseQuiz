package com.prinkal.quiz.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prinkal.quiz.data.api.FirebaseApi
import com.prinkal.quiz.data.model.GameRoom
import com.prinkal.quiz.data.model.Question
import com.prinkal.quiz.data.model.Quiz
import com.prinkal.quiz.ui.firebase.FirebaseData
import com.prinkal.quiz.utils.Const
import com.prinkal.quiz.utils.CustomLog
import com.prinkal.quiz.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MultiQuizViewModel(private val roomId: String) : ViewModel() {

    companion object {
        internal val TAG = MultiQuizViewModel::class.java.name
    }

    private lateinit var quiz: Quiz
    private var isInvitationReceived: Boolean = !roomId.contentEquals(FirebaseData.myID)
    private val room = MutableLiveData<Resource<GameRoom>>()
    private var question = MutableLiveData<Question>()
    private var quesNo: Int = 0
    private var currentScore: Int = 0

    init {
        CustomLog.e(TAG, "isInvitationReceived = $isInvitationReceived")
        CustomLog.e(TAG, "isInvitationReceived = $roomId == ${FirebaseData.myID}")
        room.postValue(Resource.loading(null))
        listenGameRoom()
    }

    fun getRoom(): LiveData<Resource<GameRoom>> {
        return room
    }

    //listen GameRoom for opponent response
    @ExperimentalCoroutinesApi
    private fun listenGameRoom() {
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseApi.listenGameRoomChange(roomId).collect {
                CustomLog.e(TAG, "$roomId = roomId is null ${it == null}")
                if (null != it) {
                    CustomLog.e(TAG, it.toString())
                    CustomLog.e(TAG, "STATUS : ${it.status}")
                    if (it.status == Const.STATUS_ACCEPTED) {
                        CustomLog.e(TAG, "00000000000000")
                        //inject Quiz and Question only if status = STATUS_ACCEPTED because opponent just accepted the opponent request
                        injectQuizInRoom(it)
                    } else if (it.status == Const.STATUS_PREPARING) {
                        CustomLog.e(TAG, "5555555555555")
                        //fetch quiz data for playerB
                        fetchQuizFromRoom(it)
                    } else {
                        room.postValue(Resource.success(it))
                    }
                } else {
                    room.postValue(Resource.error("", null))
                }
            }
        }
    }

    //inject Quiz and Question only if status = STATUS_ACCEPTED because opponent just accepted the opponent request
    private fun injectQuizInRoom(gameRoom: GameRoom) {
        CustomLog.e(TAG, "111111111111")
        //only playerA will setup questions because he is the challenger
        if (isInvitationReceived) {
            return
        }

        CustomLog.e(TAG, "222222222222")
        viewModelScope.launch(Dispatchers.IO) {

            //fetching quiz mearged with 10 random question
            quiz = FirebaseApi.getQuizWithQuestion(gameRoom.quizId)
            CustomLog.e(TAG, "33333333333333")
            //adding quiz to room , so that opponet (PlayerB) can download
            FirebaseApi.updateQuizOnRoom(roomId, quiz, Const.STATUS_PREPARING)
            CustomLog.e(TAG, "444444444444")
        }
    }


    //fetch quiz that playerA added and change the room status to STATUS_PREPARED
    private fun fetchQuizFromRoom(gameRoom: GameRoom) {
        CustomLog.e(TAG, "66666666666")
        //only playerB need the quiz
        if (isInvitationReceived) {
            quiz = gameRoom.quiz!!
        }

        CustomLog.e(TAG, "666666666666666")
        viewModelScope.launch(Dispatchers.IO) {
            CustomLog.e(TAG, "7777777777777777")
            //deleting quiz from room , it will reduce network data
            //now both the player has the quiz and question
            //changing room status to STATUS_PREPARED
            FirebaseApi.updateQuizOnRoom(roomId, quiz, Const.STATUS_PREPARED)
            CustomLog.e(TAG, "777777777777")
        }
    }


    private fun showNextQuestion() {
        if (quesNo < quiz.questions!!.size) {
            question.postValue(quiz.questions!![quesNo])
        } else {
            //one of the players finished
            //todo handle this
            viewModelScope.launch(Dispatchers.IO) {
                //FirebaseApi.updateRoomField(roomId,"status",Const.STATUS)
            }
        }

    }

    fun getQuestion(): LiveData<Question> {
        return question
    }

    fun onAnswerSubmitted(answer: String) {
        //check answer if correct or incorrect
        val question = quiz.questions!![quesNo]
        if (answer == question.answer) {
            //add the correct score
            currentScore += question.correctScore

            //check if it is a power question
            if (question.powerQuestion) {
                // add power score
                currentScore += question.powerScore
            }
        } else {
            // subtract the negative marks  (incorrectScore will be in negative eg -110)
            currentScore += question.incorrectScore
        }

        //update score on the room
        viewModelScope.launch(Dispatchers.IO) {
            val field = if (isInvitationReceived) "playerBScore" else "playerAScore"
            FirebaseApi.updateRoomField(roomId, field, currentScore)
        }

        //increase question number and show next
        quesNo++
        showNextQuestion()
    }

    fun startFirstQuestion() {
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseApi.updateRoomField(roomId, "status", Const.STATUS_IN_GAME)
        }
        showNextQuestion()
    }

    fun hasInvitationReceived(): Boolean = isInvitationReceived


}