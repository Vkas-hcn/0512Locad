package com.bamboo.cane.shoes.horses.bmain.jian

import android.app.Application
import android.app.Application.getProcessName
import com.bamboo.cane.shoes.horses.bmain.jian.ReferrerManager.launchRefData
import com.bamboo.cane.shoes.horses.contens.EnhancedShowService
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
import android.os.Build
import android.util.Log
import android.webkit.WebView
import androidx.core.app.ScwcJobIntentService
import com.bamboo.cane.shoes.horses.worker.ServiceManager
import com.bamboo.cane.shoes.horses.worker.job.ScwcJobService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object GameInitializer {
    lateinit var adLimiter: AdLimiter3
    fun init(application: Application, isReleaseData: Boolean) {
        if (!EnhancedShowService.isMainProcess(application)) {
            runCatching {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val processName = getProcessName() ?: "default"
                    WebView.setDataDirectorySuffix(processName)
                }
            }
            return
        }

        GameStart.showLog("0512Local init")
        GameStart.gameApp = application
        GameStart.isRelease = isReleaseData
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
        val path = "${GameStart.gameApp.applicationContext.dataDir.path}/scwc"
        File(path).mkdirs()
        GameStart.showLog("文件名=: $path")
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
                val intent = Intent(GameStart.gameApp, ScwcJobIntentService::class.java)
                ScwcJobIntentService.enqueueWork(GameStart.gameApp, intent)
                delay(5 * 60 * 1000)
            }
        }
    }
}
