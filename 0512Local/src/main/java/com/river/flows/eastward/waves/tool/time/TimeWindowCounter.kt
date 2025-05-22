package com.river.flows.eastward.waves.tool.time

import android.content.SharedPreferences
import com.river.flows.eastward.waves.bmain.jian.BikerStart
import java.text.SimpleDateFormat
import java.util.Date

class TimeWindowCounter(
    private val sp: SharedPreferences,
    private val keyPrefix: String,
    private val windowSize: WindowSize
) {
    enum class WindowSize { HOURLY, DAILY }

    companion object {
        const val MAX_COUNT_SUFFIX = "_max_count"
    }

    private val timeFormat = when(windowSize) {
        WindowSize.HOURLY -> "yyyyMMddHH"
        WindowSize.DAILY -> "yyyyMMdd"
    }

    // 获取当前时间字符串
    private fun getCurrentTimeString() = SimpleDateFormat(timeFormat).format(Date())

    // 检查（带最大限制）
    fun checkAndIncrement(maxCount: Int): Boolean {
        val currentTime = getCurrentTimeString()
        val storedTime = sp.getString("${keyPrefix}_time", null)
        val count = sp.getInt("${keyPrefix}_count", 0)

        if (currentTime != storedTime) {
            resetCounter()
            return true
        }
        BikerStart.showLog("$keyPrefix=$count ----MAX_=$maxCount")
        if (count >= maxCount) return false
        return true
    }

    // 仅增加计数（不检查限制）
    fun increment() {
        val currentTime = getCurrentTimeString()
        val storedTime = sp.getString("${keyPrefix}_time", null)
        val count = sp.getInt("${keyPrefix}_count", 0)

        if (currentTime != storedTime) {
            resetCounter()
        } else {
            sp.edit().putInt("${keyPrefix}_count", count + 1).apply()
        }
    }

    // 重置计数器
    private fun resetCounter() {
        sp.edit()
            .putString("${keyPrefix}_time", getCurrentTimeString())
            .putInt("${keyPrefix}_count", 0)
            .apply()
    }

    // 设置最大限制值
    fun setMaxCount(maxCount: Int) {
        sp.edit().putInt("${keyPrefix}${MAX_COUNT_SUFFIX}", maxCount).apply()
    }

    // 获取当前最大限制值
    fun getMaxCount(): Int {
        return sp.getInt("${keyPrefix}${MAX_COUNT_SUFFIX}", 0)
    }
}
