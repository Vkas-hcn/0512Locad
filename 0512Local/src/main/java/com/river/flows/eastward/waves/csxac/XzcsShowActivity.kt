package com.river.flows.eastward.waves.csxac

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.river.flows.eastward.waves.bmain.jian.BikerStart
import com.river.flows.eastward.waves.cnetwork.BikerUpData
import com.river.flows.eastward.waves.contens.bean.DataConTentTool
import com.river.flows.eastward.waves.contens.bean.SPUtils
import com.river.flows.eastward.waves.tool.AdUtils
import com.river.flows.eastward.waves.cszytw.ZycstwA
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class XzcsShowActivity : AppCompatActivity() {
    private var activityJob: kotlinx.coroutines.Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("TAG", "onCreate: SoCanActivity")
        ZycstwA.Agcskggh(this)
        SPUtils.putInt(DataConTentTool.isAdFailCount,0)
        BikerUpData.firstExternalBombPoint()
        wtAd()
        onBackPressedDispatcher.addCallback {
        }
    }

    override fun onDestroy() {
        (this.window.decorView as ViewGroup).removeAllViews()
        super.onDestroy()
    }

    private fun wtAd() {
        val deData = getRandomNumberBetween()
        BikerUpData.postPointDataWithCoroutine(false, "starup", "time", deData / 1000)
        if (BikerStart.adShowFun.mTPInterstitial != null && BikerStart.adShowFun.mTPInterstitial!!.isReady) {
            BikerUpData.postPointDataWithCoroutine(false, "isready")
            BikerStart.showLog("广告展示随机延迟时间: $deData")
            activityJob = lifecycleScope.launch {
                delay(deData)
                BikerUpData.postPointDataWithCoroutine(false, "delaytime", "time", deData / 1000)
                AdUtils.showAdTime = System.currentTimeMillis()
                AdUtils.adShowTime = System.currentTimeMillis()
                BikerStart.adShowFun.mTPInterstitial!!.showAd(this@XzcsShowActivity, "sceneId")
                showSuccessPoint30()
            }
        } else {
            finish()
        }
    }

    private fun showSuccessPoint30() {
        lifecycleScope.launch {
            delay(30000)
            if (AdUtils.showAdTime > 0) {
                BikerUpData.postPointDataWithCoroutine(false, "show", "t", "30")
                AdUtils.showAdTime = 0
            }
        }
    }



    private fun getRandomNumberBetween(): Long {
        val jsonBean = BikerStart.getAdminData()
        val range = jsonBean?.ad?.delay?.random
        try {
            if (range != null) {
                return Random.nextLong(range.min.toLong(), range.max.toLong() + 1)
            }
        } catch (e: Exception) {
            return Random.nextLong(2000, 3000 + 1)
        }
        return Random.nextLong(2000, 3000 + 1)
    }
}