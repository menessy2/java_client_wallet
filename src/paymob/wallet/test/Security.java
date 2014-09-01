/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package paymob.wallet.test;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author root
 */
public class Security {
    
    public static KeyPair generate_certificate() {
        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");

            keyPairGenerator.initialize(2048, new SecureRandom());
            KeyPair keyPair = keyPairGenerator.genKeyPair();
            return keyPair;
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
    
    public static String encrypt(byte[] plain, PublicKey key) {
        byte[] cipherText = null;
        try {
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(plain);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DatatypeConverter.printBase64Binary(cipherText);
    }

    public static byte[] generate_hash(Mobile mobile,String key_per_session_256_bit) {
		// imp note : the mobile number should be in the following form 2010xxx
        // ( pre-pended by 2 )
        String data = mobile.getHashVariables(key_per_session_256_bit);
        
        MessageDigest mda;
        try {
            mda = MessageDigest.getInstance("SHA-512");
            byte[] digesta = mda.digest(data.getBytes());

            return digesta;

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    
    public static byte[] decrypt_aes(byte[] plainText, String encryptionKey, String IV) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV.getBytes("UTF8")));

            return cipher.doFinal(plainText);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(Mobile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidAlgorithmParameterException ex) {
            Logger.getLogger(Mobile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(Mobile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(Mobile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Mobile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(Mobile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Mobile.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    
    
    public static byte[] encrypt_aes(byte[] plainText, String encryptionKey, String IV) throws IllegalBlockSizeException, BadPaddingException {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV.getBytes("UTF8")));
            int padding = (16 - (plainText.length % 16));
            byte[] padded_value = new byte[padding];
            for (int i = 0; i < padding; i++) {
                char a = ' ';
                padded_value[i] = (byte) a;
            }
            byte[] combined = new byte[padded_value.length + plainText.length];

            System.arraycopy(plainText, 0, combined, 0, plainText.length);
            System.arraycopy(padded_value, 0, combined, plainText.length, padded_value.length);
            return cipher.doFinal(combined);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(Mobile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidAlgorithmParameterException ex) {
            Logger.getLogger(Mobile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Mobile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(Mobile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Mobile.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    
}
