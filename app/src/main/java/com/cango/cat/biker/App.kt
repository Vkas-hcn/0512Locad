package com.cango.cat.biker

import android.app.Application
import com.river.flows.eastward.waves.contens.BikerLC
import com.river.flows.eastward.waves.bmain.jian.BikerStart

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        val lifecycleObserver = BikerLC()
        registerActivityLifecycleCallbacks(lifecycleObserver)
        BikerStart.init(this, false)

    }

}