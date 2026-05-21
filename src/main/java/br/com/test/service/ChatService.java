package br.com.test.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ChatService {
	
	private static final Logger log = LoggerFactory.getLogger(ChatService.class);
	
	private final ChatModel chatModel;
	//private final SyncMcpToolCallbackProvider toolCallbackProvider;
	
	@Value("${app.chat.system-prompt}")
	private String systemPrompt;
	
	// In-memory storage for conversation context by UUID
	private final Map<String, List<Message>> conversationHistory = new ConcurrentHashMap<>();
	
	/**
	 * Process a chat request with conversation context management
	 * @param uuid Unique identifier for the conversation session
	 * @param question User's question
	 * @return Flux of ChatResponse for streaming responses
	 */
	public Flux<ChatResponse> chat(String uuid, String question) {
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
		ChatClient client = ChatClient.builder(chatModel)
			//.defaultToolCallbacks(toolCallbackProvider.getToolCallbacks())
			.build();
		
		// StringBuilder to accumulate the assistant's response
		final StringBuilder assistantResponse = new StringBuilder();
		
		// Call Azure OpenAI with streaming and MCP tools
		return client.prompt(prompt).stream().chatResponse()
			.doOnNext(response -> {
				// Accumulate assistant's response content
				if (response.getResult() != null && 
					response.getResult().getOutput() != null &&
					response.getResult().getOutput().getText() != null) {
					assistantResponse.append(response.getResult().getOutput().getText());
				}
				log.debug("Received response chunk for UUID: {}", uuid);
			})
			.doOnComplete(() -> {
				// After streaming completes, add assistant's response to history
				if (assistantResponse.length() > 0) {
					messages.add(new AssistantMessage(assistantResponse.toString()));
					log.info("Added assistant response to conversation history for UUID: {}", uuid);
				}
				log.info("Completed streaming response for UUID: {}", uuid);
			})
			.doOnError(error -> {
				log.error("Error processing chat for UUID: {}", uuid, error);
			});
	}
	
	/**
	 * Clear conversation history for a specific UUID
	 * @param uuid Unique identifier for the conversation session
	 */
	public void clearConversation(String uuid) {
		conversationHistory.remove(uuid);
		log.info("Cleared conversation history for UUID: {}", uuid);
	}
	
	/**
	 * Get the number of active conversations
	 * @return Number of stored conversation sessions
	 */
	public int getActiveConversationsCount() {
		return conversationHistory.size();
	}
}
