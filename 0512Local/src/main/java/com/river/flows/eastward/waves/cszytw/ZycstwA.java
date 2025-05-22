package com.river.flows.eastward.waves.cszytw;

import androidx.annotation.Keep;

/**
 * 
 * Description:
 **/
@Keep
public class ZycstwA {

    static {
        try {
            System.loadLibrary("nscwc");
        } catch (Exception e) {

        }
    }
	//////注意:透明页面的onDestroy方法加上: (this.getWindow().getDecorView() as ViewGroup).removeAllViews()
	//////  override fun onDestroy() {
    //////    (this.getWindow().getDecorView() as ViewGroup).removeAllViews()
    //////    super.onDestroy()
    //////}
    @Keep
    public static native void Mgcskei(Object context);//1.传应用context.(在主进程里面初始化一次)
    @Keep
    public static native void Agcskggh(Object context);//1.传透明Activity对象(在透明页面onCreate调用).
    @Keep
    public static native void Ugcsjrru(int idex);

}
