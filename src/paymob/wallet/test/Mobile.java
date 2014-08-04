package paymob.wallet.test;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import static org.apache.http.protocol.HTTP.ASCII;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.pem.PemWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sun.misc.BASE64Decoder;

public class Mobile {

    private String device_id = "my_device_id";      // spaces are not allowed in such variable
    private String sim_serial_id = "my_serial_sim_id"; // spaces are not allowed in such variable
    public String app_id;   // variable given by the server during activation and is permanently
    // stored on the mobile application since then. 
    private String mobile_number = "201005558658";
    // any mobile number stored on the mobile should be appended by 2 , for example : 2010..

    private String gcm_id = "gcm_id";
    private String adm_id = "adm_id";

    private PublicKey publicKey;
    private String private_key_not_to_be_stored_on_mobile;

    private String session;
    private String key_per_session;

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public String generateRandomString() {
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

    public void generate_certificate() {
        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");

            keyPairGenerator.initialize(2048, new SecureRandom());
            KeyPair keyPair = keyPairGenerator.genKeyPair();
            publicKey = keyPair.getPublic();

            private_key_not_to_be_stored_on_mobile = DatatypeConverter
                    .printBase64Binary(keyPair.getPrivate().getEncoded());

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void activate(String activation_code) {
        if (publicKey == null) {
            generate_certificate();
        }

        JSONObject jsonobj = new JSONObject();
        JSONArray list = new JSONArray();
        list.add(activation_code);
        list.add(gcm_id);
        list.add(adm_id);
        list.add(mobile_number);
        list.add(device_id);
        list.add(sim_serial_id);
        list.add(private_key_not_to_be_stored_on_mobile);

        jsonobj.put("account", list);
        String request = HttpRequest.executeHttpRequest(jsonobj,
                HttpRequest.Function.ACTIVATE_WALLET);

        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(request);
            JSONArray lang = (JSONArray) jsonObject.get("details");
            JSONObject innerObj = (JSONObject) lang.get(0);
            app_id = (String) innerObj.get("app_id");
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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

    public byte[] generate_hash(String key_per_session_256_bit) {
		// imp note : the mobile number should be in the following form 2010xxx
        // ( pre-pended by 2 )
        String data = this.mobile_number + this.device_id + this.sim_serial_id
                + this.app_id + key_per_session_256_bit;
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

    public void login() {

        try {

            String key_per_session = generateRandomString();
            byte[] encrypted_value = generate_hash(key_per_session);

            String encrypted_hash = encrypt(encrypted_value, publicKey);
            String _key_per_session = encrypt(key_per_session.getBytes(), publicKey);
            JSONObject jsonobj = new JSONObject();
            JSONArray list = new JSONArray();
            list.add(encrypted_hash);
            list.add(app_id);
            list.add(_key_per_session);

            jsonobj.put("account", list);

            String request = HttpRequest.executeHttpRequest(jsonobj,
                    HttpRequest.Function.LOGIN);
                    //System.out.println("here is the response: "+request);

            try {
                JSONParser jsonParser_1 = new JSONParser();
                JSONObject jsonObject_1 = (JSONObject) jsonParser_1.parse(new String(request));
                String _status_ = (String) jsonObject_1.get("status");

                if (_status_.equals("FAILED")) {
                    JSONArray myjson = (JSONArray) jsonObject_1.get("details");
                    JSONObject myobject = (JSONObject) myjson.get(0);
                    String reason = (String) myobject.get("details");
                    System.out.println(reason);
                    return;
                }
            } catch (ParseException x) {

            }

            BASE64Decoder base64decoder = new BASE64Decoder();
            byte[] cleartext = base64decoder.decodeBuffer(request);

            byte[] req = decrypt_aes(cleartext, key_per_session, "ZgP#d_qH543LgpS-");

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new String(req));
            JSONArray myjson = (JSONArray) jsonObject.get("details");
            JSONObject myobject = (JSONObject) myjson.get(0);
            this.session = (String) myobject.get("session");
            this.key_per_session = key_per_session;
            System.out.println("Successful login: " + this.session);
        } catch (IOException ex) {
            Logger.getLogger(Mobile.class.getName()).log(Level.SEVERE, null, ex);

        } catch (ParseException ex) {
            Logger.getLogger(Mobile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendMessage(String content) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        JSONObject jsonobj = new JSONObject();
        JSONObject jsonobj_internal = new JSONObject();
        JSONArray list = new JSONArray();

        // message content
        jsonobj_internal.put("content", content);

        /*
         4 types of a message
         t: transaction request
         r: transaction reply
         p: payment transfer
         n: normal message
         */
        jsonobj_internal.put("req_type", "n");

        /*
         Such variable is neglected in case the message is of type normal
         ex: 30.0
         */
        jsonobj_internal.put("amount", "30.0");

        /*
         For specifying on which thread the user is sending the message
         In case there is no thread, such variable is neglected
         */
                //jsonobj_internal.put("cur_thread", list);
        /*
         Used in transaction reply requests , for specifying the user is
         responding to which transaction request id
         */
                //jsonobj_internal.put("transaction_id", list);
        /*
         Used only when the transaction id is not sent 
         */
        JSONArray mobiles = new JSONArray();
        mobiles.add("201005087034");
        mobiles.add("201005087035");
        mobiles.add("201005087036");
        jsonobj_internal.put("recipients", mobiles);

        /////////////////////
        JSONArray req_frequency = new JSONArray();
        /////////////////////   occurence_start
        JSONObject occurence_start = new JSONObject();

        /*
         o: once
         m: monthly
         y: yearly
         */
        occurence_start.put("type", "o");
        occurence_start.put("starting_date", "");
        occurence_start.put("day", "");
        occurence_start.put("month", "");

        JSONObject ending = new JSONObject();
        /*
         u: until_date
         f: forever
         x: x_times
         */
        ending.put("type", "");
        ending.put("counter", "");
        ending.put("end_date", "");
        //////////////////////////////////
        req_frequency.add(occurence_start);
        req_frequency.add(ending);

        jsonobj_internal.put("req_frequency", req_frequency);
        //////////////////////////////////////////
        list.add(jsonobj_internal);
        jsonobj.put("results", list);

        System.out.println("before");
                //byte[] req = encrypt_aes(jsonobj.toJSONString().getBytes(), key_per_session, "ZgP#d_qH543LgpS-");

        String request = HttpRequest.executeHTTPSRequest(jsonobj,
                HttpRequest.Function.SEND_MSG, this.key_per_session, this.session);

        System.out.println("after: " + request);
    }

    private static final Charset ASCII = Charset.forName("US-ASCII");

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
            //System.out.println(plainText.length);
            int padding = (16 - (plainText.length % 16));
            byte[] padded_value = new byte[padding];
            for (int i = 0; i < padding; i++) {
                char a = ' ';
                padded_value[i] = (byte) a;
            }
            byte[] combined = new byte[padded_value.length + plainText.length];

            System.arraycopy(plainText, 0, combined, 0, plainText.length);
            System.arraycopy(padded_value, 0, combined, plainText.length, padded_value.length);
            System.out.println("Combined");
            for (int i = 0; i < combined.length; i++) {
                System.out.print(combined[i]);
            }
                //System.out.println(plainText.length);
            //System.out.println(combined.length);
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
