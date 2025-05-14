package com.bamboo.cane.shoes.horses.bmain.jian

import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.bamboo.cane.shoes.horses.cnetwork.BikerUpData
import com.bamboo.cane.shoes.horses.contens.bean.DataConTentTool
import com.bamboo.cane.shoes.horses.contens.bean.SPUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object ReferrerManager {
    fun launchRefData() {
        if (SPUtils[DataConTentTool.refdata, ""].isNotEmpty()) {
            AdminRequestManager.startOneTimeAdminData()
            SPUtils[DataConTentTool.IS_INT_JSON, ""].takeIf { it.isNotEmpty() }?.let {
                BikerUpData.postInstallDataWithCoroutine(BikerStart.gameApp)
            }
            return
        }

        startRefDataCheckLoop()
    }

    private fun startRefDataCheckLoop() {
        CoroutineScope(Dispatchers.IO).launch {
            while (SPUtils[DataConTentTool.refdata, ""].isEmpty()) {
                refInformation()
                delay(10110)
            }
        }
    }

    private fun refInformation() {
        runCatching {
            val referrerClient = InstallReferrerClient.newBuilder(BikerStart.gameApp).build()
            referrerClient.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    runCatching {
                        handleReferrerSetup(responseCode, referrerClient)
                    }
                }

                override fun onInstallReferrerServiceDisconnected() {}
            })
        }.onFailure { e ->
            BikerStart.showLog("Failed to fetch referrer: ${e.message}")
        }
    }

    private fun handleReferrerSetup(responseCode: Int, referrerClient: InstallReferrerClient) {
        when (responseCode) {
            InstallReferrerClient.InstallReferrerResponse.OK -> {
                val installReferrer = referrerClient.installReferrer.installReferrer
                if (installReferrer.isNotEmpty()) {
                    SPUtils[DataConTentTool.refdata] = installReferrer
                    BikerUpData.postInstallDataWithCoroutine(BikerStart.gameApp)
                    AdminRequestManager.startOneTimeAdminData()
                }
                BikerStart.showLog("Referrer data: $installReferrer")
            }
            else -> BikerStart.showLog("Failed to setup referrer: $responseCode")
        }

        kotlin.runCatching { referrerClient.endConnection() }
    }
}
