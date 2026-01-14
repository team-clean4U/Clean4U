package org.example.clean4u.chat;

import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class ChatApiController {
    private final ChatService chatService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect() {
        return chatService.createConnection(UUID.randomUUID().toString());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> sendMessage(@RequestParam(name = "message") String message,
                                                   @RequestParam(name = "employeeId") Long employeeId) {
        chatService.addMessage(message, employeeId);
        return ResponseEntity.ok(ApiResponse.ok("메시지 전송에 성공했습니다."));
    }

}
