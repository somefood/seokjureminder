package seokju.ai.seokjureminder.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Reminder 단위 테스트")
class ReminderTest {

    @Nested
    @DisplayName("생성자 (Builder)")
    class BuilderTest {

        @Test
        @DisplayName("title과 note로 Reminder를 생성할 수 있다")
        void createWithTitleAndNote() {
            Reminder reminder = Reminder.builder()
                    .title("장보기")
                    .note("우유, 계란, 빵")
                    .build();

            assertThat(reminder.getTitle()).isEqualTo("장보기");
            assertThat(reminder.getNote()).isEqualTo("우유, 계란, 빵");
            assertThat(reminder.getIsDone()).isFalse();
        }

        @Test
        @DisplayName("note 없이 title만으로 Reminder를 생성할 수 있다")
        void createWithTitleOnly() {
            Reminder reminder = Reminder.builder()
                    .title("운동하기")
                    .build();

            assertThat(reminder.getTitle()).isEqualTo("운동하기");
            assertThat(reminder.getNote()).isNull();
            assertThat(reminder.getIsDone()).isFalse();
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTest {

        @Test
        @DisplayName("title과 note를 수정할 수 있다")
        void updateTitleAndNote() {
            Reminder reminder = Reminder.builder()
                    .title("원래 제목")
                    .note("원래 메모")
                    .build();

            reminder.update("새 제목", "새 메모", null, null, null);

            assertThat(reminder.getTitle()).isEqualTo("새 제목");
            assertThat(reminder.getNote()).isEqualTo("새 메모");
        }

        @Test
        @DisplayName("null을 전달하면 기존 값이 유지된다")
        void updateWithNullKeepsExistingValue() {
            Reminder reminder = Reminder.builder()
                    .title("원래 제목")
                    .note("원래 메모")
                    .build();

            reminder.update(null, null, null, null, null);

            assertThat(reminder.getTitle()).isEqualTo("원래 제목");
            assertThat(reminder.getNote()).isEqualTo("원래 메모");
        }

        @Test
        @DisplayName("title만 수정하면 note는 유지된다")
        void updateTitleOnly() {
            Reminder reminder = Reminder.builder()
                    .title("원래 제목")
                    .note("원래 메모")
                    .build();

            reminder.update("새 제목", null, null, null, null);

            assertThat(reminder.getTitle()).isEqualTo("새 제목");
            assertThat(reminder.getNote()).isEqualTo("원래 메모");
        }
    }

    @Nested
    @DisplayName("toggleDone()")
    class ToggleDoneTest {

        @Test
        @DisplayName("완료되지 않은 Reminder를 완료로 변경할 수 있다")
        void toggleToComplete() {
            Reminder reminder = Reminder.builder().title("할 일").build();

            reminder.toggleDone();

            assertThat(reminder.getIsDone()).isTrue();
        }

        @Test
        @DisplayName("완료된 Reminder를 미완료로 되돌릴 수 있다")
        void toggleBackToIncomplete() {
            Reminder reminder = Reminder.builder().title("할 일").build();
            reminder.toggleDone();
            reminder.toggleDone();

            assertThat(reminder.getIsDone()).isFalse();
        }
    }

}
