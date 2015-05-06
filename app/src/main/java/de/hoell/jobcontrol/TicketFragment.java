package de.hoell.jobcontrol;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
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

import de.hoell.jobcontrol.adapter.SpecialAdapter;
import de.hoell.jobcontrol.query.Functions;
import de.hoell.jobcontrol.session.SessionManager;
import de.hoell.jobcontrol.ticketlist.TicketDetailsActivity;
import de.hoell.jobcontrol.ticketlist.Tickets;


public class TicketFragment extends ListFragment {



    private static final String TAG_SUCCESS = "success";

    String Status = "Unbearbeitet";
    public int DropPos= 0;


    List<Tickets> ticketsList = new ArrayList<Tickets>();

    public JSONArray Ticketliste = null;




    private OnTicketInteractionListener mListener;


     /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TicketFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new JSONMyTickets(getActivity().getApplicationContext()).execute();


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
            intent.putExtra("value_statusnum", StringStatusnum);

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

            String Termintag = extra.getString("terminTag");
            String Terminende = extra.getString("terminEnde");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN);
            SimpleDateFormat edf = new SimpleDateFormat("dd-MM-yyyy  HH:mm", Locale.GERMAN);





            String Angenommen = extra.getString("Datum");
            Date angenomensdf = sdf.parse(Angenommen);
            String finalAngenommen = edf.format(angenomensdf);


            intent.putExtra("value_angenommen", finalAngenommen);


            String Termin ="";
            String finalTermin="";
             if(Termintag.equals("null"))
            {

                Termin = "---";
            }
            else if (Terminende.equals("null") && !Termintag.equals("null")){

                 Date Terminsdf = sdf.parse(Termintag);
                 finalTermin = edf.format(Terminsdf);

                 Termin = finalTermin;
            }
            else{
                Date Terminendesdf=sdf.parse(Terminende);
                String finalTerminende = edf.format(Terminendesdf);
            Termin = "Zwischen " + finalTermin + " und " + finalTerminende;

            }

            intent.putExtra("value_termin", Termin);

            String Auftragsnr = extra.getString("Auftragtkd");
            intent.putExtra("value_auftragnr", Auftragsnr);


            String wvnr = extra.getString("wvt");
            intent.putExtra("value_wvnr", wvnr);

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
        // TODO: Update argument type and name
        public void onTicketInteraction(String id);
    }










    public class JSONMyTickets extends AsyncTask<String, String, JSONObject> {

        private Context mContext;
        public  Date Terminsdf;
        ArrayList<HashMap<String, String>> TheTickets = new ArrayList<HashMap<String, String>>();
        public JSONMyTickets (Context context){
            mContext = context;
        }




        @Override
        protected JSONObject doInBackground(String... args) {


            String user;

            user = de.hoell.jobcontrol.Start.user;

            Functions Function = new Functions();


               JSONObject json = Function.MyTickets(user);


            System.out.println("is JSON null?" +json);
            SessionManager session = new SessionManager(mContext);



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

        @Override
        protected void onPostExecute(JSONObject json) {
            if (json != null) {
                try {

                    int success = json.getInt(TAG_SUCCESS);

                    if (success == 1) {

                        Ticketliste = json.getJSONArray("tickets");
                        for (int i = 0; i < Ticketliste.length(); i++) {
                            JSONObject c = Ticketliste.getJSONObject(i);
                            String Farbe="#ffffffff";

                            int imgid= mContext.getResources().getIdentifier("ic_status_red","mipmap","de.hoell.jobcontrol");


                            String Firma = c.getString("Firma");
                            int Statusnum = c.getInt("Status");


                            switch (Statusnum) {

                                case 10:
                                    Status = "Unbearbeitet";
                                    DropPos = 0;
                                    Farbe="#ffffffff";
                                    imgid= mContext.getResources().getIdentifier("ic_status_red","mipmap","de.hoell.jobcontrol");
                                    break;
                                case 11:
                                    Status = "Fahrt";
                                    DropPos = 1;
                                    Farbe="#ffff4d00";
                                    imgid= mContext.getResources().getIdentifier("ic_status_green","mipmap","de.hoell.jobcontrol");
                                    break;
                                case 12:
                                    Status = "In arbeit";
                                    DropPos = 2;
                                    Farbe="#ffffffff";
                                    imgid= mContext.getResources().getIdentifier("ic_status_green","mipmap","de.hoell.jobcontrol");
                                    break;
                                case 13:
                                    Status = "offen";
                                    DropPos = 3;
                                    Farbe="#ffffffff";
                                    imgid= mContext.getResources().getIdentifier("ic_status_red","mipmap","de.hoell.jobcontrol");
                                    break;
                                case 15:
                                    Status = "Erledigt";
                                    DropPos = 4;

                                    break;
                                case 16:
                                    Status = "wartet";
                                    DropPos = 5;
                                    Farbe="#ffffffff";
                                    imgid= mContext.getResources().getIdentifier("ic_status_orange","mipmap","de.hoell.jobcontrol");
                                    break;
                                case 17:
                                    Status = "Ware bestellt";
                                    DropPos = 6;
                                    Farbe="#ffffffff";
                                    imgid= mContext.getResources().getIdentifier("ic_status_orange","mipmap","de.hoell.jobcontrol");
                                    break;
                                case 18:
                                    Status = "Ware da";
                                    DropPos = 7;
                                    Farbe="#ffff4d00";
                                    imgid= mContext.getResources().getIdentifier("ic_status_orange","mipmap","de.hoell.jobcontrol");
                                    break;
                                case 19:
                                    Status = "Ware benötigt";
                                    DropPos = 8;
                                    Farbe="#ffffffff";
                                    imgid= mContext.getResources().getIdentifier("ic_status_orange","mipmap","de.hoell.jobcontrol");
                                    break;
                                case 20:
                                    Status = "installiert";
                                    DropPos = 9;
                                    Farbe="#FF0000";
                                    imgid= mContext.getResources().getIdentifier("ic_status_red","mipmap","de.hoell.jobcontrol");
                                    break;
                                case 99:
                                    Status = "Eskalation";
                                    DropPos = 10;
                                    imgid= mContext.getResources().getIdentifier("ic_status_orange","mipmap","de.hoell.jobcontrol");
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
                            Log.e("terminTag",":"+Termintag);
                            String finalTermin;

                            if(Termintag.equals("null"))
                            {
                                Termintag="---";
                                finalTermin="";

                                hintergrundid= mContext.getResources().getIdentifier("weis","drawable","de.hoell.jobcontrol");
                            }else
                           {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN);
                               Terminsdf = sdf.parse(Termintag);
                               SimpleDateFormat edf = new SimpleDateFormat("dd-MM-yyyy  HH:mm", Locale.GERMAN);
                               finalTermin = edf.format(Terminsdf);
                               finalTermin="Termin: "+finalTermin;

                                Functions Function = new Functions();

                                boolean isheute =Function.isTerminheute(Terminsdf);

                                if (isheute){
                                    System.out.println("yay");
                                    hintergrundid= mContext.getResources().getIdentifier("rot","drawable","de.hoell.jobcontrol");

                                }
                                else{
                                    hintergrundid= mContext.getResources().getIdentifier("weis","drawable","de.hoell.jobcontrol");
                                }

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
                        new String[] {"Firma", "Status", "Adresse","Ort", "Model", "Fehler", "Farbe", "Status_ic","Hintergrund","Termin","AuaNr"},
                        new int[] {R.id.FIRMA_CELL,R.id.STATUS_CELL, R.id.ADRESSE_CELL, R.id.ORT_CELL, R.id.MODEL_CELL, R.id.FEHLER_CELL,R.color.ticket_list,R.id.Status_img,R.id.BACKGROUD_all,R.id.TERMIN_CELL,R.id.AUA_CELL}));
            }else{ Toast.makeText(mContext, "Keine Internet verbindung Bitte zum Offlinemodus wechseln", Toast.LENGTH_LONG).show();

                Fragment fragment = null;
                fragment = new OfflineFragment();
                if (fragment != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_container, fragment).commit();



                }
            }
            ListView lv = getListView();
            ColorDrawable sage = new ColorDrawable(mContext.getResources().getColor(R.color.ticket_list_divider));
            lv.setDivider(sage);
            lv.setDividerHeight(10);
           /* TODO: autoupdater
           new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Log.e("Autoreload","hat es geklappte:)?");
                   // Toast.makeText(getActivity().getApplicationContext(), "AUTORELOAD! yaaayayay", Toast.LENGTH_LONG).show();
                    new JSONMyTickets(mContext).execute();
                }
            }, 10*1000);// 5*60*1000*/
        }


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
                DropPos = 10;
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
                DropPos = 10;
                break;

        }

        return DropPos;
    }


}
