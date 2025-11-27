package com.example.midterm.model.entity;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "event_media",
        foreignKeys = @ForeignKey(entity = Event.class, parentColumns = "eventId", childColumns = "event_id", onDelete = ForeignKey.CASCADE))
public class EventMedia {
    @PrimaryKey(autoGenerate = true) public int mediaId;
    @ColumnInfo(name = "event_id", index = true) public int eventId;
    public String url;
    public String type; // "Image", "Video"
}