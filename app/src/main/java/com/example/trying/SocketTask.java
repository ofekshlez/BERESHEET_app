package com.example.trying;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class SocketTask extends AsyncTask<String, Void, String> {
    //private final static String IP_ADDRESS = "192.168.1.21"; //phone ip
    private final static String IP_ADDRESS = "192.168.68.59"; //home ip
    //private final static String IP_ADDRESS = "172.19.27.137"; //school ip
    //private final static String IP_ADDRESS = "192.168.1.23"; //grandma's ip
    //private final static String IP_ADDRESS = "192.168.1.164"; //Eyal's ip


    private final static int PORT = 5000; //HTTP port
    private final static int PACKET_SIZE = 2048; //standard 1kb packet size
    private Socket socket;
    private String sendingStr="";
    private String receivingStr="";
    BufferedReader reader;
    private final static int CONNECTION_TIMEOUT = 3500;

    public SocketTask(String str1) {
        this.sendingStr = str1;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... arrStrings)
    {
        try {
            this.socket = new Socket();
            InetSocketAddress socketAddress = new InetSocketAddress(IP_ADDRESS, PORT);
            this.socket.connect(socketAddress, CONNECTION_TIMEOUT);
            send(this.sendingStr);
            receive();
            this.socket.close();
        } catch (Exception e) {
            Log.e("Exception", e.toString());
            this.receivingStr = "error";
        }
        return this.receivingStr;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void send(String sendingStr)
    {
        try {
            OutputStream outStream = this.socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outStream, true);
            writer.println(sendingStr);
            Log.d("Result", "sent");
        }
        catch (Exception e) {
            Log.e("Exception", e.toString());
            this.receivingStr = "error";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void receive()
    {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            receivingStr = reader.readLine();
            reader.close();
        }
        catch (IOException e) {
            Log.e("Exception", e.toString());
            this.receivingStr = "error";
        }
    }
}

