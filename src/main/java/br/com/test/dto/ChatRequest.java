package br.com.test.dto;

import javax.validation.constraints.Pattern;

public class ChatRequest {
	
	private String question;
	
	@Pattern(regexp = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89aAbB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}", message = "Must be a valid uuid")
	private String uuid;
	
	@Pattern(regexp = "^(ms-|api-|lib-)([a-z]|-|\\d){1,80}(-v\\d{1,3})$", message = "Component name must follow ^(ms-|api-|lib-)([a-z]|-|\\d){1,80}(-v\\d{1,3})$. pattern")
	private String name;
	
	public ChatRequest() {
	}
	
	public ChatRequest(String question, String uuid) {
		this.question = question;
		this.uuid = uuid;
	}
	
	public String getQuestion() {
		return question;
	}
	
	public void setQuestion(String question) {
		this.question = question;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
