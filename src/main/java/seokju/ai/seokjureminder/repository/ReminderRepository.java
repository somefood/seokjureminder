package seokju.ai.seokjureminder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import seokju.ai.seokjureminder.domain.Reminder;
import seokju.ai.seokjureminder.domain.ReminderList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findByList(ReminderList list);

    List<Reminder> findByListId(Long listId);

    long countByListAndIsDoneFalse(ReminderList list);

    // 오늘 마감 (미완료)
    List<Reminder> findByIsDoneFalseAndDueDate(LocalDate dueDate);

    // 날짜가 있는 미완료 (예정), 날짜 오름차순
    List<Reminder> findByIsDoneFalseAndDueDateIsNotNullOrderByDueDateAscDueTimeAsc();

    // 전체 미완료
    List<Reminder> findByIsDoneFalse();

    // 완료 (최근 30일)
    List<Reminder> findByIsDoneTrueAndCompletedAtAfter(LocalDateTime since);

    // title/note 검색 (미완료)
    @Query("SELECT r FROM Reminder r WHERE r.isDone = false AND (LOWER(r.title) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(r.note) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<Reminder> searchByTitleOrNote(@Param("q") String q);
}
