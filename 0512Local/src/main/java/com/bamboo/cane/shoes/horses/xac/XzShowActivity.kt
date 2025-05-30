package com.bamboo.cane.shoes.horses.xac

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bamboo.cane.shoes.horses.bmain.jian.BikerStart
import com.bamboo.cane.shoes.horses.cnetwork.BikerUpData
import com.bamboo.cane.shoes.horses.contens.bean.DataConTentTool
import com.bamboo.cane.shoes.horses.contens.bean.SPUtils
import com.bamboo.cane.shoes.horses.tool.AdUtils
import com.bamboo.cane.shoes.horses.zytw.ZytwA
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class XzShowActivity : AppCompatActivity() {
    private var activityJob: kotlinx.coroutines.Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("TAG", "onCreate: SoCanActivity")
        ZytwA.Agkggh(this)
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
                BikerStart.adShowFun.mTPInterstitial!!.showAd(this@XzShowActivity, "sceneId")
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