package seokju.ai.seokjureminder.service.ports.in;

import seokju.ai.seokjureminder.dto.ReminderRequest;
import seokju.ai.seokjureminder.dto.ReminderResponse;

import java.util.List;

public interface ReminderService {

    List<ReminderResponse> findAll(Long listId, String view, String q);

    ReminderResponse findById(Long id);

    ReminderResponse create(ReminderRequest request);

    ReminderResponse update(Long id, ReminderRequest request);

    ReminderResponse toggleDone(Long id);

    void delete(Long id);
}
