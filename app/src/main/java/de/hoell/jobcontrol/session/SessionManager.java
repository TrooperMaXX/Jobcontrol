package de.hoell.jobcontrol.session;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by Hoell on 21.11.2014.
 */
public class SessionManager {




        // Shared Preferences reference
        SharedPreferences pref;

        // Editor reference for Shared preferences
        SharedPreferences.Editor editor;

        // Context
        Context _context;

        // Shared pref mode
        int PRIVATE_MODE = 0;

        // Sharedpref file name
        private static final String PREFER_NAME = "AndroidExamplePref";

        // All Shared Preferences Keys
        private static final String IS_USER_LOGIN = "IsUserLoggedIn";

        // All Shared Preferences Keys
        private static final String IS_OFFLINEMODE = "IsOfflineOn";

        // User name (make variable public to access from outside)
        public static final String KEY_USER = "user";

        // Email address (make variable public to access from outside)
        public static final String KEY_PWD = "pwd";

        // Email address (make variable public to access from outside)
        public static final String KEY_JSON = "json";

        // Constructor
        public SessionManager(Context context){
            this._context = context;
            pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
            editor = pref.edit();
        }



    //Create login session
        public void saveSession(String user){
            // Storing login value as TRUE
            editor.putBoolean(IS_USER_LOGIN, true);

            // Storing user in pref
            editor.putString(KEY_USER, user);


            // commit changes
            editor.commit();
            System.out.println("Daten gespeichert:"+user);
        }

        public void saveJSON(String jsonstring){
            // Storing login value as TRUE
            editor.putBoolean(IS_OFFLINEMODE, true);

            // Storing jsonstring in pref
            editor.putString(KEY_JSON, jsonstring);

            // commit changes
            editor.commit();
            System.out.println("Daten gespeichert:"+jsonstring);
        }


        /**
         * Get stored session data
         * */
        public HashMap<String, String> getData(){

            //Use hashmap to store user credentials
            HashMap<String, String> user = new HashMap<String, String>();

            // user name
            user.put(KEY_USER, pref.getString(KEY_USER, null));

            // user email id
            user.put(KEY_PWD, pref.getString(KEY_PWD, null));


            // return user
            return user;
        }



    public String getUser(){

        // Get user name
        String user = pref.getString(KEY_USER, null);

        // return user
        return user;
    }

    public String getJstring(){

        // Get user name
        String jstring = pref.getString(KEY_JSON, null);

        // return user
        return jstring;
    }

        /**
         * Clear session details
         * */
        public void logoutUser(){

            // Clearing all user data from Shared Preferences
            editor.clear();
            editor.commit();

        }


        // Check for login
        public boolean isUserLoggedIn(){
            return pref.getBoolean(IS_USER_LOGIN, false);
        }

        // Check for offlinedata
        public boolean isJSONsaved(){
            return pref.getBoolean(IS_OFFLINEMODE, false);
        }
}



