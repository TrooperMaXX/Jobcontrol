package de.hoell.jobcontrol;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.hoell.jobcontrol.query.Functions;
import de.hoell.jobcontrol.session.SessionManager;


public class Start extends Activity {

    Button btnLogin,btnIMG;
    EditText InputName;
    EditText InputPass;
    CheckBox InputCheck;
    ImageView IMG_View;
    SessionManager session;
    public String versionName;




   // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ERROR = "error";
    private static final String TAG_MESSAGE = "message";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Functions Function = new Functions();

        if( Function.isNetworkOnline(Start.this)){

        }
        else {
            Toast.makeText(getApplicationContext(), "Keine INternet verbindung", Toast.LENGTH_SHORT).show();

        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        session = new SessionManager(this);
        //Importing all assets like buttons, text fields
        InputName = (EditText) findViewById(R.id.login_name);
        InputPass = (EditText) findViewById(R.id.login_pass);
        btnLogin = (Button) findViewById(R.id.button);
        InputCheck = (CheckBox) findViewById(R.id.checkBox);


        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {


                try {
                   String user = InputName.getText().toString();
                    String password = InputPass.getText().toString();
                    new JSONLogin(user,password).execute().get(30000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                }


            }
        });



        if (session.isUserLoggedIn()&&session.isTechNumsaved()){


            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Jobcontrol.getAppCtx());
            if(prefs.getBoolean("offline_checkbox", false)){
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("offline",true);
                startActivity(i);

            }else{




            Intent i = new Intent(getApplicationContext(), MainActivity.class);
             i.putExtra("offline",false);
            startActivity(i);
        }
            // closing this screen
            finish();

        }



    }


    private class JSONLogin extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        private String mUser,mPassword;
        JSONLogin(String user,String passwort){
            mUser=user;
            mPassword=passwort;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(Start.this);
            pDialog.setMessage("Sending Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected JSONObject doInBackground(String... args) {



            Functions Function = new Functions();
            JSONObject json = Function.loginUser(mUser, mPassword);

            // check for login response
            // check log cat fro response
            Log.d("Create Response", json.toString());
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            if (json!=null){
            try {

                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully logged in

                    if (InputCheck.isChecked()){

                        System.out.println("Checkbox is gesetz daten werden gespeichert ");


                        session.saveSession(mUser);


                    }
                    else{
                        System.out.println("Checkbox is leer daten werden NICHT gespeichert");
                    }
                    JSONObject gebiet_json = new JSONGebiet(mUser).execute().get(30000, TimeUnit.MILLISECONDS);
                    int g_success = gebiet_json.getInt(TAG_SUCCESS);
                    if (g_success == 1) {
                        JSONObject c = gebiet_json.getJSONObject("gebiet");
                       String gebiet= c.getString("tech_gebiet");
                        int technum = c.getInt("tech_nr");
                        System.out.print("Gebiet von " +mUser +" :"+ gebiet);
                        session.saveGebiet(gebiet);
                        session.saveTechNum(technum);

                    }
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);


                    // closing this screen
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Falscher Login-Name / Falsches Passwort!!!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException | InterruptedException | TimeoutException | ExecutionException e) {
                e.printStackTrace();
            }
            }
        }
    }



    private class JSONGebiet extends AsyncTask<Integer, Integer, JSONObject> {
        private String mUser;
        public JSONGebiet(String user) {
            mUser =user;
        }

        @Override
        protected JSONObject doInBackground(Integer... params) {
            Functions Function = new Functions();
            JSONObject json_gebiet = Function.Gebiet(mUser);

            return json_gebiet;
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_info) {
            Toast.makeText(getApplicationContext(), versionName, Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
