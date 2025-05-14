package com.bamboo.cane.shoes.horses.worker.job

import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log

@SuppressLint("SpecifyJobSchedulerIdRange")
class ScwcJobService : JobService() {

    @SuppressLint("SpecifyJobSchedulerIdRange")
    override fun onStartJob(params: JobParameters): Boolean {
        Log.e("JobScheduler", "Job started")

        return true // 返回true表示还有工作在进行，完成后需要调用jobFinished
    }

    override fun onStopJob(params: JobParameters): Boolean {
        Log.d("JobScheduler", "Job stopped")
        return true // 返回true表示如果任务被停止，希望稍后重新调度
    }
}