package com.globalsion.topup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.ResultSet;
import java.sql.Statement;

public class LoginActivity extends AppCompatActivity {

    EditText txtUser;
    EditText txtPassword;
    Button btnClear;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        txtUser = findViewById(R.id.txtLoginUser);
        txtPassword = findViewById(R.id.txtLoginPassword);
        btnClear = findViewById(R.id.btnClear);
        btnLogin = findViewById(R.id.btnLogin);

        btnClear.setOnClickListener(new BtnClearClickListener(txtUser, txtPassword));
        btnLogin.setOnClickListener(new BtnLoginClickListener(txtUser, txtPassword));
    }

    private class BtnLoginClickListener implements View.OnClickListener {

        private EditText txtUser;
        private EditText txtPassword;

        public BtnLoginClickListener(EditText txtUserName, EditText txtPassword) {
            this.txtUser = txtUserName;
            this.txtPassword = txtPassword;
        }

        @Override
        public void onClick(View v) {
            LogIn();
        }

        private void LogIn() {
            java.sql.Connection connection = new Connection().SQLConnection();
            if (isValidUser()) {
                try {
                    if (connection != null) {
                        String Query = "SELECT Password FROM [Staff] WHERE UserName='" + txtUser.getText().toString() + "'";
                        Statement st = connection.createStatement();
                        ResultSet rs = st.executeQuery(Query);
                        while (rs.next()) {
                            String storedPassword = rs.getString("Password");
                            String enteredPassword = txtPassword.getText().toString();

                            if (enteredPassword.equals(storedPassword)) {
                                Toast.makeText(LoginActivity.this, "Login Successful.", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(i);
                            } else {
                                Toast.makeText(LoginActivity.this, "The password is incorrect. Please check and try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (Exception ex) {
                    Log.e("Error ", ex.getMessage());
                }
            } else {
                Toast.makeText(LoginActivity.this, "User " + txtUser.getText().toString() + " not found. Please check and try again.", Toast.LENGTH_SHORT).show();
            }
        }

        private boolean isValidUser() {
            java.sql.Connection connection = new Connection().SQLConnection();
            try {
                if (connection != null) {
                    String Query = "SELECT UserName FROM [Staff] where UserName='" + txtUser.getText().toString().trim() + "'";
                    Statement st = connection.createStatement();
                    ResultSet rs = st.executeQuery(Query);
                    if (rs.next()) {
                        return true;
                    }
                }
            } catch (Exception ex) {
                Log.e("Error ", ex.getMessage());
            }
            return false;
        }
    }

    private class BtnClearClickListener implements View.OnClickListener {

        private EditText txtLoginUser;
        private EditText txtLoginPassword;

        public BtnClearClickListener(EditText txtLoginUser, EditText txtLoginPassword) {
            this.txtLoginUser = txtLoginUser;
            this.txtLoginPassword = txtLoginPassword;
        }

        @Override
        public void onClick(View v) {
            if (txtLoginUser != null) {
                txtLoginUser.setText("");
            }
            if (txtLoginPassword != null) {
                txtLoginPassword.setText("");
            }
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        LoginActivity.super.onBackPressed();
                    }
                }).create().show();
    }
}