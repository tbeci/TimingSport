package hu.reloc.timingsport;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
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


public class RegisterOldal extends Activity implements OnItemSelectedListener {

    String imei, gender, vn, kn, yob;
    InputStream is = null;
    String result = null;
    String line = null;
    int code;

    private EditText editTextVn, editTextKn, editTextYob;

    MyDatabase md;
    private SQLiteDatabase dataBase;

    /// engedely 5.1>
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 999;
    private TelephonyManager mTelephonyManager;


    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint({"WrongViewCast", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        md = new MyDatabase(this);

        // Permission StrictMode
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        /// csak 5 verzio esetén
        /*


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            } else {


                // do something for phones running an SDK before lollipop
                TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String deviceid = manager.getDeviceId();
                imei = deviceid;


            }

        */



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




        ImageView iv_exit = (ImageView) findViewById(R.id.imageViewExit);
        iv_exit.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {

                finish();


            }
        });


        TextView imeit = (TextView) findViewById(R.id.textImei);
        imeit.setText("PID: " + imei);

        editTextVn = (EditText) findViewById(R.id.editTextVn);

        editTextKn = (EditText) findViewById(R.id.editTextKn);

        editTextYob = (EditText) findViewById(R.id.editTextYob);


        Button buttonBojalista = (Button) findViewById(R.id.aktivalas);
        buttonBojalista.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {

                // Intent intent = new Intent(RegisterOldal.this, NevezesOldal.class);

                vn = editTextVn.getText().toString();
                kn = editTextKn.getText().toString();
                yob = editTextYob.getText().toString();

                if (!vn.isEmpty()
                        && !kn.isEmpty()
                        && !yob.isEmpty()


                        ) {

                    //update sqlite
                    SQLiteDatabase database = md.getWritableDatabase();
                    ContentValues values = new ContentValues();

                    int id = 1;

                    ContentValues contentValues = new ContentValues();
                    contentValues.put("vname", vn);
                    contentValues.put("kname", kn);
                    contentValues.put("yob", yob);
                    contentValues.put("pid", imei);
                    contentValues.put("gender", gender);
                    database.update("login", contentValues, "id = ? ", new String[]{Integer.toString(id)});

                    database.close();

                    insert();
                    update();

                    rajtszamLetoltes();
                    finish();


                } else {
                    Toast.makeText(getApplicationContext(),
                            (getString(R.string.msg_mindenmezo)), Toast.LENGTH_LONG).show();
                }


            }
        });

        userLogin();


        if (gender.equalsIgnoreCase("FÉRFI")) {
            spinerGenderF();
        } else if (gender.equalsIgnoreCase("NŐ")) {
            spinerGenderN();
        } else {
            spinerGenderF();
        }


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        // String item = parent.getItemAtPosition(position).toString();
        gender = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        // Toast.makeText(parent.getContext(), "Selected: " + hitel,
        // Toast.LENGTH_LONG).show();
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }


    public void update() {
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("pid", imei));
        nameValuePairs.add(new BasicNameValuePair("firstname", vn));
        nameValuePairs.add(new BasicNameValuePair("lastname", kn));
        nameValuePairs.add(new BasicNameValuePair("yob", yob));
        nameValuePairs.add(new BasicNameValuePair("gender", gender));

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getString(R.string.url) + "regupdate.php");
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


    public void insert() {
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("pid", imei));
        nameValuePairs.add(new BasicNameValuePair("firstname", vn));
        nameValuePairs.add(new BasicNameValuePair("lastname", kn));
        nameValuePairs.add(new BasicNameValuePair("yob", yob));
        nameValuePairs.add(new BasicNameValuePair("gender", gender));

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getString(R.string.url) + "register.php");
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


    public void userLogin() {
        dataBase = md.getWritableDatabase();
        Cursor c = dataBase.rawQuery("select * from login", null);
        vn = "";
        kn = "";
        yob = "";
        gender = "";

        if (c.getCount() > 0) {
            c.moveToFirst();
            vn = c.getString(1);
            kn = c.getString(2);
            yob = c.getString(3);
            gender = c.getString(6);

        }

        editTextVn.setText(vn);
        editTextKn.setText(kn);
        editTextYob.setText(yob);
        c.close();
    }


    public void spinerGenderF() {
        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.spinnerGender);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("FÉRFI");
        categories.add("NŐ");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }

    public void spinerGenderN() {
        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.spinnerGender);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("NŐ");
        categories.add("FÉRFI");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }


    ////
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    public void rajtszamLetoltes() {
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
                contentValues.put("vname", c.getString("firstname"));
                contentValues.put("kname", c.getString("lastname"));
                contentValues.put("yob", c.getString("yob"));
                contentValues.put("gender", c.getString("gender"));
                contentValues.put("status", c.getString("status"));
                contentValues.put("auto", c.getString("automata"));
                database.update("login", contentValues, "id = ? ", new String[]{Integer.toString(id)});
                database.close();

            }


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


