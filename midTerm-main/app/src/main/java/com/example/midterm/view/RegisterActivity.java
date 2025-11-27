package com.example.midterm.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.midterm.R;
import com.example.midterm.utils.HashPassword;
import com.example.midterm.viewModel.AccountViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword, etConfirmPassword; // Removed etEmail, etPhone, etName
    private TextView tvErrorUsername, tvErrorPassword, tvErrorConfirmPassword, tvLoginNow;
    private Button btnRegister;
    private ImageButton btnBack;
    private RadioGroup radioGroupRole;
    private RadioButton rbAttendee, rbOrganizer;

    private AccountViewModel accountViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        initViews();
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        setUpRealtimeValidation();

        btnRegister.setOnClickListener(v -> handleRegister());

        btnBack.setOnClickListener(v -> onBackPressed());
        tvLoginNow.setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupViewModelObservers();
    }

    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.edit_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        tvErrorUsername = findViewById(R.id.tvErrorRegisterUsername);
        tvErrorPassword = findViewById(R.id.tvErrorRegisterPassword);
        tvErrorConfirmPassword = findViewById(R.id.tvErrorConfirmPassword);
        tvLoginNow = findViewById(R.id.tv_login_now);
        btnRegister = findViewById(R.id.btn_register);
        btnBack = findViewById(R.id.btn_back);
        radioGroupRole = findViewById(R.id.radioGroupRole);
        rbAttendee = findViewById(R.id.rbAttendee);
        rbOrganizer = findViewById(R.id.rbOrganizer);
    }

    private void handleRegister() {
        String inputIdentifier = etUsername.getText().toString().trim(); // This can be email or phone
        String password = etPassword.getText().toString().trim();
        String confirm = etConfirmPassword.getText().toString().trim();

        // Placeholder for fullName and actual phoneNumber/email from inputIdentifier
        String fullName = ""; // Needs UI input field if required
        String email = "";
        String phoneNumber = "";

        if (isValidEmail(inputIdentifier)) {
            email = inputIdentifier;
        } else if (isValidPhone(inputIdentifier)) {
            phoneNumber = inputIdentifier;
        }

        boolean valid = validateUsername(inputIdentifier) &&
                        validatePassword(password) &&
                        validateConfirmPassword(password, confirm);

        if (!valid) return;

        btnRegister.setEnabled(false);
        int selectedRoleId = radioGroupRole.getCheckedRadioButtonId();
        String role;
        if (selectedRoleId == R.id.rbOrganizer) {
            role = "Organizer";
        } else {
            role = "Attendee"; // Default or if rbAttendee is checked
        }

        String hashedPassword = HashPassword.hashPassword(password);

        // Call ViewModel with updated signature
        // Note: fullName is an empty string currently as it is not in UI
        accountViewModel.register(email, hashedPassword, fullName, phoneNumber, role);
    }

    private void setupViewModelObservers() {
        accountViewModel.getIsLoading().observe(this, isLoading -> {
            btnRegister.setEnabled(!isLoading);
            // Optionally show/hide a progress bar
        });

        accountViewModel.getUser().observe(this, user -> {
            if (user != null) {
                Snackbar.make(btnRegister, "Đăng ký thành công!", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(Color.parseColor("#4CAF50"))
                        .setTextColor(Color.WHITE)
                        .show();
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        accountViewModel.getError().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Snackbar.make(btnRegister, message, Snackbar.LENGTH_LONG)
                        .setBackgroundTint(Color.parseColor("#ED2A2A"))
                        .show();
            }
        });
    }

    private void setUpRealtimeValidation() {
        etUsername.addTextChangedListener(new SimpleTextWatcher(s -> validateUsername(s.toString())));
        etPassword.addTextChangedListener(new SimpleTextWatcher(s -> validatePassword(s.toString())));
        etConfirmPassword.addTextChangedListener(new SimpleTextWatcher(s ->
                validateConfirmPassword(etPassword.getText().toString(), s.toString())));
    }

    private boolean validateUsername(String input) {
        if (input.isEmpty()) {
            showError(tvErrorUsername, "Không được để trống");
            return false;
        } else if (!isValidEmail(input) && !isValidPhone(input)) {
            showError(tvErrorUsername, "Email hoặc số điện thoại không hợp lệ");
            return false;
        } else {
            hideError(tvErrorUsername);
            return true;
        }
    }

    private boolean validatePassword(String input) {
        if (input.isEmpty()) {
            showError(tvErrorPassword, "Vui lòng nhập mật khẩu");
            return false;
        } else if (input.length() < 6) {
            showError(tvErrorPassword, "Mật khẩu phải có ít nhất 6 ký tự");
            return false;
        } else {
            hideError(tvErrorPassword);
            return true;
        }
    }

    private boolean validateConfirmPassword(String password, String confirm) {
        if (confirm.isEmpty()) {
            showError(tvErrorConfirmPassword, "Vui lòng nhập lại mật khẩu");
            return false;
        } else if (!confirm.equals(password)) {
            showError(tvErrorConfirmPassword, "Mật khẩu không khớp");
            return false;
        } else {
            hideError(tvErrorConfirmPassword);
            return true;
        }
    }

    private void showError(TextView textView, String message) {
        textView.setText(message);
        textView.setVisibility(View.VISIBLE);
    }

    private void hideError(TextView textView) {
        textView.setVisibility(View.GONE);
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("^(\\+84|0)[0-9]{9,10}$");
    }

    private static class SimpleTextWatcher implements TextWatcher {
        private final OnTextChangedListener listener;

        SimpleTextWatcher(OnTextChangedListener listener) {
            this.listener = listener;
        }

        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
            listener.onTextChanged(s);
        }
        @Override public void afterTextChanged(Editable s) {}

        interface OnTextChangedListener {
            void onTextChanged(CharSequence s);
        }
    }
}
