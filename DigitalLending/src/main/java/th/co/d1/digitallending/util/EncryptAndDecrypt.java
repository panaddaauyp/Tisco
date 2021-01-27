/*
Step : Endcode
1. Get String to Encrypt first step
2. Ramdom number and create front salt by ramdom number
3. Ramdom number and create end salt by ramdom number
4. concat frontSalt+encrypted+endSalt+frontInt+endInt
5. Send to Encrypt again concat value

Step : Decode
1. Decrypt value
2. substring last 2 value (int)
3. split int 2 value
4. substring front salt by first int
5. substring end salt by last int
6. Decrypt value 2 text


 */
package th.co.d1.digitallending.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.PBEKeySpec;
import org.apache.commons.codec.binary.Base64;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EncryptAndDecrypt {

    private final String UNICODE_FORMAT = "UTF8";
    public final String DESEDE_ENCRYPTION_SCHEME = "DESede";
    private KeySpec ks;
    private SecretKeyFactory skf;
    private Cipher cipher;
    byte[] arrayBytes;
    private String myEncryptionKey;
    private String myEncryptionScheme;
    SecretKey key;

    private final Random RANDOM = new SecureRandom();
    private final int ITERATIONS = 10000;
    private final int KEY_LENGTH = 256;

    public EncryptAndDecrypt() throws Exception {
        myEncryptionKey = "ThisIsSpartaThisIsSparta";
        myEncryptionScheme = DESEDE_ENCRYPTION_SCHEME;
        arrayBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
        ks = new DESedeKeySpec(arrayBytes);
        skf = SecretKeyFactory.getInstance(myEncryptionScheme);
        cipher = Cipher.getInstance(myEncryptionScheme);
        key = skf.generateSecret(ks);
    }

    public String encrypt(String unencryptedString) {
        String encryptedString = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] plainText = unencryptedString.getBytes(UNICODE_FORMAT);
            byte[] encryptedText = cipher.doFinal(plainText);
            encryptedString = new String(Base64.encodeBase64(encryptedText));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedString;
    }

    public String decrypt(String encryptedString) {
        String decryptedText = null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptedText = Base64.decodeBase64(encryptedString);
            byte[] plainText = cipher.doFinal(encryptedText);
            decryptedText = new String(plainText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedText;
    }

    public String enCodeValue(String target) throws Exception {
        String returnVal = null;
        try {
            if (!target.equals(null) || target.length() > 0) {
                target = java.util.Base64.getEncoder().encodeToString(target.getBytes("utf-8"));

                EncryptAndDecrypt enCode = new EncryptAndDecrypt();
                int frontInt = enCode.getRandom(9);
                int endInt = enCode.getRandom(9);
                String encrypted = enCode.encrypt(target);
                /* fix case thai lang */
                String decrypted = enCode.decrypt(encrypted);
                String frontSalt = enCode.generateRandomPassword(frontInt);
                String endSalt = enCode.generateRandomPassword(endInt);
                String lastCode = frontSalt + "" + encrypted + "" + endSalt + "" + frontInt + "" + endInt;
                String endCode = enCode.encrypt(lastCode);
                returnVal = endCode;
            }

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(EncryptAndDecrypt.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(EncryptAndDecrypt.class.getName()).log(Level.SEVERE, null, ex);
        }
        return returnVal;
    }

    public String deCodeValue(String target) {
        String returnVal = null;
        try {
            if (!target.equals(null) || target.length() > 0) {
                EncryptAndDecrypt enCode = new EncryptAndDecrypt();
                String deCode1 = enCode.decrypt(target);

                int deCodefrontInt = Character.getNumericValue(deCode1.charAt(deCode1.length() - 2));
                int deCodeendInt = Character.getNumericValue(deCode1.charAt(deCode1.length() - 1));

                String deCode2 = deCode1.substring(0, deCode1.length() - 2);
                deCode2 = deCode1.substring(0, deCode2.length() - deCodeendInt);
                String decodeValue = deCode1.substring(deCodefrontInt, deCode2.length());

                byte[] asBytes = java.util.Base64.getDecoder().decode(enCode.decrypt(decodeValue));
                returnVal = new String(asBytes, "utf-8");
            }

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(EncryptAndDecrypt.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(EncryptAndDecrypt.class.getName()).log(Level.SEVERE, null, ex);
        }
        return returnVal;
    }

    public byte[] getNextSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return salt;
    }

    public byte[] hash(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }
    }

    public String generateRandomPassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int c = RANDOM.nextInt(62);
            if (c <= 9) {
                sb.append(String.valueOf(c));
            } else if (c < 36) {
                sb.append((char) ('a' + c - 10));
            } else {
                sb.append((char) ('A' + c - 36));
            }
        }
        return sb.toString();
    }

    public void anyRandomInt(Random random) {
        int randomInt = random.nextInt();
//        System.out.println("random integer:" + randomInt);
    }

    public int getRandom(int max) {
        return (int) (Math.random() * max);
    }

    public String enCodeValueURL(String value) {
        String returnValue = "";
        try {
            returnValue = java.util.Base64.getEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8.toString()));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(EncryptAndDecrypt.class.getName()).log(Level.SEVERE, null, ex);
        }
        return returnValue;
    }

    public String deCodeValueURL(String value) {
        String returnValue = "";
        try {
            byte[] decodedValue = java.util.Base64.getUrlDecoder().decode(value);
            returnValue = new String(decodedValue, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(EncryptAndDecrypt.class.getName()).log(Level.SEVERE, null, ex);
        }
        return returnValue;
    }

}
