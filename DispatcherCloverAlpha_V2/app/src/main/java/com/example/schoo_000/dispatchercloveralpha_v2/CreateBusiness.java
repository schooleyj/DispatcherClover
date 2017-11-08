package com.example.schoo_000.dispatchercloveralpha_v2;

import android.accounts.Account;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.v1.BindingException;
import com.clover.sdk.v1.ClientException;
import com.clover.sdk.v1.ServiceException;
import com.clover.sdk.v1.merchant.Merchant;
import com.clover.sdk.v1.merchant.MerchantAddress;
import com.clover.sdk.v1.merchant.MerchantConnector;

import org.json.JSONObject;

public class CreateBusiness extends AppCompatActivity
{
    private String merchantID;
    private String merchantAddress;
    private String merchantPhone;
    private String merchantWebsite;
    private String merchantName;
    private Account account;
    private MerchantConnector merchantConnector;
    private EditText phoneEditText;
    private EditText addressEditText;
    private EditText websiteEditText;
    private EditText nameEditText;

    private BusinessInfo businessInfo;

    private SharedPreferences savedValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_business);

        phoneEditText = (EditText) findViewById(R.id.phone);
        addressEditText = (EditText) findViewById(R.id.address);
        websiteEditText = (EditText) findViewById(R.id.website);
        nameEditText = (EditText)findViewById(R.id.businessName);

        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);
    }

    private void retrieveBusinessInfo() {
        businessInfo = new BusinessInfo();

        if (merchantID != null) {
            businessInfo.setId(merchantID);
        }

        if (merchantAddress != null) {
            Log.d("CreateBusiness", "Setting address from Clover SDK");
            businessInfo.setAddress(merchantAddress);
        } else {
            businessInfo.setAddress(savedValues.getString("addressString", ""));
        }
        addressEditText.setText(businessInfo.getAddress());

        if (merchantWebsite != null) {
            Log.d("CreateBusiness", "Setting website from Clover SDK");
            businessInfo.setWebsite(merchantWebsite);
        } else {
            businessInfo.setWebsite(savedValues.getString("websiteString", ""));
        }
        websiteEditText.setText(businessInfo.getWebsite());

        if (merchantName != null) {
            Log.d("CreateBusiness", "Setting name from Clover SDK");
            businessInfo.setName(merchantName);
        } else {
            businessInfo.setName(savedValues.getString("nameString", ""));
        }
        nameEditText.setText(businessInfo.getName());

        if (merchantPhone != null) {
            Log.d("CreateBusiness", "Setting phone from Clover SDK");
            businessInfo.setPhoneNumber(merchantPhone);
        } else {
            businessInfo.setPhoneNumber(savedValues.getString("phoneString", ""));
        }
        phoneEditText.setText(businessInfo.getPhoneNumber());
    }

    private void updateBusinessInfo() {
        Editor editor = savedValues.edit();

        editor.putString("merchantID", businessInfo.getId());

        businessInfo.setPhoneNumber(phoneEditText.getText().toString());
        editor.putString("phoneString", businessInfo.getPhoneNumber());

        businessInfo.setName(nameEditText.getText().toString());
        editor.putString("nameString", businessInfo.getName());

        businessInfo.setAddress(addressEditText.getText().toString());
        editor.putString("addressString", businessInfo.getAddress());

        businessInfo.setWebsite(websiteEditText.getText().toString());
        editor.putString("websiteString", businessInfo.getWebsite());

        editor.commit();
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
                    MerchantAddress address = merchant.getAddress();
                    // we're in North America for now
                    merchantAddress = address.getAddress1() + " " + address.getCity() + ", " +
                        address.getState() + " " + address.getZip();
                    merchantPhone = merchant.getPhoneNumber();
                    merchantName = merchant.getName();
                    merchantWebsite = merchant.getWebsite();
                    Log.d("CreateBusiness", "MerchantID = " + merchantID);
                    Log.d("CreateBusiness", "MerchantAddress = " + merchantAddress);
                    Log.d("CreateBusiness", "MerchantPhone = " + merchantPhone);
                    Log.d("CreateBusiness", "MerchantName = " + merchantName);
                    Log.d("CreateBusiness", "MerchantWebsite = " + merchantWebsite);

                    retrieveBusinessInfo();
                }
            }
        }.execute();
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

    public void onPause()
    {
        updateBusinessInfo();
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

        // TODO: handle errors in getting merchant better
        connect();

        getMerchant();
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
                startActivity(new Intent(CreateBusiness.this, CreateJob.class));
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d("CreateBusiness", "Error occurred: " + error.toString());
            }
        });
        RequestQueueSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}