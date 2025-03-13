package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import potenday.backend.application.dto.ChatResponse;
import potenday.backend.domain.Meeting;
import potenday.backend.springai.models.clova.ClovaChatOptions;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@RequiredArgsConstructor
@Component
class MeetingChat {

    private static final int MAX_TOKENS = 2000;
    private static final int TOP_K_DOCUMENTS = 3;
    private static final double SIMILARITY_THRESHOLD = 0.5;

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final UUIDProvider uuidProvider;
    private final MessageChatMemoryAdvisor chatMemoryAdvisor;

    @Value("classpath:/prompts/system-chat-message.st")
    private Resource systemMessageTemplate;

    public ChatResponse chat(Meeting meeting, String question, String sessionId) {
        sessionId = Objects.requireNonNullElse(sessionId, createSessionId());

        if (meeting.getScript().isEmpty()) {
            return ChatResponse.of("회의 스크립트가 존재하지 않아, 답변 할 수 없습니다.", sessionId);
        }

        List<String> documents = fetchRelevantDocuments(meeting.getId(), question);
        return createChatResponse(meeting, question, sessionId, documents);
    }

    private ChatResponse createChatResponse(
        Meeting meeting,
        String question,
        String sessionId,
        List<String> documents
    ) {
        ChatOptions chatOptions = ClovaChatOptions.builder()
            .maxTokens(MAX_TOKENS)
            .build();

        PromptTemplate promptTemplate = new PromptTemplate(systemMessageTemplate);

        String response = chatClient.prompt(promptTemplate.create(Map.of(
                "minutes", meeting.getMinutes(),
                "documents", documents)))
            .options(chatOptions)
            .user(question)
            .advisors(chatMemoryAdvisor)
            .advisors(advisorSpec -> advisorSpec
                .param(CHAT_MEMORY_CONVERSATION_ID_KEY, sessionId)
                .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, TOP_K_DOCUMENTS))
            .call()
            .content();

        return ChatResponse.of(response, sessionId);
    }

    private String createSessionId() {
        return uuidProvider.getUUID();
    }

    private List<String> fetchRelevantDocuments(String meetingId, String question) {
        return vectorStore.similaritySearch(
                SearchRequest.builder()
                    .filterExpression(new FilterExpressionBuilder().eq("meetingId", meetingId).build())
                    .topK(TOP_K_DOCUMENTS)
                    .similarityThreshold(SIMILARITY_THRESHOLD)
                    .build()
            )
            .stream()
            .map(Document::getText)
            .toList();
    }

}
