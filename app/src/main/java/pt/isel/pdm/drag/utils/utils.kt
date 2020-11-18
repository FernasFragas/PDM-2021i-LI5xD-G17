package pt.isel.pdm.drag.utils

import android.os.Handler
import android.os.Looper


fun runDelayed(millis: Long, action: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed(action, millis)
}