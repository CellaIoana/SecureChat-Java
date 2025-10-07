package server_client_chat_app;

public class CaesarCipher {

	public static String encrypt(String text, int shift) {
        shift = shift % 26; 
        StringBuilder result = new StringBuilder();
        for (char character : text.toCharArray()) {
            if (character >= 'a' && character <= 'z') {
                char shifted = (char) (((character - 'a' + shift) % 26) + 'a');
                result.append(shifted);
            } else if (character >= 'A' && character <= 'Z') {
                char shifted = (char) (((character - 'A' + shift) % 26) + 'A');
                result.append(shifted);
            } else {
                result.append(character);
            }
        }
        return result.toString();
    }

    public static String decrypt(String cipher, int shift) {
        return encrypt(cipher, -shift);
    }
}
