package seokju.ai.seokjureminder.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import seokju.ai.seokjureminder.domain.Reminder;
import seokju.ai.seokjureminder.repository.ReminderRepository;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@DisplayName("ReminderController 통합 테스트")
class ReminderControllerTest {

    @Autowired WebApplicationContext wac;
    @Autowired ObjectMapper objectMapper;
    @Autowired ReminderRepository reminderRepository;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        reminderRepository.deleteAll();
    }

    @Nested
    @DisplayName("GET /api/reminders")
    class GetReminders {

        @Test
        @DisplayName("리마인더 목록을 200으로 반환한다")
        void returnsReminderList() throws Exception {
            reminderRepository.save(Reminder.builder().title("장보기").note("우유").build());
            reminderRepository.save(Reminder.builder().title("운동").build());

            mockMvc.perform(get("/api/reminders"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[*].title", containsInAnyOrder("장보기", "운동")));
        }

        @Test
        @DisplayName("리마인더가 없으면 빈 배열을 반환한다")
        void returnsEmptyList() throws Exception {
            mockMvc.perform(get("/api/reminders"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/reminders/{id}")
    class GetReminder {

        @Test
        @DisplayName("존재하는 id로 조회하면 200과 리마인더를 반환한다")
        void returnsReminder() throws Exception {
            Reminder saved = reminderRepository.save(Reminder.builder().title("독서").build());

            mockMvc.perform(get("/api/reminders/{id}", saved.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("독서"))
                    .andExpect(jsonPath("$.isDone").value(false));
        }

        @Test
        @DisplayName("존재하지 않는 id로 조회하면 404를 반환한다")
        void returns404WhenNotFound() throws Exception {
            mockMvc.perform(get("/api/reminders/99999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @DisplayName("POST /api/reminders")
    class CreateReminder {

        @Test
        @DisplayName("유효한 요청으로 리마인더를 생성하면 201을 반환한다")
        void createsReminder() throws Exception {
            String body = """
                    {"title": "새 할 일", "note": "메모"}
                    """;

            mockMvc.perform(post("/api/reminders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNumber())
                    .andExpect(jsonPath("$.title").value("새 할 일"))
                    .andExpect(jsonPath("$.note").value("메모"))
                    .andExpect(jsonPath("$.isDone").value(false));
        }

        @Test
        @DisplayName("title이 빈 문자열이면 400을 반환한다")
        void returns400WhenTitleBlank() throws Exception {
            String body = """
                    {"title": ""}
                    """;

            mockMvc.perform(post("/api/reminders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));
        }
    }

    @Nested
    @DisplayName("PATCH /api/reminders/{id}")
    class UpdateReminder {

        @Test
        @DisplayName("리마인더를 수정하면 200과 수정된 내용을 반환한다")
        void updatesReminder() throws Exception {
            Reminder saved = reminderRepository.save(Reminder.builder().title("원래 제목").build());
            String body = """
                    {"title": "새 제목", "note": "새 메모"}
                    """;

            mockMvc.perform(patch("/api/reminders/{id}", saved.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("새 제목"))
                    .andExpect(jsonPath("$.note").value("새 메모"));
        }

        @Test
        @DisplayName("존재하지 않는 id 수정 시 404를 반환한다")
        void returns404WhenNotFound() throws Exception {
            String body = """
                    {"title": "제목"}
                    """;

            mockMvc.perform(patch("/api/reminders/99999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PATCH /api/reminders/{id}/done")
    class ToggleDone {

        @Test
        @DisplayName("완료 토글 시 200과 변경된 상태를 반환한다")
        void togglesDone() throws Exception {
            Reminder saved = reminderRepository.save(Reminder.builder().title("할 일").build());

            mockMvc.perform(patch("/api/reminders/{id}/done", saved.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isDone").value(true));
        }
    }

    @Nested
    @DisplayName("DELETE /api/reminders/{id}")
    class DeleteReminder {

        @Test
        @DisplayName("리마인더 삭제 시 204를 반환한다")
        void deletesReminder() throws Exception {
            Reminder saved = reminderRepository.save(Reminder.builder().title("삭제할 것").build());

            mockMvc.perform(delete("/api/reminders/{id}", saved.getId()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("존재하지 않는 id 삭제 시 404를 반환한다")
        void returns404WhenNotFound() throws Exception {
            mockMvc.perform(delete("/api/reminders/99999"))
                    .andExpect(status().isNotFound());
        }
    }
}
