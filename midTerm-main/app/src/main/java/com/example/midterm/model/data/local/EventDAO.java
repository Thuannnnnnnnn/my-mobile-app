package com.example.midterm.model.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.midterm.model.entity.Event;
import com.example.midterm.model.entity.relations.EventTalentCrossRef;

import java.util.List;

@Dao
public interface EventDAO {

    // 1. Thêm sự kiện mới
    @Insert
    long insertEvent(Event event);

    // 2. Thêm mối quan hệ Sự kiện - Nghệ sĩ (Cho bảng trung gian)
    @Insert
    void insertEventTalentRef(EventTalentCrossRef ref);

    // 3. Lấy tất cả sự kiện (Mới nhất lên đầu)
    @Query("SELECT * FROM events ORDER BY start_time DESC")
    List<Event> getAllEvents();

    // 4. Lấy sự kiện theo Danh mục (Ví dụ: Bấm vào icon "Music")
    @Query("SELECT * FROM events WHERE category_id = :categoryId ORDER BY start_time DESC")
    List<Event> getEventsByCategory(int categoryId);

    // 5. Tìm kiếm sự kiện
    @Query("SELECT * FROM events WHERE title LIKE '%' || :query || '%'")
    List<Event> searchEvents(String query);

    // 6. Lấy chi tiết 1 sự kiện
    @Query("SELECT * FROM events WHERE eventId = :eventId")
    Event getEventById(int eventId);
    
    // 7. Cập nhật sự kiện
    @Update
    void updateEvent(Event event);
}