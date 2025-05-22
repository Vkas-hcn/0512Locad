package com.river.flows.eastward.waves.tool

import android.app.KeyguardManager
import android.content.Context
import android.os.PowerManager
import com.river.flows.eastward.waves.bmain.jian.BikerStart
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.river.flows.eastward.waves.contens.bean.DataConTentTool
import com.river.flows.eastward.waves.contens.bean.SPUtils

object AdUtils {
    var adShowTime: Long = 0
    var showAdTime: Long = 0

    fun initFaceBook() {
        val jsonBean = BikerStart.getAdminData()
        val data = jsonBean?.ad?.identifiers?.fallback ?: ""

        if (data.isBlank()) {
            return
        }
        BikerStart.showLog("initFaceBook: ${data}")
        FacebookSdk.setApplicationId(data)
        FacebookSdk.sdkInitialize(BikerStart.gameApp)
        AppEventsLogger.activateApp(BikerStart.gameApp)
    }

    fun canShowLocked(): Boolean {
        val powerManager = BikerStart.gameApp.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val keyguardManager = BikerStart.gameApp.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        if (powerManager == null || keyguardManager == null) {
            return false
        }
        val isScreenOn = powerManager.isInteractive
        val isInKeyguardRestrictedInputMode = keyguardManager.inKeyguardRestrictedInputMode()

        return !isScreenOn || isInKeyguardRestrictedInputMode
    }


    fun adNumAndPoint(): Boolean {
        val adNum = SPUtils.getInt(DataConTentTool.isAdFailCount)
        val adminBean = BikerStart.getAdminData()
        if (adminBean == null) {
            BikerStart.showLog("AdminBean is null, cannot determine adNumAndPoint")
            return false
        }
        return adNum > adminBean.ad.timing.maxFailures
    }
}