package com.robwid.uppgiftsbanken;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class TaskActivity extends Activity {
	
	private long studentID;
	private long taskID;
	private Statement st;
	private Button btnApply;
	private Connection con;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task);
		try {
			Bundle b = getIntent().getExtras();
			String title = b.getString("title");
			studentID = b.getLong("sid");
			taskID = b.getLong("tid");
			setTitle(title);
			con = DatabaseHandler.getConnection();
			st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM Task WHERE id = " + taskID);
			rs.first();
			String description = rs.getString("description");
			rs = st.executeQuery("SELECT * FROM Employer WHERE id = " + rs.getLong("employerID"));
			rs.first();
			String employer = rs.getString("organisation");
			if(employer.equals("")){
				employer = rs.getString("name");
			}
			printTask(title, description,employer);
			rs = st.executeQuery("SELECT id FROM StudentToTask WHERE studentID = " + studentID + " AND taskID = " + taskID);
			btnApply = (Button)this.findViewById(R.id.btnApply);
			//Only allow applying for task once
			if(!rs.first()){
				btnApply.setOnClickListener(new OnClickListener(){   
			    	@Override
			    	public void onClick(View v) {
			    		try {
							st.executeUpdate("INSERT INTO StudentToTask (studentID,taskID) VALUES (" + studentID + "," + taskID + ")");
							btnApply.setEnabled(false);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			    	}
				});
			}
			else{
				btnApply.setEnabled(false);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void printTask(String title, String description, String employer){
		TextView tv = (TextView)this.findViewById(R.id.tv);
		tv.setText("\n" + description + "\n\n" + "Uppdragsgivare: " + employer + "\n\n");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub		
		Intent intent = new Intent(TaskActivity.this, ProfileActivity.class);
		Bundle b = new Bundle();
		b.putLong("sid",studentID);
		intent.putExtras(b); 
		startActivity(intent);
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		DatabaseHandler.closeConnection();
	}
	
	@Override
    public void onBackPressed() {
        super.onBackPressed();   
        Intent intent = new Intent(TaskActivity.this, MainActivity.class);
		Bundle b = new Bundle();
		b.putLong("id",studentID);
		intent.putExtras(b); 
        startActivity(intent);
        finish();
    }

}
