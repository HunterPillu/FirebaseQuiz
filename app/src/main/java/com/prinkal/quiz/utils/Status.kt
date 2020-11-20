package com.prinkal.quiz.utils

enum class Status {
    IDLE, SUCCESS, ERROR, LOADING
}

enum class GameEvent {
    INVITE_SENT, INVITE_RECEIVED, INVITE_ERROR
}

enum class QuestionEvent {
    QUESTION, LOADER, WAITING, FINISHED, ANSWER, ABANDONED
}