package seokju.ai.seokjureminder.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reminder_lists")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReminderList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color = "#007AFF";

    private String icon;

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
    public ReminderList(String name, String color, String icon) {
        this.name = name;
        this.color = color != null ? color : "#007AFF";
        this.icon = icon;
    }

    public void update(String name, String color, String icon) {
        if (name != null) this.name = name;
        if (color != null) this.color = color;
        if (icon != null) this.icon = icon;
    }
}
