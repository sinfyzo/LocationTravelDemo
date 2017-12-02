package glplus.arnion.com.locationtraveldemo.utils;

import android.content.SharedPreferences;

/**
 * Created by welcome on 02-12-2017.
 */

public class PrefsManager {


    private static final SharedPreferences sharedPreferences = AppController.getSharedPrefs();
    private static final SharedPreferences.Editor editor = AppController.getSharedPrefsEditor();

    public static final String SP_lATITUDE = "latitude";
    public static final String SP_LONGITUDE = "longitude";

    public PrefsManager() {
    }
    //set default values in Sharedprefernces
    public static void clearPrefs() {
        setLatitude("");
        setLongitude("");
    }

    public static void setLatitude(String s) {
        editor.putString(SP_lATITUDE, s);
        editor.commit();
    }

    public static void setLongitude(String s) {
        editor.putString(SP_LONGITUDE, s);
        editor.commit();
    }

    public static double getLatitude() {
        return Double.parseDouble(sharedPreferences.getString(SP_lATITUDE,"0.0"));
    }
    public static double getLongitude() {
        return Double.parseDouble(sharedPreferences.getString(SP_LONGITUDE,"0.0"));
    }
}