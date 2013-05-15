package com.robwid.uppgiftsbanken;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ListActivity {

	private long studentID;
	private ArrayList<Long> taskIDs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getIntent().getExtras();
		studentID = b.getLong("id");
		//setContentView(R.layout.activity_main);
		try {
			Connection con = DatabaseHandler.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT title,id,open FROM Task");
			ArrayList<String> tempTasks = new ArrayList<String>();
			taskIDs = new ArrayList<Long>();
			while (rs.next()) {
				if(rs.getShort("open") == 1){
					tempTasks.add(rs.getString("title"));
					taskIDs.add(rs.getLong("id"));
				}
			}
			String[] tasks = tempTasks.toArray(new String[tempTasks.size()]);
			setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,tasks));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
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
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub		
		Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
		Bundle b = new Bundle();
		b.putLong("sid",studentID);
		intent.putExtras(b); 
		startActivity(intent);
		return super.onOptionsItemSelected(item);
	}


	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		String task = (String)this.getListAdapter().getItem(position);
		Intent intent = new Intent(MainActivity.this, TaskActivity.class);
		Bundle b = new Bundle();
		b.putString("title", task);
		b.putLong("sid",studentID);
		b.putLong("tid", taskIDs.get(position));
		intent.putExtras(b); 
		startActivity(intent);
		finish();
	}
}
