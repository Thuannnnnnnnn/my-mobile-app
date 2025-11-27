package com.example.midterm.model.entity;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "reviews",
        foreignKeys = {
            @ForeignKey(entity = User.class, parentColumns = "userId", childColumns = "user_id"),
            @ForeignKey(entity = Event.class, parentColumns = "eventId", childColumns = "event_id")
        })
public class Review {
    @PrimaryKey(autoGenerate = true) public int reviewId;
    @ColumnInfo(name = "user_id", index = true) public int userId;
    @ColumnInfo(name = "event_id", index = true) public int eventId;
    public int rating; // 1-5 sao
    public String comment;
    public long createdAt;
}