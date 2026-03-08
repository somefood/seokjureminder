package seokju.ai.seokjureminder.service.ports.in;

import seokju.ai.seokjureminder.dto.ReminderListRequest;
import seokju.ai.seokjureminder.dto.ReminderListResponse;

import java.util.List;

public interface ReminderListService {

    List<ReminderListResponse> findAll();

    ReminderListResponse findById(Long id);

    ReminderListResponse create(ReminderListRequest request);

    ReminderListResponse update(Long id, ReminderListRequest request);

    void delete(Long id);
}
