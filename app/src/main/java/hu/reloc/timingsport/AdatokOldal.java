package hu.reloc.timingsport;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdatokOldal extends Activity implements OnClickListener {

    String leiras, verseny, text, price, firstname, lastname, yob, gender, vid, pid, did, status, vurl;
    Bundle extrak;
    InputStream is = null;
    String result = null;
    String line = null;
    int code;

    //// paypal 1
    private Button btnPay;
    // set the environment for production/sandbox/no netowrk
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION;

    private static final String CONFIG_CLIENT_ID = "AXNqkc9KRRYIhuV7NlhuXL7gEnZ2ChXG3NIJklCpvMXi1lNpgW9ikkA1pF1dGj5ilmjjBfQcmGP0Kwnx";

    private static final int REQUEST_PAYPAL_PAYMENT = 1;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("Android Hub 4 You")
            .merchantPrivacyPolicyUri(
                    Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(
                    Uri.parse("https://www.example.com/legal"));

    /// vege

    MyDatabase md;
    private SQLiteDatabase dataBase;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adatok);

        md = new MyDatabase(this);

        extrak = getIntent().getExtras();
        verseny = extrak.getString("verseny");
        leiras = extrak.getString("leiras");
        price = extrak.getString("price");
        vid = extrak.getString("vid");
        did = extrak.getString("did");
        vurl = extrak.getString("vurl");

      /*  firstname = extrak.getString("firstname");
        lastname = extrak.getString("lastname");
        yob = extrak.getString("yob");
        gender = extrak.getString("gender");*/

        ImageView iv_exit = (ImageView) findViewById(R.id.imageViewExit);
        iv_exit.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {

                finish();


            }
        });


        Button bt_elonevezes = (Button) findViewById(R.id.Eloleg);
        bt_elonevezes.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {

                //nevezeshez
                status="2";
                sqlitePid();
                entry();

                rajtszamLetoltes();

                // verseny adatok letoltese
                torles();
                versenyLetoltes();
                entryLetoltes();

                /// vege

                finish();


            }
        });


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        /// paypal 2
        btnPay = (Button) findViewById(R.id.Paypal);
        btnPay.setOnClickListener(this);
        ///

        sqlitePid();


        TextView versenyt = (TextView) findViewById(R.id.textVerseny);
        versenyt.setText(Html.fromHtml(
                "<b><font color=\"#0B2161\">Nevezési adatok:</font></b><p>" +
                "<b>Pid: </b>" + pid + "<br>" +
                "<b>Name: </b>" + firstname + " " + lastname + "<br>"+
                "<b>YOB: </b>" +yob + "<br>"+
                "<b>Gender: </b>"+ gender + "<p>" +
                "<b><font color=\"#0B2161\">Verseny adatok:</font></b><p>" +
                "<b>Race: </b>" + verseny + "<br>" +
                "<b>Distamce: </b>" + leiras  + "<br>" +
                "<b>Price: </b>" + price + " Ft"));



        /////////

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        TextView leiras = (TextView) findViewById(R.id.textLeirasPaypal);
        leiras.setText(Html.fromHtml(getString(R.string.adat_leiras)));

    }



    /// paypal 3
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        //payFizetes = fizetes.getText().toString();
        //update();


        switch (v.getId()) {
            case R.id.Paypal:
                PayPalPayment thingToBuy = new PayPalPayment(new BigDecimal(
                        price), "HUF", verseny, PayPalPayment.PAYMENT_INTENT_SALE);

                Intent intent = new Intent(AdatokOldal.this, PaymentActivity.class);



                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

                startActivityForResult(intent, REQUEST_PAYPAL_PAYMENT);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PAYPAL_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data
                        .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                //nevezeshez
                status="1";
                sqlitePid();
                entry();

                rajtszamLetoltes();

                // verseny adatok letoltese
                torles();
                versenyLetoltes();
                entryLetoltes();

                /// vege

                if (confirm != null) {
                    try {
                        System.out.println("Responseeee" + confirm);
                        Log.i("paymentExample", confirm.toJSONObject()
                                .toString());

                        JSONObject jsonObj = new JSONObject(confirm
                                .toJSONObject().toString());

                        String paymentId = jsonObj.getJSONObject("response")
                                .getString("id");

                        System.out.println("payment id:-==" + paymentId);
                        Toast.makeText(getApplicationContext(), paymentId,
                                Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        Log.e("paymentExample",
                                "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample",
                        "An invalid Payment was submitted. Please see the docs.");
            }
        }
    }


    public void entry() {
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("pid", pid));
        nameValuePairs.add(new BasicNameValuePair("vid", vid));
        nameValuePairs.add(new BasicNameValuePair("did", did));
        nameValuePairs.add(new BasicNameValuePair("status", status));


        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getString(R.string.url) + "entry_insert.php");
            // httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            Log.e("pass 1", "connection success ");
        } catch (Exception e) {
            Log.e("Fail 1", e.toString());
            Toast.makeText(getApplicationContext(), "Invalid IP Address", Toast.LENGTH_LONG).show();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
            Log.e("pass 2", "connection success ");
        } catch (Exception e) {
            Log.e("Fail 2", e.toString());
        }

        try {
            JSONObject json_data = new JSONObject(result);
            code = (json_data.getInt("code"));

            if (code == 1) {
                Toast.makeText(getBaseContext(), "Update Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getBaseContext(), "Sorry, Try Again", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("Fail 3", e.toString());
        }

    }

    public void sqlitePid() {
        dataBase = md.getWritableDatabase();
        Cursor c = dataBase.rawQuery("select * from login", null);
        pid = "";


        if (c.getCount() > 0) {
            c.moveToFirst();
           pid  = c.getString(4);
           firstname = c.getString(1);
           lastname  = c.getString(2);
           yob  = c.getString(3);
           gender  = c.getString(6);


        }

        c.close();
    }


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    public void entryLetoltes() {
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


    /// vissza bib
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    public void rajtszamLetoltes() {
        // Permission StrictMode
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }



        String url = getString(R.string.url) + "bib.php";

        try {

            JSONArray data = new JSONArray(getJSONUrl(url));

            final ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> map;

            for (int i = 0; i < data.length(); i++) {
                JSONObject c = data.getJSONObject(i);

                map = new HashMap<String, String>();
                map.put("bib", c.getString("bib"));
                MyArrList.add(map);


                ///sqlite

                SQLiteDatabase database = md.getWritableDatabase();
                ContentValues values = new ContentValues();

                int id = 1;

                ContentValues contentValues = new ContentValues();
               // values.put(md.COL_LOGIN_BIB, c.getString("bib"));
                contentValues.put("bib", c.getString("bib"));
                contentValues.put("status", "1");
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



    /*** Get JSON Code from URL ***/
    public String getJSONUrl(String url) {
        StringBuilder str = new StringBuilder();
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);

        try {

            List nameValuePairs = new ArrayList(1);
            nameValuePairs.add(new BasicNameValuePair("pid", pid));
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


    ////
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    public void versenyLetoltes() {
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
                map.put("MemberID", c.getString("MemberID"));
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


}


