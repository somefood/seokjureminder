package seokju.ai.seokjureminder.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import seokju.ai.seokjureminder.domain.Reminder;
import seokju.ai.seokjureminder.dto.ReminderRequest;
import seokju.ai.seokjureminder.dto.ReminderResponse;
import seokju.ai.seokjureminder.service.ports.in.ReminderService;
import seokju.ai.seokjureminder.repository.ReminderRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@DisplayName("ReminderService 통합 테스트")
class ReminderServiceTest {

    @Autowired
    ReminderService reminderService;

    @Autowired
    ReminderRepository reminderRepository;

    @BeforeEach
    void setUp() {
        reminderRepository.deleteAll();
    }

    @Nested
    @DisplayName("findAll()")
    class FindAll {

        @Test
        @DisplayName("전체 리마인더 목록을 반환한다")
        void returnsAllReminders() {
            reminderRepository.save(Reminder.builder().title("장보기").note("우유").build());
            reminderRepository.save(Reminder.builder().title("운동").build());

            List<ReminderResponse> result = reminderService.findAll();

            assertThat(result).hasSize(2);
            assertThat(result).extracting(ReminderResponse::title)
                    .containsExactlyInAnyOrder("장보기", "운동");
        }

        @Test
        @DisplayName("리마인더가 없으면 빈 목록을 반환한다")
        void returnsEmptyListWhenNoReminders() {
            List<ReminderResponse> result = reminderService.findAll();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("존재하는 id로 조회하면 리마인더를 반환한다")
        void returnsReminderWhenExists() {
            Reminder saved = reminderRepository.save(Reminder.builder().title("독서").build());

            ReminderResponse result = reminderService.findById(saved.getId());

            assertThat(result.title()).isEqualTo("독서");
            assertThat(result.isDone()).isFalse();
        }

        @Test
        @DisplayName("존재하지 않는 id로 조회하면 EntityNotFoundException을 던진다")
        void throwsWhenNotFound() {
            assertThatThrownBy(() -> reminderService.findById(99999L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("99999");
        }
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("title과 note로 리마인더를 생성하고 반환한다")
        void createsReminder() {
            ReminderResponse result = reminderService.create(new ReminderRequest("새 할 일", "메모"));

            assertThat(result.id()).isNotNull();
            assertThat(result.title()).isEqualTo("새 할 일");
            assertThat(result.note()).isEqualTo("메모");
            assertThat(result.isDone()).isFalse();
        }

        @Test
        @DisplayName("생성된 리마인더는 DB에 저장된다")
        void savedToDatabase() {
            ReminderResponse result = reminderService.create(new ReminderRequest("저장 확인", null));

            assertThat(reminderRepository.findById(result.id())).isPresent();
        }
    }

    @Nested
    @DisplayName("update()")
    class Update {

        @Test
        @DisplayName("title과 note를 수정하고 반환한다")
        void updatesReminder() {
            Reminder saved = reminderRepository.save(Reminder.builder().title("원래 제목").note("원래 메모").build());

            ReminderResponse result = reminderService.update(saved.getId(), new ReminderRequest("새 제목", "새 메모"));

            assertThat(result.title()).isEqualTo("새 제목");
            assertThat(result.note()).isEqualTo("새 메모");
        }

        @Test
        @DisplayName("존재하지 않는 id 수정 시 EntityNotFoundException을 던진다")
        void throwsWhenNotFound() {
            assertThatThrownBy(() -> reminderService.update(99999L, new ReminderRequest("제목", null)))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("toggleDone()")
    class ToggleDone {

        @Test
        @DisplayName("미완료 리마인더를 완료로 전환한다")
        void togglesToComplete() {
            Reminder saved = reminderRepository.save(Reminder.builder().title("할 일").build());

            ReminderResponse result = reminderService.toggleDone(saved.getId());

            assertThat(result.isDone()).isTrue();
        }

        @Test
        @DisplayName("완료 리마인더를 미완료로 되돌린다")
        void togglesBackToIncomplete() {
            Reminder reminder = Reminder.builder().title("할 일").build();
            reminder.toggleDone();
            Reminder saved = reminderRepository.save(reminder);

            ReminderResponse result = reminderService.toggleDone(saved.getId());

            assertThat(result.isDone()).isFalse();
        }
    }

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("존재하는 리마인더를 삭제한다")
        void deletesReminder() {
            Reminder saved = reminderRepository.save(Reminder.builder().title("삭제할 할 일").build());

            reminderService.delete(saved.getId());

            assertThat(reminderRepository.findById(saved.getId())).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 id 삭제 시 EntityNotFoundException을 던진다")
        void throwsWhenNotFound() {
            assertThatThrownBy(() -> reminderService.delete(99999L))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }
}
