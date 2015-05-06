package de.hoell.jobcontrol.ticketlist;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hoell.jobcontrol.MainActivity;
import de.hoell.jobcontrol.R;
import de.hoell.jobcontrol.historie.Historie_Activity;
import de.hoell.jobcontrol.query.Functions;

public class TicketDetailsActivity extends Activity {

 String Statusnum;
static String ID;
private static final String TAG_SUCCESS = "success";


    @Override
    protected void onCreate(Bundle savedInstanceState) {



        getActionBar().setDisplayHomeAsUpEnabled(true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_details);
        String Status = getIntent().getStringExtra("value_status");
        Button Button_maps = (Button) findViewById(R.id.button_maps);
        Button Button_save = (Button) findViewById(R.id.button_save);
        Button Button_his = (Button) findViewById(R.id.button_his);
        Button Button_rueck = (Button) findViewById(R.id.button_rueck);
        Button Button_info = (Button) findViewById(R.id.button_info);
        Button Button_eskalation = (Button) findViewById(R.id.button_eskalation);

        int DropPos = getIntent().getIntExtra("value_droppos",0);
        String Firma = getIntent().getStringExtra("value_firma");
        String Adresse = getIntent().getStringExtra("value_adresse");
        String Standort = getIntent().getStringExtra("value_standort");
        String Modell = getIntent().getStringExtra("value_modell");//Gerät
        final String Serienummer = getIntent().getStringExtra("value_serienummer");
        String Stoerung = getIntent().getStringExtra("value_stoerung");
        String Ansprechpartner = getIntent().getStringExtra("value_ansprechpartner");
        String Telefonnummer = getIntent().getStringExtra("value_telefonnummer");
        String Angenommen = getIntent().getStringExtra("value_angenommen");
        String Termin = getIntent().getStringExtra("value_termin");
        String Auftragsnr = getIntent().getStringExtra("value_auftragnr");
        String Wvnr = getIntent().getStringExtra("value_wvnr");
        String Annahme = getIntent().getStringExtra("value_annahme");
        final String uri = getIntent().getStringExtra("value_uri");
        Statusnum = getIntent().getStringExtra("value_statusnum");
        ID = getIntent().getStringExtra("value_id");

        Log.d("INTENTEXTRA:", Status+" "+ DropPos +" "+ Adresse +" "+Standort+" "+Modell+" "+Serienummer+" "+Stoerung+" "+Ansprechpartner+" "+Telefonnummer+" "+Termin+" "+ Annahme);

        Spinner spinnerStatus = (Spinner) findViewById(R.id.status_spinner);
        spinnerStatus.setSelection(DropPos);
        spinnerStatus.setSelection(DropPos);

        TextView textViewFirma = (TextView) findViewById(R.id.textViewContentFirma);
        textViewFirma.setText(Firma);

        TextView textViewAdresse = (TextView) findViewById(R.id.textViewContentAdresse);
        textViewAdresse.setText(Adresse);

        TextView textViewStandort = (TextView) findViewById(R.id.textViewContentStandort);
        textViewStandort.setText(Standort);

        TextView textViewModell = (TextView) findViewById(R.id.textViewContentModell);
        textViewModell.setText(Modell);

        TextView textViewSerienummer = (TextView) findViewById(R.id.textViewContentSerienummer);
        textViewSerienummer.setText(Serienummer);

        TextView textViewStoerung = (TextView) findViewById(R.id.textViewContentStoerung);
        textViewStoerung.setText(Stoerung);

        TextView textViewAnsprechpartner = (TextView) findViewById(R.id.textViewContentAnsprechpartner);
        textViewAnsprechpartner.setText(Ansprechpartner);

        TextView textViewTelefonnummer = (TextView) findViewById(R.id.textViewContentTelefonnummer);
        textViewTelefonnummer.setText(Telefonnummer);
        Linkify.addLinks(textViewTelefonnummer, Linkify.PHONE_NUMBERS);

        TextView textViewAngenommen = (TextView) findViewById(R.id.textViewContentAngenommen);
        textViewAngenommen.setText(Angenommen);

        TextView textViewTermin = (TextView) findViewById(R.id.textViewContentTermin);
        textViewTermin.setText(Termin);

        TextView textViewAuftragsnr = (TextView) findViewById(R.id.textViewContentAuftragsnr);
        if(Auftragsnr == null){Auftragsnr=" ";}
        textViewAuftragsnr.setText(Auftragsnr);

        TextView textViewWvnr = (TextView) findViewById(R.id.textViewContentWvnr);
        if(Wvnr.equals("null")){Wvnr=" ";}
        textViewWvnr.setText(Wvnr);

        TextView textViewAnnahme = (TextView) findViewById(R.id.textViewContentAnnahme);
        textViewAnnahme.setText(Annahme);

        Button_maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showMap(Uri.parse(uri));
            }
        });



        Button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Functions Function = new Functions();

                if( Function.isNetworkOnline(TicketDetailsActivity.this)){
                    new JSONSaveDetails().execute();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Keine INternet verbindung", Toast.LENGTH_SHORT).show();}
            }
        });

        Button_his.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                TextView textViewSerienummer =  (TextView) findViewById(R.id.textViewContentSerienummer);
                String Serienummer = textViewSerienummer.getText().toString();
                Intent i = new Intent(getApplicationContext(), Historie_Activity.class);
                i.putExtra("value_seriennummer", Serienummer);
                startActivity(i);

            }
        });

        Button_rueck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                TextView textViewFirma =  (TextView) findViewById(R.id.textViewContentFirma);
                String Firma = textViewFirma.getText().toString();
                TextView textViewModel =  (TextView) findViewById(R.id.textViewContentModell);
                String Model = textViewModel.getText().toString();
                TextView textViewStoerung =  (TextView) findViewById(R.id.textViewContentStoerung);
                String Stoerung = textViewStoerung.getText().toString();
                TextView textViewAnsprechpartner =  (TextView) findViewById(R.id.textViewContentAnsprechpartner);
                String Ansprechpartner = textViewAnsprechpartner.getText().toString();

                Intent i = new Intent(getApplicationContext(), Rueckmeldung.class);

                i.putExtra("value_firma", Firma);
                i.putExtra("value_model", Model);
                i.putExtra("value_stoerung", Stoerung);
                i.putExtra("value_ansprechpartner", Ansprechpartner);
                i.putExtra("value_id", ID);

                startActivity(i);

            }
        });

        Button_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Functions Function = new Functions();
                    if( Function.isNetworkOnline(TicketDetailsActivity.this)) {

                        Intent i = new Intent(getApplicationContext(), InfoActivity.class);
                        i.putExtra("value_id", ID);
                        startActivity(i);
                    }
                    else{
                            Toast.makeText(getApplicationContext(), "Keine INternet verbindung", Toast.LENGTH_LONG).show();
                        }

                }
            });

        Button_eskalation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                TextView textViewFirma =  (TextView) findViewById(R.id.textViewContentFirma);
                String Firma = textViewFirma.getText().toString();
                TextView textViewModel =  (TextView) findViewById(R.id.textViewContentModell);
                String Model = textViewModel.getText().toString();
                TextView textViewStoerung =  (TextView) findViewById(R.id.textViewContentStoerung);
                String Stoerung = textViewStoerung.getText().toString();
                TextView textViewAnsprechpartner =  (TextView) findViewById(R.id.textViewContentAnsprechpartner);
                String Ansprechpartner = textViewAnsprechpartner.getText().toString();

                Intent i = new Intent(getApplicationContext(), Eskalation.class);

                i.putExtra("value_firma", Firma);
                i.putExtra("value_model", Model);
                i.putExtra("value_stoerung", Stoerung);
                i.putExtra("value_ansprechpartner", Ansprechpartner);
                i.putExtra("value_id", ID);

                startActivity(i);

            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ticket_details, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showMap(Uri geoLocation) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }



     /**  **/




    public class JSONSaveDetails extends AsyncTask<Integer, Integer, JSONObject> {




        @Override
        protected JSONObject doInBackground(Integer... args) {
            Functions Function = new Functions();
            Spinner spinnerStatus = (Spinner) findViewById(R.id.status_spinner);

            int selected = spinnerStatus.getSelectedItemPosition();

            switch (selected){

                case 0:
                    // Status = "Unbearbeitet";
                    Statusnum = "10";
                    break;
                case 1:
                    // Status = "Fahrt";
                    Statusnum = "11";
                    break;
                case 2:
                    //  Status = "In arbeit";
                    Statusnum = "12";
                    break;
                case 3:
                    //  Status = "offen";
                    Statusnum = "13";
                    break;

                case 4:
                    //  Status = "Erledigt";
                    Statusnum = "15";
                    break;
                case 5:
                    //  Status = "wartet";
                    Statusnum = "16";
                    break;
                case 6:
                    //   Status = "Ware bestellt";
                    Statusnum = "17";
                    break;
                case 7:
                    //  Status = "Ware da";
                    Statusnum = "18";
                    break;
                case 8:
                    //  Status = "Ware benötigt";
                    Statusnum = "19";
                    break;
                case 9:
                    //  Status = "installiert";
                    Statusnum = "20";
                    break;
                case 10:
                    // Status = "Eskalation";
                    Statusnum = "99";
                    break;

            }
            Log.d("Status",Statusnum);


            JSONObject json = Function.SaveDetails(Statusnum,ID);

            // check for login response
            // check log cat fro response
            Log.d("Create Response", json.toString());
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {

            try {

                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    System.out.println("LOL gehtb sofort"+ success);

                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);




                    }
                 else{

                    System.out.println("Y U NOT FUNCTION...");

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }




}
