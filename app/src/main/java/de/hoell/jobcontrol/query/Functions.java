package de.hoell.jobcontrol.query;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Hoell on 12.11.2014.
 */


    public class Functions {

        private JSONParser jsonParser;


        private static String URL = "http://85.115.30.22/job/android/index.php";


        private static String login_tag = "login";
        private static String mytickets_tag = "mytickets";
        private static String savedetails_tag = "savedetails";
        private static String historie_tag = "historie";
        private static String datech_tag = "datech";

    // constructor
        public Functions(){
            jsonParser = new JSONParser();
        }

        /**
         * function make Login Request
         * @param user
         * @param pwd
         * */
            public JSONObject loginUser(String user, String pwd){
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("tag", login_tag));
                params.add(new BasicNameValuePair("user", user));
                params.add(new BasicNameValuePair("pwd", pwd));
                JSONObject json = jsonParser.getJSONFromUrl(URL, params);
                // return json
                Log.e("loginJSON", json.toString());
                return json;
            }



          public JSONObject MyTickets(String user){
                    // Building Parameters
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("tag", mytickets_tag));
                params.add(new BasicNameValuePair("user", user));
                    // getting JSON Object
                    JSONObject json = jsonParser.getJSONFromUrl(URL, params);
                    // return json
                    return json;
          }






        public JSONObject SaveDetails(String Status ,String ID){
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("tag", savedetails_tag));
            params.add(new BasicNameValuePair("status", Status));
            params.add(new BasicNameValuePair("id", ID));
            System.out.println("status"+Status);
            // getting JSON Object
            JSONObject json = jsonParser.getJSONFromUrl(URL, params);
            Log.e("TROLJSON", json.toString());
            // return json
            return json;
        }


        public boolean isTerminheute(Date termin) {

            Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));

            int year_today = cal.get(Calendar.YEAR);
            int month_today = cal.get(Calendar.MONTH) + 1;
            int day_today = cal.get(Calendar.DATE);
            String date_today = year_today + "-" + month_today + "-" + day_today;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
            Date heute = null;
            try {
                heute = sdf.parse(date_today);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            boolean ja=heute.equals(termin);

            System.out.println("date_today" + heute +" termin" + termin + " ja"+ ja);
            if (ja){
                return true;
            }
            else{
            return false;}
        }

    public boolean isNetworkOnline(Context context) {
        boolean status=false;
        try{
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
                status= true;
            }else {
                netInfo = cm.getNetworkInfo(1);
                if(netInfo!=null && netInfo.getState()== NetworkInfo.State.CONNECTED)
                    status= true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return status;

    }

    public JSONObject Historie(String serienummer) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", historie_tag));
        params.add(new BasicNameValuePair("seriennummer", serienummer));

        System.out.println("seriennummer"+serienummer);
        // getting JSON Object
        JSONObject json = jsonParser.getJSONFromUrl(URL, params);
        Log.e("SeriennummerJSON", json.toString());
        // return json
        return json;

    }

    public JSONObject DaTech(String auanr) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", datech_tag));
        params.add(new BasicNameValuePair("auanr", auanr));

        System.out.println("auanr"+auanr);
        // getting JSON Object
        JSONObject json = jsonParser.getJSONFromUrl(URL, params);
        Log.e("datechJSON", json.toString());
        // return json
        return json;



    }
}



