package com.fc.apibanco.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {

    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = System.getenv("APP_SECRET_KEY");

    private static SecretKeySpec getKeySpec() {
        if (SECRET_KEY == null || SECRET_KEY.isBlank()) {
            throw new IllegalStateException("Falta APP_SECRET_KEY en el entorno");
        }
        return new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
    }

    public static String encrypt(String plainText) {
        try {
            SecretKeySpec key = getKeySpec();
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Error al encriptar", e);
        }
    }

    public static String decrypt(String encryptedText) {
        try {
            SecretKeySpec key = getKeySpec();
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decrypted);
        } catch (Exception e) {
            throw new RuntimeException("Error al desencriptar", e);
        }
    }
}
