package com.example.midterm.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.midterm.R;
import com.example.midterm.model.data.local.DatabaseSeeder; // Import Seeder
import com.example.midterm.view.Adapter.EventAdapter;
import com.example.midterm.viewModel.EventViewModel;

public class HomepageFragment extends Fragment {

    private EventViewModel eventViewModel;
    private EventAdapter eventAdapter;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);

        // 1. Khởi tạo RecyclerView
        recyclerView = view.findViewById(R.id.rvEvents); // Đảm bảo ID trong xml đúng là rvEvents
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        eventAdapter = new EventAdapter(event -> {
            // Xử lý khi bấm vào sự kiện (Sẽ làm sau)
        });
        recyclerView.setAdapter(eventAdapter);

        // 2. Gọi Seeder để tạo dữ liệu mẫu (Chỉ chạy lần đầu cài app)
        DatabaseSeeder.seed(requireContext());

        // 3. Kết nối ViewModel
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        // 4. Lắng nghe dữ liệu thay đổi
        eventViewModel.getEventList().observe(getViewLifecycleOwner(), events -> {
            if (events != null) {
                eventAdapter.setEvents(events);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload lại dữ liệu mỗi khi quay lại màn hình này
        if(eventViewModel != null) {
            eventViewModel.loadEvents();
        }
    }
}