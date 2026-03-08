package seokju.ai.seokjureminder.dto;

import jakarta.validation.constraints.NotBlank;

public record ReminderListRequest(
        @NotBlank(message = "목록 이름은 필수입니다") String name,
        String color,
        String icon
) {}
