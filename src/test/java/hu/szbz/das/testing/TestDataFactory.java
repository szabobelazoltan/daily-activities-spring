package hu.szbz.das.testing;

import hu.szbz.das.persistence.model.ActivityEntity;
import hu.szbz.das.persistence.model.CalendarOwnerEntity;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public interface TestDataFactory {
    static OffsetDateTime offsetDateTime(int year, int month, int day, int hour, int minute) {
        return LocalDateTime.of(year, month, day, hour, minute)
                .atZone(ZoneOffset.systemDefault())
                .toOffsetDateTime();
    }

    static CalendarOwnerEntity calendarOwnerEntity(int id, String name) {
        CalendarOwnerEntity entity = new CalendarOwnerEntity();
        entity.setId(id);
        entity.setName(name);
        return entity;
    }

    static ActivityEntity createActivityEntity(
            long id,
            String title,
            String description,
            LocalDateTime start,
            LocalDateTime end) {
        ActivityEntity entity = new ActivityEntity();
        entity.setId(id);
        entity.setTitle(title);
        entity.setDescription(description);
        entity.setStartDateTime(start.atZone(ZoneOffset.systemDefault()).toOffsetDateTime());
        entity.setStartDateTime(end.atZone(ZoneOffset.systemDefault()).toOffsetDateTime());
        return entity;
    }
}
