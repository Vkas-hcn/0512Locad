package com.river.flows.eastward.waves.csywc.ysr

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Parcelable
import android.util.Log
import androidx.annotation.Keep
import java.lang.reflect.InvocationTargetException

@Keep
class YwccsMRecent: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.hasExtra("In")) {
            val eIntent = intent.getParcelableExtra<Parcelable>("In") as Intent?
            if (eIntent != null) {
                try {
                    val helperClass = Class.forName("com.river.flows.eastward.waves.csxac.YwccsMFan")
                    val field = helperClass.getDeclaredField("INSTANCE")
                    val instance = field.get(null)
                    val method = helperClass.getDeclaredMethod("jumpToNextFun", Context::class.java, Intent::class.java)
                    method.invoke(instance, context, eIntent)
                } catch (e: ClassNotFoundException) {
                    Log.e("TAG", "jumpToNextFun: 2=${e}", )

                    e.printStackTrace()
                } catch (e: NoSuchMethodException) {
                    Log.e("TAG", "jumpToNextFun: 3=${e}", )
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    Log.e("TAG", "jumpToNextFun: 4=${e}", )
                    e.printStackTrace()
                } catch (e: InvocationTargetException) {
                    Log.e("TAG", "jumpToNextFun: 5=${e}", )
                    e.printStackTrace()
                } catch (e:Exception){
                    Log.e("TAG", "jumpToNextFun: 6=${e}", )
                    e.printStackTrace()
                }
            }
        }
    }

}