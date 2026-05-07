package hu.szbz.das.testing;

import hu.szbz.das.persistence.model.CalendarOwnerEntity;

public interface TestDataFactory {

    static CalendarOwnerEntity calendarOwnerEntity(int id, String name) {
        CalendarOwnerEntity entity = new CalendarOwnerEntity();
        entity.setId(id);
        entity.setName(name);
        return entity;
    }
}
