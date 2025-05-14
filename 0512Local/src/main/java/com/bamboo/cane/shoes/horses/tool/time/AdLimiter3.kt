package com.bamboo.cane.shoes.horses.tool.time

import android.content.Context
import com.bamboo.cane.shoes.horses.bmain.jian.BikerStart
import com.bamboo.cane.shoes.horses.cnetwork.BikerUpData

class AdLimiter3(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("ad_limits", Context.MODE_PRIVATE)

    // 初始化各计数器
    private val hourlyShowCounter = TimeWindowCounter(
        sharedPreferences,
        "hourly_show",
        TimeWindowCounter.WindowSize.HOURLY
    )

    private val dailyShowCounter = TimeWindowCounter(
        sharedPreferences,
        "daily_show",
        TimeWindowCounter.WindowSize.DAILY
    )

    private val dailyClickCounter = TimeWindowCounter(
        sharedPreferences,
        "daily_click",
        TimeWindowCounter.WindowSize.DAILY
    )

    // 当前限制配置
    private var currentLimits = LimitsConfig()

    // 限制配置数据类
    data class LimitsConfig(
        var hourlyShow: Int = 0,
        var dailyShow: Int = 0,
        var dailyClick: Int = 0
    )

    // 配置最大限制值
    fun configureLimits(hourlyShow: Int, dailyShow: Int, dailyClick: Int) {
        currentLimits = LimitsConfig(hourlyShow, dailyShow, dailyClick)
        hourlyShowCounter.setMaxCount(hourlyShow)
        dailyShowCounter.setMaxCount(dailyShow)
        dailyClickCounter.setMaxCount(dailyClick)
    }

    // 检查广告展示权限
    fun canShowAd(isCanUp: Boolean = false): Boolean {
        // 从网络获取最新限制配置
        fetchLimitsFromNetwork()
        // 检查日展示限制
        if (!dailyShowCounter.checkAndIncrement(currentLimits.dailyShow)) {
            handleLimitExceeded(isCanUp, "day_limit")
            return false
        }
        // 检查日点击限制（新增）
        if (!dailyClickCounter.checkAndIncrement(currentLimits.dailyClick)) {
            handleLimitExceeded(isCanUp, "click_limit")
            return false
        }
        // 检查小时展示限制
        if (!hourlyShowCounter.checkAndIncrement(currentLimits.hourlyShow)) {
            if (isCanUp) {
                BikerUpData.postPointDataWithCoroutine(false, "ispass", "string", "hour_limit")
            }
            return false
        }

        return true
    }

    // 记录广告展示
    fun recordAdShown() {
        hourlyShowCounter.increment()
        dailyShowCounter.increment()
    }

    // 记录广告点击
    fun recordAdClicked() {
        dailyClickCounter.increment()
    }

    // 从网络获取最新限制配置
    private fun fetchLimitsFromNetwork() {
        val jsonBean = BikerStart.getAdminData() ?: return
        val newLimits = LimitsConfig(
            hourlyShow = jsonBean.user.limits.ad.hourly,
            dailyShow = jsonBean.user.limits.ad.daily,
            dailyClick = jsonBean.user.limits.click.daily
        )
        configureLimits(newLimits.hourlyShow, newLimits.dailyShow, newLimits.dailyClick)
    }

    // 处理限制超出情况
    private fun handleLimitExceeded(isCanUp: Boolean, reason: String) {
        if (isCanUp) {
            BikerUpData.postPointDataWithCoroutine(false, "ispass", "string", reason)
            BikerUpData.getLiMitData()
        }
    }
}
