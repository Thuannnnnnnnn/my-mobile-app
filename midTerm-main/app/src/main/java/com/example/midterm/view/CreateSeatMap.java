package com.example.midterm.view;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.midterm.R;
import com.example.midterm.model.entity.Seat;
import com.example.midterm.model.entity.SeatMap;
import com.example.midterm.model.entity.TicketType;
import com.example.midterm.view.Adapter.SeatGridAdapter;
import com.example.midterm.view.Adapter.TicketPaletteAdapter;
import com.example.midterm.viewModel.EventViewModel;
import com.example.midterm.viewModel.SeatViewModel;
import com.example.midterm.viewModel.TicketTypeViewModel;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreateSeatMap extends AppCompatActivity implements TicketPaletteAdapter.OnPaletteClickListener {
    private ImageButton btnBack;
    private Button btnSaveMap;
    private AutoCompleteTextView actvSectionSelector;
    private RecyclerView rvTicketPalette, rvSeatGrid;
    private TextView tvStandingSectionNote;
    private View rootView;

    private EventViewModel eventViewModel;

    private TicketTypeViewModel ticketTypeViewModel;
    private SeatViewModel seatViewModel;

    private ArrayAdapter<String> sectionSpinnerAdapter;
    private TicketPaletteAdapter paletteAdapter;
    private SeatGridAdapter gridAdapter;

    private int currentEventId = -1;
    private List<SeatMap> seatMaps = new ArrayList<>();
    private List<TicketType> ticketTypes = new ArrayList<>();

    private SeatMap currentSelectedMap;
    private TicketType currentSelectedTicketType;
    private Map<Integer, List<Seat>> seatMapCache = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_seat_map);

        initViews();

        currentEventId = getIntent().getIntExtra("event_id", -1);
        if (currentEventId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy sự kiện", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViewModels();
        setupPaletteRecyclerView();
        setupGridRecyclerView();
        setupListeners();
        observeViewModels();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        rootView = findViewById(R.id.main);
        btnBack = findViewById(R.id.btn_back);
        btnSaveMap = findViewById(R.id.btn_save_map);
        actvSectionSelector = findViewById(R.id.actv_section_selector);
        rvTicketPalette = findViewById(R.id.rv_ticket_palette);
        rvSeatGrid = findViewById(R.id.rv_seat_grid);
        tvStandingSectionNote = findViewById(R.id.tv_standing_section_note);
    }

    private void initViewModels() {
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        ticketTypeViewModel = new ViewModelProvider(this).get(TicketTypeViewModel.class);
        seatViewModel = new ViewModelProvider(this).get(SeatViewModel.class);

    }

    private void setupPaletteRecyclerView() {
        paletteAdapter = new TicketPaletteAdapter(this, this);
        rvTicketPalette.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvTicketPalette.setAdapter(paletteAdapter);
    }

    private void setupGridRecyclerView() {
        gridAdapter = new SeatGridAdapter(this, new ArrayList<>(), (seat, position) -> {

            if (currentSelectedTicketType == null) {
                showSnackbar("Vui lòng chọn một loại vé trước.", true);
                return;
            }
            int newTicketTypeId = currentSelectedTicketType.ticketTypeId;
            Integer currentSeatTicketTypeId = seat.seatId;



            gridAdapter.updateSeat(position, seat);
        });
        rvSeatGrid.setAdapter(gridAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> showCancelConfirmationDialog());
        btnSaveMap.setOnClickListener(v -> saveSeatMapAndFinish());

        actvSectionSelector.setOnItemClickListener((parent, view, position, id) -> {
            currentSelectedMap = seatMaps.get(position);
            loadSeatGridForMap(currentSelectedMap);
        });
    }

    private void loadSeatGridForMap(SeatMap map) {
        if (map == null) return;

        int rows = 0;
        int cols = 0;
        try {

            JSONObject json = new JSONObject(map.layoutData);
            rows = json.optInt("rows", 10);
            cols = json.optInt("cols", 10);
        } catch (JSONException e) {
            e.printStackTrace();
            rows = 10; cols = 10;
        }

        rvSeatGrid.setVisibility(View.VISIBLE);
        tvStandingSectionNote.setVisibility(View.GONE);

        rvSeatGrid.setLayoutManager(new GridLayoutManager(this, cols));

        if (seatMapCache.containsKey(map.mapId)) {
            gridAdapter.setSeats(seatMapCache.get(map.mapId));
            return;
        }

        int finalRows = rows;
        int finalCols = cols;
        seatViewModel.getSeatsByMapId(map.mapId).observe(this, existingSeats -> {
            if (currentSelectedMap == null || currentSelectedMap.mapId != map.mapId) return;

            List<Seat> displaySeats = renderGrid(map.mapId, finalRows, finalCols, existingSeats);
            seatMapCache.put(map.mapId, displaySeats);
            gridAdapter.setSeats(displaySeats);
        });
    }

    private List<Seat> renderGrid(int mapId, int rows, int cols, List<Seat> existingSeats) {
        List<Seat> displaySeats = new ArrayList<>();

        for (int r = 0; r < rows; r++) {
            String rowName = String.valueOf((char) ('A' + r));
            for (int c = 0; c < cols; c++) {
                String colName = String.valueOf(c + 1);

                Seat foundSeat = null;

                if (existingSeats != null) {
                    for (Seat existing : existingSeats) {
                        if (rowName.equals(existing.row) && colName.equals(existing.number)) {
                            foundSeat = existing;
                            break;
                        }
                    }
                }

                if (foundSeat != null) {
                    displaySeats.add(foundSeat);
                } else {

                    Seat newSeat = new Seat();
                    newSeat.mapId = mapId;
                    newSeat.row = rowName;
                    newSeat.number = colName;
                    newSeat.status = "Available";
                    displaySeats.add(newSeat);
                }
            }
        }
        return displaySeats;
    }

    private void observeViewModels() {



        ticketTypeViewModel.getTicketTypes(currentEventId).observe(this, types -> {
            this.ticketTypes = types;
            paletteAdapter.setTicketTypes(types);
            gridAdapter.setTicketTypes(this.ticketTypes);
        });
    }

    @Override
    public void onPaletteClick(TicketType ticketType, int position) {
        currentSelectedTicketType = ticketType;
        paletteAdapter.setSelectedPosition(position);
    }

    private void saveSeatMapAndFinish() {
        List<Seat> allSeatsToSave = new ArrayList<>();

        for (List<Seat> mapSeats : seatMapCache.values()) {
            for (Seat seat : mapSeats) {

                allSeatsToSave.add(seat);
            }
        }

        if (allSeatsToSave.isEmpty()) {
            showSnackbar("Chưa có ghế nào được thiết lập", true);
            return;
        }

        seatViewModel.insertSeats(allSeatsToSave);

        showSnackbar("Lưu sơ đồ thành công!", false);

        Intent intent = new Intent(this, HomepageOrganizer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }


    private void deleteEventAndFinish() {

        eventViewModel.deleteEvent(currentEventId);

        showSnackbar("Đã hủy tạo sự kiện.", true);
        Intent intent = new Intent(this, HomepageOrganizer.class);
        startActivity(intent);
        finish();
    }

    private void showCancelConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Hủy tạo sự kiện?")
                .setMessage("Dữ liệu sự kiện đang tạo sẽ bị mất hoàn toàn.")
                .setPositiveButton("Xóa & Thoát", (dialog, which) -> deleteEventAndFinish())
                .setNegativeButton("Ở lại", null)
                .show();
    }

    private void showSnackbar(String message, boolean isError) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
    }
}