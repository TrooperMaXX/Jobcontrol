package de.hoell.jobcontrol.query;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class Refresh extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("bekommen:)", "wuhuuuuu");
        Toast.makeText(context, "lel", Toast.LENGTH_SHORT).show();
    }

}