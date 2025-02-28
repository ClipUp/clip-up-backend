package potenday.backend.infra.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
class ChatClientConfig {

    @Value("classpath:/prompts/system-message.st")
    private Resource systemMessageTemplate;

    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder.defaultSystem(systemMessageTemplate).build();
    }

}
