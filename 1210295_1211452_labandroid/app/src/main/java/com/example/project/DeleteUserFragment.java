package com.example.project;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DeleteUserFragment extends Fragment {

    private EditText editTextCustomerEmail;
    private DBHelper db;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_delete_user, container, false);

        editTextCustomerEmail = root.findViewById(R.id.editUserEmail);
        Button btnDeleteCustomer = root.findViewById(R.id.btnDeleteUser);

        db = new DBHelper(getContext());

        btnDeleteCustomer.setOnClickListener(v -> {
            String email = editTextCustomerEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(getContext(), "Please enter email", Toast.LENGTH_SHORT).show();
            } else {
                boolean deleted = db.deleteCustomerByEmail(email);
                if (deleted) {
                    Toast.makeText(getContext(), "User deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return root;
    }
}