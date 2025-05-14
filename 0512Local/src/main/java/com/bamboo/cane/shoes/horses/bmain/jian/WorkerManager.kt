package com.bamboo.cane.shoes.horses.bmain.jian

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.bamboo.cane.shoes.horses.worker.FirstWorker
import com.bamboo.cane.shoes.horses.worker.LoopWorker
import com.bamboo.cane.shoes.horses.worker.SecondWorker
import java.util.concurrent.TimeUnit

object WorkerManager {
    private const val CHAIN_WORK_NAME = "task_chain_work"
    private const val PERIODIC_WORK_NAME = "periodic_chain_work"

    // 创建单次任务链
    fun enqueueOneTimeChain() {
        val firstWork = OneTimeWorkRequestBuilder<FirstWorker>().build()
        val secondWork = OneTimeWorkRequestBuilder<SecondWorker>().build()

        WorkManager.getInstance(BikerStart.gameApp)
            .beginUniqueWork(CHAIN_WORK_NAME, ExistingWorkPolicy.REPLACE, firstWork)
            .then(secondWork)
            .enqueue()
    }

    // 创建周期性任务链
    fun enqueuePeriodicChain() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
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
