package hu.reloc.timingsport;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@SuppressLint("NewApi")
public class VersenyOldal extends AppCompatActivity implements LocationListener,
        SensorEventListener {


    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in
    // Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in
    // Milliseconds

    TextView textViewLat, textViewLong, textViewAlt, textViewSpeed, rajtszam,
            textTavolsag, textBoja, textBojanev, textPid;
    LocationManager lm;

    MyDatabase md;


    // iránytu eleje
    // define the display assembly compass picture
    private ImageView image;
    // record the compass picture angle turned
    private float currentDegree = 0f;
    // device sensor manager
    private SensorManager mSensorManager;
    TextView tvHeading;
    // iránytu eleje vege

    // idozito eleje
    Timer timer;
    TimerTask timerTask;
    // we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();
    // internet eleje vege

    String ilat, ilon, ialt, ispeed, ihead, fok, icim, itav, bnev1, blat, blon, bkoz,
            slat, slon, rajtsz, vurl, timeStamp, pid, bib, auto, ocim, olat, olon, ohead,
            ospeed, opid, obib, oido, otav, oalt;

    int idozit;

    double tavbe, blat1, blon1, bklat, bklon, Lat, Lon, Lat2, Lon2, Lat3, Lon3, slat1, slon1, bojakozelito;

    // double bklat = 0.00001799;
    // double bklon = 0.00002631;

    float mtav;

    InputStream is = null;
    String result = null;
    String line = null;
    int code;


    // / internet vege
    static public final int REQUEST_LOCATION = 1;


    private SQLiteDatabase dataBase;

    // flag for Internet connection status
    Boolean isInternetPresent = false;
    // Connection detector class
    ConnectionDetector cd;

    private PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.verseny2);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
                "MyApp::MyWakelockTag");



        cd = new ConnectionDetector(getApplicationContext());
        md = new MyDatabase(this);

        rajtszam = (TextView) findViewById(R.id.rajtszam);
        textBojanev = (TextView) findViewById(R.id.bojanev);
        textPid = (TextView) findViewById(R.id.pid);


        tavbe = Double.parseDouble("30");

        sqliteUrl();
        sqlitePid();
        rajtszam.setText(bib);
        textPid.setText(pid);
        idozit = Integer.parseInt(auto);


        ImageView iv_exit = (ImageView) findViewById(R.id.imageViewExit);
        iv_exit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                System.exit(0);
                finish();

            }
        });


        ImageView iv_option = (ImageView) findViewById(R.id.imageViewLetoltes);
        iv_option.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent intent = new Intent(VersenyOldal.this, FeltoltesOldal.class);

                startActivity(intent);


            }
        });



        ImageView button = (ImageView) findViewById(R.id.imageViewSend);
        button.setOnClickListener(new View.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.GINGERBREAD)
            @SuppressLint("NewApi")
            public void onClick(View view) {


                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {
                    // Internet Connection is Present
                    // make HTTP requests

                    icim = "Verseny";
                    playBeep();

                    insert();

                    //Context context = getApplicationContext();
                    //CharSequence text = "Inserted Successfully!";
                    //int duration = Toast.LENGTH_SHORT;

                    adatbazis_ellenorzes();

                    //Toast toast = Toast.makeText(context, text, duration);
                    //toast.show();


                } else {
                    // Internet connection is not present
                    // Ask user to connect to Internet

                    icim = "Verseny";
                    playBeep();

                    insert();

                    // Context context = getApplicationContext();
                    //  CharSequence text = "Offline Inserted Successfully!";
                    // int duration = Toast.LENGTH_SHORT;

                    // Toast toast = Toast.makeText(context, text, duration);
                    // toast.show();

                }


            }
        });


        ImageView buttonBojalista = (ImageView) findViewById(R.id.imageViewResults);
        buttonBojalista.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent intent = new Intent(VersenyOldal.this, ResztavOldal.class);
                startActivity(intent);

            }
        });

        startTimer();


        // iránytu eleje
        // TextView that will tell the user what degree is he heading
        tvHeading = (TextView) findViewById(R.id.tvHeading);
        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // iránytu eleje vege


        textViewLat = (TextView) findViewById(R.id.latitude);
        textViewLong = (TextView) findViewById(R.id.longitude);
        textViewAlt = (TextView) findViewById(R.id.altitude);
        textViewSpeed = (TextView) findViewById(R.id.speed);
        textTavolsag = (TextView) findViewById(R.id.megtettut);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {


            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MINIMUM_TIME_BETWEEN_UPDATES,
                    MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, this);
        }


    }


    DecimalFormat precision = new DecimalFormat("0");
    DecimalFormat precisionGeo = new DecimalFormat("0.000000");

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        Lat = location.getLatitude();
        Lon = location.getLongitude();
        double Alt = location.getAltitude();
        double Spe = location.getSpeed();

        // timestamp
        timeStamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "";

        textViewLat.setText("" + Lat);
        textViewLong.setText("" + Lon);
        textViewAlt.setText("" + precision.format(Alt));
        textViewSpeed.setText("" + precision.format(Spe * 3.6));


        SQLiteDatabase sd = md.getReadableDatabase();
        Cursor c = sd.rawQuery(
                "select * from info where ido = ? ORDER BY id LIMIT 1",
                new String[]{"0"});
        if (c.getCount() > 0) {
            c.moveToFirst();
            bnev1 = c.getString(1);
            blat = c.getString(2);
            blon = c.getString(3);
            bkoz = c.getString(5);
            slat = c.getString(6);
            slon = c.getString(7);

        }

        c.close();
        sd.close();
        SQLiteDatabase.releaseMemory();

        blat1 = Double.parseDouble(blat);
        blon1 = Double.parseDouble(blon);
        bojakozelito = Double.parseDouble(bkoz);
        bklat = Double.parseDouble(slat);
        bklon = Double.parseDouble(slon);

        textBojanev.setText(bnev1);

        String bnev2 = bnev1;

        Lat2 = blat1;
        Lon2 = blon1;


        // boja kuldes I. érzékenység alapján:

        if (
                blat1 < Lat + (bojakozelito * bklat)
                        && blat1 > Lat - (bojakozelito * bklat)
                        && blon1 < Lon + (bojakozelito * bklon)
                        && blon1 > Lon - (bojakozelito * bklon)) {


            icim = bnev2;
            playBeep();
            insert();

            sqliteFrissit();
            textBojanev.setText(bnev2);

        }


        /// távolságmérő bója

        if (Lat3 == 0 && Lon3 == 0) {
            Lat3 = Lat;
            Lon3 = Lon;
        }

        Location loc1 = new Location("");
        loc1.setLatitude(Lat);
        loc1.setLongitude(Lon);

        Location loc2 = new Location("");
        loc2.setLatitude(Lat2);
        loc2.setLongitude(Lon2);

        float distanceInMeters1 = loc1.distanceTo(loc2);

        DecimalFormat precision1 = new DecimalFormat("0.000");
        textBoja = (TextView) findViewById(R.id.bojatav);
        textBoja.setText(precision1.format(distanceInMeters1 / 1000) + " Km");

        if (Lat3 < Lat + (tavbe * bklat) && Lat3 > Lat - (tavbe * bklat)
                && Lon3 < Lon + (tavbe * bklon) && Lon3 > Lon - (tavbe * bklon)) {

            //  Double nemkell = Lat3;

        } else {
            Location loc3 = new Location("");
            loc3.setLatitude(Lat3);
            loc3.setLongitude(Lon3);

            float distanceInMeters2 = loc1.distanceTo(loc3);
            DecimalFormat precision2 = new DecimalFormat("0.000");
            mtav = mtav + (distanceInMeters2 / 1000);
            String tav = String.valueOf(mtav); // ezt kikapcsolni
            // textTavolsag.setText(precision2.format(distanceInMeters2 / 1000)); // ezt bekapcsolni!
            textTavolsag.setText(tav); // ezt kikapcsolni
            Lat3 = Lat;
            Lon3 = Lon;

        }

        // / távolságmérő bója vége

    }


    // iránytu eleje

    @Override
    protected void onResume() {
        super.onResume();
        wakeLock.acquire();

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);

        // onResume we start our timer so it can start when the app comes from
        // the background
        //startTimer();

    }


    // idozito eleje

    public void startTimer() {
        // set a new Timer
        timer = new Timer();

        // initialize the TimerTask's job
        initializeTimerTask();

        // schedule the timer, after the first 5000ms the TimerTask will run
        // every 10000ms
        if (idozit > 0) {
            timer.schedule(timerTask, 2000, idozit * 1000);


        }

    }


    public void stoptimertask(View v) {
        // stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


// offline insert eleje

    public void offlineData() {
        if (!ilat.equals("0")) {

            SQLiteDatabase sd1 = md.getWritableDatabase();

            ContentValues values = new ContentValues();

            ContentValues contentValues = new ContentValues();
            contentValues.put("ido", timeStamp);
            contentValues.put("bib", bib);
            contentValues.put("pid", pid);
            contentValues.put("cim", icim);
            contentValues.put("lat", ilat);
            contentValues.put("lon", ilon);
            contentValues.put("head", ihead);
            contentValues.put("speed", ispeed);
            contentValues.put("megtett", itav);
            contentValues.put("alt", ialt);

            sd1.insert("timer", null, contentValues);

            sd1.close();
        }
    }


    // internet eleje

    public VersenyOldal() {
        super();
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    public void insert() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();

        rajtsz = rajtszam.getText().toString();
        ilat = textViewLat.getText().toString();
        ilon = textViewLong.getText().toString();
        ialt = textViewAlt.getText().toString();
        ispeed = textViewSpeed.getText().toString();
        ihead = tvHeading.getText().toString();
        itav = textTavolsag.getText().toString();

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("pid", pid));
        nameValuePairs.add(new BasicNameValuePair("bib", bib));
        nameValuePairs.add(new BasicNameValuePair("latitude", ilat));
        nameValuePairs.add(new BasicNameValuePair("longitude", ilon));
        nameValuePairs.add(new BasicNameValuePair("altitude", ialt));
        nameValuePairs.add(new BasicNameValuePair("speedKPH", ispeed));
        nameValuePairs.add(new BasicNameValuePair("heading", ihead));
        nameValuePairs.add(new BasicNameValuePair("address", icim));
        nameValuePairs.add(new BasicNameValuePair("megtettut", itav));

        StrictMode.setThreadPolicy(policy);

        try {
            HttpClient httpclient = new DefaultHttpClient();

            // HttpPost httppost = new HttpPost(getString(R.string.url) + "gps.php");
            HttpPost httppost = new HttpPost(vurl + "gps.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            Log.e("pass 1", "connection success ");

        } catch (Exception e) {
            Log.e("Fail 1", e.toString());

            offlineData();


            //Toast.makeText(getApplicationContext(), "Invalid IP Address",
            //        Toast.LENGTH_LONG).show();

        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");

            }
            is.close();
            result = sb.toString();
            Log.e("pass 2", "connection success ");
            //deleteContact();

        } catch (Exception e) {
            Log.e("Fail 2", e.toString());

        }

        try {
            JSONObject json_data = new JSONObject(result);
            code = (json_data.getInt("code"));

            if (code == 1) {


                //   Toast.makeText(getBaseContext(), "Inserted Successfully",
                //          Toast.LENGTH_SHORT).show();


            } else {
                Toast.makeText(getBaseContext(), "Sorry, Try Again",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("Fail 3", e.toString());
        }
    }

    // internet vege


    // idozito altal kuldott parancs

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                // use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {

                    @SuppressLint("SimpleDateFormat")
                    public void run() {

                        isInternetPresent = cd.isConnectingToInternet();

                        if (isInternetPresent) {
                            // Internet Connection is Present
                            // make HTTP requests

                            icim = "Verseny";
                            // kuldes nemzok
                            insert();


                            adatbazis_ellenorzes();

                        } else {
                            // Internet connection is not present
                            // Ask user to connect to Internet

                            icim = "Verseny";
                            insert();
                        }


                    }
                });
            }
        };

    }


    public void sqliteFrissit() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "yyyy.MMMM.dd HH:mm:ss a");
        final String strDate = simpleDateFormat.format(calendar.getTime());

        SQLiteDatabase sd2 = md.getReadableDatabase();
        ContentValues args = new ContentValues();
        args.put(md.COL_INFO_IDO, strDate);

        sd2.update(md.TABLE_NAME_INFO, args, md.COL_INFO_NAME + "= ?",
                new String[]{bnev1});
        sd2.close();

    }


    @Override
    protected void onPause() {
        super.onPause();
        wakeLock.acquire();

        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);

    }



    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSensorChanged(SensorEvent arg0) {
        // TODO Auto-generated method stub

        // get the angle around the z-axis rotated
        float degree = Math.round(arg0.values[0]);

        tvHeading.setText("" + Float.toString(degree));
        fok = Float.toString(degree);

        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(currentDegree, -degree,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(210);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        //    image.startAnimation(ra);
        //    currentDegree = -degree;

    }

    /// hang
    public void playBeep() {
        MediaPlayer m = new MediaPlayer();
        try {
            if (m.isPlaying()) {
                m.stop();
                m.release();
                m = new MediaPlayer();
            }

            AssetFileDescriptor descriptor = getAssets().openFd("beep.mp3");
            m.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            m.prepare();
            m.setVolume(1f, 1f);
            m.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sqliteUrl() {

        dataBase = md.getWritableDatabase();
        Cursor c = dataBase.rawQuery("select * from verseny", null);
        vurl = "";


        if (c.getCount() > 0) {
            c.moveToFirst();
            vurl = c.getString(5);


        }

        c.close();
    }


    public void sqlitePid() {

        dataBase = md.getWritableDatabase();
        Cursor c = dataBase.rawQuery("select * from login", null);
        pid = "";
        bib = "";
        auto = "";


        if (c.getCount() > 0) {
            c.moveToFirst();
            pid = c.getString(4);
            bib = c.getString(5);
            auto = c.getString(8);

        }

        c.close();
    }

    //// offline
    public void sqlOffline() {
        SQLiteDatabase sd = md.getReadableDatabase();
        Cursor c = sd.rawQuery("select * from timer ORDER BY ido LIMIT 1", null);
        oido = "";
        opid = "";
        obib = "";
        ocim = "";
        olat = "";
        olon = "";
        ohead = "";
        ospeed = "";
        otav = "";
        oalt = "";


        if (c.getCount() > 0) {
            c.moveToFirst();
            oido = c.getString(0);
            opid = c.getString(1);
            obib = c.getString(2);
            ocim = c.getString(3);
            olat = c.getString(4);
            olon = c.getString(5);
            ohead = c.getString(6);
            ospeed = c.getString(7);
            otav = c.getString(8);
            oalt = c.getString(9);
        }

        c.close();
        sd.close();
        SQLiteDatabase.releaseMemory();
    }


    ///ellenörzés
    public void adatbazis_ellenorzes() {
        SQLiteDatabase db = md.getWritableDatabase();
        String count = "SELECT count(*) FROM timer";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if (icount > 0) {
            //jó
            icount--;
            sqlOffline();
            insertOffline();
            adatbazis_ellenorzes();


        }

        // Toast.makeText(getApplicationContext(), getString(R.string.msg_adatfeltoltesbefejezodott), Toast.LENGTH_LONG).show();

    }


    public void deleteContact() {
        SQLiteDatabase db = md.getWritableDatabase();
        db.execSQL("DELETE FROM timer WHERE lat=" + olat);
        db.close();
    }


    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    public void insertOffline() {


        // /
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        // /


        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("pid", opid));
        nameValuePairs.add(new BasicNameValuePair("bib", obib));
        nameValuePairs.add(new BasicNameValuePair("latitude", olat));
        nameValuePairs.add(new BasicNameValuePair("longitude", olon));
        nameValuePairs.add(new BasicNameValuePair("altitude", oalt));
        nameValuePairs.add(new BasicNameValuePair("speedKPH", ospeed));
        nameValuePairs.add(new BasicNameValuePair("heading", ohead));
        nameValuePairs.add(new BasicNameValuePair("address", ocim));
        nameValuePairs.add(new BasicNameValuePair("megtettut", otav));
        nameValuePairs.add(new BasicNameValuePair("ido", oido));
        // /
        StrictMode.setThreadPolicy(policy);
        // /

        try {
            HttpClient httpclient = new DefaultHttpClient();

            HttpPost httppost = new HttpPost(vurl + "gps_offline.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            Log.e("pass 1", "connection success ");

        } catch (Exception e) {
            Log.e("Fail 1", e.toString());
            Toast.makeText(getApplicationContext(), "Invalid IP Address",
                    Toast.LENGTH_LONG).show();

        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");

            }
            is.close();
            result = sb.toString();
            deleteContact();
            Log.e("pass 2", "connection success ");
        } catch (Exception e) {

            Log.e("Fail 2", e.toString());
        }

        try {
            JSONObject json_data = new JSONObject(result);
            code = (json_data.getInt("code"));

            if (code == 1) {
                //  Toast.makeText(getBaseContext(), "Inserted Successfully",
                //          Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getBaseContext(), "Sorry, Try Again",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("Fail 3", e.toString());
        }
    }

}