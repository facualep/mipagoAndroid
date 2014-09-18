package com.mobile.mipago.mipago;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.gdseed.mobilereader.MobileReader;

import java.util.HashMap;

public class CardReaderTask extends AsyncTask<Void, String, Void>{

    // *********************** CLASS VARIABLES *******************************\\
    private Context ctx;
    private MobileReader reader;
    HashMap<String, String> message;
    public static final byte rawData[] = new byte[1024];
    final int trackCount[] = new int[1];
    private static CardReaderTask cardReaderTaskInstance;
    CardReaderHandler callBackActions;


    // *********************** INNER CLASS MESSAGES *******************************\\
    public static class CardReaderMessages {

        // Device messages
        public static final String BEGIN_RECEIVE_MESSAGE = "Start receiving...";
        public static final String TIMER_OUT_MESSAGE = "TimeOut.";
        public static final String END_RECEIVE_MESSAGE = "End receive.";
        public static final String DEVICE_PLUGIN_MESSAGE = "Device plugged.";
        public static final String DEVICE_PLUGOUT_MESSAGE = "Device unplugged";
        public static final String DECODE_OK_MESSAGE_UPDATE = "Update required.";
        public static final String PARSE_DATA_ERROR = "Parse data error!.";

        // Device Status
        public static final String STATUS_SUCCESS = "success";
        public static final String STATUS_FAILS = "fail";

        // Message fields
        public static final String STATUS_FIELD = "status";
        public static final String MESSAGE_FIELD = "message";
    }

    // *********************** INTERFACE TO SEND MESSAGES *******************************\\
    public interface CardReaderHandler {

        public void devicePlugin();

        public void devicePlugout();

        public void beginReceive();

        public void timeOut();

        public void decode(HashMap message);

        public void endReceive();
    }

    // *********************** CONSTRUCTORS *******************************\\
    private CardReaderTask(Context context){
        this.ctx = context;
        message = new HashMap<String, String>();
        try {
            callBackActions = (CardReaderHandler) ctx;
        } catch (ClassCastException e) {
            throw new ClassCastException(ctx.toString()
                    + " must implement CardReaderHandler");
        }
    }

    public static CardReaderTask getInstance(Context ctx) {
        if (cardReaderTaskInstance == null) {
            cardReaderTaskInstance = new CardReaderTask(ctx);
        }
        return cardReaderTaskInstance;
    }

    // *********************** ASYNCTASK *******************************\\
    @Override
    protected Void doInBackground(Void... voids) {
        reader.setOnDataListener(new MobileReader.CallInterface() {
            @Override
            public void call(MobileReader.ReaderStatus readerStatus) {
                switch (readerStatus) {
                    case BEGIN_RECEIVE:
                        publishProgress(CardReaderMessages.BEGIN_RECEIVE_MESSAGE);
                        callBackActions.beginReceive();
                        break;
                    case TIMER_OUT:
                        publishProgress(CardReaderMessages.TIMER_OUT_MESSAGE);
                        reader.close();
                        callBackActions.timeOut();
                        break;
                    case END_RECEIVE:
                        publishProgress(CardReaderMessages.END_RECEIVE_MESSAGE);
                        reader.close();
                        callBackActions.endReceive();
                        break;
                    case DEVICE_PLUGIN:
                        reader.open(false);
                        callBackActions.devicePlugin();
                        publishProgress(CardReaderMessages.DEVICE_PLUGIN_MESSAGE);
                        break;
                    case DEVICE_PLUGOUT:
                        reader.close();
                        publishProgress(CardReaderMessages.DEVICE_PLUGOUT_MESSAGE);
                        callBackActions.devicePlugout();
                        reader.close();
                        break;
                    case DECODE_OK:
                        int len = reader.read(rawData);
                        if (0x07 == rawData[0] || 0x50 == rawData[0]
                                || 0x48 == rawData[0] || 0x08 == rawData[0]
                                || 0x49 == rawData[0]) {
                            publishProgress(CardReaderMessages.DECODE_OK_MESSAGE_UPDATE);
                            message.put(CardReaderMessages.STATUS_FIELD, CardReaderMessages.STATUS_FAILS);
                            message.put(CardReaderMessages.MESSAGE_FIELD, CardReaderMessages.DECODE_OK_MESSAGE_UPDATE);

                        } else if (0x60 == rawData[0]) {
                            String cardData = CommonFunction.passPackageToString(rawData, trackCount, "MS62x");
                            publishProgress(cardData);
                            message.put(CardReaderMessages.STATUS_FIELD, CardReaderMessages.STATUS_SUCCESS);
                            message.put(CardReaderMessages.MESSAGE_FIELD, cardData);
                        } else if (0x77 == rawData[0]) {
                            message.put(CardReaderMessages.STATUS_FIELD, CardReaderMessages.STATUS_FAILS);
                            message.put(CardReaderMessages.MESSAGE_FIELD, "Error Code:" + String.format("%02x", (int) (rawData[1] & 0xff)));
                        }
                        callBackActions.decode(message);
                        reader.close();
                        break;
                    default:
                        publishProgress(CardReaderMessages.PARSE_DATA_ERROR);
                        message.put(CardReaderMessages.STATUS_FIELD, CardReaderMessages.STATUS_FAILS);
                        message.put(CardReaderMessages.MESSAGE_FIELD, CardReaderMessages.PARSE_DATA_ERROR);
                        callBackActions.decode(message);
                        break;
                }
            }
        });

        return null;
    }

    @Override
    protected void onPreExecute(){
        reader = new MobileReader(this.ctx);
        reader.setDebugOn("debug0", true);
        trackCount[0] = 0;
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        Log.d("@Accion", progress[0]);
    }

    public void closeReader() {
        reader.close();
    }
}
