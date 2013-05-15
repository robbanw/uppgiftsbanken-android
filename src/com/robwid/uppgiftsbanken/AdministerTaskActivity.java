package com.robwid.uppgiftsbanken;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class AdministerTaskActivity extends ListActivity {
	
	private long[] studentIDs;
	private long employerID;
	private long taskID;
	private Connection con;
	private Statement st;
	private short open;
	private Button closeTask;
	private String title;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_administer_task);
		Bundle b = getIntent().getExtras();
		title = b.getString("title");
		taskID = b.getLong("tid");
		employerID = b.getLong("eid");
		open = b.getShort("open");
		setTitle(title + " - Administrera anmälan");
		//setContentView(R.layout.activity_main);
		try {
			con = DatabaseHandler.getConnection();
			st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT studentID FROM StudentToTask WHERE taskID = " + taskID);
			int size = 0;
			if (rs.last()) {
				size = rs.getRow();
				rs.beforeFirst();
			}
			String[] students = new String[size];
			studentIDs = new long[size];
			int i = 0;
			Statement studentStatement = con.createStatement();
			ResultSet resultSetStudent;
			while (rs.next()) {
				resultSetStudent = studentStatement.executeQuery("SELECT id,name FROM Student WHERE id = " + rs.getLong("studentID"));
				resultSetStudent.first();
				students[i] = resultSetStudent.getString("name");
				studentIDs[i] = resultSetStudent.getLong("id");
				i++;
			}			
			setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,students));
			
			closeTask = (Button)this.findViewById(R.id.closeTask);
			if(open == 1) closeTask.setText("Stäng anmälan");
			else closeTask.setText("Öppna anmälan");
			closeTask.setOnClickListener(new OnClickListener(){   
		    	@Override
		    	public void onClick(View v) {
		    		try {
		    			if(open == 1){
							st.executeUpdate("UPDATE Task SET open = 0 WHERE id = " + taskID);
							closeTask.setText("Öppna anmälan");
							open = 0;
		    			}
		    			else{
							st.executeUpdate("UPDATE Task SET open = 1 WHERE id = " + taskID);
							closeTask.setText("Stäng anmälan");
							open = 1;
		    			}
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
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		String name = (String)this.getListAdapter().getItem(position);
		Intent intent = new Intent(AdministerTaskActivity.this, StudentActivity.class);
		Bundle b = new Bundle();
		b.putString("name", name);
		b.putString("title", title);
		b.putLong("tid", taskID);
		b.putLong("sid", studentIDs[position]);
		b.putLong("eid", employerID);
		b.putShort("open", open);
		intent.putExtras(b); 
		startActivity(intent);
		finish();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		DatabaseHandler.closeConnection();
	}
	
	@Override
    public void onBackPressed() {
        super.onBackPressed();   
        Intent intent = new Intent(AdministerTaskActivity.this, EmployerActivity.class);
		Bundle b = new Bundle();
		b.putLong("id",employerID);
		intent.putExtras(b); 
        startActivity(intent);
        finish();
    }
}
