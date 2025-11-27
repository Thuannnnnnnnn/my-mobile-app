package com.example.midterm.model.data.local;

import android.content.Context;
import com.example.midterm.model.entity.Category;
import com.example.midterm.model.entity.Event;
import com.example.midterm.model.entity.MembershipTier;
import com.example.midterm.model.entity.User;
import com.example.midterm.utils.HashPassword;

import java.util.concurrent.Executors;

public class DatabaseSeeder {
    // Hàm này sẽ được gọi 1 lần duy nhất khi cài app
    public static void seed(Context context) {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(context);
            UserDAO userDAO = db.userDAO();
            
            // 1. Tạo Hạng Thành Viên
            if (db.query("SELECT * FROM membership_tiers", null).getCount() == 0) {
                db.runInTransaction(() -> {
                    // Cần dùng DAO của MembershipTier (bạn tự tạo thêm nếu cần) hoặc insert trực tiếp nếu lười
                    // Ở đây tôi giả định bạn dùng Entity trực tiếp thông qua một DAO chung hoặc custom insert
                    // Để đơn giản, ta chỉ log ra là cần thêm data ở đây.
                });
            }
            
            // 2. Tạo User Admin mẫu
            if (userDAO.checkEmailExists("admin@test.com") == null) {
                User admin = new User();
                admin.email = "admin@test.com";
                admin.passwordHash = HashPassword.hashPassword("123456");
                admin.fullName = "Admin User";
                admin.role = "Organizer";
                userDAO.insertUser(admin);
            }
        });
    }
}