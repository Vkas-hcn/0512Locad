package com.river.flows.eastward.waves.ntxac

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.river.flows.eastward.waves.bmain.jian.BikerStart
import com.river.flows.eastward.waves.cnetwork.BikerUpData
import com.river.flows.eastward.waves.contens.bean.DataConTentTool
import com.river.flows.eastward.waves.contens.bean.SPUtils
import com.river.flows.eastward.waves.tool.AdUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class XzntShowActivity : AppCompatActivity() {

    private lateinit var viewModel: XzntViewModel
    private var activityJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("TAG", "onCreate: SoCanActivity")
        SPUtils.putInt(DataConTentTool.isAdFailCount, 0)
        BikerUpData.firstExternalBombPoint()

        viewModel = ViewModelProvider(this)[XzntViewModel::class.java]

        viewModel.adDelayTime.observe(this) { delayTime ->
            wtAd(delayTime)
        }

        viewModel.fetchAdDelayTime()
        onBackPressedDispatcher.addCallback {
        }
    }

    override fun onDestroy() {
        (this.window.decorView as ViewGroup).removeAllViews()
        super.onDestroy()
    }

    private fun wtAd(deData: Long) {
        viewModel.postStartupPoint(deData)
        if (BikerStart.adShowFun.mTPInterstitial != null && BikerStart.adShowFun.mTPInterstitial!!.isReady) {
            viewModel.postIsReadyPoint()
            BikerStart.showLog("广告展示随机延迟时间: $deData")
            activityJob = lifecycleScope.launch {
                delay(deData)
                viewModel.postDelayTime(deData)
                AdUtils.showAdTime = System.currentTimeMillis()
                AdUtils.adShowTime = System.currentTimeMillis()
                BikerStart.adShowFun.mTPInterstitial!!.showAd(this@XzntShowActivity, "sceneId")
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
                viewModel.postShowPoint()
                AdUtils.showAdTime = 0
            }
        }
    }
}
