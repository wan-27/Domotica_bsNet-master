package com.domotica.domotica_bsnet.Utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.domotica.domotica_bsnet.R;

/**
 * Created by jctejero on 22/12/2017.
 */

public class Openweb {
    public static void OpenWeb(Context ctxt, String ipaddress){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //To open Activity outside Activity Context
        intent.setData(Uri.parse("http://" + ipaddress));

        try {
            ctxt.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            makeToast(ctxt, R.string.msg_NO_webDevice);
        }
    }

    public static void makeToast(Context ctxt, int msg) {
        Toast.makeText(ctxt, msg, Toast.LENGTH_SHORT).show();
    }
}
