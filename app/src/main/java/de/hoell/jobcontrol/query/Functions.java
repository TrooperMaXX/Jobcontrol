package de.hoell.jobcontrol.query;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Hoell on 12.11.2014.
 */


    public class Functions {

        private JSONParser jsonParser;


        private static String URL = "http://5.158.136.15/job/android/index.php";


        private static String login_tag = "login";
        private static String mytickets_tag = "mytickets";
        private static String savedetails_tag = "savedetails";
        private static String historie_tag = "historie";
        private static String datech_tag = "datech";
        private static String saverueck_tag = "saverueck";
        private static String saveinfo_tag = "saveinfo";
        private static String info_tag = "info";
        private static String saveesk_tag = "saveesk";
        private static String gebiet_tag = "gebiet";
        private static String gebtech_tag = "gebtech";
        private static String verschieben_tag = "verschieben";
        private static String techniker_tag = "techniker";
        private static String saveswitch_tag = "saveswitch";
        private static String oeffnungs_tag = "oeffnung";
        private static String filenumbers_tag = "bogen";
    // constructor
        public Functions(){
            jsonParser = new JSONParser();
        }

        /**
         * function make Login Request
         * @param user user
         * @param pwd user
         * */
            public JSONObject loginUser(String user, String pwd){
                // Building Parameters
                List<NameValuePair> params = new ArrayList<>();
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
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("tag", mytickets_tag));
                params.add(new BasicNameValuePair("user", user));

                    // getting JSON Object
                    JSONObject json = jsonParser.getJSONFromUrl(URL, params);
                    // return json
                    return json;
          }






        public JSONObject SaveDetails(int Status ,String ID,String user){
            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("tag", savedetails_tag));
            params.add(new BasicNameValuePair("status", String.valueOf(Status)));
            params.add(new BasicNameValuePair("id", ID));
            params.add(new BasicNameValuePair("user", user));
            System.out.println("status" + Status);
            // getting JSON Object
            JSONObject json = jsonParser.getJSONFromUrl(URL, params);
            Log.e("TROLJSON", json.toString());
            // return json
            return json;
        }


        public boolean isTerminheute(Date Dtermin) {



            Date dt = new Date();

            // set format for date

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // parse it like

            String check = dateFormat.format(dt);
            String termin;

            termin = dateFormat.format(Dtermin);
            boolean ja=check.equals(termin);

            Log.e("date_today!!!!!!" , check + " termin" + termin + " ja" + ja);
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

    public JSONObject Historie(String serienummer, int anfang) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", historie_tag));
        params.add(new BasicNameValuePair("seriennummer", serienummer));
        params.add(new BasicNameValuePair("anfang", String.valueOf(anfang)));

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

        System.out.println("auanr" + auanr);
        // getting JSON Object
        JSONObject json = jsonParser.getJSONFromUrl(URL, params);
        Log.e("datechJSON", json.toString());
        // return json
        return json;



    }

    public JSONObject SaveRueck(String id, String ansprech, String info,String user) {  // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("tag", saverueck_tag));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("ansprech", ansprech));
        params.add(new BasicNameValuePair("info", info));
        params.add(new BasicNameValuePair("user", user));

        // getting JSON Object
        JSONObject json = jsonParser.getJSONFromUrl(URL, params);
        Log.e("SAVERUECK", json.toString());
        // return json
        return json;
    }

    public JSONObject Info(String id) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", info_tag));
        params.add(new BasicNameValuePair("id", id));


        // getting JSON Object
        JSONObject json = jsonParser.getJSONFromUrl(URL, params);
        Log.e("datechJSON", json.toString());
        // return json
        return json;
    }

    public JSONObject SaveInfo(String id, String ansprech, String info,String user) {  // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();


        params.add(new BasicNameValuePair("tag", saveinfo_tag));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("ansprech", ansprech));
        params.add(new BasicNameValuePair("info", info));
        params.add(new BasicNameValuePair("user", user));

        // getting JSON Object
        JSONObject json = jsonParser.getJSONFromUrl(URL, params);
        Log.e("SAVEINFO", json.toString());
        // return json
        return json;
    }



    public JSONObject SaveEsk(String id, String ansprech, String info, String user) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("tag", saveesk_tag));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("ansprech", ansprech));
        params.add(new BasicNameValuePair("info", info));
        params.add(new BasicNameValuePair("user", user));

        // getting JSON Object
        JSONObject json = jsonParser.getJSONFromUrl(URL, params);
        Log.e("SAVEESK", json.toString());
        // return json
        return json;
    }

    public JSONObject Gebiet(String user) {


            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("tag", gebiet_tag));
            params.add(new BasicNameValuePair("user", user));

            System.out.println("user" + user);
            // getting JSON Object
            JSONObject json = jsonParser.getJSONFromUrl(URL, params);
            Log.e("gebietJSON", json.toString());
            // return json
            return json;




    }

    public JSONObject GebTech(String gebiet,String user) {


        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", gebtech_tag));
        params.add(new BasicNameValuePair("gebiet", gebiet));
        params.add(new BasicNameValuePair("user", user));

        System.out.println("gebiet" + gebiet);
        // getting JSON Object
        JSONObject json = jsonParser.getJSONFromUrl(URL, params);
        Log.e("gebietJSON", json.toString());
        return json;
    }

    public JSONObject Verschieben(String id, String techniker,String user) {
   // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", verschieben_tag));
        params.add(new BasicNameValuePair("techniker", techniker));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("user", user));

        // getting JSON Object
        JSONObject json = jsonParser.getJSONFromUrl(URL, params);
        Log.e("VERSCHIEBENJSON", json.toString());
        // return json
        return json;

    }

    public JSONObject Techniker(String user) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", techniker_tag));
        params.add(new BasicNameValuePair("user", user));

        // getting JSON Object
        JSONObject json = jsonParser.getJSONFromUrl(URL, params);
        // return json
        return json;
    }



    public JSONObject SaveSwitch(int status,String user) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("tag", saveswitch_tag));
            params.add(new BasicNameValuePair("status", String.valueOf(status)));
            params.add(new BasicNameValuePair("user", user));

            System.out.println("status" + status);
            // getting JSON Object
            JSONObject json = jsonParser.getJSONFromUrl(URL, params);
            Log.e("TROLJSON", json.toString());
            // return json
            return json;


    }

    public JSONObject Oeffnung(String gernr) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", oeffnungs_tag));
        params.add(new BasicNameValuePair("gernr", gernr));

        // getting JSON Object
        JSONObject json = jsonParser.getJSONFromUrl(URL, params);
        // return json
        return json;
    }

    public JSONObject getFilenumbers(String Ticketid, String Adressid) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", filenumbers_tag));
        params.add(new BasicNameValuePair("ticketid", Ticketid));
        params.add(new BasicNameValuePair("adressid", Adressid));


        // getting JSON Object
        JSONObject json = jsonParser.getJSONFromUrl(URL, params);
        // return json
        return json;
    }


}



