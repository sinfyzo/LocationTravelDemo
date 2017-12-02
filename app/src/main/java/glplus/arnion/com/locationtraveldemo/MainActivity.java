package glplus.arnion.com.locationtraveldemo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import glplus.arnion.com.locationtraveldemo.listners.DistanceListner;
import glplus.arnion.com.locationtraveldemo.listners.PermissionListioner;
import glplus.arnion.com.locationtraveldemo.utils.GPSTracker;
import glplus.arnion.com.locationtraveldemo.utils.PermissionUtil;
import glplus.arnion.com.locationtraveldemo.utils.PrefsManager;
import glplus.arnion.com.locationtraveldemo.utils.Util;

public class MainActivity extends AppCompatActivity implements DistanceListner{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    PermissionUtil permissionUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        permissionUtil = PermissionUtil.getInstance(this);
        permissionUtil.checkAudioPermission(permissionListioner);
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onReached50Meters() {
        addNotification();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return CurrentLocationFragment.newInstance(0);
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return DistanceTravelledFragment.newInstance(1);
                case 2: // Fragment # 1 - This will show SecondFragment
                    return DistanceTravelledFragment.newInstance(2);
                default:
                    return null;
            }
            //return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class CurrentLocationFragment extends Fragment {
        GPSTracker gps;
        TextView textView;
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public CurrentLocationFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static CurrentLocationFragment newInstance(int sectionNumber) {
            CurrentLocationFragment fragment = new CurrentLocationFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }

        @Override
        public void onStart() {
            super.onStart();
            System.out.println("CurrentLocationFragment onStart");
        }

        @Override
        public void onResume() {
            super.onResume();
            System.out.println("CurrentLocationFragment onResume");
            // create class object
            gps = new GPSTracker(getActivity().getApplicationContext());

            // check if GPS enabled
            if(gps.canGetLocation()){

                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();
                PrefsManager.setLatitude(""+latitude);
                PrefsManager.setLongitude(""+longitude);
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(address)
                            .append(",").append(city)
                            .append(",").append(state)
                            .append(",").append(country);
                    textView.setText(stringBuilder.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }


                // \n is for new line
                //Toast.makeText(getActivity().getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            }else{
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gps.showSettingsAlert();
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            System.out.println("CurrentLocationFragment onPause");
        }

        @Override
        public void onStop() {
            super.onStop();
            System.out.println("CurrentLocationFragment onStop");
        }
    }
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DistanceTravelledFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        GPSTracker gps;
        TextView textView;
        DistanceListner distanceListner;
        public DistanceTravelledFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static DistanceTravelledFragment newInstance(int sectionNumber) {
            DistanceTravelledFragment fragment = new DistanceTravelledFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
        @Override
        public void onStart() {
            super.onStart();
            System.out.println("CurrentLocationFragment onStart");
            distanceListner = (DistanceListner)getActivity().getBaseContext();
            distanceListner.onReached50Meters();

        }
        @Override
        public void onResume() {
            super.onResume();
            System.out.println("DistanceTravelledFragment onResume");
            gps = new GPSTracker(getActivity().getApplicationContext());

            // check if GPS enabled
            if(gps.canGetLocation()){
                Location from = new Location("");
                from.setLatitude(PrefsManager.getLatitude());
                from.setLongitude(PrefsManager.getLongitude());
                Location to = gps.getLocation();
                textView.setText("" + from.distanceTo(to) + " meters") ;
                // \n is for new line
                //Toast.makeText(getActivity().getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            }else{
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gps.showSettingsAlert();
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            System.out.println("DistanceTravelledFragment onPause");
        }
    }
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class TravellingListFragment  extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public TravellingListFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static TravellingListFragment newInstance(int sectionNumber) {
            TravellingListFragment fragment = new TravellingListFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
        @Override
        public void onResume() {
            super.onResume();
            System.out.println("TravellingListFragment onResume");
        }

        @Override
        public void onPause() {
            super.onPause();
            System.out.println("TravellingListFragment onPause");
        }
    }

    PermissionListioner permissionListioner = new PermissionListioner() {
        @Override
        public void onSuccess() {

        }

        @Override
        public void onDailogSuccess() {

        }

        @Override
        public void onFail() {

        }
    };

    public void getAddressFromLocation(
            final Location location, final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> list = geocoder.getFromLocation(
                            location.getLatitude(), location.getLongitude(), 1);
                    if (list != null && list.size() > 0) {
                        Address address = list.get(0);
                        // sending back first address line and locality
                        result = address.getAddressLine(0) + ", " + address.getLocality();
                    }
                } catch (IOException e) {
                    Log.e("Main Act", "Impossible to connect to Geocoder", e);
                } finally {
                    Message msg = Message.obtain();
                    msg.setTarget(handler);
                    if (result != null) {
                        msg.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("address", result);
                        msg.setData(bundle);
                    } else
                        msg.what = 0;
                    msg.sendToTarget();
                }
            }
        };
        thread.start();
    }

    private void addNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("50 meters alert")
                        .setContentText("You travelled 50 meters");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}
