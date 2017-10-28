package com.example.schoo_000.dispatchalpha_v1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Button;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MainActivity extends AppCompatActivity
implements OnClickListener{

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

        submitButton.setOnClickListener(this);

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

    public void onClick(View v)
    {
        sendJobInfo();
    }


    public String sendJobInfo()
    {
        return jobNameString;
    }

    //public String[] sendJobInfo()
   // {
    //    return jobInfo;
    //}



}
