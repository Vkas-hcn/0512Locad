package com.bamboo.cane.shoes.horses.tool

import android.app.KeyguardManager
import android.content.Context
import android.os.PowerManager
import com.bamboo.cane.shoes.horses.bmain.jian.GameStart
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.bamboo.cane.shoes.horses.contens.bean.DataConTentTool
import com.bamboo.cane.shoes.horses.contens.bean.SPUtils

object AdUtils {
    var adShowTime: Long = 0
    var showAdTime: Long = 0

    fun initFaceBook() {
        val jsonBean = GameStart.getAdminData()
        val data = jsonBean?.ad?.identifiers?.fallback ?: ""

        if (data.isBlank()) {
            return
        }
        GameStart.showLog("initFaceBook: ${data}")
        FacebookSdk.setApplicationId(data)
        FacebookSdk.sdkInitialize(GameStart.gameApp)
        AppEventsLogger.activateApp(GameStart.gameApp)
    }

    fun canShowLocked(): Boolean {
        val powerManager = GameStart.gameApp.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val keyguardManager = GameStart.gameApp.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        if (powerManager == null || keyguardManager == null) {
            return false
        }
        val isScreenOn = powerManager.isInteractive
        val isInKeyguardRestrictedInputMode = keyguardManager.inKeyguardRestrictedInputMode()

        return !isScreenOn || isInKeyguardRestrictedInputMode
    }


    fun adNumAndPoint(): Boolean {
        val adNum = SPUtils.getInt(DataConTentTool.isAdFailCount)
        val adminBean = GameStart.getAdminData()
        if (adminBean == null) {
            GameStart.showLog("AdminBean is null, cannot determine adNumAndPoint")
            return false
        }
        return adNum > adminBean.ad.timing.maxFailures
    }
}