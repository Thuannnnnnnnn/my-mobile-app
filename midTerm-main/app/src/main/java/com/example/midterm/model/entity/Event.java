package com.example.midterm.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "events",
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "userId", childColumns = "organizer_id", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Category.class, parentColumns = "categoryId", childColumns = "category_id", onDelete = ForeignKey.SET_NULL)
        })
public class Event implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int eventId;

    // Khóa ngoại
    @ColumnInfo(name = "organizer_id", index = true)
    public int organizerId;

    @ColumnInfo(name = "category_id", index = true)
    public Integer categoryId;

    // Thông tin cơ bản
    public String title;
    public String description;
    public String location;
    
    // Thời gian (Lưu dạng timestamp long để dễ tính toán)
    @ColumnInfo(name = "start_time")
    public long startTime;
    
    @ColumnInfo(name = "end_time")
    public long endTime;

    public String status; // "Draft", "Published", "Cancelled"
    
    @ColumnInfo(name = "image_url")
    public String imageUrl; // Ảnh bìa chính

    public Event() {}
}