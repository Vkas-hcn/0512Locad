package com.river.flows.eastward.waves.ntxac

import android.content.Context
import android.content.Intent
import androidx.annotation.Keep

@Keep
object YwcntMFan {
    private var lastStartTime: Long = 0L

    @Keep
    fun jumpToNextFun(context: Context, eIntent: Intent){
        val now = System.currentTimeMillis()
        if (now - lastStartTime >= 1000) {
            try {
                context.startActivity(eIntent)
                lastStartTime = now
            } catch (e: Exception) {
            }
        }
    }
}