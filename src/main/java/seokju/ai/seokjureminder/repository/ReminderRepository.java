package seokju.ai.seokjureminder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import seokju.ai.seokjureminder.domain.Reminder;
import seokju.ai.seokjureminder.domain.ReminderList;

import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findByList(ReminderList list);

    List<Reminder> findByListId(Long listId);

    long countByListAndIsDoneFalse(ReminderList list);
}
