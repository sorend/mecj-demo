package svu.userdb;
	
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import java.util.stream.Collectors;

import org.dalesbred.Database;

import svu.mprsa.MPRSA3;

public class UserDAO {

	Database database = Database.forUrlAndCredentials("jdbc:h2:./database", "", "");
	
	public UserDAO() {
		database.update("CREATE TABLE IF NOT EXISTS users (username VARCHAR(250) PRIMARY KEY, encryptedMeasures TEXT NULL)");
	}
	
	public Optional<User> findUser(String username) {
		return database.findOptional(User.class, "SELECT username, encryptedMeasures FROM users WHERE username = ?", username);
	}

	public Optional<String> loadMeasures(String username, String privateKey) {
		Optional<User> user = findUser(username);
		if (!user.isPresent())
			return Optional.empty();
		
		byte[] cipherText = Base64.getDecoder().decode(user.get().encryptedMeasures);
		byte[] clearText = MPRSA3.decrypt(privateKey, cipherText);
		return Optional.of(new String(clearText));
	}
	
	public void saveMeasures(String username, String measures, String publicKey) {
		byte[] cipherText = MPRSA3.encrypt(publicKey, measures.getBytes());
		String encryptedMeasures = Base64.getEncoder().encodeToString(cipherText);
		database.update("DELETE FROM users WHERE username = ?", username);
		database.update("INSERT INTO users (username, encryptedMeasures) VALUES (?, ?)", username, encryptedMeasures);
	}
	
}
