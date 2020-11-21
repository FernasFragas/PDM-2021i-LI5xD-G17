package pt.isel.pdm.drag.utils

import android.os.Handler
import android.os.Looper


fun runDelayed(millis: Long, action: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed(action, millis)
}

fun runTimer(millis: Long, action: () -> Unit) {
    Handler(Looper.getMainLooper()).postAtTime(action,millis)
}