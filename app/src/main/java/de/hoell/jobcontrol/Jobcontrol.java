package de.hoell.jobcontrol;

import android.app.Application;
import android.content.Context;

import de.hoell.jobcontrol.query.DBManager;
import de.hoell.jobcontrol.query.MyVolley;


/**
 * Created by Hoell on 30.06.2015.
 */
public class Jobcontrol extends Application {

    protected static Context appCtx=null;

    @Override
    public void onCreate(){
        super.onCreate();
        appCtx=getApplicationContext();
        new DBManager(appCtx);


        /*Stetho.initializeWithDefaults(this);
        Log.e("Stetho", "done");*/

        init();

    }
    public static Context getAppCtx(){

        return appCtx;
    }
    private void init() {
        MyVolley.init(this);
    }
}

