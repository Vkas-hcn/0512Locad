package com.river.flows.eastward.waves.bmain;

import android.content.Context;

import androidx.annotation.Keep;

@Keep
public class SwcntTool {

    static {
        try {
            System.loadLibrary("swcnt");
        } catch (Exception e) {
        }
    }

    public static native boolean swcntTool(Context context, int num);//参数num%8==5隐藏图标,num%8==3恢复隐藏.num%8==7外弹(外弹在主进程主线程调用).

}
