package com.example.midterm.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "tickets",
        foreignKeys = {
            @ForeignKey(entity = Order.class, parentColumns = "orderId", childColumns = "order_id", onDelete = ForeignKey.CASCADE),
            @ForeignKey(entity = TicketType.class, parentColumns = "ticketTypeId", childColumns = "ticket_type_id"),
            @ForeignKey(entity = Seat.class, parentColumns = "seatId", childColumns = "seat_id")
        })
public class Ticket implements Serializable {
    // ID vé dùng chuỗi UUID để làm mã QR luôn cho tiện
    @PrimaryKey
    @androidx.annotation.NonNull
    public String ticketId; 

    @ColumnInfo(name = "order_id", index = true)
    public int orderId;

    @ColumnInfo(name = "ticket_type_id", index = true)
    public int ticketTypeId;

    @ColumnInfo(name = "seat_id", index = true)
    public Integer seatId; // Có thể null nếu vé đứng

    public String qrCode;  // Lưu chuỗi mã hóa QR
    public String status;  // "Unused", "CheckedIn"

    public Ticket() {}
}