package seokju.ai.seokjureminder.dto;

import seokju.ai.seokjureminder.domain.Reminder;

import java.time.LocalDateTime;

public record ReminderResponse(
        Long id,
        String title,
        String note,
        Boolean isDone,
        Long listId,
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
                reminder.getCreatedAt(),
                reminder.getUpdatedAt()
        );
    }
}
