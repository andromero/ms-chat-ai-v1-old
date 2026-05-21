package br.com.test.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.test.dto.ChatRequest;
import br.com.test.dto.TimeResponse;
import br.com.test.service.ChatService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
@Validated
public class ChatController {
	
	private static final Logger log = LoggerFactory.getLogger(ChatController.class);
	
	private final ChatService chatService;
	
	/**
	 * POST /chats - Process a chat request with conversation context
	 * 
	 * @param request ChatRequest with question and uuid
	 * @return Flux of ChatResponse streaming response from Azure OpenAI
	 */
	@PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public List<TimeResponse> chat(@RequestBody ChatRequest request) {
		log.info("Received chat request for UUID: {}", request.getUuid());
		
		return chatService.chat(request.getUuid(), request.getQuestion());
	}
}
