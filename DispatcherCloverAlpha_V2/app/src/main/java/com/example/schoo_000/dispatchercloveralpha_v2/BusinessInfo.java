package com.example.schoo_000.dispatchercloveralpha_v2;

import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jburk on 11/4/2017.
 */

public class BusinessInfo {
    private String id;
    private String name;
    private String website;
    private String phoneNumber;
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public JSONObject getJSON() {
        JSONObject result = new JSONObject();
        try {
            result.accumulate("merch_id", id);
            result.accumulate("merch_name", name);
            result.accumulate("phone_num",phoneNumber);
            result.accumulate("merch_address", address);
        } catch (JSONException e) {
            // this should never happen
            e.printStackTrace();
        }
        return result;
    }

    public static BusinessInfo readFromSharedPreferences(SharedPreferences prefs) {
        BusinessInfo result = new BusinessInfo();
        result.setId(prefs.getString("merchantID", ""));
        result.setName(prefs.getString("nameString", ""));
        result.setPhoneNumber(prefs.getString("phoneString", ""));
        result.setAddress(prefs.getString("addressString", ""));
        result.setWebsite(prefs.getString("emailString", ""));
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}