package de.hoell.jobcontrol.adapter;

/**
 * Created by Hoell on 06.02.2015.
 */
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import org.json.JSONArray;

import de.hoell.jobcontrol.TicketFragment;

public class SpecialAdapter extends SimpleAdapter {
    private int[] colors = new int[] { 0x30FF0000, 0x300000FF };

    public SpecialAdapter(Context context, List<HashMap<String, String>> items, int resource, String[] from, int[] to) {
        super(context, items, resource, from, to);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
       // int colorPos = position % colors.length;
       // TicketFragment ticketFragment=new TicketFragment();
       //int Dpos =ticketFragment.DropPos;
        //Log.e("Liste im VIEW","Ticketliste: "+ Dpos);
        //liste[position]
       // view.setBackgroundColor(colors[colorPos]);
        return view;
    }
}