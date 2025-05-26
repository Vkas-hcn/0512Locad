package com.river.flows.eastward.waves.cnetwork

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.river.flows.eastward.waves.cnetwork.BikerShowNet.showAppVersion
import com.river.flows.eastward.waves.tool.AdUtils
import org.json.JSONObject
import java.util.UUID
import kotlin.random.Random
import com.appsflyer.AFAdRevenueData
import com.appsflyer.AdRevenueScheme
import com.appsflyer.AppsFlyerLib
import com.appsflyer.MediationNetwork
import com.river.flows.eastward.waves.bmain.jian.BikerStart
import com.river.flows.eastward.waves.bmain.jian.GameInitializer
import com.facebook.appevents.AppEventsLogger
import com.river.flows.eastward.waves.contens.config.AppConfigFactory.hasHo
import com.river.flows.eastward.waves.contens.bean.DataConTentTool
import com.river.flows.eastward.waves.contens.bean.SPUtils
import com.tradplus.ads.base.bean.TPAdInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.math.BigDecimal
import java.util.Currency
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
object BikerUpData {
    private fun topJsonData(context: Context): JSONObject {
        val studious = JSONObject().apply {
            //manufacturer
            put("thy", Build.MANUFACTURER)
            //bundle_id
            put("waldron", context.packageName)
            //client_ts
            put("vibrate", System.currentTimeMillis())
            //operator 传假值字符串
            put("assassin", "ccse")
            //gaid
            put("section", "")
        }
        val emboss = JSONObject().apply {
            //log_id
            put("roll", UUID.randomUUID().toString())
            //brand
            put("brice", "xxx")
            //system_language//假值
            put("oxonian", "asc_wds")
            //os_version
            put("neigh", Build.VERSION.RELEASE)
            //os
            put("hangdog", "ware")
            //device_model//传空值
            put("deere", "")
            //android_id
            put("sycamore", SPUtils[DataConTentTool.appiddata, ""])
            //distinct_id
            put("obsidian", SPUtils[DataConTentTool.appiddata, ""])

            //app_version
            put("avert", showAppVersion())

        }
        val json = JSONObject().apply {
            put("studious", studious)
            put("emboss", emboss)
        }

        return json
    }


    private fun upInstallJson(context: Context): String {
        val chinese = JSONObject().apply {
            //build
            put("staley", "build/${Build.ID}")

            //referrer_url
            put("doorstep", SPUtils[DataConTentTool.refdata, ""])

            //user_agent
            put("peepy", "")

            //lat
            put("medium", "serbia")

            //referrer_click_timestamp_seconds
            put("operant", 0)

            //install_begin_timestamp_seconds
            put("locale", 0)

            //referrer_click_timestamp_server_seconds
            put("liqueur", 0)

            //install_begin_timestamp_server_seconds
            put("empiric", 0)

            //install_first_seconds
            put("scrubby", getFirstInstallTime(context))

            //last_update_seconds
            put("village", 0)
        }
        return topJsonData(context).apply {
            put("chinese", chinese)

        }.toString()
    }


    private fun upAdJson(context: Context, adValue: TPAdInfo): String {
        Log.e("TAG", "广告原值:${adValue.ecpm}")
        return topJsonData(context).apply {
            //ad_pre_ecpm
            put("acorn", adValue.ecpm.toDouble() * 1000)
            //currency
            put("quantile", "USD")
            //ad_network
            put("slake", adValue.adSourceName)
            //ad_source
            put("covalent", "Tradplus")
            //ad_code_id
            put("hyades", adValue.tpAdUnitId)
            //ad_pos_id
            put("clip", "int")
            //ad_rit_id
            put("seedy", "")
            //ad_sense
            put("grease", "")
            //ad_format
            put("hit", adValue.format)
            put("eruption", "ribald")
        }.toString()
    }

    private fun upPointJson(name: String): String {
        return topJsonData(BikerStart.gameApp).apply {
            put("eruption", name)
        }.toString()
    }

    private fun upPointJson(
        name: String,
        key1: String? = null,
        keyValue1: Any? = null,
        key2: String? = null,
        keyValue2: Any? = null,
        key3: String? = null,
        keyValue3: Any? = null,
        key4: String? = null,
        keyValue4: Any? = null
    ): String {

        return topJsonData(BikerStart.gameApp).apply {
            put("eruption", name)

            put("helix", JSONObject().apply {
                if (key1 != null) {
                    put(key1, keyValue1)
                }
                if (key2 != null) {
                    put(key2, keyValue2)
                }
                if (key3 != null) {
                    put(key3, keyValue3)
                }
                if (key4 != null) {
                    put(key4, keyValue4)
                }
            })
        }.toString()
    }


    fun postInstallDataWithCoroutine(context: Context) {
        val maxRetries = 20
        var success = false
        var job: Job? = null
        val is_int_ref = SPUtils[DataConTentTool.IS_INT_JSON, ""]
        val data = is_int_ref.ifEmpty {
            val newData = upInstallJson(context)
            SPUtils[DataConTentTool.IS_INT_JSON] = newData
            newData
        }

        BikerStart.showLog("Install: data=$data")
        job = CoroutineScope(Dispatchers.IO).launch {
            var currentRetry = 0
            var isProcessing = false

            while (currentRetry <= maxRetries && !success) {
                if (!isProcessing) {
                    isProcessing = true
                    currentRetry++

                    BikerStart.showLog("Install: retryCount=$currentRetry")

                    // 计算随机延迟时间
                    val delayTime = Random.nextLong(10_000, 40_000)

                    // 启动网络请求
                    launch(Dispatchers.IO) {
                        try {
                            // 执行网络请求
                            val response = BikerShowNet.postPutDataAsync(data)
                            // 处理成功情况
                            BikerStart.showLog("Install:请求成功: $response")
                            SPUtils[DataConTentTool.IS_INT_JSON] = ""
                            success = true
                            job?.cancel()  // 成功后取消任务
                        } catch (e: Exception) {
                            // 处理失败情况
                            isProcessing = false
                            BikerStart.showLog("Install:请求失败: ${e.message}")

                            if (currentRetry >= maxRetries) {
                                BikerStart.showLog("Install:请求失败，达到最大重试次数: $maxRetries")
                                job?.cancel()
                            }
                        }
                    }
                    delay(delayTime)
                    isProcessing = false
                }
            }
        }
    }


    suspend fun BikerShowNet.postPutDataAsync(data: String): String =
        suspendCancellableCoroutine { continuation ->
            postPutData(data, object : BikerShowNet.CallbackMy {
                override fun onSuccess(response: String) {
                    continuation.resume(response)
                }

                override fun onFailure(error: String) {
                    continuation.resumeWithException(Exception(error))
                }
            })
        }

    fun postAdmobDataWithCoroutine(adValue: TPAdInfo) {
        val maxRetries = 20
        var success = false
        var job: Job? = null
        val data = upAdJson(BikerStart.gameApp, adValue)
        BikerStart.showLog("Ad: data=$data")
        job = CoroutineScope(Dispatchers.IO).launch {
            var currentRetry = 0
            var isProcessing = false

            while (currentRetry <= maxRetries && !success) {
                if (!isProcessing) {
                    isProcessing = true
                    currentRetry++

                    BikerStart.showLog("Ad: retryCount=$currentRetry")

                    // 计算随机延迟时间
                    val delayTime = Random.nextLong(10_000, 40_000)

                    // 启动网络请求
                    launch(Dispatchers.IO) {
                        try {
                            // 执行网络请求
                            val response = BikerShowNet.postPutDataAsync(data)
                            // 处理成功情况
                            BikerStart.showLog("Ad:请求成功: $response")
                            success = true
                            job?.cancel()
                        } catch (e: Exception) {
                            // 处理失败情况
                            isProcessing = false
                            BikerStart.showLog("Ad:请求失败: ${e.message}")
                            if (currentRetry >= maxRetries) {
                                BikerStart.showLog("Ad:请求失败，达到最大重试次数: $maxRetries")
                                job?.cancel()
                            }
                        }
                    }
                    delay(delayTime)
                    isProcessing = false
                }
            }
            postAdValue(adValue)
        }
    }


    fun postPointDataWithCoroutine(
        isAdMinCon: Boolean,
        name: String,
        key1: String? = null,
        keyValue1: Any? = null,
        key2: String? = null,
        keyValue2: Any? = null
    ) {
        var success = false
        var job: Job? = null
        val adminBean = BikerStart.getAdminData()

        if (!isAdMinCon && (adminBean != null && !adminBean.user.permissions.uploadEnabled.hasHo())) {
            return
        }
        val data = if (key1 != null) {
            upPointJson(name, key1, keyValue1, key2, keyValue2)
        } else {
            upPointJson(name)
        }
        val retriesNum = if (isAdMinCon) {
            20
        } else {
            Random.nextInt(2, 5)
        }

        BikerStart.showLog("Point-${name}-开始打点--${data}")
        job = CoroutineScope(Dispatchers.IO).launch {
            var currentRetry = 0
            var isProcessing = false

            while (currentRetry <= retriesNum && !success) {
                if (!isProcessing) {
                    isProcessing = true
                    currentRetry++

                    BikerStart.showLog("Point-${name}=$currentRetry")

                    // 计算随机延迟时间
                    val delayTime = Random.nextLong(10_000, 40_000)

                    // 启动网络请求
                    launch(Dispatchers.IO) {
                        try {
                            // 执行网络请求
                            val response = BikerShowNet.postPutDataAsync(data)
                            // 处理成功情况
                            BikerStart.showLog("Point-${name}:请求成功: $response")
                            success = true
                            job?.cancel()  // 成功后取消任务
                        } catch (e: Exception) {
                            // 处理失败情况
                            isProcessing = false
                            BikerStart.showLog("Point-${name}:请求失败: ${e.message}")

                            if (currentRetry >= retriesNum) {
                                BikerStart.showLog("Point-${name}:请求失败，达到最大重试次数: $retriesNum")
                                job?.cancel()
                            }
                        }
                    }
                    delay(delayTime)
                    isProcessing = false
                }
            }
        }
    }


    private fun getFirstInstallTime(context: Context): Long {
        try {
            val packageInfo =
                context.packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.firstInstallTime / 1000
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return 0
    }


    private fun postAdValue(adValue: TPAdInfo) {
        val ecmVVVV = try {
            adValue.ecpm.toDouble() / 1000.0
        } catch (e: NumberFormatException) {
            BikerStart.showLog("Invalid ecpmPrecision value: ${adValue.ecpm}, using default value 0.0")
            0.0
        }
        val adRevenueData = AFAdRevenueData(
            adValue.adSourceName,
            MediationNetwork.TRADPLUS,
            "USD",
            ecmVVVV
        )
        val additionalParameters: MutableMap<String, Any> = HashMap()
        additionalParameters[AdRevenueScheme.AD_UNIT] = adValue.adSourceId
        additionalParameters[AdRevenueScheme.AD_TYPE] = "Interstitial"
        AppsFlyerLib.getInstance().logAdRevenue(adRevenueData, additionalParameters)

        logAdImpressionRevenue(ecmVVVV.toString())

        val jsonBean = BikerStart.getAdminData()
        val data = jsonBean?.ad?.identifiers?.fallback ?: ""
        if (data.isBlank()) {
            return
        }
        if (jsonBean != null && data.isNotEmpty()) {
            try {
                AppEventsLogger.newLogger(BikerStart.gameApp).logPurchase(
                    BigDecimal(ecmVVVV.toString()),
                    Currency.getInstance("USD")
                )
            } catch (e: NumberFormatException) {
                BikerStart.showLog("Invalid ecpmPrecision value: ${adValue.ecpm}, skipping logPurchase")
            }
        }
    }


    private fun logAdImpressionRevenue(adFormat: String) {
        val firebaseAnalytics = Firebase.analytics
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.VALUE, adFormat)       // 广告收入（单位：美元）
            putString(FirebaseAnalytics.Param.CURRENCY, "USD")               // 货币类型
        }
        firebaseAnalytics.logEvent("ad_impression_QuadBikesRush", params)
    }

    fun getadmin(canNext: Boolean, codeInt: String?) {
        var isuserData: String? = null

        if (codeInt == null) {
            isuserData = null
        } else if (codeInt != "200") {
            isuserData = codeInt
        } else if (canNext) {
            isuserData = "a"
        } else {
            isuserData = "b"
        }

        postPointDataWithCoroutine(true, "getadmin", "getstring", isuserData)
    }


    fun showsuccessPoint() {
        val time = (System.currentTimeMillis() - AdUtils.showAdTime) / 1000
        postPointDataWithCoroutine(false, "show", "t", time)
        AdUtils.showAdTime = 0
    }

    fun firstExternalBombPoint() {
        if (SPUtils[DataConTentTool.firstPoint, false]) {
            return
        }
        val instalTime = GameInitializer.getInstallTimeInSeconds()
        postPointDataWithCoroutine(false, "first_start", "time", instalTime)
        SPUtils.putBoolean(DataConTentTool.firstPoint, true)

    }

    fun pointInstallAf(data: String) {
        if (data.contains("non_organic", true) && !SPUtils[DataConTentTool.adOrgPoint, false]) {
            postPointDataWithCoroutine(false, "non_organic")
            SPUtils.putBoolean(DataConTentTool.adOrgPoint, true)
        }
    }

    fun getLiMitData() {
        if (!SPUtils[DataConTentTool.getlimit, false]) {
            postPointDataWithCoroutine(true, "getlimit")
            SPUtils.putBoolean(DataConTentTool.getlimit, true)
        }
    }
}