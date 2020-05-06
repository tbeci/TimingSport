package hu.reloc.timingsport;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import java.io.InputStream;


public class RegisterNezet extends Activity {

    String imei, gender, vn, kn, yob, verseny, tav, bib, auto;
    InputStream is = null;
    String result = null;
    String line = null;
    int code;


    MyDatabase md;
    private SQLiteDatabase dataBase;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registernezet);

        md = new MyDatabase(this);

        // Permission StrictMode
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        String deviceid = manager.getDeviceId();
        imei = deviceid;

        userEntry();
        userLogin();


        TextView imeit = (TextView) findViewById(R.id.textImei);
        imeit.setText(Html.fromHtml(
                "<b>PID: </b>" + imei + "<br>" +
                        "<b>" + getString(R.string.regn_nev)+" </b><font color=\"#f15a22\">" + vn + " " + kn + "</font><br>" +
                        "<b>" + getString(R.string.regn_szuletesiev)+" </b>" + yob + "<br>" +
                        "<b>" + getString(R.string.regn_neme)+" </b>" + gender + "<p>" +
                        "<b>" + getString(R.string.regn_rsz)+" </b>" + bib + "<br>" +
                        "<b>" + getString(R.string.regn_verseny)+" </b>" + verseny + "<br>" +
                        "<b>" + getString(R.string.regn_tav)+" </b>" + tav + "<br>" +
                        "<b>" + getString(R.string.regn_timer)+" </b>" + auto));


        ImageView iv_exit = (ImageView) findViewById(R.id.imageViewExit);
        iv_exit.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {

                finish();


            }
        });

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


}


