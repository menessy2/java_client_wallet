package paymob.wallet.test;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class MainClass {

        String content;

	public static void main(String[] args)  {
		System.out.println("Hello first");
		
		MainClass x = new MainClass();
		Mobile mob = new Mobile();
		System.out.println("Activation Request is occuring");
		mob.activate("myxgx");
		
		System.out.println("Generating Login Request");
		mob.login();


		System.out.println("Generating normal send message request");
		//x.genNormalReq();
		
		/*
		System.out.println("Generating normal send payment request");
		x.genPayReq();
		System.out.println("Generating normal Transaction request");
		x.genTransactionRequestReq();
		
		//cancelTransactionRequest();
		//syncContacts();
		*/
	}


	private void genNormalReq()  {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("req_type", "n");
		jsonObj.put("content", this.content);
		// current thread id
		// jsonObj.put("cur_thread", );

		// recipients ONLY IF THERE IS NO CURRENT THREAD ID (IF IT IS THE FIRST
		// MESSAGE OF THE THREAD)
		// the thread ID is generated and returned by the server in response to
		// the first message of the thread
		JSONArray jsonArr = new JSONArray();
		String request = HttpRequest.executeHttpRequest(jsonObj,
				HttpRequest.Function.SEND_MSG);
		// Log.d("response",request);
		System.out.println(request);
		/*
		 * for all recipients jsonArr.put("recipients", recipient.phone_number)
		 */
		// jsonObj.put("recipients", );
	}

	private void genPayReq() {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("req_type", "p");

		// If the message isn't empty
		if (this.content != "")
			jsonObj.put("content", this.content);

		// if (validate that the amount field isn't empty or 0
		// jsonObj.put("content", amount);

		// current thread id
		// jsonObj.put("cur_thread", );

		// recipients ONLY IF THERE IS NO CURRENT THREAD ID (IF IT IS THE FIRST
		// MESSAGE OF THE THREAD)
		// the thread ID is generated and returned by the server in response to
		// the first message of the thread
		JSONArray jsonArr = new JSONArray();
		/*
		 * for all recipients jsonArr.put(recipient.phone_number)
		 */
		// jsonObj.put("recipients", );

		String request = HttpRequest.executeHttpRequest(jsonObj,
				HttpRequest.Function.SEND_MSG);
	}

	private void genTransactionRequestReq() {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("req_type", "t");

		// If the message isn't empty
		if (this.content != "")
			jsonObj.put("content", this.content);

		// if (validate that the amount field isn't empty or 0)
		// jsonObj.put("content", amount);

		// current thread id
		// jsonObj.put("cur_thread", );

		// recipients ONLY IF THERE IS NO CURRENT THREAD ID (IF IT IS THE FIRST
		// MESSAGE OF THE THREAD)
		// the thread ID is generated and returned by the server in response to
		// the first message of the thread
		JSONArray jsonArr = new JSONArray();
		/*
		 * for all recipients jsonArr.put(recipient.phone_number)
		 */
		// jsonObj.put("recipients", );

		// amount of time IN HOURS to wait before the transaction request times
		// out
		// is not required, will be 24 hours by default
		// jsonObj.put("timeout", );

		JSONObject frequency = new JSONObject();
		JSONObject occurence = new JSONObject();
		JSONObject ending = new JSONObject();

		// type of the occurence:
		// "o" for One time payment
		// "d" for Daily payment
		// "w" for Weekly payment
		// "m" for Monthly payment
		occurence.put("type", "o");

		// starting date, format is (year, month, day)
		// occurence.put("starting_date", );

		// used only with weekly and monthly payments
		// specifies which days of the week/month should the request be sent
		// format "1,2,3" where each number specifies a day
		// starting from saturday in weekly and day 1 in monthly
		// range is from 1-7 in weekly and 1-31 in monthly
		// occurence.put("payment_days", );

		// how may times should be skipped between each request
		// example if the number is 1 and weekly payment is specified, program
		// will skip 1 week
		// without checking for the payment days
		occurence.put("skip_x", "3");

		frequency.put("occurence", occurence);

		// optional, values are:
		// "u" for Until Date
		// "f" for forever
		// "x" for do the process X times
		ending.put("type", "u");

		// used for "x" ending type
		// ending.put("counter", );

		// used for "u" ending type, format is (year, month, day)
		ending.put("end_date", "u");

		frequency.put("ending", ending);
		jsonObj.put("fequency", frequency);

		String request = HttpRequest.executeHttpRequest(jsonObj,
				HttpRequest.Function.SEND_MSG);
	}

	private void syncContacts() {
		JSONObject jsonObj = new JSONObject();
		JSONObject contacts = new JSONObject();
		JSONArray jsonArr = new JSONArray();

		// jsonArr.put(contacts);
		jsonObj.put("results", jsonArr);
		String request = HttpRequest.executeHttpRequest(jsonObj,
				HttpRequest.Function.SYNC_CONTACTS);

	}

	private void cancelTransactionRequest() {
		JSONObject jsonObj = new JSONObject();

		// the id of the transaction we wish to cancel
		// transaction ids are generated by the server and returned to the
		// client upon creation of a new transaction request
		// jsonObj.put("trans_id", );
		// POSTReq(jsonObj);

	}

	private void activatePhone() {
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArr = new JSONArray();

		String activation_code = "123123";
		String gcm_registration_id = "ASDAdas";
		String adm_registration_id = "ASDAdas";
		String mobile_number = "010055565854";
		String device_id = "at54fwsda";
		String simserialid = "2342421312312123123";
		String private_pem_certificate = "adssadasssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
		/*
		 * jsonArr.put(activation_code); jsonArr.put(gcm_registration_id);
		 * jsonArr.put(adm_registration_id); jsonArr.put(mobile_number);
		 * jsonArr.put(device_id); jsonArr.put(simserialid);
		 * jsonArr.put(private_pem_certificate);
		 */
		jsonObj.put("account", jsonArr);
		String request = HttpRequest.executeHttpRequest(jsonObj,
				HttpRequest.Function.ACTIVATE_WALLET);

	}

	/*
	 * private static HttpResponse getMessages(){ try { HttpClient client = new
	 * DefaultHttpClient(); HttpGet request = new HttpGet();
	 * 
	 * //request.new URI(path); return client.execute(request); } catch
	 * (URISyntaxException e) { e.printStackTrace(); } catch
	 * (ClientProtocolException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } catch (IOException e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); } }
	 */

}
