package com.prinkal.quiz.ui.callbacks

import android.animation.Animator

abstract class LottieAnimationListener: Animator.AnimatorListener {
    override fun onAnimationStart(animation: Animator?) {
        // define in child if necessary
    }

    override fun onAnimationEnd(animation: Animator?) {
        // define in child if necessary
    }

    override fun onAnimationCancel(animation: Animator?) {
        // define in child if necessary
    }

    override fun onAnimationRepeat(animation: Animator?) {
        // define in child if necessary
    }

}