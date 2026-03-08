package seokju.ai.seokjureminder.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seokju.ai.seokjureminder.domain.ReminderList;
import seokju.ai.seokjureminder.dto.ReminderListRequest;
import seokju.ai.seokjureminder.dto.ReminderListResponse;
import seokju.ai.seokjureminder.repository.ReminderListRepository;
import seokju.ai.seokjureminder.repository.ReminderRepository;
import seokju.ai.seokjureminder.service.ports.in.ReminderListService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultReminderListService implements ReminderListService {

    private final ReminderListRepository reminderListRepository;
    private final ReminderRepository reminderRepository;

    @Override
    public List<ReminderListResponse> findAll() {
        return reminderListRepository.findAllWithCount().stream()
                .map(row -> ReminderListResponse.from((ReminderList) row[0], (Long) row[1]))
                .toList();
    }

    @Override
    public ReminderListResponse findById(Long id) {
        ReminderList list = getList(id);
        long count = reminderRepository.countByListAndIsDoneFalse(list);
        return ReminderListResponse.from(list, count);
    }

    @Override
    @Transactional
    public ReminderListResponse create(ReminderListRequest request) {
        ReminderList list = ReminderList.builder()
                .name(request.name())
                .color(request.color())
                .icon(request.icon())
                .build();
        return ReminderListResponse.from(reminderListRepository.save(list), 0);
    }

    @Override
    @Transactional
    public ReminderListResponse update(Long id, ReminderListRequest request) {
        ReminderList list = getList(id);
        list.update(request.name(), request.color(), request.icon());
        long count = reminderRepository.countByListAndIsDoneFalse(list);
        return ReminderListResponse.from(list, count);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ReminderList list = getList(id);
        reminderRepository.findByList(list).forEach(r -> r.assignList(null));
        reminderListRepository.delete(list);
    }

    private ReminderList getList(Long id) {
        return reminderListRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ReminderList not found: " + id));
    }
}
