package com.bamboo.cane.shoes.horses.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.bamboo.cane.shoes.horses.bmain.jian.BikerStart
import kotlinx.coroutines.delay

class LoopWorker(context: Context, workerParams: WorkerParameters)
    : CoroutineWorker(context, workerParams) {

    companion object {
        private var retryCount = 0
    }

    override suspend fun doWork(): Result {
        BikerStart.showLog( "LoopWorker 执行次数: ${retryCount + 1}")
        delay(1000*60)
        retryCount++
        val nextWork = OneTimeWorkRequestBuilder<LoopWorker>().build()
        WorkManager.getInstance(applicationContext).enqueue(nextWork)

        return Result.success()
    }
}
