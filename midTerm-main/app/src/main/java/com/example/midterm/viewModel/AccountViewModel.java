package com.example.midterm.viewModel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.midterm.model.entity.User;
import com.example.midterm.model.repository.AccountRepository;
import com.example.midterm.utils.HashPassword;

public class AccountViewModel extends AndroidViewModel {

    private final AccountRepository repository;

    // --- LiveData để giao diện lắng nghe ---
    // User đang đăng nhập (Dùng cho toàn app)
    private final MutableLiveData<User> loggedInUser = new MutableLiveData<>();

    // User được load theo ID (Dùng cho màn hình xem profile người khác hoặc reload profile)
    private final MutableLiveData<User> currentUserData = new MutableLiveData<>();

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public AccountViewModel(@NonNull Application application) {
        super(application);
        repository = new AccountRepository(application);
    }

    // --- Getters LiveData ---
    public LiveData<User> getUser() { return loggedInUser; }
    public LiveData<User> getCurrentUserData() { return currentUserData; }
    public LiveData<String> getError() { return errorMessage; }
    public LiveData<String> getSuccessMessage() { return successMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    // =================================================================
    // 1. ĐĂNG NHẬP
    // =================================================================
    public void login(String email, String password) {
        isLoading.setValue(true);
        // Hash password trước khi gửi xuống DB
        String hashedPassword = HashPassword.hashPassword(password);

        repository.loginUser(email, hashedPassword, new AccountRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                isLoading.setValue(false);
                loggedInUser.setValue(user); // Cập nhật user cho toàn app
            }

            @Override
            public void onError(String message) {
                isLoading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    // =================================================================
    // 2. ĐĂNG KÝ
    // =================================================================
    public void register(String email, String pass, String name, String phone, String role) {
        isLoading.setValue(true);
        String hashedPassword = HashPassword.hashPassword(pass);

        User newUser = new User(email, hashedPassword, name, role);
        newUser.phoneNumber = phone; // Lưu số điện thoại

        repository.registerUser(newUser, new AccountRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                isLoading.setValue(false);
                successMessage.setValue("Đăng ký thành công!");
                loggedInUser.setValue(user); // Tự động login luôn
            }

            @Override
            public void onError(String message) {
                isLoading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    // =================================================================
    // 3. CẬP NHẬT THÔNG TIN (Dùng cho AccountInfoActivity)
    // =================================================================
    public void updateUser(User user) {
        isLoading.setValue(true);
        repository.updateUser(user, new AccountRepository.ActionCallback() {
            @Override
            public void onSuccess(String message) {
                isLoading.setValue(false);
                successMessage.setValue(message);

                // Cập nhật lại LiveData để giao diện tự refresh
                loggedInUser.setValue(user);
                currentUserData.setValue(user);
            }

            @Override
            public void onError(String message) {
                isLoading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    // =================================================================
    // 4. TẢI THÔNG TIN USER THEO ID
    // =================================================================
    public void loadUserById(int userId) {
        repository.getUserById(userId, new AccountRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                currentUserData.setValue(user);
            }

            @Override
            public void onError(String message) {
                errorMessage.setValue(message);
            }
        });
    }

    // Hàm hỗ trợ code cũ (nếu file cũ gọi getAccountById)
    public LiveData<User> getAccountById(int userId) {
        loadUserById(userId);
        return currentUserData;
    }

    // =================================================================
    // 5. KIỂM TRA TRÙNG LẶP (Email/Phone)
    // =================================================================

    // Interface callback để trả kết quả true/false về Activity
    public interface CheckExistCallback {
        void onResult(boolean exists);
    }

    public void checkEmailOrPhoneExist(String email, String phone, int currentUserId, CheckExistCallback callback) {
        // Gọi xuống repository để kiểm tra
        repository.checkExist(email, phone, currentUserId, callback);
    }

    // Hàm giữ chỗ để tránh lỗi code cũ (nếu Activity cũ gọi hàm này)
    // Logic thực tế đã chuyển sang updateUser(User user)
    public void updateEmailOrPhone(int userId, String email, String phone, Object dummy) {
        // Không cần làm gì ở đây vì hàm updateUser đã lo hết rồi.
        // Bạn có thể xóa hàm này sau khi refactor xong Activity.
    }
}