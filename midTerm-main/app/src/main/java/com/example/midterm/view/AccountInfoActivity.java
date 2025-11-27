package com.example.midterm.view;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.midterm.R;
import com.example.midterm.model.entity.User; // Import User mới
import com.example.midterm.viewModel.AccountViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountInfoActivity extends AppCompatActivity {
    private CircleImageView imgAvatar;
    private ImageView imgEditAvatar;
    private Uri selectedImageUri;
    private TextInputEditText etFullname, etPhone, etEmail, etDOB;
    private RadioButton rbMale, rbFemale, rbOther;
    private Button btnSaveProfile;
    private ImageButton btnBack;

    // Chỉ cần 1 ViewModel
    private AccountViewModel accountViewModel;

    // Biến lưu user hiện tại
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_info);

        initViews();

        // Khởi tạo ViewModel
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        // Lấy thông tin User hiện tại (từ ViewModel đã load khi đăng nhập)
        // Hoặc load lại từ DB nếu cần thiết
        accountViewModel.getUser().observe(this, user -> {
            if (user != null) {
                this.currentUser = user;
                displayUserInfo(user);
            }
        });

        // Setup các sự kiện click
        setupEvents();

        // Xử lý giao diện EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        imgAvatar = findViewById(R.id.img_avatar);
        imgEditAvatar = findViewById(R.id.img_edit_avatar);
        etFullname = findViewById(R.id.edit_user_fullname);
        etEmail = findViewById(R.id.edit_user_email);
        etPhone = findViewById(R.id.edit_user_phone);
        etDOB = findViewById(R.id.edit_user_dob);
        rbMale = findViewById(R.id.rb_male);
        rbFemale = findViewById(R.id.rb_female);
        rbOther = findViewById(R.id.rb_other);
        btnSaveProfile = findViewById(R.id.btn_save_profile);
        btnBack = findViewById(R.id.btn_back);
    }

    private void setupEvents() {
        etDOB.setOnClickListener(v -> showDatePicker(etDOB));
        btnBack.setOnClickListener(v -> finish());
        imgEditAvatar.setOnClickListener(v -> openImagePicker());

        // Xử lý nút Lưu
        btnSaveProfile.setOnClickListener(v -> handleSaveProfile());
    }

    private void displayUserInfo(User user) {
        // Hiển thị tất cả thông tin từ đối tượng User duy nhất
        etFullname.setText(user.fullName);
        etEmail.setText(user.email);
        etPhone.setText(user.phoneNumber);
        etDOB.setText(user.dob); // Đảm bảo User.java đã có trường dob

        // Xử lý giới tính
        String gender = user.gender != null ? user.gender : "Khác";
        switch (gender) {
            case "Nam":
                rbMale.setChecked(true);
                break;
            case "Nữ":
                rbFemale.setChecked(true);
                break;
            default:
                rbOther.setChecked(true);
                break;
        }

        // Xử lý ảnh đại diện
        String avatar = user.avatarUrl; // Đảm bảo User.java đã có trường avatarUrl
        if (avatar != null && !avatar.isEmpty()) {
            Glide.with(this)
                    .load(Uri.parse(avatar))
                    .placeholder(R.drawable.catlogo_removebg_preview)
                    .into(imgAvatar);
        } else {
            Glide.with(this)
                    .load(R.drawable.catlogo_removebg_preview)
                    .into(imgAvatar);
        }
    }

    private void handleSaveProfile() {
        if (currentUser == null) return;

        // 1. Lấy dữ liệu từ giao diện
        String newFullName = etFullname.getText().toString().trim();
        String newEmail = etEmail.getText().toString().trim();
        String newPhone = etPhone.getText().toString().trim();
        String newDOB = etDOB.getText().toString().trim();
        String newGender = getSelectedGender();
        String newAvatar = selectedImageUri != null ? selectedImageUri.toString() : currentUser.avatarUrl;

        // 2. Validate cơ bản
        if (newEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            etEmail.setError("Email không hợp lệ");
            etEmail.requestFocus();
            return;
        }
        if (!newPhone.isEmpty() && !newPhone.matches("^(\\+84|0)[0-9]{9,10}$")) {
            etPhone.setError("Số điện thoại không hợp lệ");
            etPhone.requestFocus();
            return;
        }

        // 3. Kiểm tra xem có thay đổi gì không
        boolean isChanged = !newFullName.equals(currentUser.fullName) ||
                !newEmail.equals(currentUser.email) ||
                !newPhone.equals(currentUser.phoneNumber) ||
                !newDOB.equals(currentUser.dob) ||
                !newGender.equals(currentUser.gender) ||
                !newAvatar.equals(currentUser.avatarUrl);

        if (!isChanged) {
            Toast.makeText(this, "Không có thay đổi nào", Toast.LENGTH_SHORT).show();
            return;
        }

        // 4. Cập nhật vào đối tượng User hiện tại
        currentUser.fullName = newFullName;
        currentUser.email = newEmail;
        currentUser.phoneNumber = newPhone;
        currentUser.dob = newDOB;
        currentUser.gender = newGender;
        currentUser.avatarUrl = newAvatar;

        // 5. Gọi ViewModel để lưu xuống DB (Chỉ cần 1 hàm update)
        // Lưu ý: Cần thêm hàm updateUser vào AccountViewModel nếu chưa có
        accountViewModel.updateUser(currentUser);
        Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();

        // Tùy chọn: Kết thúc màn hình sau khi lưu
        // finish();
    }

    // --- CÁC HÀM TIỆN ÍCH GIỮ NGUYÊN ---

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        Glide.with(this)
                                .load(selectedImageUri)
                                .placeholder(R.drawable.catlogo_removebg_preview)
                                .into(imgAvatar);
                    }
                }
            });

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private String getSelectedGender() {
        if (rbMale.isChecked()) return "Nam";
        if (rbFemale.isChecked()) return "Nữ";
        return "Khác";
    }

    private void showDatePicker(TextInputEditText targetEditText) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String formatted = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                            dayOfMonth, month + 1, year);
                    targetEditText.setText(formatted);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
}