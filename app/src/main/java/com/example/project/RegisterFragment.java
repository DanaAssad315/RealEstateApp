package com.example.project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class RegisterFragment extends Fragment {

    private EditText editEmail, editFirstName, editLastName, editPassword, editConfirmPassword, editPhone;
    private Spinner spinnerGender, spinnerCountry, spinnerCity;
    private Button btnRegister;

    private DBHelper dbHelper;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Initialize views
        editEmail = view.findViewById(R.id.editEmail);
        editFirstName = view.findViewById(R.id.editFirstName);
        editLastName = view.findViewById(R.id.editLastName);
        editPassword = view.findViewById(R.id.editPassword);
        editConfirmPassword = view.findViewById(R.id.editConfirmPassword);
        editPhone = view.findViewById(R.id.editPhone);

        spinnerGender = view.findViewById(R.id.spinnerGender);
        spinnerCountry = view.findViewById(R.id.spinnerCountry);
        spinnerCity = view.findViewById(R.id.spinnerCity);

        btnRegister = view.findViewById(R.id.btnRegister);

        dbHelper = new DBHelper(getContext());


        String[] genders = {"Male", "Female"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);


        String[] countries = {
                "Palestine", "Qatar", "Jordan",
                "Egypt", "Lebanon", "Syria",
                "Iraq", "Saudi Arabia", "Algeria",
                "Morocco", "Tunisia", "Kuwait"
        };
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, countries);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(countryAdapter);

        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                updateCitySpinner(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });

        btnRegister.setOnClickListener(v -> attemptRegister());

        return view;
    }

    private void attemptRegister() {

        String email = editEmail.getText().toString().trim();
        String firstName = editFirstName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String password = editPassword.getText().toString();
        String confirmPassword = editConfirmPassword.getText().toString();
        String phone = editPhone.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem().toString();
        String country = spinnerCountry.getSelectedItem().toString();
        String city = spinnerCity.getSelectedItem().toString();
        if (email.endsWith("@admin.com")) {
            Toast.makeText(getContext(), "Emails ending with @admin.com are reserved for admin accounts.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || phone.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }


        if (firstName.length() < 3 || lastName.length() < 3) {
            Toast.makeText(getContext(), "First and Last Name must be at least 3 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6 || !password.matches(".*[A-Za-z].*") || !password.matches(".*[0-9].*") || !password.matches(".*[!@#$%^&*].*")) {
            Toast.makeText(getContext(), "Password must be at least 6 characters, including a letter, a number, and a special character", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        User newUser = new User(email, firstName, lastName, password, phone, gender, country, city);

        boolean result = dbHelper.insertUser(newUser);

        if (result) {
            Toast.makeText(getContext(), "Registration Successful", Toast.LENGTH_SHORT).show();

            if (getActivity() instanceof LoginRegisterActivity) {
                LoginRegisterActivity activity = (LoginRegisterActivity) getActivity();
                activity.switchToLogin();
            }
        }
        else {
            Toast.makeText(getContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCitySpinner(int countryPosition) {
        String[] cities;

        switch (countryPosition) {
            case 0:
                cities = new String[]{"Ramallah", "Nablus", "Gaza"};
                break;
            case 1:
                cities = new String[]{"Doha", "Al Rayyan", "Al Wakrah"};
                break;
            case 2:
                cities = new String[]{"Amman", "Zarqa", "Irbid"};
                break;
            case 3:
                cities = new String[]{"Cairo", "Alexandria", "Giza"};
                break;
            case 4:
                cities = new String[]{"Beirut", "Tripoli", "Sidon"};
                break;
            case 5:
                cities = new String[]{"Damascus", "Aleppo", "Homs"};
                break;
            case 6:
                cities = new String[]{"Baghdad", "Basra", "Erbil"};
                break;
            case 7:
                cities = new String[]{"Riyadh", "Jeddah", "Mecca"};
                break;
            case 8:
                cities = new String[]{"Algiers", "Oran", "Constantine"};
                break;
            case 9:
                cities = new String[]{"Rabat", "Casablanca", "Marrakesh"};
                break;
            case 10:
                cities = new String[]{"Tunis", "Sfax", "Sousse"};
                break;
            case 11:
                cities = new String[]{"Kuwait City", "Hawalli", "Salmiya"};
                break;
            default:
                cities = new String[]{"Unknown"};
                break;
        }

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, cities);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(cityAdapter);
    }

}