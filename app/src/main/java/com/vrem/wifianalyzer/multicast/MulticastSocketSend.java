package com.vrem.wifianalyzer.multicast;

/**
 * Created by Dário on 24/03/2017.
 */

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MulticastSocketSend extends AsyncTask<Void, Void, Boolean> {


    final static String INET_ADDR = "224.0.0.3";
    final static int PORT = 8888;

    String message ;


    public MulticastSocketSend(String message) {
        this.message = message;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return send();
    }

    private Boolean send()  {

        DatagramSocket serverSocket = null;
        try {
            // Get the address that we are going to connect to.
            InetAddress addr = InetAddress.getByName(INET_ADDR);
            // Open a new DatagramSocket, which will be used to send the data.
            serverSocket = new DatagramSocket();

            // Create a packet that will contain the data
            // (in the form of bytes) and send it.
            DatagramPacket msgPacket = new DatagramPacket(message.getBytes(),
                    message.getBytes().length, addr, PORT);
            serverSocket.send(msgPacket);

            serverSocket.close();

            Log.d("Chat","Sending: " + message);
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        finally {
            //This should be implemented with try with resources for higher Android versions
            // Android 4.1 doesnt support try with resources, therefore the finally approach is implemented
            if(serverSocket != null && !serverSocket.isClosed())
            {
                serverSocket.close();
            }
        }


        return true;
    }
}
