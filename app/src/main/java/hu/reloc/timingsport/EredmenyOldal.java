package hu.reloc.timingsport;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EredmenyOldal extends Activity {

    String felhasznalo, id, pid;
    //String id, rsz, datum, km, munka, leiras, ar, szerviz, beszallito, cikkszam;

    Bundle extrak;
    // MyDatabase md;




    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);

        // md = new MyDatabase(this);
        //  torles();


        extrak = getIntent().getExtras();
        final String apid = extrak.getString("pid");
        pid = apid;








        ImageView btnExit = (ImageView) findViewById(R.id.imageViewExit);
        btnExit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub


                finish();

            }
        });



        // Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // listView1
        final ListView lstView1 = (ListView) findViewById(R.id.listView10);


        String url = getString(R.string.url) + "results.php";

        try {
            JSONArray data = new JSONArray(getJSONUrl(url));

            final ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> map;

            for (int i = 0; i < data.length(); i++) {
                JSONObject c = data.getJSONObject(i);
                map = new HashMap<String, String>();
                map.put("id", c.getString("id"));
                map.put("rank", c.getString("rank"));
                map.put("crank", c.getString("crank"));
                map.put("bib", c.getString("bib"));
                map.put("name", c.getString("name"));
                map.put("verseny", c.getString("verseny"));
                map.put("leiras", c.getString("leiras"));
                map.put("cat", c.getString("cat"));
                map.put("ido", c.getString("ido"));
                map.put("mikor", c.getString("mikor"));

                MyArrList.add(map);


                ///sqlite
             /*   SQLiteDatabase database = md.getWritableDatabase();
                ContentValues values = new ContentValues();

                values.put(md.COL_INFO_ID, c.getString("id"));
                values.put(md.COL_INFO_RSZ, c.getString("rsz"));
                values.put(md.COL_INFO_DAT, c.getString("datum"));
                values.put(md.COL_INFO_KM, c.getString("km"));
                values.put(md.COL_INFO_MUNKA, c.getString("munka"));
                values.put(md.COL_INFO_LEIRAS, c.getString("leiras"));
                values.put(md.COL_INFO_AR, c.getString("ar"));
                values.put(md.COL_INFO_SZER, c.getString("szerviz"));
                database.insert(md.TABLE_NAME_INFO, null, values);
                database.close();
*/
            }


            lstView1.setAdapter(new ImageAdapter(this, MyArrList));

            final AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
            final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

            // OnClick
            // OnClick Item
     /*       lstView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> myAdapter, View myView, int position, long mylng) {

                    Intent newActivity = new Intent(SzervizKonyv.this, SzervizKonyvReszlet.class);

                    newActivity.putExtra("id", MyArrList.get(position).get("id").toString());
                    newActivity.putExtra("rsz", MyArrList.get(position).get("rsz").toString());
                    newActivity.putExtra("datum", MyArrList.get(position).get("datum").toString());
                    newActivity.putExtra("km", MyArrList.get(position).get("km").toString());
                    newActivity.putExtra("ar", MyArrList.get(position).get("ar").toString());
                    newActivity.putExtra("munka", MyArrList.get(position).get("munka").toString());
                    newActivity.putExtra("leiras", MyArrList.get(position).get("leiras").toString());
                    newActivity.putExtra("beszallito", MyArrList.get(position).get("beszallito").toString());
                    newActivity.putExtra("cikkszam", MyArrList.get(position).get("cikkszam").toString());
                    newActivity.putExtra("szerviz", MyArrList.get(position).get("szerviz").toString());
                    newActivity.putExtra("felhasznalo", felhasznalo);

                    startActivity(newActivity);
                    finish();


                }
            });*/


            // OnClick Item
            final AlertDialog.Builder viewDetail = new AlertDialog.Builder(this);
            lstView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> myAdapter, View myView,
                                        int position, long mylng) {

                    String sBib = MyArrList.get(position).get("bib").toString();
                    String sRank = MyArrList.get(position).get("rank").toString();
                    String sCrank = MyArrList.get(position).get("crank").toString();
                    String sVerseny = MyArrList.get(position).get("verseny").toString();
                    String sLeiras = MyArrList.get(position).get("leiras").toString();
                    String sCat = MyArrList.get(position).get("cat").toString();
                    String sIdo = MyArrList.get(position).get("ido").toString();
                    String sName = MyArrList.get(position).get("name").toString();




                    viewDetail.setIcon(android.R.drawable.ic_dialog_info);
                    viewDetail.setTitle(sName);
                    viewDetail.setMessage(Html.fromHtml(
                            getString(R.string.regn_rsz)+ " <b>" + sBib  + "</b><br><br>"

                            +"<font color=\"#f15a22\"><b>" + sVerseny + "</b></font><br>"
                                    + "<font color=\"#0B2161\">" + sLeiras + "</font><br>"
                            + getString(R.string.res_helyzes) + " <b>" + sRank  + ".</b><br><br>"

                            + getString(R.string.res_kat)     + " <b>" + sCat   + "</b><br>"
                            + getString(R.string.res_hely_kat)+ " <b>" + sCrank + ".</b><br><br>"

                            + getString(R.string.res_ido)     + " <b>" + sIdo

                    ));
                    viewDetail.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub
                                    dialog.dismiss();
                                }
                            });
                    viewDetail.show();

                }
            });



        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }


    public class ImageAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<HashMap<String, String>> MyArr = new ArrayList<HashMap<String, String>>();

        public ImageAdapter(Context c, ArrayList<HashMap<String, String>> list) {
            // TODO Auto-generated method stub
            context = c;
            MyArr = list;
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return MyArr.size();
        }

        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


            if (convertView == null) {
                convertView = inflater.inflate(R.layout.activity_column_results, null);
            }

/*
            // ColImage
            ImageView imageView = (ImageView) convertView.findViewById(R.id.ColImgPath);
            imageView.getLayoutParams().height = 100;
            imageView.getLayoutParams().width = 100;
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            try
            {
                imageView.setImageBitmap(loadBitmap(MyArr.get(position).get("ImagePath")));
            } catch (Exception e) {
                // When Error
                imageView.setImageResource(android.R.drawable.ic_menu_report_image);
            }
*/





            // ColPosition
            TextView txtBib = (TextView) convertView.findViewById(R.id.ColBib);
            //txtPosition.setPadding(10, 0, 0, 0);
            txtBib.setText(MyArr.get(position).get("bib"));

            TextView txtVerseny = (TextView) convertView.findViewById(R.id.ColVerseny);
            //txtPosition.setPadding(10, 0, 0, 0);
            txtVerseny.setText(Html.fromHtml("<b>"+(MyArr.get(position).get("verseny"))));

            TextView txtMikor = (TextView) convertView.findViewById(R.id.ColMikor);
            //txtPosition.setPadding(10, 0, 0, 0);
            txtMikor.setText(MyArr.get(position).get("mikor"));

            // ColPicname
            TextView txtLeiras = (TextView) convertView.findViewById(R.id.ColLeiras);
            //txtPicName.setPadding(50, 0, 0, 0);
            txtLeiras.setText(MyArr.get(position).get("leiras"));

            // ColPicname
            TextView txtIdo = (TextView) convertView.findViewById(R.id.ColIdo);
            //txtPicName.setPadding(50, 0, 0, 0);
            txtIdo.setText(MyArr.get(position).get("ido"));

         /*   TextView txtSzerviz = (TextView) convertView.findViewById(R.id.szerviz);
            //txtPicName.setPadding(50, 0, 0, 0);
            txtSzerviz.setText(Html.fromHtml("<font color=\"#f15a22\">" + MyArr.get(position).get("szerviz")));
*/
            return convertView;

        }

    }


    /*
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


}
