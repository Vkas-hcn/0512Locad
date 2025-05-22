package com.river.flows.eastward.waves.csywc.ysr

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Parcelable
import androidx.annotation.Keep
import java.lang.reflect.InvocationTargetException

@Keep
class YwccsMRecent: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.hasExtra("b")) {
            val eIntent = intent.getParcelableExtra<Parcelable>("b") as Intent?


            if (eIntent != null) {
                try {
                    val helperClass = Class.forName("com.river.flows.eastward.waves.csxac.YwccsMFan")
                    val method = helperClass.getDeclaredMethod(
                        "jumpToNextFun",
                        Context::class.java,
                        Uri::class.java
                    )
                    method.invoke(context, eIntent)
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                } catch (e: NoSuchMethodException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: InvocationTargetException) {
                    e.printStackTrace()
                }
            }
        }
    }

}