package com.river.flows.eastward.waves.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class FirstWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        Log.e("TAG", "doWork: FirstWorker", )
        return Result.success()
    }
}