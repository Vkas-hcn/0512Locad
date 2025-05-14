package com.bamboo.cane.shoes.horses.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bamboo.cane.shoes.horses.bmain.jian.GameStart

class SecondWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        // 执行第二个任务逻辑
        GameStart.showLog( "执行第二个任务")

        // 返回成功以触发循环任务
        return Result.success()
    }
}