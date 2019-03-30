package com.example.shane_kruse.datacollection;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;

public class DataSender extends AsyncTask<Void, Void, Void> {

    private File file;
    private Socket s;
    private DataOutputStream dos;
    private BufferedReader br;
    private String data;
    private Context context;

    public DataSender(Context context, File file) {
        this.context = context;
        this.file = file;
    }
    @Override
    protected Void doInBackground(Void... voids) {

        // Connect to server and create output stream object
        try {
            s= new Socket("141.165.19.152", 42215);
            dos = new DataOutputStream(s.getOutputStream());
            br = new BufferedReader(new FileReader(file));
            while ((data = br.readLine()) != null) {
                Log.i("SENSOR DATA VALUE: ", data);
                dos.writeUTF(data);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Toast.makeText(context, "Finished Sending to Server", Toast.LENGTH_LONG).show();
    }
}
