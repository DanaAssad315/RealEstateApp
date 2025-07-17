package com.example.project;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class ConnectionAsyncTask extends AsyncTask<String, Void, String> {
    PropertiesFragment fragment;

    public ConnectionAsyncTask(PropertiesFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    protected void onPreExecute() {
        fragment.showLoading();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return HttpManager.getData(params[0]);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        fragment.hideLoading();
        try {
            ArrayList<Property> props = PropertyJsonParser.getPropertiesFromJson(s);
            fragment.displayProperties(props);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}