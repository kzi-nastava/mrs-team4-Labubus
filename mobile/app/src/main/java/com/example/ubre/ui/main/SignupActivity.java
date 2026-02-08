package com.example.ubre.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ubre.R;
import com.example.ubre.ui.apis.AccountSettingsApi;
import com.example.ubre.ui.apis.ApiClient;
import com.example.ubre.ui.apis.LoginApi;
import com.example.ubre.ui.apis.UserApi;
import com.example.ubre.ui.dtos.UserDto;
import com.example.ubre.ui.dtos.UserRegistrationDto;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText etFirstName, etLastName, etEmail, etPassword, etConfirmPassword, etAddress, etPhone;
    private Button btnRegister;
    private Uri selectedFileUri;
    private ImageView ivProfilePic;
    private final UserApi userApi = ApiClient.getClient().create(UserApi.class);
    private final AccountSettingsApi avatarApi = ApiClient.getClient().create(AccountSettingsApi.class);

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedFileUri = result.getData().getData();
                    ivProfilePic.setImageURI(selectedFileUri);
                    Glide.with(this)
                            .load(selectedFileUri)
                            .circleCrop()
                            .placeholder(R.drawable.img_default_avatar)
                            .error(R.drawable.img_default_avatar)
                            .into(ivProfilePic);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        ImageView btnBack = findViewById(R.id.btn_back);
        TextView btnLogin = findViewById(R.id.tvLogin);
        ivProfilePic = findViewById(R.id.ivProfilePic);
        btnRegister = findViewById(R.id.btnRegisterSubmit);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);

        btnBack.setOnClickListener(v -> finish());

        btnRegister.setOnClickListener(v -> {
            performRegistration();
        });

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        Glide.with(this)
                .load(R.drawable.img_default_avatar)
                .circleCrop()
                .into(ivProfilePic);

        ivProfilePic.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });
    }

    private void performRegistration() {
        String firstName = String.valueOf(etFirstName.getText()).trim();
        String lastName  = String.valueOf(etLastName.getText()).trim();
        String email     = String.valueOf(etEmail.getText()).trim();
        String password  = String.valueOf(etPassword.getText());
        String confirmPassword  = String.valueOf(etConfirmPassword.getText());
        String address   = String.valueOf(etAddress.getText()).trim();
        String phone     = String.valueOf(etPhone.getText()).trim();

        if (password.isEmpty() || !password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords must match and not be empty");
            return;
        }
        String avatarUrlValue = "default-avatar.jpg";

        if (selectedFileUri != null) {
            avatarUrlValue = email + "_avatar.jpg";
        }

            UserRegistrationDto registrationDto = new UserRegistrationDto(
                avatarUrlValue, email, password, firstName, lastName, address, phone
        );

        sendRegistrationRequest(registrationDto);
    }

    private void sendRegistrationRequest(UserRegistrationDto dto) {
        userApi.register(dto).enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserDto createdUser = response.body();

                    if (selectedFileUri != null) {
                        uploadAvatar(createdUser.getId(), createdUser.getEmail());
                    } else {
                            Toast.makeText(SignupActivity.this, "We sent you an activation link to your email", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    String errorMsg = "Error";
                    try {
                        errorMsg = response.errorBody().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Toast.makeText(SignupActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserDto> call, Throwable t) {
                Toast.makeText(SignupActivity.this, "Unsuccessful", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void uploadAvatar(Long userId, String email) {
        try {
            byte[] avatarBytes = getBytesFromUri(selectedFileUri);

            RequestBody rb = RequestBody.create(avatarBytes, MediaType.parse("image/*"));
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", email + "_avatar.jpg", rb);

            avatarApi.updateUserAvatar("", userId, part).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Toast.makeText(SignupActivity.this, "We sent you an activation link to your email", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(SignupActivity.this, "User created, but avatar failed.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] getBytesFromUri(Uri uri) throws Exception {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        if (inputStream == null) return null;

        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }

}