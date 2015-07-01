package de.hoell.jobcontrol;

import android.app.Application;
import android.content.Context;

/**
 * Created by Hoell on 30.06.2015.
 */
public class Jobcontrol extends Application {

    protected static Context appCtx=null;

    @Override
    public void onCreate(){
        super.onCreate();
        appCtx=getApplicationContext();


    }
    public static Context getAppCtx(){

        return appCtx;
    }
}
