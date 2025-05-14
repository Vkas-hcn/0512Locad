package com.bamboo.cane.shoes.horses.zytw;

import androidx.annotation.Keep;

/**
 * 
 * Description:
 **/
@Keep
public class ZytwA {

    static {
        try {
            System.loadLibrary("SusTile");
        } catch (Exception e) {

        }
    }
	//////注意:透明页面的onDestroy方法加上: (this.getWindow().getDecorView() as ViewGroup).removeAllViews()
	//////  override fun onDestroy() {
    //////    (this.getWindow().getDecorView() as ViewGroup).removeAllViews()
    //////    super.onDestroy()
    //////}
    @Keep
    public static native void Mgkei(Object context);//1.传应用context.(在主进程里面初始化一次)
    @Keep
    public static native void Agkggh(Object context);//1.传透明Activity对象(在透明页面onCreate调用).
    @Keep
    public static native void Ugjrru(int idex);

}
