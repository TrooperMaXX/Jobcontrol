package de.hoell.jobcontrol.ticketlist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ortiz.touch.TouchImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.hoell.jobcontrol.Jobcontrol;
import de.hoell.jobcontrol.MainActivity;
import de.hoell.jobcontrol.R;
import de.hoell.jobcontrol.query.Functions;
import de.hoell.jobcontrol.schein.Arbeitsschein;
import de.hoell.jobcontrol.session.SessionManager;
import de.hoell.jobcontrol.widget.WidgetProvider;


public class TicketDetailsActivity extends Activity {

 String user,gebiet;
    public boolean fertig=false;
    public static  int Statusnum;
    public TouchImageView img;
static String ID,email;
private static final String TAG_SUCCESS = "success";

    AlertDialog.Builder builderSingle;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        String test = getIntent().getStringExtra(WidgetProvider.EXTRA_WORD);
        Log.e("TEEEEEEEEEEST",""+test);
        setContentView(R.layout.activity_ticket_details);
        String Status = getIntent().getStringExtra("value_status");
        Button Button_maps = (Button) findViewById(R.id.button_maps);
        Button Button_save = (Button) findViewById(R.id.button_save);
        Button Button_his = (Button) findViewById(R.id.button_his);
        Button Button_rueck = (Button) findViewById(R.id.button_rueck);
        Button Button_info = (Button) findViewById(R.id.button_info);
        Button Button_eskalation = (Button) findViewById(R.id.button_eskalation);
        Button Button_img = (Button) findViewById(R.id.button_img);
        img = (TouchImageView) findViewById(R.id.img);

        int DropPos = getIntent().getIntExtra("value_droppos", 0);
        final String Firma = getIntent().getStringExtra("value_firma");
        int KundenNr = getIntent().getIntExtra("value_kundennr", 0);
        final String Adresse = getIntent().getStringExtra("value_adresse");
        String Standort = getIntent().getStringExtra("value_standort");
        final String Modell = getIntent().getStringExtra("value_modell");//Gerät
        final String Serienummer = getIntent().getStringExtra("value_serienummer");
        String Stoerung = getIntent().getStringExtra("value_stoerung");
        String Ansprechpartner = getIntent().getStringExtra("value_ansprechpartner");
        String Telefonnummer = getIntent().getStringExtra("value_telefonnummer");
        String Angenommen = getIntent().getStringExtra("value_angenommen");
        String Termin = getIntent().getStringExtra("value_termin");
        String Oeffnung = getIntent().getStringExtra("value_oeffnung");
        String Auftragsnr = getIntent().getStringExtra("value_auftragnr");
        String Wvnr = getIntent().getStringExtra("value_wvnr");
        String Fleet = getIntent().getStringExtra("value_fleet");
        String Annahme = getIntent().getStringExtra("value_annahme");
        int bogenverfuegbar = getIntent().getIntExtra("value_bogenverfuegbar",0);
        final String uri = getIntent().getStringExtra("value_uri");
        Statusnum = getIntent().getIntExtra("value_statusnum",0);
        ID = getIntent().getStringExtra("value_id");
        email = getIntent().getStringExtra("value_email");

        Log.d("INTENTEXTRA:", Status + " " + DropPos + " " + Adresse + " " + Standort + " " + Modell + " " + Serienummer + " " + Stoerung + " " + Ansprechpartner + " " + Telefonnummer + " " + Termin + " " + Annahme);
        Log.e("Statusnum",String.valueOf(Statusnum));
       if (Statusnum<90){
            Spinner spinnerStatus = (Spinner) findViewById(R.id.status_spinner);
            spinnerStatus.setSelection(DropPos);
            spinnerStatus.setVisibility(View.VISIBLE);
        }
        else{
           Spinner spinnerStatus = (Spinner) findViewById(R.id.status_spinner_eskalation);
         spinnerStatus.setSelection(DropPos);
          spinnerStatus.setVisibility(View.VISIBLE);}



        if (bogenverfuegbar>0){
            Button_img.setVisibility(View.VISIBLE);
        }else{
            Button_img.setVisibility(View.GONE);
        }


        TextView textViewKunde = (TextView) findViewById(R.id.textViewFirma);
        String Kundentxt= "Firma ("+KundenNr+")";
        textViewKunde.setText(Kundentxt);

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

        TextView textViewOeffnung = (TextView) findViewById(R.id.textViewContentOeffnung);
        if(Oeffnung==null){Oeffnung=" ";}
        textViewOeffnung.setText(Oeffnung);


        TextView textViewAuftragsnr = (TextView) findViewById(R.id.textViewContentAuftragsnr);
        if(Auftragsnr == null){Auftragsnr=" ";}
        textViewAuftragsnr.setText(Auftragsnr);

        TextView textViewWvnr = (TextView) findViewById(R.id.textViewContentWvnr);
        if(Wvnr.equals("null")){Wvnr=" ";}
        textViewWvnr.setText(Wvnr);

        TextView textViewFleet = (TextView) findViewById(R.id.textViewContentFleet);
        if(Fleet.equals("null")){Fleet=" ";}
        textViewFleet.setText(Fleet);

        TextView textViewAnnahme = (TextView) findViewById(R.id.textViewContentAnnahme);
        textViewAnnahme.setText(Annahme);

        Button_maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showMap(Uri.parse(uri));
            }
        });

        Button_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getImgs(ID,0,TicketDetailsActivity.this);
            }
        });

        img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                deshowImage();
                return true;
            }
        });





        Button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Functions Function = new Functions();

                if( Function.isNetworkOnline(TicketDetailsActivity.this)){
                    try {
                        new JSONSaveDetails().execute().get(30000, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException | ExecutionException | TimeoutException e) {
                        e.printStackTrace();
                    }
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


                TextView textViewFirma = (TextView) findViewById(R.id.textViewContentFirma);
                String Firma = textViewFirma.getText().toString();
                TextView textViewModel = (TextView) findViewById(R.id.textViewContentModell);
                String Model = textViewModel.getText().toString();
                TextView textViewStoerung = (TextView) findViewById(R.id.textViewContentStoerung);
                String Stoerung = textViewStoerung.getText().toString();
                TextView textViewAnsprechpartner = (TextView) findViewById(R.id.textViewContentAnsprechpartner);
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
                if (Function.isNetworkOnline(TicketDetailsActivity.this)) {

                    Intent i = new Intent(getApplicationContext(), InfoActivity.class);
                    i.putExtra("value_id", ID);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Keine INternet verbindung", Toast.LENGTH_LONG).show();
                }

            }
        });

        Button_eskalation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                TextView textViewFirma = (TextView) findViewById(R.id.textViewContentFirma);
                String Firma = textViewFirma.getText().toString();
                TextView textViewModel = (TextView) findViewById(R.id.textViewContentModell);
                String Model = textViewModel.getText().toString();
                TextView textViewStoerung = (TextView) findViewById(R.id.textViewContentStoerung);
                String Stoerung = textViewStoerung.getText().toString();
                TextView textViewAnsprechpartner = (TextView) findViewById(R.id.textViewContentAnsprechpartner);
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
      builderSingle = new AlertDialog.Builder(
                TicketDetailsActivity.this);
        builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Select One Name:-");
       arrayAdapter = new ArrayAdapter<String>(
                TicketDetailsActivity.this,
                android.R.layout.select_dialog_singlechoice);
        SessionManager session = new SessionManager(this);
        gebiet ="null";
                gebiet=session.getGebiet();
        user=session.getUser();

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


        if (id == R.id.action_verschieben) {

            if (!gebiet.equals("null")){


                try {
                    JSONObject gebietstech = new JSONGebTech(gebiet,user).execute().get(30000, TimeUnit.MILLISECONDS);
                    int g_success = gebietstech.getInt(TAG_SUCCESS);
                    if (g_success == 1) {
                        JSONArray gebtech = gebietstech.getJSONArray("gebtech");
                        for (int i = 0; i < gebtech.length(); i++) {
                            JSONObject c = gebtech.getJSONObject(i);
                            String techniker= c.getString("user_name");
                            arrayAdapter.add(techniker);
                        }

                    }


                } catch (InterruptedException | ExecutionException | TimeoutException | JSONException e) {
                    e.printStackTrace();
                }

                builderSingle.setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                builderSingle.setAdapter(arrayAdapter,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String strName = arrayAdapter.getItem(which);
                                AlertDialog.Builder builderInner = new AlertDialog.Builder(
                                        TicketDetailsActivity.this);

                                Functions Function = new Functions();

                                if( Function.isNetworkOnline(TicketDetailsActivity.this)){
                                    try {
                                        JSONObject verschoben=new JSONVerschieben(ID,strName,user).execute().get(30000, TimeUnit.MILLISECONDS);

                                        try {

                                            int success = verschoben.getInt(TAG_SUCCESS);

                                            if (success == 1) {
                                                System.out.println("LOL gehtb sofort"+ success);


                                                builderInner.setMessage("Ticket an " + strName + " verschoben");
                                                builderInner.setTitle("Ticket Verschoben");
                                                builderInner.setPositiveButton("Ok",
                                                        new DialogInterface.OnClickListener() {

                                                            @Override
                                                            public void onClick(
                                                                    DialogInterface dialog,
                                                                    int which) {
                                                                dialog.dismiss();
                                                            }
                                                        });
                                                builderInner.show();

                                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(i);


                                            }
                                            else{

                                                System.out.println("Y U NOT FUNCTION...");

                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    } catch (InterruptedException | ExecutionException | TimeoutException e) {
                                        e.printStackTrace();
                                    }
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "Keine INternet verbindung", Toast.LENGTH_SHORT).show();}

                            }
                        });
            }else{
                Toast.makeText(getApplicationContext(), "Keine anderen Techniker in deinem Gebiet Verfügbar", Toast.LENGTH_SHORT).show();}

            builderSingle.show();



    /*        Functions Function = new Functions();

            if( Function.isNetworkOnline(TicketDetailsActivity.this)){
                new JSONSaveDetails().execute();
            }
            else {
                Toast.makeText(getApplicationContext(), "Keine INternet verbindung", Toast.LENGTH_SHORT).show();}
    */
        }

        if (id == R.id.action_arbeitsschein) {
            TextView textViewSerienummer =  (TextView) findViewById(R.id.textViewContentSerienummer);
            String Serienummer = textViewSerienummer.getText().toString();

            TextView textViewAuftragsnr =  (TextView) findViewById(R.id.textViewContentAuftragsnr);
            String Auftragsnr = textViewAuftragsnr.getText().toString();

            TextView textViewAnsprechpartner =  (TextView) findViewById(R.id.textViewContentAnsprechpartner);
            String Ansprechpartner = textViewAnsprechpartner.getText().toString();

            TextView textViewStandort =  (TextView) findViewById(R.id.textViewContentStandort);
            String Standort = textViewStandort.getText().toString();

            TextView textViewError =  (TextView) findViewById(R.id.textViewContentStoerung);
            String Error = textViewError.getText().toString();

            Intent i = new Intent(getApplicationContext(), Arbeitsschein.class);
            i.putExtra("value_seriennummer", Serienummer);
            i.putExtra("value_auftragsnr", Auftragsnr);
            i.putExtra("value_name", Ansprechpartner);
            i.putExtra("value_standort", Standort);
            i.putExtra("value_error", Error);
            i.putExtra("value_email", email);
            i.putExtra("value_id", ID);
            startActivity(i);

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
            JSONObject json;
            if (Statusnum<99){
                Spinner spinnerStatus = (Spinner) findViewById(R.id.status_spinner);
                int selected = spinnerStatus.getSelectedItemPosition();

                switch (selected){

                    case 0:
                        // Status = "Unbearbeitet";
                        Statusnum = 10;
                        break;
                    case 1:
                        // Status = "Fahrt";
                        Statusnum = 11;
                        break;
                    case 2:
                        //  Status = "In arbeit";
                        Statusnum = 12;
                        break;
                    case 3:
                        //  Status = "offen";
                        Statusnum = 13;
                        break;

                    case 4:
                        //  Status = "Erledigt";
                        Statusnum = 15;

                        break;
                    case 5:
                        //  Status = "wartet";
                        Statusnum = 16;
                        break;
                    case 6:
                        //   Status = "Ware bestellt";
                        Statusnum = 17;
                        break;
                    case 7:
                        //  Status = "Ware da";
                        Statusnum = 18;
                        break;
                    case 8:
                        //  Status = "Ware benötigt";
                        Statusnum = 19;
                        break;
                    case 9:
                        //  Status = "installiert";
                        Statusnum = 20;
                        break;
                    case 10:
                        // Status = "Eskalation";
                        Statusnum = 99;
                        break;

                }
                // Log.d("Status",Statusnum);


                json = Function.SaveDetails(Statusnum,ID,user);

            }
            else{
                Spinner spinnerStatus = (Spinner) findViewById(R.id.status_spinner_eskalation);
                int selected = spinnerStatus.getSelectedItemPosition();

                switch (selected){

                    case 0:
                        // Status = "Eskalation";
                        Statusnum = 99;
                        break;
                    case 1:
                        // Status = "Eskalation in arbeit";
                        Statusnum = 100;
                        break;
                    case 2:
                        //  Status = "Eskaltion erledigt";
                        Statusnum = 101;
                        break;




                }
                // Log.d("Status",Statusnum);


                json = Function.SaveDetails(Statusnum,ID,user);
            }




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


    private class JSONGebTech extends AsyncTask<Integer, Integer, JSONObject> {
        private String mGebiet,mUser;
        public JSONGebTech(String gebiet,String user) {
            mGebiet =gebiet;
            mUser = user;
        }

        @Override
        protected JSONObject doInBackground(Integer... params) {
            Functions Function = new Functions();
            JSONObject json_gebtech = Function.GebTech(mGebiet,mUser);

            return json_gebtech;
        }
    }

    private class JSONVerschieben extends AsyncTask<Integer, Integer, JSONObject> {
        private String mID ,mTechniker,mUser;
        public JSONVerschieben(String ID,String techniker,String user) {
            mID=ID;
            mTechniker=techniker;
            mUser=user;
        }

        @Override
        protected JSONObject doInBackground(Integer... params) {
            Functions Function = new Functions();
            JSONObject json_gebtech = Function.Verschieben(mID, mTechniker, mUser);

            return json_gebtech;
        }
    }
       private class JSONFileNumbers extends AsyncTask<Integer, Integer, JSONObject> {
           private String mID,mplaceholder="";

           public JSONFileNumbers(String ID) {
               mID=ID;

           }

           @Override
           protected JSONObject doInBackground(Integer... params) {
               Functions Function = new Functions();
               JSONObject json_filenumbers = Function.getFilenumbers(mID, mplaceholder);

               return json_filenumbers;
           }
       }

    public void getImgs(String TicketID, int current_img, final Context context) {
        //Log.e("get bis getImgs","1");
        int length = 1;
        Bitmap bmp=null;
        JSONArray Files=null ;
        Functions Function = new Functions();
        JSONObject json_filenumbers = null;
       if( Function.isNetworkOnline(Jobcontrol.getAppCtx())) {

            try {//Log.e("get bis getImgs","1.2");
                json_filenumbers = new JSONFileNumbers(TicketID).execute().get(30000, TimeUnit.MILLISECONDS);
               // Log.e("get bis nach json","2");
                int success = json_filenumbers.getInt(TAG_SUCCESS);


                if (success == 1) {
                  //  Log.e("get bis success","3");
                    length = json_filenumbers.getInt("length");
                    Files=json_filenumbers.getJSONArray("FileNumbers");
                }else {
                    length=1;
                    Files=null;
                }
            } catch (JSONException | InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();

            }

       // Log.e("get bis nach success","4");
        }else{
           Files=null;
       }

        File PATHdir =new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/Jobcontrol/" + TicketID+"/")));
        PATHdir.mkdirs();
       // Log.e("get bis PATH",PATHdir.toString());

        String filePath= String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/Jobcontrol/"+TicketID+"/"+current_img+".png"));
        File file = new File(filePath);

        if(file.exists()){
            bmp=BitmapFactory.decodeFile(filePath);
            img = showImage(bmp);
            Log.e("yay","Datei vom speicher gelesen");
        }
        else{
            try {

                    if (Files!=null) {


                       // Log.e("get bis vor ASYNC", "5");
                        System.out.println("AsyncIMG(Files.getString(" + String.valueOf(current_img) + ")," + TicketID + "," + String.valueOf(current_img) + ")");
                        Log.e("Async:", "gestartet");
                      new AsyncIMG(Files.get(current_img).toString(), TicketID, String.valueOf(current_img),context).execute();



                       // Log.e("get bis nach ASYNC", "6");
                    }


            } catch (JSONException e) {

                e.printStackTrace();
            }

        }
      //  Log.e("get bis vor showIMG","7");

        TextView BildAnz = (TextView) findViewById(R.id.textViewSeitenAnz);
        BildAnz.setVisibility(View.VISIBLE);
        BildAnz.setText((current_img+1) + "/"+ length);
        //Log.e("get bis nach showIMG","8");


        final String finalTicketID=TicketID;
        final int next_img=current_img+1;
        final boolean next;
        if (next_img<length){
            next=true;
            }
        else {
            next=false;
        }
        final int prev_img=current_img-1;
        final boolean prev;
        if (prev_img>=0){
            prev=true;
        }
        else {
            prev=false;
        }
        img.setOnTouchListener(new SwipeDetect() {
            //NextBild
            public void onSwipeLeft() {


                    if (next){
                       getImgs(finalTicketID,next_img,context);
                    }else {
                        Toast.makeText(getApplicationContext(), "Kein Weiteres Bild verfügbar", Toast.LENGTH_SHORT).show();

                    }

                //     Toast.makeText(getApplicationContext(), "onSwipeLeft", Toast.LENGTH_SHORT).show();


            }

            public void onSwipeRight() {


                if (prev){
                    getImgs(finalTicketID,prev_img,context);
                }else {
                    Toast.makeText(getApplicationContext(), "Kein Weiteres Bild verfügbar", Toast.LENGTH_SHORT).show();

                }
               // Toast.makeText(getApplicationContext(), "onSwipeRight", Toast.LENGTH_SHORT).show();
            }
        });



    }


    public TouchImageView showImage(Bitmap bmp) {

         img = (TouchImageView) findViewById(R.id.img);

        img.setVisibility(View.VISIBLE);


        System.out.println("Datei von speicher gelesen");

        if (bmp != null) {

            if((bmp.getWidth() <=4096)&&(bmp.getHeight() <=4096)){

                img.setImageBitmap(bmp);


                img.resetZoom();
            }else{
                Toast.makeText(getApplicationContext(), "Bild ist zu groß Bitte im Downloadordner seperat anschauen", Toast.LENGTH_LONG).show();
            }
        }

        return img;
    }





    public void deshowImage() {

         img = (TouchImageView) findViewById(R.id.img);
        img.setVisibility(View.GONE);
        TextView BildAnz = (TextView) findViewById(R.id.textViewSeitenAnz);
        BildAnz.setVisibility(View.GONE);

    }

    private class AsyncIMG extends AsyncTask<Integer, Integer, Bitmap> {
        private String mID,mTicketID, mName;
        private  Bitmap mbmp;
        private Context mContext;
        private ProgressDialog pDialog;


        public AsyncIMG(String ID,String TicketID,String Name,Context context) {
            mID=ID;
            mTicketID= TicketID;
            mName= Name;
            mContext= context;
            fertig=false;

        }

        /**
         * Before starting background thread Show Progress Bar Dialog
         * */


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(mContext);
            pDialog.setMessage("Downloading file. Please wait...");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setIndeterminate(true);
            pDialog.show();
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            InputStream in = null;
            try {

                in = new URL("http://5.158.136.15/job/android/bild.php?id="+mID).openStream();
                Log.e("bildurl","http://5.158.136.15/job/android/bild.php?id="+mID);
                mbmp = BitmapFactory.decodeStream(in);
                in.close();
                /**SAVE IMG AS PNG**/
                FileOutputStream out = null;
                File dir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                String PATH = dir.toString()+"/Jobcontrol/"+mTicketID+"/";
                File PATHdir =new File(PATH);
                PATHdir.mkdirs();
                Log.e("PATH",PATH);


                    out = new FileOutputStream(dir+"/Jobcontrol/"+mTicketID+"/"+mName+".png");
                    mbmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                    // PNG is a lossless format, the compression factor (100) is ignored

            } catch (IOException e) {
                e.printStackTrace();
            }

            return mbmp;
        }
        @Override
        protected void onPostExecute(Bitmap bmp) {
            // dismiss the dialog after the file was downloaded
            img = showImage(bmp);
            pDialog.dismiss();


        }
    }






}
