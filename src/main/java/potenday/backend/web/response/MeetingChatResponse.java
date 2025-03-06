package potenday.backend.web.response;

import potenday.backend.application.dto.ChatResponse;

public record MeetingChatResponse(
    String answer,
    String sessionId
) {

    public static MeetingChatResponse from(ChatResponse chatResponse) {
        return new MeetingChatResponse(chatResponse.answer(), chatResponse.sessionId());
    }

}
