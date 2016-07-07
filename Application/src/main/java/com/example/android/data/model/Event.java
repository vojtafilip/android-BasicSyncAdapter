package com.example.android.data.model;

import java.util.Date;

public class Event {
    private String id;
    private String color;
    private Date startDate;
    private Date endDate;
    private boolean isAllDay;
    private String name;

    public String getId() {
        return id;
    }

    public String getColor() {
        return color;
    }

    public Date getStart() {
//        return start;
        return startDate;
    }

    public Date getEnd() {
//        return end;
        return endDate;
    }

    public boolean isAllDay() {
        return isAllDay;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", color='" + color + '\'' +
                ", start=" + startDate +
                ", end=" + endDate +
                ", isAllDay=" + isAllDay +
                ", name='" + name + '\'' +
                '}';
    }
}
