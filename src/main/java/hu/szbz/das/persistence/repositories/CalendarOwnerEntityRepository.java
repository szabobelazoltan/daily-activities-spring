package hu.szbz.das.persistence.repositories;

import hu.szbz.das.persistence.model.CalendarOwnerEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CalendarOwnerEntityRepository extends CrudRepository<CalendarOwnerEntity, Integer> {

    Optional<CalendarOwnerEntity> findByName(String name);
}
