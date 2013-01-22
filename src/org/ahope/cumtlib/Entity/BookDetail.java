package org.ahope.cumtlib.Entity;

public class BookDetail {
	private String location;
	private String state;
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "BookDetail [location=" + location + ", state=" + state + "]";
	}
}
