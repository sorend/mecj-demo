package svu.userdb;

public final class User {

	public String username;
	public String encryptedMeasures;
	public String encryptedPassword;

	@Override
	public String toString() {
		return "User [username=" + username + ", encryptedMeasures="
				+ encryptedMeasures + "]";
	}
	
}
