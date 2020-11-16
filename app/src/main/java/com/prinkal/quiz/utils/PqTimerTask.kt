package com.prinkal.quiz.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class PqTimerTask(
    private val name: String,
    private val delay: Int = 0,
    private val interval: Int? = null,
    private val duration: Int? = null,
    private val coroutineScope: CoroutineScope,
    action: suspend (duration: Int) -> Unit
) {
    private var remainingTime = duration ?: -1
    private val keepRunning = AtomicBoolean(true)
    private var job: Job? = null
    private val tryAction = suspend {
        try {
            action(remainingTime)
        } catch (e: Exception) {
            CustomLog.e("PqTimerTask", "timerNo=$name timer action failed: ", e)
            cancel()
        }
    }

    fun getConsumedTime(): Int {
        return if (null != duration) {
            duration - remainingTime
        } else 0
    }

    fun start() {
        job = coroutineScope.launch {
            delay(delay * 1000L)
            if (interval != null) {
                while (keepRunning.get() && remainingTime >= 0) {
                    CustomLog.d("PqTimerTask", "running name=$name")
                    tryAction()
                    delay(interval * 1000L)
                    remainingTime -= interval
                }
            } else {
                if (keepRunning.get()) {
                    tryAction()
                }
            }
        }
    }

    /**
     * Initiates an orderly shutdown, where if the timer task is currently running,
     * we will let it finish, but not run it again.
     * Invocation has no additional effect if already shut down.
     */
    fun shutdown() {
        keepRunning.set(false)
    }

    /**
     * Immediately stops the timer task, even if the job is currently running,
     * by cancelling the underlying PQ Job.
     */
    fun cancel() {
        CustomLog.e("PqTimerTask", "name=${name} ,cancel called & job!=null ${job != null}")
        shutdown()
        job?.cancel()
    }
}