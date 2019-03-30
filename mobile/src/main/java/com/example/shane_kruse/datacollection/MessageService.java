package com.example.shane_kruse.datacollection;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.text.SimpleDateFormat;
import java.util.Date;

//Extend WearableListenerService//

public class MessageService extends WearableListenerService {

    private Date date;
    private SimpleDateFormat sdf;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        //If the message’s path equals "/my_path"...//
        Log.d("Path", messageEvent.getPath());
        if (messageEvent.getPath().equals("/my_path")) {

            //...retrieve the message//
            final String message = new String(messageEvent.getData());

            date = new Date();
            sdf = new SimpleDateFormat("hh:mm:ss a");

            Intent messageIntent = new Intent();
            messageIntent.setAction(Intent.ACTION_SEND);
            messageIntent.putExtra("message", message  + "," +
                    sdf.format(date) + "," + System.currentTimeMillis());

            //Broadcast the received Data Layer messages locally//
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
        }
        else {
            super.onMessageReceived(messageEvent);
        }
    }

}
