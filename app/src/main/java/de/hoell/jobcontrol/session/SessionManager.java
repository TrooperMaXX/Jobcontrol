package de.hoell.jobcontrol.session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.lang.reflect.Array;
import java.util.HashMap;


import de.hoell.jobcontrol.MainActivity;
import de.hoell.jobcontrol.Start;

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
        public void saveSession(String user, String pwd){
            // Storing login value as TRUE
            editor.putBoolean(IS_USER_LOGIN, true);

            // Storing user in pref
            editor.putString(KEY_USER, user);

            // Storing pwd in pref
            editor.putString(KEY_PWD, pwd);


            // commit changes
            editor.commit();
            System.out.println("Daten gespeichert:"+user + pwd);
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
         * Check login method will check user login status
         * If false it will redirect user to login page
         * Else do anything
         *
        public boolean checkLogin(){
            // Check login status
            if(!this.isUserLoggedIn()){

                // user is not logged in redirect him to Login Activity
                Intent i = new Intent(_context, Start.class);

                // Closing all the Activities from stack
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Add new Flag to start new Activity
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // Staring Login Activity
                _context.startActivity(i);

                return true;
            }
            return false;
        }
    */
/*/Create login session
        public void saveTickets(Array Tickets){
            // Storing login value as TRUE
            editor.putBoolean(IS_OFFLINE, true);

            // Storing user in pref
            editor.putString(KEY_USER, user);

            // Storing pwd in pref
            editor.putString(KEY_PWD, pwd);

            // commit changes
            editor.commit();
            System.out.println("Daten gespeichert:"+user + pwd);
        }
*/
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



