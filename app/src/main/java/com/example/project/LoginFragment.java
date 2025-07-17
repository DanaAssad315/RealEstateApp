package com.example.project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginFragment extends Fragment {

    private EditText editEmail, editPassword;
    private CheckBox checkboxRemember;
    SharedPrefManager sharedPrefManager;
    private DBHelper dbHelper;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_log_in, container, false);

        editEmail = view.findViewById(R.id.editEmail);
        editPassword = view.findViewById(R.id.editPassword);
        checkboxRemember = view.findViewById(R.id.checkboxRemember);

        Button btnLogin = view.findViewById(R.id.btnLogin);
        TextView txtRegisterNow = view.findViewById(R.id.txtRegisterNow);

        sharedPrefManager = SharedPrefManager.getInstance(requireContext());

        dbHelper = new DBHelper(getContext());

        String savedEmail = sharedPrefManager.readString("email", "");
        boolean rememberMe = sharedPrefManager.readBoolean("rememberedMe", false);

        if (rememberMe && !savedEmail.isEmpty()) {
            editEmail.setText(savedEmail);
            checkboxRemember.setChecked(true);
        }
        btnLogin.setOnClickListener(v -> attemptLogin());

        txtRegisterNow.setOnClickListener(v -> {
            if (getActivity() instanceof LoginRegisterActivity) {
                LoginRegisterActivity activity = (LoginRegisterActivity) getActivity();
                activity.switchToRegister();
            }
        });

        return view;
    }

    private void attemptLogin() {

        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (checkboxRemember.isChecked()) {
            sharedPrefManager.writeString("email",editEmail.getText().toString());
            sharedPrefManager.writeBoolean("rememberedMe", true);

        } else {
            sharedPrefManager.clear();
        }

        User user = dbHelper.validateData(email, password);

        if (user != null) {
            ReservationManager.initialize(requireContext(), user.getEmail());
            FavoritesManager.initialize(requireContext(), user.getEmail());

            Intent intent;

            if (email.endsWith("@admin.com")) {
                intent = new Intent(getActivity(), AdminActivity.class);
            }
            else {
                intent = new Intent(getActivity(), Home_page.class);
            }
            intent.putExtra("user_email", user.getEmail());
            startActivity(intent);
            requireActivity().finish();
        }

        else {
            Toast.makeText(getContext(), "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }
}