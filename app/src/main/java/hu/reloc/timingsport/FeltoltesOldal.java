package hu.reloc.timingsport;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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
import java.util.ArrayList;

public class FeltoltesOldal extends Activity {

    String ocim, olat, olon, ohead, ospeed, opid, obib, oido, otav, oalt;
    ArrayList arrayList;
    ListView listView;
    MyDatabase md;


    InputStream is = null;
    String result = null;
    String line = null;
    int code;

    // flag for Internet connection status
    Boolean isInternetPresent = false;
    // Connection detector class
    ConnectionDetector cd;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.feltoltes);
        cd = new ConnectionDetector(getApplicationContext());


        md = new MyDatabase(this);
        md.getWritableDatabase();


        ImageView iv_exit = (ImageView) findViewById(R.id.imageViewExit);
        iv_exit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                finish();

            }
        });

        ImageView iv_upload = (ImageView) findViewById(R.id.imageViewUpload);
        iv_upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {



                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {
                    // Internet Connection is Present
                    // make HTTP requests

                    adatbazis_ellenorzes();

                    finish();
                    adatbazis_ellenorzes();

                } else {
                    // Internet connection is not present
                    // Ask user to connect to Internet
                   showAlertDialog(FeltoltesOldal.this, getString(R.string.msg_nincs_internet),
                            getString(R.string.msg_nincs_internet_leiras), false);
                }

            }
        });

        listView = (ListView) findViewById(R.id.listView2);
        arrayList = md.getAllBoja2();

        SimpleAdapter adapter;
        adapter = new SimpleAdapter(FeltoltesOldal.this, arrayList,
                R.layout.activity_column_utvonal, new String[]{"ido",
                 "lat", "lon" }, new int[]{R.id.ido,
                R.id.lat, R.id.lon});

        listView.setAdapter(adapter);

    }

    //////////////

    public void sqlOffline() {
        SQLiteDatabase sd = md.getReadableDatabase();
        Cursor c = sd.rawQuery("select * from timer ORDER BY ido LIMIT 1", null);
        oido="";
        opid="";
        obib="";
        ocim = "";
        olat = "";
        olon = "";
        ohead =  "";
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

        Toast.makeText(getApplicationContext(), getString(R.string.msg_adatfeltoltesbefejezodott), Toast.LENGTH_LONG).show();

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

            HttpPost httppost = new HttpPost(getString(R.string.url) + "offline.php");
            //"http://kekszalag.reloc.hu/gps.php");
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
                Toast.makeText(getBaseContext(), "Inserted Successfully",
                        Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getBaseContext(), "Sorry, Try Again",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("Fail 3", e.toString());
        }
    }


    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        alertDialog.setIcon((status) ? R.drawable.abc_ic_commit_search_api_mtrl_alpha : R.drawable.no);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

}