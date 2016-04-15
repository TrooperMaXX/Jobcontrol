package de.hoell.jobcontrol.schein;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import de.hoell.jobcontrol.R;
import de.hoell.jobcontrol.query.DBManager;
import de.hoell.jobcontrol.session.SessionManager;

/**
 * A placeholder fragment containing a simple view.
 */
public class arbeit extends Fragment {
    private Context context;
    public EditText mArbeit;
    public TextView Arbeit;
    public arbeit() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.fab);

        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);


        final View rootView = localInflater.inflate(R.layout.fragment_arbeit, container, false);


        context = rootView.getContext();
        Bundle args=getArguments();

        DBManager dbManager = new DBManager(context);
        SQLiteDatabase db = dbManager.getReadableDatabase();
        String query = "SELECT " + DBManager.COLUMN_BEZEICHNUNG +
                " FROM " +
                DBManager.TABLE_LOHNART + ";";
        Cursor result = db.rawQuery(query, null);

        Log.e("querryyyyy:::", query);
        int count = result.getCount();
        String lohnbez[] = new String[count];

        int i = 0;

        while (result.moveToNext()) {
            lohnbez[i] = result.getString(result.getColumnIndex(DBManager.COLUMN_BEZEICHNUNG));

            System.out.println("values[" + i + "]: " + lohnbez[i]);

            i++;
        }
        result.close();


        /** AW Picker **/
        NumberPicker numberPickerAw = (NumberPicker) rootView.findViewById(R.id.numberPickerAw);
        numberPickerAw.setMaxValue(100);
        numberPickerAw.setMinValue(0);
        numberPickerAw.setWrapSelectorWheel(false);
        /** Weg Picker **/
        NumberPicker numberPickerWeg = (NumberPicker) rootView.findViewById(R.id.numberPickerWeg);
        numberPickerWeg.setMaxValue(100);
        numberPickerWeg.setMinValue(0);
        numberPickerWeg.setWrapSelectorWheel(false);

        NumberPicker LohnPicker = (NumberPicker) rootView.findViewById(R.id.lohnart);
        LohnPicker.setMaxValue(count-1);
        LohnPicker.setMinValue(0);
        LohnPicker.setDisplayedValues(lohnbez);
        LohnPicker.setWrapSelectorWheel(false);
        LohnPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        EditText editTextTechnikernr =  (EditText) rootView.findViewById(R.id.editTextTechniker);
        SessionManager session = new SessionManager(context);

        editTextTechnikernr.setText(String.valueOf(session.getTechNum()));

        Arbeit =  (TextView) rootView.findViewById(R.id.TextArbeit);

        Arbeit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getArbeitDialog();
            }
        });




        String mSeriennummer =args.getString("Srn");
        final   int pos =args.getInt("Pos");
        Log.e("args", mSeriennummer + "lol " + args);
        final  Bundle next_args=args;

        FloatingActionButton fab_next = (FloatingActionButton) rootView.findViewById(R.id.fab_next);
        fab_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle next= addValues(next_args,rootView,String.valueOf(pos));
                Log.e("final next", ""+next );

                eteile nextFragment = new eteile();

                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.frame_container, nextFragment);
                transaction.addToBackStack(null);

                nextFragment.setArguments(next);
                // Commit the transaction
                transaction.commit();

                Toast.makeText(context, "next", Toast.LENGTH_SHORT).show();



            }
        });
        FloatingActionButton fab_add = (FloatingActionButton) rootView.findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle next= addValues(next_args,rootView,String.valueOf(pos));

                arbeit nextFragment = new arbeit();

                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.frame_container, nextFragment);
                transaction.addToBackStack(null);
                next.putInt("Pos", pos + 1);
                nextFragment.setArguments(next);
                // Commit the transaction
                transaction.commit();
                Toast.makeText(context, "add", Toast.LENGTH_SHORT).show();


            }
        });





        return rootView;
    }

    private Bundle addValues(Bundle next_args, View rootView,String Position) {
        Bundle next= new Bundle(next_args);
        DatePicker Datum = (DatePicker) rootView.findViewById(R.id.datePickerArbeit);
        NumberPicker Aw = (NumberPicker) rootView.findViewById(R.id.numberPickerAw);
        NumberPicker Weg = (NumberPicker) rootView.findViewById(R.id.numberPickerWeg);
        NumberPicker LohnPicker = (NumberPicker) rootView.findViewById(R.id.lohnart);
        EditText TechnikerNr =  (EditText) rootView.findViewById(R.id.editTextTechniker);
        TextView Arbeit =  (TextView) rootView.findViewById(R.id.TextArbeit);

        String Lohnarten[]=LohnPicker.getDisplayedValues();
        String Lohnart=Lohnarten[LohnPicker.getValue()];

        DBManager dbManager = new DBManager(context);
        SQLiteDatabase db = dbManager.getReadableDatabase();
        String query = "SELECT " + DBManager.COLUMN_LOHNARTNR +
                " FROM " +
                DBManager.TABLE_LOHNART +
                " WHERE "+DBManager.COLUMN_BEZEICHNUNG+" = '" +Lohnart+"';";
        Cursor lohnresult = db.rawQuery(query, null);

        Log.e("querryyyyy:::", query);
        int count = lohnresult.getCount();
        int lohnartnr[] = new int[count];

        int i = 0;

        while (lohnresult.moveToNext()) {
            lohnartnr[i] = lohnresult.getInt(lohnresult.getColumnIndex(DBManager.COLUMN_LOHNARTNR));

            System.out.println("values[" + i + "]: " + lohnartnr[i]);

            i++;
        }
        lohnresult.close();

        String Date = String.valueOf(String.valueOf(Datum.getDayOfMonth())+"-"+String.valueOf(Datum.getMonth()+1)+"-"+Datum.getYear());
        Log.e("DATUM!!!", Date);
        String AW =String.valueOf(Aw.getValue());
        String WEG =String.valueOf(Weg.getValue());
        String TechNr = String.valueOf(TechnikerNr.getText());
        String ARBEIT =String.valueOf(Arbeit.getText());
        String LohnArtNr =String.valueOf(lohnartnr[0]);

        next.putString("AW"+Position,AW);
        next.putString("WEG"+Position,WEG);
        next.putString("TechNr"+Position,TechNr);
        next.putString("Arbeit"+Position,ARBEIT);
        next.putString("Datum"+Position, Date);
        next.putString("LohnArtNr"+Position, LohnArtNr);
        Log.e("NextBundle",""+next);
        return next;
    }
    private void getArbeitDialog() {
        View mView = View.inflate(context, R.layout.dialog_arbeit, null);
        mArbeit = ((EditText) mView.findViewById(R.id.getarbeit));
        mArbeit.setText(Arbeit.getText().toString());
        mArbeit.setSelection(mArbeit.getText().length());

        final InputMethodManager mInputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.restartInput(mView);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder( context);
        mBuilder.setTitle(getString(R.string.getarbeit));
        mBuilder.setPositiveButton(getString(R.string.save), new Dialog.OnClickListener() {
            public void onClick(DialogInterface mDialogInterface, int mWhich) {
                String mGetArbeitString = mArbeit.getText().toString().trim();
                if (mGetArbeitString.length() > 0) {
                    Arbeit.setText(mGetArbeitString);
                    mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    mDialogInterface.dismiss();
                }
            }
        });

        mBuilder.setNegativeButton(getString(R.string.cancel), new Dialog.OnClickListener() {
            public void onClick(DialogInterface mDialogInterface, int mWhich) {

                    mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    mDialogInterface.dismiss();

            }
        });

        mBuilder.setView(mView);
        mBuilder.show();

        if (mInputMethodManager != null) {
            mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

}
