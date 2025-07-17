package com.example.project;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import android.util.Log;
public class PropertyJsonParser {
    public static ArrayList<Property> getPropertiesFromJson(String json) throws JSONException {
        JSONObject root = new JSONObject(json);
        JSONArray arr = root.getJSONArray("properties");
        ArrayList<Property> list = new ArrayList<>();

        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            Property p = new Property();
            p.id = obj.getInt("id");
            p.title = obj.getString("title");
            p.type = obj.getString("type");
            p.price = obj.getDouble("price");
            p.location = obj.getString("location");
            p.area = obj.getString("area");
            p.bedrooms = obj.getInt("bedrooms");
            p.bathrooms = obj.getInt("bathrooms");
            p.image_url = obj.getString("image_url");
            p.description = obj.getString("description");
            list.add(p);
        }

        for (Property p : list) {
            Log.d("PropertyJsonParser", "Final Property in list: " + p.toString());
        }

        return list;
    }


}