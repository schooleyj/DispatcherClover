package com.example.schoo_000.dispatchalpha_v1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Button;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
// imports for connecting to the server
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

public class MainActivity extends AppCompatActivity
{

    private EditText jobNameEditText;
    private EditText descriptionEditText;
    private Button submitButton;

    private String jobNameString = "";
    private String descriptionString = "";

    private String[] jobInfo = {jobNameString, descriptionString};
    private SharedPreferences savedValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jobNameEditText = (EditText) findViewById(R.id.job_name);
        descriptionEditText = (EditText) findViewById(R.id.description);
        submitButton = (Button) findViewById(R.id.submit_button);

        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);
    }

    public void onPause()
    {
        Editor editor = savedValues.edit();
        editor.putString("jobNameString", jobNameString);
        editor.putString("descriptionString", descriptionString);
        editor.commit();
        super.onPause();
    }

    public void onResume()
    {
        super.onResume();

        jobNameString = savedValues.getString("jobNameString", "");
        jobNameEditText.setText(jobNameString);
        descriptionString = savedValues.getString("descriptionString", "");
        descriptionEditText.setText(descriptionString);
    }
        //Test Comment


    private void sendInfoToURL(String url) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("jobTitle", jobNameEditText.getText());
        } catch (JSONException ex) {
            // this will never happen
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        Log.d("MainActivity", "Received response: " + response.toString());
                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d("MainActivity", "Error occurred: " + error.toString());
            }
        });
        RequestQueueSingleton.getInstance(this.getApplicationContext())
                .addToRequestQueue(jsonObjectRequest);
    }

    public void sendJobInfo(View v)
    {
        Log.d("MainActivity", "sendJobInfo clicked.");
        String createJobURL = "http://ec2-52-23-224-226.compute-1.amazonaws.com/dispatcher/create_job";
        sendInfoToURL(createJobURL);
    }

}
