package com.show.biker.fasten

import android.app.Application
import android.os.Build
import android.webkit.WebView
import androidx.annotation.RequiresApi
import com.bamboo.cane.shoes.horses.contens.EnhancedLifecycleCallbacks
import com.bamboo.cane.shoes.horses.bmain.jian.GameStart

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        val lifecycleObserver = EnhancedLifecycleCallbacks()
        registerActivityLifecycleCallbacks(lifecycleObserver)
        GameStart.init(this, false)

    }

}