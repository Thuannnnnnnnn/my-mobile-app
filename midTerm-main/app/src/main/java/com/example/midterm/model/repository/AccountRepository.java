package com.example.midterm.model.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.example.midterm.model.data.local.AppDatabase;
import com.example.midterm.model.data.local.UserDAO;
import com.example.midterm.model.entity.User;
import com.example.midterm.viewModel.AccountViewModel; // Import interface nếu cần

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AccountRepository {

    private final UserDAO userDAO;
    private final ExecutorService executor;
    private final Handler handler;

    public AccountRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        this.userDAO = db.userDAO();
        this.executor = Executors.newSingleThreadExecutor();
        this.handler = new Handler(Looper.getMainLooper());
    }

    // ==========================================================
    // CÁC INTERFACE CALLBACK (Để báo kết quả về ViewModel)
    // ==========================================================
    public interface AuthCallback {
        void onSuccess(User user);
        void onError(String message);
    }

    public interface ActionCallback {
        void onSuccess(String message);
        void onError(String message);
    }

    // ==========================================================
    // 1. ĐĂNG KÝ
    // ==========================================================
    public void registerUser(User user, AuthCallback callback) {
        executor.execute(() -> {
            try {
                // 1. Check trùng Email
                if (userDAO.checkEmailExists(user.email) != null) {
                    handler.post(() -> callback.onError("Email đã tồn tại!"));
                    return;
                }
                // 2. Check trùng Phone (nếu có nhập)
                if (user.phoneNumber != null && !user.phoneNumber.isEmpty()) {
                    if (userDAO.checkPhoneExists(user.phoneNumber) != null) {
                        handler.post(() -> callback.onError("Số điện thoại đã tồn tại!"));
                        return;
                    }
                }

                // 3. Insert
                userDAO.insertUser(user);

                // 4. Lấy lại user để có ID tự sinh
                User newUser = userDAO.checkEmailExists(user.email);
                handler.post(() -> callback.onSuccess(newUser));

            } catch (Exception e) {
                handler.post(() -> callback.onError("Lỗi đăng ký: " + e.getMessage()));
            }
        });
    }

    // ==========================================================
    // 2. ĐĂNG NHẬP
    // ==========================================================
    public void loginUser(String email, String password, AuthCallback callback) {
        executor.execute(() -> {
            try {
                User user = userDAO.login(email, password);
                if (user != null) {
                    handler.post(() -> callback.onSuccess(user));
                } else {
                    handler.post(() -> callback.onError("Sai email hoặc mật khẩu!"));
                }
            } catch (Exception e) {
                handler.post(() -> callback.onError("Lỗi đăng nhập: " + e.getMessage()));
            }
        });
    }

    // ==========================================================
    // 3. CẬP NHẬT THÔNG TIN (UPDATE)
    // ==========================================================
    public void updateUser(User user, ActionCallback callback) {
        executor.execute(() -> {
            try {
                userDAO.updateUser(user);
                handler.post(() -> callback.onSuccess("Cập nhật thành công!"));
            } catch (Exception e) {
                handler.post(() -> callback.onError("Lỗi cập nhật: " + e.getMessage()));
            }
        });
    }

    // ==========================================================
    // 4. LẤY USER THEO ID (Get By ID)
    // ==========================================================
    public void getUserById(int userId, AuthCallback callback) {
        executor.execute(() -> {
            try {
                User user = userDAO.getUserById(userId);
                if (user != null) {
                    handler.post(() -> callback.onSuccess(user));
                } else {
                    handler.post(() -> callback.onError("Không tìm thấy người dùng"));
                }
            } catch (Exception e) {
                handler.post(() -> callback.onError("Lỗi tải dữ liệu: " + e.getMessage()));
            }
        });
    }

    // ==========================================================
    // 5. KIỂM TRA TỒN TẠI (Check Exist) - Dùng khi sửa Profile
    // ==========================================================
    public void checkExist(String email, String phone, int currentUserId, AccountViewModel.CheckExistCallback callback) {
        executor.execute(() -> {
            boolean exists = false;

            // Check Email
            User userByEmail = userDAO.checkEmailExists(email);
            // Nếu tìm thấy user có email này MÀ KHÔNG PHẢI là chính mình -> Trùng
            if (userByEmail != null && userByEmail.userId != currentUserId) {
                exists = true;
            }

            // Check Phone (Nếu chưa trùng email thì check tiếp phone)
            if (!exists && phone != null && !phone.isEmpty()) {
                User userByPhone = userDAO.checkPhoneExists(phone);
                if (userByPhone != null && userByPhone.userId != currentUserId) {
                    exists = true;
                }
            }

            boolean finalExists = exists;
            handler.post(() -> callback.onResult(finalExists));
        });
    }
}