package com.bamboo.cane.shoes.horses.bmain.jian

import android.annotation.SuppressLint
import android.os.Build
import android.provider.Settings
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.bamboo.cane.shoes.horses.bmain.SwcTool
import com.bamboo.cane.shoes.horses.cnetwork.GamNetUtils
import com.bamboo.cane.shoes.horses.cnetwork.GameCanPost
import com.bamboo.cane.shoes.horses.contens.bean.AppTestData
import com.bamboo.cane.shoes.horses.contens.bean.DataConTentTool
import com.bamboo.cane.shoes.horses.contens.bean.SPUtils
import com.bamboo.cane.shoes.horses.contens.config.AppConfigFactory
import com.bamboo.cane.shoes.horses.contens.config.AppConfigFactory.hasGo
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.TimeUnit

object StartTool {
    fun getFcmFun() {
        if (!GameStart.isRelease) return
        val localStorage = SPUtils.getBoolean(DataConTentTool.fcmState)
        if (localStorage) return
        runCatching {
            Firebase.messaging.subscribeToTopic(AppConfigFactory.FCM)
                .addOnSuccessListener {
                    SPUtils.putBoolean(DataConTentTool.fcmState, true)
                    GameStart.showLog("Firebase: subscribe success")
                }
                .addOnFailureListener {
                    GameStart.showLog("Firebase: subscribe fail")
                }
        }
    }

    fun startSessionUp() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                GameCanPost.postPointDataWithCoroutine(false, "session_up")
                delay(1000 * 60 * 15)
            }
        }
    }

    fun initAppsFlyer() {
        GameStart.showLog("AppsFlyer-id: ${AppConfigFactory.getConfig().appsflyId}")
        AppsFlyerLib.getInstance()
            .init(AppConfigFactory.getConfig().appsflyId, object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(conversionDataMap: MutableMap<String, Any>?) {
                    //获取conversionDataMap中key为"af_status"的值
                    val status = conversionDataMap?.get("af_status") as String?
                    GameStart.showLog("AppsFlyer: $status")
                    GameCanPost.pointInstallAf(status.toString())
                    //打印conversionDataMap值
                    conversionDataMap?.forEach { (key, value) ->
                        GameStart.showLog("AppsFlyer-all: key=$key: value=$value")
                    }
                }

                override fun onConversionDataFail(p0: String?) {
                    GameStart.showLog("AppsFlyer: onConversionDataFail$p0")
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    GameStart.showLog("AppsFlyer: onAppOpenAttribution$p0")
                }

                override fun onAttributionFailure(p0: String?) {
                    GameStart.showLog("AppsFlyer: onAttributionFailure$p0")
                }

            }, GameStart.gameApp)
        val adminData = SPUtils[DataConTentTool.appiddata, ""]
        AppsFlyerLib.getInstance().setCustomerUserId(adminData)
        AppsFlyerLib.getInstance().start(GameStart.gameApp)
        AppsFlyerLib.getInstance()
            .logEvent(GameStart.gameApp, "scwc_install", hashMapOf<String, Any>().apply {
                put("customer_user_id", adminData)
                put("app_version", GamNetUtils.showAppVersion())
                put("os_version", Build.VERSION.RELEASE)
                put("bundle_id", GameStart.gameApp.packageName)
                put("language", "asc_wds")
                put("platform", "raincoat")
                put("android_id", adminData)
            })
    }

    fun noShowICCC() {
        CoroutineScope(Dispatchers.Main).launch {
            val isaData = GameStart.getAdminData()
            if (isaData == null || !isaData.user.profile.type.hasGo()) {
                GameStart.showLog("不是A方案显示图标")
                SwcTool.swcTool(6771)
            }
        }
    }

    fun getAndroidId() {
        val adminData = SPUtils[DataConTentTool.appiddata, ""]
        if (adminData.isEmpty()) {
            val androidId =
                Settings.Secure.getString(
                    GameStart.gameApp.contentResolver,
                    Settings.Secure.ANDROID_ID
                )
            if (!androidId.isNullOrBlank()) {
                SPUtils[DataConTentTool.appiddata] = androidId
            } else {
                SPUtils[DataConTentTool.appiddata] = UUID.randomUUID().toString()
            }
        }
    }
}