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

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
class MinutesProcessor {

    private static final int DEFAULT_CHUNK_SIZE = 3000;
    private static final int MIN_CHUNK_SIZE_CHARS = 350;
    private static final int MIN_CHUNK_LENGTH_TO_EMBED = 5;
    private static final int MAX_NUM_CHUNKS = 100;
    private static final boolean KEEP_SEPARATOR = true;
    private static final int MAX_TOKENS = 4096;
    private static final int THREAD_POOL_SIZE = 5;
    private static final int MIN_SCRIPT_LENGTH = 100;

    private final ChatClient chatClient;
    private final TextSplitter textSplitter = new TokenTextSplitter(
        DEFAULT_CHUNK_SIZE, MIN_CHUNK_SIZE_CHARS, MIN_CHUNK_LENGTH_TO_EMBED, MAX_NUM_CHUNKS, KEEP_SEPARATOR
    );

    String generate(List<Dialogue> script) {
        List<Document> documents = splitScript(script);
        if (documents.isEmpty()) {
            return "회의 내용이 존재하지 않습니다.";
        } else if (documents.size() == 1 && documents.getFirst().getText().length() < MIN_SCRIPT_LENGTH) {
            return "회의 내용이 너무 적습니다.";
        }

        return processInBatches(documents);
    }

    private List<Document> splitScript(List<Dialogue> script) {
        String strScript = script.stream()
            .map(Dialogue::toString)
            .collect(Collectors.joining("\n"));
        return textSplitter.split(new Document(strScript));
    }

    private String processInBatches(List<Document> documents) {
        ChatOptions chatOptions = ClovaChatOptions.builder().maxTokens(MAX_TOKENS).build();
        StringBuilder sb = new StringBuilder();

        try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE)) {
            List<CompletableFuture<String>> futures = documents.stream()
                .map(doc -> CompletableFuture.supplyAsync(
                    () -> chatClient.prompt().options(chatOptions).user(doc.getText()).call().content(), executor)
                )
                .toList();

            futures.stream()
                .map(CompletableFuture::join)
                .forEach(result -> sb.append(result).append("\n"));
        }

        return sb.toString();
    }

}
