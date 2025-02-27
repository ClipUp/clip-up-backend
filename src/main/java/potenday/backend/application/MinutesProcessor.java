package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;
import potenday.backend.domain.Dialogue;
import potenday.backend.springai.models.clova.ClovaChatOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
class MinutesProcessor {

    private static final int DEFAULT_CHUNK_SIZE = 3000;
    private static final int MIN_CHUNK_SIZE_CHARS = 350;
    private static final int MIN_CHUNK_LENGTH_TO_EMBED = 5;
    private static final int MAX_NUM_CHUNKS = 15;
    private static final boolean KEEP_SEPARATOR = true;

    private static final int MAX_TOKENS = 4096;

    private final ChatClient chatClient;
    private final TextSplitter textSplitter = new TokenTextSplitter(DEFAULT_CHUNK_SIZE, MIN_CHUNK_SIZE_CHARS, MIN_CHUNK_LENGTH_TO_EMBED, MAX_NUM_CHUNKS, KEEP_SEPARATOR);

    String generate(List<Dialogue> script) {
        String strScript = convertScriptToString(script);

        Document document = new Document(strScript);
        List<Document> documents = textSplitter.split(document);

        return process(documents);
    }

    private String process(List<Document> documents) {
        ChatOptions chatOptions = ClovaChatOptions.builder().maxTokens(4096).build();
        StringBuilder sb = new StringBuilder();
        ExecutorService executor = Executors.newFixedThreadPool(5); // 최대 5개 병렬 실행
        int batchSize = 5;

        List<List<Document>> batches = new ArrayList<>();
        for (int i = 0; i < documents.size(); i += batchSize) {
            batches.add(documents.subList(i, Math.min(i + batchSize, documents.size())));
        }

        for (List<Document> batch : batches) {
            // 비동기 실행
            List<CompletableFuture<String>> futures = batch.stream()
                .map(doc -> CompletableFuture.supplyAsync(() ->
                    chatClient.prompt()
                        .options(chatOptions)
                        .user(doc.getText())
                        .call()
                        .content(), executor))
                .toList();

            // 모든 Future가 완료될 때까지 대기하고 결과 취합
            List<String> results = futures.stream()
                .map(CompletableFuture::join)
                .toList();

            results.forEach(result -> sb.append(result).append("\n"));

            // 마지막 batch가 아닐 경우 1분 대기
            if (batch != batches.get(batches.size() - 1)) {
                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread was interrupted", e);
                }
            }
        }

        return sb.toString();
    }

    private String convertScriptToString(List<Dialogue> script) {
        StringBuilder sb = new StringBuilder();
        for (Dialogue dialogue : script) {
            sb.append(dialogue.toString()).append("\n");
        }
        return sb.toString();
    }

}
