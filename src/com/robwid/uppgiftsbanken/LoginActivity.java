package com.robwid.uppgiftsbanken;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	private EditText txtUserName;
	private EditText txtPassword;
	private Button btnLogin;
	private RadioButton radioButtonType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setTitle("Logga in");
        txtUserName = (EditText)this.findViewById(R.id.txtUname);
        txtPassword = (EditText)this.findViewById(R.id.txtPwd);
        btnLogin = (Button)this.findViewById(R.id.btnLogin);
        radioButtonType = (RadioButton)this.findViewById(R.id.radioStudent);
        btnLogin.setOnClickListener(new OnClickListener(){   
	    	@Override
	    	public void onClick(View v) {
	    		try{
					Connection con = DatabaseHandler.openConnection();
					boolean isStudent = false;
					
					//We have a student login if this radiobutton is checked
					if(radioButtonType.isChecked()){
						isStudent = true;
					}
					
					
					//Hashing the supplied password
					MessageDigest md = MessageDigest.getInstance("SHA-256");
					md.update(txtPassword.getText().toString().getBytes());
					byte[] hashedPass = md.digest();
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < hashedPass.length; i++) {
						sb.append(Integer.toString((hashedPass[i] & 0xff) + 0x100, 16).substring(1));
					}
					PreparedStatement st;
					if(isStudent) st = con.prepareStatement("SELECT id FROM Student WHERE username LIKE ? AND password LIKE ?");
					else st = con.prepareStatement("SELECT id FROM Employer WHERE username LIKE ? AND password LIKE ?");
					st.setString(1,txtUserName.getText().toString());
					st.setString(2,sb.toString());
					ResultSet rs = st.executeQuery();
					if(rs.first()){
						Intent intent;
						if(isStudent) intent = new Intent(LoginActivity.this, MainActivity.class);
						else intent = new Intent(LoginActivity.this, EmployerActivity.class);
						Bundle b = new Bundle();
						long id = rs.getLong("id");
						b.putLong("id", id);
						intent.putExtras(b);
						startActivity(intent);
						finish();
					}
					else{
			            Toast.makeText(LoginActivity.this, "Felaktigt användarnamn eller lösenord!",Toast.LENGTH_LONG).show();	 
					}
	    		}
	    		catch(NoSuchAlgorithmException ne){
	    			System.out.print(ne.getMessage());
	    		}
				 catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
        });  
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		DatabaseHandler.closeConnection();
	}

}
