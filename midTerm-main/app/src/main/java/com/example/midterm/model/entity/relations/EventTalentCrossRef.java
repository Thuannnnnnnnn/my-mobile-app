package com.example.midterm.model.entity.relations;

import androidx.room.Entity;

@Entity(tableName = "event_talent_cross_ref",
        primaryKeys = {"eventId", "talentId"})
public class EventTalentCrossRef {
    public int eventId;
    public int talentId;

    public EventTalentCrossRef(int eventId, int talentId) {
        this.eventId = eventId;
        this.talentId = talentId;
    }
}