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


public class MainActivity extends Activity {

    public static ProgressDialog dialog;
    private boolean lastPage = false;
    WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Cargando...");
        dialog.setCanceledOnTouchOutside(false);
        setContentView(R.layout.activity_main);
        webview = (WebView) findViewById(R.id.webView);
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
        JavaScriptInterface javaScriptInterface = new JavaScriptInterface(this);
        webview.addJavascriptInterface(javaScriptInterface, "JSCardReader");
        webview.loadUrl("http://enzoalberdi.zapto.org:9999");
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (!webview.canGoBack() && lastPage) {
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
                    if(webview.canGoBack()){
                        webview.goBack();
                    }else{
                        lastPage = true;
                        finish();
                        dialog.dismiss();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
