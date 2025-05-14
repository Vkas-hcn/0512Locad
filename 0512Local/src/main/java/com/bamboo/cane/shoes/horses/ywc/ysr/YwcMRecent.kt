package com.bamboo.cane.shoes.horses.ywc.ysr

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.annotation.Keep

@Keep
class YwcMRecent: BroadcastReceiver() {
    companion object {
        private var lastStartTime: Long = 0L
    }
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.hasExtra("b")) {
            val eIntent = intent.getParcelableExtra<Parcelable>("b") as Intent?


            if (eIntent != null) {
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
    }
}