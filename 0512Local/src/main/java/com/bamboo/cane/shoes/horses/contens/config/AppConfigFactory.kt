package com.bamboo.cane.shoes.horses.contens.config

import com.bamboo.cane.shoes.horses.bmain.jian.BikerStart

// 文件路径: src/main/java/com/your/package/di/AppConfigFactory.kt

object AppConfigFactory {
    const val FCM = "bickscwc"

    fun String.hasGo(): Boolean {
        return this.contains("go",true)
    }
    fun String.hasHo(): Boolean {
        return this.contains("ho",true)
    }
    fun getConfig(): AppConfig {
        val config = if (BikerStart.isRelease) ReleaseAppConfig else DebugAppConfig
        return config
    }
}
