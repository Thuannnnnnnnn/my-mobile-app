package com.example.midterm.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "users",
        foreignKeys = @ForeignKey(
                entity = MembershipTier.class,
                parentColumns = "tierId",
                childColumns = "tier_id",
                onDelete = ForeignKey.SET_NULL
        ))
public class User implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int userId;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "password_hash")
    public String passwordHash;

    @ColumnInfo(name = "full_name")
    public String fullName;

    @ColumnInfo(name = "phone_number")
    public String phoneNumber;

    @ColumnInfo(name = "role")
    public String role;

    @ColumnInfo(name = "total_points")
    public int totalPoints = 0;

    @ColumnInfo(name = "tier_id", index = true)
    public Integer tierId;

    // --- THÊM CÁC TRƯỜNG MỚI (Từ UserProfile cũ chuyển sang) ---
    @ColumnInfo(name = "dob")
    public String dob; // Ngày sinh

    @ColumnInfo(name = "gender")
    public String gender; // Giới tính

    @ColumnInfo(name = "avatar_url")
    public String avatarUrl; // Link ảnh đại diện

    // --- Constructors ---
    public User() {}

    public User(String email, String passwordHash, String fullName, String role) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
        this.totalPoints = 0;
    }
}