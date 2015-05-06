package de.hoell.jobcontrol;

/**
 * Created by Hoell on 16.10.2014.
 */

import android.app.ListFragment;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

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

public class OfflineFragment extends ListFragment {

    public OfflineFragment(){}
    private static final String TAG_SUCCESS = "success";
    JSONArray Ticketliste = null;
    List<Tickets> ticketsList = new ArrayList<Tickets>();
    ArrayList<HashMap<String, String>> TheTickets = new ArrayList<HashMap<String, String>>();
    String Status = "Unbearbeitet";
    int DropPos= 0;
    private OnTicketInteractionListener mListener;
    public  Date Terminsdf;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        SessionManager session = new SessionManager(getActivity());
        if (session.isJSONsaved()) {
            String jstring = session.getJstring();

            if (jstring != null)
                try {
                    JSONObject jsonData = new JSONObject(jstring);
                    int success = jsonData.getInt(TAG_SUCCESS);


                    if (success == 1) {

                        Ticketliste = jsonData.getJSONArray("tickets");
                        for (int i = 0; i < Ticketliste.length(); i++) {
                            JSONObject c = Ticketliste.getJSONObject(i);


                            int imgid= this.getResources().getIdentifier("ic_status_red", "mipmap", "de.hoell.jobcontrol");


                            String Firma = c.getString("Firma");
                            int Statusnum = c.getInt("Status");



                            switch (Statusnum) {

                                case 10:
                                    Status = "Unbearbeitet";
                                    DropPos = 0;

                                    imgid= this.getResources().getIdentifier("ic_status_red","mipmap","de.hoell.jobcontrol");
                                    break;
                                case 11:
                                    Status = "Fahrt";
                                    DropPos = 1;

                                    imgid= this.getResources().getIdentifier("ic_status_green","mipmap","de.hoell.jobcontrol");
                                    break;
                                case 12:
                                    Status = "In arbeit";
                                    DropPos = 2;

                                    imgid= this.getResources().getIdentifier("ic_status_green","mipmap","de.hoell.jobcontrol");
                                    break;
                                case 13:
                                    Status = "offen";
                                    DropPos = 3;

                                    imgid= this.getResources().getIdentifier("ic_status_red","mipmap","de.hoell.jobcontrol");
                                    break;
                                case 15:
                                    Status = "Erledigt";
                                    DropPos = 4;

                                    break;
                                case 16:
                                    Status = "wartet";
                                    DropPos = 5;

                                    imgid= this.getResources().getIdentifier("ic_status_orange","mipmap","de.hoell.jobcontrol");
                                    break;
                                case 17:
                                    Status = "Ware bestellt";
                                    DropPos = 6;

                                    imgid= this.getResources().getIdentifier("ic_status_orange","mipmap","de.hoell.jobcontrol");
                                    break;
                                case 18:
                                    Status = "Ware da";
                                    DropPos = 7;

                                    imgid= this.getResources().getIdentifier("ic_status_orange","mipmap","de.hoell.jobcontrol");
                                    break;
                                case 19:
                                    Status = "Ware benötigt";
                                    DropPos = 8;

                                    imgid= this.getResources().getIdentifier("ic_status_orange","mipmap","de.hoell.jobcontrol");
                                    break;
                                case 20:
                                    Status = "installiert";
                                    DropPos = 9;

                                    imgid= this.getResources().getIdentifier("ic_status_red","mipmap","de.hoell.jobcontrol");
                                    break;
                                case 39:
                                    Status = "Eskalation";
                                    DropPos = 10;
                                    imgid= this.getResources().getIdentifier("ic_status_orange","mipmap","de.hoell.jobcontrol");
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
                            Log.e("terminTag", ":" + Termintag);
                            String finalTermin;

                            if(Termintag.equals("null"))
                            {
                                Termintag="---";
                                finalTermin="";

                                hintergrundid= this.getResources().getIdentifier("weis","drawable","de.hoell.jobcontrol");
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
                                    hintergrundid= this.getResources().getIdentifier("rot","drawable","de.hoell.jobcontrol");

                                }
                                else{
                                    hintergrundid= this.getResources().getIdentifier("weis","drawable","de.hoell.jobcontrol");
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

                            map.put("Status_ic",String.valueOf(imgid));
                            map.put("Hintergrund",String.valueOf(hintergrundid));
                            Log.e("Statusid",""+imgid);

                            TheTickets.add(map);


                        }
                        System.out.println("Abfrage" + TheTickets);

                    }
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }

            setListAdapter(new SpecialAdapter(getActivity(),TheTickets,R.layout.row_list,
                    new String[] {"Firma", "Status", "Adresse","Ort", "Model", "Fehler", "Farbe", "Status_ic","Hintergrund","Termin","AuaNr"},
                    new int[] {R.id.FIRMA_CELL,R.id.STATUS_CELL, R.id.ADRESSE_CELL, R.id.ORT_CELL, R.id.MODEL_CELL, R.id.FEHLER_CELL,R.color.ticket_list,R.id.Status_img,R.id.BACKGROUD_all,R.id.TERMIN_CELL,R.id.AUA_CELL}));

            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                ListView lv = getListView();
                ColorDrawable sage = new ColorDrawable(this.getResources().getColor(R.color.ticket_list_divider));
                lv.setDivider(sage);
                lv.setDividerHeight(10);

            }}

        }





    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent intent = new Intent(getActivity(), TicketDetailsActivity.class);
        try {SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN);
            SimpleDateFormat edf = new SimpleDateFormat("dd-MM-yyyy  HH:mm", Locale.GERMAN);

            JSONObject extra = Ticketliste.getJSONObject(position);

            int Statusnum = extra.getInt("Status");
            String StringStatusnum = extra.getString("Status");
            Status= getStatus(Statusnum);
            DropPos= getDropPos(Statusnum);
            intent.putExtra("value_statusnum", StringStatusnum);



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

            String Angenommen = extra.getString("Datum");
            Date angenomensdf = sdf.parse(Angenommen);
            String finalAngenommen = edf.format(angenomensdf);

            intent.putExtra("value_angenommen", finalAngenommen);

            String Termin =""; String finalTermin="";
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
            }
            else if(Annahmekue.equals("mt")){

                Annahme="Thomas Mersch";
            }
            else if(Annahmekue.equals("am")){

                Annahme="Andrej Makarevic";
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
            }
            else{
                Annahme=Annahmekue;
            }

            intent.putExtra("value_annahme", Annahme);

            getActivity().startActivity(intent);



        } catch (JSONException | ParseException e) {
            e.printStackTrace();
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