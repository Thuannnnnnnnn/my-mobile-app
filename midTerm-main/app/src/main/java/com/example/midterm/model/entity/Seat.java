package com.example.midterm.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "seats",
        foreignKeys = @ForeignKey(entity = SeatMap.class,
                parentColumns = "mapId",
                childColumns = "map_id",
                onDelete = ForeignKey.CASCADE))
public class Seat implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int seatId;

    @ColumnInfo(name = "map_id", index = true)
    public int mapId;

    public String row;      // Hàng "A"
    public String number;   // Số "12"

    // Trạng thái ghế: "Available", "Booked", "Locked"
    public String status;

    public Seat() {}

    public Seat(int mapId, String row, String number) {
        this.mapId = mapId;
        this.row = row;
        this.number = number;
        this.status = "Available";
    }

    // Helper để lấy tên hiển thị: "A-12"
    public String getFullName() {
        return row + "-" + number;
    }
}