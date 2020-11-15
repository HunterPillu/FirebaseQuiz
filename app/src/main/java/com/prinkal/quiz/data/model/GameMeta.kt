package com.prinkal.quiz.data.model

import com.prinkal.quiz.utils.Const

data class GameMeta(

    var uid: String = "",
    var name: String = "",
    var status: Int = Const.STATUS_IN_GAME,
    var score: Int = 0,
    var totalQuestion: Int = 0,
    var totalAttempted: Int = 0,
    var totalCorrect: Int = 0,
    var totalIncorrect: Int = 0,
    var totalTime: Int = 0,
    var lifelineUsed: Int = 0,

    var opponentId: String = "",

    )