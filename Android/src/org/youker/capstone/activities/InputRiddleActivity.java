package org.youker.capstone.activities;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.youker.capstone.R;
import org.youker.capstone.fragments.GoogleMapFragment;
import org.youker.capstone.models.Riddle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Mitch Youker on 11/25/2017.
 */

public class InputRiddleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_riddle_input);

        final String latitude = getIntent().getStringExtra("latitude");
        final String longitude = getIntent().getStringExtra("longitude");
        final String answer = getIntent().getStringExtra("answer");

        final EditText riddle_input = findViewById(R.id.input_riddle);
        final Button submit = findViewById(R.id.submit_input_riddle);
        final TextView answerView = findViewById(R.id.input_riddle_answer);

        answerView.setText(answer);

        submit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String riddle_text = riddle_input.getText().toString();
                if (riddle_text.length() > 15) {
                    new CreateRiddleTask().execute(riddle_text, answer, latitude, longitude);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "You must enter a riddle for this object that is greater than 15 characters.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }


    public class CreateRiddleTask extends AsyncTask<String, Boolean, Boolean> {

        private String GET_RIDDLES_URL = "http://67.249.252.255/createRiddle.php";

        @Override
        protected void onPostExecute(Boolean content) {
            finish();
        }


        @Override
        protected Boolean doInBackground(String[] args) {
            sendRequest(args[0], args[1], args[2], args[3]);
            return true;
        }

        @Override
        protected void onPreExecute() {}

        private void sendRequest(String riddle_text, String riddle_answer, String latitude, String longitude) {
            try {
                URL url = new URL(GET_RIDDLES_URL + "?riddle_lat=" + URLEncoder.encode(latitude, "UTF-8") + "&riddle_long=" + URLEncoder.encode(longitude, "UTF-8") + "&riddle_answer=" + URLEncoder.encode(riddle_answer, "UTF-8") + "&riddle_text=" + URLEncoder.encode(riddle_text, "UTF-8"));
                url.openStream();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
