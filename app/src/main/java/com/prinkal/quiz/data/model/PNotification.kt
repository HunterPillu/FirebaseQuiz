package com.prinkal.quiz.data.model

data class PQNotification(
    var to: String,
    var time_to_live: Long = 86400,
    var priority: String = "high",
    var data: PQData,
)

data class PQData(
    var title: String,
    var body: String,
    var opponentId: String
)