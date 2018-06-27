package soulissclient.net;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.domotica.domotica_bsnet.MainActivity;
import com.domotica.domotica_bsnet.preferencesHandler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import soulissclient.Constants;

import static android.content.ContentValues.TAG;
import static junit.framework.Assert.assertEquals;

/**
 * Created by jctejero on 16/09/2017.
 *
 * function has been imported from UDPHelper class from shine@angelic.it
 */

public class UDPHelper {

    private static void debugbytes(String text, byte[] frame){
        StringBuilder deb = new StringBuilder();
        for (int i = 0; i < frame.length; i++) {
            deb.append("0x").append(Long.toHexString((long) frame[i] & 0xff)).append(" ");
        }
        Log.d(Constants.TAG, text + deb.toString());
    }

    private static void debugByteArray(String text, ArrayList<Byte> frame) {
        StringBuilder deb = new StringBuilder();
        for (int i = 0; i < frame.size(); i++) {
            deb.append("0x").append(Long.toHexString((long) frame.get(i) & 0xff)).append(" ");
        }
        Log.d(Constants.TAG, text + deb.toString());
    }

    /**
     * contruction of the frame vNet: 0D 0C 17 11 00 64 01 XX 00 00 00 01 01 0D è la
     * lunghezza complessiva del driver vNet 0C è la lunghezza complessiva vNet
     * 17 è la porta MaCaco su vNet (fissa)
     *
     * 11 00 è l'indirizzo della scheda quindi un indirizzo IP con ultimo byte
     * 100 va SEMPRE passato l'indirizzo privato della scheda
     *
     * 64 01 è l'indirizzo dell'interfaccia utente, 01 è l'User Mode Index
     *
     * @param macaco
     *            frame input
     * @return
     */
    private static ArrayList<Byte> buildVNetFrame(List<Byte> macaco, String ipd, int useridx, int nodeidx) {

        assertEquals(true, useridx < Constants.MAX_USER_IDX);
        assertEquals(true, nodeidx < Constants.MAX_NODE_IDX);

        ArrayList<Byte> frame = new ArrayList<>();
        InetAddress ip;
        try {
            ip = InetAddress.getByName(ipd);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return frame;
        }
        byte[] dude = ip.getAddress();
        frame.add((byte) 23);// PUTIN

        frame.add(dude[3]);// es 192.168.1.XX BOARD
        // n broadcast : La comunicazione avviene utilizzando l'indirizzo IP
        // 255.255.255.255 a cui associare l'indirizzo vNet 0xFFFF.
        frame.add((byte) ipd.compareTo(Constants.Net.BROADCASTADDR) == 0 ? dude[2] : 0);
        // 192.168.XX.0
        frame.add((byte) nodeidx); // NODE INDEX
        frame.add((byte) useridx);// USER IDX

        // aggiunge in testa il calcolo
        frame.add(0, (byte) (frame.size() + macaco.size() + 1));
        frame.add(0, (byte) (frame.size() + macaco.size() + 1));// Check 2

        frame.addAll(macaco);

        //Byte[] ret = new Byte[frame.size()];

        debugByteArray("buildVNetFrame --> ", frame);

        // Send broadcast timeout
        Intent i = new Intent();
        //int it = SoulissApp.getOpzioni().getRemoteTimeoutPref() + SoulissApp.getOpzioni().getBackoff() * 1000;
        int it = 1000;
        Log.d(TAG, "buildVNetFrame Posting timeout msec. " + it);
        i.putExtra("REQUEST_TIMEOUT_MSEC", it);
        i.setAction(Constants.CUSTOM_INTENT_SOULISS_TIMEOUT);
        MainActivity.getPrefs().getCntx().sendBroadcast(i);

        return frame;
    }

    /**
     * Issue a command to Souliss, at specified coordinates
     *
     * //@param ip            //typical device address
     * @param startOffset   //Initial register
     * //@param cmd
     */

    public static void thread_issueSouliss(final byte functional, final String ipaddress,
                                           final String startOffset, final preferencesHandler prefs, final String... cmd) {
        Thread t = new Thread() {
            public void run() {
                //Read module config mode
                String[] ips = ipaddress.split("\\.");

                issueSouliss(functional, ipaddress, startOffset, prefs, cmd);
            }
        };
        t.start();
    }

    public static void issueSouliss(byte functional, String ip, String startOffset, preferencesHandler prefs, String... cmd){
        ArrayList<Byte> buf;
        buf = buildVNetFrame(buildMaCaCoMassive(functional, startOffset, cmd), ip, prefs.getUserIndex(), prefs.getNodeIndex());


        debugByteArray("Trama issueSoulissCommand --> ", buf);

        Log.i(Constants.TAG, "<-- issueSoulissCommand " + cmd[0] + " sent to: " + ip);

        sendTramaToIP(ip, buf, prefs);
    }

    /**
     * Issue a command to Souliss, at specified coordinates
     *
     * @param ip        //typical device address
     * @param slot      //Device slot
     * //@param cmd
     */
    public static void issueSoulissCommand(String ip, String nodeid, String slot, preferencesHandler prefs, String... cmd ) {

        ArrayList<Byte> buf;
        buf = buildVNetFrame(buildMaCaCoMassive(Constants.Net.Souliss_UDP_function_force, slot, cmd),
                ip, prefs.getUserIndex(), prefs.getNodeIndex());


        debugByteArray("Trama issueSoulissCommand --> ", buf);

        Log.i(Constants.TAG, "<-- issueSoulissCommand " + cmd[0] + " sent to: " + ip);

        sendTramaToIP(ip, buf, prefs);
    }

    /**
     * Subscribe request.
     *
     * @param prefs
     *            App preferences
     * @param numberOf
     *            number of nodes to request
     * @param startOffset
     */
    public static void stateRequest(byte functional, String ip, preferencesHandler prefs, int numberOf, int startOffset) {
        List<Byte> macaco = new ArrayList<>();
        macaco.add(functional);
        // PUTIN, STARTOFFEST, NUMBEROF
        macaco.add((byte) 0x0);// PUTIN
        macaco.add((byte) 0x0);// PUTIN

        macaco.add((byte) startOffset);// startnode
        macaco.add((byte) numberOf);// numberof

        ArrayList<Byte> buf = UDPHelper.buildVNetFrame(macaco, ip, prefs.getUserIndex(),
                prefs.getNodeIndex());

        sendTramaToIP(ip, buf, prefs);

    }

    /**
     * * N+1 N 17 B1 00 64 01 08 00 00 00 00 ipubbl can be = to ip, if local
     * area LAN is used
     *
     * @param ip
     *            private LAN IP address, mandatory
     * @throws Exception
     *             catch not implemented by design
     */

    //public static InetAddress ping(String ip, String ipubbl, int userix, int nodeix, @Nullable preferencesHandler pref) throws Exception {
    public static void ping(String ip, @Nullable preferencesHandler pref) throws Exception {
        List<Byte> macaco = new ArrayList<>();
        macaco = Arrays.asList(Constants.Net.PING_PAYLOAD);
        // qui inserisco broadcast
        byte whoami = 0xB;// PRIVATE by default

        macaco.set(1, whoami);
        ArrayList<Byte> buf = UDPHelper.buildVNetFrame(macaco, ip, pref.getUserIndex(), pref.getNodeIndex());

        debugByteArray("Trama ping --> ", buf);
        Log.i(Constants.TAG, "<-- Ping To: " + ip);

        sendTramaToIP(ip, buf, pref);
    }

    /**
     * * N+1 N 17 B1 00 64 01 08 00 00 00 00 ipubbl can be = to ip, if local
     * area LAN is used
     *
     * @return contacted address
     *
     * @param ip
     *            private LAN IP address, mandatory
     * @throws Exception
     *             catch not implemented by design
     */
    public static void discoverGateway(String ip, preferencesHandler pref) {

        List<Byte> macaco = new ArrayList<>();
        macaco = Arrays.asList(Constants.Net.PING_BCAST_PAYLOAD);
        // qui inserisco broadcast
        byte whoami = 0x5;// PRIVATE by default
        macaco.set(1, whoami);
        ArrayList<Byte> buf = UDPHelper.buildVNetFrame(macaco, ip, pref.getUserIndex(), pref.getNodeIndex());

        //byte[] merd = toByteArray(buf);

        sendTramaToIP(ip, buf, pref);

        Log.i(Constants.TAG, "<-- Check if Gateway: " + ip);
    }

    /**
     * Trigger a structural request for given device
     * @param pref
     * @param numberOf number of desired nodes' devices
     * @param startOffset node offset
     */
    public static void typicalRequest(String ip, preferencesHandler pref, int numberOf, int startOffset) {
        assertEquals(true, numberOf < 128);

        List<Byte> macaco = new ArrayList<>();

        // PUTIN, STARTOFFEST, NUMBEROF
        macaco.add(Constants.Net.Souliss_UDP_function_typreq);
        macaco.add((byte) 0x0);// PUTIN
        macaco.add((byte) 0x0);// PUTIN
        macaco.add((byte) startOffset);// startnode
        macaco.add((byte) numberOf);// numberof

        ArrayList<Byte> buf = UDPHelper.buildVNetFrame(macaco, ip, pref.getUserIndex(), pref.getNodeIndex());

        sendTramaToIP(ip, buf, pref);

        debugByteArray("Trama typicalRequest --> ", buf);
        Log.w(Constants.TAG, "<-- typicalRequest ");
    }

    /**
     * @param ip
     *            private LAN IP address, mandatory
     * @param prefs
     *
     * Request database of a Gateway (ip).
     *
     * The answer of the request has a fixed structure:
     *  - the number of nodes configured
     *  - the maximum number of allowed nodes
     *  - the number of slots for each node
     *  - the number of maximum subscription allowed by the gateway
     */
    public static void dbStructRequest(String ip, preferencesHandler prefs) {

        List<Byte> macaco = new ArrayList<>();
        macaco = Arrays.asList(Constants.Net.DBSTRUCT_PAYLOAD);
        ArrayList<Byte> buf = UDPHelper.buildVNetFrame(macaco, ip, prefs.getUserIndex(), prefs.getNodeIndex());

        sendTramaToIP(ip, buf, prefs);

        Log.w(Constants.TAG, "<-- dbStructRequest bytes");
    }

    private static ArrayList<Byte> buildMaCaCoMassive(byte functional, String typical, String... cmd) {
        assertEquals(true, functional < Byte.MAX_VALUE);
        ArrayList<Byte> frame = new ArrayList<>();

        frame.add(Byte.valueOf(functional));// functional code

        frame.add(Byte.valueOf("0"));// PUTIN
        frame.add(Byte.valueOf("0"));

        frame.add(Byte.valueOf(typical)); // STARTOFFSET
        frame.add((byte) cmd.length); // NUMBEROF

        for (String number : cmd) {
            // che schifo
            int merdata = Integer.decode(number);
            if (merdata > 255)
                Log.w(Constants.TAG, "Overflow with command string: " + number);
            frame.add((byte) merdata);
        }

        Log.d(Constants.TAG, "MaCaCo MASSIVE frame built size:" + frame.size());
        return frame;

    }

    /**
     * Send trama to ip address
     *
     * @param ip
     * @param buf
     * @trama pref
     * @return
     */
    public static void sendTramaToIP(String ip, ArrayList<Byte> buf, preferencesHandler pref){
        InetAddress serverAddr;
        DatagramSocket sender = null;
        DatagramPacket packet;

        try{
            serverAddr = InetAddress.getByName(ip);
            sender = getSenderSocket();

            byte[] merd = toByteArray(buf);

            packet = new DatagramPacket(merd, merd.length, serverAddr,  pref.getUDPPort());
            sender.send(packet);

        } catch (Exception e) {
            debugByteArray("Send Trama To IP Fail : ", buf);
            //Log.e(Constants.TAG, "Send Trama To IP Fail", e);
            return;
        } finally {
            if (sender != null && !sender.isClosed())
                sender.close();
        }
    }

    /**
     * Builds old-school byte array
     *
     * @param buf
     * @return
     */
    private static byte[] toByteArray(ArrayList<Byte> buf) {
        byte[] merd = new byte[buf.size()];
        for (int i = 0; i < buf.size(); i++) {
            merd[i] = buf.get(i);
        }
        return merd;
    }

    /**
     * This is a method
     *
     * @return
     */
    private static DatagramSocket getSenderSocket() {
        DatagramSocket sender = null;
        try {
            DatagramChannel channel = DatagramChannel.open();
            sender = channel.socket();
            sender.setReuseAddress(true);
            // hole punch
            InetSocketAddress sa = new InetSocketAddress(Constants.Net.SERVERPORT);
            sender.bind(sa);
        } catch (SocketException e) {
            Log.e(Constants.TAG, "SOCKETERR: " + e.getMessage());
        } catch (IOException e) {
            Log.e(Constants.TAG, "IOERR: " + e.getMessage());
        }
        return sender;
    }
}
