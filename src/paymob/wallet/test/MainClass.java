package paymob.wallet.test;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;


public class MainClass {

        String content;

	public static void main(String[] args) throws IOException, IllegalBlockSizeException, UnsupportedEncodingException, BadPaddingException  {
                System.out.println("Hello first");
                
                Mobile mob = new Mobile();
                System.out.println("Activation Request is occuring");
                mob.activate("7ijf44v");
                
                System.out.println("Generating Login Request");
                mob.login();
                
                System.out.println("Sending Message");
                JSONArray mobiles = new JSONArray();
                mobiles.add("201005087044");
                mobiles.add("201005087047");
                mobiles.add("201005087048");
                mob.sendNormalMessage("a msg","",mobiles);
                
                
                Date startDate = new Date();
                Date  endDate = new Date();
                Frequency obj = new Frequency(startDate,endDate,"3","12","",
                        Frequency.StartFrequencyType.MONTHLY,
                        Frequency.EndFrequencyType.FOREVER);
                mob.sendTransactionRequest("transaction req", 40.0, obj,"",mobiles);
                
                
                System.out.println("Sending Transaction Reply");
                mob.sendTransactionReply("reply", "22", "2");
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