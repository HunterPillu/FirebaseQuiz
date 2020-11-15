package com.prinkal.quiz.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prinkal.quiz.data.api.FirebaseApi
import com.prinkal.quiz.data.model.GameMeta
import com.prinkal.quiz.data.model.GameRoom
import com.prinkal.quiz.data.model.Question
import com.prinkal.quiz.data.model.Quiz
import com.prinkal.quiz.ui.firebase.FirebaseData
import com.prinkal.quiz.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MultiQuizViewModel(private val roomId: String) : ViewModel() {

    companion object {
        internal val TAG = MultiQuizViewModel::class.java.name
    }

    //game stats of this player will be saved on gameMeta
    private lateinit var gameMeta: GameMeta

    private lateinit var quiz: Quiz
    private var isInvitationReceived: Boolean = !roomId.contentEquals(FirebaseData.myID)
    private val room = MutableLiveData<Resource<GameRoom>>()
    private var timer: PqTimerTask? = null
    private val mElapsedTime = MutableLiveData<Progress>()

    //used for notifying new question
    private var question = MutableLiveData<QuestionData<Question>>()

    //used for notifying view if selected option is correct or not
    private var quesNo: Int = 0
    private var currentScore: Int = 0

    init {
        CustomLog.d(TAG, "init")
        CustomLog.d(TAG, "isInvitationReceived = $isInvitationReceived")
        room.postValue(Resource.loading(null))
        listenGameRoom()
    }

    fun getRoom(): LiveData<Resource<GameRoom>> {
        return room
    }

    fun getQuestion(): LiveData<QuestionData<Question>> {
        return question
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

        //game stats of this player will be saved on gameMeta
        gameMeta = gameRoom.playerA!!

        CustomLog.e(TAG, "222222222222")
        viewModelScope.launch(Dispatchers.IO) {

            //fetching quiz mearged with 10 random question
            quiz = FirebaseApi.getQuizWithQuestion(gameRoom.quizId)
            CustomLog.e(TAG, "33333333333333")
            //adding quiz to room , so that opponent (PlayerB) can download
            FirebaseApi.updateQuizOnRoom(roomId, quiz, Const.STATUS_PREPARING)
            CustomLog.e(TAG, "444444444444")

            //saving game stats : total question count
            gameMeta.totalQuestion = quiz.questions!!.size
        }
    }


    //fetch quiz that playerA added and change the room status to STATUS_PREPARED
    private fun fetchQuizFromRoom(gameRoom: GameRoom) {
        CustomLog.e(TAG, "66666666666")
        //only playerB need the quiz
        if (!isInvitationReceived) {
            return
        }

        quiz = gameRoom.quiz!!

        //game stats of this player will be saved on gameMeta
        gameMeta = gameRoom.playerB!!

        //saving game stats : total question count
        gameMeta.totalQuestion = quiz.questions!!.size

        CustomLog.e(TAG, "666666666666666")
        viewModelScope.launch(Dispatchers.IO) {
            CustomLog.e(TAG, "7777777777777777")
            //deleting quiz from room , it will reduce network data
            //now both the player has the quiz and question
            //changing room status to STATUS_PREPARED
            FirebaseApi.updateQuizOnRoom(roomId, null, Const.STATUS_PREPARED)
            CustomLog.e(TAG, "8888888888888")
        }
    }


    private fun showNextQuestion() {
        if (quesNo < quiz.questions!!.size) {
            val ques = quiz.questions!![quesNo]
            startTimer(ques.time)
            question.postValue(QuestionData.nextQuestion(ques))
        } else {
            //one of the players finished the quiz
            question.postValue(QuestionData.finished())
            viewModelScope.launch(Dispatchers.IO) {

                //used to notify other player that this player has finished
                gameMeta.status = Const.STATUS_FINISHED

                //save game stats to correct player
                val field = if (isInvitationReceived) "playerB" else "playerA"
                FirebaseApi.updateRoomField(roomId, field, gameMeta)
            }
        }
    }


    fun onAnswerSubmitted(selectedOption: String) {
        //while calculating result put view on waiting
        mElapsedTime.postValue(Progress())

        //save total consumed time for this question
        gameMeta.totalTime += timer!!.getConsumedTime()

        cancelTimer()
        question.postValue(QuestionData.waiting())


        //val delay2Sec = PqTimerTask("delay2Sec $quesNo", 1, null, null, viewModelScope) {

        calculateAnswer(selectedOption)


        //}
        //delay2Sec.start()

    }

    private fun calculateAnswer(selectedOption: String) {
        //check answer if correct or incorrect
        val questionVo = quiz.questions!![quesNo]
        val isCorrect = selectedOption == questionVo.answer

        if (selectedOption != "") {
            //saving game stats : correct answer count
            gameMeta.totalAttempted += 1
        }

        if (isCorrect) {
            //saving game stats : correct answer count
            gameMeta.totalCorrect += 1

            //add the correct score
            currentScore += questionVo.correctScore

            //check if it is a power question
            if (questionVo.powerQuestion) {
                // add power score
                currentScore += questionVo.powerScore
            }
            //question.postValue(QuestionData.correctLoader())
        } else {
            // subtract the negative marks  (incorrectScore will be in negative eg -110)
            currentScore += questionVo.incorrectScore

            if (selectedOption != "") {
                //saving game stats : incorrect answer count
                gameMeta.totalIncorrect += 1
            }
        }


        //update score on the room
        viewModelScope.launch(Dispatchers.IO) {

            //saving game stats : current score
            gameMeta.score = currentScore

            //optimization : aving score on room.playerBScore instead of room.playerA.score
            val field = if (isInvitationReceived) "playerBScore" else "playerAScore"
            FirebaseApi.updateRoomField(roomId, field, currentScore)

            //update ui to show current question result
            question.postValue(QuestionData.showAnswer(selectedOption, questionVo.answer))
            delay(1000)

            //show laoder on ui
            question.postValue(QuestionData.loader(isCorrect))
            delay(3000)

            //increase question number and show next
            quesNo++
            showNextQuestion()
        }
    }

    //set room status to IN_GAME and show first question
    fun startFirstQuestion() {
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseApi.updateRoomField(roomId, "status", Const.STATUS_IN_GAME)
        }
        showNextQuestion()
    }

    fun hasInvitationReceived(): Boolean = isInvitationReceived

    fun getElapsedTime(): LiveData<Progress> {
        return mElapsedTime
    }

    //start timer for each question
    private fun startTimer(duration: Int) {
        CustomLog.e(TAG, "quesNo=$quesNo time=$duration")
        cancelTimer()
        timer = PqTimerTask("$quesNo", 0, 1, duration, viewModelScope) { remainingTime ->
            CustomLog.e(TAG, "quesNo=$quesNo duration=$remainingTime")
            if (remainingTime == 0) {
                onAnswerSubmitted("")
            } else {
                mElapsedTime.value = Progress(
                    "${remainingTime / 60}:${remainingTime % 60}",
                    100 - ((remainingTime * 100) / duration)
                )
            }
        }
        timer?.start()

    }

    //optional: clearing timer when view model destroyed
    override fun onCleared() {
        super.onCleared()
        cancelTimer()
    }

    //cancel timer if running
    private fun cancelTimer() {
        timer?.cancel()
    }

    data class Progress(var timeStr: String = "0.0", var progress: Int = 0)

}