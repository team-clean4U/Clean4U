package org.example.clean4u.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/chats")
    public String index(Model model) {
        List<ChatResponse.DetailDTO> listDTO = chatService.findAll();
        model.addAttribute("chatList", listDTO);
        model.addAttribute("additionalCss", Arrays.asList("/css/chat.css", "/css/list.css", "/css/base.css"));
        return "chat/index";
    }

}
