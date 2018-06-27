package com.domotica.domotica_bsnet;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.domotica.domotica_bsnet.DeviceConfigurations.ActivityGeneralConf;
import com.domotica.domotica_bsnet.DeviceConfigurations.ActivityNetConf;
import com.domotica.domotica_bsnet.DeviceConfigurations.ActivityNetInfo;
import com.domotica.domotica_bsnet.DeviceConfigurations.ActivityNtpConf;
import com.domotica.domotica_bsnet.DeviceConfigurations.ActivitySystemConf;
import com.domotica.domotica_bsnet.Utils.resourcelistAdapter;
import com.domotica.domotica_bsnet.fragments.DevicesFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActivityOptions extends AppCompatActivity {
    ListView listOpt;
    private String ipServer;
    private int     mSelectedItem = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        String name = DevicesFragment.getNameLongclick();
        TextView devName = (TextView) this.findViewById(R.id.Conf_DeviceName);
        devName.setText(name);

        // Create a List from String Array elements
        String[] Options = getResources().getStringArray(R.array.configuration_options);
        //final List<String> fruits_list = new ArrayList<String>(Arrays.asList(cmd));

        listOpt = (ListView) findViewById(R.id.listView_options);
        //ArrayAdapter<CharSequence> itemsAdapter = ArrayAdapter.createFromResource(this, R.array.configuration_options, android.R.layout.simple_list_item_1);

        final ArrayAdapter<String> itemsAdapter = new resourcelistAdapter(this, Options);

        listOpt.setAdapter(itemsAdapter);
        listOpt.setClickable(true);

        listOpt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //int pos=0;
                //view.setBackgroundColor(Color.parseColor("#FFff9900"));
                mSelectedItem = position;
                onClickListener(mSelectedItem);
            }
        });
    }



    public void onClickListener(int which) {
        //Bundle bundle = getIntent().getExtras();
        ipServer = DevicesFragment.getIpServerLongclick();
        Intent  ActConf;

        switch (which){
            case 0:     //General Configuration
                ActConf = new Intent(ActivityOptions.this, ActivityGeneralConf.class);
                ActConf.putExtra("ipServer",ipServer);
                startActivity(ActConf);
                break;
            case 1:     //Net Configuration
                ActConf = new Intent(ActivityOptions.this, ActivityNetConf.class);
                ActConf.putExtra("ipServer",ipServer);
                startActivity(ActConf);
                break;
            case 2:     //Net Information
                ActConf = new Intent(ActivityOptions.this, ActivityNetInfo.class);
                ActConf.putExtra("ipServer",ipServer);
                startActivity(ActConf);
                break;
            case 3:     //NTP Adjust
                ActConf = new Intent(ActivityOptions.this, ActivityNtpConf.class);
                ActConf.putExtra("ipServer",ipServer);
                startActivity(ActConf);
                break;
            case 4:     //System Adjust
                ActConf = new Intent(ActivityOptions.this, ActivitySystemConf.class);
                ActConf.putExtra("ipServer",ipServer);
                startActivity(ActConf);
                break;
            default:
                break;

        }
    }
}
