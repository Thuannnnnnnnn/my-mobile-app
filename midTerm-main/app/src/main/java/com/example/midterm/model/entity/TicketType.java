package com.example.midterm.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "ticket_types",
        foreignKeys = @ForeignKey(
                entity = Event.class,
                parentColumns = "eventId",
                childColumns = "event_id",
                onDelete = ForeignKey.CASCADE // Xóa sự kiện thì xóa luôn loại vé
        ))
public class TicketType implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int ticketTypeId;

    @ColumnInfo(name = "event_id", index = true)
    public int eventId;

    public String name;         // "VIP Zone A", "Standard"
    public double price;        // Giá tiền
    
    @ColumnInfo(name = "total_quantity")
    public int totalQuantity;   // Tổng số vé bán ra

    @ColumnInfo(name = "sold_quantity")
    public int soldQuantity = 0; // Số vé đã bán

    public TicketType() {}
}