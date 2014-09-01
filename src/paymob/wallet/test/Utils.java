/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package paymob.wallet.test;

import java.security.SecureRandom;
import static paymob.wallet.test.Mobile.hexArray;

/**
 *
 * @author root
 */
public class Utils {
    
    public static String generateRandomString() {
        char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890-=!@#$%^&*()_+~".toCharArray();
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 32; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String output = sb.toString();
        return output;
    }
    
    
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    
}
