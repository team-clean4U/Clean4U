package org.example.clean4u.chat;

import lombok.RequiredArgsConstructor;
import org.example.clean4u.employee.Employee;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/chats")
    public String index(Model model, HttpSession session) {
        Employee sessionUser = session != null ? (Employee) session.getAttribute("sessionUser") : null;
        
        List<ChatResponse.DetailDTO> listDTO = chatService.findAll();
        if (sessionUser != null) {
            final String myName = sessionUser.getName();
            listDTO.forEach(dto -> {
                boolean isMine = myName != null && myName.equals(dto.getSender());
                dto.setMine(isMine);
            });
        }
        model.addAttribute("chatList", listDTO);
        model.addAttribute("additionalCss", Arrays.asList("/css/chat.css", "/css/list.css", "/css/base.css"));
        return "chat/index";
    }
}
