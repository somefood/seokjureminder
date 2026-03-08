package seokju.ai.seokjureminder.dto;

import jakarta.validation.constraints.NotBlank;

public record ReminderRequest(
        @NotBlank(message = "제목은 필수입니다") String title,
        String note
) {}
