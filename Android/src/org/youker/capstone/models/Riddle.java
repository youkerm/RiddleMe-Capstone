package org.youker.capstone.models;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;
import org.youker.capstone.R;

/**
 * Created by Mitch Youker on 11/16/2017.
 */

public class Riddle {

    private String id;
    private String question;
    private String answer;
    private LatLng location;
    private Marker marker;
    private String created;

    private static int MAX_TITLE_LENGTH = 100;

    public Riddle(JSONObject json) {
        try {
            id = json.getString("id");
            question = json.getString("riddle");
            answer = json.getString("answer");
            location = new LatLng( Double.parseDouble(json.getString("lat")),
                    Double.parseDouble(json.getString("long")) );
            created = json.getString("created");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public LatLng getLocation() {
        return location;
    }

    public String getCreated() {
        return created;
    }

    public void createMarker(GoogleMap map) {
        MarkerOptions markerOptions = new MarkerOptions().position(location);

        if (getQuestion() != null) {
            String question = getQuestion();
            if (question.length() > MAX_TITLE_LENGTH) {
                question = question.substring(0, (MAX_TITLE_LENGTH - 3)) + "...";
            }
            question = Character.toUpperCase(question.charAt(0)) + question.substring(1, question.length()); //Forces all alpha character to uppercase for first char

            markerOptions.title(question);
        }

        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.camera_marker));
        marker = map.addMarker(markerOptions);
        marker.setTag(id); // Need for click event
    }

    public void destroy() {
        //remove maker from google map
        if (marker != null) {
            marker.remove();
        }

        //free memory (waits for GC)
        question = null;
        answer = null;
        location = null;
        marker = null;
        created = null;
    }
}
