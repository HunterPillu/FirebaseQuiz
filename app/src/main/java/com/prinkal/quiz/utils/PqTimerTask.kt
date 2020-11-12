package com.prinkal.quiz.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class PqTimerTask(
    private val timerNo: Int,
    private val delay: Long = 0,
    private val repeat: Long? = null,
    private val coroutineScope: CoroutineScope,
    action: suspend () -> Unit
) {
    private val keepRunning = AtomicBoolean(true)
    private var job: Job? = null
    private val tryAction = suspend {
        try {
            action()
        } catch (e: Exception) {
            CustomLog.e("PqTimerTask", "timerNo=$timerNo timer action failed: $action", e)
            cancel()
        }
    }

    fun start() {
        job = coroutineScope.launch {
            delay(delay * 1000)
            if (repeat != null) {
                while (keepRunning.get()) {
                    CustomLog.d("PqTimerTask", "running timerNo=$timerNo")
                    tryAction()
                    delay(repeat * 1000)
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
        CustomLog.e("PqTimerTask", "timerNo=${timerNo} ,cancel called & job!=null ${job != null}")
        shutdown()
        job?.cancel()
    }

    companion object {
        /**
         * Runs the given `action` after the given `delay`,
         * once the `action` completes, waits the `repeat` duration
         * and runs again, until `shutdown` is called.
         *
         * if action() throws an exception, it will be swallowed and a warning will be logged.
         */
        fun start(
            coroutineScope: CoroutineScope,
            timerNo: Int,
            delay: Long = 0,
            repeat: Long? = null,
            action: suspend () -> Unit
        ): PqTimerTask =
            PqTimerTask(timerNo, delay, repeat, coroutineScope, action).also { it.start() }
    }
}