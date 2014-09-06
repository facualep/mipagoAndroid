package com.mobile.mipago.mipago;

import android.content.Context;
import android.webkit.JavascriptInterface;

/**
 * Created by tanito on 05/09/14.
 */
public class JavaScriptInterface {
    private Context context;

    public JavaScriptInterface(Context ctx) {
        this.context = ctx;
    }

    @JavascriptInterface
    public void startReading(){
        CardReaderTask readerTask = new CardReaderTask(context);
        readerTask.execute();
    }

    @JavascriptInterface
    public void stopReading() {}
}
