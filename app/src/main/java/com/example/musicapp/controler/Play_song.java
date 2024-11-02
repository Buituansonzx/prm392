package com.example.musicapp.controler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.example.musicapp.R;
import com.example.musicapp.model.Song;

import java.io.IOException;

public class Play_song extends AppCompatActivity {

    private static final String TAG = "Play_song";
    private TextView titleTv, artistTv, currentTimeTv, totalTimeTv;
    private SeekBar seekBar;
    private ImageView pausePlay, nextBtn, previousBtn, musicIcon, btnBack;
    private Song song;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Runnable updateSeekBarRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_song);

        initializeViews();
        setupClickListeners();

        song = (Song) getIntent().getSerializableExtra("song");
        if (song == null) {
            Log.e(TAG, "No song data received");
            Toast.makeText(this, "Error: No song data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        updateSongInfo();
        setupMediaPlayer();
    }

    private void startUpdateSeekBarProgress() {
        if (updateSeekBarRunnable == null) {
            updateSeekBarRunnable = new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        int currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        currentTimeTv.setText(formatTime(currentPosition));
                        handler.postDelayed(this, 100);
                    }
                }
            };
        }
        handler.post(updateSeekBarRunnable);
    }

    private void stopUpdateSeekBarProgress() {
        if (updateSeekBarRunnable != null) {
            handler.removeCallbacks(updateSeekBarRunnable);
        }
    }

    private void initializeViews() {
        titleTv = findViewById(R.id.song_title);
        artistTv = findViewById(R.id.artist_name);
        currentTimeTv = findViewById(R.id.current_time);
        totalTimeTv = findViewById(R.id.total_time);
        seekBar = findViewById(R.id.seek_bar);
        pausePlay = findViewById(R.id.pause_play);
        nextBtn = findViewById(R.id.next);
        previousBtn = findViewById(R.id.previous);
        musicIcon = findViewById(R.id.music_icon_big);
        btnBack = findViewById(R.id.btn_back);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void updateSongInfo() {
        titleTv.setText(song.getTitle());
        artistTv.setText(song.getArtist());

        byte[] imageData = song.getImage();
        if (imageData != null && imageData.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
            roundedBitmapDrawable.setCircular(true);
            musicIcon.setImageDrawable(roundedBitmapDrawable);
        }
    }

    private void setupMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );

        try {
            mediaPlayer.setDataSource(song.getSongUrl());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                totalTimeTv.setText(formatTime(mediaPlayer.getDuration()));
                seekBar.setMax(mediaPlayer.getDuration());
                mediaPlayer.start();
                startUpdateSeekBarProgress();
            });
        } catch (IOException e) {
            Log.e(TAG, "Error setting data source", e);
            Toast.makeText(this, "Error playing song", Toast.LENGTH_SHORT).show();
        }

        mediaPlayer.setOnCompletionListener(mp -> {
            pausePlay.setImageResource(R.drawable.baseline_play_circle_outline_24);
            stopUpdateSeekBarProgress();
        });

        setupSeekBar();
        setupPlayPauseButton();
    }

    private void setupSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
                currentTimeTv.setText(formatTime(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupPlayPauseButton() {
        pausePlay.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                pausePlay.setImageResource(R.drawable.baseline_play_circle_outline_24);
                stopUpdateSeekBarProgress();
            } else {
                mediaPlayer.start();
                pausePlay.setImageResource(R.drawable.baseline_pause_circle_outline_24);
                startUpdateSeekBarProgress();
            }
        });
    }

    private String formatTime(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        stopUpdateSeekBarProgress();
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);
    }
}