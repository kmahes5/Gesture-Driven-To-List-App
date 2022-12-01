package com.google.mlkit.samples.vision.digitalink;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class HelpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.help_activity);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * 0.9), (int) (height * 0.35));
    }
}
