package com.example.schoo_000.dispatchercloveralpha_v2;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jburk on 11/4/2017.
 */

public class BusinessInfo {
    private String name;
    private String email;
    private String phoneNumber;
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
            // not sure what to do with merchant id, for now same as name
            result.accumulate("merch_id", name);
            result.accumulate("merch_name", name);
            result.accumulate("phone_num",phoneNumber);
            result.accumulate("merch_address", address);
        } catch (JSONException e) {
            // this should never happen
            e.printStackTrace();
        }
        return result;
    }
}