package soulissclient.net;

import android.os.Looper;
import android.util.Log;

import com.domotica.domotica_bsnet.preferencesHandler;

import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import soulissclient.Constants;

/**
 * Apre una porta sul 23000 e si mette in ascolto per le risposte.
 * usa un thread pool executor, anche se active count in realta non va mai a superare uno
 *
 * @author Ale
 */
public class UDPRunnable implements Runnable {
    // implements Runnable so it can be created as a new thread
    private static final String TAG = "Souliss:UDP";

    // private Context context;

    final int MAX_THREADS = 8;
    private preferencesHandler prefs;
    private DatagramSocket socket;              //socket for sending and receiving datagram packets
    private ThreadPoolExecutor threadExecutor;

public UDPRunnable(preferencesHandler prefs) {
        super();

        this.prefs = prefs;

        Log.i(Constants.TAG, "***UDPRunnable Created");

        // this.context = ctx;
        threadExecutor = new ThreadPoolExecutor(
                MAX_THREADS / 2, // core thread pool size
                MAX_THREADS, // maximum thread pool size
                53, // time to wait before resizing pool
                TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(MAX_THREADS, true),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }


    public void run() {

        // Souliss listens on port 23000
        Looper.prepare();   //message loop for the thread
        // lifecycle

        while (true) {

            try {
                // InetAddress serverAddr = InetAddress.getByName(SOULISSIP);
                DatagramChannel channel = DatagramChannel.open();
                socket = channel.socket();

                // socket = new DatagramSocket();
                socket.setReuseAddress(true);
                socket.setBroadcast(true);
                // port to receive souliss board data
                InetSocketAddress sa = new InetSocketAddress(Constants.Net.SERVERPORT);
                socket.bind(sa);

                // create a buffer to fileCopy packet contents into
                byte[] buf = new byte[200];
                // create a packet to receive
                final DatagramPacket packet = new DatagramPacket(buf, buf.length);
                int to = prefs.getServiceIntervalmsec();
                Log.i(TAG, "***Waiting on packet, timeout=" + to);
                socket.setSoTimeout(to);
                // wait to receive the packet
                socket.receive(packet);
                Log.i(Constants.TAG, "***Packet received, spawning decoder and dying. Recvd bytes=" + packet.getLength());
                // spawn a decoder and go on

                threadExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        //UDPSoulissDecoder decoder = new UDPSoulissDecoder(opzioni, SoulissApp.getAppContext());
                        UDPSoulissDecoder decoder = new UDPSoulissDecoder(prefs);
                        Log.d(Constants.TAG, "***Created decoder:" + decoder.toString());
                        decoder.decodeVNetDatagram(packet);
                    }
                });
                Log.v(Constants.TAG, "***ThreadPool, active=" + threadExecutor.getActiveCount() + ", completed:" + threadExecutor.getCompletedTaskCount() + ", poolsize:" + threadExecutor.getPoolSize());

                socket.close();

            } catch (BindException e) {
                Log.e(Constants.TAG, "***UDP Port busy, Souliss already listening? " + e.getMessage());
                e.printStackTrace();
                try {
                    //Thread.sleep(opzioni.getDataServiceIntervalMsec());
                    socket.close();
                } catch (Exception e1) {
                    Log.e(Constants.TAG, "***UDP socket close failed: " + e1.getMessage());
                }
            } catch (SocketTimeoutException e2) {
                Log.w(TAG, "***UDP SocketTimeoutException close!" + e2);
                socket.close();
            } catch (ClosedByInterruptException xc) {
                xc.printStackTrace();
                Log.e(Constants.TAG, "***UDP runnable interrupted!");
                socket.close();
                Thread.currentThread().interrupt();
                return;
            } catch (Exception ee) {
                ee.printStackTrace();
                Log.e(Constants.TAG, "***UDP unhandled error!" + ee.getMessage() + " of class " + ee.getClass());
                socket.close();
                Thread.currentThread().interrupt();
            }
        }
    }
}
