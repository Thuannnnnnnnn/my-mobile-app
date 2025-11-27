package com.example.midterm.model.entity;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "checkin_logs",
        foreignKeys = {
            @ForeignKey(entity = Ticket.class, parentColumns = "ticketId", childColumns = "ticket_id"),
            @ForeignKey(entity = User.class, parentColumns = "userId", childColumns = "staff_id")
        })
public class CheckinLog {
    @PrimaryKey(autoGenerate = true) public int logId;
    @ColumnInfo(name = "ticket_id", index = true) public String ticketId;
    @ColumnInfo(name = "staff_id", index = true) public int staffId; // Ai quét?
    public long checkinTime;
}