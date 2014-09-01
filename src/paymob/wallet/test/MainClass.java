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
                
                Mobile mob = new Mobile("201005558658","my_device_id","my_serial_sim_id",
                        "gcm_id","adm_id");
                System.out.println("Activation Request is occuring");
                mob.activate("6u2eufl");
                
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
                Frequency obj1 = new Frequency(startDate,endDate,"3","12","",
                        Frequency.StartFrequencyType.MONTHLY,
                        Frequency.EndFrequencyType.FOREVER);
                
                Frequency obj2 = new Frequency(startDate,endDate,"3","12","",
                        Frequency.StartFrequencyType.ONCE,
                        Frequency.EndFrequencyType.UNTIL_DATE);
                
                Frequency obj3 = new Frequency(startDate,endDate,"3","12","",
                        Frequency.StartFrequencyType.YEARLY,
                        Frequency.EndFrequencyType.X_TIMES);
                
                //mob.sendTransactionRequest("transaction req", 40.0, obj2,"",mobiles);
                //mob.sendTransactionReply("reply", "3", "2");
                //System.out.println("Sending Payment");
                //mob.sendPayment("send payment", "", 50.0, mobiles);
                /*
                JSONObject newMobiles = new JSONObject();
                newMobiles.put("201005558655", "Adsada asdasd");
                newMobiles.put("201005558635", "Adsada agasdasd");
                newMobiles.put("201005558615", "Adsadada asdasd");
                newMobiles.put("201005558605", "Adsadada asdasd");
                newMobiles.put("201005558005", "Adsadada asdasd");
                newMobiles.put("201005550005", "Adsadada asdasd");
                newMobiles.put("201000998005", "Adsadada asdasd");
                newMobiles.put("201000558005", "Adsadada asdasd");
                newMobiles.put("201000558005", "Adsadada asdasd");
                newMobiles.put("201005558004", "Adsadada asdasd");
                newMobiles.put("201005553004", "Adsadada asdasd");
                mob.syncContacts(newMobiles);
                */
                JSONObject _obj = new JSONObject();
                
                _obj.put("time","31122014606024");
                mob.syncBulkMessages(_obj);

                
	}

}