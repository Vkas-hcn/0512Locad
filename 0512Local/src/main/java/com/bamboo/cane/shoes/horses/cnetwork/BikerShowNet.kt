package com.bamboo.cane.shoes.horses.cnetwork


import android.annotation.SuppressLint
import com.google.gson.Gson

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import android.util.Base64
import com.bamboo.cane.shoes.horses.bmain.jian.BikerStart
import com.bamboo.cane.shoes.horses.contens.bean.UserAdminBean
import com.bamboo.cane.shoes.horses.contens.config.AppConfigFactory.hasGo
import com.bamboo.cane.shoes.horses.contens.bean.DataConTentTool
import com.bamboo.cane.shoes.horses.contens.bean.SPUtils
import com.bamboo.cane.shoes.horses.contens.config.AppConfigFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object BikerShowNet {

    interface CallbackMy {
        fun onSuccess(response: String)
        fun onFailure(error: String)
    }

    fun showAppVersion(): String {
        return BikerStart.gameApp.packageManager.getPackageInfo(
            BikerStart.gameApp.packageName,
            0
        ).versionName ?: ""
    }


    @SuppressLint("HardwareIds")
    fun adminData(): String {
        return JSONObject().apply {
            put("qrlUsEfTUp", "com.bikeracer.quadrush.bike")
            put("UDiQB", SPUtils[DataConTentTool.appiddata, ""])
            put("IuhlDTg", SPUtils[DataConTentTool.refdata, ""])
//            put("IuhlDTg", "fb4a")
            put("gujBa", showAppVersion())
        }.toString()
    }

    val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    fun postAdminData(callback: CallbackMy) {
        BikerStart.showLog("postAdminData=${AppConfigFactory.getConfig().adminUrl}=${adminData()}")
        val jsonBodyString = JSONObject(adminData()).toString()
        val dt = System.currentTimeMillis().toString()
        val xorEncryptedString = jxData(jsonBodyString, dt)
        val base64EncodedString = Base64.encodeToString(
            xorEncryptedString.toByteArray(StandardCharsets.UTF_8),
            Base64.NO_WRAP
        )


        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = base64EncodedString.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(AppConfigFactory.getConfig().adminUrl)
            .post(requestBody)
            .addHeader("dt", dt)
            .build()
        BikerUpData.postPointDataWithCoroutine(false, "reqadmin")
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                BikerStart.showLog("admin----Request failed: ${e.message}")

                callback.onFailure("Request failed: ${e.message}")
                BikerUpData.getadmin(false, "timeout")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code != 200) {
                    callback.onFailure("Unexpected code $response")
                    BikerUpData.getadmin(false, response.code.toString())
                    return
                }
                try {
                    val timestampResponse = response.header("dt")
                        ?: throw IllegalArgumentException("Timestamp missing in headers")

                    val decodedBytes = Base64.decode(response.body?.string() ?: "", Base64.DEFAULT)
                    val decodedString = String(decodedBytes, Charsets.UTF_8)
                    val finalData = jxData(decodedString, timestampResponse)
                    val jsonResponse = JSONObject(finalData)
                    val jsonData = parseAdminRefData(jsonResponse.toString())
                    val adminBean = runCatching {
                        Gson().fromJson(jsonData, UserAdminBean::class.java)
                    }.getOrNull()

                    if (adminBean == null) {
                        callback.onFailure("The data is not in the correct format")
                        BikerUpData.getadmin(false, null)

                    } else {
                        if (BikerStart.getAdminData() == null) {
                            BikerStart.putAdminData(jsonData)
                        } else if (adminBean.user.profile.type.hasGo()) {
                            BikerStart.putAdminData(jsonData)
                        }
                        BikerUpData.getadmin(
                            adminBean.user.profile.type.hasGo(),
                            response.code.toString()
                        )

                        callback.onSuccess(jsonData)
                    }
                } catch (e: Exception) {
                    callback.onFailure("Decryption failed: ${e.message}")
                }
            }
        })

    }

    private fun jxData(text: String, dt: String): String {
        val cycleKey = dt.toCharArray()
        val keyLength = cycleKey.size
        return text.mapIndexed { index, char ->
            char.toInt().xor(cycleKey[index % keyLength].toInt()).toChar()
        }.joinToString("")
    }

    private fun parseAdminRefData(jsonString: String): String {
        try {
            val confString = JSONObject(jsonString).getJSONObject("ApX").getString("conf")
            return confString
        } catch (e: Exception) {
            return ""
        }
    }

    fun postPutData(body: Any, callbackData: CallbackMy) {
        val jsonBodyString = JSONObject(body.toString()).toString()
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            jsonBodyString
        )

        val request = Request.Builder()
            .url(AppConfigFactory.getConfig().upUrl)
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                BikerStart.showLog("admin-Error: ${e.message}")
                callbackData.onFailure(e.message ?: "Unknown error")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        callbackData.onFailure("Unexpected code $response")
                    } else {
                        val responseData = response.body?.string() ?: ""
                        callbackData.onSuccess(responseData)
                    }
                }
            }
        })
    }

}
