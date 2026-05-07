package hu.szbz.das.persistence.repositories;

import hu.szbz.das.errors.DailyActivitiesException;
import hu.szbz.das.errors.ErrorCode;
import hu.szbz.das.persistence.model.ActivityEntity;
import hu.szbz.das.persistence.model.CalendarOwnerEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityEntityRepository extends CrudRepository<ActivityEntity, Long> {

    @Query("SELECT a FROM ActivityEntity a WHERE a.calendarOwner = :user AND a.startDateTime BETWEEN :lower AND :upper ORDER BY a.startDateTime ASC")
    List<ActivityEntity> findAllByUserAndStartBetween(@Param("user") CalendarOwnerEntity calendarOwner, @Param("lower") OffsetDateTime lowerBound, @Param("upper") OffsetDateTime upperBound);

    @Query("SELECT a FROM ActivityEntity a WHERE a.calendarOwner = :user AND a.startDateTime <= :designated AND a.endDateTime > :designated")
    Optional<ActivityEntity> findByUserAndBounds(@Param("user") CalendarOwnerEntity calendarOwner, @Param("designated") OffsetDateTime designated);

    default ActivityEntity checkedFindById(Long id) {
        return findById(id).orElseThrow(() -> new DailyActivitiesException(String.format("Activity was not found with id: %d", id), ErrorCode.ACTIVITY_NOT_FOUND));
    }
}
