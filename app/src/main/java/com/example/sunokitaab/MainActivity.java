package com.example.sunokitaab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    Button rb, pb, sb, srb, deleteButton;
    String path = "";
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    File file;

    final int REQUEST_PERMISSION_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = findViewById(R.id.Play);
        rb = findViewById(R.id.record);
        sb = findViewById(R.id.stop);
        srb = findViewById(R.id.stoprecord);
        deleteButton = findViewById(R.id.del);
        rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissionFromDevice()) {
                    Toast.makeText(MainActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                    path = Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/"
                            + UUID.randomUUID().toString()+"_myrecording.mp3" /*+ "_audio_record.3gp"*/;

                    setupMediaRecorder();
                    try {
                        mediaRecorder.prepare();
                        Log.i("Prepared", "prepared");
                        mediaRecorder.start();
                        Log.i("Started:", "started");
                        Toast.makeText(MainActivity.this, "Recording...", Toast.LENGTH_SHORT).show();
                        srb.setEnabled(true);
                        pb.setEnabled(false);
                        sb.setEnabled(false);
                    } catch (Exception e) {
                        Log.i("There is error in rec:", e.toString());
                    }

                } else {
                    requestPermission();
                }
            }
        });

        srb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mediaRecorder.stop();
                } catch (Exception e) {
                    file.delete();
                    Toast.makeText(MainActivity.this, "mediaPlayer did not stop", Toast.LENGTH_SHORT).show();
                } finally {
                    mediaRecorder.release();
                    mediaRecorder = null;
                }
                srb.setEnabled(false);
                pb.setEnabled(true);
                rb.setEnabled(true);
                sb.setEnabled(false);
            }
        });

        pb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sb.setEnabled(true);
                srb.setEnabled(false);
                rb.setEnabled(false);
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(path);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    Toast.makeText(MainActivity.this, "Playing...", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Log.i("There is error in play:",e.toString());
                } finally {
                    file.delete();
                }

            }
        });

        sb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sb.setEnabled(false);
                rb.setEnabled(true);
                sb.setEnabled(false);
                pb.setEnabled(true);

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    setupMediaRecorder();
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                file.delete();
            }
        });

    }

    /*@Override
    protected void onStop() {
        super.onStop();
        if(mediaPlayer!=null) {
            mediaPlayer.release();
        }
        if(mediaRecorder!=null){
            mediaRecorder.release();
        }
        file.delete();
        Toast.makeText(this, "deleted", Toast.LENGTH_SHORT).show();
    }*/

    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        file=new File(path);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(path);
        TextView textView = findViewById(R.id.textview);
        textView.setText(path);

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED && record_audio_result == PackageManager.PERMISSION_GRANTED;

    }
}
