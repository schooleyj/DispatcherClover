package com.example.schoo_000.dispatchalpha_v1;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.content.SharedPreferences.Editor;

public class CreateBusiness extends AppCompatActivity
    implements OnClickListener{



    private EditText phoneEditText;
    private EditText addressEditText;
    private EditText emailEditText;
    private Button submitBusinessButton;

    private String phoneString;
    private String addressString;
    private String emailString;

    private String[] businessInfo = {phoneString, addressString,emailString};
    private SharedPreferences savedValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_business);

        phoneEditText = (EditText) findViewById(R.id.phone);
        addressEditText = (EditText) findViewById(R.id.address);
        emailEditText = (EditText) findViewById(R.id.email);
        submitBusinessButton = (Button) findViewById(R.id.submit_business);

        submitBusinessButton.setOnClickListener(this);

        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);
    }

    public void onPause()
    {
        Editor editor = savedValues.edit();
        editor.putString("phoneString", "");
        editor.putString("addressString", "");
        editor.putString("emailString", "");
        editor.commit();
        super.onPause();
    }

    public void onResume()
    {
        super.onResume();

        phoneString = savedValues.getString("phoneString", "");
        phoneEditText.setText(phoneString);
        addressString = savedValues.getString("addressString", "");
        addressEditText.setText(addressString);
        emailString = savedValues.getString("emailString", "");
        emailEditText.setText(emailString);
    }

    public void onClick(View v)
    {
        sendBusinessInfo();
    }

    public String[] sendBusinessInfo()
    {
        return businessInfo;
    }

    public String getAddress()
    {
        return addressString;
    }

    public String getPhone()
    {
        return phoneString;
    }

    public String getEmail()
    {
        return emailString;
    }
}
