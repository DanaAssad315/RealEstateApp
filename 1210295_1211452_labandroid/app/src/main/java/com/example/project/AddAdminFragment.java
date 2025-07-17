package com.example.project;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddAdminFragment extends Fragment {

    private EditText AdminFirstName, AdminLastName , AdminEmail , AdminPassword , AdminPhone , AdminGender,
            AdminCountry , AdminCity;
    private Button btnAddAdmin;
    private DBHelper db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_admin, container, false);
        AdminFirstName = root.findViewById(R.id.editAdminFirstNamee);
        AdminLastName =  root.findViewById(R.id.editAdminLastName);
        AdminEmail = root.findViewById(R.id.editAdminEmail);
        AdminPassword = root.findViewById(R.id.editAdminPassword);
        AdminPhone = root.findViewById(R.id.editAdminPhone);
        AdminGender = root.findViewById(R.id.editAdminGender);
        AdminCountry = root.findViewById(R.id.editAdminCountry);
        AdminCity = root.findViewById(R.id.editAdminCity);


        btnAddAdmin = root.findViewById(R.id.btnAddAdmin);

        db = new DBHelper(getContext());

        btnAddAdmin.setOnClickListener(v -> {
            String first = AdminFirstName.getText().toString().trim();
            String last = AdminLastName.getText().toString().trim();
            String email = AdminEmail.getText().toString().trim();
            String password = AdminPassword.getText().toString().trim();
            String phone = AdminPhone.getText().toString().trim();
            String gender = AdminGender.getText().toString().trim();
            String country = AdminCountry.getText().toString().trim();
            String city = AdminCity.getText().toString().trim();

            if (first.isEmpty() || last.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean exists = db.isUserExists(email);
            if (exists) {
                Toast.makeText(getContext(), "Email already registered", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User(email, first, last, password, phone, gender, country, city);

            boolean inserted = db.addAdmin(user);
            if (inserted) {
                Toast.makeText(getContext(), "Admin added successfully", Toast.LENGTH_SHORT).show();


                AdminFirstName.setText("");
                AdminLastName.setText("");
                AdminEmail.setText("");
                AdminPassword.setText("");
                AdminPhone.setText("");
                AdminGender.setText("");
                AdminCountry.setText("");
                AdminCity.setText("");
            } else {
                Toast.makeText(getContext(), "Error adding admin", Toast.LENGTH_SHORT).show();
            }
        });


        return root;
    }
}