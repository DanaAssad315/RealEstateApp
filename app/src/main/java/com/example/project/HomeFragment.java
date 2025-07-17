package com.example.project;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.VideoView;

import java.util.Arrays;

public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public HomeFragment() {}

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        VideoView videoView = view.findViewById(R.id.videoView);
        String path = "android.resource://" + requireContext().getPackageName() + "/" + R.raw.tt;
        Uri uri = Uri.parse(path);
        videoView.setVideoURI(uri);

        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            mp.setVolume(0f, 0f);
            videoView.start();
        });
//        اضافة للتجربة فقط

/*
        Button checkDbBtn = view.findViewById(R.id.checkDbButton);
        checkDbBtn.setOnClickListener(v -> {
            DBHelper dbHelper = new DBHelper(requireContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            Cursor cursor = db.rawQuery("SELECT * FROM reservations", null);

            Log.d("RESERVATION_COLUMNS", Arrays.toString(cursor.getColumnNames()));
            Log.d("RESERVATION_COUNT", "Cursor count = " + cursor.getCount());

            if (cursor.moveToFirst()) {
                do {
                    Log.d("RES_ROW", "ID=" + cursor.getInt(cursor.getColumnIndexOrThrow("id")) +
                            ", Email=" + cursor.getString(cursor.getColumnIndexOrThrow("user_email")) +
                            ", PropertyID=" + cursor.getInt(cursor.getColumnIndexOrThrow("property_id")) +
                            ", Status=" + cursor.getString(cursor.getColumnIndexOrThrow("status")));
                } while (cursor.moveToNext());
            } else {
                Log.d("RES_ROW", "No data found in reservations.");
            }
            Log.d("PROPERTIES", "Count: " + cursor.getCount());
            while (cursor.moveToNext()) {
                Log.d("PROP", "ID=" + cursor.getInt(cursor.getColumnIndexOrThrow("id")) +
                        ", Title=" + cursor.getString(cursor.getColumnIndexOrThrow("title")));
            }

            cursor.close();
        });*/

        return view;
    }

}
