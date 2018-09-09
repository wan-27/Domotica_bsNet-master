package com.domotica.domotica_bsnet.Utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.domotica.domotica_bsnet.R;

public class resourcelistAdapter extends ArrayAdapter {
    public resourcelistAdapter(Context ctxt, String[] resourceOptions){
        super(ctxt, android.R.layout.simple_list_item_1, resourceOptions);

        // Create a List from String Array elements
        //final List<String> options_list = new ArrayList<String>(Arrays.asList(resourceOptions));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        // Get the current item from ListView
        View view = super.getView(position,convertView,parent);

        if(!view.isSelected()) {
            if (position % 2 == 1) {
                // Set a background color for ListView regular row/item
                //view.setBackgroundColor(Color.parseColor("#FFECF9F8"));
                view.setBackgroundResource(R.drawable.option_list_backgroundcolor_alt);
            } else {
                // Set the background color for alternate row/item
                //view.setBackgroundColor(Color.parseColor("#FFE2F9F8"));
                view.setBackgroundResource(R.drawable.option_list_backgroundcolor);
            }
        }

        //TextView item = (TextView) super.getView(position,convertView,parent);

        // Set the list view item's text color
        //((TextView) view).setTextColor(Color.parseColor("#FF3E80F1"));

        // Set the item text style to bold
        ((TextView) view).setTypeface(((TextView) view).getTypeface(), Typeface.BOLD);

        // Change the item text size
        ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);

        return view;
    }
}
