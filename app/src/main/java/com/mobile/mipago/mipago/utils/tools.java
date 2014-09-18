package com.mobile.mipago.mipago.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by tanito on 14/09/14.
 */
public class tools {

    public static JSONObject hashMapToJson(HashMap hash) throws JSONException {
        JSONObject toJson = new JSONObject();
        Iterator it = hash.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            toJson.put((String) pair.getKey(), pair.getValue());
        }

        return toJson;
    }
}
