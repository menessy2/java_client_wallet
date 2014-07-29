package paymob.wallet.test;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.pem.PemWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.xml.bind.DatatypeConverter;

public class Mobile {

	private String device_id = "my device id";
	private String sim_serial_id = "my serial sim id";
	public String app_id;
	private String mobile_number = "01005558658";

	private String gcm_id = "gcm_id";
	private String adm_id = "adm_id";

	private PublicKey publicKey;
	private String private_key_not_to_be_stored_on_mobile;

	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
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
		if (publicKey == null)
			generate_certificate();

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

		System.out.println(request);

	}

	public static String encrypt(byte[] plain, PublicKey key) {
		byte[] cipherText = null;
		try {
			// get an RSA cipher object and print the provider
			final Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
			// encrypt the plain text using the public key
			cipher.init(Cipher.ENCRYPT_MODE, key);
			System.out.println( new String(plain, "UTF-8"));			
			cipherText = cipher.doFinal(plain);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return DatatypeConverter.printBase64Binary( cipherText );
	}

	public byte[] generate_hash(byte[] key_per_session_256_bit) {
		// imp note : the mobile number should be in the following form 2010xxx
		// ( pre-pended by 2 )
		String data = this.mobile_number + this.device_id + this.sim_serial_id
				+ this.app_id + bytesToHex(key_per_session_256_bit);
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
		SecureRandom random = new SecureRandom();
		byte[] key_per_session = random.generateSeed(16);
		byte[] encrypted_value = generate_hash(key_per_session);
		
                
		String encrypted_hash = encrypt(encrypted_value, publicKey);
		String _key_per_session = encrypt(key_per_session, publicKey);

		
		
		JSONObject jsonobj = new JSONObject();
		JSONArray list = new JSONArray();
		list.add(encrypted_hash);
		list.add(app_id);
		list.add(_key_per_session);

		jsonobj.put("account", list);

		String request = HttpRequest.executeHttpRequest(jsonobj,
				HttpRequest.Function.LOGIN);
		System.out.println(request);
	}
}
