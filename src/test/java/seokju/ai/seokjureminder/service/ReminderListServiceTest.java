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
import seokju.ai.seokjureminder.domain.ReminderList;
import seokju.ai.seokjureminder.dto.ReminderListRequest;
import seokju.ai.seokjureminder.dto.ReminderListResponse;
import seokju.ai.seokjureminder.repository.ReminderListRepository;
import seokju.ai.seokjureminder.repository.ReminderRepository;
import seokju.ai.seokjureminder.service.ports.in.ReminderListService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@DisplayName("ReminderListService 통합 테스트")
class ReminderListServiceTest {

    @Autowired
    ReminderListService reminderListService;

    @Autowired
    ReminderListRepository reminderListRepository;

    @Autowired
    ReminderRepository reminderRepository;

    @BeforeEach
    void setUp() {
        reminderRepository.deleteAll();
        reminderListRepository.deleteAll();
    }

    @Nested
    @DisplayName("findAll()")
    class FindAll {

        @Test
        @DisplayName("전체 목록을 반환한다")
        void returnsAllLists() {
            reminderListRepository.save(ReminderList.builder().name("개인").build());
            reminderListRepository.save(ReminderList.builder().name("업무").build());

            List<ReminderListResponse> result = reminderListService.findAll();

            assertThat(result).hasSize(2);
            assertThat(result).extracting(ReminderListResponse::name)
                    .containsExactlyInAnyOrder("개인", "업무");
        }

        @Test
        @DisplayName("목록이 없으면 빈 목록을 반환한다")
        void returnsEmptyList() {
            List<ReminderListResponse> result = reminderListService.findAll();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("미완료 리마인더 수를 함께 반환한다")
        void returnsCountOfUndoneReminders() {
            ReminderList list = reminderListRepository.save(ReminderList.builder().name("개인").build());
            reminderRepository.save(Reminder.builder().title("할 일1").list(list).build());
            reminderRepository.save(Reminder.builder().title("할 일2").list(list).build());
            Reminder done = Reminder.builder().title("완료").list(list).build();
            done.toggleDone();
            reminderRepository.save(done);

            List<ReminderListResponse> result = reminderListService.findAll();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).count()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("존재하는 id로 조회하면 목록을 반환한다")
        void returnsListWhenExists() {
            ReminderList saved = reminderListRepository.save(ReminderList.builder().name("개인").color("#FF0000").build());

            ReminderListResponse result = reminderListService.findById(saved.getId());

            assertThat(result.name()).isEqualTo("개인");
            assertThat(result.color()).isEqualTo("#FF0000");
        }

        @Test
        @DisplayName("존재하지 않는 id로 조회하면 EntityNotFoundException을 던진다")
        void throwsWhenNotFound() {
            assertThatThrownBy(() -> reminderListService.findById(99999L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("99999");
        }
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("목록을 생성하고 반환한다")
        void createsList() {
            ReminderListResponse result = reminderListService.create(
                    new ReminderListRequest("개인", "#FF0000", "person"));

            assertThat(result.id()).isNotNull();
            assertThat(result.name()).isEqualTo("개인");
            assertThat(result.color()).isEqualTo("#FF0000");
            assertThat(result.icon()).isEqualTo("person");
            assertThat(result.count()).isZero();
        }

        @Test
        @DisplayName("color를 지정하지 않으면 기본값 #007AFF이 사용된다")
        void usesDefaultColor() {
            ReminderListResponse result = reminderListService.create(
                    new ReminderListRequest("업무", null, null));

            assertThat(result.color()).isEqualTo("#007AFF");
        }
    }

    @Nested
    @DisplayName("update()")
    class Update {

        @Test
        @DisplayName("목록 정보를 수정하고 반환한다")
        void updatesList() {
            ReminderList saved = reminderListRepository.save(ReminderList.builder().name("원래 이름").build());

            ReminderListResponse result = reminderListService.update(saved.getId(),
                    new ReminderListRequest("새 이름", "#00FF00", "star"));

            assertThat(result.name()).isEqualTo("새 이름");
            assertThat(result.color()).isEqualTo("#00FF00");
            assertThat(result.icon()).isEqualTo("star");
        }

        @Test
        @DisplayName("존재하지 않는 id 수정 시 EntityNotFoundException을 던진다")
        void throwsWhenNotFound() {
            assertThatThrownBy(() -> reminderListService.update(99999L,
                    new ReminderListRequest("이름", null, null)))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("목록을 삭제한다")
        void deletesList() {
            ReminderList saved = reminderListRepository.save(ReminderList.builder().name("삭제할 목록").build());

            reminderListService.delete(saved.getId());

            assertThat(reminderListRepository.findById(saved.getId())).isEmpty();
        }

        @Test
        @DisplayName("목록 삭제 시 연결된 리마인더의 list가 null로 해제된다")
        void unlinksRemindersOnDelete() {
            ReminderList list = reminderListRepository.save(ReminderList.builder().name("목록").build());
            Reminder reminder = reminderRepository.save(Reminder.builder().title("할 일").list(list).build());

            reminderListService.delete(list.getId());

            Reminder updated = reminderRepository.findById(reminder.getId()).orElseThrow();
            assertThat(updated.getList()).isNull();
        }

        @Test
        @DisplayName("존재하지 않는 id 삭제 시 EntityNotFoundException을 던진다")
        void throwsWhenNotFound() {
            assertThatThrownBy(() -> reminderListService.delete(99999L))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }
}
