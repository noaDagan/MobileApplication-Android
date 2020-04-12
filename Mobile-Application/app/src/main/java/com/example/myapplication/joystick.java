package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MotionEventCompat;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class joystick extends AppCompatActivity {
    private PrintWriter writer;
    public String SERVER_IP; //server IP address
    public int SERVER_PORT; //server port
    joystickCanvas joystickCanvas;
    Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joystick);
        Intent intent = getIntent();
        //Get ip and port from the user.
        this.SERVER_IP = intent.getStringExtra("ip");
        String portIntent = intent.getStringExtra("port");
        this.SERVER_PORT = (Integer.parseInt(portIntent));
        //Connect to server
        connect();
        //Set the view ,ip ,port to joystick canvas.
        joystickCanvas = new joystickCanvas(this);
        joystickCanvas.SERVER_IP = intent.getStringExtra("ip");
        joystickCanvas.SERVER_PORT = (Integer.parseInt(portIntent));
        joystickCanvas.writer = writer;
        setContentView(joystickCanvas);
    }


    /**
     * The function connect to server
     */
    public void connect() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    //Put the ip address and port
                    InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                    Log.d("TCP Client", "C: Connecting...");
                    //create a socket to make the connection with the server
                    socket = new Socket(serverAddr, SERVER_PORT);
                    writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                } catch (Exception e) {
                    Log.e("TCP", "C: Error", e);
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }


    /**
     * The function close the socket
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        writer.flush();
        writer.close();
        try {
            socket.close();

        } catch (Exception e) {
            Log.e("TCP", "C: Error", e);
        }
    }

}

