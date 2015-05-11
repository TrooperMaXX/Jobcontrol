package de.hoell.jobcontrol;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import de.hoell.jobcontrol.query.Functions;
import de.hoell.jobcontrol.session.SessionManager;
import de.hoell.jobcontrol.ticketlist.TicketDetailsActivity;


public class Start extends Activity {

    Button btnLogin;
    EditText InputName;
    EditText InputPass;
    CheckBox InputCheck;
    public static String user;
    private  String password,gebiet;
    SessionManager session;




   // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ERROR = "error";
    private static final String TAG_MESSAGE = "message";





    @Override
    protected void onCreate(Bundle savedInstanceState) {

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


                new JSONLogin().execute();

            }
        });



        if (session.isUserLoggedIn()){


            user = session.getUser();

            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);

            // closing this screen
            finish();

        }

    }


    private class JSONLogin extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
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

            user = InputName.getText().toString();
            password = InputPass.getText().toString();

            Functions Function = new Functions();
            JSONObject json = Function.loginUser(user, password);

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


                        session.saveSession(user);


                    }
                    else{
                        System.out.println("Checkbox is leer daten werden NICHT gespeichert");
                    }
                    JSONObject gebiet_json = new JSONGebiet(user).execute().get();
                    int g_success = gebiet_json.getInt(TAG_SUCCESS);
                    if (g_success == 1) {
                        JSONObject c = gebiet_json.getJSONObject("gebiet");
                        gebiet= c.getString("Gebiet");
                        System.out.print("Gebiet von " +user +" :"+ gebiet);
                        session.saveGebiet(gebiet);

                    }
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);


                    // closing this screen
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Falscher Login-Name / Falsches Passwort!!!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException | InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
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
            JSONObject json_gebiet = Function.Gebiet(user);

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
            Toast.makeText(getApplicationContext(), "Version 0.0.6", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
