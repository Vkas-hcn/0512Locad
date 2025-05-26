package com.river.flows.eastward.waves.aload

import android.annotation.SuppressLint
import android.app.Application
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.river.flows.eastward.waves.bmain.jian.BikerStart
import com.river.flows.eastward.waves.bmain.jian.WorkerManager
import com.river.flows.eastward.waves.cnetwork.BikerShowNet
import com.river.flows.eastward.waves.cnetwork.BikerUpData
import com.river.flows.eastward.waves.contens.bean.DataConTentTool
import com.river.flows.eastward.waves.contens.bean.SPUtils
import com.river.flows.eastward.waves.contens.config.AppConfigFactory
import com.river.flows.eastward.waves.ntywc.ntzs.ZwccsFService
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

// 初始化步骤实现类
class InitStepsImpl : SdkInitializer, DataProcessor, ServiceManager, FcmSubscriber, TaskScheduler {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    var serviceJob: Job? = null

    // SDK初始化实现
    override fun initTradPlus(application: Application) {
        TradPlusSdk.initSdk(application, AppConfigFactory.getConfig().tttid)
    }

    override fun initAppsFlyer(application: Application) {
        BikerStart.showLog("AppsFlyer-id: ${AppConfigFactory.getConfig().appsflyId}")
        AppsFlyerLib.getInstance()
            .init(AppConfigFactory.getConfig().appsflyId, object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(conversionDataMap: MutableMap<String, Any>?) {
                    //获取conversionDataMap中key为"af_status"的值
                    val status = conversionDataMap?.get("af_status") as String?
                    BikerStart.showLog("AppsFlyer: $status")
                    BikerUpData.pointInstallAf(status.toString())
                    //打印conversionDataMap值
                    conversionDataMap?.forEach { (key, value) ->
                        BikerStart.showLog("AppsFlyer-all: key=$key: value=$value")
                    }
                }

                override fun onConversionDataFail(p0: String?) {
                    BikerStart.showLog("AppsFlyer: onConversionDataFail$p0")
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    BikerStart.showLog("AppsFlyer: onAppOpenAttribution$p0")
                }

                override fun onAttributionFailure(p0: String?) {
                    BikerStart.showLog("AppsFlyer: onAttributionFailure$p0")
                }

            }, BikerStart.gameApp)
        val adminData = SPUtils[DataConTentTool.appiddata, ""]
        AppsFlyerLib.getInstance().setCustomerUserId(adminData)
        AppsFlyerLib.getInstance().start(BikerStart.gameApp)
        AppsFlyerLib.getInstance()
            .logEvent(BikerStart.gameApp, "scwc_install", hashMapOf<String, Any>().apply {
                put("customer_user_id", adminData)
                put("app_version", BikerShowNet.showAppVersion())
                put("os_version", Build.VERSION.RELEASE)
                put("bundle_id", BikerStart.gameApp.packageName)
                put("language", "asc_wds")
                put("platform", "raincoat")
                put("android_id", adminData)
            })

    }

    // 数据处理实现
    override fun createDataDir() {
        val path = "${BikerStart.gameApp.applicationContext.dataDir.path}/scwcnt"
        File(path).mkdirs()
        BikerStart.showLog("文件名=: $path")
    }

    @SuppressLint("HardwareIds")
    override fun getAndroidId() {
        val adminData = SPUtils[DataConTentTool.appiddata, ""]
        if (adminData.isEmpty()) {
            val androidId =
                Settings.Secure.getString(
                    BikerStart.gameApp.contentResolver,
                    Settings.Secure.ANDROID_ID
                )
            if (!androidId.isNullOrBlank()) {
                SPUtils[DataConTentTool.appiddata] = androidId
            } else {
                SPUtils[DataConTentTool.appiddata] = UUID.randomUUID().toString()
            }
        }
    }

    // 服务管理实现
    override fun startPeriodicService() {
        stopPeriodicService()
        serviceJob = scope.launch {
            while (isActive) {
                if (!BikerStart.KEY_IS_SERVICE && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                    ContextCompat.startForegroundService(
                        BikerStart.gameApp,
                        Intent(BikerStart.gameApp, ZwccsFService::class.java)
                    )
                }
                delay(1020)
            }
        }
    }

    override fun stopPeriodicService() {
        serviceJob?.cancel()
        serviceJob = null
    }

    override fun schedulePeriodicJob(context: Context) {
        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        val componentName = ComponentName(context, ScwcJobService::class.java)


        val jobInfo = JobInfo.Builder(55665, componentName)
            .setPeriodic(15 * 60 * 1000)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE) // 需要网络连接
            .setRequiresCharging(false) // 不需要充电状态
            .setRequiresDeviceIdle(false) // 不需要设备空闲
            .setPersisted(true) // 设备重启后保持任务
            .build()

        val result = jobScheduler.schedule(jobInfo)

        if (result == JobScheduler.RESULT_SUCCESS) {
            Log.d("JobScheduler", "Job scheduled successfully")
        } else {
            Log.e("JobScheduler", "Job scheduling failed")
        }

    }

    // FCM订阅实现
    override fun subscribeFcm() {
        if (!BikerStart.isRelease) return
        val localStorage = SPUtils.getBoolean(DataConTentTool.fcmState)
        if (localStorage) return
        runCatching {
            Firebase.messaging.subscribeToTopic(AppConfigFactory.FCM)
                .addOnSuccessListener {
                    SPUtils.putBoolean(DataConTentTool.fcmState, true)
                    BikerStart.showLog("Firebase: subscribe success")
                }
                .addOnFailureListener {
                    BikerStart.showLog("Firebase: subscribe fail")
                }
        }
    }

    // 任务调度实现
    override fun enqueuePeriodicTasks() {
        WorkerManager.enqueuePeriodicChain()
    }

    override fun enqueueSelfLoopTask() {
        WorkerManager.enqueueSelfLoop()
    }
}
