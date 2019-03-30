package com.example.shane_kruse.datacollection;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends WearableActivity implements SensorEventListener {

    private TextView mTextView;
    private SensorManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Enables Always-on
        setAmbientEnabled();
    }

    class SendMessage extends Thread {
        String path;
        String message;

        //Constructor for sending information to the Data Layer//
        SendMessage(String p, String m) {
            path = p;
            message = m;
        }

        public void run() {

            //Retrieve the connected devices//
            Task<List<Node>> nodeListTask =
                    Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
            try {

                //Block on a task and get the result synchronously//
                List<Node> nodes = Tasks.await(nodeListTask);
                for (Node node : nodes) {

                    //Send the message///
                    Task<Integer> sendMessageTask =
                            Wearable.getMessageClient(MainActivity.this).sendMessage(node.getId(), path, message.getBytes());

                    try {

                        Integer result = Tasks.await(sendMessageTask);

                        //Handle the errors//
                    } catch (ExecutionException exception) {
                        //TO DO//
                    } catch (InterruptedException exception) {
                        //TO DO//
                    }

                }
            } catch (ExecutionException exception) {
                //TO DO//
            } catch (InterruptedException exception) {
                //TO DO//
            }
        }
    }

    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;

        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        //Use the same path//
        String datapath = "/my_path";

        String msg = String.valueOf(x) + "," + String.valueOf(y) + "," + String.valueOf(z);
        new SendMessage(datapath, msg).start();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    protected void onStart() {
        super.onStart();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sm.registerListener(this,
                sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sm.unregisterListener(this);
    }

}
