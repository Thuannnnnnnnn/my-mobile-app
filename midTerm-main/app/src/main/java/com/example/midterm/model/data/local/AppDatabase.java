package com.example.midterm.model.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// Import các DAO
import com.example.midterm.model.data.local.EventDAO;
import com.example.midterm.model.data.local.SeatMapDAO; // Thêm import mới

// Import các Entity
import com.example.midterm.model.entity.User;
import com.example.midterm.model.entity.MembershipTier;
import com.example.midterm.model.entity.Category;
import com.example.midterm.model.entity.Talent;
import com.example.midterm.model.entity.Event;
import com.example.midterm.model.entity.relations.EventTalentCrossRef;
import com.example.midterm.model.entity.TicketType;
import com.example.midterm.model.entity.SeatMap;
import com.example.midterm.model.entity.Seat;
import com.example.midterm.model.entity.PromoCode;
import com.example.midterm.model.entity.Order;
import com.example.midterm.model.entity.Ticket;
import com.example.midterm.model.entity.EventMedia;
import com.example.midterm.model.entity.Review;
import com.example.midterm.model.entity.CheckinLog;
import com.example.midterm.model.entity.Notification;


@Database(entities = {
        // Nhóm User
        User.class, MembershipTier.class, Notification.class,
        // Nhóm Sự kiện
        Category.class, Talent.class, Event.class, EventTalentCrossRef.class, EventMedia.class,
        // Nhóm Vé
        TicketType.class, SeatMap.class, Seat.class,
        // Nhóm Giao dịch (MỚI THÊM)
        PromoCode.class, Order.class, Ticket.class, Review.class, CheckinLog.class
}, version = 5, exportSchema = false) 
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    // Khai báo DAO
    public abstract UserDAO userDAO();
    public abstract EventDAO eventDAO(); // DAO cho sự kiện
    public abstract SeatMapDAO seatMapDAO(); // Thêm phương thức cho SeatMapDAO

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                        AppDatabase.class, "event_app_database_v2") // Đổi tên DB để tạo mới sạch sẽ
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}