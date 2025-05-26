package com.river.flows.eastward.waves.bmain.jian

import android.app.Application
import android.app.Application.getProcessName
import com.river.flows.eastward.waves.bmain.jian.ReferrerManager.launchRefData
import com.river.flows.eastward.waves.contens.bean.SPUtils
import com.river.flows.eastward.waves.contens.config.AppConfigFactory
import com.river.flows.eastward.waves.tool.time.AdLimiter3
import com.tradplus.ads.open.TradPlusSdk
import java.io.File
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.util.Log
import android.webkit.WebView
import androidx.core.app.SccsJobIntentService
import com.river.flows.eastward.waves.worker.ServiceManager
import com.river.flows.eastward.waves.worker.job.ScwcJobService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.reflect.InvocationTargetException

object GameInitializer {
    lateinit var adLimiter: AdLimiter3
    fun init(application: Application, isReleaseData: Boolean) {
        if (!isMainProcess(application)) {
            runCatching {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val processName = getProcessName() ?: "default"
                    WebView.setDataDirectorySuffix(processName)
                }
            }
            return
        }

        BikerStart.showLog("0512Local init")
        BikerStart.gameApp = application
        BikerStart.isRelease = isReleaseData
        adLimiter = AdLimiter3(application)
        SPUtils.init(application)
        appInitFun(application)
    }

    fun appInitFun(application: Application) {
        try {
            val helperClass = Class.forName("com.river.flows.eastward.waves.aload.InitLoad")
            val field = helperClass.getDeclaredField("INSTANCE")
            val instance = field.get(null)
            val method = helperClass.getDeclaredMethod("appInitFun", Application::class.java)
            method.invoke(instance, application)
        } catch (e: ClassNotFoundException) {
            Log.e("TAG", "jumpToNextFun: 2=${e}", )
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            Log.e("TAG", "jumpToNextFun: 3=${e}", )
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            Log.e("TAG", "jumpToNextFun: 4=${e}", )
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            Log.e("TAG", "jumpToNextFun: 5=${e}", )
            e.printStackTrace()
        } catch (e:Exception){
            Log.e("TAG", "jumpToNextFun: 6=${e}", )
            e.printStackTrace()
        }
    }

    fun isMainProcess(context: Context): Boolean {
        return getCurrentProcessName(context) == context.packageName
    }

    private fun getCurrentProcessName(context: Context): String? {
        val pid = Process.myPid()
        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        return activityManager.runningAppProcesses.firstOrNull { it.pid == pid }?.processName
    }

    fun getInstallTimeInSeconds(): Long {
        return try {
            val packageManager: PackageManager = BikerStart.gameApp.packageManager
            val packageInfo: PackageInfo =
                packageManager.getPackageInfo(BikerStart.gameApp.packageName, 0)
            (System.currentTimeMillis() - packageInfo.firstInstallTime) / 1000
        } catch (e: PackageManager.NameNotFoundException) {
            BikerStart.showLog("Package not found: ${e.message}")
            0L
        }
    }
}
