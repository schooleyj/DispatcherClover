package com.example.schoo_000.dispatchalpha_v1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.content.SharedPreferences.Editor;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class CreateBusiness extends AppCompatActivity
    {
    private EditText phoneEditText;
    private EditText addressEditText;
    private EditText emailEditText;
    private EditText nameEditText;

    private BusinessInfo businessInfo;

    private SharedPreferences savedValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_business);

        phoneEditText = (EditText) findViewById(R.id.phone);
        addressEditText = (EditText) findViewById(R.id.address);
        emailEditText = (EditText) findViewById(R.id.email);
        nameEditText = (EditText)findViewById(R.id.businessName);

        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);
    }

    private void retrieveBusinessInfo() {
        businessInfo = new BusinessInfo();
        businessInfo.setAddress(savedValues.getString("addressString", ""));
        addressEditText.setText(businessInfo.getAddress());

        businessInfo.setEmail(savedValues.getString("emailString", ""));
        emailEditText.setText(businessInfo.getEmail());

        businessInfo.setName(savedValues.getString("nameString", ""));
        nameEditText.setText(businessInfo.getName());

        businessInfo.setPhoneNumber(savedValues.getString("phoneString", ""));
        phoneEditText.setText(businessInfo.getPhoneNumber());
    }

    private void updateBusinessInfo() {
        Editor editor = savedValues.edit();

        businessInfo.setPhoneNumber(phoneEditText.getText().toString());
        editor.putString("phoneString", businessInfo.getPhoneNumber());

        businessInfo.setName(nameEditText.getText().toString());
        editor.putString("nameString", businessInfo.getName());

        businessInfo.setAddress(addressEditText.getText().toString());
        editor.putString("addressString", businessInfo.getAddress());

        businessInfo.setEmail(emailEditText.getText().toString());
        editor.putString("emailString", businessInfo.getEmail());

        editor.commit();
    }

    public void onPause()
    {
        updateBusinessInfo();
        super.onPause();
    }

    public void onResume()
    {
        super.onResume();

        retrieveBusinessInfo();
    }

    public void registerBusiness(View v) {
        updateBusinessInfo();

        String url = "http://ec2-52-23-224-226.compute-1.amazonaws.com/dispatcher/register_business";
        registerBusinessToURL(url);
    }

    public void registerBusinessToURL(String url) {
        JSONObject object = businessInfo.getJSON();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("CreateBusiness", "Received response: " + response.toString());
                startActivity(new Intent(CreateBusiness.this, MainActivity.class));
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d("CreateBusiness", "Error occurred: " + error.toString());
            }
        });
        RequestQueueSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}
