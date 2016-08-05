package de.hoell.jobcontrol.schein;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
        TextView Anschrift = (TextView) rootView.findViewById(R.id.TextViewAnschriftContent);
        TextView Ger = (TextView) rootView.findViewById(R.id.TextViewGerContent);
        TextView Srn = (TextView) rootView.findViewById(R.id.TextViewSrnContent);
        final TextView Zaehler = (TextView) rootView.findViewById(R.id.textViewZaehler);
        final TextView sw = (TextView) rootView.findViewById(R.id.TextViewSWContent);
        final TextView Farb = (TextView) rootView.findViewById(R.id.TextViewFarbContent);
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
                new String[]{"Datum", "Technr", "AW", "Weg", "Arbeit"}, new int[]{R.id.TextViewDatumContent,
                R.id.TextViewTechNrContent,
                R.id.TextViewAWContent,
                R.id.TextViewWegContent,
                R.id.TextViewAusArbeitContent}));

        for ( int t=0; t <= args.getInt("TeilePos");t++){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("Menge", args.getString("Anz" + String.valueOf(t)));
            map.put("TeilehNr", args.getString("TeileNr" + String.valueOf(t)));
            map.put("Bez", args.getString("Bez" + String.valueOf(t)));

            teilelist.add(map);
        }

        teileliste.setAdapter(new SpecialAdapter(context, teilelist, R.layout.row_eteile,
                new String[]{"Menge", "TeilehNr", "Bez"}, new int[]{R.id.TextViewMenge,
                R.id.TextViewTeileNr,
                R.id.TextViewBezeichnung
               }));
        arbeitsliste.setExpanded(true);
        teileliste.setExpanded(true);



        FloatingActionButton fab_next = (FloatingActionButton) rootView.findViewById(R.id.fab_next);
        fab_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("save","klickedd");
                    CheckBox Checkboxpruefen = (CheckBox)rootView.findViewById(R.id.checkBoxPruefen);
                    args.putInt("pruefen",Checkboxpruefen.isChecked() ? 1 : 0);


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



                abschliessen nextFragment = new abschliessen();

                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.frame_container, nextFragment);
                transaction.addToBackStack(null);

                //nextFragment.setArguments(next);
                // Commit the transaction
                transaction.commit();

                Toast.makeText(context, "next", Toast.LENGTH_SHORT).show();



            }
        });

        return rootView;
    }

}
