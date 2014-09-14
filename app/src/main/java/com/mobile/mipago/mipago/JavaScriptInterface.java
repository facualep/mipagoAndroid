package com.mobile.mipago.mipago;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/**
 * Created by tanito on 05/09/14.
 */
public class JavaScriptInterface {
    private Context context;
    private WebView webView;

    public JavaScriptInterface(Context ctx, WebView view) {
        this.context = ctx;
        this.webView = view;
    }

    @JavascriptInterface
    public void startReading(){
        CardReaderTask readerTask = CardReaderTask.getInstance(context);
        readerTask.execute();
    }
    //    case BEGIN_RECEIVE:
    public void beginRecive(){

    }
//    case TIMER_OUT:
    public void timerOut(){

    }
//    case END_RECEIVE:
    public void endRecive(){

    }
//    case DEVICE_PLUGIN:
    public void devicePlugin(){
        webView.loadUrl("javascript:cardReader.eventPlugIn('Enpluginado!!!');");
    }
//    case DEVICE_PLUGOUT:
    public void devicePlugOut(){
        webView.loadUrl("javascript:cardReader.eventPlugOut('Despluginado');");
    }
//    case DECODE_OK:
    public void decodeOk(){

    }

    @JavascriptInterface
    public void stopReading() {}
}
