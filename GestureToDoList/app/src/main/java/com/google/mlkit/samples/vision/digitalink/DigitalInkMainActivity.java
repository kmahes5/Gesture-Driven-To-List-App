package com.google.mlkit.samples.vision.digitalink;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.VisibleForTesting;

import com.google.android.gms.tasks.OnSuccessListener;

/** Main activity which creates a StrokeManager and connects it to the DrawingView. */
public class DigitalInkMainActivity extends AppCompatActivity {
  private static final String TAG = "MLKDI.Activity";
  @VisibleForTesting final StrokeManager strokeManager = new StrokeManager();
  TextView textView1;
  Button save;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_digital_ink_main);

    DrawingView drawingView = findViewById(R.id.drawing_view);
    StatusTextView statusTextView = findViewById(R.id.status_text_view);
    drawingView.setStrokeManager(strokeManager);
    statusTextView.setStrokeManager(strokeManager);

    strokeManager.setStatusChangedListener(statusTextView);
    strokeManager.setContentChangedListener(drawingView);
    strokeManager.setClearCurrentInkAfterRecognition(true);
    strokeManager.setTriggerRecognitionAfterInput(false);

    strokeManager.refreshDownloadedModelsStatus();

    strokeManager.reset();

    String languageCode = "en-US";
    Log.i(TAG, "Selected language: " + languageCode);
    strokeManager.setActiveModel(languageCode);
    strokeManager.download();
  }

  public void recognizeClick(View v) {
    strokeManager.recognize().addOnSuccessListener(new OnSuccessListener<String>() {
      @Override
      public void onSuccess(String s) {
         textView1 = (TextView) findViewById(R.id.textview1);
         textView1.setText(s);
         textView1.setTextSize(15);
         save = (Button) findViewById(R.id.button);
         save.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
             // Put the String to pass back into an Intent and close this activity
             Intent intent = new Intent();
             intent.putExtra("result", textView1.getText());
             setResult(RESULT_OK, intent);
             finish();
           }
         });
      }
    });
  }

  public void clearClick(View v) {
    strokeManager.reset();
    DrawingView drawingView = findViewById(R.id.drawing_view);
    drawingView.clear();
  }
}
