package hu.szbz.das.endpoints.rest;

import hu.szbz.das.api.ActivitiesApi;
import hu.szbz.das.api.ActivitiesGet200ResponseInner;
import hu.szbz.das.api.Activity;
import hu.szbz.das.api.NewActivity;
import hu.szbz.das.components.ActivityMapper;
import hu.szbz.das.services.DailyActivitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
public class ApiController implements ActivitiesApi {
    private final DailyActivitiesService service;
    private final ActivityMapper mapper;
    private final NativeWebRequest webRequest;

    @Autowired
    public ApiController(DailyActivitiesService service, ActivityMapper mapper, NativeWebRequest webRequest) {
        this.service = service;
        this.mapper = mapper;
        this.webRequest = webRequest;
    }

    @Override
    public ResponseEntity<Activity> activitiesActivityIdCompletePut(Long activityId) {
        var activity = service.completeActivity(getCurrentUserName(), activityId);
        return ResponseEntity.ok(mapper.mapToApiModel(activity));
    }

    @Override
    public ResponseEntity<Void> activitiesActivityIdDelete(Long activityId) {
        service.cancelActivity(getCurrentUserName(), activityId);
        return ResponseEntity.ok(null);
    }

    @Override
    public ResponseEntity<List<ActivitiesGet200ResponseInner>> activitiesGet(OffsetDateTime start, OffsetDateTime end) {
        var calendarResult = service.getActivities(getCurrentUserName(), start, end);
        return ResponseEntity.ok(mapper.mapCalendarResult(calendarResult));
    }

    @Override
    public ResponseEntity<Activity> activitiesPost(NewActivity newActivity) {
        var createdActivity = service.addActivity(getCurrentUserName(), newActivity);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.mapToApiModel(createdActivity));
    }

    private String getCurrentUserName() {
        return webRequest.getUserPrincipal().getName();
    }
}
