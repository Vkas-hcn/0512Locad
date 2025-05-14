package com.show.biker.fasten

import android.app.Application
import com.bamboo.cane.shoes.horses.contens.BikerLC
import com.bamboo.cane.shoes.horses.bmain.jian.BikerStart

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        val lifecycleObserver = BikerLC()
        registerActivityLifecycleCallbacks(lifecycleObserver)
        BikerStart.init(this, false)

    }

}