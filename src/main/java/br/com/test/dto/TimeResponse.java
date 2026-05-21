package br.com.test.dto;

public class TimeResponse {
	
	private String country;
	
	private String time;

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public TimeResponse(String country, String time) {
		super();
		this.country = country;
		this.time = time;
	}

	public TimeResponse() {
		super();
	}
}
