package com.prinkal.quiz.data.model

import com.prinkal.quiz.utils.Const

data class GameRoom(
    var playerAId: String = "",
    var playerAName: String = "",
    var playerBId: String = "",
    var playerBName: String = "",
    var quizId: String = "",
    var ts: Long = 0,
    var status: Int = Const.STATUS_WAITING,
    var quiz: Quiz? = null,

    //game data
    var playerAScore: Int = 0,
    var playerBScore: Int = 0

)