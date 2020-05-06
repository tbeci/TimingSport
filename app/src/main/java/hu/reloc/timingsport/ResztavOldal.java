package hu.reloc.timingsport;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ResztavOldal extends Activity {

	TextView textView1;
	ArrayList arrayList;
	ListView listView;
	MyDatabase md;
	String imei;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.resztav);


		md = new MyDatabase(this);
		md.getWritableDatabase();
		
		
		ImageView iv_exit = (ImageView) findViewById(R.id.imageViewExit);
		iv_exit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				finish();

			}
		});



		listView = (ListView) findViewById(R.id.listView2);
		arrayList = md.getAllBoja();

		SimpleAdapter adapter;
		adapter = new SimpleAdapter(ResztavOldal.this, arrayList,
				R.layout.activity_column_eredmeny, new String[] { "MemberID", "Name",
						"Lat", "Lon", "Ido" }, new int[] { R.id.ColMemberID,
						R.id.ColName, R.id.ColLat, R.id.ColLon, R.id.ColIdo  });

		listView.setAdapter(adapter);



	}



}