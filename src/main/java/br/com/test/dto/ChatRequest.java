package br.com.test.dto;

public class ChatRequest {
	
	private String question;
	
	private String uuid;
	
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
