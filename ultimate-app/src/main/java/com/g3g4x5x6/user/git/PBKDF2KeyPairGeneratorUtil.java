package com.g3g4x5x6.user.git;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;

public class PBKDF2KeyPairGeneratorUtil {

    public static KeyPair generateKeyPair(String password, byte[] salt, int iterations, int keyLength) throws Exception {
        // 使用 PBKDF2WithHmacSHA256 算法从主密码派生出一个密钥
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        byte[] secret = factory.generateSecret(spec).getEncoded();

        // 使用派生的密钥作为 SecureRandom 的种子
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(secret);

        // 生成 RSA 密钥对
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048, secureRandom);
        return keyPairGenerator.generateKeyPair();
    }

    public static void main(String[] args) {
        try {
            // 定义盐（salt），应当是保存好的，对于每个用户都是唯一的
            byte[] salt = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(salt);

            // 定义迭代次数和密钥长度
            int iterations = 10000;
            int keyLength = 256;

            // 生成密钥对
            KeyPair keyPair = generateKeyPair("your_master_password_here", salt, iterations, keyLength);

            // 在此处，你可以使用 keyPair.getPublic() 和 keyPair.getPrivate() 来使用你的公私钥
            System.out.println("Public Key: " + Arrays.toString(keyPair.getPublic().getEncoded()));
            System.out.println("Private Key: " + Arrays.toString(keyPair.getPrivate().getEncoded()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
