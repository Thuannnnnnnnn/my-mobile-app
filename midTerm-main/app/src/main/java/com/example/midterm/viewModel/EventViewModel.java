package com.example.midterm.viewModel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.midterm.model.entity.Event;
import com.example.midterm.model.repository.EventRepository;

import java.util.List;

public class EventViewModel extends AndroidViewModel {
    private final EventRepository repository;
    private final MutableLiveData<List<Event>> eventList = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public EventViewModel(@NonNull Application application) {
        super(application);
        repository = new EventRepository(application);
        loadEvents(); // Tự động load khi khởi tạo
    }

    public LiveData<List<Event>> getEventList() {
        return eventList;
    }

    public void loadEvents() {
        repository.getAllEvents(new EventRepository.DataCallback<List<Event>>() {
            @Override
            public void onDataLoaded(List<Event> data) {
                eventList.setValue(data);
            }

            @Override
            public void onError(String e) {
                errorMessage.setValue(e);
            }
        });
    }
    
    // Hàm tạo event mẫu để test (nếu cần)
    public void createSampleEvent() {
        Event event = new Event();
        event.title = "Sự kiện Test Mới";
        event.location = "Online";
        event.startTime = System.currentTimeMillis();
        event.status = "Published";
        
        repository.createEvent(event, new EventRepository.DataCallback<Boolean>() {
            @Override
            public void onDataLoaded(Boolean data) {
                loadEvents(); // Reload lại list sau khi thêm
            }
            @Override
            public void onError(String e) {}
        });
    }
}