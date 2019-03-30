package com.example.shane_kruse.datacollection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    protected Handler myHandler;
    private TextView dataView;
    private int count;
    private Button sendButton;
    private File file;
    private BufferedWriter bw;
    private Date date;
    private SimpleDateFormat sdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        file = new File(this.getFilesDir(), "data.txt");

        // Create buffered writer to test file, if creation fails attempt to create the file again
        try {
            bw = new BufferedWriter(new FileWriter(file));
        } catch (IOException ex) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.e("FILE CREATION STATUS: ","File could not be created");
            }
        }

        dataView = findViewById(R.id.data_view);
        dataView.setMovementMethod(new ScrollingMovementMethod());

        //Create a message handler//
        myHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                return true;
            }
        });

        //Register to receive local broadcasts, which we'll be creating in the next step//
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        Receiver messageReceiver = new Receiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

        sendButton = findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                DataSender ds = new DataSender(MainActivity.this, file);
                ds.execute();
            }
        });
    }

    //Define a nested class that extends BroadcastReceiver//
    public class Receiver extends BroadcastReceiver {

        public Intent intent;

        @Override
        public void onReceive(Context context, Intent intent) {
            //Upon receiving each message from the wearable, display the following text//

            this.intent = intent;

            count++;
            String message = intent.getStringExtra("message");

            dataView.append(String.valueOf(count) + ": " + message + "\n");
            try {
                bw.write(message + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

        }
    }
}
