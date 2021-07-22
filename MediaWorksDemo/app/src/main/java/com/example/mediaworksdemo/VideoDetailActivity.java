package com.example.mediaworksdemo;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class VideoDetailActivity extends Activity {
    private VideoView videoView;

    String mockUrl = "https://stream7.iqilu.com/10339/upload_transcode/202002/18/20200218114723HDu3hhxqIT.mp4";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        MediaController mediaController = new MediaController(this);

        videoView = findViewById(R.id.vv_detail);
        videoView.setVideoURI(Uri.parse(mockUrl));
        videoView.setMediaController(mediaController);
        mediaController.setMediaPlayer(videoView);
        videoView.start();
        if (savedInstanceState != null) {
            Toast.makeText(this, "restore", Toast.LENGTH_SHORT).show();
            int time = savedInstanceState.getInt("time");
            Log.d("Save", "time "+ time + "  duration: " + videoView.getDuration());
            videoView.seekTo(time);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        int second = outState.getInt("time");
        videoView.seekTo(second);
        super.onRestoreInstanceState(outState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int second = videoView.getCurrentPosition();
        Log.d("Save", "second: "+ second + "  duration: " + videoView.getDuration());
        outState.putInt("time", second);
        super.onSaveInstanceState(outState);
    }
}
