package seokju.ai.seokjureminder.dto;

import seokju.ai.seokjureminder.domain.Priority;
import seokju.ai.seokjureminder.domain.Reminder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ReminderResponse(
        Long id,
        String title,
        String note,
        Boolean isDone,
        Long listId,
        LocalDate dueDate,
        LocalTime dueTime,
        Priority priority,
        LocalDateTime completedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ReminderResponse from(Reminder reminder) {
        return new ReminderResponse(
                reminder.getId(),
                reminder.getTitle(),
                reminder.getNote(),
                reminder.getIsDone(),
                reminder.getList() != null ? reminder.getList().getId() : null,
                reminder.getDueDate(),
                reminder.getDueTime(),
                reminder.getPriority(),
                reminder.getCompletedAt(),
                reminder.getCreatedAt(),
                reminder.getUpdatedAt()
        );
    }
}
