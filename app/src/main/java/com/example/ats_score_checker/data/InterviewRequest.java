package com.example.ats_score_checker.data;

import java.util.List;

public class InterviewRequest {
    private List<ChatMessage> messages;
    private String job_role;

    public InterviewRequest(List<ChatMessage> messages, String job_role) {
        this.messages = messages;
        this.job_role = job_role;
    }

    public List<ChatMessage> getMessages() { return messages; }
    public String getJobRole() { return job_role; }

    public static class ChatMessage {
        private String role;
        private String content;

        public ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() { return role; }
        public String getContent() { return content; }
    }
}
