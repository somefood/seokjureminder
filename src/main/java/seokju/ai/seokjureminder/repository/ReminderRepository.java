package seokju.ai.seokjureminder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import seokju.ai.seokjureminder.domain.Reminder;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {}
