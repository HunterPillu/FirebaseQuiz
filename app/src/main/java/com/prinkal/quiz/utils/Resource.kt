package com.prinkal.quiz.utils

data class Resource<out T>(val status: Status, val data: T?, val message: String?) {

    companion object {

        fun <T> idle(): Resource<T> {
            return Resource(Status.IDLE, null, null)
        }

        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T?): Resource<T> {
            return Resource(Status.ERROR, data, msg)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }

    }
}

data class Invitation<out T>(
    val status: GameEvent,
    val data: T?,
    val keyId: String?,
    val errorMsg: Int
) {

    companion object {

        fun <T> receiveInvite(opponentId: String): Invitation<T> {
            return Invitation(GameEvent.INVITE_RECEIVED, null, opponentId, 0)
        }

        fun <T> sendInvite(data: T?, quizId: String): Invitation<T> {
            return Invitation(GameEvent.INVITE_SENT, data, quizId, 0)
        }

        fun <T> error(msg: Int, userName: String): Invitation<T> {
            return Invitation(GameEvent.INVITE_ERROR, null, userName, msg)
        }

    }
}