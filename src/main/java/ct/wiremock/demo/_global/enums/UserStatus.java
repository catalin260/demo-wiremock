package ct.wiremock.demo._global.enums;

public enum UserStatus {
	ACTIVE("active"),
	INACTIVE("inactive");
	
	private String text;
	
	UserStatus(String text) {
		this.text = text;
	}
	
	public String getText() {
		return this.text;
	}
	
	public static UserStatus fromString(String text) {
		for (UserStatus b : UserStatus.values()) {
			if (b.text.equalsIgnoreCase(text)) {
				return b;
			}
		}
		return null;
	}
}
