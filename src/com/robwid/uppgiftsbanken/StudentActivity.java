package com.robwid.uppgiftsbanken;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;

public class StudentActivity extends Activity {
	
	private Statement st;
	private Connection con;
	private long employerID;
	private long taskID;
	private short open;
	private String title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_student);
		try {
			Bundle b = getIntent().getExtras();
			String name = b.getString("name");
			long studentID = b.getLong("sid");
			title = b.getString("title");
			taskID = b.getLong("tid");
			employerID = b.getLong("eid");
			open = b.getShort("open");
			setTitle(name);
			con = DatabaseHandler.getConnection();
			st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT resume,email FROM Student WHERE id = " + studentID);
			rs.first();
			String resume = rs.getString("resume");
			String email = rs.getString("email");
			printTask(resume, email, name);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void printTask(String resume, String email, String name){
		TextView tv = (TextView)this.findViewById(R.id.tv);
		tv.setText("\n" + resume + "\n\n" + "Kontakta " + name + " på adressen: " + email + "\n\n");
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		DatabaseHandler.closeConnection();
	}
	
	@Override
    public void onBackPressed() {
        super.onBackPressed();   
        Intent intent = new Intent(StudentActivity.this, AdministerTaskActivity.class);
		Bundle b = new Bundle();
		b.putLong("eid",employerID);
		b.putShort("open", open);
		b.putString("title", title);
		b.putLong("tid", taskID);
		intent.putExtras(b); 
        startActivity(intent);
        finish();
    }
}