package org.youker.capstone.activities;

import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.tensorflow.classifier.Classifier;
import org.tensorflow.classifier.ClassifierActivity;
import org.youker.capstone.R;
import org.youker.capstone.fragments.GoogleMapFragment;

import java.util.List;

/**
 * Created by Mitch Youker on 11/25/2017.
 */

public class CreateRiddleActivity extends ClassifierActivity {

    private Button camera_capture;

    @Override
    public void onPreviewSizeChosen(final Size size, final int rotation) {
        super.onPreviewSizeChosen(size, rotation);

        final String latitude = getIntent().getStringExtra("latitude");
        final String longitude = getIntent().getStringExtra("longitude");

        // Has to be setup after layout is created! (Can't be move to onCreate method)
        camera_capture = findViewById(R.id.capture);
        camera_capture.setVisibility(View.VISIBLE);

        camera_capture.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Classifier.Recognition> results = getResults();

                if (results.size() > 0) {
                    Classifier.Recognition mostConfidentResult = results.get(0);

                    if (mostConfidentResult.getConfidence() <= 0.20) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Sorry, you must pick an object with 20% or greater confidence.", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        //Create riddle
                        String answer = mostConfidentResult.getTitle();

                        Intent intent = new Intent(getApplicationContext(), InputRiddleActivity.class);
                        intent.putExtra("latitude", latitude);
                        intent.putExtra("longitude", longitude);
                        intent.putExtra("answer", answer);
                        startActivity(intent);
                    }
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "No objects are detected.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }



}
