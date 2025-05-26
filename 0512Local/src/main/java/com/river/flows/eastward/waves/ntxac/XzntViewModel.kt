package com.river.flows.eastward.waves.ntxac

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.river.flows.eastward.waves.bmain.jian.BikerStart
import com.river.flows.eastward.waves.cnetwork.BikerUpData
import kotlin.random.Random

class XzntViewModel : ViewModel() {
    private val _adDelayTime = MutableLiveData<Long>()
    val adDelayTime: LiveData<Long> get() = _adDelayTime

    fun fetchAdDelayTime() {
        val jsonBean = BikerStart.getAdminData()
        val range = jsonBean?.ad?.delay?.random
        val delayTime = try {
            if (range != null) {
                Random.nextLong(range.min.toLong(), range.max.toLong() + 1)
            } else {
                Random.nextLong(2000, 3000 + 1)
            }
        } catch (e: Exception) {
            Random.nextLong(2000, 3000 + 1)
        }
        _adDelayTime.value = delayTime
    }

    fun postStartupPoint(deData: Long) {
        BikerUpData.postPointDataWithCoroutine(false, "starup", "time", deData / 1000)
    }

    fun postIsReadyPoint() {
        BikerUpData.postPointDataWithCoroutine(false, "isready")
    }

    fun postDelayTime(deData: Long) {
        BikerUpData.postPointDataWithCoroutine(false, "delaytime", "time", deData / 1000)
    }

    fun postShowPoint() {
        BikerUpData.postPointDataWithCoroutine(false, "show", "t", "30")
    }
}
