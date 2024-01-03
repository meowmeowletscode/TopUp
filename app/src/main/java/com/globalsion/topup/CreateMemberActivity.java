package com.globalsion.topup;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;

public class CreateMemberActivity extends AppCompatActivity {

    EditText txtName;
    EditText txtBalance;
    Button btnBack;
    Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_member);

        double defaultBalance = 100.00;
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        String formattedBalance = decimalFormat.format(defaultBalance);

        //Link to XML
        txtName = findViewById(R.id.txtName);
        txtBalance = findViewById(R.id.txtBalance);

        btnBack = findViewById(R.id.btnBack);
        btnConfirm = findViewById(R.id.btnConfirm);

        //default Balance
        txtBalance.setText((formattedBalance));

        //Click Listener Part
        btnBack.setOnClickListener(new BtnBackClickListener());
        btnConfirm.setOnClickListener(new BtnConfirmClickListener(txtName, txtBalance));

    }

    private class BtnConfirmClickListener implements View.OnClickListener {

        private EditText txtName;
        private EditText txtBalance;

        public BtnConfirmClickListener(EditText txtName, EditText txtBalance) {
            this.txtName = txtName;
            this.txtBalance = txtBalance;
        }

        @Override
        public void onClick(View v) {
            String name = txtName.getText().toString().trim();
            String balanceText = txtBalance.getText().toString().trim();

            if (name.isEmpty()) {
                showAlertDialog("Invalid Name", "The Member Name cannot be empty.", v);
            } else if (balanceText.isEmpty()) {
                showAlertDialog("Invalid Initial Balance", "The initial balance cannot be empty.", v);
            } else {
                double initialBalance = Double.parseDouble(balanceText);
                if (initialBalance < 100) {
                    showAlertDialog("Invalid Initial Balance", "The initial balance must be over RM100.00", v);
                } else {
                    CreateMember();
                }
            }
        }

        private void showAlertDialog(String title, String message, View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .show();
        }

        private void CreateMember() {
            java.sql.Connection connection = new Connection().SQLConnection();
            try {
                if (connection != null) {
                    String Query = "INSERT INTO [Member] (Name, Balance) VALUES ('" + txtName.getText().toString().trim() + "', " + txtBalance.getText().toString().trim() + ")";
                    try {
                        Statement st = connection.createStatement();
                        st.executeUpdate(Query, Statement.RETURN_GENERATED_KEYS);

                        ResultSet generatedKeys = st.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            int generatedMemberNo = generatedKeys.getInt(1);

                            Intent intent = new Intent(CreateMemberActivity.this, MemberDetailActivity.class);

                            intent.putExtra("MEMBER_NO", generatedMemberNo);
                            intent.putExtra("NAME", txtName.getText().toString().trim());
                            intent.putExtra("BALANCE", Double.parseDouble(txtBalance.getText().toString().trim()));

                            startActivity(intent);

                            Toast.makeText(CreateMemberActivity.this, "Member " + txtName.getText().toString() + " create Successful.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception ex) {
                Log.e("Error ", ex.getMessage());
            }
        }
    }

    private class BtnBackClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(CreateMemberActivity.this, MainActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(CreateMemberActivity.this, MainActivity.class);
        startActivity(i);
    }
}