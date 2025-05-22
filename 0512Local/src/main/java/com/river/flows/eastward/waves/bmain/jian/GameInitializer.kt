package com.river.flows.eastward.waves.bmain.jian

import android.app.Application
import android.app.Application.getProcessName
import com.river.flows.eastward.waves.bmain.jian.ReferrerManager.launchRefData
import com.river.flows.eastward.waves.contens.bean.SPUtils
import com.river.flows.eastward.waves.contens.config.AppConfigFactory
import com.river.flows.eastward.waves.tool.time.AdLimiter3
import com.tradplus.ads.open.TradPlusSdk
import java.io.File
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.util.Log
import android.webkit.WebView
import androidx.core.app.SccsJobIntentService
import com.river.flows.eastward.waves.worker.ServiceManager
import com.river.flows.eastward.waves.worker.job.ScwcJobService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object GameInitializer {
    lateinit var adLimiter: AdLimiter3
    fun init(application: Application, isReleaseData: Boolean) {
        if (!isMainProcess(application)) {
            runCatching {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val processName = getProcessName() ?: "default"
                    WebView.setDataDirectorySuffix(processName)
                }
            }
            return
        }

        BikerStart.showLog("0512Local init")
        BikerStart.gameApp = application
        BikerStart.isRelease = isReleaseData
        adLimiter = AdLimiter3(application)
        SPUtils.init(application)
        createDataDir()
        TradPlusSdk.initSdk(application, AppConfigFactory.getConfig().tttid)
        StartTool.getAndroidId()
        ServiceManager.startPeriodicService()
        StartTool.noShowICCC()
        launchRefData()
        StartTool.startSessionUp()
        StartTool.initAppsFlyer()
        StartTool.getFcmFun()

        WorkerManager.enqueuePeriodicChain()
        WorkerManager.enqueueSelfLoop()
        schedulePeriodicJob(application)
        startJobIntServiceFun()
    }


    private fun createDataDir() {
        val path = "${BikerStart.gameApp.applicationContext.dataDir.path}/scwccs"
        File(path).mkdirs()
        BikerStart.showLog("文件名=: $path")
//        ZycstwA.Mgcskei(BikerStart.gameApp)
    }


    private fun schedulePeriodicJob(context: Context) {
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

    private fun startJobIntServiceFun() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                val intent = Intent(BikerStart.gameApp, SccsJobIntentService::class.java)
                SccsJobIntentService.enqueueWork(BikerStart.gameApp, intent)
                delay(5 * 60 * 1000)
            }
        }
    }

    fun isMainProcess(context: Context): Boolean {
        return getCurrentProcessName(context) == context.packageName
    }

    private fun getCurrentProcessName(context: Context): String? {
        val pid = Process.myPid()
        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        return activityManager.runningAppProcesses.firstOrNull { it.pid == pid }?.processName
    }

    fun getInstallTimeInSeconds(): Long {
        return try {
            val packageManager: PackageManager = BikerStart.gameApp.packageManager
            val packageInfo: PackageInfo =
                packageManager.getPackageInfo(BikerStart.gameApp.packageName, 0)
            (System.currentTimeMillis() - packageInfo.firstInstallTime) / 1000
        } catch (e: PackageManager.NameNotFoundException) {
            BikerStart.showLog("Package not found: ${e.message}")
            0L
        }
    }
}
