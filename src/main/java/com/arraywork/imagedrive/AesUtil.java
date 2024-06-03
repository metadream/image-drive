package com.arraywork.imagedrive;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES Utility
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/06/03
 */
public class AesUtil {

    // "ALGORITHM_NAME/ENCRYPT_MODE/PADDING_MODE"
    private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
    private static final Base64.Decoder base64Decoder = Base64.getUrlDecoder();

    // Encrypt
    public static String encrypt(String content, String key) {
        try {
            key = md5(key).substring(0, 16);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(), "AES"));
            byte[] bytes = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return base64Encoder.encodeToString(bytes);
        } catch (Exception e) {
            return null;
        }
    }

    // Decrypt
    public static String decrypt(String content, String key) {
        try {
            key = md5(key).substring(0, 16);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(), "AES"));
            byte[] encryptBytes = base64Decoder.decode(content);
            byte[] decryptBytes = cipher.doFinal(encryptBytes);
            return new String(decryptBytes);
        } catch (Exception e) {
            return null;
        }
    }

    // MD5
    private static String md5(String content) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(content.getBytes());
        return new BigInteger(1, md.digest()).toString(16);
    }

}