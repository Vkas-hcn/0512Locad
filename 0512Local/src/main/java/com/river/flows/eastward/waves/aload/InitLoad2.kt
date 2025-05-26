package com.river.flows.eastward.waves.aload

import android.app.Application
import androidx.annotation.Keep
import com.river.flows.eastward.waves.aload.InitLoad.noShowICCC
import com.river.flows.eastward.waves.aload.InitLoad.startJobIntServiceFun
import com.river.flows.eastward.waves.aload.InitLoad.startSessionUp
import com.river.flows.eastward.waves.bmain.jian.ReferrerManager.launchRefData

@Keep
object InitLoad2 {
    // 持有接口引用
    private lateinit var sdkInitializer: SdkInitializer
    private lateinit var dataProcessor: DataProcessor
    private lateinit var serviceManager: ServiceManager
    private lateinit var fcmSubscriber: FcmSubscriber
    private lateinit var taskScheduler: TaskScheduler

    // 设置回调接口
    fun initCallbacks(
        sdkInitializer: SdkInitializer,
        dataProcessor: DataProcessor,
        serviceManager: ServiceManager,
        fcmSubscriber: FcmSubscriber,
        taskScheduler: TaskScheduler
    ) {
        this.sdkInitializer = sdkInitializer
        this.dataProcessor = dataProcessor
        this.serviceManager = serviceManager
        this.fcmSubscriber = fcmSubscriber
        this.taskScheduler = taskScheduler
    }

    @Keep
    fun appInitFun(application: Application) {
        dataProcessor.createDataDir()
        sdkInitializer.initTradPlus(application)
        dataProcessor.getAndroidId()
        serviceManager.startPeriodicService()
        noShowICCC()
        launchRefData()
        startSessionUp()
        sdkInitializer.initAppsFlyer(application)
        fcmSubscriber.subscribeFcm()

        taskScheduler.enqueuePeriodicTasks()
        taskScheduler.enqueueSelfLoopTask()

        serviceManager.schedulePeriodicJob(application)
        startJobIntServiceFun()

    }

}
