package com.example.midterm.model.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.midterm.model.entity.SeatMap;

import java.util.List;

@Dao
public interface SeatMapDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(SeatMap seatMap);

    @Update
    void update(SeatMap seatMap);

    @Query("SELECT * FROM seat_maps WHERE event_id = :eventId ORDER BY name ASC")
    LiveData<List<SeatMap>> getSeatMapsByEventId(int eventId);

    @Query("SELECT * FROM seat_maps WHERE mapId = :mapId LIMIT 1")
    LiveData<SeatMap> getSeatMapById(int mapId);

    @Query("DELETE FROM seat_maps WHERE mapId = :mapId")
    void deleteSeatMapById(int mapId);

}