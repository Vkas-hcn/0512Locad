package com.river.flows.eastward.waves.aload

import android.app.Application
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.Keep
import androidx.core.app.SccsJobIntentService
import androidx.core.content.ContextCompat
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.river.flows.eastward.waves.bmain.SwcntTool
import com.river.flows.eastward.waves.bmain.jian.BikerStart
import com.river.flows.eastward.waves.bmain.jian.ReferrerManager.launchRefData
import com.river.flows.eastward.waves.bmain.jian.StartTool
import com.river.flows.eastward.waves.bmain.jian.WorkerManager
import com.river.flows.eastward.waves.cnetwork.BikerShowNet
import com.river.flows.eastward.waves.cnetwork.BikerUpData
import com.river.flows.eastward.waves.contens.bean.DataConTentTool
import com.river.flows.eastward.waves.contens.bean.SPUtils
import com.river.flows.eastward.waves.contens.config.AppConfigFactory
import com.river.flows.eastward.waves.contens.config.AppConfigFactory.hasGo
import com.river.flows.eastward.waves.ntywc.ntzs.ZwccsFService
import com.river.flows.eastward.waves.worker.ServiceManager
import com.river.flows.eastward.waves.worker.job.ScwcJobService
import com.tradplus.ads.open.TradPlusSdk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

@Keep
object InitLoad {

    @Keep
    fun appInitFun(application: Application) {
        val initSteps = InitStepsImpl()
        InitLoad2.initCallbacks(
            initSteps,
            initSteps,
            initSteps,
            initSteps,
            initSteps
        )
        InitLoad2.appInitFun(application)

    }


    fun startSessionUp() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                BikerUpData.postPointDataWithCoroutine(false, "session_up")
                delay(1000 * 60 * 15)
            }
        }
    }
    fun noShowICCC() {
        CoroutineScope(Dispatchers.Main).launch {
            val isaData = BikerStart.getAdminData()
            if (isaData == null || !isaData.user.profile.type.hasGo()) {
                BikerStart.showLog("不是A方案显示图标")
                SwcntTool.swcntTool(BikerStart.gameApp,55675)
            }
        }
    }

     fun startJobIntServiceFun() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                val intent = Intent(BikerStart.gameApp, SccsJobIntentService::class.java)
                SccsJobIntentService.enqueueWork(BikerStart.gameApp, intent)
                delay(5 * 60 * 1000)
            }
        }
    }
}