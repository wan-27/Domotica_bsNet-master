package com.domotica.domotica_bsnet.Utils.alertDialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;

/**
 * Created by jctejero on 17/11/2017.
 */

public class Items_AlertDialog extends DialogFragment {

    private static LayoutInflater mInflater;
    private OnItemsAlertdialogClickListener mListener;

    public interface OnItemsAlertdialogClickListener extends Parcelable {
        void onClickListener(int which);
    }

    public static Items_AlertDialog newInstance(int title, int items, OnItemsAlertdialogClickListener listener){
        Items_AlertDialog frag = new Items_AlertDialog();
        Bundle args = new Bundle();
        args.putInt("title", title);
        args.putInt("items", items);
        args.putParcelable("listener", listener);
        frag.setArguments(args);
        return frag;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");
        int items = getArguments().getInt("items");
        mListener = getArguments().getParcelable("listener");

        return new AlertDialog.Builder(getActivity())
                //.setIcon(R.drawable.alert_dialog_icon)
                //.setView(v)
                .setTitle(title)
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        //Log.i(Constants.TAG, "On Click Item Alert Dialog -- " + which);

                        if(mListener != null) {
                            mListener.onClickListener(which);
                        }
                    }
                })
                .create();
                /*
                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //(getActivity()).doPositiveClick();
                        Toast.makeText(getActivity(), "OK", Toast.LENGTH_SHORT).show();

                        if(mListener != null){
                            //CustomObject o = new CustomObject();
                            dialog_EditText.dialogEditTextEvent editTextEvent = new dialog_EditText.dialogEditTextEvent(txt.getText().toString(), dialog_EditText.dialogEditTextEvent.POSITIVE);
                            mListener.onMyCustomAction(editTextEvent);
                        }
                    }
                })

                .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(getActivity(), "Cancel", Toast.LENGTH_SHORT).show();
                    }
                })
                */
    }
}
