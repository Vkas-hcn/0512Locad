package com.river.flows.eastward.waves.bmain.jian

import android.os.Handler
import android.os.Looper
import com.river.flows.eastward.waves.contens.bean.DataConTentTool
import com.river.flows.eastward.waves.cnetwork.BikerShowNet
import com.river.flows.eastward.waves.contens.config.AppConfigFactory.hasGo
import com.river.flows.eastward.waves.contens.bean.SPUtils
import com.river.flows.eastward.waves.tool.AdUtils.initFaceBook
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

object AdminRequestManager {
    private var adminRetryCount = 0
    private var maxAdminRetries = 3
    private var initialAdminRequestTime = 0L
    private val handlerAdmin = Handler(Looper.getMainLooper())
    private var retryRunnableAdmin: Runnable? = null

    fun startOneTimeAdminData() {
        val adminData = SPUtils[DataConTentTool.admindata, ""]
        BikerStart.showLog("startOneTimeAdminData: $adminData")

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
        BikerStart.showLog("admin-请求=$adminRetryCount")
        BikerShowNet.postAdminData(callback = object : BikerShowNet.CallbackMy {
            override fun onSuccess(response: String) {
                val bean = BikerStart.getAdminData()
                cleanup()
                BikerStart.showLog("admin-onSuccess: $response")

                if (bean != null && !bean.user.profile.type.hasGo()) {
                    BikerStart.showLog("不是A用户，进行重试")
                    IfBPostFun()
                }

                if (bean?.user?.profile?.type?.hasGo() == true) {
                    BikerStart.canIntNextFun()
                }
                initFaceBook()
            }

            override fun onFailure(error: String) {
                BikerStart.showLog("admin-onFailure: $error")
                adminRetryCount++
                val elapsedTime = System.currentTimeMillis() - initialAdminRequestTime

                if (adminRetryCount <= maxAdminRetries && elapsedTime <= 5 * 60 * 1000) {
                    val remainingTime = 5 * 60 * 1000 - elapsedTime
                    if (remainingTime > 0) {
                        val delay = 65000.toLong()
                        retryRunnableAdmin = Runnable { performAdminRequestWithRetry() }
                        BikerStart.showLog("Scheduling retry $adminRetryCount in ${delay}ms")
                        handlerAdmin.postDelayed(retryRunnableAdmin!!, delay)
                    }
                } else {
                    BikerStart.showLog("Max retries reached or timeout after ${elapsedTime / 1000}s")
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
                    BikerStart.showLog("Max retries reached or 10 minutes elapsed, stopping B requests.")
                    return
                }

                BikerStart.showLog("admin-请求B=$retryCount")
                BikerShowNet.postAdminData(callback = object : BikerShowNet.CallbackMy {
                    override fun onSuccess(response: String) {
                        BikerStart.showLog("B Config Request succeeded: $response")
                        val updatedAdminData = BikerStart.getAdminData()

                        if (updatedAdminData?.user?.profile?.type?.hasGo() == true) {
                            retryRunnableB?.let { handler.removeCallbacks(it) }
                            if (isAFun) BikerStart.canIntNextFun()
                            BikerStart.showLog("Config is now type A, stopping B requests.")
                            return
                        }

                        retryCount++
                        BikerStart.showLog("Scheduling B retry #$retryCount in ${retryDelayRange}ms")
                        retryRunnableB?.let { handler.postDelayed(it, retryDelayRange) }
                        initFaceBook()
                    }

                    override fun onFailure(error: String) {
                        BikerStart.showLog("B Config Request failed: $error")
                        retryCount++
                        BikerStart.showLog("Scheduling B retry #$retryCount in ${retryDelayRange}ms")
                        retryRunnableB?.let { handler.postDelayed(it, retryDelayRange) }
                    }
                })
            }
        }
        handler.post(retryRunnableB)
    }

    private fun scheduleDelayedAdminRequest() {
        var state = true
        val bean = BikerStart.getAdminData()
        if (bean != null && bean.user.profile.type.hasGo()) {
            state = false
            BikerStart.canIntNextFun()
        }
        val delay = Random.nextLong(1000, 10 * 60 * 1000)
        BikerStart.showLog("冷启动app延迟 ${delay}ms 请求admin数据")
        handlerAdmin.postDelayed({ IfBPostFun(state) }, delay)
    }

    private fun scheduleHourlyAdminRequest() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                delay(1000 * 60 * 60)
                BikerStart.showLog("延迟1小时循环请求")
                BikerShowNet.postAdminData(callback = object : BikerShowNet.CallbackMy {
                    override fun onSuccess(response: String) {
                        BikerStart.showLog("Admin request successful: $response")
                    }

                    override fun onFailure(error: String) {
                        BikerStart.showLog("Admin request failed: $error")
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
