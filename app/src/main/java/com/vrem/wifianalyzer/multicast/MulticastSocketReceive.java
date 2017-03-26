package com.vrem.wifianalyzer.multicast;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Dário on 24/03/2017.
 */

public class MulticastSocketReceive extends Thread {


    final static String INET_ADDR = "224.0.0.3";
    final static int PORT = 8888;

    private MulticastSocket clientSocket;
    byte[] buf;
    MulticastFragment root = null;
    private boolean keepRunning;


    public MulticastSocketReceive(MulticastFragment root) {

        // Create a buffer of bytes, which will be used to store
        // the incoming bytes containing the information from the server.
        // Since the message is small here, 256 bytes should be enough.

        this.root = root;

        buf = new byte[4096];
        keepRunning = false;

        try {

            // Get the address that we are going to connect to.
            InetAddress address = InetAddress.getByName(INET_ADDR);

            // Create a new Multicast socket (that will allow other sockets/programs
            // to join it as well.
            clientSocket = new MulticastSocket(PORT);
            //Joint the Multicast group.
            clientSocket.joinGroup(address);
        } catch (UnknownHostException ex) {
            Logger.getLogger(MulticastSocketReceive.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {

        keepRunning = true;


        try {
            while (keepRunning) {
                // Receive the information and print it.
                DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
                clientSocket.receive(msgPacket);

                String msg = new String(buf, 0, buf.length);
                Log.d("Chat","Received: " + msg);
                root.receiveMessage(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            keepRunning = false;
        }

    }

    /**
     * Stop the thread
     */
    public void stopRunning() {
        keepRunning = false;
    }

    public boolean isRunning() {
        return keepRunning;
    }
}
