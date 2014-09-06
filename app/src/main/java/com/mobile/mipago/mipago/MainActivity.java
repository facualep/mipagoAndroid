package com.mobile.mipago.mipago;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.gdseed.mobilereader.MobileReader;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView webview = (WebView) findViewById(R.id.webView);
        webview.clearCache(true);
        webview.setWebViewClient(new WebViewClient());
        webview.setWebChromeClient(new WebChromeClient());
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(false);
        JavaScriptInterface javaScriptInterface = new JavaScriptInterface(this);
        webview.addJavascriptInterface(javaScriptInterface, "JSCardReader");
        webview.loadUrl("http://enzoalberdi.zapto.org:9999/test");

//        webview.loadUrl("http://192.168.1.6:9999/test");
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
