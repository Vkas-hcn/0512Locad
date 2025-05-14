package com.bamboo.cane.shoes.horses.bmain.jian

import android.os.Handler
import android.os.Looper
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.bamboo.cane.shoes.horses.contens.bean.DataConTentTool
import com.bamboo.cane.shoes.horses.cnetwork.GamNetUtils
import com.bamboo.cane.shoes.horses.contens.config.AppConfigFactory.hasGo
import com.bamboo.cane.shoes.horses.contens.bean.SPUtils
import com.bamboo.cane.shoes.horses.tool.AdUtils.initFaceBook
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.random.Random

object AdminRequestManager {
    private var adminRetryCount = 0
    private var maxAdminRetries = 3
    private var initialAdminRequestTime = 0L
    private val handlerAdmin = Handler(Looper.getMainLooper())
    private var retryRunnableAdmin: Runnable? = null

    fun startOneTimeAdminData() {
        val adminData = SPUtils[DataConTentTool.admindata, ""]
        GameStart.showLog("startOneTimeAdminData: $adminData")

        if (adminData.isEmpty()) {
            startAdminDataWithRetry()
        } else {
            scheduleDelayedAdminRequest()
        }
        scheduleHourlyAdminRequest()
    }

    private fun startAdminDataWithRetry() {
        adminRetryCount = 0
        maxAdminRetries = (3..5).random()
        initialAdminRequestTime = System.currentTimeMillis()
        performAdminRequestWithRetry()
    }

    private fun performAdminRequestWithRetry() {
        GameStart.showLog("admin-请求=$adminRetryCount")
        GamNetUtils.postAdminData(callback = object : GamNetUtils.CallbackMy {
            override fun onSuccess(response: String) {
                val bean = GameStart.getAdminData()
                cleanup()
                GameStart.showLog("admin-onSuccess: $response")

                if (bean != null && !bean.user.profile.type.hasGo()) {
                    GameStart.showLog("不是A用户，进行重试")
                    IfBPostFun()
                }

                if (bean?.user?.profile?.type?.hasGo() == true) {
                    GameStart.canIntNextFun()
                }
                initFaceBook()
            }

            override fun onFailure(error: String) {
                GameStart.showLog("admin-onFailure: $error")
                adminRetryCount++
                val elapsedTime = System.currentTimeMillis() - initialAdminRequestTime

                if (adminRetryCount <= maxAdminRetries && elapsedTime <= 5 * 60 * 1000) {
                    val remainingTime = 5 * 60 * 1000 - elapsedTime
                    if (remainingTime > 0) {
                        val delay = 65000.toLong()
                        retryRunnableAdmin = Runnable { performAdminRequestWithRetry() }
                        GameStart.showLog("Scheduling retry $adminRetryCount in ${delay}ms")
                        handlerAdmin.postDelayed(retryRunnableAdmin!!, delay)
                    }
                } else {
                    GameStart.showLog("Max retries reached or timeout after ${elapsedTime / 1000}s")
                }
            }
        })
    }

    fun IfBPostFun(isAFun: Boolean = true) {
        val startTime = System.currentTimeMillis()
        var retryCount = 0
        val maxRetryCount = (3..5).random()
        val retryDelayRange = 45000L
        var retryRunnableB: Runnable? = null
        val handler = Handler(Looper.getMainLooper())

        retryRunnableB = object : Runnable {
            override fun run() {
                val elapsedTime = System.currentTimeMillis() - startTime

                if (elapsedTime > 10 * 60 * 1000 || retryCount >= maxRetryCount) {
                    GameStart.showLog("Max retries reached or 10 minutes elapsed, stopping B requests.")
                    return
                }

                GameStart.showLog("admin-请求B=$retryCount")
                GamNetUtils.postAdminData(callback = object : GamNetUtils.CallbackMy {
                    override fun onSuccess(response: String) {
                        GameStart.showLog("B Config Request succeeded: $response")
                        val updatedAdminData = GameStart.getAdminData()

                        if (updatedAdminData?.user?.profile?.type?.hasGo() == true) {
                            retryRunnableB?.let { handler.removeCallbacks(it) }
                            if (isAFun) GameStart.canIntNextFun()
                            GameStart.showLog("Config is now type A, stopping B requests.")
                            return
                        }

                        retryCount++
                        GameStart.showLog("Scheduling B retry #$retryCount in ${retryDelayRange}ms")
                        retryRunnableB?.let { handler.postDelayed(it, retryDelayRange) }
                        initFaceBook()
                    }

                    override fun onFailure(error: String) {
                        GameStart.showLog("B Config Request failed: $error")
                        retryCount++
                        GameStart.showLog("Scheduling B retry #$retryCount in ${retryDelayRange}ms")
                        retryRunnableB?.let { handler.postDelayed(it, retryDelayRange) }
                    }
                })
            }
        }
        handler.post(retryRunnableB)
    }

    private fun scheduleDelayedAdminRequest() {
        var state = true
        val bean = GameStart.getAdminData()
        if (bean != null && bean.user.profile.type.hasGo()) {
            state = false
            GameStart.canIntNextFun()
        }
        val delay = Random.nextLong(1000, 10 * 60 * 1000)
        GameStart.showLog("冷启动app延迟 ${delay}ms 请求admin数据")
        handlerAdmin.postDelayed({ IfBPostFun(state) }, delay)
    }

    private fun scheduleHourlyAdminRequest() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                delay(1000 * 60 * 60)
                GameStart.showLog("延迟1小时循环请求")
                GamNetUtils.postAdminData(callback = object : GamNetUtils.CallbackMy {
                    override fun onSuccess(response: String) {
                        GameStart.showLog("Admin request successful: $response")
                    }

                    override fun onFailure(error: String) {
                        GameStart.showLog("Admin request failed: $error")
                    }
                })
            }
        }
    }

    fun cleanup() {
        retryRunnableAdmin?.let {
            handlerAdmin.removeCallbacks(it)
            retryRunnableAdmin = null
        }
    }
}
