package seokju.ai.seokjureminder.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "reminders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(nullable = false)
    private Boolean isDone = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id")
    private ReminderList list;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.NONE;

    private LocalDate dueDate;

    private LocalTime dueTime;

    private LocalDateTime completedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Builder
    public Reminder(String title, String note, ReminderList list, LocalDate dueDate, LocalTime dueTime, Priority priority) {
        this.title = title;
        this.note = note;
        this.list = list;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.priority = priority != null ? priority : Priority.NONE;
        this.isDone = false;
    }

    public void update(String title, String note, LocalDate dueDate, LocalTime dueTime, Priority priority) {
        if (title != null) this.title = title;
        if (note != null) this.note = note;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        if (priority != null) this.priority = priority;
    }

    public void assignList(ReminderList list) {
        this.list = list;
    }

    public void toggleDone() {
        this.isDone = !this.isDone;
        this.completedAt = this.isDone ? LocalDateTime.now() : null;
    }
}
