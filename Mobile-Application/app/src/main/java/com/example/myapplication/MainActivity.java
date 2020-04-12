package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendMessage(View view) {
        //Get the ip and port from the user
        EditText ipText = (EditText)findViewById(R.id.ipText);
        EditText portText = (EditText)findViewById(R.id.portText);
        Intent intent=new Intent(this,joystick.class);
        //Send the ip and port to the joystick
        String ip = ipText.getText().toString();
        String port = portText.getText().toString();
        intent.putExtra("ip",ip);
        intent.putExtra("port",port);
        startActivity(intent);
    }

}
