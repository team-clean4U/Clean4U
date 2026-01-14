package org.example.clean4u.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.clean4u._core.errors.exception.Exception404;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.employee.EmployeeRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ChatService {
    private final ChatRepository chatRepository;
    private final EmployeeRepository employeeRepository;
    private final ObjectMapper objectMapper;
    private final Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    public List<ChatResponse.DetailDTO> findAll() {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        return chatRepository.findAll(sort)
                .stream()
                .map(ChatResponse.DetailDTO::new)
                .toList();
    }

    public List<ChatResponse.DetailDTO> findRecent(int limit) {
        int size = Math.max(1, Math.min(limit, 200));
        var page = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
        return chatRepository.findAll(page)
                .getContent()
                .stream()
                .sorted((a, b) -> Long.compare(a.getId(), b.getId()))
                .map(ChatResponse.DetailDTO::new)
                .toList();
    }

    @Transactional
    public SseEmitter createConnection(String clientId) {
        SseEmitter emitter = new SseEmitter(10 * 60 * 1000L);
        emitterMap.put(clientId, emitter);

        emitter.onCompletion(() -> emitterMap.remove(clientId));
        emitter.onTimeout(() -> emitterMap.remove(clientId));
        emitter.onError((e) -> emitterMap.remove(clientId));

        try {
            emitter.send(SseEmitter.event().name( "connect").data("연결됨"));
        } catch (IOException e) {
            log.error("초기 전송 실패: ", e);
        }
        return emitter;
    }

    @Transactional
    public void addMessage(String message, Long employeeId) {
         Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new Exception404("해당 직원을 찾을 수 없습니다."));

         Chat chat = Chat.builder()
                .message(message)
                .employee(employee)
                .build();

        chatRepository.save(chat);

        broadcast(message, employee.getId());
    }

    public void broadcast(String message, Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new Exception404("해당 직원을 찾을 수 없습니다."));

        Map<String, String> data = Map.of(
                "sender", employee.getName(),
                "message", message
        );

        emitterMap.forEach((id, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("newMessage")
                        .data(objectMapper.writeValueAsString(data)));
            } catch (IOException e) {
                emitterMap.remove(id);
            }
        });
    }
}
