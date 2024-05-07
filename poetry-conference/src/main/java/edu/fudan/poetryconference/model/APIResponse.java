package edu.fudan.poetryconference.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class APIResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;

    // Getter and Setters
    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public static class Choice {
        private int index;
        private Message message;
        private String finishReason;

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        public static class Message {
            private String role;
            private String content;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }
        }
    }
}
