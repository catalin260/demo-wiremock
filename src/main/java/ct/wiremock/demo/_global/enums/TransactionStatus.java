package ct.wiremock.demo._global.enums;

public enum TransactionStatus {
	QUEUE("queue"),
	DONE("done"),
	ERROR("error");
	
	private String text;
	
	TransactionStatus(String text) {
		this.text = text;
	}
	
	public String getText() {
		return this.text;
	}
	
	public static TransactionStatus fromString(String text) {
		for (TransactionStatus b : TransactionStatus.values()) {
			if (b.text.equalsIgnoreCase(text)) {
				return b;
			}
		}
		return null;
	}
}
