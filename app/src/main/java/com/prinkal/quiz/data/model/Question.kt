package com.prinkal.quiz.data.model

data class Question(
    var answer: String,
    var uid: String,
    var correctScore: Int = 100,
    var incorrectScore: Int = 0,
    var level: Int = 0,
    var optA: String,
    var optB: String,
    var optC: String,
    var optD: String,
    var powerQuestion: Boolean = false,
    var powerScore: Int = 0,
    var question: String,
    var time: Int = 10,
)