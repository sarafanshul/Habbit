package com.projectdelta.habbit.util.lang

import android.view.View
import android.view.animation.Animation

/**
 * Extension functions for starting animation with a function call after end of animation
 * @param animation animation to play
 * @param onEnd lambda to trigger for end of animation
 */
fun View.startAnimation( animation : Animation , onEnd : () -> Unit ) {
    animation.setAnimationListener( object : Animation.AnimationListener {
        override fun onAnimationStart(p0: Animation?) {
        }

        override fun onAnimationEnd(p0: Animation?) {
            onEnd()
        }

        override fun onAnimationRepeat(p0: Animation?) {}
    } )
    this.startAnimation(animation)
}