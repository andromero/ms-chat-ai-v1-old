package br.com.tlf.code;

import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@TestPropertySource(properties = {
	"spring.ai.azure.openai.api-key=test-key",
	"spring.ai.azure.openai.endpoint=https://test.openai.azure.com",
	"spring.ai.mcp.client.enabled=false"
})
class ApplicationTest {

	@Configuration
	static class TestConfig {
		
		@Bean
		@Primary
		public ChatModel testChatModel() {
			return mock(ChatModel.class);
		}
	}

	@Test
	void contextLoads() {
		// Test that Spring context loads successfully
	}

}
