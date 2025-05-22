package com.river.flows.eastward.waves.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class FirstWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        return Result.success()
    }
}