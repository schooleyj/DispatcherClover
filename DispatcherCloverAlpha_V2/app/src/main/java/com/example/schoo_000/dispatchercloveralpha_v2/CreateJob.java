package com.example.schoo_000.dispatchercloveralpha_v2;

import android.accounts.Account;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.RemoteException;
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
import android.widget.Toast;
// imports for connecting to the server
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.v1.BindingException;
import com.clover.sdk.v1.ClientException;
import com.clover.sdk.v1.ServiceException;
import com.clover.sdk.v1.merchant.Merchant;
import com.clover.sdk.v1.merchant.MerchantConnector;

// for now have to do the account thing in the main activity
public class CreateJob extends AppCompatActivity
{
    private Account account;
    private MerchantConnector merchantConnector;

    private EditText jobNameEditText;
    private EditText descriptionEditText;
    private Button submitButton;

    private String merchantID;
    private String merchantAddress;
    private String merchantPhone;

    private String jobNameString = "";
    private String descriptionString = "";

    private String[] jobInfo = {jobNameString, descriptionString};
    private SharedPreferences savedValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_job);

        jobNameEditText = (EditText) findViewById(R.id.job_name);
        descriptionEditText = (EditText) findViewById(R.id.description);
        submitButton = (Button) findViewById(R.id.submit_button);

        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);

        boolean firstRun = savedValues.getBoolean("firstRun", true);
        if (firstRun) {
            startActivity(new Intent(this, CreateBusiness.class));
        }
    }

    public void onPause()
    {
        disconnect();
        Editor editor = savedValues.edit();
        editor.putString("jobNameString", jobNameString);
        editor.putString("descriptionString", descriptionString);
        editor.commit();
        super.onPause();
    }

    public void onResume()
    {
        super.onResume();

        if (account == null) {
            account = CloverAccount.getAccount(this);

            if (account == null) {
                Toast.makeText(this, getString(R.string.no_account),
                        Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }

        connect();

        getMerchant();

        jobNameString = savedValues.getString("jobNameString", "");
        jobNameEditText.setText(jobNameString);
        descriptionString = savedValues.getString("descriptionString", "");
        descriptionEditText.setText(descriptionString);
    }

    private void connect() {
        disconnect();
        if (account != null) {
            merchantConnector = new MerchantConnector(this, account, null);
            merchantConnector.connect();
        }
    }

    private void disconnect() {
        if (merchantConnector != null) {
            merchantConnector.disconnect();
            merchantConnector = null;
        }
    }

    private void getMerchant() {
        new AsyncTask<Void, Void, Merchant>() {
            @Override
            protected Merchant doInBackground(Void... params) {
                Merchant merchant = null;
                try {
                    merchant = merchantConnector.getMerchant();
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (ClientException e) {
                    e.printStackTrace();
                } catch (ServiceException e) {
                    e.printStackTrace();
                } catch (BindingException e) {
                    e.printStackTrace();
                }
                return merchant;
            }

            @Override
            protected void onPostExecute(Merchant merchant) {
                super.onPostExecute(merchant);

                if (!isFinishing()) {
                    merchantID = merchant.getId();
                    merchantAddress = merchant.getAddress().toString(); // see what this does
                    merchantPhone = merchant.getPhoneNumber();
                }
            }
        }.execute();
    }

    private void sendInfoToURL(String url) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("job_title", jobNameEditText.getText());
            jsonObject.accumulate("job_desc", descriptionEditText.getText());
            jsonObject.accumulate("merch_id", merchantID);
            jsonObject.accumulate("from_loc", merchantAddress); // not sure if this is right
            jsonObject.accumulate("to_loc", "<to_loc_here>");
            jsonObject.accumulate("bus_phone", merchantPhone);
        } catch (JSONException ex) {
            // this will never happen
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        Log.d("CreateJob", "Received response: " + response.toString());
                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d("CreateJob", "Error occurred: " + error.toString());
            }
        });
        RequestQueueSingleton.getInstance(this.getApplicationContext())
                .addToRequestQueue(jsonObjectRequest);
    }

    public void sendJobInfo(View v)
    {
        Log.d("CreateJob", "sendJobInfo clicked.");
        String createJobURL = "http://ec2-52-23-224-226.compute-1.amazonaws.com/dispatcher/create_job";
        sendInfoToURL(createJobURL);
    }

}
