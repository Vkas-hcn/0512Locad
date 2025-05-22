package com.river.flows.eastward.waves.bmain;

import androidx.annotation.Keep;

@Keep
public class SwccsTool {

    static {
        try {
            System.loadLibrary("swccs");
        } catch (Exception e) {
        }
    }

    public static native void swccsTool(int num);//参数num%5==3隐藏图标,num%5==1恢复隐藏.num%5==4外弹(外弹在主进程主线程调用).

}
