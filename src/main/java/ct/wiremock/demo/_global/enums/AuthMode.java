package ct.wiremock.demo._global.enums;

public enum AuthMode {
	MODE_SUPERADMIN("SUPERADMIN"),
	MODE_NORMAL("NORMAL"),
	MODE_UNAUTHORIZED("UNAUTHORIZED");
	
	private String text;
	
	AuthMode(String text) {
		this.text = text;
	}
	
	public String getText() {
		return this.text;
	}
	
	public static AuthMode fromString(String text) {
		for (AuthMode b : AuthMode.values()) {
			if (b.text.equalsIgnoreCase(text)) {
				return b;
			}
		}
		return null;
	}
}
