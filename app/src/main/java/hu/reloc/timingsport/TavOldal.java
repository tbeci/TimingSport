package hu.reloc.timingsport;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TavOldal extends Activity {

    String verseny, text, leiras, price, vid, firstname, lastname, yob, gender, did, vurl;
    Bundle extrak;
    InputStream is = null;
    String result = null;
    String line = null;
    int code;
    Spinner spin;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tav);

        extrak = getIntent().getExtras();
        verseny = extrak.getString("verseny");
        vid = extrak.getString("vid");
        vurl = extrak.getString("vurl");

        firstname = extrak.getString("firstname");
        lastname = extrak.getString("lastname");
        yob = extrak.getString("yob");
        gender = extrak.getString("gender");

        ImageView iv_exit = (ImageView) findViewById(R.id.imageViewExit);
        iv_exit.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {

                finish();


            }
        });


        TextView versenytext = (TextView) findViewById(R.id.textImei);
        versenytext.setText(verseny);


        Button buttonBojalista = (Button) findViewById(R.id.aktivalas);
        buttonBojalista.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {

                Map<String, String> selectedMap = (HashMap<String, String>) spin.getSelectedItem();
                leiras = selectedMap.get("leiras");

                Map<String, String> selectedMap2 = (HashMap<String, String>) spin.getSelectedItem();
                price = selectedMap2.get("price");

                Map<String, String> selectedMap3 = (HashMap<String, String>) spin.getSelectedItem();
                did = selectedMap3.get("id");


                Intent intent = new Intent(TavOldal.this, AdatokOldal.class);
                intent.putExtra("verseny", verseny);
                intent.putExtra("vid", vid);
                intent.putExtra("did", did);
                intent.putExtra("leiras", leiras);
                intent.putExtra("price", price);
                intent.putExtra("vurl", vurl);


                startActivity(intent);
                finish();
            }
        });


        postData();
        adatletoltes();
    }


    ///
    public void postData() {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(getString(R.string.url) + "tav.php");

        try {
            // Add your data
            List nameValuePairs = new ArrayList(1);
            nameValuePairs.add(new BasicNameValuePair("id", vid));
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
            //txtvw.setText(text);

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }


    }


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    public void adatletoltes() {

        // Permission StrictMode
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();

            StrictMode.setThreadPolicy(policy);
        }


        spin = (Spinner) findViewById(R.id.spinnerTav);


        try {

            JSONArray data = new JSONArray(text);

            final ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> map;


            for (int i = 0; i < data.length(); i++) {
                JSONObject c = data.getJSONObject(i);

                map = new HashMap<String, String>();

                map.put("leiras", c.getString("leiras"));
                map.put("price", c.getString("price"));
                map.put("id", c.getString("id"));

                MyArrList.add(map);

            }

            SimpleAdapter sAdapS;
            sAdapS = new SimpleAdapter(TavOldal.this, MyArrList, R.layout.spinner_nevezes, new String[]{"leiras"},
                    new int[]{R.id.ColCustomerID,});
            spin.setAdapter(sAdapS);


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public String getJSONUrl(String url) {
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);

        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) { // Download OK
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
            } else {
                Log.e("Log", "Failed to download result..");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }


}





