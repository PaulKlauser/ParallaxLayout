package com.example.parallaxlayout

import android.animation.Animator

abstract class SimpleAnimatorListener : Animator.AnimatorListener {
    override fun onAnimationStart(animation: Animator?) {
        // Noop
    }

    override fun onAnimationEnd(animation: Animator?) {
        // Noop
    }

    override fun onAnimationCancel(animation: Animator?) {
        // Noop
    }

    override fun onAnimationRepeat(animation: Animator?) {
        // Noop
    }
}
