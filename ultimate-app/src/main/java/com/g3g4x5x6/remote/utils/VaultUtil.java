package com.g3g4x5x6.remote.utils;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.g3g4x5x6.AppConfig;
import lombok.extern.slf4j.Slf4j;


/**
 * <a href="https://hutool.cn/docs/#/crypto/%E5%AF%B9%E7%A7%B0%E5%8A%A0%E5%AF%86-SymmetricCrypto">hutool</a>
 */
@Slf4j
public class VaultUtil {
    private static String secretKey = AppConfig.getProperty("ssh.session.secret.key");

    static {
        if (secretKey.equals("")) {
            //随机生成密钥
            byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
            secretKey = bytesToHex(key);
            AppConfig.setProperty("ssh.session.secret.key", secretKey);
            AppConfig.saveSettingsProperties();
            log.debug("secretKey: " + secretKey);
        }
    }

    public static String encryptPasswd(String passwordStr) {
        // 密钥
        byte[] key = hexToBytes(secretKey);
        // 构建
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);
        // 加密为16进制表示
        return aes.encryptHex(passwordStr);
    }

    public static String decryptPasswd(String passwordHex) {
        // 密钥
        byte[] key = hexToBytes(secretKey);
        // 构建
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);
        //解密为字符串
        return aes.decryptStr(passwordHex, CharsetUtil.CHARSET_UTF_8);
    }


    private static String byteToHex(byte b) {
        String hexString = Integer.toHexString(b & 0xFF);
        if (hexString.length() < 2)
            hexString = 0 + hexString;

        return hexString.toUpperCase();
    }

    /**
     * 字节数组转Hex
     *
     * @param bytes 字节数组
     * @return Hex
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        if (bytes != null && bytes.length > 0) {
            for (byte aByte : bytes) {
                String hex = byteToHex(aByte);
                sb.append(hex);
            }
        }
        return sb.toString();
    }

    /**
     * Hex转Byte字节
     *
     * @param hex 十六进制字符串
     * @return 字节
     */
    private static byte hexToByte(String hex) {
        return (byte) Integer.parseInt(hex, 16);
    }

    /**
     * Hex转Byte字节数组
     *
     * @param hex 十六进制字符串
     * @return 字节数组
     */
    private static byte[] hexToBytes(String hex) {
        int hexLength = hex.length();
        byte[] result;
        //判断Hex字符串长度，如果为奇数个需要在前边补0以保证长度为偶数
        //因为Hex字符串一般为两个字符，所以我们在截取时也是截取两个为一组来转换为Byte。
        if (hexLength % 2 == 1) {
            //奇数
            hexLength++;
            hex = "0" + hex;
        }
        result = new byte[(hexLength / 2)];
        int j = 0;
        for (int i = 0; i < hexLength; i += 2) {
            result[j] = hexToByte(hex.substring(i, i + 2));
            j++;
        }
        return result;
    }
}
