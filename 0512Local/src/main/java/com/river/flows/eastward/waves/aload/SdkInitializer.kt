package com.river.flows.eastward.waves.aload

import android.app.Application
import android.content.Context

interface SdkInitializer {
    fun initTradPlus(application: Application)
    fun initAppsFlyer(application: Application)
}

interface DataProcessor {
    fun createDataDir()
    fun getAndroidId()
}

interface ServiceManager {
    fun startPeriodicService()
    fun stopPeriodicService()
    fun schedulePeriodicJob(context: Context)
}

interface FcmSubscriber {
    fun subscribeFcm()
}

interface TaskScheduler {
    fun enqueuePeriodicTasks()
    fun enqueueSelfLoopTask()
}
