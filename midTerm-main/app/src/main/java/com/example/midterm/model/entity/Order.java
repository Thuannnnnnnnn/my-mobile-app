package com.example.midterm.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "orders",
        foreignKeys = {
            @ForeignKey(entity = User.class, parentColumns = "userId", childColumns = "user_id"),
            @ForeignKey(entity = PromoCode.class, parentColumns = "promoId", childColumns = "promo_code_id")
        })
public class Order implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int orderId;

    @ColumnInfo(name = "user_id", index = true)
    public int userId; // Người mua

    @ColumnInfo(name = "promo_code_id", index = true)
    public Integer promoCodeId; // Mã giảm giá (có thể null)

    public long orderDate;
    public double totalAmount; // Tổng tiền
    public String status;      // "Pending", "Paid", "Cancelled"

    public Order() {}
}