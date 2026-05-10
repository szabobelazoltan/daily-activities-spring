package hu.szbz.das.services;

import hu.szbz.das.api.NewActivity;
import hu.szbz.das.components.OwnerGuard;
import hu.szbz.das.errors.DailyActivitiesException;
import hu.szbz.das.errors.ErrorCode;
import hu.szbz.das.persistence.model.ActivityEntity;
import hu.szbz.das.persistence.model.ActivityStatus;
import hu.szbz.das.persistence.model.CalendarOwnerEntity;
import hu.szbz.das.persistence.repositories.ActivityEntityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static hu.szbz.das.testing.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class DailyActivitiesServiceTest {
    @MockitoBean
    private OwnerGuard ownerGuard;

    @MockitoBean
    private ActivityEntityRepository activityEntityRepository;

    @Autowired
    private DailyActivitiesService service;

    @Test
    void addActivity_returnsEntity_whenSuccessfullyCreatesIt() {
        CalendarOwnerEntity owner = calendarOwnerEntity(1, "john.doe");
        when(ownerGuard.getOrCreate(owner.getName())).thenReturn(owner);

        when(activityEntityRepository.findByUserAndBounds(eq(owner), any())).thenReturn(Optional.empty());

        when(activityEntityRepository.save(any())).thenAnswer(p -> p.getArgument(0));

        NewActivity params = new NewActivity();
        params.setTitle("Dummy");
        params.setDescription("Test");
        params.setStart(offsetDateTime(2026, 5, 10, 8, 40));
        params.setEnd(offsetDateTime(2026, 5, 10, 13, 30));

        ActivityEntity result = service.addActivity(owner.getName(), params);

        assertNotNull(result);
        assertEquals(owner, result.getCalendarOwner());
        assertEquals(params.getTitle(), result.getTitle());
        assertEquals(params.getDescription(), result.getDescription());
        assertEquals(params.getStart(), result.getStartDateTime());
        assertEquals(params.getEnd(), result.getEndDateTime());
        assertEquals(ActivityStatus.SCHEDULED, result.getStatus());
        verify(activityEntityRepository).save(any(ActivityEntity.class));
    }

    @Test
    void addActivity_throwsException_whenCollisionDetected() {
        CalendarOwnerEntity owner = calendarOwnerEntity(1, "john.doe");
        when(ownerGuard.getOrCreate(owner.getName())).thenReturn(owner);

        OffsetDateTime start = offsetDateTime(2026, 5, 10, 8, 40);
        ActivityEntity existingEntity = new ActivityEntity();
        existingEntity.setId(50L);
        when(activityEntityRepository.findByUserAndBounds(owner, start)).thenReturn(Optional.of(existingEntity));

        NewActivity params = new NewActivity();
        params.setTitle("Dummy");
        params.setDescription("Test");
        params.setStart(offsetDateTime(2026, 5, 10, 8, 40));
        params.setEnd(offsetDateTime(2026, 5, 10, 13, 30));

        DailyActivitiesException exception = assertThrows(DailyActivitiesException.class, () -> service.addActivity(owner.getName(), params), "An activity already exists in the given time window with id: 51!");
        assertEquals(ErrorCode.CALENDER_COLLISION, exception.getErrorCode());
    }

    @Test
    void cancelActivity_returnsEntity_whenUpdatedIt() {
        CalendarOwnerEntity owner = calendarOwnerEntity(1, "john.doe");
        when(ownerGuard.get(owner.getName())).thenReturn(owner);

        ActivityEntity entity = createActivityEntity(
                1L,
                "Dummy 1",
                "This is a test",
                LocalDateTime.of(2026, 4, 10, 12, 0),
                LocalDateTime.of(2026, 4, 10, 12, 30)
        );
        entity.setCalendarOwner(owner);
        entity.setStatus(ActivityStatus.SCHEDULED);
        when(activityEntityRepository.checkedFindById(entity.getId())).thenReturn(entity);

        ActivityEntity result = service.cancelActivity(owner.getName(), entity.getId());
        assertEquals(entity, result);
        assertEquals(ActivityStatus.CANCELLED, entity.getStatus());
    }

    @Test
    void cancelActivity_throwsException_whenOwnershipFails() {
        CalendarOwnerEntity owner = calendarOwnerEntity(1, "john.doe");
        when(ownerGuard.get(owner.getName())).thenReturn(owner);

        CalendarOwnerEntity trueOwner = calendarOwnerEntity(2, "true.owner");
        ActivityEntity entity = createActivityEntity(
                1L,
                "Dummy 1",
                "This is a test",
                LocalDateTime.of(2026, 4, 10, 12, 0),
                LocalDateTime.of(2026, 4, 10, 12, 30)
        );
        entity.setCalendarOwner(trueOwner);
        entity.setStatus(ActivityStatus.SCHEDULED);
        when(activityEntityRepository.checkedFindById(entity.getId())).thenReturn(entity);

        DailyActivitiesException exception = assertThrows(DailyActivitiesException.class, () -> service.cancelActivity(owner.getName(), entity.getId()), "Given user is not owner of the activity!");
        assertEquals(ErrorCode.ACCESS_DENIED, exception.getErrorCode());
    }

    @Test
    void cancelActivity_throwsException_whenActivityIsAlreadyClosed() {
        CalendarOwnerEntity owner = calendarOwnerEntity(1, "john.doe");
        when(ownerGuard.get(owner.getName())).thenReturn(owner);

        ActivityEntity entity = createActivityEntity(
                1L,
                "Dummy 1",
                "This is a test",
                LocalDateTime.of(2026, 4, 10, 12, 0),
                LocalDateTime.of(2026, 4, 10, 12, 30)
        );
        entity.setCalendarOwner(owner);
        entity.setStatus(ActivityStatus.CANCELLED);
        when(activityEntityRepository.checkedFindById(entity.getId())).thenReturn(entity);

        DailyActivitiesException exception = assertThrows(DailyActivitiesException.class, () -> service.cancelActivity(owner.getName(), entity.getId()), "Activity is already closed!");
        assertEquals(ErrorCode.CLOSED_ACTIVITY, exception.getErrorCode());
    }

    @Test
    void completeActivity_returnsEntity_whenUpdatedIt() {
        CalendarOwnerEntity owner = calendarOwnerEntity(1, "john.doe");
        when(ownerGuard.get(owner.getName())).thenReturn(owner);

        ActivityEntity entity = createActivityEntity(
                1L,
                "Dummy 1",
                "This is a test",
                LocalDateTime.of(2026, 4, 10, 12, 0),
                LocalDateTime.of(2026, 4, 10, 12, 30)
        );
        entity.setCalendarOwner(owner);
        entity.setStatus(ActivityStatus.SCHEDULED);
        when(activityEntityRepository.checkedFindById(entity.getId())).thenReturn(entity);

        ActivityEntity result = service.completeActivity(owner.getName(), entity.getId());
        assertEquals(entity, result);
        assertEquals(ActivityStatus.COMPLETED, entity.getStatus());
    }

    @Test
    void getActivities_returnMapOfMatches() {
        CalendarOwnerEntity owner = calendarOwnerEntity(1, "john.doe");
        when(ownerGuard.get(owner.getName())).thenReturn(owner);

        ActivityEntity entity1 = createActivityEntity(
                1L,
                "Dummy 1",
                "This is a test",
                LocalDateTime.of(2026, 4, 10, 12, 0),
                LocalDateTime.of(2026, 4, 10, 12, 30)
        );
        ActivityEntity entity2 = createActivityEntity(
                2L,
                "Dummy 2",
                "This is a test",
                LocalDateTime.of(2026, 4, 11, 12, 0),
                LocalDateTime.of(2026, 4, 11, 12, 30)
        );
        when(activityEntityRepository.findAllByUserAndStartBetween(owner, entity1.getStartDateTime(), entity2.getEndDateTime())).thenReturn(List.of(entity1, entity2));

        Map<LocalDate, List<ActivityEntity>> result = service.getActivities(owner.getName(), entity1.getStartDateTime(), entity2.getEndDateTime());
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsKey(entity1.getStartDateTime().toLocalDate()));
        assertTrue(result.containsKey(entity2.getStartDateTime().toLocalDate()));

        List<ActivityEntity> list1 = result.get(entity1.getStartDateTime().toLocalDate());
        assertEquals(1, list1.size());
        assertEquals(entity1, list1.get(0));

        List<ActivityEntity> list2 = result.get(entity2.getStartDateTime().toLocalDate());
        assertEquals(1, list2.size());
        assertEquals(entity2, list2.get(0));
    }
}
