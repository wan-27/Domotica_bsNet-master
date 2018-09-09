package com.domotica.domotica_bsnet.Utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import soulissclient.Constants;

public class Utils {

    public static JSONArray SortJSONARRAY(JSONArray jsonArray, final String sortField ) {
        JSONArray sortedJsonArray = new JSONArray();

        try {
            List<JSONObject> jsonValues = new ArrayList<JSONObject>();
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonValues.add(jsonArray.getJSONObject(i));
            }

            Collections.sort(jsonValues, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject a, JSONObject b) {
                    //String valA = new String();
                    //String valB = new String();

                    Integer A = 0;
                    Integer B = 0;

                    try {
                        A = (Integer) a.get(sortField);
                        B = (Integer) b.get(sortField);
                    } catch (JSONException e) {
                        //do something
                    }
                    return -A.compareTo(B);
                    //if you want to change the sort order, simply use the following:
                    //return -valA.compareTo(valB);
                }
            });
            for (int i = 0; i < jsonArray.length(); i++) {
                sortedJsonArray.put(jsonValues.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return sortedJsonArray;
    }

    public static String json_Encode(String msg){
        String msg_out = msg.replace("Ã±", "ñ");

        msg_out = msg_out.replace("Ã¡", "á");
        msg_out = msg_out.replace("Ã©", "é");
        msg_out = msg_out.replace("Ã\u00AD", "í");
        msg_out = msg_out.replace("Ã³", "ó");
        msg_out = msg_out.replace("Ãº", "ú");
        msg_out = msg_out.replace("Ã\u0091", "Ñ");
        msg_out = msg_out.replace("Ã\u0081", "Á");
        msg_out = msg_out.replace("Ã\u0089", "É");
        msg_out = msg_out.replace("Ã\u008D", "Í");
        msg_out = msg_out.replace("Ã\u0093", "Ó");
        msg_out = msg_out.replace("Ã\u009A", "Ú");



        /*
        char[] caract = new char[2];
        //Ú 195 154
        caract[0] = 195;
        caract[1] = 154;
        //__Ãº_Ã±_Ã_Ã_Ã_Ã_Ã_Ã
        msg_out = msg_out.replace(new String(caract), "Ú");
        */
        return msg_out;
    }

    public static String toAscii(String s){
        StringBuilder sb = new StringBuilder();
        char[] chars = s.toCharArray();
        for(char c : chars)
            sb.append((int) c);
        String output = sb.toString();

        Log.i(Constants.TAG, output);

        return output;
    }
}
