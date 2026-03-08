package seokju.ai.seokjureminder.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReminderRequest(
        @NotBlank(message = "제목은 필수입니다") String title,
        String note,
        Long listId,
        LocalDate dueDate,
        LocalTime dueTime
) {}
