/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package paymob.wallet.test;

import java.util.*;
import java.text.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
/**
 *
 * @author root
 */
public class Frequency {
    private static HashMap<StartFrequencyType, String> startFrequency;
    private static HashMap<EndFrequencyType, String> endFrequency; 
    private String startDate;
    private String endDate;
    private String frequencyDay;
    private String frequencyMonth;
    private String endcounter;
    private StartFrequencyType startingType;
    private EndFrequencyType endingType;
    
    public static enum StartFrequencyType {
	ONCE, MONTHLY, YEARLY,
    }
    
    public static enum EndFrequencyType {
	UNTIL_DATE, FOREVER, X_TIMES,
    }
    
    
    private static void init() {
        startFrequency = new HashMap<StartFrequencyType, String>();
        endFrequency = new HashMap<EndFrequencyType, String>();
        
        
	startFrequency.put(StartFrequencyType.ONCE, "o");
	startFrequency.put(StartFrequencyType.MONTHLY, "m");
	startFrequency.put(StartFrequencyType.YEARLY, "y");
	endFrequency.put(EndFrequencyType.UNTIL_DATE, "u");
	endFrequency.put(EndFrequencyType.FOREVER, "f");
        endFrequency.put(EndFrequencyType.X_TIMES, "x");
    }
    
    /*
    in case a value is neglected , one must put an empty string instead '' 
    */
    public Frequency(Date startdate, Date enddate,String frequencyday,
            String frequencymonth, String counter_of_end, 
            StartFrequencyType frequencytype, EndFrequencyType endft) {
        
        init();
        frequencyDay = frequencyday;
        frequencyMonth = frequencymonth;
        endcounter = counter_of_end;
        startingType = frequencytype;
        endingType = endft;
        
        SimpleDateFormat ft = new SimpleDateFormat ("ddMMYYYY");  
        startDate = ft.format(startdate);
        endDate = ft.format(enddate);
    }
    
    
    public JSONArray toJSONArray(){
        JSONArray myJSONArray = new JSONArray();
        JSONObject occurence = new JSONObject();
        JSONObject ending = new JSONObject();
        
        occurence.put("type", startFrequency.get(startingType));
        occurence.put("starting_date", startDate );
        occurence.put("day", frequencyDay );
        occurence.put("month", frequencyMonth );
        
        
        ending.put("type", endFrequency.get(endingType));
        ending.put("counter", endcounter );
        ending.put("end_date", endDate );
        JSONObject myoccurence = new JSONObject();
        JSONObject myending = new JSONObject();
        
        myoccurence.put("occurence",occurence );
        myending.put("ending", ending);
        
        myJSONArray.add( myoccurence );
        myJSONArray.add( myending );
        
        return myJSONArray;
    }
    
}