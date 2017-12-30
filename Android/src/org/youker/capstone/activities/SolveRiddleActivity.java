package org.youker.capstone.activities;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.TextView;

import org.tensorflow.classifier.Classifier;
import org.tensorflow.classifier.ClassifierActivity;
import org.youker.capstone.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Mitch Youker on 11/25/2017.
 */

public class SolveRiddleActivity extends ClassifierActivity {

    private TextView riddleView;
    private String riddle_id;
    private String riddle_text;
    private String riddle_answer;
    private long startTime = -1;


    @Override
    public void onPreviewSizeChosen(final Size size, final int rotation) {
        super.onPreviewSizeChosen(size, rotation);

        // Has to be setup after layout is created! (Can't be move to onCreate method)
        riddleView = findViewById(R.id.riddle);
        riddleView.setVisibility(View.VISIBLE);

        // Get data from previous activity
        riddle_id = getIntent().getStringExtra("riddle_id");
        riddle_text = getIntent().getStringExtra("riddle_text");
        riddle_answer = getIntent().getStringExtra("riddle_answer");

        setRiddleView(riddle_text);
    }

    @Override
    protected void processImage() {
        super.processImage();

        for (int i = 0; i < getResults().size(); i++) {
            Classifier.Recognition result = getResults().get(i);
            if (result.getTitle().equalsIgnoreCase(riddle_answer)) {
                if (startTime == -1) {
                    startTime = SystemClock.uptimeMillis();
                } else {
                    if ((SystemClock.uptimeMillis() - startTime) >= 3000) { //The object has been detected for 3 seconds
                        new SolveRiddleTask().execute(riddle_id, riddle_answer);
                    }
                }
            }
        }
    }

    public void setRiddleView(String text) {
        riddleView = findViewById(R.id.riddle);
        if (riddleView != null) {
            riddleView.setText(text);
        }
    }

    public class SolveRiddleTask extends AsyncTask<String, Boolean, Boolean> {

        private String GET_SOLVE_URL = "http://67.249.252.255/solveRiddle.php";

        @Override
        protected void onPostExecute(Boolean content) {
            finish();
        }


        @Override
        protected Boolean doInBackground(String[] args) {
            sendRequest(args[0], args[1]);
            return true;
        }

        @Override
        protected void onPreExecute() {}

        private void sendRequest(String riddle_id, String answer) {
            try {
                URL url = new URL(GET_SOLVE_URL + "?riddle_id=" + URLEncoder.encode(riddle_id, "UTF-8") + "&answer=" + URLEncoder.encode(answer, "UTF-8"));
                url.openStream();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
