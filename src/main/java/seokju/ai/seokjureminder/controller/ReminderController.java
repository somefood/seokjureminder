package seokju.ai.seokjureminder.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import seokju.ai.seokjureminder.dto.ReminderRequest;
import seokju.ai.seokjureminder.dto.ReminderResponse;
import seokju.ai.seokjureminder.service.ports.in.ReminderService;

import java.util.List;

@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    @GetMapping
    public List<ReminderResponse> getReminders(
            @RequestParam(required = false) Long listId,
            @RequestParam(required = false) String view,
            @RequestParam(required = false) String q) {
        return reminderService.findAll(listId, view, q);
    }

    @GetMapping("/{id}")
    public ReminderResponse getReminder(@PathVariable Long id) {
        return reminderService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReminderResponse createReminder(@Valid @RequestBody ReminderRequest request) {
        return reminderService.create(request);
    }

    @PatchMapping("/{id}")
    public ReminderResponse updateReminder(@PathVariable Long id,
                                           @Valid @RequestBody ReminderRequest request) {
        return reminderService.update(id, request);
    }

    @PatchMapping("/{id}/done")
    public ReminderResponse toggleDone(@PathVariable Long id) {
        return reminderService.toggleDone(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReminder(@PathVariable Long id) {
        reminderService.delete(id);
    }
}
