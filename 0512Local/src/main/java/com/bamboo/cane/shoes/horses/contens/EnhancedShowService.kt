package com.bamboo.cane.shoes.horses.contens

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Process
import androidx.work.WorkManager
import com.bamboo.cane.shoes.horses.bmain.jian.GameStart

object EnhancedShowService {
    fun getInstallTimeInSeconds(): Long {
        return try {
            val packageManager: PackageManager = GameStart.gameApp.packageManager
            val packageInfo: PackageInfo = packageManager.getPackageInfo(GameStart.gameApp.packageName, 0)
            (System.currentTimeMillis() - packageInfo.firstInstallTime) / 1000
        } catch (e: PackageManager.NameNotFoundException) {
            GameStart.showLog("Package not found: ${e.message}")
            0L
        }
    }

    fun isMainProcess(context: Context): Boolean {
        return getCurrentProcessName(context) == context.packageName
    }

    private fun getCurrentProcessName(context: Context): String? {
        val pid = Process.myPid()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        return activityManager.runningAppProcesses.firstOrNull { it.pid == pid }?.processName
    }

}