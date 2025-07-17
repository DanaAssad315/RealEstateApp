package com.example.project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileFragment extends Fragment {

    private ImageView imageViewProfilePic;
    private ImageButton buttonChangeProfilePic;
    private TextInputEditText editTextFirstName, editTextLastName, editTextPhoneNumber;
    private TextInputEditText editTextCurrentPassword, editTextNewPassword, editTextConfirmPassword;
    private Button buttonUpdateProfile;

    private DBHelper dbHelper;
    private SharedPrefManager sharedPrefManager;


    private Uri selectedImageUri;

    private ActivityResultLauncher<Intent> pickImageLauncher;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        dbHelper = new DBHelper(requireContext());
        sharedPrefManager = SharedPrefManager.getInstance(requireContext());

        imageViewProfilePic = view.findViewById(R.id.imageViewProfilePic);
        buttonChangeProfilePic = view.findViewById(R.id.buttonChangeProfilePic);
        editTextFirstName = view.findViewById(R.id.editTextFirstName);
        editTextLastName = view.findViewById(R.id.editTextLastName);
        editTextPhoneNumber = view.findViewById(R.id.editTextPhoneNumber);
        editTextCurrentPassword = view.findViewById(R.id.editTextCurrentPassword);
        editTextNewPassword = view.findViewById(R.id.editTextNewPassword);
        editTextConfirmPassword = view.findViewById(R.id.editTextConfirmPassword);
        buttonUpdateProfile = view.findViewById(R.id.buttonUpdateProfile);


        loadUserData();

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImageUri);
                            imageViewProfilePic.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });


        buttonChangeProfilePic.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });


        buttonUpdateProfile.setOnClickListener(v -> {
            updateProfile();
        });

        return view;
    }

    private void loadUserData() {
        String email = sharedPrefManager.readString("email", null);
        if (email == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = dbHelper.getUserByEmail(email);
        if (user != null) {
            editTextFirstName.setText(user.getFirstName());
            editTextLastName.setText(user.getLastName());
            editTextPhoneNumber.setText(user.getPhone());
        }
    }

    private void updateProfile() {
        String email = sharedPrefManager.readString("email", null);
        if (email == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String phone = editTextPhoneNumber.getText().toString().trim();

        String currentPassword = editTextCurrentPassword.getText().toString();
        String newPassword = editTextNewPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName)) {
            Toast.makeText(requireContext(), "First and Last names cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.isEmpty(phone) && !Patterns.PHONE.matcher(phone).matches()) {
            Toast.makeText(requireContext(), "Invalid phone number", Toast.LENGTH_SHORT).show();
            return;
        }
        User user = dbHelper.getUserByEmail(email);
        if (user == null) {
            Toast.makeText(requireContext(), "User data not found", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!TextUtils.isEmpty(currentPassword) || !TextUtils.isEmpty(newPassword) || !TextUtils.isEmpty(confirmPassword)) {

            if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(requireContext(), "Please fill all password fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!currentPassword.equals(user.getPassword())) {
                Toast.makeText(requireContext(), "Current password is incorrect", Toast.LENGTH_SHORT).show();
                return;
            }


            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(requireContext(), "New password and confirmation do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidPassword(newPassword)) {
                Toast.makeText(requireContext(), "Password must be at least 8 characters with uppercase, number, and special character", Toast.LENGTH_LONG).show();
                return;
            }


            user.setPassword(newPassword);
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);


        boolean success = dbHelper.updateUser(user);
        if (success) {
            Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();

            editTextCurrentPassword.setText("");
            editTextNewPassword.setText("");
            editTextConfirmPassword.setText("");
        } else {
            Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8)
            return false;
        Pattern uppercase = Pattern.compile("[A-Z]");
        Pattern digit = Pattern.compile("[0-9]");
        Pattern special = Pattern.compile("[^a-zA-Z0-9]");
        Matcher hasUppercase = uppercase.matcher(password);
        Matcher hasDigit = digit.matcher(password);
        Matcher hasSpecial = special.matcher(password);

        return hasUppercase.find() && hasDigit.find() && hasSpecial.find();
    }
}
