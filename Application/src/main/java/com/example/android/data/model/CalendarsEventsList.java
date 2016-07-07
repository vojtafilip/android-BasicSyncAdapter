package com.example.android.data.model;

import java.util.List;

/**
 * Created by vojta on 7.7.16.
 */
public class CalendarsEventsList {
    private boolean ok;
    private String error;
    private List<Event> events;

    public boolean isOk() {
        return ok;
    }

    public String getError() {
        return error;
    }

    public List<Event> getEvents() {
        return events;
    }

    @Override
    public String toString() {
        return "CalendarsEventsList{" +
                "ok=" + ok +
                ", error='" + error + '\'' +
                ", events=" + events +
                '}';
    }
}
