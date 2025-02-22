package potenday.backend.web;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
class TestController {

    private final ChatClient chatClient;

    @PostMapping("/chat")
    String test(@RequestBody ChatRequest request) {
        return chatClient
            .prompt()
            .user(request.text)
            .call()
            .content();
    }

    record ChatRequest(
        String text
    ) {

    }

}
