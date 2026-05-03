package hu.szbz.das.persistence.repositories;

import hu.szbz.das.persistence.model.CalendarOwnerEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql("/sql/ActivityEntityRepositoryTest.sql")
public class ActivityEntityRepositoryTest {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ActivityEntityRepository repository;

    @Test
    void findAllByUserAndStartBetween() {
        CalendarOwnerEntity calendarOwner = entityManager.find(CalendarOwnerEntity.class, -2);

        OffsetDateTime lowerBound = LocalDate.of(2026, 4, 15)
                .atStartOfDay(ZoneOffset.systemDefault())
                .toOffsetDateTime();
        OffsetDateTime upperBound = LocalDate.of(2026, 4, 18)
                .atStartOfDay(ZoneOffset.systemDefault())
                .toOffsetDateTime();

        var result = repository.findAllByUserAndStartBetween(calendarOwner, lowerBound, upperBound);

        assertNotNull(result);
        assertEquals(6, result.size());
        assertEquals(-1, result.get(0).getId());
        assertEquals(-2, result.get(1).getId());
        assertEquals(-3, result.get(2).getId());
        assertEquals(-4, result.get(3).getId());
        assertEquals(-5, result.get(4).getId());
        assertEquals(-6, result.get(5).getId());
    }

    @Test
    void findByUserAndBounds_whenActivityIsPresent_thenOptionalContainsIt() {
        CalendarOwnerEntity calendarOwner = entityManager.find(CalendarOwnerEntity.class, -2);

        OffsetDateTime designatedDateTime = LocalDate.of(2026, 4, 14)
                .atTime(9, 15)
                .atZone(ZoneOffset.systemDefault())
                .toOffsetDateTime();

        var result = repository.findByUserAndBounds(calendarOwner, designatedDateTime);

        assertTrue(result.isPresent());
        assertEquals(-7, result.get().getId());
    }
}
