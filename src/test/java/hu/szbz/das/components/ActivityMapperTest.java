package hu.szbz.das.components;

import hu.szbz.das.api.ActivitiesGet200ResponseInner;
import hu.szbz.das.api.Activity;
import hu.szbz.das.persistence.model.ActivityEntity;
import hu.szbz.das.persistence.model.ActivityStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static hu.szbz.das.testing.TestDataFactory.createActivityEntity;
import static org.junit.jupiter.api.Assertions.*;

public class ActivityMapperTest {
    private final ActivityMapper mapper = new ActivityMapperImpl();

    @Test
    void mapToApiModel() {
        ActivityEntity entity = createActivityEntity(
                1L,
                "Dummy",
                "This is a test",
                LocalDateTime.of(2026, 4, 10, 9, 40),
                LocalDateTime.of(2026, 4, 10, 10, 0)
        );
        entity.setStatus(ActivityStatus.COMPLETED);

        Activity dto = mapper.mapToApiModel(entity);

        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getTitle(), dto.getTitle());
        assertEquals(entity.getDescription(), dto.getDescription());
        assertEquals(entity.getStartDateTime(), dto.getStart());
        assertEquals(entity.getEndDateTime(), dto.getEnd());
        assertEquals(entity.getStatus().name(), dto.getStatus().name());
    }

    @Test
    void mapToApiModels() {
        ActivityEntity entity = createActivityEntity(
                1L,
                "Dummy",
                "This is a test",
                LocalDateTime.of(2026, 4, 10, 9, 40),
                LocalDateTime.of(2026, 4, 10, 10, 0)
        );
        entity.setStatus(ActivityStatus.COMPLETED);

        List<Activity> dtos = mapper.mapToApiModels(List.of(entity));

        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        assertEquals(entity.getId(), dtos.get(0).getId());
    }

    @Test
    void mapCalendarResult() {
        ActivityEntity entity1 = createActivityEntity(
                1L,
                "Dummy 1",
                "This is a test",
                LocalDateTime.of(2026, 4, 10, 12, 0),
                LocalDateTime.of(2026, 4, 10, 12, 30)
        );
        entity1.setStatus(ActivityStatus.SCHEDULED);
        ActivityEntity entity2 = createActivityEntity(
                2L,
                "Dummy 2",
                "This is a test",
                LocalDateTime.of(2026, 4, 16, 14, 0),
                LocalDateTime.of(2026, 4, 16, 14, 30)
        );
        entity2.setStatus(ActivityStatus.CANCELLED);

        Map<LocalDate, List<ActivityEntity>> calendarResult = Map.of(
                entity1.getStartDateTime().toLocalDate(), List.of(entity1),
                entity2.getStartDateTime().toLocalDate(), List.of(entity2)
        );

        List<ActivitiesGet200ResponseInner> dtos = mapper.mapCalendarResult(calendarResult);
        assertNotNull(dtos);
        assertEquals(calendarResult.size(), dtos.size());
        assertEntries(calendarResult, dtos);
    }

    private void assertEntries(Map<LocalDate, List<ActivityEntity>> calendarResult, List<ActivitiesGet200ResponseInner> dtos) {
        for (ActivitiesGet200ResponseInner dto : dtos) {
            var key = dto.getDate();
            assertTrue(calendarResult.containsKey(key));

            List<ActivityEntity> entities = calendarResult.get(key);
            List<Activity> items = dto.getActivities();
            assertEquals(entities.size(), items.size());
            for (int i = 0; i < entities.size(); i++) {
                assertEquals(entities.get(0).getId(), items.get(0).getId());
            }
        }
    }
}
