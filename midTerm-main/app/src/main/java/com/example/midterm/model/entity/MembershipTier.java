package com.example.midterm.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "membership_tiers")
public class MembershipTier implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int tierId;

    @ColumnInfo(name = "tier_name")
    public String tierName;       // Ví dụ: "Silver", "Gold", "Diamond"

    @ColumnInfo(name = "min_points")
    public int minPoints;         // Điểm tối thiểu: 0, 1000, 5000

    @ColumnInfo(name = "benefits")
    public String benefits;       // Mô tả quyền lợi

    // Constructor rỗng bắt buộc cho Room
    public MembershipTier() {}

    public MembershipTier(String tierName, int minPoints, String benefits) {
        this.tierName = tierName;
        this.minPoints = minPoints;
        this.benefits = benefits;
    }
}