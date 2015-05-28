package de.hoell.jobcontrol;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import de.hoell.jobcontrol.adapter.SpecialAdapter;
import de.hoell.jobcontrol.query.Functions;
import de.hoell.jobcontrol.session.SessionManager;

import de.hoell.jobcontrol.ticketlist.TicketDetailsActivity;
import de.hoell.jobcontrol.ticketlist.Tickets;




public class TicketFragment extends ListFragment {



    private static final String TAG_SUCCESS = "success";

    String Status = "Unbearbeitet";
    public int DropPos= 0;
    public  Date Terminsdf,Terminsdfend;
    ArrayList<HashMap<String, String>> TheTickets = new ArrayList<HashMap<String, String>>();


    List<Tickets> ticketsList = new ArrayList<Tickets>();

    public JSONArray Ticketliste = null;
    public JSONArray Ticketliste_neu = null;
    public JSONArray Ticketliste_old = null;
    final Context moinContext = MainActivity.context;





    private OnTicketInteractionListener mListener;





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {
            Functions Function = new Functions();

            if( Function.isNetworkOnline(getActivity().getApplicationContext())) {
                JSONObject json = new JSONMyTickets(getActivity().getApplicationContext()).execute().get();
                if (json != null) {
                    try {

                        int success = json.getInt(TAG_SUCCESS);

                        if (success == 1) {

                            Ticketliste = json.getJSONArray("tickets");
                            for (int i = 0; i < Ticketliste.length(); i++) {
                                JSONObject c = Ticketliste.getJSONObject(i);
                                String Farbe="#ffffffff";

                                int imgid= getActivity().getApplicationContext().getResources().getIdentifier("ic_status_red", "mipmap", "de.hoell.jobcontrol");


                                String Firma = c.getString("Firma");
                                int Statusnum = c.getInt("Status");


                                switch (Statusnum) {

                                    case 10:
                                        Status = "Unbearbeitet";
                                        DropPos = 0;
                                        Farbe="#ffffffff";
                                        imgid= getActivity().getApplicationContext().getResources().getIdentifier("ic_status_red","mipmap","de.hoell.jobcontrol");
                                        break;
                                    case 11:
                                        Status = "Fahrt";
                                        DropPos = 1;
                                        Farbe="#ffff4d00";
                                        imgid= getActivity().getApplicationContext().getResources().getIdentifier("ic_status_green","mipmap","de.hoell.jobcontrol");
                                        break;
                                    case 12:
                                        Status = "In arbeit";
                                        DropPos = 2;
                                        Farbe="#ffffffff";
                                        imgid= getActivity().getApplicationContext().getResources().getIdentifier("ic_status_green","mipmap","de.hoell.jobcontrol");
                                        break;
                                    case 13:
                                        Status = "offen";
                                        DropPos = 3;
                                        Farbe="#ffffffff";
                                        imgid= getActivity().getApplicationContext().getResources().getIdentifier("ic_status_orange","mipmap","de.hoell.jobcontrol");
                                        break;
                                    case 15:
                                        Status = "Erledigt";
                                        DropPos = 4;

                                        break;
                                    case 16:
                                        Status = "wartet";
                                        DropPos = 5;
                                        Farbe="#ffffffff";
                                        imgid= getActivity().getApplicationContext().getResources().getIdentifier("ic_status_orange","mipmap","de.hoell.jobcontrol");
                                        break;
                                    case 17:
                                        Status = "Ware bestellt";
                                        DropPos = 6;
                                        Farbe="#ffffffff";
                                        imgid= getActivity().getApplicationContext().getResources().getIdentifier("ic_status_orange","mipmap","de.hoell.jobcontrol");
                                        break;
                                    case 18:
                                        Status = "Ware da";
                                        DropPos = 7;
                                        Farbe="#ffff4d00";
                                        imgid= getActivity().getApplicationContext().getResources().getIdentifier("ic_status_green","mipmap","de.hoell.jobcontrol");
                                        break;
                                    case 19:
                                        Status = "Ware benötigt";
                                        DropPos = 8;
                                        Farbe="#ffffffff";
                                        imgid= getActivity().getApplicationContext().getResources().getIdentifier("ic_status_orange","mipmap","de.hoell.jobcontrol");
                                        break;
                                    case 20:
                                        Status = "installiert";
                                        DropPos = 9;
                                        Farbe="#FF0000";
                                        imgid= getActivity().getApplicationContext().getResources().getIdentifier("ic_status_red","mipmap","de.hoell.jobcontrol");
                                        break;
                                    case 99:
                                        Status = "Eskalation";
                                        DropPos = 10;
                                        imgid= getActivity().getApplicationContext().getResources().getIdentifier("ic_status_orange","mipmap","de.hoell.jobcontrol");
                                        break;
                                    case 100:
                                        Status = "Eskalation in arbeit";
                                        DropPos = 10;
                                        imgid= getActivity().getApplicationContext().getResources().getIdentifier("ic_status_orange","mipmap","de.hoell.jobcontrol");
                                        break;

                                }

                                String Modell = c.getString("Modell");

                                String Strasse = c.getString("Straße");
                                String Plz = c.getString("Plz");
                                String Ort = c.getString("Ort");

                                String ort = Plz + " " + Ort;

                                String Fehler = c.getString("Stoerung");

                                ticketsList.add(new Tickets(Firma + ", " + Modell + ", " + Status));
                                int hintergrundid ;

                                String Termintag = c.getString("terminTag");
                                String Terminende = c.getString("terminEnde");
                                int Termintyp = c.getInt("terminTyp");
                                Log.e("terminTag",":"+Termintag);
                                String formated_termintag="",formated_terminende;
                                String finalTermin="";
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN);
                                SimpleDateFormat edf = new SimpleDateFormat("dd-MM-yyyy  HH:mm", Locale.GERMAN);

                                boolean isheute=false;
                                switch (Termintyp){

                                    case 0:

                                        Terminsdf = sdf.parse(Termintag);
                                        formated_termintag = edf.format(Terminsdf);
                                        finalTermin="Termin: "+formated_termintag;


                                        isheute =Function.isTerminheute(Terminsdf);
                                        break;
                                    case 1:

                                        Terminsdf = sdf.parse(Termintag);
                                        formated_termintag = edf.format(Terminsdf);
                                        Terminsdfend = sdf.parse(Terminende);
                                        formated_terminende = edf.format(Terminsdfend);
                                        finalTermin="Ab "+formated_termintag+" bis "+ formated_terminende;


                                        isheute =Function.isTerminheute(Terminsdf);
                                        break;
                                    case 2:
                                        Terminsdf = sdf.parse(Termintag);
                                        formated_termintag = edf.format(Terminsdf);
                                        Terminsdfend = sdf.parse(Terminende);
                                        formated_terminende = edf.format(Terminsdfend);
                                        finalTermin="Von "+formated_termintag+" bis "+ formated_terminende;


                                        isheute =Function.isTerminheute(Terminsdf);
                                        break;
                                    case 3:

                                        Terminsdf = sdf.parse(Termintag);
                                        formated_termintag = edf.format(Terminsdf);
                                        finalTermin="Termin: "+formated_termintag;


                                        isheute =Function.isTerminheute(Terminsdf);
                                        break;
                                }







                                    if (isheute){
                                        System.out.println("yay");
                                        hintergrundid= getActivity().getApplicationContext().getResources().getIdentifier("rot","drawable","de.hoell.jobcontrol");

                                    }
                                    else{
                                        hintergrundid= getActivity().getApplicationContext().getResources().getIdentifier("weis","drawable","de.hoell.jobcontrol");
                                    }

                                String Oeffnung = c.getString("oeffnung");
                                int imgsichtbar=0;
                                boolean sichtbar=false;
                                if(Oeffnung.equals("")){
                                    Log.e("WARUNG","unsichtbar");
                                    sichtbar=false;
                                }
                                else{
                                    Log.e("WARUNG", "sichtbar");
                                    sichtbar=true;
                                    imgsichtbar= getActivity().getApplicationContext().getResources().getIdentifier("ic_warning","drawable","de.hoell.jobcontrol");
                                }

                                String auanr = c.getString("Auftragtkd");


                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("Firma", Firma);
                                map.put("Status", Status);
                                map.put("Termin", finalTermin);
                                map.put("AuaNr", auanr);
                                map.put("Model",Modell);
                                map.put("Adresse",Strasse);
                                map.put("Ort",ort);
                                map.put("Fehler",Fehler);
                                map.put("Farbe",Farbe);
                                map.put("Sichtbar",String.valueOf(imgsichtbar));
                                map.put("Status_ic",String.valueOf(imgid));
                                map.put("Hintergrund",String.valueOf(hintergrundid));


                                Log.e("Statusid",""+imgid);

                                TheTickets.add(map);


                            }
                            System.out.println("Abfrage" + TheTickets);

                        } else {


                        }

                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }

                    setListAdapter(new SpecialAdapter(getActivity(),TheTickets,R.layout.row_list,
                            new String[] {"Firma", "Status", "Adresse","Ort", "Model", "Fehler", "Farbe", "Status_ic","Hintergrund","Termin","AuaNr","Sichtbar"},
                            new int[] {R.id.FIRMA_CELL,R.id.STATUS_CELL, R.id.ADRESSE_CELL, R.id.ORT_CELL, R.id.MODEL_CELL, R.id.FEHLER_CELL,R.color.ticket_list,R.id.Status_img,R.id.BACKGROUD_all,R.id.TERMIN_CELL,R.id.AUA_CELL,R.id.img_warning}));
                }
            }
            else{

             Toast.makeText(getActivity().getApplicationContext(), "Keine Internet verbindung Bitte zum Offlinemodus wechseln", Toast.LENGTH_LONG).show();

                Fragment fragment = null;
                fragment = new OfflineFragment();
                if (fragment != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_container, fragment).commit();

                }
            }
//
          /*/  TODO: autoupdater*/
          ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
            SessionManager session=new SessionManager(MainActivity.context);
            int zeit=session.getZeit();
            System.out.println("UpdateTimer auf " + zeit + " gesetzt");
// This schedule a runnable task every 15 minutes
            scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    new JSONMyTickets(MainActivity.context).execute();
                }
            }, 0, zeit, TimeUnit.MINUTES);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }



    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView lv = getListView();
        ColorDrawable sage = new ColorDrawable(getActivity().getApplicationContext().getResources().getColor(R.color.ticket_list_divider));
        lv.setDivider(sage);
        lv.setDividerHeight(10);
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnTicketInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTicketInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent intent = new Intent(getActivity(), TicketDetailsActivity.class);
        try {
            JSONObject extra = Ticketliste.getJSONObject(position);

            int Statusnum = extra.getInt("Status");
            String StringStatusnum = extra.getString("Status");
            Status= getStatus(Statusnum);
            DropPos= getDropPos(Statusnum);
            intent.putExtra("value_statusnum", Statusnum);

            String ID = extra.getString("ID");
            intent.putExtra("value_id", ID);

            intent.putExtra("value_status", Status);
            intent.putExtra("value_droppos", DropPos);

            String Firma = extra.getString("Firma");
            intent.putExtra("value_firma", Firma);

            String Strasse = extra.getString("Straße");
            String Plz = extra.getString("Plz");
            String Ort = extra.getString("Ort");

            String Adresse = Strasse + "\n" + Plz + " " + Ort;

            String uri = "geo:"+ 0 + "," + 0 + "?q="+ Strasse + "%20" + Plz + "%20"+ Ort;

            intent.putExtra("value_adresse", Adresse);
            intent.putExtra("value_uri", uri);

            String Standort = extra.getString("Standort");
            intent.putExtra("value_standort", Standort);

            String Modell = extra.getString("Modell");
            intent.putExtra("value_modell", Modell);

            String Serienummer = extra.getString("geraetenr");
            intent.putExtra("value_serienummer", Serienummer);

            String Stoerung = extra.getString("Stoerung");
            intent.putExtra("value_stoerung", Stoerung);

            String Ansprechpartner = extra.getString("Ansprechpartner");
            intent.putExtra("value_ansprechpartner", Ansprechpartner);

            String Telefonnummer = extra.getString("telnummer");
            intent.putExtra("value_telefonnummer", Telefonnummer);

            String Oeffnung = extra.getString("oeffnung");
            intent.putExtra("value_oeffnung", Oeffnung);


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN);
            SimpleDateFormat edf = new SimpleDateFormat("dd-MM-yyyy  HH:mm", Locale.GERMAN);

           String Angenommen = extra.getString("Datum");
            Date angenomensdf = sdf.parse(Angenommen);
            String finalAngenommen = edf.format(angenomensdf);
              intent.putExtra("value_angenommen", finalAngenommen);


            String Termintag = extra.getString("terminTag");
            String Terminende = extra.getString("terminEnde");
            int Termintyp = extra.getInt("terminTyp");
            String Termin="";

            Log.e("terminTag",":"+Termintag);
            String formated_termintag="",formated_terminende;



            switch (Termintyp){

                case 0:

                    Terminsdf = sdf.parse(Termintag);
                    formated_termintag = edf.format(Terminsdf);
                    Termin="Termin: "+formated_termintag;



                    break;
                case 1:

                    Terminsdf = sdf.parse(Termintag);
                    formated_termintag = edf.format(Terminsdf);
                    Terminsdfend = sdf.parse(Terminende);
                    formated_terminende = edf.format(Terminsdfend);
                    Termin="Ab "+formated_termintag+" bis "+ formated_terminende;



                    break;
                case 2:
                    Terminsdf = sdf.parse(Termintag);
                    formated_termintag = edf.format(Terminsdf);
                    Terminsdfend = sdf.parse(Terminende);
                    formated_terminende = edf.format(Terminsdfend);
                    Termin="Von "+formated_termintag+" bis "+ formated_terminende;

                    break;
                case 3:

                    Terminsdf = sdf.parse(Termintag);
                    formated_termintag = edf.format(Terminsdf);
                    Termin="Termin: "+formated_termintag;



                    break;
            }



            intent.putExtra("value_termin", Termin);

            String Auftragsnr = extra.getString("Auftragtkd");
            intent.putExtra("value_auftragnr", Auftragsnr);


            String wvnr = extra.getString("wvt");
            intent.putExtra("value_wvnr", wvnr);

            String fleet = extra.getString("fleet");
            intent.putExtra("value_fleet", fleet);

            String Annahmekue = extra.getString("annahmedurch");
            String Annahme = null;

          if (Annahmekue.equals("mt")){
               Annahme="Thomas Mersch";
            }else if(Annahmekue.equals("am")){

              Annahme="Andrej Makarevic";
          }
          else if(Annahmekue.equals("mt")){

              Annahme="Thomas Mersch";
          }
          else if(Annahmekue.equals("ubu")){

              Annahme="Ursula Büchel";
          }
          else if(Annahmekue.equals("ao")){

              Annahme="Atila Ovari";
          }
          else if(Annahmekue.equals("ap")){

              Annahme="Anton Pignar";
          }
          else if(Annahmekue.equals("ce")){

              Annahme="Christian Einbock";
          }
          else if(Annahmekue.equals("ch")){

              Annahme="Christelle Heimlich";
          }
          else if(Annahmekue.equals("dg")){

              Annahme="Daniel Gloger";
          }
          else if(Annahmekue.equals("dj")){

              Annahme="Dirk Junker";
          }
          else if(Annahmekue.equals("jp")){

              Annahme="Jan Pignar";
          }
          else if(Annahmekue.equals("kd")){

              Annahme="Kevin Donnell";
          }
          else if(Annahmekue.equals("kls")){

              Annahme="Klaus Späth";
          }
          else if(Annahmekue.equals("lb")){

              Annahme="Leon Bauer";
          }
          else if(Annahmekue.equals("mm")){

              Annahme="Michael Maurer";
          }
          else if(Annahmekue.equals("ms")){

              Annahme="Manuel Stein";
          }
          else if(Annahmekue.equals("mx")){

              Annahme="Max Stäglich";
          }
          else if(Annahmekue.equals("ns")){

              Annahme="Nicolas Stecher";
          }
          else if(Annahmekue.equals("rp")){

              Annahme="Ricardo Pallek";
          }
          else if(Annahmekue.equals("se")){

              Annahme="Stefan Essig";
          }
          else if(Annahmekue.equals("sp")){

              Annahme="Sven Pignar";
          }
          else if(Annahmekue.equals("sr")){

              Annahme="Stefan Reinelt";
          }
          else if(Annahmekue.equals("tm")){

              Annahme="Torsten Mehlhorn";
          }else{
              Annahme=Annahmekue;
          }

            intent.putExtra("value_annahme", Annahme);

            getActivity().startActivity(intent);

        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }


        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.

        }
    }




    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnTicketInteractionListener {

        public void onTicketInteraction(String id);
    }










    public class JSONMyTickets extends AsyncTask<String, String, JSONObject> {

        private Context mContext;


        public JSONMyTickets (Context context){
            mContext = context;
        }




        @Override
        protected JSONObject doInBackground(String... args) {


            String user;



            Functions Function = new Functions();
            SessionManager session = new SessionManager(mContext);
            user = session.getUser();

               JSONObject json = Function.MyTickets(user);
                if(json!=null){


                    int success = 0;
                    try {
                        success = json.getInt(TAG_SUCCESS);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (success == 1) {
                            neueTickets(json,mContext);

                    }


                }

            System.out.println("is JSON null?" + json);




            // check for login response
            // check log cat fro response
            if (json!=null){
                String jstring = json.toString();
                System.out.println("is JSONstring null?" +jstring);
                session.saveJSON(jstring);
                Log.d("Create Response", json.toString());
            }
            return json;

        }



    }

    private void neueTickets(JSONObject neuJson,Context mContext) {

        SessionManager session = new SessionManager(mContext);
        ArrayList <Integer> old = new ArrayList <Integer> ();
        ArrayList <Integer> neu = new ArrayList <Integer> ();
        ArrayList <String> old_obj = new ArrayList <> ();
        ArrayList <String> neu_obj = new ArrayList <> ();
        ArrayList <Integer> neueTickets = new ArrayList<Integer>();
        ArrayList <Integer> Ticketpos = new ArrayList<Integer>();



        if (session.isJSONsaved()) {

            String jstring = session.getJstring();
                //********ALTE TICKETS**************
            if (jstring != null) {
                try {
                    JSONObject jsonData = new JSONObject(jstring);
                    int success = jsonData.getInt(TAG_SUCCESS);


                    if (success == 1) {

                        Ticketliste_old = jsonData.getJSONArray("tickets");
                        for (int i = 0; i < Ticketliste_old.length(); i++) {
                            JSONObject c = Ticketliste_old.getJSONObject(i);
                            int ID = c.getInt("ID");
                            old.add(ID);
                            old_obj.add(c.toString());
                        }
                    }
                    //********NEUE TICKETS**************
                    if (neuJson != null) {


                            success = neuJson.getInt(TAG_SUCCESS);

                            if (success == 1) {

                                Ticketliste_neu = neuJson.getJSONArray("tickets");
                                for (int i = 0; i < Ticketliste_neu.length(); i++) {
                                    JSONObject c = Ticketliste_neu.getJSONObject(i);
                                    int ID = c.getInt("ID");
                                    neu.add(ID);
                                    neu_obj.add(c.toString());
                                }
                            }

                    }else{
                        Log.e("ERROR","Abgleich fehlgeschlagen weil JSON null");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Log.e("ERROR","Abgleich fehlgeschlagen weil string null");
            }



            for (int j = 0; j < neu.size(); j++){

                if (!old.contains(neu.get(j))){

                    neueTickets.add(neu.get(j));
                    Ticketpos.add(j);

                }else{


                        try {JSONObject new_json = new JSONObject(neu_obj.get(j));
                            JSONObject old_json;
                            for (int k =0 ;k< old.size();k++) {

                                old_json = new JSONObject(old_obj.get(k));
                                if (old_json.getString("ID").equals(neu.get(j).toString())){
                                    if (!old_json.getString("terminTag").equals(new_json.getString("terminTag"))) {
                                        createNotificationTermin(new_json);
                                        System.out.println("LOOOOOOOOLLLLLL TERMIN GEÄNDERT");

                                    }//(old_json.getInt("Status")==19 ||old_json.getInt("Status")==17)
                                    if (!(old_json.getInt("Status")==18)&&new_json.getInt("Status")==18) {
                                        createNotificationStatus(new_json);
                                        System.out.println("LOOOOOOOOLLLLLL Status GEÄNDERT");

                                    }
                                    break;

                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
            if (neueTickets.size()>0){

                for (int k = 0; k < neueTickets.size(); k++) {
                    createNotification(k, Ticketliste_neu);
                    Log.e("FINALEEOLEEE", neueTickets.toString());
                }

            }else{
                Log.e("KEINEN NEUEN TICKETS","NULL");
            }





        }


    private void createNotification(int k, JSONArray ticketliste_neu) {

        String gesamtMeldung="";
        String termin="";


            try {
                JSONObject c = ticketliste_neu.getJSONObject(k);
                String Firma = c.getString("Firma");
                String Modell = c.getString("Modell");
                String Fehler = c.getString("Stoerung");
                String Termintag = c.getString("terminTag");
                String Terminende = c.getString("terminEnde");
                int Termintyp = c.getInt("terminTyp");
                Log.e("terminTag",":"+Termintag);
                String formated_termintag="",formated_terminende;

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN);
                SimpleDateFormat edf = new SimpleDateFormat("dd-MM-yyyy  HH:mm", Locale.GERMAN);

                switch (Termintyp){

                    case 0:

                        Terminsdf = sdf.parse(Termintag);
                        formated_termintag = edf.format(Terminsdf);
                        termin="Termin: "+formated_termintag;



                        break;
                    case 1:

                        Terminsdf = sdf.parse(Termintag);
                        formated_termintag = edf.format(Terminsdf);
                        Terminsdfend = sdf.parse(Terminende);
                        formated_terminende = edf.format(Terminsdfend);
                        termin="Ab "+formated_termintag+" bis "+ formated_terminende;



                        break;
                    case 2:
                        Terminsdf = sdf.parse(Termintag);
                        formated_termintag = edf.format(Terminsdf);
                        Terminsdfend = sdf.parse(Terminende);
                        formated_terminende = edf.format(Terminsdfend);
                        termin="Von "+formated_termintag+" bis "+ formated_terminende;

                        break;
                    case 3:

                        Terminsdf = sdf.parse(Termintag);
                        formated_termintag = edf.format(Terminsdf);
                        termin="Termin: "+formated_termintag;



                        break;
                }


                gesamtMeldung=Firma+" "+Modell+" "+Fehler;
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }




        // BEGIN_INCLUDE(notificationCompat)
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.context);
        // END_INCLUDE(notificationCompat)

        // BEGIN_INCLUDE(intent)
        //Create Intent to launch this Activity again if the notification is clicked.
        Intent i = new Intent(MainActivity.context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(MainActivity.context, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(intent);
        // END_INCLUDE(intent)

        // BEGIN_INCLUDE(ticker)
        // Sets the ticker text
        builder.setTicker(getResources().getString(R.string.custom_notification));

        // Sets the small icon for the ticker
        builder.setSmallIcon(R.drawable.ic_launcher);
        // END_INCLUDE(ticker)

        // BEGIN_INCLUDE(buildNotification)
        // Cancel the notification when clicked
        builder.setAutoCancel(true);


        // SET SOUND
        builder.setDefaults(Notification.DEFAULT_SOUND);

        // Build the notification
        Notification notification = builder.build();
        // END_INCLUDE(buildNotification)

        // BEGIN_INCLUDE(customLayout)
        // Inflate the notification layout as RemoteViews
        RemoteViews contentView = new RemoteViews(MainActivity.context.getPackageName(), R.layout.notification);

        // Set text on a TextView in the RemoteViews programmatically.
       // final String time = DateFormat.getTimeInstance().format(new Date()).toString();
        String text = gesamtMeldung;
        contentView.setTextViewText(R.id.textView_not, text);
        contentView.setTextViewText(R.id.termin_not, termin);

        /* Workaround: Need to set the content view here directly on the notification.
         * NotificationCompatBuilder contains a bug that prevents this from working on platform
         * versions HoneyComb.
         * See https://code.google.com/p/android/issues/detail?id=30495
         */
        notification.contentView = contentView;

        // Add a big content view to the notification if supported.
        // Support for expanded notifications was added in API level 16.
        // (The normal contentView is shown when the notification is collapsed, when expanded the
        // big content view set here is displayed.)

        // END_INCLUDE(customLayout)

        // START_INCLUDE(notify)
        // Use the NotificationManager to show the notification
        NotificationManager nm = (NotificationManager) MainActivity.context.getSystemService(Context.NOTIFICATION_SERVICE);
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        nm.notify(m, notification);
        // END_INCLUDE(notify)
    }


    private void createNotificationTermin(JSONObject c) {

        String gesamtMeldung="";
        String termin="";


            try {

                String Firma = c.getString("Firma");
                String Modell = c.getString("Modell");

                String Termintag = c.getString("terminTag");
                String Terminende = c.getString("terminEnde");
                int Termintyp = c.getInt("terminTyp");
                Log.e("terminTag",":"+Termintag);
                String formated_termintag="",formated_terminende;

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN);
                SimpleDateFormat edf = new SimpleDateFormat("dd-MM-yyyy  HH:mm", Locale.GERMAN);

                switch (Termintyp){

                    case 0:

                        Terminsdf = sdf.parse(Termintag);
                        formated_termintag = edf.format(Terminsdf);
                        termin="Termin: "+formated_termintag;



                        break;
                    case 1:

                        Terminsdf = sdf.parse(Termintag);
                        formated_termintag = edf.format(Terminsdf);
                        Terminsdfend = sdf.parse(Terminende);
                        formated_terminende = edf.format(Terminsdfend);
                        termin="Ab "+formated_termintag+" bis "+ formated_terminende;



                        break;
                    case 2:
                        Terminsdf = sdf.parse(Termintag);
                        formated_termintag = edf.format(Terminsdf);
                        Terminsdfend = sdf.parse(Terminende);
                        formated_terminende = edf.format(Terminsdfend);
                        termin="Von "+formated_termintag+" bis "+ formated_terminende;

                        break;
                    case 3:

                        Terminsdf = sdf.parse(Termintag);
                        formated_termintag = edf.format(Terminsdf);
                        termin="Termin: "+formated_termintag;



                        break;
                }


                gesamtMeldung="TERMIN GEÄNDERT!!\n"+Firma+" "+Modell;
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }




        // BEGIN_INCLUDE(notificationCompat)
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.context);
        // END_INCLUDE(notificationCompat)

        // BEGIN_INCLUDE(intent)
        //Create Intent to launch this Activity again if the notification is clicked.
        Intent i = new Intent(MainActivity.context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(MainActivity.context, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(intent);
        // END_INCLUDE(intent)

        // BEGIN_INCLUDE(ticker)
        // Sets the ticker text
        builder.setTicker(getResources().getString(R.string.custom_notification_newtemin));

        // Sets the small icon for the ticker
        builder.setSmallIcon(R.drawable.ic_launcher);
        // END_INCLUDE(ticker)

        // BEGIN_INCLUDE(buildNotification)
        // Cancel the notification when clicked
        builder.setAutoCancel(true);


        // SET SOUND
        builder.setDefaults(Notification.DEFAULT_SOUND);

        // Build the notification
        Notification notification = builder.build();
        // END_INCLUDE(buildNotification)

        // BEGIN_INCLUDE(customLayout)
        // Inflate the notification layout as RemoteViews
        RemoteViews contentView = new RemoteViews(MainActivity.context.getPackageName(), R.layout.notification_newtermin);

        // Set text on a TextView in the RemoteViews programmatically.
        // final String time = DateFormat.getTimeInstance().format(new Date()).toString();
        String text = gesamtMeldung;
        contentView.setTextViewText(R.id.textView_not_newtermin, text);
        contentView.setTextViewText(R.id.termin_not_new, termin);

        /* Workaround: Need to set the content view here directly on the notification.
         * NotificationCompatBuilder contains a bug that prevents this from working on platform
         * versions HoneyComb.
         * See https://code.google.com/p/android/issues/detail?id=30495
         */
        notification.contentView = contentView;

        // Add a big content view to the notification if supported.
        // Support for expanded notifications was added in API level 16.
        // (The normal contentView is shown when the notification is collapsed, when expanded the
        // big content view set here is displayed.)

        // END_INCLUDE(customLayout)

        // START_INCLUDE(notify)
        // Use the NotificationManager to show the notification
        NotificationManager nm = (NotificationManager) MainActivity.context.getSystemService(Context.NOTIFICATION_SERVICE);
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        nm.notify(m, notification);
        // END_INCLUDE(notify)
    }

    private void createNotificationStatus(JSONObject c) {

        String gesamtMeldung="";
        String Statusstring="";


        try {

            String Firma = c.getString("Firma");
            String Modell = c.getString("Modell");

            int Statusnum = c.getInt("Status");
           Statusstring= getStatus(Statusnum);



            gesamtMeldung="STATUS GEÄNDERT!!\n"+Firma+" "+Modell;
        } catch (JSONException e) {
            e.printStackTrace();
        }




        // BEGIN_INCLUDE(notificationCompat)
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.context);
        // END_INCLUDE(notificationCompat)

        // BEGIN_INCLUDE(intent)
        //Create Intent to launch this Activity again if the notification is clicked.
        Intent i = new Intent(MainActivity.context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(MainActivity.context, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(intent);
        // END_INCLUDE(intent)

        // BEGIN_INCLUDE(ticker)
        // Sets the ticker text
        builder.setTicker(getResources().getString(R.string.custom_notification_newstatus));

        // Sets the small icon for the ticker
        builder.setSmallIcon(R.drawable.ic_launcher);
        // END_INCLUDE(ticker)

        // BEGIN_INCLUDE(buildNotification)
        // Cancel the notification when clicked
        builder.setAutoCancel(true);


        // SET SOUND
        builder.setDefaults(Notification.DEFAULT_SOUND);

        // Build the notification
        Notification notification = builder.build();
        // END_INCLUDE(buildNotification)

        // BEGIN_INCLUDE(customLayout)
        // Inflate the notification layout as RemoteViews
        RemoteViews contentView = new RemoteViews(MainActivity.context.getPackageName(), R.layout.notification_newstatus);

        // Set text on a TextView in the RemoteViews programmatically.
        // final String time = DateFormat.getTimeInstance().format(new Date()).toString();
        String text = gesamtMeldung;
        contentView.setTextViewText(R.id.textView_not_newstatus, text);
        contentView.setTextViewText(R.id.status_not_new, Statusstring);

        /* Workaround: Need to set the content view here directly on the notification.
         * NotificationCompatBuilder contains a bug that prevents this from working on platform
         * versions HoneyComb.
         * See https://code.google.com/p/android/issues/detail?id=30495
         */
        notification.contentView = contentView;

        // Add a big content view to the notification if supported.
        // Support for expanded notifications was added in API level 16.
        // (The normal contentView is shown when the notification is collapsed, when expanded the
        // big content view set here is displayed.)

        // END_INCLUDE(customLayout)

        // START_INCLUDE(notify)
        // Use the NotificationManager to show the notification
        NotificationManager nm = (NotificationManager) MainActivity.context.getSystemService(Context.NOTIFICATION_SERVICE);
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        nm.notify(m, notification);
        // END_INCLUDE(notify)
    }



    public String getStatus(int Statusnum){
        switch (Statusnum){

            case 10:
                Status = "Unbearbeitet";
                DropPos = 0;
                break;
            case 11:
                Status = "Fahrt";
                DropPos = 1;
                break;
            case 12:
                Status = "In arbeit";
                DropPos = 2;
                break;
            case 13:
                Status = "offen";
                DropPos = 3;
                break;

            case 15:
                Status = "Erledigt";
                DropPos = 4;
                break;
            case 16:
                Status = "wartet";
                DropPos = 5;
                break;
            case 17:
                Status = "Ware bestellt";
                DropPos = 6;
                break;
            case 18:
                Status = "Ware da";
                DropPos = 7;
                break;
            case 19:
                Status = "Ware benötigt";
                DropPos = 8;
                break;
            case 20:
                Status = "installiert";
                DropPos = 9;
                break;

            case 99:
                Status = "Eskalation";
                DropPos = 0;
                break;

            case 100:
                Status = "Eskalation in arbeit";
                DropPos = 1;

                break;


        }

        return Status;
    }

    public int getDropPos(int Statusnum){
        switch (Statusnum){

            case 10:
                Status = "Unbearbeitet";
                DropPos = 0;
                break;
            case 11:
                Status = "Fahrt";
                DropPos = 1;
                break;
            case 12:
                Status = "In arbeit";
                DropPos = 2;
                break;
            case 13:
                Status = "offen";
                DropPos = 3;
                break;

            case 15:
                Status = "Erledigt";
                DropPos = 4;
                break;
            case 16:
                Status = "wartet";
                DropPos = 5;
                break;
            case 17:
                Status = "Ware bestellt";
                DropPos = 6;
                break;
            case 18:
                Status = "Ware da";
                DropPos = 7;
                break;
            case 19:
                Status = "Ware benötigt";
                DropPos = 8;
                break;
            case 20:
                Status = "installiert";
                DropPos = 9;
                break;
            case 99:
                Status = "Eskalation";
                DropPos = 0;
                break;

            case 100:
                Status = "Eskalation in arbeit";
                DropPos = 1;

                break;

        }

        return DropPos;
    }


}
