package com.example.maximilian.recordandstream;

import android.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    static String ip;
    static String Port;
    static boolean recording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Called when the user taps the Stop button.
     */
    public void stopRecording(View view) {


        TextView textViewStatus = (TextView) findViewById(R.id.textViewStatus);
        textViewStatus.setText("Status: Idle");
        recording = false;
    }

    /**
     * Called when the user taps the Record button.
     */
    public void recordAudio(View view) {
        EditText editTextIP = (EditText) findViewById(R.id.editTextIP);
        EditText editTextPort = (EditText) findViewById(R.id.editTextPort);
        ip = editTextIP.getText().toString();
        Port = editTextPort.getText().toString();

        // If IP or Port has not been set, show an alert and return
        if (ip.isEmpty() || Port.isEmpty()) {
            DialogFragment alert1 = new NoIPorPortAlert();
            alert1.show(getFragmentManager(), "NoIPorPortAlert");
        } else if (recording) {
            // Show an alert if already recording
            DialogFragment alert2 = new AlreadyRecordingAlert();
            alert2.show(getFragmentManager(), "recording");
        } else {
            // Record
            recording = true;
            TextView textViewStatus = (TextView) findViewById(R.id.textViewStatus);
            textViewStatus.setText("Status: Recording");
        }
    }
}
