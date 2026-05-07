package hu.szbz.das.components;

import hu.szbz.das.api.ActivitiesGet200ResponseInner;
import hu.szbz.das.api.Activity;
import hu.szbz.das.persistence.model.ActivityEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ActivityMapper {

    @Mapping(target = "start", source = "startDateTime")
    @Mapping(target = "end", source = "endDateTime")
    Activity mapToApiModel(ActivityEntity entity);

    List<Activity> mapToApiModels(List<ActivityEntity> entities);

    default List<ActivitiesGet200ResponseInner> mapCalendarResult(Map<LocalDate, List<ActivityEntity>> calendarResult) {
        List<ActivitiesGet200ResponseInner> rpList = new ArrayList<>(calendarResult.size());
        for (Map.Entry<LocalDate, List<ActivityEntity>> calendarEntry : calendarResult.entrySet()) {
            ActivitiesGet200ResponseInner calendarEntryDto = new ActivitiesGet200ResponseInner();
            calendarEntryDto.setDate(calendarEntry.getKey());
            calendarEntryDto.setActivities(mapToApiModels(calendarEntry.getValue()));
        }
        return rpList;
    }
}
