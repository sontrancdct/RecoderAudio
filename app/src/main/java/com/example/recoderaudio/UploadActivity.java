package com.example.recoderaudio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import static android.graphics.Typeface.BOLD;

public class UploadActivity extends AppCompatActivity {

   private static final String FOLDER_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/VietDung/";

   private TextView txtRecordAgain,txtFilePath;
   private Spinner spinner;
   private RadioGroup radioGroup;
   private String language = "ja-JP";
   private String filePath,newFilePath;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_upload);

      initView();
      initIntentData();
      initData();
      initEvents();
   }

   private void initIntentData() {
      filePath = getIntent().getStringExtra("file");
   }

   private void initEvents() {
      radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
               case R.id.rbJapanese:
                  language = "ja-JP";
                  break;
               case R.id.rbEnglish:
                  language = "en-US";
                  break;
            }
         }
      });
   }

   private void initData() {
      txtRecordAgain.setPaintFlags(txtRecordAgain.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

      ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(
         this, R.array.spinner, R.layout.row_spinner);

      spinner.setAdapter(arrayAdapter);
   }

   private void initView() {
      spinner        = findViewById(R.id.spinner);
      radioGroup     = findViewById(R.id.radioGroup);
      txtFilePath    = findViewById(R.id.txtFilePath);
      txtRecordAgain = findViewById(R.id.txtRecordAgain);
   }

   public void onClickUpload(View view) {
      if (TextUtils.isEmpty(language)){
         Toast.makeText(this, "Vui lòng chọn ngôn ngữ", Toast.LENGTH_SHORT).show();
         return;
      }

      int number = Integer.valueOf(spinner.getSelectedItem().toString().replace(" ",""));

      boolean status;
      if (number == 1){
         status = false;
      }else {
         status = true;
      }

      File file = new File(filePath);

      File folder = new File(FOLDER_PATH);

      if (!folder.exists()){
         folder.mkdirs();
      }

      newFilePath = FOLDER_PATH + System.currentTimeMillis() + "_" + language + "_" + status + "_" + number + ".mp3";

      File newFile = new File(newFilePath);

      if (rename(file,newFile)){
         Toast.makeText(this, "Đổi đường dẫn file thành công", Toast.LENGTH_SHORT).show();

         SpannableString spannableString = new SpannableString("Đường dẫn: " + newFilePath);
         spannableString.setSpan(BOLD,0,10,0);
         spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
               File fileAudio = new File(newFilePath);

               Intent intent = new Intent();
               intent.setAction(android.content.Intent.ACTION_VIEW);
               if (Build.VERSION.SDK_INT > 23){
                  Uri uri = FileProvider.getUriForFile(UploadActivity.this, getPackageName() + ".provider",fileAudio );

                  intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                  intent.setDataAndType(uri, "audio/*");
               }else {
                  intent.setDataAndType(Uri.fromFile(fileAudio), "audio/*");
               }

               startActivity(intent);
            }
         },11,spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
         txtFilePath.setText(spannableString);
         txtFilePath.setMovementMethod(LinkMovementMethod.getInstance());
      }else {
         Toast.makeText(this, "Đổi đường dẫn file thất bại", Toast.LENGTH_SHORT).show();
      }
   }

   private boolean rename(File from, File to) {
      return from.getParentFile().exists() && from.exists() && from.renameTo(to);
   }

   public void onClickRecordAgain(View view) {
      finish();
   }
}
