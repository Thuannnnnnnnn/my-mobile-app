package com.example.midterm.model.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "promo_codes")
public class PromoCode implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int promoId;

    public String code;         // "SUMMER2024"
    public double discountValue; // 50000 hoặc 0.1 (10%)
    public String type;         // "Fixed" hoặc "Percent"
    public long expiryDate;     // Hạn sử dụng

    public PromoCode() {}
}