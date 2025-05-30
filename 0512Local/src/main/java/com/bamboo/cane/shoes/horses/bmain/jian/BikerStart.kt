package com.bamboo.cane.shoes.horses.bmain.jian

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.annotation.Keep
import androidx.work.Configuration
import androidx.work.WorkManager
import com.bamboo.cane.shoes.horses.contens.bean.DataConTentTool
import com.bamboo.cane.shoes.horses.contens.bean.SPUtils
import com.bamboo.cane.shoes.horses.contens.bean.UserAdminBean
import com.bamboo.cane.shoes.horses.tool.AdShowFun
import com.google.gson.Gson

@Keep
object BikerStart {
    lateinit var gameApp: Application
    var isRelease: Boolean = true
    val adShowFun = AdShowFun()
    var KEY_IS_SERVICE = false
    var activityList = ArrayList<Activity>()

    fun init(application: Application, isReleaseData: Boolean) {
        WorkManager.initialize(application, Configuration.Builder().build())
        GameInitializer.init(application, isReleaseData)
    }

    fun canIntNextFun() {
        adShowFun.startRomFun()
    }
    fun showLog(msg: String) {
        if (isRelease) {
            return
        }
        Log.e("SCWC", msg)
    }

    fun getAdminData(): UserAdminBean? {
//        SPUtils[DataConTentTool.admindata] = AppTestData.new_json_data_shuoming
        val adminData = SPUtils[DataConTentTool.admindata,""]
        val adminBean = runCatching {
            Gson().fromJson(adminData, UserAdminBean::class.java)
        }.getOrNull()
        return if (adminBean != null && isValidAdminBean(adminBean)) {
            adminBean
        } else {
            null
        }
    }

    private fun isValidAdminBean(bean: UserAdminBean): Boolean {
        return bean.user != null && bean.user.profile.type.isNotEmpty()
    }

    fun putAdminData(adminBean: String) {
        SPUtils[DataConTentTool.admindata] = adminBean
//        SPUtils[DataConTentTool.admindata] = AppTestData.new_json_data_shuoming
    }
}
