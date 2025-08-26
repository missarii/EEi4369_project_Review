package com.s23001792.thiriposa;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class VoiceChatActivity extends AppCompatActivity {

    private Button btnRecord, btnStop;
    private MediaRecorder recorder;
    private MediaPlayer player;
    private String currentFileName;
    private ListView listVoiceNotes;
    private ArrayList<String> voiceNotesList;
    private ArrayAdapter<String> adapter;

    private static final int REQUEST_MIC_PERMISSION = 200;
    private int noteCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_chat);

        btnRecord = findViewById(R.id.btnRecord);
        btnStop = findViewById(R.id.btnStop);
        listVoiceNotes = findViewById(R.id.listVoiceNotes);

        btnStop.setEnabled(false);

        voiceNotesList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, voiceNotesList);
        listVoiceNotes.setAdapter(adapter);

        // Request microphone permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_MIC_PERMISSION);
        }

        btnRecord.setOnClickListener(v -> startRecording());
        btnStop.setOnClickListener(v -> stopRecording());

        // Play selected note
        listVoiceNotes.setOnItemClickListener((parent, view, position, id) -> {
            String filePath = getExternalFilesDir(null).getAbsolutePath() + "/" + voiceNotesList.get(position);
            playRecording(filePath);
        });
    }

    private void startRecording() {
        noteCounter++;
        currentFileName = "voice_note_" + noteCounter + ".3gp";

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(getExternalFilesDir(null).getAbsolutePath() + "/" + currentFileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
            recorder.start();
            btnRecord.setEnabled(false);
            btnStop.setEnabled(true);
            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;

        // Save note in list
        voiceNotesList.add(currentFileName);
        adapter.notifyDataSetChanged();

        btnStop.setEnabled(false);
        btnRecord.setEnabled(true);
        Toast.makeText(this, "Recording saved", Toast.LENGTH_SHORT).show();
    }

    private void playRecording(String filePath) {
        if (player != null) {
            player.release();
            player = null;
        }

        player = new MediaPlayer();
        try {
            player.setDataSource(filePath);
            player.prepare();
            player.start();
            Toast.makeText(this, "Playing recording", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_MIC_PERMISSION) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Microphone permission is required!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
