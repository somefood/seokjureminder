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
import seokju.ai.seokjureminder.domain.ReminderList;
import seokju.ai.seokjureminder.repository.ReminderListRepository;
import seokju.ai.seokjureminder.repository.ReminderRepository;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@DisplayName("ReminderListController 통합 테스트")
class ReminderListControllerTest {

    @Autowired WebApplicationContext wac;
    @Autowired ReminderListRepository reminderListRepository;
    @Autowired ReminderRepository reminderRepository;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        reminderRepository.deleteAll();
        reminderListRepository.deleteAll();
    }

    @Nested
    @DisplayName("GET /api/lists")
    class GetLists {

        @Test
        @DisplayName("목록을 200으로 반환한다")
        void returnsLists() throws Exception {
            reminderListRepository.save(ReminderList.builder().name("개인").build());
            reminderListRepository.save(ReminderList.builder().name("업무").build());

            mockMvc.perform(get("/api/lists"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[*].name", containsInAnyOrder("개인", "업무")));
        }

        @Test
        @DisplayName("목록이 없으면 빈 배열을 반환한다")
        void returnsEmptyList() throws Exception {
            mockMvc.perform(get("/api/lists"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/lists/{id}")
    class GetList {

        @Test
        @DisplayName("존재하는 id로 조회하면 200과 목록을 반환한다")
        void returnsList() throws Exception {
            ReminderList saved = reminderListRepository.save(ReminderList.builder().name("개인").color("#FF0000").build());

            mockMvc.perform(get("/api/lists/{id}", saved.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("개인"))
                    .andExpect(jsonPath("$.color").value("#FF0000"))
                    .andExpect(jsonPath("$.count").value(0));
        }

        @Test
        @DisplayName("존재하지 않는 id로 조회하면 404를 반환한다")
        void returns404WhenNotFound() throws Exception {
            mockMvc.perform(get("/api/lists/99999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @DisplayName("POST /api/lists")
    class CreateList {

        @Test
        @DisplayName("유효한 요청으로 목록을 생성하면 201을 반환한다")
        void createsList() throws Exception {
            String body = """
                    {"name": "개인", "color": "#FF0000", "icon": "person"}
                    """;

            mockMvc.perform(post("/api/lists")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNumber())
                    .andExpect(jsonPath("$.name").value("개인"))
                    .andExpect(jsonPath("$.color").value("#FF0000"))
                    .andExpect(jsonPath("$.icon").value("person"))
                    .andExpect(jsonPath("$.count").value(0));
        }

        @Test
        @DisplayName("name이 빈 문자열이면 400을 반환한다")
        void returns400WhenNameBlank() throws Exception {
            String body = """
                    {"name": ""}
                    """;

            mockMvc.perform(post("/api/lists")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));
        }
    }

    @Nested
    @DisplayName("PATCH /api/lists/{id}")
    class UpdateList {

        @Test
        @DisplayName("목록을 수정하면 200과 수정된 내용을 반환한다")
        void updatesList() throws Exception {
            ReminderList saved = reminderListRepository.save(ReminderList.builder().name("원래 이름").build());
            String body = """
                    {"name": "새 이름", "color": "#00FF00"}
                    """;

            mockMvc.perform(patch("/api/lists/{id}", saved.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("새 이름"))
                    .andExpect(jsonPath("$.color").value("#00FF00"));
        }

        @Test
        @DisplayName("존재하지 않는 id 수정 시 404를 반환한다")
        void returns404WhenNotFound() throws Exception {
            String body = """
                    {"name": "이름"}
                    """;

            mockMvc.perform(patch("/api/lists/99999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/lists/{id}")
    class DeleteList {

        @Test
        @DisplayName("목록 삭제 시 204를 반환한다")
        void deletesList() throws Exception {
            ReminderList saved = reminderListRepository.save(ReminderList.builder().name("삭제할 목록").build());

            mockMvc.perform(delete("/api/lists/{id}", saved.getId()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("존재하지 않는 id 삭제 시 404를 반환한다")
        void returns404WhenNotFound() throws Exception {
            mockMvc.perform(delete("/api/lists/99999"))
                    .andExpect(status().isNotFound());
        }
    }
}
