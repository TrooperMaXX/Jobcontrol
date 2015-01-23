package de.hoell.jobcontrol;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;



import de.hoell.jobcontrol.query.Functions;
import de.hoell.jobcontrol.ticketlist.TicketDetailsActivity;
import de.hoell.jobcontrol.ticketlist.Tickets;


public class TicketFragment extends ListFragment {



    private static final String TAG_SUCCESS = "success";

    String Status = "Unbearbeitet";
    int DropPos= 0;

    ArrayList<HashMap<String, String>> TheTickets = new ArrayList<HashMap<String, String>>();
    List<Tickets> ticketsList = new ArrayList<Tickets>();

    JSONArray Ticketliste = null;




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

        new JSONMyTickets().execute();
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
            /**
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
                    case 14:
                        Status = "Abgeschlossen";
                        DropPos = 4;
                        break;
                    case 15:
                        Status = "Erledigt";
                        DropPos = 5;
                        break;
                    case 16:
                        Status = "wartet";
                        DropPos = 6;
                        break;
                    case 17:
                        Status = "Ware bestellt";
                        DropPos = 7;
                        break;
                    case 18:
                        Status = "Ware da";
                        DropPos = 8;
                        break;
                    case 19:
                        Status = "Ware benötigt";
                        DropPos = 9;
                        break;
                    case 20:
                        Status = "installiert";
                        DropPos = 10;
                        break;
                    case 39:
                        Status = "gelöscht";
                        DropPos = 11;
                        break;

                }**/


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


            String ID = extra.getString("ID");
            intent.putExtra("value_id", ID);

            String Termin ="";
            if (Terminende == "null" && Termintag != "null")
            {
                Termin = Termintag;

            }
            else if(Termintag == "null"){

                Termin = "---";
            }
            else{
            Termin = "Zwischen " + Termintag + " und " + Terminende;

            }

            intent.putExtra("value_termin", Termin);

            String Auftragsnr = extra.getString("Auftragtkd");
            intent.putExtra("value_auftragnr", Auftragsnr);


            String wvnr = extra.getString("wvt");
            intent.putExtra("value_wvnr", wvnr);

            String Annahmekue = extra.getString("annahmedurch");
            String Annahme = null;
          if (Annahmekue.equals("mt")){
               Annahme="Tomas Mersch";
            }else if(Annahmekue.equals("am")){

              Annahme="Andrej Makarevic";
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
          }

            intent.putExtra("value_annahme", Annahme);

            getActivity().startActivity(intent);

        } catch (JSONException e) {
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




        @Override
        protected JSONObject doInBackground(String... args) {
            String user;

            user = de.hoell.jobcontrol.Start.user;

            Functions Function = new Functions();
            JSONObject json = Function.MyTickets(user);
            //Function.isTerminheute();
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

                    Ticketliste = json.getJSONArray("tickets");
                    for (int i = 0; i < Ticketliste.length(); i++) {
                        JSONObject c = Ticketliste.getJSONObject(i);
                        String Firma = c.getString("Firma");
                        int Statusnum = c.getInt("Status");
                        //TODO: MACH DAS HIER SCHÖNER.....
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
                            case 14:
                                Status = "Abgeschlossen";
                                DropPos = 4;
                                break;
                            case 15:
                                Status = "Erledigt";
                                DropPos = 5;
                                break;
                            case 16:
                                Status = "wartet";
                                DropPos = 6;
                                break;
                            case 17:
                                Status = "Ware bestellt";
                                DropPos = 7;
                                break;
                            case 18:
                                Status = "Ware da";
                                DropPos = 8;
                                break;
                            case 19:
                                Status = "Ware benötigt";
                                DropPos = 9;
                                break;
                            case 20:
                                Status = "installiert";
                                DropPos = 10;
                                break;
                            case 39:
                                Status = "gelöscht";
                                DropPos = 11;
                                break;

                        }

                        String Modell = c.getString("Modell");


                        ticketsList.add(new Tickets(Firma+ ", " +Modell+ ", " +  Status));
                        ListView lv = getListView();
                        /**   if (Statusnum > 11) {

                            lv.setBackgroundColor(Color.RED);
                        }
                        else {
                            lv.setBackgroundColor(Color.WHITE);
                        }

                        String Termintag = c.getString("terminTag");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date Terminsdf = sdf.parse(Termintag);

                        Functions Function = new Functions();

                        boolean isheute =Function.isTerminheute(Terminsdf);

                        if (isheute){
                            System.out.println("yay");
                            //TODO: If this true should this row in the listAdapter colored red ...
                        }/**/


                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("Firma", Firma);

                        map.put("Status", Status);

                        TheTickets.add(map);



                    }System.out.println("Abfrage" + TheTickets);

                }
                else{


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }/** catch (ParseException e) {
                e.printStackTrace();
            }/**/
            setListAdapter(new ArrayAdapter<Tickets>(getActivity(),
                    android.R.layout.simple_list_item_1, android.R.id.text1, ticketsList));
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
            case 14:
                Status = "Abgeschlossen";
                DropPos = 4;
                break;
            case 15:
                Status = "Erledigt";
                DropPos = 5;
                break;
            case 16:
                Status = "wartet";
                DropPos = 6;
                break;
            case 17:
                Status = "Ware bestellt";
                DropPos = 7;
                break;
            case 18:
                Status = "Ware da";
                DropPos = 8;
                break;
            case 19:
                Status = "Ware benötigt";
                DropPos = 9;
                break;
            case 20:
                Status = "installiert";
                DropPos = 10;
                break;
            case 39:
                Status = "gelöscht";
                DropPos = 11;
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
            case 14:
                Status = "Abgeschlossen";
                DropPos = 4;
                break;
            case 15:
                Status = "Erledigt";
                DropPos = 5;
                break;
            case 16:
                Status = "wartet";
                DropPos = 6;
                break;
            case 17:
                Status = "Ware bestellt";
                DropPos = 7;
                break;
            case 18:
                Status = "Ware da";
                DropPos = 8;
                break;
            case 19:
                Status = "Ware benötigt";
                DropPos = 9;
                break;
            case 20:
                Status = "installiert";
                DropPos = 10;
                break;
            case 39:
                Status = "gelöscht";
                DropPos = 11;
                break;

        }

        return DropPos;
    }


}
