package com.example.midterm.view.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.midterm.R;
import com.example.midterm.model.entity.User; // Sử dụng User mới

import java.util.ArrayList;
import java.util.List;

public class GuestAdapter extends RecyclerView.Adapter<GuestAdapter.GuestViewHolder> {

    // Thay đổi từ List<Guest> thành List<User>
    private List<User> guestList = new ArrayList<>();
    private OnGuestClickListener listener;

    public interface OnGuestClickListener {
        void onGuestClick(User user);
    }

    public void setOnGuestClickListener(OnGuestClickListener listener) {
        this.listener = listener;
    }

    public void setGuests(List<User> guests) {
        this.guestList = guests;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Đảm bảo bạn vẫn giữ file layout item_guest.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_guest, parent, false);
        return new GuestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GuestViewHolder holder, int position) {
        User user = guestList.get(position);

        // Hiển thị thông tin từ User
        holder.tvName.setText(user.fullName);
        holder.tvEmail.setText(user.email);

        // Nếu layout có hiển thị số điện thoại
        if (holder.tvPhone != null) {
            holder.tvPhone.setText(user.phoneNumber);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGuestClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return guestList != null ? guestList.size() : 0;
    }

    static class GuestViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvPhone;

        public GuestViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ ID từ file item_guest.xml
            // Bạn cần kiểm tra lại ID trong file XML để khớp với code này
            tvName = itemView.findViewById(R.id.tvGuestName);
            tvEmail = itemView.findViewById(R.id.tvGuestEmail);
            tvPhone = itemView.findViewById(R.id.tvGuestPhone);
        }
    }
}