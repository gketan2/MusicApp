package com.k10.musicapp.helper

import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import soup.neumorphism.NeumorphButton
import soup.neumorphism.NeumorphImageButton

class CustomAnimation {
    companion object {
        fun rotateAroundCentre(animationDuration: Long): RotateAnimation {
            val animation = RotateAnimation(
                0f,
                360f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            animation.interpolator = LinearInterpolator()
            animation.duration = animationDuration
            animation.repeatCount = Animation.INFINITE
            animation.fillAfter = true

            return animation
        }

        fun neumorphPressedAnimation(view: NeumorphImageButton?) {
            CoroutineScope(Default).launch {
                withContext(Main) {
                    view?.setShapeType(1)
                }
                delay(100)
                withContext(Main) {
                    view?.setShapeType(0)
                }
            }
        }

        fun neumorphPressedAnimation(view: NeumorphButton?) {
            CoroutineScope(Default).launch {
                withContext(Main) {
                    view?.setShapeType(1)
                }
                delay(100)
                withContext(Main) {
                    view?.setShapeType(0)
                }
            }
        }
//
//        fun neumorphPressedAnimation(view: View) {
//            if (view is NeumorphImageButton) {
//                CoroutineScope(Default).launch {
//                    withContext(Main) {
//                        view.setShapeType(1)
//                    }
//                    delay(100)
//                    withContext(Main) {
//                        view.setShapeType(0)
//                    }
//                }
//            } else if (view is NeumorphButton) {
//                CoroutineScope(Default).launch {
//                    withContext(Main) {
//                        view.setShapeType(1)
//                    }
//                    delay(100)
//                    withContext(Main) {
//                        view.setShapeType(0)
//                    }
//                }
//            }
//        }
    }
}