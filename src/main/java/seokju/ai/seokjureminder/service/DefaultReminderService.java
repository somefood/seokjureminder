package seokju.ai.seokjureminder.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seokju.ai.seokjureminder.domain.Reminder;
import seokju.ai.seokjureminder.dto.ReminderRequest;
import seokju.ai.seokjureminder.dto.ReminderResponse;
import seokju.ai.seokjureminder.service.ports.in.ReminderService;
import seokju.ai.seokjureminder.repository.ReminderRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultReminderService implements ReminderService {

    private final ReminderRepository reminderRepository;

    @Override
    public List<ReminderResponse> findAll() {
        return reminderRepository.findAll().stream()
                .map(ReminderResponse::from)
                .toList();
    }

    @Override
    public ReminderResponse findById(Long id) {
        return ReminderResponse.from(getReminder(id));
    }

    @Override
    @Transactional
    public ReminderResponse create(ReminderRequest request) {
        Reminder reminder = Reminder.builder()
                .title(request.title())
                .note(request.note())
                .build();
        return ReminderResponse.from(reminderRepository.save(reminder));
    }

    @Override
    @Transactional
    public ReminderResponse update(Long id, ReminderRequest request) {
        Reminder reminder = getReminder(id);
        reminder.update(request.title(), request.note());
        return ReminderResponse.from(reminder);
    }

    @Override
    @Transactional
    public ReminderResponse toggleDone(Long id) {
        Reminder reminder = getReminder(id);
        reminder.toggleDone();
        return ReminderResponse.from(reminder);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Reminder reminder = getReminder(id);
        reminderRepository.delete(reminder);
    }

    private Reminder getReminder(Long id) {
        return reminderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reminder not found: " + id));
    }
}
