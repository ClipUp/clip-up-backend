package potenday.backend.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import potenday.backend.domain.Dialogue;
import potenday.backend.domain.Minutes;
import potenday.backend.springai.models.clova.ClovaChatOptions;
import potenday.backend.support.exception.ErrorCode;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
class MinutesProcessor {

    private static final int MAX_TOKENS = 4096;
    private static final int DEFAULT_CHUNK_SIZE = 3000;
    private static final int MIN_CHUNK_SIZE_CHARS = 350;
    private static final int MIN_CHUNK_LENGTH_TO_EMBED = 5;
    private static final int MAX_NUM_CHUNKS = 10000;
    private static final boolean KEEP_SEPARATOR = true;
    private static final int MIN_SCRIPT_LENGTH = 100;

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final TextSplitter textSplitter = new TokenTextSplitter(DEFAULT_CHUNK_SIZE, MIN_CHUNK_SIZE_CHARS, MIN_CHUNK_LENGTH_TO_EMBED, MAX_NUM_CHUNKS, KEEP_SEPARATOR);
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("classpath:/prompts/system-minutes-message.st")
    private Resource systemMessageTemplate;

    Minutes generate(String id, List<Dialogue> script) {
        List<Document> documents = toDocuments(id, script);
        if (documents.isEmpty()) {
            throw ErrorCode.MEETING_IS_EMPTY.toException();
        }
        if (documents.size() == 1 && documents.getFirst().getText().length() < MIN_SCRIPT_LENGTH) {
            return new Minutes();
        }

        saveRAG(documents);
        return chat(documents);
    }

    void deleteRAGByMeetingId(String meetingId) {
        Filter.Expression expression = new FilterExpressionBuilder().eq("meetingId", meetingId).build();
        vectorStore.delete(expression);
    }

    private List<Document> toDocuments(String id, List<Dialogue> script) {
        String strScript = script.stream()
            .map(Dialogue::toString)
            .collect(Collectors.joining("\n"));
        Document document = Document.builder()
            .text(strScript)
            .metadata(Map.of("meetingId", id))
            .build();
        return textSplitter.split(document);
    }

    private Minutes chat(List<Document> documents) {
        ChatOptions chatOptions = ClovaChatOptions.builder().maxTokens(MAX_TOKENS).build();
        Minutes totalMinutes = new Minutes();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<Minutes>> futures = documents.stream()
                .map(doc -> CompletableFuture.supplyAsync(() -> processDocument(doc, chatOptions), executor))
                .toList();

            List<Minutes> results = futures.stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (Exception e) {
                        return new Minutes();
                    }
                }).toList();

            for (Minutes minutes : results) {
                totalMinutes.getDiscussions().addAll(minutes.getDiscussions());
                totalMinutes.getDecisions().addAll(minutes.getDecisions());
            }
        }

        return totalMinutes;
    }

    private Minutes processDocument(Document document, ChatOptions chatOptions) {
        String text = document.getText();
        String json = chatClient.prompt()
            .options(chatOptions)
            .system(systemMessageTemplate)
            .user(text)
            .call()
            .content();

        try {
            return objectMapper.readValue(json, Minutes.class);
        } catch (JsonProcessingException ignored) {
            return new Minutes();
        }
    }

    private void saveRAG(List<Document> documents) {
        vectorStore.accept(documents);
    }

}
