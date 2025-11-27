package com.example.midterm.model.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.example.midterm.model.data.local.AppDatabase;
import com.example.midterm.model.data.local.EventDAO;
import com.example.midterm.model.entity.Event;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventRepository {
    private final EventDAO eventDAO;
    private final ExecutorService executor;
    private final Handler handler;

    public EventRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        this.eventDAO = db.eventDAO(); // Đảm bảo bạn đã khai báo eventDAO() trong AppDatabase
        this.executor = Executors.newSingleThreadExecutor();
        this.handler = new Handler(Looper.getMainLooper());
    }

    public interface DataCallback<T> {
        void onDataLoaded(T data);
        void onError(String e);
    }

    // --- LẤY DANH SÁCH SỰ KIỆN ---
    public void getAllEvents(DataCallback<List<Event>> callback) {
        executor.execute(() -> {
            try {
                List<Event> events = eventDAO.getAllEvents();
                handler.post(() -> callback.onDataLoaded(events));
            } catch (Exception e) {
                handler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    // --- TẠO SỰ KIỆN MỚI ---
    public void createEvent(Event event, DataCallback<Boolean> callback) {
        executor.execute(() -> {
            try {
                eventDAO.insertEvent(event);
                handler.post(() -> callback.onDataLoaded(true));
            } catch (Exception e) {
                handler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }
}