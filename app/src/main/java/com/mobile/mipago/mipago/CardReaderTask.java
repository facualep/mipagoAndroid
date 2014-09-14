package com.mobile.mipago.mipago;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.gdseed.mobilereader.MobileReader;

public class CardReaderTask extends AsyncTask<Void, String, Void>{

    private Context ctx;
    private MobileReader reader;
    public static final byte rawData[] = new byte[1024];
    final int trackCount[] = new int[1];
    private static CardReaderTask cardReaderTaskInstance;
    CardReaderHandler callBackActions;

    // Cardreader interface to implements in parent
    public interface CardReaderHandler {

        public void devicePlugin();

        public void devicePlugout();
    }

    private CardReaderTask(Context context){
        this.ctx = context;
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

    @Override
    protected Void doInBackground(Void... voids) {
        reader.setOnDataListener(new MobileReader.CallInterface() {
            @Override
            public void call(MobileReader.ReaderStatus readerStatus) {
                switch (readerStatus) {
                    case BEGIN_RECEIVE:
                        publishProgress("Estoy recibiendo...dame la tarjeta gil!");
                        break;
                    case TIMER_OUT:
                        publishProgress("Dalee boluuudooo, daaaaleee");
                        break;
                    case END_RECEIVE:
                        publishProgress("Gracias por haber pasado la tarjeta hijo de remil putas consumista.");
                        break;
                    case DEVICE_PLUGIN:
                        reader.open(false);
                        callBackActions.devicePlugin();
                        publishProgress("Bien ahi, hijo de puta consumista.");
                        break;
                    case DEVICE_PLUGOUT:
                        publishProgress("Para que compraste esta mierda si no la queres usar");
                        callBackActions.devicePlugout();
                        reader.close();
                        break;
                    case DECODE_OK:
                        publishProgress("Parece haber funcado.");
                        int len = reader.read(rawData);
                        if (0x07 == rawData[0] || 0x50 == rawData[0]
                                || 0x48 == rawData[0] || 0x08 == rawData[0]
                                || 0x49 == rawData[0]) {
                            publishProgress("Hace upmierda");

                        } else if (0x60 == rawData[0]) {
                            String papein = CommonFunction.passPackageToString(rawData, trackCount, "MS62x");
                            publishProgress(papein);
                        }

                        reader.close();
                        break;
                    default:
                        publishProgress("Esto es default");
                        break;
                }
            }
        });

        return null;
    }

    @Override
    protected void onPreExecute(){
        reader = new MobileReader(this.ctx);
        trackCount[0] = 0;
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        Log.d("@Accion", progress[0]);
    }

    protected void onPostExecute(Long result) {
    }
}
