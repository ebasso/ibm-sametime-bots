package net.ebasso;

/**
  * @author ebasso
 */
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class EncryptAll {

    private static final String CYPHER = "AES/ECB/PKCS5Padding";
    private static final String CRYPT_KEY = "q0a2bWuTyTXC79Wf";


    public static void main(String[] args) {

        String password = "";
        if (args.length != 0) {
            for (String arg : args) {
                if (arg.startsWith("-password=")) {
                    password = arg.substring(10);
                }
            }
        } else {
            System.out.println("Execute:");
            System.out.println("java -cp .:commons-codec-1.9.jar EncryptAll -password=<SENHA>");
            //System.exit(1);
        }

        try {
            System.out.println("");
            password = "87650051";
            String stringEncoded = encryptString(password);
            System.out.println("password=" + password + "");
            System.out.println("password=ENC(" + stringEncoded + ")");

            System.out.println("");

//            String original = "usuario" + ":" + "senha";
//
//            System.out.println("Original Value: [" + original + "]");
//
//            stringEncoded = encryptString(original);
//            System.out.println("Encoded Value [" + stringEncoded + "]");
//
//            String stringDecoded = decryptString(stringEncoded);
//            System.out.println("Decoded Value [" + stringDecoded + "]");
            System.out.println("\nEncryptAll - end");

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static String encryptString(String token) {
        byte[] bytesEncryptAES = encryptAES(token.getBytes());
        byte[] bytesEncodedBase64 = Base64.encodeBase64(bytesEncryptAES);

        return new String(bytesEncodedBase64);
    }

    private static byte[] encryptAES(byte[] tokenBytes) {
        try {
            byte[] cryptKeyBytes = CRYPT_KEY.getBytes();
            Cipher localCipher = Cipher.getInstance(CYPHER);
            SecretKeySpec localSecretKeySpec = new SecretKeySpec(cryptKeyBytes, "AES");
            localCipher.init(1, localSecretKeySpec);
            return localCipher.doFinal(tokenBytes);
        } catch (Exception localException) {
            System.out.println("ERROR: Unable to encrypt string with AES.");
        }
        return null;
    }

    private static byte[] decryptAES(byte[] tokenBytes) {
        try {
            byte[] cryptKeyBytes = CRYPT_KEY.getBytes();
            Cipher localCipher = Cipher.getInstance(CYPHER);
            SecretKeySpec localSecretKeySpec = new SecretKeySpec(cryptKeyBytes, "AES");
            localCipher.init(2, localSecretKeySpec);
            return localCipher.doFinal(tokenBytes);
        } catch (Exception localException) {
            System.out.println("ERROR: Unable to decrypt string from AES.");
        }
        return null;
    }

    public static String decryptString(String token) {
        byte[] bytesEncodedBase64 = Base64.decodeBase64(token.getBytes());
        byte[] bytesDecryptAES = decryptAES(bytesEncodedBase64);

        return new String(bytesDecryptAES);
    }
}
