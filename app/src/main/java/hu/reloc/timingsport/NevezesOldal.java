package hu.reloc.timingsport;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NevezesOldal extends Activity implements OnItemSelectedListener {

    String verseny, text, vid, vurl, user, pid, firstname, lastname, yob, gender;
    Bundle extrak;
    MyDatabase md;
    private SQLiteDatabase dataBase;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nevezes);

        md = new MyDatabase(this);

        extrak = getIntent().getExtras();


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            // Permission already Granted
            // Do your work here
            // Perform operations here only which requires permission
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }


        TextView imeit = (TextView) findViewById(R.id.textImei);

        ImageView iv_exit = (ImageView) findViewById(R.id.imageViewExit);
        iv_exit.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {

                finish();

            }
        });

        // Permission StrictMode
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // spinner1_orszag
        final Spinner spin = (Spinner) findViewById(R.id.spinnerVerseny);


        Button buttonBojalista = (Button) findViewById(R.id.aktivalas);
        buttonBojalista.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {

                Map<String, String> selectedMap = (HashMap<String, String>) spin.getSelectedItem();
                verseny = selectedMap.get("nev");

                Map<String, String> selectedMap2 = (HashMap<String, String>) spin.getSelectedItem();
                vid = selectedMap2.get("id");

                Map<String, String> selectedMap3 = (HashMap<String, String>) spin.getSelectedItem();
                vurl = selectedMap3.get("url");


                Intent intent = new Intent(NevezesOldal.this, TavOldal.class);
                intent.putExtra("verseny", verseny);
                intent.putExtra("vid", vid);
                intent.putExtra("vurl", vurl);

                startActivity(intent);
                finish();

            }
        });



        postData();

        try {


            JSONArray data = new JSONArray(text);

            final ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> map;
            for (int i = 0; i < data.length(); i++) {
                JSONObject c = data.getJSONObject(i);
                map = new HashMap<String, String>();
                map.put("id", c.getString("id"));
                map.put("nev", c.getString("nev"));
                map.put("url", c.getString("url"));
                MyArrList.add(map);
            }

            SimpleAdapter sAdap;
            sAdap = new SimpleAdapter(NevezesOldal.this, MyArrList, R.layout.spinner_nevezes, new String[]{"nev"},
                    new int[]{R.id.ColCustomerID,});
            spin.setAdapter(sAdap);


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

        public void postData() {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getString(R.string.url) + "verseny.php");

            try {


                // Add your data
                List nameValuePairs = new ArrayList(1);
                // nameValuePairs.add(new BasicNameValuePair("orszag", orszag));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                InputStream is = response.getEntity().getContent();
                BufferedInputStream bis = new BufferedInputStream(is);
                ByteArrayBuffer baf = new ByteArrayBuffer(20);

                int current = 0;

                while ((current = bis.read()) != -1) {
                    baf.append((byte) current);

                }

                /* Convert the Bytes read to a String. */
                text = new String(baf.toByteArray());

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
        }


        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position,
        long id) {
            // TODO Auto-generated method stub

        }

    }


