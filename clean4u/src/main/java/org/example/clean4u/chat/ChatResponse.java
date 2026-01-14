package org.example.clean4u.chat;

import lombok.Data;

public class ChatResponse {
    @Data
    public static class DetailDTO {
        private String message;
        private String sender;

        public DetailDTO(Chat chat) {
            this.message = chat.getMessage();
            this.sender = chat.getEmployee().getName();
        }
    }
}
