package com.example.midterm.model.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RewriteQueriesToDropUnusedColumns;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.midterm.model.entity.Event;
import com.example.midterm.model.entity.relations.EventWithGuests;
import com.example.midterm.model.entity.relations.EventWithTicketTypes;

import java.util.List;

@Dao
public interface EventDAO {
    @Insert
    long insert(Event event);
    @Update
    void update(Event event);
    @Delete
    void delete(Event event);

    @Query("SELECT * FROM events WHERE organizer_id = :organizerId ORDER BY created_at DESC")
    LiveData<List<Event>> getEventsByOrganizerId(long organizerId);

    @Query("SELECT * FROM events WHERE organizer_id = :organizerId AND end_date >= datetime('now', 'localtime') ORDER BY end_date ASC")
    LiveData<List<Event>> getActiveEventsByOrganizer(long organizerId);

    @Query("SELECT * FROM events WHERE organizer_id = :organizerId AND end_date < datetime('now', 'localtime') ORDER BY end_date DESC")
    LiveData<List<Event>> getPastEventsByOrganizer(long organizerId);

    @Query("DELETE FROM events WHERE id = :eventId")
    void deleteEventById(long eventId);

    @Transaction
    @Query("SELECT * FROM events WHERE id = :eventId")
    LiveData<EventWithTicketTypes> getEventWithTickets(int eventId);

    @Transaction
    @Query("SELECT * FROM events WHERE id = :eventId")
    LiveData<EventWithGuests> getEventWithGuests(int eventId);


    /// Phía User
    @Query("SELECT bannerUrl FROM events WHERE bannerUrl IS NOT NULL AND bannerUrl != '' AND end_date >= datetime('now', 'localtime') LIMIT 5")
    LiveData<List<String>> getBannerUrls();

    @Query("SELECT DISTINCT genre FROM events WHERE genre IS NOT NULL AND genre != ''")
    LiveData<List<String>> getAllGenres();

    @Query("SELECT * FROM events WHERE end_date >= datetime('now', 'localtime') ORDER BY start_date ASC LIMIT 10")
    LiveData<List<Event>> getUpcomingEvents();

    // ===== STATISTICS QUERIES FOR ORGANIZER =====

    // Lấy sự kiện bằng ID
    @Query("SELECT * FROM events WHERE id = :eventId")
    LiveData<Event> getEventById(int eventId);

    // Get event by ID (non-LiveData for sync operations)
    @Query("SELECT * FROM events WHERE id = :eventId")
    Event getEventByIdSync(int eventId);

    // Tổng event của organizer
    @Query("SELECT COUNT(*) FROM events WHERE organizer_id = :organizerId")
    LiveData<Integer> getTotalEventCount(int organizerId);

    // Tông event đang dien ra của organizer
    @Query("SELECT COUNT(*) FROM events WHERE organizer_id = :organizerId AND end_date >= datetime('now', 'localtime')")
    LiveData<Integer> getActiveEventCount(int organizerId);

    // Tông event đã kết thúc của organizer
    @Query("SELECT COUNT(*) FROM events WHERE organizer_id = :organizerId AND end_date < datetime('now', 'localtime')")
    LiveData<Integer> getPastEventCount(int organizerId);

    // Lấy tổng số vé đã bán của 1 event
    @Query("SELECT COALESCE(SUM(tt.sold_quantity), 0) FROM ticket_types tt WHERE tt.event_id = :eventId")
    LiveData<Integer> getTotalTicketsSoldForEvent(int eventId);

    // Lấy doanh thu của 1 event
    @Query("SELECT COALESCE(SUM(tt.price * tt.sold_quantity), 0) FROM ticket_types tt WHERE tt.event_id = :eventId")
    LiveData<Double> getTotalRevenueForEvent(int eventId);

    // Lấy sức chua 1 event
    @Query("SELECT COALESCE(SUM(tt.quantity), 0) FROM ticket_types tt WHERE tt.event_id = :eventId")
    LiveData<Integer> getTotalCapacityForEvent(int eventId);

    // Get all events with search and filter
    @Query("SELECT * FROM events WHERE organizer_id = :organizerId " +
           "AND (event_name LIKE '%' || :searchQuery || '%' OR location LIKE '%' || :searchQuery || '%') " +
           "ORDER BY created_at DESC")
    LiveData<List<Event>> searchEventsByOrganizer(int organizerId, String searchQuery);

    // Lấy event theo thể loai (genre)
    @Query("SELECT * FROM events WHERE organizer_id = :organizerId AND genre = :genre ORDER BY created_at DESC")
    LiveData<List<Event>> getEventsByGenre(int organizerId, String genre);

    // Lấy event theo ngày
    @Query("SELECT * FROM events WHERE organizer_id = :organizerId " +
           "AND start_date >= :startDate AND end_date <= :endDate ORDER BY start_date ASC")
    LiveData<List<Event>> getEventsByDateRange(int organizerId, String startDate, String endDate);

    // Get draft events (events with no tickets created yet)
    @Query("SELECT e.* FROM events e LEFT JOIN ticket_types tt ON e.id = tt.event_id " +
           "WHERE e.organizer_id = :organizerId GROUP BY e.id HAVING COUNT(tt.id) = 0")
    LiveData<List<Event>> getDraftEvents(int organizerId);

    // lấy tổng doanh thu của 1 organizer (all events)
    @Query("SELECT COALESCE(SUM(tt.price * tt.sold_quantity), 0) FROM ticket_types tt " +
           "INNER JOIN events e ON tt.event_id = e.id WHERE e.organizer_id = :organizerId")
    LiveData<Double> getTotalRevenueByOrganizer(int organizerId);

    // Get total tickets sold by organizer (all events)
    @Query("SELECT COALESCE(SUM(tt.sold_quantity), 0) FROM ticket_types tt " +
           "INNER JOIN events e ON tt.event_id = e.id WHERE e.organizer_id = :organizerId")
    LiveData<Integer> getTotalTicketsSoldByOrganizer(int organizerId);

    // Get events sorted by popularity (sold tickets)
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT e.*, COALESCE(SUM(tt.sold_quantity), 0) as total_sold FROM events e " +
           "LEFT JOIN ticket_types tt ON e.id = tt.event_id " +
           "WHERE e.organizer_id = :organizerId GROUP BY e.id ORDER BY total_sold DESC")
    LiveData<List<Event>> getEventsSortedByPopularity(int organizerId);

    // ===== USER SEARCH QUERIES =====

    // Search events by name, location, or description for users
    @Query("SELECT * FROM events WHERE end_date >= datetime('now', 'localtime') " +
           "AND (event_name LIKE '%' || :query || '%' OR location LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%') " +
           "ORDER BY start_date ASC")
    LiveData<List<Event>> searchEvents(String query);

    // Get events by genre for users
    @Query("SELECT * FROM events WHERE end_date >= datetime('now', 'localtime') AND genre = :genre ORDER BY start_date ASC")
    LiveData<List<Event>> getEventsByGenreForUser(String genre);

    // Get events by city/location for users
    @Query("SELECT * FROM events WHERE end_date >= datetime('now', 'localtime') AND location LIKE '%' || :city || '%' ORDER BY start_date ASC")
    LiveData<List<Event>> getEventsByCity(String city);

    // Get events by date range for users
    @Query("SELECT * FROM events WHERE start_date >= :startDate AND start_date <= :endDate ORDER BY start_date ASC")
    LiveData<List<Event>> getEventsByDateRangeForUser(String startDate, String endDate);

    // Get distinct cities from events
    @Query("SELECT DISTINCT location FROM events WHERE location IS NOT NULL AND location != '' AND end_date >= datetime('now', 'localtime')")
    LiveData<List<String>> getAllCities();

    // Get popular/hot events (by sold tickets)
    @Query("SELECT e.* FROM events e " +
           "LEFT JOIN ticket_types tt ON e.id = tt.event_id " +
           "WHERE e.end_date >= datetime('now', 'localtime') " +
           "GROUP BY e.id ORDER BY COALESCE(SUM(tt.sold_quantity), 0) DESC LIMIT 10")
    LiveData<List<Event>> getHotEvents();

    // Combined search with filters
    @Query("SELECT * FROM events WHERE end_date >= datetime('now', 'localtime') " +
           "AND (:query IS NULL OR event_name LIKE '%' || :query || '%' OR location LIKE '%' || :query || '%') " +
           "AND (:genre IS NULL OR genre = :genre) " +
           "AND (:city IS NULL OR location LIKE '%' || :city || '%') " +
           "ORDER BY start_date ASC")
    LiveData<List<Event>> searchEventsWithFilters(String query, String genre, String city);
}

