package seokju.ai.seokjureminder.dto;

import seokju.ai.seokjureminder.domain.ReminderList;

public record ReminderListResponse(
        Long id,
        String name,
        String color,
        String icon,
        long count
) {
    public static ReminderListResponse from(ReminderList list, long count) {
        return new ReminderListResponse(
                list.getId(),
                list.getName(),
                list.getColor(),
                list.getIcon(),
                count
        );
    }
}
