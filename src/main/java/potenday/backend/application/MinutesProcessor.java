package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
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
    private static final int MAX_NUM_CHUNKS = 15;
    private static final boolean KEEP_SEPARATOR = true;

    private static final int MAX_TOKENS = 4096;

    private final ChatClient chatClient;
    private final TextSplitter textSplitter = new TokenTextSplitter(DEFAULT_CHUNK_SIZE, MIN_CHUNK_SIZE_CHARS, MIN_CHUNK_LENGTH_TO_EMBED, MAX_NUM_CHUNKS, KEEP_SEPARATOR);
    private final ChatOptions chatOptions = ClovaChatOptions.builder().maxTokens(4096).build();

    @Value("classpath:/prompts/draft-minutes-message.st")
    private Resource draftMinutesSystemMessageTemplate;
    @Value("classpath:/prompts/minutes-message.st")
    private Resource minutesSystemMessageTemplate;


    String generate(List<Dialogue> script) {
        String strScript = convertScriptToString(script);

        Document document = new Document(strScript);
        List<Document> documents = textSplitter.split(document);

        return generateDraft(documents);
//        return chatClient.prompt()
//            .system(minutesSystemMessageTemplate)
//            .options(chatOptions)
//            .user(draft)
//            .call()
//            .content();
    }

    private String generateDraft(List<Document> documents) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<String>> futures = documents.stream()
                .map(d -> CompletableFuture.supplyAsync(() ->
                    chatClient.prompt()
                        .system(draftMinutesSystemMessageTemplate)
                        .options(chatOptions)
                        .user(d.getText())
                        .call()
                        .content(), executor))
                .toList();

            List<String> responses = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

            return String.join("\n", responses);
        }
    }

    private String convertScriptToString(List<Dialogue> script) {
        StringBuilder sb = new StringBuilder();
        for (Dialogue dialogue : script) {
            sb.append(dialogue.toString()).append("\n");
        }
        return sb.toString();
    }

}
