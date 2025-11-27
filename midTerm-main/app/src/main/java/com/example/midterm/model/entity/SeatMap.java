package com.example.midterm.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "seat_maps",
        foreignKeys = @ForeignKey(entity = Event.class,
                parentColumns = "eventId",
                childColumns = "event_id",
                onDelete = ForeignKey.CASCADE))
public class SeatMap implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int mapId;

    @ColumnInfo(name = "event_id", index = true)
    public int eventId;

    public String name; // "Tầng 1", "Khán đài A"

    // QUAN TRỌNG: Lưu cấu trúc sơ đồ dưới dạng chuỗi JSON
    // Ví dụ: { "rows": 10, "cols": 12, "aisles": [5] }
    @ColumnInfo(name = "layout_data")
    public String layoutData; 

    public SeatMap() {}
}