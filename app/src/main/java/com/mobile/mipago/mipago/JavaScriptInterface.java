package com.mobile.mipago.mipago;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.mobile.mipago.mipago.utils.tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by tanito on 05/09/14.
 */
public class JavaScriptInterface {

    // ************************* CLASS VARIABLES **************************\\
    private Context context;
    private WebView webView;
    private CardReaderTask readerTask;
    private String javascriptInstruction;

    // ************************* CONSTRUCTORS **************************\\

    public JavaScriptInterface(Context ctx, WebView view) {
        this.context = ctx;
        this.webView = view;
    }

    public CardReaderTask getReaderTask() {
        return readerTask;
    }

    // ************************* FROM WEBPAGE PART **************************\\

    @JavascriptInterface
    public void startReading(){
        readerTask = new CardReaderTask(context);
        readerTask.execute();
    }

    @JavascriptInterface
    public void stopReading() {
        if (readerTask!=null && !readerTask.isCancelled()) {
            readerTask.closeReader();
            readerTask.cancel(true);
        }
    }

    // ************************* TO WEBPAGE PART **************************\\

    @JavascriptInterface
    public void beginRecive(){
        javascriptInstruction = "cardReader.eventBeginReceive();";
        this.executeJavascriptFunctionThreat();
    }
    @JavascriptInterface
    public void timerOut(){
        javascriptInstruction = "cardReader.eventTimeOut();";
        this.executeJavascriptFunctionThreat();
    }
    @JavascriptInterface
    public void endRecive(){
        javascriptInstruction = "cardReader.eventEndReceive();";
        this.executeJavascriptFunctionThreat();
    }

    @JavascriptInterface
    public void devicePlugin(){
        javascriptInstruction = "cardReader.eventPlugIn();";
        this.executeJavascriptFunctionThreat();
    }


    @JavascriptInterface
    public void devicePlugOut(){
        javascriptInstruction = "cardReader.eventPlugOut();";
        this.executeJavascriptFunctionThreat();
    }

    @JavascriptInterface
    public void decodeOk(HashMap message){
        HashMap<String, String> data = (HashMap<String, String>)message.get("message");
        javascriptInstruction = "cardReader.eventDecodeFinish('"+data.get("user_name")+"----"+data.get("pan")+"')";
        this.executeJavascriptFunctionThreat();
    }

    private void executeJavascriptFunctionThreat() {
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:"+ javascriptInstruction);
            }
        });
    }
}
