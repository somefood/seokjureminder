package seokju.ai.seokjureminder.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import seokju.ai.seokjureminder.dto.ReminderListRequest;
import seokju.ai.seokjureminder.dto.ReminderListResponse;
import seokju.ai.seokjureminder.service.ports.in.ReminderListService;

import java.util.List;

@RestController
@RequestMapping("/api/lists")
@RequiredArgsConstructor
public class ReminderListController {

    private final ReminderListService reminderListService;

    @GetMapping
    public List<ReminderListResponse> getLists() {
        return reminderListService.findAll();
    }

    @GetMapping("/{id}")
    public ReminderListResponse getList(@PathVariable Long id) {
        return reminderListService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReminderListResponse createList(@Valid @RequestBody ReminderListRequest request) {
        return reminderListService.create(request);
    }

    @PatchMapping("/{id}")
    public ReminderListResponse updateList(@PathVariable Long id,
                                           @Valid @RequestBody ReminderListRequest request) {
        return reminderListService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteList(@PathVariable Long id) {
        reminderListService.delete(id);
    }
}
