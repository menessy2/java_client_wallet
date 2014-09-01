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

    private String device_id;      // spaces are not allowed in such variable
    private String sim_serial_id; // spaces are not allowed in such variable
    public String app_id;   // variable given by the server during activation and is permanently
    // stored on the mobile application since then. 
    private String mobile_number;
    // any mobile number stored on the mobile should be appended by 2 , for example : 2010..

    private String gcm_id;
    private String adm_id;

    private PublicKey publicKey;
    private String private_key_not_to_be_stored_on_mobile;

    private String session;
    private String key_per_session;

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static final Charset ASCII = Charset.forName("US-ASCII");
    public static String initializationV = "ZgP#d_qH543LgpS-";
    

    public Mobile(String mobileNumber,String deviceID,String simSerialID,
            String gcmID,String admID) {
        this.mobile_number = mobileNumber;
        this.device_id = deviceID;
        this.sim_serial_id = simSerialID;
        this.gcm_id = gcmID;
        this.adm_id = admID;
    }
    
    
    public String getHashVariables(String keypersession){
        return this.mobile_number + this.device_id + this.sim_serial_id
                + this.app_id + keypersession;
    }
    

    public void activate(String activation_code) {
        if (publicKey == null) {
            KeyPair keyPair = Security.generate_certificate();
            publicKey = keyPair.getPublic();
            private_key_not_to_be_stored_on_mobile = DatatypeConverter
                    .printBase64Binary(keyPair.getPrivate().getEncoded());
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

    

    public void login() {

        try {

            String key_per_session = Utils.generateRandomString();
            byte[] encrypted_value = Security.generate_hash(this,key_per_session);

            String encrypted_hash = Security.encrypt(encrypted_value, publicKey);
            String _key_per_session = Security.encrypt(key_per_session.getBytes(), publicKey);
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

            byte[] req = Security.decrypt_aes(cleartext, key_per_session, initializationV);

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

    
    
    
    
    public void sendNormalMessage(String content, String threadID,JSONArray mobiles) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        JSONObject jsonobj = new JSONObject();
        JSONObject jsonobj_internal = new JSONObject();
        JSONArray list = new JSONArray();
        
        jsonobj_internal.put("content", content);
        jsonobj_internal.put("req_type", "n");
        
        if ( ! threadID.equals("") )
            jsonobj_internal.put("cur_thread", threadID);
        
        if ( mobiles != null )
            jsonobj_internal.put("recipients", mobiles);
        
        list.add(jsonobj_internal);
        jsonobj.put("results", list);
                
        String request = HttpRequest.executeHTTPSRequest(jsonobj,
                HttpRequest.Function.SEND_MSG, this.key_per_session, this.session);
        
        try {
            JSONObject obj = HttpRequest.parseResponse(request,this.key_per_session);
            System.out.println(obj.toJSONString());
            String thread_id = (String) obj.get("thread_id");
            String message_id = (String) obj.get("message_id");
            
            System.out.println(message_id);
            System.out.println(thread_id);
            
        } catch (IOException ex) {
            Logger.getLogger(Mobile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Mobile.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }
    
    public void sendTransactionRequest(String content,double amount,Frequency frequencyObject,
            String threadID,JSONArray mobiles) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        JSONObject jsonobj = new JSONObject();
        JSONObject jsonobj_internal = new JSONObject();
        JSONArray list = new JSONArray();

        // message content
        if ( ! threadID.equals("") )
            jsonobj_internal.put("cur_thread", threadID);
        
        if ( mobiles != null )
            jsonobj_internal.put("recipients", mobiles);
        
       // System.out.println(frequencyObject.toJSONArray());
        
        jsonobj_internal.put("content", content);
        jsonobj_internal.put("req_type", "t");
        jsonobj_internal.put("amount", amount);
        jsonobj_internal.put("frequency", frequencyObject.toJSONArray() );
        //////////////////////////////////////////
        list.add(jsonobj_internal);
        jsonobj.put("results", list);
                //byte[] req = encrypt_aes(jsonobj.toJSONString().getBytes(), key_per_session, "ZgP#d_qH543LgpS-");

        String request = HttpRequest.executeHTTPSRequest(jsonobj,
                HttpRequest.Function.SEND_MSG, this.key_per_session, this.session);
        
        try {
            JSONObject obj = HttpRequest.parseResponse(request,key_per_session);
            System.out.println(obj.toJSONString());
            String thread_id = (String) obj.get("thread_id");
            String message_id = (String) obj.get("message_id");
            
            System.out.println(message_id);
            System.out.println(thread_id);
            
        } catch (IOException ex) {
            Logger.getLogger(Mobile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Mobile.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }
    
    public void sendTransactionReply(String content,String threadID,String transactionID) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        JSONObject jsonobj = new JSONObject();
        JSONObject jsonobj_internal = new JSONObject();
        JSONArray list = new JSONArray();

        // message content
        jsonobj_internal.put("req_type", "r");
        jsonobj_internal.put("content", content);
        jsonobj_internal.put("cur_thread", threadID);
        jsonobj_internal.put("transaction_id", transactionID);
        
        list.add(jsonobj_internal);
        jsonobj.put("results", list);
        
        String request = HttpRequest.executeHTTPSRequest(jsonobj,
                HttpRequest.Function.SEND_MSG, this.key_per_session, this.session);
        
        try {
            JSONObject obj = HttpRequest.parseResponse(request,this.key_per_session);
            System.out.println(obj.toJSONString());
            String thread_id = (String) obj.get("thread_id");
            String message_id = (String) obj.get("message_id");
            
        } catch (IOException ex) {
            Logger.getLogger(Mobile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Mobile.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }
     
    public void sendPayment(String content,String threadID,double amount,JSONArray mobiles) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        JSONObject jsonobj = new JSONObject();
        JSONObject jsonobj_internal = new JSONObject();
        JSONArray list = new JSONArray();

        // message content
        jsonobj_internal.put("content", content);
        jsonobj_internal.put("req_type", "p");
        jsonobj_internal.put("amount", amount);

        if ( ! threadID.equals("") )
            jsonobj_internal.put("cur_thread", threadID);
        
        if ( mobiles != null )
            jsonobj_internal.put("recipients", mobiles);
        //////////////////////////////////////////
        list.add(jsonobj_internal);
        jsonobj.put("results", list);
                //byte[] req = encrypt_aes(jsonobj.toJSONString().getBytes(), key_per_session, "ZgP#d_qH543LgpS-");

        String request = HttpRequest.executeHTTPSRequest(jsonobj,
                HttpRequest.Function.SEND_MSG, this.key_per_session, this.session);
        
        try {
            JSONObject obj = HttpRequest.parseResponse(request,this.key_per_session);
            System.out.println(obj.toJSONString());
            String thread_id = (String) obj.get("thread_id");
            String message_id = (String) obj.get("message_id");
            
        } catch (IOException ex) {
            Logger.getLogger(Mobile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Mobile.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }
    
    
    public void syncContacts(JSONObject newNumbers) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException{
        JSONObject jsonobj = new JSONObject();
        JSONArray list = new JSONArray();
        list.add(newNumbers);
        jsonobj.put("results", list);
               
        String request = HttpRequest.executeHTTPSRequest(jsonobj,
                HttpRequest.Function.SYNC_CONTACTS, this.key_per_session, this.session);
        
        try {
            JSONObject obj = HttpRequest.parseResponse(request,this.key_per_session);
            System.out.println(obj.toJSONString());
            
        } catch (IOException ex) {
            Logger.getLogger(Mobile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Mobile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    public void syncBulkMessages(JSONObject object_) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException{
        
        String request = HttpRequest.executeHTTPSRequest(object_,
                HttpRequest.Function.SYNC_MSG, this.key_per_session, this.session);
        
        try {
            JSONObject obj = HttpRequest.parseResponse(request,this.key_per_session);
            System.out.println(obj.toJSONString());
            
        } catch (IOException ex) {
            Logger.getLogger(Mobile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Mobile.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

}
