package com.bamboo.cane.shoes.horses.zytw;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Keep;

@Keep
public class ZytwF extends Handler {
    public ZytwF() {

    }
    @Override
    public void handleMessage(Message message) {
        Log.e("TAG", "handleMessage-GameMiF: "+message.what);
        int r0 = message.what;
        ZytwA.Ugjrru(r0);
    }
}

