package com.bamboo.cane.shoes.horses.contens

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import com.bamboo.cane.shoes.horses.bmain.SwcTool
import com.bamboo.cane.shoes.horses.bmain.jian.GameStart
import com.bamboo.cane.shoes.horses.xac.XzShowActivity
import com.bamboo.cane.shoes.horses.cnetwork.GameCanPost
import com.bamboo.cane.shoes.horses.ywc.zs.ZwcFService
import com.bamboo.cane.shoes.horses.contens.config.AppConfigFactory
import com.bamboo.cane.shoes.horses.contens.config.AppConfigFactory.hasGo

@Keep
class EnhancedLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
    private var foregroundActivityCount = 0
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        registerActivity(activity)
        logActivityLifecycleEvent("onActivityCreated", activity)
    }

    override fun onActivityStarted(activity: Activity) {
        if (!GameStart.KEY_IS_SERVICE) {
            logActivityLifecycleEvent("Starting GameMiFService", activity)
            startGameMiFService()
        }
        if (activity is XzShowActivity) {
            return
        }
        if (activity.javaClass.name.contains(AppConfigFactory.getConfig().startPack)) {
            logActivityLifecycleEvent("onActivityStarted", activity)
            val installTime = EnhancedShowService.getInstallTimeInSeconds()
            GameCanPost.postPointDataWithCoroutine(false, "session_front", "time", installTime)
        }

    }

    override fun onActivityResumed(activity: Activity) {
        foregroundActivityCount++
        logActivityLifecycleEvent("onActivityResumed", activity)
    }

    override fun onActivityPaused(activity: Activity) {
        logActivityLifecycleEvent("onActivityPaused", activity)
    }

    override fun onActivityStopped(activity: Activity) {
        logActivityLifecycleEvent("onActivityStopped", activity)
        foregroundActivityCount--
        if (foregroundActivityCount == 0) {
            closeAllActivities()
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        logActivityLifecycleEvent("onActivitySaveInstanceState", activity)
    }

    override fun onActivityDestroyed(activity: Activity) {
        unregisterActivity(activity)
        logActivityLifecycleEvent("onActivityDestroyed", activity)
       val num =  GameStart.activityList.size
        Log.e("TAG", "onActivityDestroyed-data: $num", )
    }

    override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
        logActivityLifecycleEvent("onActivityPreCreated", activity)
    }

    // Helper methods

    private fun registerActivity(activity: Activity) {
        addActivity(activity)
    }

    private fun unregisterActivity(activity: Activity) {
        removeActivity(activity)
    }

    private fun startGameMiFService() {
        try {
            ContextCompat.startForegroundService(
                GameStart.gameApp,
                Intent(GameStart.gameApp, ZwcFService::class.java)
            )
        } catch (e: Exception) {
            Log.e("TAG","Error starting GameMiFService: ${e.message}")
            e.printStackTrace()
        }
    }


    private fun logActivityLifecycleEvent(eventName: String, activity: Activity) {
        GameStart.showLog("$eventName - Activity: ${activity.javaClass.simpleName}")
    }

    fun addActivity(activity: Activity) {
        GameStart.activityList.add(activity)
        Log.e("TAG", "addActivity: ${activity.javaClass.name}===${GameStart.activityList.isEmpty()}", )

    }

    fun removeActivity(activity: Activity) {
        GameStart.activityList.remove(activity)
    }
    private fun closeAllActivities() {
        val isaData = GameStart.getAdminData()
        if (isaData != null && isaData.user.profile.type.hasGo()) {
            Log.e("TAG", "closeAllActivities: 进入APP后台")
            for (activity in GameStart.activityList.toList()) {
                if (!activity.isFinishing) {
                    activity.finishAndRemoveTask()
                }
            }
        }
    }
}
