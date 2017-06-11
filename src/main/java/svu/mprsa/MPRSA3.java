package svu.mprsa;

import java.io.*;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.stream.Collectors;


public class MPRSA3 {
	static int bitlength = 1024;
	static int blocksize = 256; // blocksize in byte

	public BigInteger p;
	public BigInteger q;
	public BigInteger N;
	public BigInteger e;
	public BigInteger d1;
	public BigInteger d2;

	public static MPRSA3 generateKeys() {
		MPRSA3 instance = new MPRSA3();
		Random r = new Random();
		// get two big primes
		instance.p = BigInteger.probablePrime(bitlength, r);
		instance.q = BigInteger.probablePrime(bitlength, r);
		instance.N = instance.p.multiply(instance.p.multiply(instance.p).multiply(instance.q)); // N=P3Q
		BigInteger phiN = instance.p.subtract(BigInteger.ONE).multiply(instance.q.subtract(BigInteger.ONE));
		// compute the exponent necessary for encryption (private key)
		instance.e = BigInteger.probablePrime(bitlength / 2, r);
		while (phiN.gcd(instance.e).compareTo(BigInteger.ONE) > 0 && instance.e.compareTo(phiN) < 0) {
			instance.e.add(BigInteger.ONE);
		}
		// compute public key
		BigInteger d = instance.e.modInverse(phiN);
		instance.d1 = d.mod(instance.p.subtract(BigInteger.ONE));
		instance.d2 = d.mod(instance.q.subtract(BigInteger.ONE));
		return instance;
	}

	public static byte[] encrypt(String publicKey, byte[] plainText) {

		BigInteger[] key = stringToKey(publicKey);
		if (key.length != 2)
			throw new IllegalArgumentException("Invalid public key, must contain e and N");
		BigInteger e = key[0];
		BigInteger N = key[1];
		
		return (new BigInteger(plainText)).modPow(e, N).toByteArray();
	}
	
	public static byte[] decrypt(String privateKey, byte[] cipherText) {

		BigInteger[] key = stringToKey(privateKey);
		if (key.length != 4)
			throw new IllegalArgumentException("Invalid public key, must contain p, q, d1 and d2");
		BigInteger p = key[0];
		BigInteger q = key[1];
		BigInteger d1 = key[2];
		BigInteger d2 = key[3];

		BigInteger c = new BigInteger(cipherText);
		BigInteger m1 = c.modPow(d1, p);
		BigInteger m2 = c.modPow(d2, q);
		// CHINESE REMAINDER THEOREM
		BigInteger M = m1.multiply(q).multiply(q.modInverse(p)).add(m2.multiply(p).multiply(p.modInverse(q)))
				.mod(p.multiply(q));
		return (M.toByteArray());
	}

	static BigInteger[] stringToKey(String s) {
		// split and make into bigintegers
		return Arrays.stream(s.split(";")).map((x) -> new BigInteger(x.replaceAll("[^0-9]", ""))).toArray(BigInteger[]::new);
	}
	
	static String keyToString(BigInteger[] key) {
		// join the key parts
		String full = Arrays.stream(key).map((x) -> x.toString()).collect(Collectors.joining(";"));
		// split into lines
		return Arrays.stream(full.split("(?<=\\G.{70})")).collect(Collectors.joining("\n"));
	}
	
	public String getPublicKey() {
		return keyToString(new BigInteger[]{ e, N });
	}
	
	public String getPrivateKey() {
		return keyToString(new BigInteger[]{ p, q, d1, d2 });
	}
	
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		MPRSA3 r1 = generateKeys();
		long endTime = System.currentTimeMillis();
		System.out.println(" Key Generation Time :" + (endTime - startTime));
		
		String publicKey = r1.getPublicKey();
		String privateKey = r1.getPrivateKey();
		
		System.out.println("\nPublic key : \n" + publicKey);
		System.out.println("\nPrivate key: \n" + privateKey + "\n");
		
		// System.out.println("The bitlength "+ rsa.bitlength);
		String ptext = new String();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Enter the Plaintext");
			ptext = br.readLine();
			System.out.println("Encrypting string: " + ptext);
			System.out.println("Encrypting string Base64: " + encode(ptext.getBytes()));
		} catch (Exception ex) {
			throw new RuntimeException("Error in encryption", ex);
		}
		// encrypt
		long startEncyTime = System.currentTimeMillis();
		byte[] encrypted = MPRSA3.encrypt(publicKey, ptext.getBytes());
		System.out.println("Encrypted string Base64: " + encode(encrypted));
		long endEncyTime = System.currentTimeMillis();
		System.out.println(" Encryption Time :" + (endEncyTime - startEncyTime) + "millSecond");
		// decrypt
		long startDecyTime = System.currentTimeMillis();
		byte[] decrypted = decrypt(privateKey, encrypted);
		System.out.println("\nDecrypted String: " + new String(decrypted));
		long endDecyTime = System.currentTimeMillis();
		System.out.println(" Decrypted Time :" + (endDecyTime - startDecyTime) + "millSecond");
	}
	
	private static String encode(byte[] text) {
		return Base64.getEncoder().encodeToString(text);
	}

}