package com.robwid.uppgiftsbanken;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class EmployerActivity extends ListActivity {

	private long employerID;
	private long[] taskIDs;
	private short[] open;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Dina uppgifter");
		Bundle b = getIntent().getExtras();
		employerID = b.getLong("id");
		//setContentView(R.layout.activity_main);
		try {
			Connection con = DatabaseHandler.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT id,title,open FROM Task WHERE employerID = " + employerID);
			int size = 0;
			if (rs.last()) {
				size = rs.getRow();
				rs.beforeFirst();
			}
			String[] tasks = new String[size];
			taskIDs = new long[size];
			open = new short[size];
			int i = 0;
			while (rs.next()) {
				tasks[i] = rs.getString("title");
				taskIDs[i] = rs.getLong("id");
				open[i] = rs.getShort("open");
				i++;
			}			
			setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,tasks));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		DatabaseHandler.closeConnection();
	}	
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		DatabaseHandler.closeConnection();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		String task = (String)this.getListAdapter().getItem(position);
		Intent intent = new Intent(EmployerActivity.this, AdministerTaskActivity.class);
		Bundle b = new Bundle();
		b.putString("title", task);
		b.putLong("tid", taskIDs[position]);
		b.putLong("eid",employerID);
		b.putShort("open", open[position]);
		intent.putExtras(b); 
		startActivity(intent);
		finish();
	}
}
