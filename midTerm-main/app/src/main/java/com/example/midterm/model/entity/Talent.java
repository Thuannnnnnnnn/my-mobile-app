package com.example.midterm.model.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "talents")
public class Talent implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int talentId;

    public String name;   // "Sơn Tùng MTP", "Dr. Strange"
    public String type;   // "Artist", "Speaker", "Host"
    public String bio;    // Tiểu sử
    public String image_url;

    public Talent() {}

    public Talent(String name, String type, String bio) {
        this.name = name;
        this.type = type;
        this.bio = bio;
    }
}