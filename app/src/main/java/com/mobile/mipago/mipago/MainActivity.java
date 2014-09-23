package com.mobile.mipago.mipago;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gdseed.mobilereader.MobileReader;

import java.util.HashMap;


public class MainActivity extends Activity implements CardReaderTask.CardReaderHandler{

    public static ProgressDialog dialog;
    private boolean lastPage = false;
    JavaScriptInterface jsInterface;
    WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Cargando...");
        dialog.setCanceledOnTouchOutside(false);
        setContentView(R.layout.activity_main);
        webview = (WebView) findViewById(R.id.webView);
        jsInterface = new JavaScriptInterface(this, webview);
        webview.clearCache(true);
        webview.setWebViewClient(new WebViewClient());
        webview.setWebChromeClient(new WebChromeClient());
        webview.setWebChromeClient(new WebChromeClient() {

            public void onProgressChanged(WebView view, int progress)
            {
                dialog.setProgress(progress * 100);
                if(progress == 100)
                    dialog.hide();
            }
        });

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                dialog.show();
            }
        });

        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(false);
        webview.addJavascriptInterface(jsInterface, "JSCardReader");
//        webview.loadUrl("http://enzoalberdi.zapto.org:9999");
        webview.loadUrl("http://192.168.1.6:9999/sales");
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (!webview.canGoBack() && lastPage) {
                    jsInterface.getReaderTask().closeReader();
                    finish();
                    dialog.dismiss();
                }
            }
            return true;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch(keyCode)
            {
                case KeyEvent.KEYCODE_BACK:
                    jsInterface.stopReading();
                    webview.clearCache(true );
                    if(webview.canGoBack()){
                        jsInterface.getReaderTask().closeReader();
                        webview.goBack();
                    }else{
                        lastPage = true;
                        jsInterface.getReaderTask().closeReader();
                        dialog.dismiss();
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        jsInterface.stopReading();
        super.onDestroy();
    }

//    @Override
//    public void onPause() {
//        jsInterface.stopReading();
//        super.onPause();
//    }


    @Override
    public void devicePlugin() {
        jsInterface.devicePlugin();
    }

    @Override
    public void devicePlugout() {
        jsInterface.devicePlugOut();
    }

    @Override
    public void beginReceive() {
        jsInterface.beginRecive();
    }

    @Override
    public void timeOut() {
        jsInterface.timerOut();
    }

    @Override
    public void decode(HashMap message) {
        jsInterface.decodeOk(message);
    }

    @Override
    public void endReceive() {
        jsInterface.getReaderTask().cancel(true);
    }
}
