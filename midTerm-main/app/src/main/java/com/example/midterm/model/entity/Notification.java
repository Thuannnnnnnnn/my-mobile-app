package com.example.midterm.model.entity;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "notifications",
        foreignKeys = @ForeignKey(entity = User.class, parentColumns = "userId", childColumns = "user_id", onDelete = ForeignKey.CASCADE))
public class Notification {
    @PrimaryKey(autoGenerate = true) public int notifId;
    @ColumnInfo(name = "user_id", index = true) public int userId;
    public String title;
    public String message;
    public boolean isRead = false;
    public long createdAt;
}