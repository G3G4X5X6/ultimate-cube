package com.g3g4x5x6.user.git;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class PasswordUtils {

    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256; // bits
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    /**
     * Generate a random salt.
     * @return A base64 encoded salt
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Generate a hash of the password using PBKDF2.
     * @param password The password to hash.
     * @param salt A base64 encoded salt to use in the hash.
     * @return A base64 encoded hash of the password.
     */
    public static String hashPassword(String password, String salt) {
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), Base64.getDecoder().decode(salt), ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error while hashing the password", e);
        }
    }

    /**
     * Verify a password against a hash.
     * @param password The password to verify.
     * @param salt The base64 encoded salt used to hash the password.
     * @param expectedHash The expected base64 encoded hash of the password.
     * @return true if the password matches the hash, false otherwise.
     */
    public static boolean verifyPassword(String password, String salt, String expectedHash) {
        String pwdHash = hashPassword(password, salt);
        return pwdHash.equals(expectedHash);
    }

    public static void main(String[] args) {
        String password = "";
        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);

        System.out.println("Salt: " + salt);
        System.out.println("Hashed Password: " + hashedPassword);

        // Verify the password
        boolean isMatch = verifyPassword(password, salt, hashedPassword);
        System.out.println("Password verification result: " + isMatch);
    }
}
