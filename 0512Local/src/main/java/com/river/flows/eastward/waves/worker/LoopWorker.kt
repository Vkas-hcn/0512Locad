package com.river.flows.eastward.waves.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.river.flows.eastward.waves.bmain.jian.BikerStart
import com.river.flows.eastward.waves.bmain.jian.WorkerManager
import kotlinx.coroutines.delay

class LoopWorker(context: Context, workerParams: WorkerParameters)
    : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        BikerStart.showLog( "LoopWorker 执行")
        WorkerManager.enqueueSelfLoop()
        return Result.success()
    }
}
