package com.example.recoderaudio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

   private static final int REQUEST_CODE_STORAGE = 113;

   private FrameLayout stopContainer;
   private ImageView imgPlay;
   private TextView txtDuration,txtRecorderStatus;
   private LottieAnimationView lavRecorder;

   private SimpleDateFormat simpleDateFormat;
   private Handler handler = new Handler();
   private MediaRecorder recorder;
   private String fileName;
   private boolean isRecording = false;
   private long timeRecording = 0;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      initView();
      initData();
   }

   @SuppressLint("SimpleDateFormat")
   private void initData() {
      fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + System.currentTimeMillis() + ".mp3";

      simpleDateFormat = new SimpleDateFormat("mm:ss");
   }

   private void initView() {
      imgPlay           = findViewById(R.id.imgPlay);
      txtDuration       = findViewById(R.id.txtDuration);
      lavRecorder       = findViewById(R.id.lavRecorder);
      stopContainer     = findViewById(R.id.stopContainer);
      txtRecorderStatus = findViewById(R.id.txtRecorderStatus);
   }

   public void onClickPlayRecord(View view) {
      if (Build.VERSION.SDK_INT >= 23) {
         if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED | checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_STORAGE);
            return;
         }
      }

      startRecording();
   }

   private void startRecording(){
      timeRecording = 0;

      imgPlay.setVisibility(View.GONE);
      stopContainer.setVisibility(View.VISIBLE);

      txtRecorderStatus.setText("Tap again to stop");
      txtDuration.setText("Duration: 00:00");

      recorder = new MediaRecorder();
      recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
      recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
      recorder.setOutputFile(fileName);
      recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

      try {
         recorder.prepare();
      } catch (IOException e) {
         Log.d("RecordError",e.toString());
      }

      recorder.start();
      speakingListener.run();
      handler.postDelayed(durationRecording,1000);
   }

   public void onClickStopRecording(View view) {
      imgPlay.setVisibility(View.VISIBLE);
      stopContainer.setVisibility(View.GONE);

      txtRecorderStatus.setText("Tap button to record");
      txtDuration.setText("");

      handler.removeCallbacks(speakingListener);
      handler.removeCallbacks(durationRecording);
      recorder.stop();
      recorder.release();
      recorder = null;

      Intent intent = new Intent(this,UploadActivity.class);
      intent.putExtra("file",fileName);
      startActivity(intent);
   }

   @Override
   public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
         startRecording();
      }
   }

   private Runnable durationRecording = new Runnable() {
      @SuppressLint("SetTextI18n")
      @Override
      public void run() {
         timeRecording += 1000;

         txtDuration.setText("Duration: " + simpleDateFormat.format(timeRecording));

         handler.postDelayed(this,1000);
      }
   };

   private Runnable speakingListener = new Runnable() {
      @Override
      public void run() {
         if (recorder.getMaxAmplitude() > 5000) {
            Log.d("xxxxxxxxxxxxxx1","2");
            if (!lavRecorder.isAnimating()) {
               Log.d("xxxxxxxxxxxxxx1","3");
               lavRecorder.playAnimation();
            }
         }else {
            Log.d("xxxxxxxxxxxxxx1","4");
            if (lavRecorder.isAnimating()){
               Log.d("xxxxxxxxxxxxxx1","5");

            }
         }
         handler.postDelayed(this,10);
      }
   };
}
