package com.google.mlkit.samples.vision.digitalink;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;

import android.gesture.Prediction;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements OnGesturePerformedListener {
    FloatingActionButton helpButton;
    private GestureLibrary objGestureLib;
    private ListView languageLV;
    ArrayList<String> toDoList;
    ArrayList<Boolean> isDoneToDoList;
    String gestureName = null;
    ArrayAdapter<String> adapter = null;
    Toast curToast;

    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;


    enum NUI {UNDO, CHECK, CROSS, ADD}

    NUI mode = null;
    NUI pMode = null;

    String lastActionText = "";
    int lastActionPos = -1;
    Boolean lastActionStatus = Boolean.FALSE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helpButton = findViewById(R.id.help_fab);
        helpButton.setOnClickListener(view -> {
                startActivity(new Intent (MainActivity.this, HelpActivity.class));
        });

        languageLV = findViewById(R.id.idLVLanguages);

        curToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        toDoList = getStringArrayList();
        isDoneToDoList = getBoolArrayList();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, toDoList);
        languageLV.setAdapter(adapter);

        objGestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if(!objGestureLib.load()){
            finish();
        }

        GestureOverlayView objGestureOverlay = (GestureOverlayView) findViewById(R.id.WidgetGesture);
        objGestureOverlay.addOnGesturePerformedListener(this);

        languageLV.setOnItemClickListener((parent, view, position, id) -> {
            if ((mode == NUI.CHECK) && (!isDoneToDoList.get(position))){
                lastActionText = toDoList.get(position);
                lastActionPos = position;
                lastActionStatus = isDoneToDoList.get(position);

                toDoList.set(position, toDoList.get(position) + "- COMPLETED!");
                isDoneToDoList.set(position, Boolean.TRUE);
                adapter.notifyDataSetChanged();

                pMode = mode;
            } else if (mode == NUI.CROSS) {
                lastActionText = toDoList.get(position);
                lastActionPos = position;
                lastActionStatus = isDoneToDoList.get(position);

                toDoList.remove(position);
                isDoneToDoList.remove(position);
                adapter.notifyDataSetChanged();

                pMode = mode;
            }
            mode = null;
        });


    }

    @Override
    public void onGesturePerformed(GestureOverlayView gestureOverlayView, Gesture gesture) {
        String text = "";
        ArrayList<Prediction> objPrediction = objGestureLib.recognize(gesture);
        if(objPrediction.size() > 0 && objPrediction.get(0).score > 1) {
            gestureName = objPrediction.get(0).name;
        }
        if (gestureName.contains("checkmark")){
            mode = NUI.CHECK;
            text = "Select item to check off";
        } else if (gestureName.contains("cross")) {
            mode = NUI.CROSS;
            text = "Select item to delete";
        } else if (gestureName.contains("square")) {
            mode = NUI.ADD;
            Intent intent = new Intent(this, DigitalInkMainActivity.class);
            startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
        } else if (gestureName.contains("undo")) {
            if (pMode == NUI.UNDO) {
                mode = NUI.UNDO;
                text = "Can't undo twice in a row";
            } else if (pMode == null) {
                text = "Nothing to undo";
            } else {
                undoAction();
                mode = NUI.UNDO;
                pMode = mode;
                text = "Previous Action Undone";
            }
        }
        if (text != "") {
            curToast.cancel();
            curToast = Toast.makeText(this, text, Toast.LENGTH_LONG);
            curToast.show();
        }
        gestureName = null;

    }

    private void undoAction(){
        if (pMode == NUI.CHECK) {
            toDoList.set(lastActionPos, lastActionText);
            isDoneToDoList.set(lastActionPos, Boolean.FALSE);
        } else if (pMode == NUI.CROSS) {
            toDoList.add(lastActionPos, lastActionText);
            isDoneToDoList.add(lastActionPos, lastActionStatus);
        } else if (pMode == NUI.ADD) {
            toDoList.remove(toDoList.size()-1);
            isDoneToDoList.remove(isDoneToDoList.size()-1);
        }

        adapter.notifyDataSetChanged();
    }

    // This method is called when the second activity finishes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);

                // Check that it is the SecondActivity with an OK result
                if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
                    if (resultCode == RESULT_OK) {

                // Get String data from Intent
                String returnString = data.getStringExtra("result");

                toDoList.add(returnString);
                isDoneToDoList.add(Boolean.FALSE);
                adapter.notifyDataSetChanged();
                pMode = mode;
            }
        }
    }

    private ArrayList<String> getStringArrayList() {
        ArrayList<String> temp = new ArrayList<>();
        for(int i = 0;i<10;i++) {
            temp.add("TODO #" + String.valueOf(i));
        }
        return temp;
    }

    private ArrayList<Boolean> getBoolArrayList() {
        ArrayList<Boolean> temp = new ArrayList<>();
        for(int i = 0;i<10;i++) {
            temp.add(Boolean.FALSE);
        }
        return temp;
    }
}