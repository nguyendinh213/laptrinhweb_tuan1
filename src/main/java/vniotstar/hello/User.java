package vniotstar.hello;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String username;
	private final String displayName;

	public User(String username, String displayName) {
		this.username = username;
		this.displayName = displayName;
	}

	public String getUsername() {
		return username;
	}

	public String getDisplayName() {
		return displayName;
	}
} 