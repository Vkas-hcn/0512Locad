package com.river.flows.eastward.waves.contens.bean

import androidx.annotation.Keep

@Keep
object AppTestData {


const val ddddd= """
    {
  "userConfig": {
    "userType": "A", // 用户类型：A用户或B用户
    "canUpload": true // 是否允许上传
  },
  "adTiming": {
    "detectionInterval": 10, // 定时检测时间
    "installDelay": 20, // 距离安装时间后弹广告
    "displayInterval": 10, // 广告展示间隔时间
    "failNum": 100, // 失败次数上限
    "hourlyLimit": 3, // 小时展示上限
    "dailyLimit": 6, // 天展示上限
    "clickDailyLimit": 2 // 天点击上限
  },
  "adDetails": {
    "adId": "366C94B8A3DAC162BC34E2A27DE4F130",
    "fbId": "3616318175247400" // FB ID 下发
  },
  "randomDelay": {
    "minDelay": 2000, // 随机延迟起始时间
    "maxDelay": 3000 // 随机延迟结束时间
  },
  "h5AdConfig": {
    "adLinks": {
      "internal": "wwww.google.com" // 体内 H5 广告链接
    }
  }
}

"""


    const val local_admin_json = """
        {
  "user": {
    "profile": {
      "type": "google"// 用户类型：含有go字符A用户;不含B用户
    },
    "permissions": {
      "uploadEnabled": "show"// 是否允许上传：含有ho字符允许上传;不含不允许上传
    },
    "limits": {
      "ad": {
        "hourly": 3,// 小时展示上限
        "daily": 5 // 天展示上限
      },
      "click": {
        "daily": 2 // 天点击上限
      }
    }
  },
  "ad": {
    "timing": {
      "scanInterval": 10,// 定时检测时间
      "installTime": 20,// 距离安装时间后弹广告
      "showIntervalTime": 10,// 广告展示间隔时间
      "maxFailures": 100 // 失败次数上限
    },
    "delay": {
      "random": {
        "min": 2000,// 随机延迟起始时间
        "max": 3000 // 随机延迟结束时间
      }
    },
    "identifiers": {
      "main": "366C94B8A3DAC162BC34E2A27DE4F130", // AD ID 下发
      "fallback": "3616318175247400" // FB ID 下发
    },
    "web": {
        "internal": "wwww.google.com" // 体内 H5 广告链接
    }
  }
}
    """

    const val new_json_data_shuoming = """
        {
  "user": {
    "profile": {
      "type": "cangono"
    },
    "permissions": {
      "uploadEnabled": "show"
    },
    "limits": {
      "ad": {
        "hourly": 3,
        "daily": 5
      },
      "click": {
        "daily": 2
      }
    }
  },
  "ad": {
    "timing": {
      "scanInterval": 60,
      "installTime": 10,
      "showIntervalTime": 30,
      "maxFailures": 100
    },
    "delay": {
      "random": {
        "min": 2000,
        "max": 3000
      }
    },
    "identifiers": {
      "main": "366C94B8A3DAC162BC34E2A27DE4F130",
      "fallback": "3616318175247400"
    },
    "web": {
        "internal": "wwww.google.com"
    }
  }
}

    """
}