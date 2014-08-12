package paymob.wallet.test;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import javax.net.ssl.SSLSocketFactory;
import javax.xml.bind.DatatypeConverter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import org.json.simple.JSONObject;

public class HttpRequest {

	private static String backend_url = "http://127.0.0.1:8000/mobile_api/";
	private static HashMap<Function, String> methods = new HashMap<Function, String>();
	private static boolean isReady = false;

	public static enum Function {
		ACTIVATE_WALLET, LOGIN, SEND_MSG, SYNC_MSG, SYNC_CONTACTS,
	}

	private static void init() {
		methods.put(Function.LOGIN, "login");
		methods.put(Function.SEND_MSG, "send_message");
		methods.put(Function.ACTIVATE_WALLET, "activate_wallet");
		methods.put(Function.SYNC_MSG, "sync/message");
		methods.put(Function.SYNC_CONTACTS, "sync/contacts");
	}

	public static String executeHttpRequest(JSONObject jsonObj, Function func) {

		if (!isReady) {
			init();
			isReady = true;
		}

		String rawData = jsonObj.toString();
		String type = "application/x-www-form-urlencoded";
		String encodedData = URLEncoder.encode(rawData);
		URL u;
		HttpURLConnection conn;
		InputStream is;
		ByteArrayOutputStream os2 = new ByteArrayOutputStream();

		byte[] buf = new byte[4096];
		try {
			u = new URL(backend_url + methods.get(func));
			conn = (HttpURLConnection) u.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", type);
			conn.setRequestProperty("Content-Length",
					String.valueOf(encodedData.length()));
			OutputStream os = conn.getOutputStream();
			os.write(encodedData.getBytes());

			if (conn.getResponseCode() == 200) {
				is = conn.getInputStream();
			} else {
			    is = conn.getErrorStream();
			    PrintWriter writer;
			    int ret = 0;
				while ((ret = is.read(buf)) > 0) {
					os2.write(buf, 0, ret);
				}
				try {
					
					writer = new PrintWriter("/root/wallet_debug.html", "UTF-8");
					writer.println(new String(os2.toByteArray()));
					writer.close();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			int ret = 0;
			while ((ret = is.read(buf)) > 0) {
				os2.write(buf, 0, ret);
			}
			// close the inputstream
			is.close();
			return new String(os2.toByteArray());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			 
				// TODO Auto-generated catch block
				e.printStackTrace();
			
		}

		return encodedData;

	}
        
        
        public static String executeHTTPSRequest(JSONObject jsonObj, Function func, String key_per_session, String session) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {

		if (!isReady) {
			init();
			isReady = true;
		}
                byte[] mybyte = jsonObj.toString().getBytes();
                
                byte[] req = Mobile.encrypt_aes(mybyte, key_per_session, "ZgP#d_qH543LgpS-");
                //System.out.println("KEY:"+key_per_session);
		String type = "application/x-www-form-urlencoded";
                //System.out.println(session);
                String raw = session.substring(0,20) + 
                        DatatypeConverter.printBase64Binary( req ) + session.substring(20,32);
		String encodedData = URLEncoder.encode(raw);
		URL u;
		HttpURLConnection conn; 
		InputStream is;
		ByteArrayOutputStream os2 = new ByteArrayOutputStream();

		byte[] buf = new byte[4096];
		try {
			u = new URL(backend_url + methods.get(func));
			conn = (HttpURLConnection) u.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", type);
			conn.setRequestProperty("Content-Length",
					String.valueOf(encodedData.length()));
                        
                        
                        
			OutputStream os = conn.getOutputStream();
			os.write(encodedData.getBytes());

			if (conn.getResponseCode() == 200) {
				is = conn.getInputStream();
			} else {
			    is = conn.getErrorStream();
			    PrintWriter writer;
			    int ret = 0;
				while ((ret = is.read(buf)) > 0) {
					os2.write(buf, 0, ret);
				}
				try {
					
					writer = new PrintWriter("/root/wallet_debug.html", "UTF-8");
					writer.println(new String(os2.toByteArray()));
					writer.close();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			int ret = 0;
			while ((ret = is.read(buf)) > 0) {
				os2.write(buf, 0, ret);
			}
			// close the inputstream
			is.close();
			return new String(os2.toByteArray());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			 
				// TODO Auto-generated catch block
				e.printStackTrace();
			
		}

		return encodedData;

	}

	public static HttpClient getNewHttpClient() {

		try {

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 8000));
			// registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			return new DefaultHttpClient(ccm, params);

		} catch (Exception e) {
			return new DefaultHttpClient();
		}

	}
}
