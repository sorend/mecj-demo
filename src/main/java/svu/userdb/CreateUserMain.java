package svu.userdb;

public class CreateUserMain {

	public static void main(String[] args) throws Exception {
		
		UserDAO dao = new UserDAO();
		String username = args[0];
		String password = args[1];
		
		if (username == null || password == null) {
			System.err.println("Syntax: gradlew createUser -Dusername=the-user-name -Dpassword=the-password");
			System.exit(100);
		}
		
		dao.createUser(username, password);
		System.out.println("Done - user can now login with details:");
		System.out.println("Username: " + username);
		System.out.println("Password: " + password);
	}
	
}
