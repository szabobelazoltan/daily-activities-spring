package hu.szbz.das.components;

import hu.szbz.das.errors.DailyActivitiesException;
import hu.szbz.das.errors.ErrorCode;
import hu.szbz.das.persistence.repositories.CalendarOwnerEntityRepository;
import hu.szbz.das.testing.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OwnerGuardTest {
    @Mock
    private CalendarOwnerEntityRepository repository;

    @InjectMocks
    private OwnerGuard ownerGuard = new OwnerGuard();

    @Test
    void get_whenOwnerIsPresent_returnEntity() {
        var entity = TestDataFactory.calendarOwnerEntity(1, "john.doe");
        when(repository.findByName(entity.getName())).thenReturn(Optional.of(entity));

        var result = ownerGuard.get(entity.getName());

        assertEquals(entity, result);
    }

    @Test
    void get_whenOwnerIsAbsent_throwException() {
        String name = "no.user";
        when(repository.findByName(name)).thenReturn(Optional.empty());

        var ex = assertThrows(DailyActivitiesException.class, () -> ownerGuard.get(name), "Owner does not exist with name: no.user");

        assertEquals(ErrorCode.UNKNOWN_CALENDAR_OWNER, ex.getErrorCode());
    }

    @Test
    void getOrCreate_whenOwnerIsPresent_returnEntityFromDb() {
        var entity = TestDataFactory.calendarOwnerEntity(1, "john.doe");
        when(repository.findByName(entity.getName())).thenReturn(Optional.of(entity));

        var result = ownerGuard.getOrCreate(entity.getName());

        assertEquals(entity, result);
    }

    @Test
    void getOrCreate_whenOwnerIsAbsent_createNewEntityAndReturnIt() {
        String name = "jane.doe";
        when(repository.findByName(name)).thenReturn(Optional.empty());

        when(repository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        var result = ownerGuard.getOrCreate(name);

        assertEquals(name, result.getName());
        verify(repository).save(any());
    }
}
