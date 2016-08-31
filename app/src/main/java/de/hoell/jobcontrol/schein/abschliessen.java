package de.hoell.jobcontrol.schein;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hoell.jobcontrol.Jobcontrol;
import de.hoell.jobcontrol.MainActivity;
import de.hoell.jobcontrol.R;
import de.hoell.jobcontrol.adapter.ExpandableHeightListView;
import de.hoell.jobcontrol.adapter.SpecialAdapter;
import de.hoell.jobcontrol.query.CustomRequest;
import de.hoell.jobcontrol.query.DBManager;
import de.hoell.jobcontrol.query.MyVolley;
import de.hoell.jobcontrol.session.SessionManager;

public class abschliessen extends Fragment {

    private Context context;
    SignatureView mSig;
    boolean issign = false;
    String myBase64Image = "";
    JSONObject DBSchein = null;
    EditText editTextTeileNr, editTextBezeichnung;
    static TextView mDate;

    public abschliessen() {
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

        final View rootView = localInflater.inflate(R.layout.fragment_abschliessen, container, false);

        TextView Anschrift = (TextView) rootView.findViewById(R.id.TextViewAnschriftContent);
        TextView Ger = (TextView) rootView.findViewById(R.id.TextViewGerContent);
        TextView Srn = (TextView) rootView.findViewById(R.id.TextViewSrnContent);
        final TextView Zaehler = (TextView) rootView.findViewById(R.id.textViewZaehler);
        final TextView sw = (TextView) rootView.findViewById(R.id.EditTextSWContent);
        final TextView Farb = (TextView) rootView.findViewById(R.id.EditTextFarbContent);
        final EditText Bemerkung = (EditText) rootView.findViewById(R.id.editTextBemerkung);
        final EditText Klarname = (EditText) rootView.findViewById(R.id.editTextKlarname);
        final EditText Email = (EditText) rootView.findViewById(R.id.editTextEmail);

        final ExpandableHeightListView arbeitsliste = (ExpandableHeightListView) rootView.findViewById(R.id.arbeitList);
        final ExpandableHeightListView teileliste = (ExpandableHeightListView) rootView.findViewById(R.id.teileList);
        final FloatingActionButton fab_next = (FloatingActionButton) rootView.findViewById(R.id.fab_next);
        final FloatingActionButton fab_save = (FloatingActionButton) rootView.findViewById(R.id.fab_save);
        final FloatingActionButton fab_remove = (FloatingActionButton) rootView.findViewById(R.id.fab_sign_remove);
        final FloatingActionButton fab_check = (FloatingActionButton) rootView.findViewById(R.id.fab_sign_check);
        final LinearLayout mContent = (LinearLayout) rootView.findViewById(R.id.SignLayout);

        context = rootView.getContext();
        final Bundle bundle = getArguments();
        final  Map<String, String> args = DBManager.GetSchein(context, bundle.getInt("ScheinId"),false);
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
        try {

            DBSchein = new JSONObject(args.get("scheindata"));
            Log.d("DBSchein", "" + DBSchein);
            JSONObject DBVde = new JSONObject(args.get("vdedata"));
            Log.d("DBVde", "" + DBVde);
            JSONObject DBUnterschrift = new JSONObject(args.get("unterschriftdata"));
            Log.d("DBUnterschrift", "" + DBUnterschrift);
            JSONArray DBTeile = new JSONArray(args.get("teiledata"));
            Log.d("DBTeile", "" + DBTeile);
            JSONArray DBArbeit = new JSONArray(args.get("arbeitdata"));
            Log.d("DBArbeit", "" + DBArbeit);
            JSONObject DBZaehler = new JSONObject(args.get("zaehlerdata"));
            Log.d("DBZaehler", "" + DBZaehler);


            String anschrift = bundle.getString("Firma") + "\n" + bundle.getString("Str") + "\n" + bundle.getString("Ort");
            Anschrift.setText(anschrift);


            Ger.setText(bundle.getString("Ger"));
            Srn.setText(DBSchein.getString("srn"));
            Email.setText(DBSchein.getString("email"));
            String ges = "Zähler(" + (DBZaehler.optInt("z1",0) + DBZaehler.optInt("z2",0)) + ")";
            Zaehler.setText(ges);
            sw.setText(String.valueOf(DBZaehler.optInt("z1",0)));
            Farb.setText(String.valueOf(DBZaehler.optInt("z2",0)));

            for (int i = 0; i < DBArbeit.length(); i++) {
                JSONObject Arbeit = DBArbeit.getJSONObject(i);
                Log.e("arbeit", Arbeit.toString());
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("Datum", Arbeit.getString("datum"));
                map.put("Artnr", Arbeit.getString("artnr"));
                map.put("Technr", Arbeit.getString("technr"));
                map.put("AW", Arbeit.getString("mengeaw"));
                map.put("Weg", Arbeit.getString("wegaw"));
                map.put("Arbeit", Arbeit.getString("text"));
                map.put("Sync", Arbeit.getString("syncid"));
                arbeitlist.add(map);

            }
SpecialAdapter arbeitsadapter =new SpecialAdapter(context, arbeitlist, R.layout.row_arbeit,
        new String[]{"Datum","Artnr", "Technr", "AW", "Weg", "Arbeit", "Sync"}, new int[]{R.id.TextViewDatumContent,
        R.id.TextViewArtnr,
        R.id.TextViewTechNrContent,
        R.id.TextViewAWContent,
        R.id.TextViewWegContent,
        R.id.TextViewAusArbeitContent,
        R.id.TextViewSyncidArbeit});
            arbeitsliste.setAdapter(arbeitsadapter);


            for (int t = 0; t < DBTeile.length(); t++) {
                JSONObject Teile = DBTeile.getJSONObject(t);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("Menge", Teile.getString("mengeaw"));
                map.put("TeilehNr", Teile.getString("artnr"));
                map.put("Bez", Teile.getString("text"));
                map.put("Sync", Teile.getString("syncid"));

                teilelist.add(map);
            }
            SpecialAdapter teileadapter =new SpecialAdapter(context, teilelist, R.layout.row_eteile,
                    new String[]{"Menge", "TeilehNr", "Bez", "Sync"}, new int[]{R.id.TextViewMenge,
                    R.id.TextViewTeileNr,
                    R.id.TextViewBezeichnung,
                    R.id.TextViewSyncidTeile
            });
            teileliste.setAdapter(teileadapter);

            arbeitsliste.setExpanded(true);
            teileliste.setExpanded(true);


            arbeitsliste.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {


                    final TextView syncid = (TextView) view.findViewById(R.id.TextViewSyncidArbeit);


                    Log.e("test it hard", pos + " - " + syncid.getText().toString());

                    TextView Datum = (TextView) view.findViewById(R.id.TextViewDatumContent);
                    final TextView LohnArtNr = (TextView) view.findViewById(R.id.TextViewArtnr);
                    TextView Technr = (TextView) view.findViewById(R.id.TextViewTechNrContent);
                    TextView Aw = (TextView) view.findViewById(R.id.TextViewAWContent);
                    TextView Weg = (TextView) view.findViewById(R.id.TextViewWegContent);
                    TextView Arbeit = (TextView) view.findViewById(R.id.TextViewAusArbeitContent);

                    View mView = View.inflate(context, R.layout.dialog_change_arbeit, null);


                    //DatePicker mDate = (DatePicker) mView.findViewById(R.id.datePicker);
                    mDate = (TextView) mView.findViewById(R.id.textViewChangeDatum);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
                    Date dDatum = null;
                    String formated = "null";
                    try {
                        dDatum = sdf.parse(Datum.getText().toString());
                        formated = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMAN).format(dDatum);
                        Log.d("formated", formated);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    mDate.setText(formated);
                    mDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DialogFragment newFragment = new DatePickerFragment();
                            newFragment.show(getFragmentManager(), "datePicker");
                        }
                    });

                    System.out.println(mDate.getText().toString());
                    final EditText mTechnr = (EditText) mView.findViewById(R.id.editTextChangeTechNr);
                    mTechnr.setText(Technr.getText().toString());
                    final EditText mAw = (EditText) mView.findViewById(R.id.editTextChangeAw);
                    mAw.setText(Aw.getText().toString());
                    final EditText mWeg = (EditText) mView.findViewById(R.id.editTextChangeWeg);
                    mWeg.setText(Weg.getText().toString());
                    final EditText mArbeit = (EditText) mView.findViewById(R.id.editTextChangeText);
                    mArbeit.setText(Arbeit.getText().toString());


                    final InputMethodManager mInputMethodManager = (InputMethodManager) context
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    mInputMethodManager.restartInput(mView);

                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                    mBuilder.setTitle(getString(R.string.posaendern));
                    mBuilder.setPositiveButton(getString(R.string.save), new Dialog.OnClickListener() {
                        public void onClick(DialogInterface mDialogInterface, int mWhich) {

                            System.out.println(mDate.getText().toString());
                            DBManager.InsterArbeit(context,bundle.getInt("ScheinId"),LohnArtNr.getText().toString(),mDate.getText().toString(),mTechnr.getText().toString(),mAw.getText().toString(),mWeg.getText().toString(),mArbeit.getText().toString(),0,syncid.getText().toString());
                            abschliessen nextFragment = new abschliessen();

                            FragmentTransaction transaction = getFragmentManager().beginTransaction();

                            // Replace whatever is in the fragment_container view with this fragment,
                            // and add the transaction to the back stack so the user can navigate back
                            transaction.replace(R.id.frame_container, nextFragment);
                            transaction.addToBackStack(null);

                            nextFragment.setArguments(bundle);
                            //nextFragment.setArguments(next);
                            // Commit the transaction
                            transaction.commit();
                        }
                    });

                    mBuilder.setNegativeButton(getString(R.string.cancel), new Dialog.OnClickListener() {
                        public void onClick(DialogInterface mDialogInterface, int mWhich) {

                            mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                            mDialogInterface.dismiss();

                        }
                    });


                    mBuilder.setNeutralButton(getString(R.string.delete), new Dialog.OnClickListener() {
                        public void onClick(DialogInterface mDialogInterface, int mWhich) {

                            DBManager.InsterArbeit(context,bundle.getInt("ScheinId"),LohnArtNr.getText().toString(),mDate.getText().toString(),mTechnr.getText().toString(),mAw.getText().toString(),mWeg.getText().toString(),mArbeit.getText().toString(),1,syncid.getText().toString());
                            mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                            mDialogInterface.dismiss();
                            abschliessen nextFragment = new abschliessen();

                            FragmentTransaction transaction = getFragmentManager().beginTransaction();

                            // Replace whatever is in the fragment_container view with this fragment,
                            // and add the transaction to the back stack so the user can navigate back
                            transaction.replace(R.id.frame_container, nextFragment);
                            transaction.addToBackStack(null);

                            nextFragment.setArguments(bundle);
                            //nextFragment.setArguments(next);
                            // Commit the transaction
                            transaction.commit();

                        }
                    });

                    mBuilder.setView(mView);
                    mBuilder.show();

                    if (mInputMethodManager != null) {
                        mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    }


                    return false;
                }

            });

            teileliste.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {

                    final TextView syncid = (TextView) view.findViewById(R.id.TextViewSyncidTeile);

                    Log.e("test it hard", pos + " - " + syncid.getText().toString());

                    TextView Menge = (TextView) view.findViewById(R.id.TextViewMenge);
                    final TextView TeileNr = (TextView) view.findViewById(R.id.TextViewTeileNr);
                    final TextView Bez = (TextView) view.findViewById(R.id.TextViewBezeichnung);
                    View mView = View.inflate(context, R.layout.dialog_change_teile, null);




                    final EditText mMenge = (EditText) mView.findViewById(R.id.editTextChangeMenge);
                    mMenge.setText(Menge.getText().toString());
                    final TextView mTeileNr = (TextView) mView.findViewById(R.id.textViewChangeTeineNr);
                    mTeileNr.setText(TeileNr.getText().toString());
                    final TextView mBez = (TextView) mView.findViewById(R.id.textViewChangeBezeichnung);
                    mBez.setText(Bez.getText().toString());


                    final InputMethodManager mInputMethodManager = (InputMethodManager) context
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    mInputMethodManager.restartInput(mView);
                    final SessionManager session =new SessionManager(context);
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                    mBuilder.setTitle(getString(R.string.posaendern));
                    mBuilder.setPositiveButton(getString(R.string.save), new Dialog.OnClickListener() {
                        public void onClick(DialogInterface mDialogInterface, int mWhich) {


                            DBManager.InsterTeile(context,bundle.getInt("ScheinId"),mTeileNr.getText().toString(), session.getTechNum(), Integer.parseInt(mMenge.getText().toString()), mBez.getText().toString(),0,syncid.getText().toString());
                            abschliessen nextFragment = new abschliessen();

                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            // Replace whatever is in the fragment_container view with this fragment,
                            // and add the transaction to the back stack so the user can navigate back
                            transaction.replace(R.id.frame_container, nextFragment);
                            transaction.addToBackStack(null);

                            nextFragment.setArguments(bundle);
                            //nextFragment.setArguments(next);
                            // Commit the transaction
                            transaction.commit();
                        }
                    });

                    mBuilder.setNegativeButton(getString(R.string.cancel), new Dialog.OnClickListener() {
                        public void onClick(DialogInterface mDialogInterface, int mWhich) {

                            mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                            mDialogInterface.dismiss();

                        }
                    });

                    mBuilder.setNeutralButton(getString(R.string.delete), new Dialog.OnClickListener() {
                        public void onClick(DialogInterface mDialogInterface, int mWhich) {

                            DBManager.InsterTeile(context,bundle.getInt("ScheinId"),mTeileNr.getText().toString(), session.getTechNum(), Integer.parseInt(mMenge.getText().toString()), mBez.getText().toString(),1,syncid.getText().toString());

                            mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                            mDialogInterface.dismiss();

                            abschliessen nextFragment = new abschliessen();

                            FragmentTransaction transaction = getFragmentManager().beginTransaction();

                            // Replace whatever is in the fragment_container view with this fragment,
                            // and add the transaction to the back stack so the user can navigate back
                            transaction.replace(R.id.frame_container, nextFragment);
                            transaction.addToBackStack(null);

                            nextFragment.setArguments(bundle);
                            //nextFragment.setArguments(next);
                            // Commit the transaction
                            transaction.commit();

                        }
                    });

                    mBuilder.setView(mView);
                    mBuilder.show();

                    if (mInputMethodManager != null) {
                        mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    }


                    return false;
                }

            });


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

                        Zaehler.setText("Zähler(Gesamt: " + gesamt + ")");
                    }

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
                        Zaehler.setText("Zähler(Gesamt: " + gesamt + ")");
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

                            try {
                                if(json.getInt("success")==1){
                                    Intent i = new Intent(Jobcontrol.getAppCtx(), MainActivity.class);
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


                    queue.add(jsObjRequest1);




                }
                catch (IllegalArgumentException | IllegalStateException | IOException e) {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }


        final ImageView unterschrift = (ImageView) rootView.findViewById(R.id.img_sign);
        unterschrift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                mContent.setBackgroundResource(R.drawable.sign_);
                mContent.setVisibility(View.VISIBLE);
                mSig = new SignatureView(context, null);
                mContent.addView(mSig, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                fab_next.setVisibility(View.GONE);
                fab_save.setVisibility(View.GONE);
                fab_remove.setVisibility(View.VISIBLE);
                fab_remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContent.setBackground(null);
                        if (mSig == null) {
                            fab_remove.setVisibility(View.GONE);
                            fab_check.setVisibility(View.GONE);
                            mContent.setVisibility(View.GONE);
                            fab_next.setVisibility(View.VISIBLE);
                            fab_save.setVisibility(View.VISIBLE);
                            issign = false;
                        } else {
                            mContent.setBackgroundResource(R.drawable.sign_);
                            mSig.clear();
                            issign = false;
                        }


                    }
                });


                fab_check.setVisibility(View.VISIBLE);
                fab_check.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        byte[] signature = mSig.getBytes();
                        mContent.setBackground(null);
                        Log.d("BLOB?", Arrays.toString(signature));
                        Bitmap bitsignature = mSig.getBitmap();
                        Bitmap rotatedbitmap = RotateBitmap(bitsignature, 90);
                        myBase64Image = encodeToBase64(rotatedbitmap, Bitmap.CompressFormat.PNG, 100);
                        unterschrift.setImageBitmap(rotatedbitmap);
                        fab_remove.setVisibility(View.GONE);
                        fab_check.setVisibility(View.GONE);
                        mContent.setVisibility(View.GONE);
                        fab_next.setVisibility(View.VISIBLE);
                        fab_save.setVisibility(View.VISIBLE);
                        issign = true;
                        mSig.clear();
                        mContent.removeAllViews();
                    }
                });


            }
        });


        fab_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   /* if(Email.length()>3){


                    if (issign && Klarname.length()>3 ){*/
                CheckBox Checkboxpruefen = (CheckBox) rootView.findViewById(R.id.checkBoxPruefen);
                Log.i("save", "klickedd");
                //bundle.putInt("pruefen",Checkboxpruefen.isChecked() ? 1 : 0);
                bundle.putString("BLOB", myBase64Image);
                //bundle.putString("bemerkung", String.valueOf(Bemerkung.getText()));

                DBManager.UpdateSchein(context, bundle.getInt("ScheinId"), String.valueOf(Bemerkung.getText()), String.valueOf(Email.getText()),Checkboxpruefen.isChecked() ? 1 : 0);
                DBManager.InsterUnterschrift(context, bundle.getInt("ScheinId"), String.valueOf(Klarname.getText()), myBase64Image, false);


                Intent i = new Intent(Jobcontrol.getAppCtx(), MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

                    /*}else{
                        Toast.makeText(context, "Bitte Schein unterschreiben lassen", Toast.LENGTH_SHORT).show();
                    }

                }else {
                        Toast.makeText(context, "Bitte eMail eintragen lassen", Toast.LENGTH_SHORT).show();
                    }*/
            }
        });


        fab_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Email.length() > 3) {


                    if (issign && Klarname.length() > 3) {
                        CheckBox Checkboxpruefen = (CheckBox) rootView.findViewById(R.id.checkBoxPruefen);
                        Log.i("save", "klickedd");
                        //bundle.putInt("pruefen",Checkboxpruefen.isChecked() ? 1 : 0);
                        bundle.putString("BLOB", myBase64Image);
                        //bundle.putString("bemerkung", String.valueOf(Bemerkung.getText()));

                        DBManager.UpdateSchein(context, bundle.getInt("ScheinId"), String.valueOf(Bemerkung.getText()), String.valueOf(Email.getText()),Checkboxpruefen.isChecked() ? 1 : 0);
                        DBManager.InsterUnterschrift(context, bundle.getInt("ScheinId"), String.valueOf(Klarname.getText()), myBase64Image, true);

                        try {

                            RequestQueue queue = MyVolley.getRequestQueue();
                            final String index = "https://hoell.syno-ds.de:55443/job/android/index.php";

                            Map<String, String> postparams = new HashMap<String, String>();
                            postparams.put("tag", "savedetails");
                            postparams.put("user", new SessionManager(context).getUser());
                            postparams.put("status", "15");
                            postparams.put("id", DBSchein.getString("ticketnr"));
                            postparams.put("xml", String.valueOf(bundle.getInt("ScheinId")));

                            Log.d("Volley Params: ", postparams.toString());
                            Log.i("volley", postparams.toString());


                            CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, index, postparams, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject json) {
                                    Log.d("Response Volley: ", json.toString());
                                    //  showProgress(false);
                                    try {
                                        if (json.getInt("success") == 1) {
                                            Log.e("succsess", "yaaaaaaaaaay");

                                        } else {
                                            Log.e("GetScheinID", "Failed succsess != 1");

                                            Toast.makeText(Jobcontrol.getAppCtx(), "keine schein id bekommen", Toast.LENGTH_LONG).show();


                                        }
                                        Intent i = new Intent(Jobcontrol.getAppCtx(), MainActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Log.e("GetScheinID", "Something went wrong w/ the json");
                                    }
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError response) {
                                    Log.e("eror_Response: ", response.toString());
                                    Intent i = new Intent(Jobcontrol.getAppCtx(), MainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);

                                }
                            });

                            queue.add(jsObjRequest);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(context, "Bitte Schein unterschreiben lassen", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(context, "Bitte eMail eintragen lassen", Toast.LENGTH_SHORT).show();
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

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){

            String date = mDate.getText().toString();     // full date
            String[] parts = date.split("\\-"); // String array, each element is text between dots

            int year = Integer.parseInt(parts[0]);
            int month = (Integer.parseInt(parts[1])-1);
            int day = Integer.parseInt(parts[2]);

            Log.d("date kram", date+": "+year+" ~ "+month+ " ~ "+ day);
            /*
                Create a DatePickerDialog using Theme.

                    DatePickerDialog(Context context, int theme, DatePickerDialog.OnDateSetListener listener,
                        int year, int monthOfYear, int dayOfMonth)
             */

            // DatePickerDialog THEME_DEVICE_DEFAULT_LIGHT
           DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                    android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,this,year,month,day);

           /*  // DatePickerDialog THEME_DEVICE_DEFAULT_DARK
            DatePickerDialog dpd2 = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_DARK,this,year,month,day);

           // DatePickerDialog THEME_HOLO_LIGHT
            DatePickerDialog dpd3 = new DatePickerDialog(getActivity(),
                    android.app.AlertDialog.THEME_HOLO_LIGHT,this,year,month,day);
            // DatePickerDialog THEME_HOLO_DARK
            DatePickerDialog dpd4 = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_HOLO_DARK,this,year,month,day);

            // DatePickerDialog THEME_TRADITIONAL
            DatePickerDialog dpd5 = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_TRADITIONAL,this,year,month,day);*/

            // Return the DatePickerDialog
            return  dpd;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            String datum =day+"-"+(month+1)+"-"+year;
            mDate.setText(datum);

        }
    }

}


