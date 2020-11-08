package com.edu.mvvmtutorial.utils

object Utils {

    fun hasInvitationExpired(ts: Long): Boolean {
        val time = getCurrentTimeInMillis()
        val result = time - ts
        CustomLog.e("hasInvitationExpired", "result=$result , time= $time , ts=$ts")
        return result < Config.INVITATION_EXPIRE_TIME
    }

    fun getCurrentTimeInMillis(): Long = System.currentTimeMillis()

}