package seokju.ai.seokjureminder.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seokju.ai.seokjureminder.domain.Reminder;
import seokju.ai.seokjureminder.domain.ReminderList;
import seokju.ai.seokjureminder.dto.ReminderRequest;
import seokju.ai.seokjureminder.dto.ReminderResponse;
import seokju.ai.seokjureminder.repository.ReminderListRepository;
import seokju.ai.seokjureminder.repository.ReminderRepository;
import seokju.ai.seokjureminder.service.ports.in.ReminderService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultReminderService implements ReminderService {

    private final ReminderRepository reminderRepository;
    private final ReminderListRepository reminderListRepository;

    @Override
    public List<ReminderResponse> findAll(Long listId) {
        List<Reminder> reminders = listId != null
                ? reminderRepository.findByListId(listId)
                : reminderRepository.findAll();
        return reminders.stream().map(ReminderResponse::from).toList();
    }

    @Override
    public ReminderResponse findById(Long id) {
        return ReminderResponse.from(getReminder(id));
    }

    @Override
    @Transactional
    public ReminderResponse create(ReminderRequest request) {
        ReminderList list = resolveList(request.listId());
        Reminder reminder = Reminder.builder()
                .title(request.title())
                .note(request.note())
                .list(list)
                .build();
        return ReminderResponse.from(reminderRepository.save(reminder));
    }

    @Override
    @Transactional
    public ReminderResponse update(Long id, ReminderRequest request) {
        Reminder reminder = getReminder(id);
        reminder.update(request.title(), request.note());
        if (request.listId() != null) {
            reminder.assignList(resolveList(request.listId()));
        }
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
        reminderRepository.delete(getReminder(id));
    }

    private Reminder getReminder(Long id) {
        return reminderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reminder not found: " + id));
    }

    private ReminderList resolveList(Long listId) {
        if (listId == null) return null;
        return reminderListRepository.findById(listId)
                .orElseThrow(() -> new EntityNotFoundException("ReminderList not found: " + listId));
    }
}
