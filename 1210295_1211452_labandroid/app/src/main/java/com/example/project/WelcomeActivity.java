package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class WelcomeActivity extends AppCompatActivity {

    private Button btnConnect;
    private static final String API_URL = "https://mocki.io/v1/705ada5b-062b-4d0d-a017-112ad0f89c4c";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnConnect = findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(v -> connectToApi());
    }

    private void connectToApi() {

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, API_URL, null,
                response -> {
                    Toast.makeText(this, "Connected Successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(WelcomeActivity.this, LoginRegisterActivity.class);
                    startActivity(intent);
                    finish();
                }, error -> {
                    Toast.makeText(this, "Connection Failed. Please try again.", Toast.LENGTH_LONG).show();
                }
        );

        queue.add(request);
    }
}
