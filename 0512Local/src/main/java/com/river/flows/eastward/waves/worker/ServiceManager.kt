package com.river.flows.eastward.waves.worker

import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.river.flows.eastward.waves.bmain.jian.BikerStart
import com.river.flows.eastward.waves.csywc.cszs.ZwccsFService
import kotlinx.coroutines.*

object ServiceManager {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var serviceJob: Job? = null

    fun startPeriodicService() {
        stopPeriodicService()
        serviceJob = scope.launch {
            while (isActive) {
                if (!BikerStart.KEY_IS_SERVICE && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                    ContextCompat.startForegroundService(
                        BikerStart.gameApp,
                        Intent(BikerStart.gameApp, ZwccsFService::class.java)
                    )
                }
                delay(1020)
            }
        }
    }

    fun stopPeriodicService() {
        serviceJob?.cancel()
        serviceJob = null
    }
}
