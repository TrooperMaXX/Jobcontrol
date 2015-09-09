package de.hoell.jobcontrol.widget;

/***
 Copyright (c) 2008-2012 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 From _The Busy Coder's Guide to Advanced Android Development_
 http://commonsware.com/AdvAndroid
 */

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hoell.jobcontrol.R;
import de.hoell.jobcontrol.query.Functions;
import de.hoell.jobcontrol.session.SessionManager;
import de.hoell.jobcontrol.ticketlist.TicketDetailsActivity;


public class LoremViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    String[] TicketID,TicketFirma, TicketStatus,TicketTermin,TicketAuaNr,TicketModell, TicketAdresse,TicketOrt,
            TicketFehler, TicketSichtbar, TicketStatus_ic, TicketHintergrund ;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN);
    SimpleDateFormat edf = new SimpleDateFormat("dd-MM-yyyy  HH:mm", Locale.GERMAN);
    JSONArray Ticketliste = null;
    String Status = "Unbearbeitet";
    int DropPos= 0;
    public Date Terminsdf,Terminsdfend;

    private Context ctxt=null;
    private int appWidgetId;

    public LoremViewsFactory(Context ctxt, Intent intent) {
        this.ctxt=ctxt;

        appWidgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

    }

    @Override
    public void onCreate() {
        // no-op

       makeList();
    }

    @Override
    public void onDestroy() {
        // no-op
    }

    @Override
    public int getCount() {
        return(Ticketliste.length());
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row=new RemoteViews(ctxt.getPackageName(),
                R.layout.row_list);



            //TODO: alle felder setzen :)
            row.setTextViewText(R.id.FIRMA_CELL,TicketFirma[position]);
            row.setTextViewText(R.id.STATUS_CELL,TicketStatus[position]);
            row.setTextViewText(R.id.ADRESSE_CELL, TicketAdresse[position]);
        row.setTextViewText(R.id.ORT_CELL, TicketOrt[position]);
        row.setTextViewText(R.id.MODEL_CELL, TicketModell[position]);
        row.setTextViewText(R.id.FEHLER_CELL, TicketFehler[position]);
        row.setImageViewResource(R.id.Status_img, Integer.parseInt(TicketStatus_ic[position]));
        row.setImageViewResource(R.id.BACKGROUD_all, Integer.parseInt(TicketHintergrund[position]));
        row.setTextViewText(R.id.TERMIN_CELL, TicketTermin[position]);
        row.setTextViewText(R.id.AUA_CELL, TicketAuaNr[position]);
        row.setImageViewResource(R.id.img_warning, Integer.parseInt(TicketSichtbar[position]));

        Intent intent=new Intent();

        JSONObject extra = null;
        try {
            extra = Ticketliste.getJSONObject(position);


        int Statusnum = extra.getInt("Status");
        String StringStatusnum = extra.getString("Status");
        Status= getStatus(Statusnum);
        DropPos= getDropPos(Statusnum);
        intent.putExtra("value_statusnum", Statusnum);



        intent.putExtra("value_status", Status);
        intent.putExtra("value_droppos", DropPos);

        String Firma = extra.getString("Firma");
        intent.putExtra("value_firma", Firma);

        String Strasse = extra.getString("Strasse");
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

        int Bogenverfuegbar = extra.getInt("bogen_verfuegbar");
        intent.putExtra("value_bogenverfuegbar", Bogenverfuegbar);

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

        String fleet = extra.getString("fleet");
        intent.putExtra("value_fleet", fleet);

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
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
        row.setOnClickFillInIntent(R.id.list_row, intent);

        return(row);

    }

    @Override
    public RemoteViews getLoadingView() {
        return(null);
    }

    @Override
    public int getViewTypeCount() {
        return(1);
    }

    @Override
    public long getItemId(int position) {
        return(position);
    }

    @Override
    public boolean hasStableIds() {
        return(true);
    }

    @Override
    public void onDataSetChanged() {
        // no-op
    }
    public void makeList(){

        SessionManager sessionManager = new SessionManager(ctxt);
        if (sessionManager.isJSONsaved()) {
            String jstring = sessionManager.getJstring();
            if (jstring != null){
                try {
                    JSONObject jsonData = new JSONObject(jstring);
                    int success = jsonData.getInt("success");


                    if (success == 1) {

                        Ticketliste = jsonData.getJSONArray("tickets");

                        TicketID = new String[Ticketliste.length()];
                        TicketFirma= new String[Ticketliste.length()];
                        TicketStatus= new String[Ticketliste.length()];
                        TicketTermin= new String[Ticketliste.length()];
                        TicketAuaNr= new String[Ticketliste.length()];
                        TicketModell= new String[Ticketliste.length()];
                        TicketAdresse= new String[Ticketliste.length()];
                        TicketOrt= new String[Ticketliste.length()];
                        TicketFehler= new String[Ticketliste.length()];
                        TicketSichtbar= new String[Ticketliste.length()];
                        TicketStatus_ic= new String[Ticketliste.length()];
                        TicketHintergrund= new String[Ticketliste.length()];

                        for (int i = 0; i < Ticketliste.length(); i++) {
                            JSONObject c = Ticketliste.getJSONObject(i);


                            int imgid= ctxt.getResources().getIdentifier("ic_status_red", "mipmap", "de.hoell.jobcontrol");

                            String ID = c.getString("ID");
                            Log.e("ID",ID);
                            String Firma = c.getString("Firma");
                            int Statusnum = c.getInt("Status");
                            boolean eskalation=false;


                            switch (Statusnum) {

                                case 10:
                                    Status = "Unbearbeitet";
                                    DropPos = 0;


                                    imgid= ctxt.getResources().getIdentifier("ic_status_red","mipmap","de.hoell.jobcontrol");
                                    break;
                                case 11:
                                    Status = "Fahrt";
                                    DropPos = 1;

                                    imgid= ctxt.getResources().getIdentifier("ic_status_green","mipmap","de.hoell.jobcontrol");
                                    break;
                                case 12:
                                    Status = "In arbeit";
                                    DropPos = 2;

                                    imgid= ctxt.getResources().getIdentifier("ic_status_green","mipmap","de.hoell.jobcontrol");
                                    break;
                                case 13:
                                    Status = "offen";
                                    DropPos = 3;

                                    imgid= ctxt.getResources().getIdentifier("ic_status_orange","mipmap","de.hoell.jobcontrol");
                                    break;
                                case 15:
                                    Status = "Erledigt";
                                    DropPos = 4;

                                    break;
                                case 16:
                                    Status = "wartet";
                                    DropPos = 5;

                                    imgid= ctxt.getResources().getIdentifier("ic_status_orange","mipmap","de.hoell.jobcontrol");
                                    break;
                                case 17:
                                    Status = "Ware bestellt";
                                    DropPos = 6;

                                    imgid= ctxt.getResources().getIdentifier("ic_status_orange","mipmap","de.hoell.jobcontrol");
                                    break;
                                case 18:
                                    Status = "Ware da";
                                    DropPos = 7;

                                    imgid= ctxt.getResources().getIdentifier("ic_status_green","mipmap","de.hoell.jobcontrol");
                                    break;
                                case 19:
                                    Status = "Ware ben�tigt";
                                    DropPos = 8;

                                    imgid= ctxt.getResources().getIdentifier("ic_status_orange","mipmap","de.hoell.jobcontrol");
                                    break;
                                case 20:
                                    Status = "installiert";
                                    DropPos = 9;

                                    imgid= ctxt.getResources().getIdentifier("ic_status_red","mipmap","de.hoell.jobcontrol");
                                    break;
                                case 99:
                                    Status = "Eskalation";
                                    DropPos = 0;
                                    eskalation=true;
                                    imgid= ctxt.getResources().getIdentifier("ic_status_orange","mipmap","de.hoell.jobcontrol");
                                    break;
                                case 100:
                                    Status = "Eskalation";
                                    DropPos = 1;
                                    eskalation=true;
                                    imgid= ctxt.getResources().getIdentifier("ic_status_orange","mipmap","de.hoell.jobcontrol");
                                    break;
                                case 101:
                                    Status = "Eskalation";
                                    DropPos = 2;
                                    eskalation=true;
                                    imgid= ctxt.getResources().getIdentifier("ic_status_orange","mipmap","de.hoell.jobcontrol");
                                    break;


                            }

                            String Modell = c.getString("Modell");

                            String Strasse = c.getString("Strasse");
                            String Plz = c.getString("Plz");
                            String Ort = c.getString("Ort");

                            String ort = Plz + " " + Ort;

                            String Fehler = c.getString("Stoerung");


                            int hintergrundid ;
                            String Termintag = c.getString("terminTag");
                            String Terminende = c.getString("terminEnde");
                            int Termintyp = c.getInt("terminTyp");

                            Log.e("terminTag", ":" + Termintag);
                            String formated_termintag="",formated_terminende;
                            String finalTermin="";
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN);
                            SimpleDateFormat edf = new SimpleDateFormat("dd-MM-yyyy  HH:mm", Locale.GERMAN);
                            Functions Function = new Functions();
                            boolean isheute=false;
                            if (!Termintag.equals("null")){


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
                            }



                            if (isheute){
                                System.out.println("yay");
                                hintergrundid= ctxt.getResources().getIdentifier("rot","drawable","de.hoell.jobcontrol");

                            }
                            else{
                                hintergrundid= ctxt.getResources().getIdentifier("weis","drawable","de.hoell.jobcontrol");
                            }

                            String auanr = c.getString("Auftragtkd");


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
                                imgsichtbar= ctxt.getResources().getIdentifier("ic_warning","drawable","de.hoell.jobcontrol");
                            }

                            TicketID[i]=ID;
                            TicketFirma[i]=Firma;
                            TicketStatus[i]=Status;
                            TicketTermin[i]=finalTermin;
                            TicketAuaNr[i]=auanr;
                            TicketModell[i]=Modell;
                            TicketAdresse[i]=Strasse;
                            TicketOrt[i]=ort;
                            TicketFehler[i]=Fehler;
                            TicketSichtbar[i]=String.valueOf(imgsichtbar);
                            TicketStatus_ic[i]=String.valueOf(imgid);
                            TicketHintergrund[i]=String.valueOf(hintergrundid);
                            Log.e("Statusid",""+imgid);



                        }


                    }
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }

            }
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
                DropPos = 0;
                break;
            case 100:
                Status = "Eskalation";
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