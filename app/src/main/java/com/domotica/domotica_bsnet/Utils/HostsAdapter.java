package com.domotica.domotica_bsnet.Utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.domotica.domotica_bsnet.Network.NetInfo;
import com.domotica.domotica_bsnet.Network.bsnetDevice;
import com.domotica.domotica_bsnet.R;

import java.util.ArrayList;

import soulissclient.Constants;

import static soulissclient.HandleResource.icons.typical_icon;

/**
 * Created by jctejero on 08/11/2017.
 */

public class HostsAdapter extends ArrayAdapter<Void> {
    private Context ctxt;
    private ArrayList<bsnetDevice> hosts = null;

    public HostsAdapter(Context ctxt, ArrayList hosts) {
        super(ctxt, R.layout.list_host, hosts);
        this.ctxt = ctxt;
        this.hosts = hosts;
    }

    public HostsAdapter(Context ctxt) {
        super(ctxt, R.layout.list_host, R.id.hostname);
        this.ctxt = ctxt;
    }

    private class ViewHolder {
        TextView host;
        TextView mac;
        TextView vendor;
        ImageView logo;
    }

    public bsnetDevice getHost(int pos){
        return hosts.get(pos);
    }

    public void setHosts(ArrayList<bsnetDevice> hosts){
        if(hosts != null) {
            if(this.hosts != null) {
                this.hosts.clear();
                //this.hosts = hosts;
                this.hosts.addAll(new ArrayList<bsnetDevice>(hosts));
            }else {
                this.hosts = new ArrayList<bsnetDevice>(hosts);
            }
        }else this.hosts.clear();

        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater mInflater = LayoutInflater.from(ctxt);
            convertView = mInflater.inflate(R.layout.list_host, null);
            holder = new ViewHolder();
            holder.host = (TextView) convertView.findViewById(R.id.hostname);
            holder.mac = (TextView) convertView.findViewById(R.id.mac);
            holder.vendor = (TextView) convertView.findViewById(R.id.device);
            holder.logo = (ImageView) convertView.findViewById(R.id.logo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        try {
            bsnetDevice host = hosts.get(position);

            holder.logo.setImageResource(typical_icon(host.typical));

            holder.mac.setVisibility(View.VISIBLE);

            if (host.hostname != null && !host.hostname.equals(host.ipAddress)) {
                holder.host.setText(host.hostname );
                holder.mac.setText(host.ipAddress);
            } else {
                holder.host.setText(host.ipAddress);
                if (!host.hardwareAddress.equals(NetInfo.NOMAC)) {
                    holder.mac.setText(host.hardwareAddress);
                } else {
                    holder.mac.setVisibility(View.GONE);
                }
            }


            if(host.deviceType == bsnetDevice.TYPE_GATEWAY){
                holder.vendor.setText(ctxt.getString(R.string.info_vendor, ctxt.getString(R.string.info_gateway), host.ssid));
            }
            else holder.vendor.setText(ctxt.getString(R.string.info_vendor, ctxt.getString(R.string.info_device), host.ssid));

        }catch (Exception ie){
            Log.e(Constants.TAG, ie.getMessage());
        }

        return convertView;
    }
}