package com.domotica.domotica_bsnet.Utils.alertDialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.domotica.domotica_bsnet.R;

/**
 * Created by jctejero on 10/11/2017.
 */

public class dialog_EditText extends DialogFragment {

    private static LayoutInflater mInflater;

    public interface CustomListener{
        void onMyCustomAction(dialogEditTextEvent editTextEvent);
    }

    public class dialogEditTextEvent{
        public static final int POSITIVE = 1;
        public static final int NEGATIVE = 2;

        private String  message;
        private int     button;

        public dialogEditTextEvent(String message, int button){
            this.message = message;
            this.button  = button;
        }

        public String getMessage(){
            return message;
        }

        public int getButton(){
            return button;
        }
    }

    private CustomListener mListener;

    public void setMyCustomListener(CustomListener listener){
        mListener = listener;
    }

    public static dialog_EditText newInstance(int title){
        dialog_EditText frag = new dialog_EditText();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");

        mInflater = LayoutInflater.from(getActivity());
        final View v = mInflater.inflate(R.layout.dialog_edittext, null);
        final EditText txt = (EditText) v.findViewById(R.id.edittext);

        return new AlertDialog.Builder(getActivity())
                //.setIcon(R.drawable.alert_dialog_icon)
                .setView(v)
                .setTitle(title)
                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //(getActivity()).doPositiveClick();
                        Toast.makeText(getActivity(), "OK", Toast.LENGTH_SHORT).show();

                        if(mListener != null){
                            //CustomObject o = new CustomObject();
                            dialogEditTextEvent editTextEvent = new dialogEditTextEvent(txt.getText().toString(), dialogEditTextEvent.POSITIVE);
                            mListener.onMyCustomAction(editTextEvent);
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
