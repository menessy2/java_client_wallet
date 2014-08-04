package paymob.wallet.test;


import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;


public class MainClass {

        String content;

	public static void main(String[] args) throws IOException  {
                System.out.println("Hello first");
                
                Mobile mob = new Mobile();
                System.out.println("Activation Request is occuring");
                mob.activate("jcuyznt");
                
                System.out.println("Generating Login Request");
                mob.login();
                
                System.out.println("Sending Message");
                mob.sendMessage("a msg");
                //System.out.println("Generating normal send message request");
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

}