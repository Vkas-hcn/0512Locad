package com.bamboo.cane.shoes.horses.ywc.zs

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import android.widget.RemoteViews
import com.bamboo.cane.shoes.horses.R
import com.bamboo.cane.shoes.horses.bmain.jian.BikerStart

class ZwcFService : Service() {
    @SuppressLint("ForegroundServiceType", "RemoteViewLayout")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        BikerStart.showLog("0512Service onStartCommand-1=${BikerStart.KEY_IS_SERVICE}")
        if (!BikerStart.KEY_IS_SERVICE) {
            BikerStart.KEY_IS_SERVICE =true
            val channel = NotificationChannel("0512", "0512", NotificationManager.IMPORTANCE_MIN)
            channel.setSound(null, null)
            channel.enableLights(false)
            channel.enableVibration(false)
            (application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).run {
                if (getNotificationChannel(channel.toString()) == null) createNotificationChannel(channel)
            }
            runCatching {
                startForeground(
                    5657,
                    NotificationCompat.Builder(this, "0512").setSmallIcon(R.drawable.shape_no)
                        .setContentText("")
                        .setContentTitle("")
                        .setOngoing(true)
                        .setCustomContentView(RemoteViews(packageName, R.layout.layout_nocan))
                        .build()
                )
            }
            BikerStart.showLog("0512Service onStartCommand-2=${BikerStart.KEY_IS_SERVICE}")
        }
        return super.onStartCommand(intent, flags, startId)
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


}
