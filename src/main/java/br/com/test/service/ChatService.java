package br.com.test.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.test.dto.TimeResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

	private static final Logger log = LoggerFactory.getLogger(ChatService.class);

	private final ChatModel chatModel;
	private final SyncMcpToolCallbackProvider toolCallbackProvider;

	@Value("${app.chat.system-prompt}")
	private String systemPrompt;

	// In-memory storage for conversation context by UUID
	private final Map<String, List<Message>> conversationHistory = new ConcurrentHashMap<>();

	/**
	 * Process a chat request with conversation context management
	 * 
	 * @param uuid     Unique identifier for the conversation session
	 * @param question User's question
	 * @return Flux of ChatResponse for streaming responses
	 */
	public List<TimeResponse> chat(String uuid, String question) {
		log.info("Processing chat request for UUID: {} with question: {}", uuid, question);

		// Get or create conversation history for this UUID
		List<Message> messages = conversationHistory.computeIfAbsent(uuid, k -> {
			log.info("Creating new conversation for UUID: {}", uuid);
			List<Message> newMessages = new ArrayList<>();
			// Add system prompt as first message
			newMessages.add(new SystemMessage(systemPrompt));
			return newMessages;
		});

		// Add user question to conversation history
		messages.add(new UserMessage(question));

		// Create prompt with conversation history
		Prompt prompt = new Prompt(messages);

		// Create ChatClient with MCP tools
		ChatClient client = ChatClient.builder(chatModel).defaultToolCallbacks(toolCallbackProvider.getToolCallbacks())
				.build();

		// Call Azure OpenAI with streaming and MCP tools
		try {
			return new ObjectMapper().readValue(
					client.prompt(prompt).stream().chatResponse()
							.map(response -> response != null && response.getResult() != null
									&& response.getResult().getOutput() != null
									&& response.getResult().getOutput().getText() != null
											? response.getResult().getOutput().getText()
											: "")
							.collectList().map(list -> String.join("", list)).block(),
					new TypeReference<List<TimeResponse>>() {});
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Clear conversation history for a specific UUID
	 * 
	 * @param uuid Unique identifier for the conversation session
	 */
	public void clearConversation(String uuid) {
		conversationHistory.remove(uuid);
		log.info("Cleared conversation history for UUID: {}", uuid);
	}

	/**
	 * Get the number of active conversations
	 * 
	 * @return Number of stored conversation sessions
	 */
	public int getActiveConversationsCount() {
		return conversationHistory.size();
	}
}
