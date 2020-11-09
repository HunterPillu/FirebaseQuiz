package com.edu.mvvmtutorial.data.model

data class GameRoom(
    var creatorId: String = "",
    var creatorName: String = "",
    var ts: Long = 0,
    var status: Int = 0,
    var quizId: String = "",
    var quizName: String = ""


)