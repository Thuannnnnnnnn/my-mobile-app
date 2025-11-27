package com.example.midterm.model.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.midterm.model.data.local.AppDatabase;
import com.example.midterm.model.data.local.SeatMapDAO;
import com.example.midterm.model.entity.SeatMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SeatMapRepository {
    private SeatMapDAO seatMapDAO;
    private ExecutorService executorService;

    public SeatMapRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        this.seatMapDAO = db.seatMapDAO();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    // Giả sử bạn đã viết DAO, hàm này sẽ gọi DAO
    // public LiveData<List<SeatMap>> getSeatMapsByEventId(int eventId) {
    //     return seatMapDAO.getSeatMapsByEventId(eventId);
    // }
}