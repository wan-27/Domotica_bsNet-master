package soulissclient.net;

import android.content.Intent;
import android.util.Log;

import com.domotica.domotica_bsnet.preferencesHandler;

import java.net.DatagramPacket;
import java.util.ArrayList;

import soulissclient.Constants;

import static junit.framework.Assert.assertEquals;

/**
 * Created by jctejero on 19/10/2017.
 *
 * Based on class of the same name included on Souliss App --> @Author Ale
 */

public class UDPSoulissDecoder {

    private preferencesHandler prefs;

    public UDPSoulissDecoder(preferencesHandler prefs){
        this.prefs = prefs;
    }

    /**
     * Decodes lower level MaCaCo packet
     *
     * @param macacoPck
     */
    private void decodeMacaco(ArrayList<Short> macacoPck) {
        int functionalCode = macacoPck.get(0);
        // int putIn = mac.get(1);
        // PUTIN :) 2 byte
        // STARTOFFSET 1 byte
        // NUMBEROF 1 byte
        int startOffset = macacoPck.get(3);
        int numberOf = macacoPck.get(4);
        Log.d(Constants.TAG, ">- Macaco IN: Start Offset:" + startOffset + ", Number of " + numberOf + ", functional code : " + functionalCode);

        switch (functionalCode) {
/*
            case Constants.Net.Souliss_UDP_function_subscribe_data:
                Log.d(Constants.TAG, ">- Subscription answer");
                decodeStateRequest(macacoPck);
                break;

            case Constants.Net.Souliss_UDP_function_poll_resp:
                Log.d(Constants.Net.TAG, ">- Poll answer");
                decodeStateRequest(macacoPck);
                processTriggers();
                processWidgets();
                break;
*/
            case Constants.Net.Souliss_UDP_function_ping_resp:
                // assertEquals(mac.size(), 8);
                Log.d(Constants.TAG, ">- Ping response bytes " + macacoPck.size());
                decodePing(macacoPck);
                break;
/*
            case Constants.Net.Souliss_UDP_function_ping_bcast_resp:
                // assertEquals(mac.size(), 8);
                Log.d(Constants.Net.TAG, ">- Ping BROADCAST response bytes " + macacoPck.size());
                decodePing(macacoPck);
                break;
*/
            case Constants.Net.Souliss_UDP_function_force_back_register:
            case Constants.Net.Souliss_UDP_subscribe_data_answer:
            case Constants.Net.Souliss_UDP_function_subscribe_resp:
                Log.d(Constants.TAG, ">- State request answer");
                // I Send a broadcast for anything received
                Intent i = new Intent();
                i.putExtra("MACACO", macacoPck);
                i.setAction(Constants.CUSTOM_INTENT_SOULISS_SUBSCRIBE_RESP);
                prefs.getCntx().sendBroadcast(i);
//                decodeStateRequest(macacoPck);
//                processTriggers();
//                processWidgets();
                break;

            case Constants.Net.Souliss_UDP_function_typreq_resp:// Answer for assigned
                // typical logic
                Log.d(Constants.TAG, ">- TypReq answer");
                decodeTypRequest(macacoPck);
                break;
            /*
            case Constants.Net.Souliss_UDP_function_health_resp:// Answer nodes healty
                Log.d(Constants.Net.TAG, ">- Health answer");
                decodeHealthRequest(macacoPck);
                break;
            case Constants.Net.Souliss_UDP_function_db_struct_resp:// Answer nodes
                assertEquals(macacoPck.size(), 9); // healty
                Log.w(Constants.Net.TAG, ">- DB Structure answer");
                decodeDBStructRequest(macacoPck);
                break;
            case 0x83:
                Log.e(Constants.Net.TAG, "!!! (Functional code not supported)");
                Toast.makeText(context, "Functional code not supported", Toast.LENGTH_SHORT).show();
                break;
            case 0x84:
                Log.e(Constants.Net.TAG, "** (Data out of range)");
                Toast.makeText(context, "Data out of range", Toast.LENGTH_SHORT).show();
                break;
            case 0x85:
                Log.e(Constants.Net.TAG, "** (Subscription refused)");
                Toast.makeText(context, "Subscription refused", Toast.LENGTH_SHORT).show();
                break;
            default:
                Log.e(Constants.Net.TAG, "** Unknown functional code: " + functionalCode);
                break;
*/
        }
    }

    /**
     * process the UDP Packet recived
     *
     * @param packet incoming datagram
     */
    public void decodeVNetDatagram(DatagramPacket packet) {
        int checklen = packet.getLength();
        // Log.d(Constants.TAG, "** Packet received");
        ArrayList<Short> mac = new ArrayList<>();
        for (int ig = 7; ig < checklen; ig++) {
            mac.add((short) (packet.getData()[ig] & 0xFF));
        }

        // 0xf 0xe 0x17 0x64 0x1 0x11 0x0 0x18 0x0 0x0 0x0 0x3 0xa 0x8 0xa
        // il primo byte dev essere la dimensione
        if (checklen != packet.getData()[0]) {
            StringBuilder dump = new StringBuilder();

            for (int ig = 0; ig < checklen; ig++) {
                // 0xFF & buf[index]
                dump.append("0x").append(Long.toHexString(0xFF & packet.getData()[ig])).append(" ");
                // dump.append(":"+packet.getData()[ig]);
            }
            Log.e(Constants.TAG, "**WRONG PACKET SIZE: " + packet.getData()[0] + "bytes\n" + "Actual size: " + checklen
                    + "\n" + dump.toString());
        } else {
            decodeMacaco(mac);
        }

        //Include address host at mac
        for(int ad = 0; ad<4; ad++){
            mac.add(ad, (short) ((packet.getAddress().getAddress()[ad])&0xFF));
        }

        Log.d(Constants.TAG, "Send MACACO Broadcast ");

        // I Send a broadcast for anything received
        Intent i = new Intent();
        i.putExtra("MACACO", mac);
        i.setAction(Constants.CUSTOM_INTENT_SOULISS_RAWDATA);
        prefs.getCntx().sendBroadcast(i);

        /*
        //Segnala a Tasker
        if (SoulissApp.getOpzioni().isTaskerEnabled()) {
            Intent it = new Intent();
            it.setAction(com.twofortyfouram.locale.Intent.ACTION_REQUEST_QUERY);
            opzioni.getContx().sendBroadcast(it);
        }
        // resetta backoff irraggiungibilit�
        opzioni.resetBackOff();
        //se era irraggiungibile, pinga
        if (!opzioni.isSoulissReachable())
            opzioni.setBestAddress();
*/
    }

    /**
     * Alla ricezione di una risposta ping, aggiorna il cached address F e`
     * locale, se trovo B e` Remoto
     *
     * When receiving a ping reply, update the cached address F
     * local, if I find B Remote
     *
     * @param mac
     */
    private void decodePing(ArrayList<Short> mac) {
/*
        int putIn = mac.get(1);

        // se trovo F e` locale, se trovo B e` Remoto

        SharedPreferences.Editor editor = soulissSharedPreference.edit();
        // se ho gia` indirizzo privato che funziona (!= 0)
        boolean alreadyPrivate = opzioni.isSoulissIpConfigured() && opzioni.getPrefIPAddress().equals(opzioni.getCachedAddress());
        if (putIn == 0xB && (!alreadyPrivate || !opzioni.isSoulissReachable())) {// PUBBLICO
            opzioni.setCachedAddr(opzioni.getIPPreferencePublic());
            editor.putString("cachedAddress", opzioni.getIPPreferencePublic());
            Log.w(Constants.Net.TAG, ">--decodePing Set cached address (PUBLIC): " + opzioni.getIPPreferencePublic());
        } else if (putIn == 0xF) {// PRIVATO
            opzioni.setCachedAddr(opzioni.getPrefIPAddress());
            editor.putString("cachedAddress", opzioni.getPrefIPAddress());
            Log.w(Constants.Net.TAG, ">--decodePing Set cached address: " + opzioni.getPrefIPAddress());
        } else if (putIn == 0x5) {// BROADCAST VA, USO QUELLA
            try {// sanity check
                final InetAddress toverify = NetUtils.extractTargetAddress(mac);
                Log.i(Constants.Net.TAG, "decodePing Parsed private IP: " + toverify.getHostAddress());
                /**
                 * deve essere determinato se l'indirizzo appartiene ad un nodo
                 * e se è all'interno della propria subnet. Se entrambe le
                 * verifiche hanno esito positivo, si può utilizzare tale
                 * indirizzo, altrimenti si continua ad usare broadcast.
                 */
/*
                Log.d(Constants.Net.TAG, "BROADCAST detected, IP to verify: " + toverify);
                Log.d(Constants.Net.TAG, "BROADCAST, subnet: " + NetUtils.getDeviceSubnetMask(context));
                Log.d(Constants.Net.TAG, "BROADCAST, me: " + localHost);

                Log.d(Constants.Net.TAG,
                        "BROADCAST, belongsToNode: "
                                + NetUtils.belongsToNode(toverify, NetUtils.getDeviceSubnetMask(context)));
                Log.d(Constants.Net.TAG,
                        "BROADCAST, belongsToSameSubnet: "
                                + NetUtils.belongsToSameSubnet(toverify,
                                NetUtils.getDeviceSubnetMask(context), localHost));
                if (NetUtils.belongsToNode(toverify, NetUtils.getDeviceSubnetMask(context))
                        && NetUtils.belongsToSameSubnet(toverify, NetUtils.getDeviceSubnetMask(context),
                        localHost)) {
                    opzioni.setCachedAddr(toverify.getHostAddress());
                    editor.putString("cachedAddress", toverify.getHostAddress());
                    if (!opzioni.isSoulissIpConfigured()) {// forse e` da
                        // togliere
                        Log.w(Constants.Net.TAG, "Auto-setting private IP: " + opzioni.getCachedAddress());
                        opzioni.setIPPreference(opzioni.getCachedAddress());
                        //lo aggiungo a dizionario
                        SoulissGlobalPreferenceHelper gbPref = new SoulissGlobalPreferenceHelper(SoulissApp.getAppContext());
                        gbPref.addWordToIpDictionary(opzioni.getCachedAddress());
                    }
                } else {
                    throw new UnknownHostException("belongsToNode or belongsToSameSubnet = FALSE");
                }
            } catch (final Exception e) {
                Log.e(Constants.Net.TAG, "Error in address parsing, using BCAST address: " + e.getMessage(), e);
                opzioni.setCachedAddr(Constants.Net.BROADCASTADDR);
                editor.putString("cachedAddress", Constants.Net.BROADCASTADDR);

            }

        } else if (alreadyPrivate) {
            Log.w(Constants.Net.TAG,
                    "Local address already set. I'll NOT overwrite it: "
                            + soulissSharedPreference.getString("cachedAddress", ""));
        } else
            Log.e(Constants.Net.TAG, "Unknown putIn code: " + putIn);
        editor.commit();
*/
    }

    /**
     * Definizione dei tipici
     *
     * @param mac
     */
    private void decodeTypRequest(ArrayList<Short> mac) {
        try {
            assertEquals(Constants.Net.Souliss_UDP_function_typreq_resp, (short) mac.get(0));
/*
            SharedPreferences.Editor editor = soulissSharedPreference.edit();
            short tgtnode = mac.get(3);
            int numberOf = mac.get(4);
            int done = 0;
            // SoulissNode node = database.getSoulissNode(tgtnode);
            int typXnodo = soulissSharedPreference.getInt("TipiciXNodo", 1);
            Log.i(Constants.Net.TAG, ">--decodeTypRequest:" + tgtnode + " NUMOF:" + numberOf + " TYPICALSXNODE: "
                    + typXnodo);
            // creates Souliss nodes
            for (int j = 0; j < numberOf; j++) {
                if (mac.get(5 + j) != 0) {// create only not-empty typicals
                    SoulissDBHelper db = new SoulissDBHelper(SoulissApp.getAppContext());
                    short slot = ((short) (j % typXnodo));
                    short node = (short) (j / typXnodo + tgtnode);
                    SoulissTypicalDTO dto;
                    try {
                        dto = db.getTypical(node, slot).getTypicalDTO();
                    } catch (Exception e) {
                        dto = new SoulissTypicalDTO();
                    }
                    dto.setTypical(mac.get(5 + j));
                    dto.setSlot(slot);// magia
                    dto.setNodeId(node);
                    try {
                        dto.persist();
                        // conta solo i master
                        if (mac.get(5 + j) != it.angelic.soulissclient.Constants.Typicals.Souliss_T_related) {
                            done++;
                            Log.d(Constants.Net.TAG, "---PERSISTED TYPICAL ON NODE:" + ((short) (j / typXnodo + tgtnode))
                                    + " SLOT:" + ((short) (j % typXnodo)) + " TYP:" + (mac.get(5 + j)));
                        }
                    } catch (Exception ie) {
                        Log.e(Constants.Net.TAG, "---PERSIST ERROR:" + ie.getMessage() + " - " + ((short) (j / typXnodo + tgtnode))
                                + " SLOT:" + ((short) (j % typXnodo)) + " TYP:" + (mac.get(5 + j)));

                    }
                }
            }
            if (soulissSharedPreference.contains("numTipici"))
                editor.remove("numTipici");// unused
            editor.putInt("numTipici", database.countTypicals());
            editor.apply();
            Log.i(Constants.Net.TAG, "Refreshed " + numberOf + " typicals for node " + tgtnode);
*/
        } catch (Exception uy) {
            Log.e(Constants.TAG, "decodeTypRequest ERROR", uy);
        }
    }
}
