package com.robwid.uppgiftsbanken;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ProfileActivity extends Activity {

	private EditText et;
	private Statement st;
	private Button btnUpdate;
	private long studentID;
	private Connection con;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		try {
			Bundle b = getIntent().getExtras();
			studentID = b.getLong("sid");
			setTitle("Min profil");
			con = DatabaseHandler.getConnection();
			st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT resume FROM Student WHERE id = " + studentID);
			rs.first();
			String resume = rs.getString("resume");
			et = (EditText)this.findViewById(R.id.eId);
			et.setText(resume);
			btnUpdate = (Button)this.findViewById(R.id.btnUpdate);
			//Only allow applying for task once
			btnUpdate.setOnClickListener(new OnClickListener(){   
		    	@Override
		    	public void onClick(View v) {
		    		try {
						st.executeUpdate("UPDATE Student SET resume = '" + et.getText().toString() + "' WHERE id = " + studentID);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    	}
			});
			
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
    public void onBackPressed() {
        super.onBackPressed();   
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
		Bundle b = new Bundle();
		b.putLong("id",studentID);
		intent.putExtras(b); 
        startActivity(intent);
        finish();
    }


}
