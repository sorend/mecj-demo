package svu.userdb;
	
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import java.util.stream.Collectors;

import org.dalesbred.Database;

import com.lambdaworks.crypto.SCryptUtil;

import svu.mprsa.MPRSA3;

public class UserDAO {

	Database database = Database.forUrlAndCredentials("jdbc:h2:./database2", "", "");
	
	public UserDAO() {
		database.update("CREATE TABLE IF NOT EXISTS users (username VARCHAR(250) PRIMARY KEY, encryptedPassword VARCHAR(250), encryptedMeasures TEXT NULL)");
	}
	
	public Optional<User> findUser(String username) {
		return database.findOptional(User.class, "SELECT username, encryptedPassword, encryptedMeasures FROM users WHERE username = ?", username);
	}
	
	public void createUser(String username, String password) {
		database.update("INSERT INTO users (username, encryptedPassword) VALUES (?, ?)", username, SCryptUtil.scrypt(password, 16384, 8, 1));
	}
	
	public boolean verifyUser(String username, String password) {
		Optional<User> user = findUser(username);
		if (!user.isPresent())
			return false;
		return SCryptUtil.check(password, user.get().encryptedPassword);
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
		database.update("UPDATE users SET encryptedMeasures = ? WHERE username = ?", encryptedMeasures, username);
	}
	
}
