package com.example.maximilian.recordandstream;

import android.app.DialogFragment;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    private String ip;
    private String portStr;
    private boolean recording = false;

    private int port;

    AudioRecord recorder;

    private int sampleRate = 44100;
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Called when the user taps the Stop button.
     */
    public void stopRecording(View view) {
        recorder.release();
        Log.d("VS", "Recording stopped.");

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
        portStr = editTextPort.getText().toString();

        // If IP or portStr has not been set, show an alert and return
        if (ip.isEmpty() || portStr.isEmpty()) {
            DialogFragment alert1 = new NoIPorPortAlert();
            alert1.show(getFragmentManager(), "NoIPorPortAlert");
        } else if (recording) {
            // Show an alert if already recording
            DialogFragment alert2 = new AlreadyRecordingAlert();
            alert2.show(getFragmentManager(), "recording");
        } else {
            // Record and stream
            startStreaming();
        }
    }

    public void startStreaming() {
        recording = true;
        TextView textViewStatus = (TextView) findViewById(R.id.textViewStatus);
        textViewStatus.setText("Status: Recording");

        Thread streamThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    DatagramSocket socket = new DatagramSocket();
                    Log.d("VS", "Socket Created");

                    byte[] buffer = new byte[minBufSize];

                    Log.d("VS", "Buffer created of size " + minBufSize);
                    DatagramPacket packet;

                    final InetAddress destination = InetAddress.getByName(ip);
                    Log.d("VS", "IP retrieved: " + destination.toString());

                    recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, minBufSize * 10);
                    Log.d("VS", "Recorder initialized");

                    recorder.startRecording();

                    while (recording) {
                        //reading data from MIC into buffer
                        minBufSize = recorder.read(buffer, 0, buffer.length);

                        //putting buffer in the packet
                        port = Integer.valueOf(portStr);
                        Log.d("VS", "Port retrieved: " + portStr);
                        packet = new DatagramPacket(buffer, buffer.length, destination, port);

                        socket.send(packet);
                        System.out.println("MinBufferSize: " + minBufSize);
                    }

                } catch (UnknownHostException e) {
                    Log.e("VS", "UnknownHostException");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("VS", "IOException");
                }
            }
        });
        streamThread.start();
    }
}
