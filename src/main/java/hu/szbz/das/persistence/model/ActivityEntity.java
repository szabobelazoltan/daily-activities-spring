package hu.szbz.das.persistence.model;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "ACTIVITY")
public class ActivityEntity {
    private static final String SEQUENCE_NAME = "SEQ_USER_ID";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME)
    private Long id;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "START_DT")
    private OffsetDateTime startDateTime;

    @Column(name = "END_DT")
    private OffsetDateTime endDateTime;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private ActivityStatus status;

    @ManyToOne
    @JoinColumn(name = "CALOWNER_ID", referencedColumnName = "ID")
    private CalendarOwnerEntity calendarOwner;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OffsetDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(OffsetDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public OffsetDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(OffsetDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public ActivityStatus getStatus() {
        return status;
    }

    public void setStatus(ActivityStatus status) {
        this.status = status;
    }

    public CalendarOwnerEntity getCalendarOwner() {
        return calendarOwner;
    }

    public void setCalendarOwner(CalendarOwnerEntity calendarOwner) {
        this.calendarOwner = calendarOwner;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ActivityEntity that = (ActivityEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(startDateTime, that.startDateTime) && Objects.equals(calendarOwner, that.calendarOwner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, startDateTime, calendarOwner);
    }

    @Override
    public String toString() {
        return "ActivityEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", status=" + status +
                ", user=" + calendarOwner +
                '}';
    }
}
