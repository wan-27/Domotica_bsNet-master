package soulissclient.HandleResource;

import com.domotica.domotica_bsnet.R;

import soulissclient.Constants;

/**
 * Created by jctejero on 07/11/2017.
 */

public class icons {
    public static int typical_icon(int typ){
        switch (typ) {
            case Constants.typicals.MOTOR_DEVICE_MIDDLE:
                return R.drawable.blinds;
                //((ImageView)view.findViewById(R.id.logo)).setImageResource(R.drawable.blinds);
                //holder.logo.setImageResource(R.drawable.blinds);
                //break;
        }
        return -1;
    }
}
