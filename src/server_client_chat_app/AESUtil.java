package server_client_chat_app;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;

public class AESUtil {
	
	private static final String ALGORITHM = "AES";

    public static String encrypt(String data, byte[] keyBytes) throws Exception {
        
        byte[] aesKey = Arrays.copyOf(keyBytes, 16);
        SecretKey key = new SecretKeySpec(aesKey, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedData, byte[] keyBytes) throws Exception {
        
        byte[] aesKey = Arrays.copyOf(keyBytes, 16);
        SecretKey key = new SecretKeySpec(aesKey, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
