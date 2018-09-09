package com.domotica.domotica_bsnet.Utils.alertDialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.domotica.domotica_bsnet.R;

/**
 * Created by jctejero on 22/11/2017.
 */

public class OkCancelDialog extends DialogFragment {

    public interface OKListener{
        public void onOKClick();
    }

    private OKListener mOKListener;

    public void setMyCustomListener(OKListener listener){
        mOKListener = listener;
    }

    public static OkCancelDialog newInstance(String title){
        OkCancelDialog frag = new OkCancelDialog();
        Bundle args = new Bundle();
        //args.putInt("title", title);
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");

        return new AlertDialog.Builder(getActivity())
                //.setIcon(R.drawable.alert_dialog_icon)
                .setTitle(title)
                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //(getActivity()).doPositiveClick();

                        Toast.makeText(getActivity(), "OK", Toast.LENGTH_SHORT).show();

                        if(mOKListener != null){
                            mOKListener.onOKClick();
                        }

                    }
                })

                .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(getActivity(), "Cancel", Toast.LENGTH_SHORT).show();
                    }
                })
                .create();
    }

}
