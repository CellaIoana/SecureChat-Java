package server_client_chat_app;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.DHParameterSpec;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

public class DiffieHellmanUtil {

	private static final int DH_KEY_SIZE = 2048;

    public static KeyPair generateKeyPair() throws Exception {
        AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
        paramGen.init(DH_KEY_SIZE);
        AlgorithmParameters params = paramGen.generateParameters();
        DHParameterSpec dhSpec = params.getParameterSpec(DHParameterSpec.class);
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
        keyPairGenerator.initialize(dhSpec);
        return keyPairGenerator.generateKeyPair();
    }

    public static byte[] generatePublicKeyEncoded(KeyPair keyPair) {
        return keyPair.getPublic().getEncoded();
    }

    public static PublicKey decodePublicKey(byte[] publicKeyEncoded) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKeyEncoded);
        return keyFactory.generatePublic(x509EncodedKeySpec);
    }

    public static SecretKey agreeSecretKey(PrivateKey privateKey, PublicKey receivedPublicKey) throws Exception {
        KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
        keyAgreement.init(privateKey);
        keyAgreement.doPhase(receivedPublicKey, true);
        byte[] secret = keyAgreement.generateSecret();
        return new SecretKeySpec(secret, 0, 16, "AES");  
    }
    
    public static byte[] hashSecretKey(SecretKey dhKey) throws Exception {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = sha256.digest(dhKey.getEncoded());

        byte[] trimmedKey = new byte[16]; 
        System.arraycopy(keyBytes, 0, trimmedKey, 0, 16); 
        return trimmedKey;
    }
    
    public static KeyPair generateKeyPairWithParams(javax.crypto.spec.DHParameterSpec params) throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");
        kpg.initialize(params);
        return kpg.generateKeyPair();
    }

}
