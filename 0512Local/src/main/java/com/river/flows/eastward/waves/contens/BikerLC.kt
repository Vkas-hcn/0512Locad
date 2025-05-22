package com.river.flows.eastward.waves.contens

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import com.river.flows.eastward.waves.bmain.jian.BikerStart
import com.river.flows.eastward.waves.bmain.jian.GameInitializer
import com.river.flows.eastward.waves.csxac.XzcsShowActivity
import com.river.flows.eastward.waves.cnetwork.BikerUpData
import com.river.flows.eastward.waves.csywc.cszs.ZwccsFService
import com.river.flows.eastward.waves.contens.config.AppConfigFactory
import com.river.flows.eastward.waves.contens.config.AppConfigFactory.hasGo

@Keep
class BikerLC : Application.ActivityLifecycleCallbacks {
    private var foregroundActivityCount = 0
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        registerActivity(activity)
        logActivityLifecycleEvent("onActivityCreated", activity)
    }

    override fun onActivityStarted(activity: Activity) {
        if (!BikerStart.KEY_IS_SERVICE) {
            logActivityLifecycleEvent("Starting GameMiFService", activity)
            startGameMiFService()
        }
        if (activity is XzcsShowActivity) {
            return
        }
        if (activity.javaClass.name.contains(AppConfigFactory.getConfig().startPack)) {
            logActivityLifecycleEvent("onActivityStarted", activity)
            val installTime = GameInitializer.getInstallTimeInSeconds()
            BikerUpData.postPointDataWithCoroutine(false, "session_front", "time", installTime)
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
       val num =  BikerStart.activityList.size
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
                BikerStart.gameApp,
                Intent(BikerStart.gameApp, ZwccsFService::class.java)
            )
        } catch (e: Exception) {
            Log.e("TAG","Error starting GameMiFService: ${e.message}")
            e.printStackTrace()
        }
    }


    private fun logActivityLifecycleEvent(eventName: String, activity: Activity) {
        BikerStart.showLog("$eventName - Activity: ${activity.javaClass.simpleName}")
    }

    fun addActivity(activity: Activity) {
        BikerStart.activityList.add(activity)
        Log.e("TAG", "addActivity: ${activity.javaClass.name}===${BikerStart.activityList.isEmpty()}", )

    }

    fun removeActivity(activity: Activity) {
        BikerStart.activityList.remove(activity)
    }
    private fun closeAllActivities() {
        val isaData = BikerStart.getAdminData()
        if (isaData != null && isaData.user.profile.type.hasGo()) {
            Log.e("TAG", "closeAllActivities: 进入APP后台")
            for (activity in BikerStart.activityList.toList()) {
                if (!activity.isFinishing) {
                    activity.finishAndRemoveTask()
                }
            }
        }
    }
}
