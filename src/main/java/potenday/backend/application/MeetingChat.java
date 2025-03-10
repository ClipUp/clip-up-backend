package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import potenday.backend.application.dto.ChatResponse;
import potenday.backend.domain.Meeting;
import potenday.backend.springai.models.clova.ClovaChatOptions;

import java.util.List;
import java.util.Map;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@RequiredArgsConstructor
@Component
class MeetingChat {

    private static final int MAX_TOKENS = 2000;

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final UUIDProvider uuidProvider;
    private final MessageChatMemoryAdvisor chatMemoryAdvisor;
    @Value("classpath:/prompts/system-chat-message.st")
    private Resource systemMessageTemplate;

    public ChatResponse chat(Meeting meeting, String question, String sessionId) {
        if (sessionId == null) {
            sessionId = createSessionId();
        }

        if (meeting.getScript().isEmpty()) {
            return ChatResponse.of(
                "회의 스크립트가 존재하지 않아, 답변 할 수 없습니다.",
                sessionId
            );
        }

        List<Document> documents = getFilteredDocuments(meeting.getId(), question);

        ChatOptions chatOptions = ClovaChatOptions.builder().maxTokens(MAX_TOKENS).build();

        PromptTemplate promptTemplate = new PromptTemplate(systemMessageTemplate);
        String finalSessionId = sessionId;
        return ChatResponse.of(chatClient
                .prompt(promptTemplate.create(Map.of("minutes", meeting.getMinutes(), "documents", documents)))
                .options(chatOptions)
                .user(question)
                .advisors(chatMemoryAdvisor)
                .advisors(advisorSpec ->
                    advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, finalSessionId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 3))
                .call()
                .content(),
            sessionId);
    }

    private String createSessionId() {
        return uuidProvider.getUUID();
    }

    private List<Document> getFilteredDocuments(String id, String question) {
        return vectorStore.similaritySearch(question)
            .stream()
            .filter(doc -> id.equals(doc.getMetadata().get("meetingId")))
            .limit(3)
            .toList();
    }

}
