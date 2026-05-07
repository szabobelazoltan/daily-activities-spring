package hu.szbz.das.services;

import hu.szbz.das.api.NewActivity;
import hu.szbz.das.components.OwnerGuard;
import hu.szbz.das.errors.DailyActivitiesException;
import hu.szbz.das.errors.ErrorCode;
import hu.szbz.das.persistence.model.ActivityEntity;
import hu.szbz.das.persistence.model.ActivityStatus;
import hu.szbz.das.persistence.repositories.ActivityEntityRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Service
public class DailyActivitiesService {
    private final OwnerGuard ownerGuard;
    private final ActivityEntityRepository activityEntityRepository;

    @Autowired
    public DailyActivitiesService(OwnerGuard ownerGuard, ActivityEntityRepository activityEntityRepository) {
        this.ownerGuard = ownerGuard;
        this.activityEntityRepository = activityEntityRepository;
    }

    @Transactional
    public ActivityEntity addActivity(String calendarOwnerName, NewActivity params) {
        var calendarOwner = ownerGuard.getOrCreate(calendarOwnerName);
        var existingEntry = activityEntityRepository.findByUserAndBounds(calendarOwner, params.getStart());
        if (existingEntry.isPresent()) {
            throw new DailyActivitiesException(String.format("An activity already exists in the given time window with id: %d!", existingEntry.get().getId()), ErrorCode.CALENDER_COLLISION);
        }

        var activity = new ActivityEntity();
        activity.setCalendarOwner(calendarOwner);
        activity.setTitle(params.getTitle());
        activity.setDescription(params.getDescription());
        activity.setStartDateTime(params.getStart());
        activity.setEndDateTime(params.getEnd());
        activity.setStatus(ActivityStatus.SCHEDULED);
        return activityEntityRepository.save(activity);
    }

    @Transactional
    public ActivityEntity cancelActivity(String calendarOwnerName, Long activityId) {
        return updateActivityStatus(calendarOwnerName, activityId, ActivityStatus.CANCELLED);
    }

    @Transactional
    public ActivityEntity completeActivity(String calendarOwnerName, Long activityId) {
        return updateActivityStatus(calendarOwnerName, activityId, ActivityStatus.COMPLETED);
    }

    public Map<LocalDate, List<ActivityEntity>> getActivities(String calendarOwnerName, OffsetDateTime start, OffsetDateTime end) {
        var calendarOwner = ownerGuard.get(calendarOwnerName);
        var grouping = new ActivityResultGrouping(168);
        activityEntityRepository
                .findAllByUserAndStartBetween(calendarOwner, start, end)
                .forEach(grouping);
        return grouping.get();
    }

    private ActivityEntity updateActivityStatus(String calendarOwnerName, Long activityId, ActivityStatus newStatus) {
        var calendarOwner = ownerGuard.get(calendarOwnerName);
        var activity = activityEntityRepository.checkedFindById(activityId);
        if (!calendarOwner.equals(activity.getCalendarOwner())) {
            throw new DailyActivitiesException("Given user is not owner of the activity!", ErrorCode.ACCESS_DENIED);
        }
        if (!ActivityStatus.SCHEDULED.equals(activity.getStatus())) {
            throw new DailyActivitiesException("Activity is already closed!", ErrorCode.ACTIVITY_NOT_FOUND);
        }
        activity.setStatus(newStatus);
        return activity;
    }

    private class ActivityResultGrouping implements Consumer<ActivityEntity> {
        private final Map<LocalDate, List<ActivityEntity>> resultMap;

        ActivityResultGrouping(int capacity) {
            this.resultMap = new HashMap<>(capacity);
        }

        @Override
        public void accept(ActivityEntity activity) {
            LocalDate groupBy = activity.getStartDateTime().toLocalDate();
            this.resultMap.putIfAbsent(groupBy, new ArrayList<>()).add(activity);
        }

        Map<LocalDate, List<ActivityEntity>> get() {
            return this.resultMap;
        }
    }
}
