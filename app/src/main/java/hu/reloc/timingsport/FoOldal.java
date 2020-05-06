package hu.reloc.timingsport;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FoOldal extends Activity {

    String engedely, gender, vn, kn, yob, verseny, tav, bib, auto, imei;
    MyDatabase md;
    private SQLiteDatabase dataBase;
    // flag for Internet connection status
    Boolean isInternetPresent = false;
    // Connection detector class
    ConnectionDetector cd;

    /// engedely 5.1>
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 999;
    private TelephonyManager mTelephonyManager;


    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fooldal);
        cd = new ConnectionDetector(getApplicationContext());

        md = new MyDatabase(this);



     int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP) {
            // Do something for lollipop and above versions

            /// engedely 5.1>
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                        PERMISSIONS_REQUEST_READ_PHONE_STATE);
            } else {
                getDeviceImei();
            }


        } else {
            // do something for phones running an SDK before lollipop
            TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String deviceid = manager.getDeviceId();
            imei = deviceid;


        }





        TextView regisztracio = (TextView) findViewById(R.id.textRegiszter);
        regisztracio.setText(Html.fromHtml(getString(R.string.foo_regisztracio)));

        TextView nevezes = (TextView) findViewById(R.id.textNevezes);
        nevezes.setText(Html.fromHtml(getString(R.string.foo_nevezes)));

        TextView verseny = (TextView) findViewById(R.id.textVerseny);
        verseny.setText(Html.fromHtml(getString(R.string.foo_start)));


        ImageView iv_exit = (ImageView) findViewById(R.id.imageViewExit);
        iv_exit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                System.exit(0);


            }
        });


        ImageView buttonRegisztral = (ImageView) findViewById(R.id.btregiszter);
        buttonRegisztral.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                adatmodositas();

                if (engedely.equalsIgnoreCase("0")) {
                    Intent intent = new Intent(FoOldal.this, RegisterOldal.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(FoOldal.this, RegisterNezet.class);
                    startActivity(intent);
                }


            }
        });


        ImageView buttonNevezes = (ImageView) findViewById(R.id.btnevezes);
        buttonNevezes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {


                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {
                    // Internet Connection is Present
                    // make HTTP requests
                    Intent intent = new Intent(FoOldal.this, NevezesOldal.class);
                    startActivity(intent);

                } else {
                    // Internet connection is not present
                    // Ask user to connect to Internet
                    showAlertDialog(FoOldal.this, getString(R.string.msg_nincs_internet),
                            getString(R.string.msg_nincs_internet_leiras), false);
                }


            }
        });

        ImageView buttonVerseny = (ImageView) findViewById(R.id.btverseny);
        buttonVerseny.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {


                sqlite_ellenorzes();


            }
        });


        ImageView buttonFrissites = (ImageView) findViewById(R.id.btfrissites);
        buttonFrissites.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {
                    // Internet Connection is Present
                    // make HTTP requests
                    dialogBox();

                } else {
                    // Internet connection is not present
                    // Ask user to connect to Internet
                    showAlertDialog(FoOldal.this, getString(R.string.msg_nincs_internet),
                            getString(R.string.msg_nincs_internet_leiras), false);
                }


            }
        });


        ImageView buttonResztav = (ImageView) findViewById(R.id.btresztav);
        buttonResztav.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {


                Intent intent = new Intent(FoOldal.this, ResztavOldal.class);

                startActivity(intent);


            }
        });


        ImageView buttonEredmeny = (ImageView) findViewById(R.id.bteredmeny);
        buttonEredmeny.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {


                Intent intent = new Intent(FoOldal.this, EredmenyOldal.class);
                intent.putExtra("pid", imei);

                startActivity(intent);


            }
        });



        Button buttonWeb = (Button) findViewById(R.id.textWeb);
        buttonWeb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                String url ="https://futoverseny.reloc.hu/";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);

            }
        });
    }


    public void dialogBox() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getString(R.string.msg_frissites));
        alertDialogBuilder.setMessage(Html.fromHtml(getString(R.string.msg_frissites_leiras)));

        final EditText input = new EditText(FoOldal.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialogBuilder.setView(input);

        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        String password = input.getText().toString();


                        if (password.equalsIgnoreCase("123")) {

                            nevezesLetoltes();
                            versenyLetoltes();
                            userLogin();
                            userEntry();
                            bibInsert();

                            // résztáv adatok
                            torles();
                            resztavLetoltes();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Wrong Password! password: 123", Toast.LENGTH_SHORT).show();
                        }




                    /*
                    nevezesLetoltes();
                        versenyLetoltes();
                        userLogin();
                        userEntry();
                        bibInsert();

                        // résztáv adatok
                        torles();
                        resztavLetoltes();
                    */

                    }
                });

        alertDialogBuilder.setNegativeButton("MÉGSE",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public void adatmodositas() {
        dataBase = md.getWritableDatabase();
        Cursor c = dataBase.rawQuery("select * from login", null);
        engedely = "";


        if (c.getCount() > 0) {
            c.moveToFirst();
            engedely = c.getString(7);

        }

        c.close();
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

    public void sqlite_ellenorzes() {
        SQLiteDatabase db = md.getWritableDatabase();
        String count = "SELECT count(*) FROM info";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if (icount > 0) {
            //leave

            Intent intent = new Intent(FoOldal.this, VersenyOldal.class);

            startActivity(intent);
        } else {
            //populate table
            Toast.makeText(getApplicationContext(), getString(R.string.msg_idomerohiba), Toast.LENGTH_LONG).show();
        }

    }

    /////////// frissitesek

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    public void nevezesLetoltes() {
        // Permission StrictMode
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        String url = getString(R.string.url) + "user.php";

        try {

            JSONArray data = new JSONArray(getJSONUrl(url));

            final ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> map;

            for (int i = 0; i < data.length(); i++) {
                JSONObject c = data.getJSONObject(i);

                map = new HashMap<String, String>();
                map.put("pid", c.getString("pid"));
                map.put("firstname", c.getString("firstname"));
                map.put("lastname", c.getString("lastname"));
                map.put("yob", c.getString("yob"));
                map.put("gender", c.getString("gender"));
                map.put("status", c.getString("status"));
                map.put("automata", c.getString("automata"));
                MyArrList.add(map);


                ///sqlite

                SQLiteDatabase database = md.getWritableDatabase();
                ContentValues values = new ContentValues();

                int id = 1;

                ContentValues contentValues = new ContentValues();
                // values.put(md.COL_LOGIN_BIB, c.getString("bib"));
                contentValues.put("pid", c.getString("pid"));
                contentValues.put("vname", c.getString("firstname"));
                contentValues.put("kname", c.getString("lastname"));
                contentValues.put("yob", c.getString("yob"));
                contentValues.put("gender", c.getString("gender"));
                contentValues.put("status", c.getString("status"));
                contentValues.put("auto", c.getString("automata"));
                database.update("login", contentValues, "id = ? ", new String[]{Integer.toString(id)});
                database.close();

            }


            final AlertDialog.Builder viewDetail = new AlertDialog.Builder(this);

            // OnClick Item


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }








    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    public void versenyLetoltes() {
        // Permission StrictMode
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        String url = getString(R.string.url) + "entry.php";

        try {

            JSONArray data = new JSONArray(getJSONUrl(url));

            final ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> map;

            for (int i = 0; i < data.length(); i++) {
                JSONObject c = data.getJSONObject(i);

                map = new HashMap<String, String>();
                map.put("bib", c.getString("bib"));
                map.put("nev", c.getString("nev"));
                map.put("leiras", c.getString("leiras"));
                map.put("mikor", c.getString("mikor"));
                map.put("url", c.getString("url"));

                MyArrList.add(map);


                ///sqlite

                SQLiteDatabase database = md.getWritableDatabase();
                ContentValues values = new ContentValues();

                int id = 1;

                ContentValues contentValues = new ContentValues();
                // values.put(md.COL_LOGIN_BIB, c.getString("bib"));
                contentValues.put("bib", c.getString("bib"));
                contentValues.put("nev", c.getString("nev"));
                contentValues.put("tav", c.getString("leiras"));
                contentValues.put("dat", c.getString("mikor"));
                contentValues.put("url", c.getString("url"));
                database.update("verseny", contentValues, "id = ? ", new String[]{Integer.toString(id)});
                database.close();

            }


            final AlertDialog.Builder viewDetail = new AlertDialog.Builder(this);

            // OnClick Item


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    /*** Get JSON Code from URL ***/
    public String getJSONUrl(String url) {
        StringBuilder str = new StringBuilder();
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);

        try {

            List nameValuePairs = new ArrayList(1);
            nameValuePairs.add(new BasicNameValuePair("pid", imei));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpclient.execute(httppost);

            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) { // Download OK
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
            } else {
                Log.e("Log", "Failed to download file..");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }


    public void userLogin() {
        dataBase = md.getWritableDatabase();
        Cursor c = dataBase.rawQuery("select * from login", null);
        vn = "";
        kn = "";
        yob = "";
        gender = "";
        auto = "";

        if (c.getCount() > 0) {
            c.moveToFirst();
            vn = c.getString(1);
            kn = c.getString(2);
            yob = c.getString(3);
            gender = c.getString(6);
            auto = c.getString(8);

        }


        c.close();
    }

    public void userEntry() {
        dataBase = md.getWritableDatabase();
        Cursor c = dataBase.rawQuery("select * from verseny", null);
        verseny = "";

        if (c.getCount() > 0) {
            c.moveToFirst();
            bib = c.getString(1);
            verseny = c.getString(2);
            tav = c.getString(3);

        }

        c.close();
    }

    public void bibInsert() {
        SQLiteDatabase database = md.getWritableDatabase();
        ContentValues values = new ContentValues();

        int id = 1;

        ContentValues contentValues = new ContentValues();
        // values.put(md.COL_LOGIN_BIB, c.getString("bib"));
        contentValues.put("bib", bib);

        database.update("login", contentValues, "id = ? ", new String[]{Integer.toString(id)});
        database.close();
        Toast.makeText(getApplicationContext(), getString(R.string.msg_adatokletoltese), Toast.LENGTH_LONG).show();
    }


    /// resztáv letöltése
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    public void resztavLetoltes() {
        // Permission StrictMode
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        String url = getString(R.string.url) + "split.php";

        try {

            JSONArray data = new JSONArray(getJSONUrl(url));

            final ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> map;

            for (int i = 0; i < data.length(); i++) {
                JSONObject c = data.getJSONObject(i);

                map = new HashMap<String, String>();
                map.put("Name", c.getString("Name"));
                map.put("Lat", c.getString("Lat"));
                map.put("Lon", c.getString("Lon"));
                map.put("Ido", c.getString("Ido"));
                map.put("Erzek", c.getString("Erzek"));
                map.put("sLat", c.getString("sLat"));
                map.put("sLon", c.getString("sLon"));
                map.put("did", c.getString("did"));
                MyArrList.add(map);

                SQLiteDatabase database = md.getWritableDatabase();
                ContentValues values = new ContentValues();

                values.put(md.COL_INFO_ID, c.getString("MemberID"));
                values.put(md.COL_INFO_NAME, c.getString("Name"));
                values.put(md.COL_INFO_LAT, c.getString("Lat"));
                values.put(md.COL_INFO_LON, c.getString("Lon"));
                values.put(md.COL_INFO_IDO, c.getString("Ido"));
                values.put(md.COL_INFO_ERZEK, c.getString("Erzek"));
                values.put(md.COL_INFO_SLAT, c.getString("sLat"));
                values.put(md.COL_INFO_SLON, c.getString("sLon"));
                values.put(md.COL_INFO_DIS, c.getString("did"));
                database.insert(md.TABLE_NAME_INFO, null, values);
                database.close();

            }


            final AlertDialog.Builder viewDetail = new AlertDialog.Builder(this);

            // OnClick Item


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }



    public void torles() {
        SQLiteDatabase database = md.getWritableDatabase();
        ContentValues values = new ContentValues();
        database.delete(md.TABLE_NAME_INFO, null, null);
    }

    /// engedely 5.1>
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getDeviceImei();
        }
    }

    @SuppressLint("MissingPermission")
    private void getDeviceImei() {

        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imei = mTelephonyManager.getDeviceId();

    }


}
