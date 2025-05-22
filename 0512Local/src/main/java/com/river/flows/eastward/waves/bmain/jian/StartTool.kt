package com.river.flows.eastward.waves.bmain.jian

import android.os.Build
import android.provider.Settings
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.river.flows.eastward.waves.bmain.SwccsTool
import com.river.flows.eastward.waves.cnetwork.BikerShowNet
import com.river.flows.eastward.waves.cnetwork.BikerUpData
import com.river.flows.eastward.waves.contens.bean.DataConTentTool
import com.river.flows.eastward.waves.contens.bean.SPUtils
import com.river.flows.eastward.waves.contens.config.AppConfigFactory
import com.river.flows.eastward.waves.contens.config.AppConfigFactory.hasGo
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

object StartTool {
    fun getFcmFun() {
        if (!BikerStart.isRelease) return
        val localStorage = SPUtils.getBoolean(DataConTentTool.fcmState)
        if (localStorage) return
        runCatching {
            Firebase.messaging.subscribeToTopic(AppConfigFactory.FCM)
                .addOnSuccessListener {
                    SPUtils.putBoolean(DataConTentTool.fcmState, true)
                    BikerStart.showLog("Firebase: subscribe success")
                }
                .addOnFailureListener {
                    BikerStart.showLog("Firebase: subscribe fail")
                }
        }
    }

    fun startSessionUp() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                BikerUpData.postPointDataWithCoroutine(false, "session_up")
                delay(1000 * 60 * 15)
            }
        }
    }

    fun initAppsFlyer() {
        BikerStart.showLog("AppsFlyer-id: ${AppConfigFactory.getConfig().appsflyId}")
        AppsFlyerLib.getInstance()
            .init(AppConfigFactory.getConfig().appsflyId, object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(conversionDataMap: MutableMap<String, Any>?) {
                    //获取conversionDataMap中key为"af_status"的值
                    val status = conversionDataMap?.get("af_status") as String?
                    BikerStart.showLog("AppsFlyer: $status")
                    BikerUpData.pointInstallAf(status.toString())
                    //打印conversionDataMap值
                    conversionDataMap?.forEach { (key, value) ->
                        BikerStart.showLog("AppsFlyer-all: key=$key: value=$value")
                    }
                }

                override fun onConversionDataFail(p0: String?) {
                    BikerStart.showLog("AppsFlyer: onConversionDataFail$p0")
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    BikerStart.showLog("AppsFlyer: onAppOpenAttribution$p0")
                }

                override fun onAttributionFailure(p0: String?) {
                    BikerStart.showLog("AppsFlyer: onAttributionFailure$p0")
                }

            }, BikerStart.gameApp)
        val adminData = SPUtils[DataConTentTool.appiddata, ""]
        AppsFlyerLib.getInstance().setCustomerUserId(adminData)
        AppsFlyerLib.getInstance().start(BikerStart.gameApp)
        AppsFlyerLib.getInstance()
            .logEvent(BikerStart.gameApp, "scwc_install", hashMapOf<String, Any>().apply {
                put("customer_user_id", adminData)
                put("app_version", BikerShowNet.showAppVersion())
                put("os_version", Build.VERSION.RELEASE)
                put("bundle_id", BikerStart.gameApp.packageName)
                put("language", "asc_wds")
                put("platform", "raincoat")
                put("android_id", adminData)
            })
    }

    fun noShowICCC() {
        CoroutineScope(Dispatchers.Main).launch {
            val isaData = BikerStart.getAdminData()
            if (isaData == null || !isaData.user.profile.type.hasGo()) {
                BikerStart.showLog("不是A方案显示图标")
                SwccsTool.swcTool(6771)
            }
        }
    }

    fun getAndroidId() {
        val adminData = SPUtils[DataConTentTool.appiddata, ""]
        if (adminData.isEmpty()) {
            val androidId =
                Settings.Secure.getString(
                    BikerStart.gameApp.contentResolver,
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