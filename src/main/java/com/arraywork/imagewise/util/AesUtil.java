package com.arraywork.imagewise.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
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
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, generateKey(key));
            byte[] bytes = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return base64Encoder.encodeToString(bytes);
        } catch (Exception e) {
            return null;
        }
    }

    // Decrypt
    public static String decrypt(String content, String key) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, generateKey(key));
            byte[] encryptBytes = base64Decoder.decode(content);
            byte[] decryptBytes = cipher.doFinal(encryptBytes);
            return new String(decryptBytes);
        } catch (Exception e) {
            return null;
        }
    }

    // Generate key
    private static SecretKey generateKey(String key) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(key.getBytes());
        key = new BigInteger(1, md.digest()).toString(16);
        return new SecretKeySpec(key.getBytes(), "AES");
    }

}