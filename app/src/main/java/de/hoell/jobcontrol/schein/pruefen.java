package de.hoell.jobcontrol.schein;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.hoell.jobcontrol.Jobcontrol;
import de.hoell.jobcontrol.R;
import de.hoell.jobcontrol.adapter.ExpandableHeightListView;
import de.hoell.jobcontrol.adapter.SpecialAdapter;
import de.hoell.jobcontrol.query.CustomRequest;
import de.hoell.jobcontrol.query.DBManager;
import de.hoell.jobcontrol.query.MyVolley;

/**
 * A placeholder fragment containing a simple view.
 */
public class pruefen extends Fragment {
    private Context context;
    SignatureView mSig;
    EditText editTextTeileNr,editTextBezeichnung;
    public pruefen() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.fab);

        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        ArrayList<HashMap<String, String>> arbeitlist = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> teilelist = new ArrayList<HashMap<String, String>>();

        final View rootView = localInflater.inflate(R.layout.fragment_pruefen, container, false);
        EditText Anschrift = (EditText) rootView.findViewById(R.id.editTextAnschriftContent);
        EditText Ger = (EditText) rootView.findViewById(R.id.editTextGerContent);
        EditText Srn = (EditText) rootView.findViewById(R.id.editTextSrnContent);
        final TextView Zaehler = (TextView) rootView.findViewById(R.id.textViewZaehler);
        final EditText sw = (EditText) rootView.findViewById(R.id.editTextSWContent);
        final EditText Farb = (EditText) rootView.findViewById(R.id.editTextFarbContent);
        final EditText Bemerkung = (EditText) rootView.findViewById(R.id.editTextBemerkung);

                ExpandableHeightListView arbeitsliste = (ExpandableHeightListView) rootView.findViewById(R.id.arbeitList);
        ExpandableHeightListView teileliste = (ExpandableHeightListView) rootView.findViewById(R.id.teileList);



        context = rootView.getContext();
        final Bundle args=getArguments();

        String anschrift=args.getString("Firma")+"\n"+args.getString("Str")+"\n"+args.getString("Ort");
        Anschrift.setText(anschrift);


        Ger.setText(args.getString("Ger"));
        Srn.setText(args.getString("Srn"));
        String ges="Zähler("+args.getString("Ges")+")";
        Zaehler.setText(ges);
        sw.setText(args.getString("Sw"));
        Farb.setText(args.getString("Farb"));

        for ( int i=0; i <= args.getInt("Pos");i++){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("Datum", args.getString("Datum" + String.valueOf(i)));
            map.put("Technr", args.getString("TechNr" + String.valueOf(i)));
            map.put("AW", args.getString("AW" + String.valueOf(i)));
            map.put("Weg", args.getString("WEG" + String.valueOf(i)));
            map.put("Arbeit", args.getString("Arbeit" + String.valueOf(i)));
            arbeitlist.add(map);

        }

        arbeitsliste.setAdapter(new SpecialAdapter(context, arbeitlist, R.layout.row_arbeit,
                new String[]{"Datum", "Technr", "AW", "Weg", "Arbeit"}, new int[]{R.id.editTextDatumContent,
                R.id.editTextTechNrContent,
                R.id.editTextAWContent,
                R.id.editTextWegContent,
                R.id.editTextAusArbeitContent}));

        for ( int t=0; t <= args.getInt("TeilePos");t++){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("Menge", args.getString("Anz" + String.valueOf(t)));
            map.put("TeilehNr", args.getString("TeileNr" + String.valueOf(t)));
            map.put("Bez", args.getString("Bez" + String.valueOf(t)));

            teilelist.add(map);
        }

        teileliste.setAdapter(new SpecialAdapter(context, teilelist, R.layout.row_eteile,
                new String[]{"Menge", "TeilehNr", "Bez"}, new int[]{R.id.editTextMenge,
                R.id.editTextTeileNr,
                R.id.editTextBezeichnung
               }));
        arbeitsliste.setExpanded(true);
        teileliste.setExpanded(true);





        Farb.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }


            @Override
            public void afterTextChanged(Editable edit) {
                if (Farb.length() > 0 && sw.length() > 0) {
                    int gesamt = Integer.parseInt(Farb.getText().toString()) + Integer.parseInt(sw.getText().toString());
                    Log.e("gesamt", "" + gesamt);

                    Zaehler.setText("Zähler(Gesamt: " + gesamt+")");}

            }
        });
        sw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }


            @Override
            public void afterTextChanged(Editable edit) {
                if (Farb.length() > 0 && sw.length() > 0) {

                    int gesamt = Integer.parseInt(Farb.getText().toString()) + Integer.parseInt(sw.getText().toString());
                    Log.e("gesamt", "" + gesamt);
                    Zaehler.setText("Zähler(Gesamt: " + gesamt+")");
                }

            }
        });
        










                /*try {
                    //FileOutputStream fos = new  FileOutputStream("userData.xml");
                    SessionManager session =new SessionManager(context);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.GERMAN);
                    Calendar cal = Calendar.getInstance();
                    String today = sdf.format(cal.getTime());

                    File xml = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/Jobcontrol/"+args.getString("Srn")+"_"+today+".xml")));
                    FileOutputStream fileos= new FileOutputStream (xml);

                    XmlSerializer xmlSerializer = Xml.newSerializer();
                    xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
                    StringWriter writer = new StringWriter();
                    xmlSerializer.setOutput(writer);
                    xmlSerializer.startDocument("UTF-8", true);
                    xmlSerializer.startTag(null, "AuftragsDaten");
                    xmlSerializer.startTag(null, "AuftragsKopf");
                        xmlSerializer.startTag(null, "Auftragsnummer");
                            xmlSerializer.text(args.getString("AuaNr"));
                        xmlSerializer.endTag(null, "Auftragsnummer");

                        xmlSerializer.startTag(null, "Techniker");
                            xmlSerializer.text(String.valueOf(session.getTechNum()));
                        xmlSerializer.endTag(null, "Techniker");

                        xmlSerializer.startTag(null, "Störung");
                            xmlSerializer.text(args.getString("Error"));
                        xmlSerializer.endTag(null, "Störung");


                    xmlSerializer.startTag(null, "GeräteDaten");

                            xmlSerializer.startTag(null, "Firma");
                                xmlSerializer.text(args.getString("Firma"));
                            xmlSerializer.endTag(null, "Firma");

                            xmlSerializer.startTag(null, "Strasse");
                                xmlSerializer.text(args.getString("Str"));
                            xmlSerializer.endTag(null, "Strasse");

                            xmlSerializer.startTag(null, "Ort");
                                xmlSerializer.text(args.getString("Ort"));
                            xmlSerializer.endTag(null, "Ort");

                            xmlSerializer.startTag(null, "Ansprechpartner");
                                xmlSerializer.text(args.getString("Name"));
                            xmlSerializer.endTag(null, "Ansprechpartner");

                            xmlSerializer.startTag(null, "Modell");
                                xmlSerializer.text(args.getString("Ger"));
                            xmlSerializer.endTag(null, "Modell");

                            xmlSerializer.startTag(null, "Seriennummer");
                                xmlSerializer.text(args.getString("Srn"));
                            xmlSerializer.endTag(null, "Seriennummer");

                            xmlSerializer.startTag(null, "Zähler");

                                xmlSerializer.startTag(null, "SW");
                                    xmlSerializer.text(args.getString("Sw"));
                                xmlSerializer.endTag(null, "SW");

                                xmlSerializer.startTag(null, "Farbe");
                                    xmlSerializer.text(args.getString("Farb"));
                                xmlSerializer.endTag(null, "Farbe");

                            xmlSerializer.endTag(null, "Zähler");

                        xmlSerializer.endTag(null, "GeräteDaten");

                    xmlSerializer.endTag(null, "AuftragsKopf");

                        xmlSerializer.startTag(null, "Arbeitszeit");
                        for ( int i=0; i <= args.getInt("Pos");i++) {
                            xmlSerializer.startTag(null, "Position");

                                xmlSerializer.startTag(null, "Artikelnummer");
                                    xmlSerializer.text(args.getString("LohnArtNr" + String.valueOf(i)));
                                xmlSerializer.endTag(null, "Artikelnummer");

                                xmlSerializer.startTag(null, "Datum");
                                    xmlSerializer.text(args.getString("Datum" + String.valueOf(i)));
                                xmlSerializer.endTag(null, "Datum");

                                xmlSerializer.startTag(null, "Techniker");
                                    xmlSerializer.text(args.getString("TechNr" + String.valueOf(i)));
                                xmlSerializer.endTag(null, "Techniker");

                                xmlSerializer.startTag(null, "AW");
                                    xmlSerializer.text(args.getString("AW" + String.valueOf(i)));
                                xmlSerializer.endTag(null, "AW");

                                xmlSerializer.startTag(null, "WegAW");
                                    xmlSerializer.text(args.getString("WEG" + String.valueOf(i)));
                                xmlSerializer.endTag(null, "WegAW");

                                xmlSerializer.startTag(null, "Text");
                                    xmlSerializer.text(args.getString("Arbeit" + String.valueOf(i)));
                                xmlSerializer.endTag(null, "Text");

                            xmlSerializer.endTag(null, "Position");
                        }
                        xmlSerializer.endTag(null, "Arbeitszeit");

                        xmlSerializer.startTag(null, "Artikel");

                        for ( int t=0; t <= args.getInt("TeilePos");t++){
                            if ( args.containsKey("Anz"+ String.valueOf(t))) {

                                xmlSerializer.startTag(null, "Position");

                                xmlSerializer.startTag(null, "Menge");
                                xmlSerializer.text(args.getString("Anz" + String.valueOf(t)));
                                xmlSerializer.endTag(null, "Menge");

                                xmlSerializer.startTag(null, "Artikelnummer");
                                xmlSerializer.text(args.getString("ArtNr" + String.valueOf(t)));
                                xmlSerializer.endTag(null, "Artikelnummer");

                                xmlSerializer.startTag(null, "Teilenummer");
                                xmlSerializer.text(args.getString("TeileNr" + String.valueOf(t)));
                                xmlSerializer.endTag(null, "Teilenummer");

                                xmlSerializer.startTag(null, "Bezeichnung");
                                xmlSerializer.text(args.getString("Bez" + String.valueOf(t)));
                                xmlSerializer.endTag(null, "Bezeichnung");

                                xmlSerializer.endTag(null, "Position");
                            }
                        }

                        xmlSerializer.endTag(null, "Artikel");

                        xmlSerializer.startTag(null, "Bemerkung");
                            xmlSerializer.text(Bemerkung.getText().toString());
                        xmlSerializer.endTag(null, "Bemerkung");

                        xmlSerializer.startTag(null, "Unterschrift");
                            xmlSerializer.text("Test BOLB");
                        xmlSerializer.endTag(null, "Unterschrift");

                    xmlSerializer.endTag(null, "AuftragsDaten");
                    xmlSerializer.endDocument();
                    xmlSerializer.flush();
                    String dataWrite = writer.toString();
                    fileos.write(dataWrite.getBytes(StandardCharsets.UTF_8));
                    fileos.close();

                    Toast.makeText(context, "Schein erstellt", Toast.LENGTH_SHORT).show();



                    Log.e("today",today);

                    final RequestQueue queue = MyVolley.getRequestQueue();

                    final String url = "https://hoell.syno-ds.de:55443/job/android/xml.php";



                    FileInputStream fin = new FileInputStream(xml);
                    String ret = convertStreamToString(fin);
                    //Make sure you close all streams.
                    fin.close();

                    //Log.e("xml",ret);


                    CustomBodyStringRequest jsObjRequest1 = new CustomBodyStringRequest( url,ret ,new Response.Listener<String>() {

                        @Override
                        public void onResponse(String string) {
                            Log.d("Response Volley: ", string);


                            if ( args.containsKey("TicketID")) {
                                final String index = "https://hoell.syno-ds.de:55443/job/android/index.php";

                                Map<String, String> postparams = new HashMap<String, String>();
                                postparams.put("tag", "savedetails");
                                postparams.put("user", new SessionManager(Jobcontrol.getAppCtx()).getUser());
                                postparams.put("status", "15");
                                postparams.put("id", args.getString("TicketID"));
                                Log.d("Volley Params: ", postparams.toString());


                                CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, index, postparams, new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject json) {
                                        Log.d("Response Volley: ", json.toString());

                                        try {
                                            if(json.getInt("success")==1){
                                                Intent i = new Intent(Jobcontrol.getAppCtx(), MainActivity.class);
                                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(i);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError response) {
                                        Log.d("onErrorResponse: ", response.toString());

                                    }
                                });

                                queue.add(jsObjRequest);
                            }else{
                                Intent i = new Intent(Jobcontrol.getAppCtx(), MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }

                           *//* try {
                                if(json.getInt("success")==1){
                                    Intent i = new Intent(Jobcontrol.getAppCtx(), MainActivity.class);
                                    startActivity(i);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }*//*

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError response) {
                            Log.d("onErrorResponse: ", response.toString());

                        }
                    });


                    queue.add(jsObjRequest1);




                }
                catch (IllegalArgumentException | IllegalStateException | IOException e) {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
*/






        FloatingActionButton fab_unterschrift = (FloatingActionButton) rootView.findViewById(R.id.fab_unterschrift);
        fab_unterschrift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout mContent = (LinearLayout) rootView.findViewById(R.id.linearLayout);
                 mSig=new SignatureView(context,null);
                mContent.addView(mSig, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            }
        });

        FloatingActionButton fab_next = (FloatingActionButton) rootView.findViewById(R.id.fab_next);
        fab_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSig!=null){
                    CheckBox Checkboxpruefen = (CheckBox)rootView.findViewById(R.id.checkBoxPruefen);
                    Log.i("save","klickedd");
                    args.putInt("pruefen",Checkboxpruefen.isChecked() ? 1 : 0);

                    byte[] signature = mSig.getBytes();

                    Log.d("BLOB?", Arrays.toString(signature));
                    Bitmap bitsignature = mSig.getBitmap();
                    String myBase64Image = encodeToBase64(bitsignature, Bitmap.CompressFormat.PNG, 100);
                    args.putString("BLOB",myBase64Image);
                    args.putString("bemerkung", String.valueOf(Bemerkung.getText()));

                    RequestQueue queue = MyVolley.getRequestQueue();
                    String url = "https://hoell.syno-ds.de:55443/job/android/index.php";

                    Map<String, String> postparams = new HashMap<String, String>();
                    postparams.put("tag", "scheinid");
                    postparams.put("srn", args.getString("Srn"));
                    postparams.put("ticketnr", args.getString("TicketID"));

                    Log.i("volley",postparams.toString());

                    CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, url, postparams, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject json) {
                            Log.d("Response Volley: ", json.toString());
                            //  showProgress(false);
                            try {
                                if (json.getInt("success")==1) {
                                    Log.e("succsess","yaaaaaaaaaay");
                                    new DBManager.FillScheinDB(context,args,json.getInt("ScheinId")).execute();
                                    //TODO: FillSchein mit schein id
                                } else {
                                    Log.e("GetScheinID","Failed succsess != 1");

                                    Toast.makeText(Jobcontrol.getAppCtx(), "keine schein id bekommen", Toast.LENGTH_LONG).show();



                                }
                            } catch (JSONException  e) {
                                e.printStackTrace();
                                Log.e("GetScheinID","Something went wrong w/ the json");
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError response) {
                            Log.e("eror_Response: ", response.toString());
                            new DBManager.FillScheinDB(context,args,0).execute();
                            //TODO: FIllschein mit scheinid 0 und spater nochmal versuchen

                        }
                    });

                    queue.add(jsObjRequest);






                    /* Intent i = new Intent(Jobcontrol.getAppCtx(), MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);*/

                }else{
                    Toast.makeText(context, "Bitte Schein unterschreiben lassen", Toast.LENGTH_SHORT).show();
                }


            }
        });

        return rootView;
    }


    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

}
