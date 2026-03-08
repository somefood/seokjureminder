package seokju.ai.seokjureminder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import seokju.ai.seokjureminder.domain.ReminderList;

import java.util.List;

public interface ReminderListRepository extends JpaRepository<ReminderList, Long> {

    @Query("SELECT rl, COUNT(r) FROM ReminderList rl LEFT JOIN Reminder r ON r.list = rl AND r.isDone = false GROUP BY rl")
    List<Object[]> findAllWithCount();
}
