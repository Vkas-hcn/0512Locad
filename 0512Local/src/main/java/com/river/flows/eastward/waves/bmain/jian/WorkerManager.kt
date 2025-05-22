package com.river.flows.eastward.waves.bmain.jian

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.river.flows.eastward.waves.worker.FirstWorker
import com.river.flows.eastward.waves.worker.LoopWorker
import java.util.concurrent.TimeUnit

object WorkerManager {
    private const val CHAIN_WORK_NAME = "task_chain_work"
    private const val PERIODIC_WORK_NAME = "periodic_chain_work"


    // 创建周期性任务链
    fun enqueuePeriodicChain() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .build()

        val periodicWork = PeriodicWorkRequestBuilder<FirstWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(BikerStart.gameApp).enqueueUniquePeriodicWork(
            PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicWork
        )
    }

    fun enqueueSelfLoop() {
        val work = OneTimeWorkRequestBuilder<LoopWorker>().build()
        WorkManager.getInstance(BikerStart.gameApp).enqueue(work)
    }
}
