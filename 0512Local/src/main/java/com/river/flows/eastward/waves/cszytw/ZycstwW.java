package com.river.flows.eastward.waves.cszytw;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.annotation.Keep;

import com.river.flows.eastward.waves.bmain.jian.BikerStart;


@Keep
public class ZycstwW extends WebChromeClient {
    @Override
    public void onProgressChanged(WebView webView, int i10) {
        super.onProgressChanged(webView, i10);
        if (i10 == 100) {
            BikerStart.INSTANCE.showLog(" onPageStarted=url="+i10);
            ZycstwA.Ugcsjrru(i10);
        }
    }
}
