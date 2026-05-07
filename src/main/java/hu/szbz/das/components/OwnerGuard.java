package hu.szbz.das.components;

import hu.szbz.das.errors.DailyActivitiesException;
import hu.szbz.das.errors.ErrorCode;
import hu.szbz.das.persistence.model.CalendarOwnerEntity;
import hu.szbz.das.persistence.repositories.CalendarOwnerEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OwnerGuard {
    @Autowired
    private CalendarOwnerEntityRepository repository;

    public CalendarOwnerEntity get(String name) {
        return repository.findByName(name).orElseThrow(() -> new DailyActivitiesException(String.format("Owner does not exist with name: %s!", name), ErrorCode.UNKNOWN_CALENDAR_OWNER));
    }

    public CalendarOwnerEntity getOrCreate(String name) {
        var registeredOwner = repository.findByName(name);
        if (registeredOwner.isPresent()) {
            return registeredOwner.get();
        } else {
            var newOwner = new CalendarOwnerEntity();
            newOwner.setName(name);
            return repository.save(newOwner);
        }
    }
}
