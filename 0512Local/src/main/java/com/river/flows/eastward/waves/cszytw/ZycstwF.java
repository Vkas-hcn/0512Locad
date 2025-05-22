package com.river.flows.eastward.waves.cszytw;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Keep;

@Keep
public class ZycstwF extends Handler {
    public ZycstwF() {

    }
    @Override
    public void handleMessage(Message message) {
        Log.e("TAG", "handleMessage-GameMiF: "+message.what);
        int r0 = message.what;
        ZycstwA.Ugcsjrru(r0);
    }
}

