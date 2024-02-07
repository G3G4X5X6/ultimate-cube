package com.g3g4x5x6.user.git;

import javax.crypto.Cipher;
import java.security.*;
import java.util.Base64;

public class KeyPairGeneratorUtil {

    private static final String VERIFICATION_MESSAGE = "password_verification_message";

    public static KeyPair generateKeyPair(String password) throws NoSuchAlgorithmException {
        SecureRandom secureRandom = new SecureRandom(password.getBytes());
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048, secureRandom);
        return keyPairGenerator.generateKeyPair();
    }

    public static String encryptWithPrivateKey(PrivateKey privateKey, String message) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decryptWithPublicKey(PublicKey publicKey, String encryptedMessage) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
        return new String(decryptedBytes);
    }

    public static boolean verifyPassword(KeyPair keyPair, String encryptedVerificationMessage) {
        try {
            String decryptedMessage = decryptWithPublicKey(keyPair.getPublic(), encryptedVerificationMessage);
            return VERIFICATION_MESSAGE.equals(decryptedMessage);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        try {
            KeyPair keyPair = generateKeyPair("your_master_password_here");
            String encryptedVerificationMessage = encryptWithPrivateKey(keyPair.getPrivate(), VERIFICATION_MESSAGE);

            boolean isPasswordCorrect = verifyPassword(keyPair, encryptedVerificationMessage);
            System.out.println("Is the password correct? " + isPasswordCorrect);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
