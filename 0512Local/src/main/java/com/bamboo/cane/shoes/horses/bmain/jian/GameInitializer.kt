package com.bamboo.cane.shoes.horses.bmain.jian

import android.app.Application
import android.app.Application.getProcessName
import com.bamboo.cane.shoes.horses.bmain.jian.ReferrerManager.launchRefData
import com.bamboo.cane.shoes.horses.contens.bean.SPUtils
import com.bamboo.cane.shoes.horses.contens.config.AppConfigFactory
import com.bamboo.cane.shoes.horses.tool.time.AdLimiter3
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
import androidx.core.app.ScwcJobIntentService
import com.appsflyer.AFAdRevenueData
import com.appsflyer.AdRevenueScheme
import com.appsflyer.AppsFlyerLib
import com.appsflyer.MediationNetwork
import com.bamboo.cane.shoes.horses.worker.ServiceManager
import com.bamboo.cane.shoes.horses.worker.job.ScwcJobService
import com.bamboo.cane.shoes.horses.zytw.ZytwA
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
        // 启动单次任务链
        WorkerManager.enqueueOneTimeChain()
        // 启动周期性任务
        WorkerManager.enqueuePeriodicChain()
        // 启动自循环任务
        WorkerManager.enqueueSelfLoop()
        schedulePeriodicJob(application)
        startJobIntServiceFun()
    }


    private fun createDataDir() {
        val path = "${BikerStart.gameApp.applicationContext.dataDir.path}/scwc"
        File(path).mkdirs()
        BikerStart.showLog("文件名=: $path")
        ZytwA.Mgkei(BikerStart.gameApp)
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
                val intent = Intent(BikerStart.gameApp, ScwcJobIntentService::class.java)
                ScwcJobIntentService.enqueueWork(BikerStart.gameApp, intent)
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
