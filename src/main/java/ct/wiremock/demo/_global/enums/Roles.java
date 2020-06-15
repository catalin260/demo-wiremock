package ct.wiremock.demo._global.enums;

public enum Roles {
	ROLE_USER("ROLE_USER"),
	ROLE_SUPERADMIN("ROLE_SUPERADMIN");
	
	private String text;
	
	Roles(String text) {
		this.text = text;
	}
	
	public String getText() {
		return this.text;
	}
	
	public static Roles fromString(String text) {
		for (Roles b : Roles.values()) {
			if (b.text.equalsIgnoreCase(text)) {
				return b;
			}
		}
		return null;
	}
}
