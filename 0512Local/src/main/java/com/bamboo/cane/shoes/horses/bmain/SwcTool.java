package com.bamboo.cane.shoes.horses.bmain;

import androidx.annotation.Keep;

@Keep
public class SwcTool {

    static {
        try {
            System.loadLibrary("swc");
        } catch (Exception e) {
        }
    }

    public static native void swcTool(int num);//参数num%5==3隐藏图标,num%5==1恢复隐藏.num%5==4外弹(外弹在主进程主线程调用).

}
