package hu.szbz.das.persistence.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Sql("/sql/CalendarOwnerEntityRepositoryTest.sql")
public class CalendarOwnerEntityRepositoryTest {
    @Autowired
    private CalendarOwnerEntityRepository repository;

    @Test
    void findByName_whenUserIsPresent_thenReturnOptionalContainingEntity() {
        var result = repository.findByName("john.doe");

        assertTrue(result.isPresent());
        assertEquals(-1, result.get().getId().intValue());
    }

    @Test
    void findByName_whenUserIsMissing_thenReturnEmptyOptional() {
        var result = repository.findByName("krix.krax");

        assertTrue(result.isEmpty());
    }
}
