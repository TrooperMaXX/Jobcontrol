package de.hoell.jobcontrol.schein;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Xml;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import de.hoell.jobcontrol.Jobcontrol;
import de.hoell.jobcontrol.R;
import de.hoell.jobcontrol.adapter.ExpandableHeightListView;
import de.hoell.jobcontrol.adapter.SpecialAdapter;
import de.hoell.jobcontrol.query.DBManager;
import de.hoell.jobcontrol.query.DownloadFileFromURL;

/**
 * A placeholder fragment containing a simple view.
 */
public class pruefen extends Fragment {
    private Context context;
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
        
        
        
        
        
        
        
        
        
        
        

                FloatingActionButton fab_next = (FloatingActionButton) rootView.findViewById(R.id.fab_next);
        fab_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String xmlFile = "userData";
                String userNAme = "username";
                String password = "password";
                try {
                    //FileOutputStream fos = new  FileOutputStream("userData.xml");
                    FileOutputStream fileos= new FileOutputStream (new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/Jobcontrol/test.xml"))));
                    XmlSerializer xmlSerializer = Xml.newSerializer();
                    xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
                    StringWriter writer = new StringWriter();
                    xmlSerializer.setOutput(writer);
                    xmlSerializer.startDocument("UTF-8", true);
                    xmlSerializer.startTag(null, "Report");
                        xmlSerializer.startTag(null, "AuftragsDaten");

                            xmlSerializer.startTag(null, "Firma");
                                xmlSerializer.text(args.getString("Firma"));
                            xmlSerializer.endTag(null, "Firma");

                            xmlSerializer.startTag(null, "Strasse");
                                xmlSerializer.text(args.getString("Str"));
                            xmlSerializer.endTag(null, "Strasse");

                            xmlSerializer.startTag(null, "Ort");
                                xmlSerializer.text(args.getString("Ort"));
                            xmlSerializer.endTag(null, "Ort");

                            xmlSerializer.startTag(null, "AuaNr");
                                xmlSerializer.text("testnummer12345");
                            xmlSerializer.endTag(null, "AuaNr");

                            xmlSerializer.startTag(null, "Modell");
                                xmlSerializer.text(args.getString("Ger"));
                            xmlSerializer.endTag(null, "Modell");

                            xmlSerializer.startTag(null, "Srn");
                                xmlSerializer.text(args.getString("Srn"));
                            xmlSerializer.endTag(null, "Srn");

                            xmlSerializer.startTag(null, "SW");
                                xmlSerializer.text(args.getString("Sw"));
                            xmlSerializer.endTag(null, "SW");

                            xmlSerializer.startTag(null, "Farb");
                                xmlSerializer.text(args.getString("Farb"));
                            xmlSerializer.endTag(null, "Farb");

                        xmlSerializer.endTag(null, "AuftragsDaten");

                        xmlSerializer.startTag(null, "Arbeit");
                        for ( int i=0; i <= args.getInt("Pos");i++) {
                            xmlSerializer.startTag(null, "Position"+String.valueOf(i));

                                xmlSerializer.startTag(null, "Datum");
                                    xmlSerializer.text(args.getString("Datum" + String.valueOf(i)));
                                xmlSerializer.endTag(null, "Datum");

                                xmlSerializer.startTag(null, "Techniker");
                                    xmlSerializer.text(args.getString("TechNr" + String.valueOf(i)));
                                xmlSerializer.endTag(null, "Techniker");

                                xmlSerializer.startTag(null, "AW");
                                    xmlSerializer.text(args.getString("AW" + String.valueOf(i)));
                                xmlSerializer.endTag(null, "AW");

                                xmlSerializer.startTag(null, "Weg");
                                    xmlSerializer.text(args.getString("WEG" + String.valueOf(i)));
                                xmlSerializer.endTag(null, "Weg");

                                xmlSerializer.startTag(null, "ausgefuert");
                                    xmlSerializer.text(args.getString("Arbeit" + String.valueOf(i)));
                                xmlSerializer.endTag(null, "ausgefuert");

                            xmlSerializer.endTag(null, "Position"+String.valueOf(i));
                        }
                        xmlSerializer.endTag(null, "Arbeit");

                        xmlSerializer.startTag(null, "Teile");

                        for ( int t=0; t <= args.getInt("TeilePos");t++){
                            xmlSerializer.startTag(null, "Teil"+String.valueOf(t));

                                xmlSerializer.startTag(null, "Menge");
                                    xmlSerializer.text(args.getString("Anz" + String.valueOf(t)));
                                xmlSerializer.endTag(null, "Menge");

                                xmlSerializer.startTag(null, "TeileNr");
                                    xmlSerializer.text(args.getString("TeileNr" + String.valueOf(t)));
                                xmlSerializer.endTag(null, "TeileNr");

                                xmlSerializer.startTag(null, "Bez");
                                    xmlSerializer.text(args.getString("Bez" + String.valueOf(t)));
                                xmlSerializer.endTag(null, "Bez");

                            xmlSerializer.endTag(null, "Teil" + String.valueOf(t));
                        }

                        xmlSerializer.endTag(null, "Teile");


                    xmlSerializer.endTag(null, "Report");
                    xmlSerializer.endDocument();
                    xmlSerializer.flush();
                    String dataWrite = writer.toString();
                    fileos.write(dataWrite.getBytes());
                    fileos.close();
                }
                catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


             /*   Bundle next = addValues(args, rootView);
                Log.e("final next", "" + next);

                eteile nextFragment = new eteile();

                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.frame_container, nextFragment);
                transaction.addToBackStack(null);

                nextFragment.setArguments(next);
                // Commit the transaction
                transaction.commit();

                Toast.makeText(context, "next", Toast.LENGTH_SHORT).show();*/


            }
        });




        return rootView;
    }

   /* private Bundle addValues(Bundle next_args, View rootView) {
        Bundle next= new Bundle(next_args);

        NumberPicker numberPickerAnz = (NumberPicker) rootView.findViewById(R.id.numberPickerAnzahl);


        String Anzahl =String.valueOf(numberPickerAnz.getValue());

        String TeileNr = String.valueOf(editTextTeileNr.getText());
        String Bezeichnung =String.valueOf(editTextBezeichnung.getText());

        next.putString("Anz" + Position, Anzahl);

        next.putString("TechNr" + Position, TeileNr);
        next.putString("Arbeit" + Position, Bezeichnung);

        Log.e("NextBundle", "" + next);
        return next;
    }*/



}
