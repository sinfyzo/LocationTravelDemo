package glplus.arnion.com.locationtraveldemo.utils;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;

import java.net.Socket;

/**
 * Created by welcome on 02-12-2017.
 */

public class AppController extends Application {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private static AppController mInstance;
    private static Activity mactivity;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        sharedPreferences = initSharedPrefs();
        editor = sharedPreferences.edit();

    }
    public static synchronized AppController getInstance(Activity mactivityPara) {
        mactivity = mactivityPara;
        return mInstance;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }
    public SharedPreferences initSharedPrefs() {
        sharedPreferences = getSharedPreferences(Const.SHARED_PREF_FILE, MODE_PRIVATE);
        return sharedPreferences;
    }

    public static SharedPreferences.Editor getSharedPrefsEditor() {
        return editor;
    }

    public static SharedPreferences getSharedPrefs() {
        return sharedPreferences;
    }
}
