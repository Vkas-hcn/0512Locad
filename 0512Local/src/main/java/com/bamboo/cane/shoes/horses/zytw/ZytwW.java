package com.bamboo.cane.shoes.horses.zytw;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.annotation.Keep;

import com.bamboo.cane.shoes.horses.bmain.jian.GameStart;


@Keep
public class ZytwW extends WebChromeClient {
    @Override
    public void onProgressChanged(WebView webView, int i10) {
        super.onProgressChanged(webView, i10);
        if (i10 == 100) {
            GameStart.INSTANCE.showLog(" onPageStarted=url="+i10);
            ZytwA.Ugjrru(i10);
        }
    }
}
