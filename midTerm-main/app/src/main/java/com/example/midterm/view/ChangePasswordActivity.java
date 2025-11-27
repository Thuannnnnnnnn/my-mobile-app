package com.example.midterm.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.midterm.R;
import com.example.midterm.model.entity.User; // Dùng User mới
import com.example.midterm.utils.HashPassword; // Tiện ích mã hóa mật khẩu
import com.example.midterm.viewModel.AccountViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText etOldPassword, etNewPassword, etConfirmPassword;
    private Button btnChangePassword;
    private ImageButton btnBack;

    private AccountViewModel accountViewModel;
    private User currentUser; // Thay Account bằng User

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initViews();

        // 1. Khởi tạo ViewModel
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        // 2. Lắng nghe dữ liệu User hiện tại
        accountViewModel.getUser().observe(this, user -> {
            if (user != null) {
                this.currentUser = user;
            }
        });

        // 3. Xử lý sự kiện click
        btnChangePassword.setOnClickListener(v -> handleChangePassword());
        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        etOldPassword = findViewById(R.id.edit_current_password);
        etNewPassword = findViewById(R.id.edit_new_password);
        etConfirmPassword = findViewById(R.id.edit_confirm_password);
        btnChangePassword = findViewById(R.id.btn_change_password);
        btnBack = findViewById(R.id.btn_back);
    }

    private void handleChangePassword() {
        if (currentUser == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            return;
        }

        String oldPass = etOldPassword.getText().toString().trim();
        String newPass = etNewPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        // --- VALIDATION (Kiểm tra dữ liệu nhập) ---

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra mật khẩu cũ có đúng không
        // (Phải hash mật khẩu nhập vào rồi mới so sánh với hash trong DB)
        String hashedOldPass = HashPassword.hashPassword(oldPass);
        if (!hashedOldPass.equals(currentUser.passwordHash)) {
            etOldPassword.setError("Mật khẩu cũ không chính xác");
            etOldPassword.requestFocus();
            return;
        }

        // Kiểm tra mật khẩu mới
        if (newPass.length() < 6) {
            etNewPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            etConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            return;
        }

        // --- CẬP NHẬT MẬT KHẨU MỚI ---

        // 1. Hash mật khẩu mới
        String hashedNewPass = HashPassword.hashPassword(newPass);

        // 2. Cập nhật vào object User
        currentUser.passwordHash = hashedNewPass;

        // 3. Lưu xuống Database
        accountViewModel.updateUser(currentUser);

        Toast.makeText(this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
        finish(); // Đóng màn hình
    }
}