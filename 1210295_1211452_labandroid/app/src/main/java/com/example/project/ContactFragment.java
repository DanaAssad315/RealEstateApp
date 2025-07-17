package com.example.project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

public class ContactFragment extends Fragment {

    public ContactFragment() {

    }
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        ImageButton btnCall = view.findViewById(R.id.btn_call);
        ImageButton btnLocate = view.findViewById(R.id.btn_locate);
        ImageButton btnEmail = view.findViewById(R.id.btn_email);

        Animation scaleUp = AnimationUtils.loadAnimation(getContext(), R.anim.scale_up);
        Animation scaleDown = AnimationUtils.loadAnimation(getContext(), R.anim.scale_down);

        View.OnTouchListener animTouchListener = (v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.startAnimation(scaleUp);
                    return true;
                case MotionEvent.ACTION_UP:
                    v.startAnimation(scaleDown);
                    v.performClick();
                    return true;
                case MotionEvent.ACTION_CANCEL:
                    v.startAnimation(scaleDown);
                    return true;
            }
            return false;
        };

        btnCall.setOnTouchListener(animTouchListener);
        btnEmail.setOnTouchListener(animTouchListener);
        btnLocate.setOnTouchListener(animTouchListener);


        btnCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:+970599000000"));
            startActivity(intent);
        });

        btnLocate.setOnClickListener(v -> {
            Uri locationUri = Uri.parse("geo:0,0?q=Real+Estate+Hub");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, locationUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });

        btnEmail.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:RealEstateHub@agency.com"));
            startActivity(Intent.createChooser(emailIntent, "Send Email"));
        });

        return view;
    }

}
