package com.example.midterm.model.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.midterm.model.entity.User;

@Dao
public interface UserDAO {

    // 1. ĐĂNG KÝ MỚI (Dùng chung cho cả Guest và Organizer)
    @Insert
    void insertUser(User user);

    // 2. ĐĂNG NHẬP (Chỉ cần check 1 bảng duy nhất)
    @Query("SELECT * FROM users WHERE email = :email AND password_hash = :password LIMIT 1")
    User login(String email, String password);

    // 3. CHECK TRÙNG EMAIL
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User checkEmailExists(String email);

    // 4. LẤY THÔNG TIN THEO ID
    @Query("SELECT * FROM users WHERE userId = :id")
    User getUserById(int id);

    // 5. NÂNG CẤP QUYỀN (Logic: Guest muốn thành Organizer -> Update role)
    @Query("UPDATE users SET role = 'Organizer' WHERE userId = :userId")
    void upgradeToOrganizer(int userId);

    // 6. CẬP NHẬT THÔNG TIN
    @Update
    void updateUser(User user);
    @Query("SELECT * FROM users WHERE phone_number = :phone LIMIT 1")
    User checkPhoneExists(String phone);
}
