package com.globalsion.topup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    EditText txtMemberNo;
    TextView txtName;
    TextView txtBalance;
    Button btnRetrieve;
    Button btnClear;
    Button btnRM20;
    Button btnRM50;
    Button btnRM100;
    Button btnNewMember;
    Button btnLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        double defaultBalance = 0.00;
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        String formattedBalance = decimalFormat.format(defaultBalance);

        //Link to XML
        txtMemberNo = findViewById(R.id.txtMemberNo);
        txtName = findViewById(R.id.txtName);
        txtBalance = findViewById(R.id.txtBalance);

        btnRetrieve = findViewById(R.id.btnRetrieve);
        btnClear = findViewById(R.id.btnClear);

        btnRM20 = findViewById(R.id.btnRM20);
        btnRM50 = findViewById(R.id.btnRM50);
        btnRM100 = findViewById(R.id.btnRM100);

        btnNewMember = findViewById(R.id.btnNewMember);
        btnLogOut = findViewById(R.id.btnLogOut);

        //default Balance
        txtBalance.setText((formattedBalance));

        //Click Listener Part
        btnRetrieve.setOnClickListener(new BtnRetrieveClickListener(txtMemberNo, MainActivity.this));
        btnClear.setOnClickListener(new BtnClearClickListener(txtMemberNo, txtName, txtBalance));

        View.OnClickListener topUpClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(txtMemberNo.getText().toString())) {
                    double topUpAmount = 0.00;

                    switch (v.getId()) {
                        case R.id.btnRM20:
                            topUpAmount = 20.00;
                            break;
                        case R.id.btnRM50:
                            topUpAmount = 50.00;
                            break;
                        case R.id.btnRM100:
                            topUpAmount = 100.00;
                            break;
                    }
                    new BtnTopUpClickListener(MainActivity.this, txtMemberNo, txtBalance, topUpAmount).onClick(v);
                } else {
                    Toast.makeText(MainActivity.this, "Member No is required", Toast.LENGTH_SHORT).show();
                }
            }
        };

        btnRM20.setOnClickListener(topUpClickListener);
        btnRM50.setOnClickListener(topUpClickListener);
        btnRM100.setOnClickListener(topUpClickListener);

        btnNewMember.setOnClickListener(new BtnNewMemberClickListener());
        btnLogOut.setOnClickListener(new BtnLogOutClickListener());
    }

    private class BtnRetrieveClickListener implements View.OnClickListener{

        private EditText txtMemberNo;
        private Context context;

        public BtnRetrieveClickListener(EditText txtMemberNo, Context context){
            this.txtMemberNo = txtMemberNo;
            this.context = context;
        }

        @Override
        public void onClick(View v){
            if(isValidMemberNo(txtMemberNo.getText().toString().trim())) {
                java.sql.Connection connection = new Connection().SQLConnection();
                try {
                    if (connection != null) {
                        String Query = "SELECT Name, Balance FROM [Member] where MemberNo='" + txtMemberNo.getText().toString() + "'";
                        Statement st = connection.createStatement();
                        ResultSet rs = st.executeQuery(Query);
                        if (rs.next()) {
                            txtName.setText(rs.getString(1));
                            txtBalance.setText(rs.getString(2));
                        } else {
                            showCreateNewUserDialog();
                        }
                    }
                } catch (Exception ex) {
                    Log.e("Error ", ex.getMessage());
                }
            }else{
                Toast.makeText(MainActivity.this, "Invalid member number. Please enter a 3-digit number.", Toast.LENGTH_SHORT).show();
            }
        }

        private boolean isValidMemberNo(String memberNo) {
            return !TextUtils.isEmpty(memberNo) && memberNo.length() == 3 && TextUtils.isDigitsOnly(memberNo);
        }

        private void showCreateNewUserDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Member "+txtMemberNo.getText().toString()+" not found. Kindly check again the Member No.\n\nDo you want to register as a new member?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent i = new Intent(MainActivity.this, CreateMemberActivity.class);
                            startActivity(i);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked "No," do nothing
                        }
                    });
            builder.create().show();
        }
    }

    private class BtnClearClickListener implements View.OnClickListener{

        private EditText txtMemberNo;
        private TextView txtName;
        private TextView txtBalance;

        // Constructor to initialize the TextViews
        public BtnClearClickListener(EditText txtMemberNo, TextView txtName, TextView txtBalance) {
            this.txtMemberNo = txtMemberNo;
            this.txtName = txtName;
            this.txtBalance = txtBalance;
        }

        @Override
        public void onClick(View v){
            if (txtMemberNo != null) {
                txtMemberNo.setText("");
            }
            if (txtName != null) {
                txtName.setText("");
            }
            if (txtBalance != null) {
                txtBalance.setText("0.00");
            }
        }
    }

    private class BtnTopUpClickListener implements View.OnClickListener{

        private Context context;
        private  EditText txtMemberNo;
        private TextView txtBalance;
        private double topUpAmount;

        public BtnTopUpClickListener(Context context, EditText txtMemberNo, TextView txtBalance, double topUpAmount){
            this.context = context;
            this.txtMemberNo = txtMemberNo;
            this.txtBalance = txtBalance;
            this.topUpAmount = topUpAmount;
        }

        @Override
        public void onClick(View v){
            showConfirmationDialog();
        }

        private void showConfirmationDialog() {
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            String formattedTopUpAmount = decimalFormat.format(topUpAmount);

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Confirm Top-Up");
            builder.setMessage("Are you sure you want to add RM" + formattedTopUpAmount + " to "+txtName.getText().toString().trim()+" balance?\n\nTHIS PROCESS CANNOT BE UNDONE.");

            builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updateBalance();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // User canceled, do nothing
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        private void updateBalance() {

            String currentBalanceString = txtBalance.getText().toString();
            double currentBalance = Double.parseDouble(currentBalanceString);

            double newBalance = currentBalance + topUpAmount;

            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            String formattedNewBalance = decimalFormat.format(newBalance);
            txtBalance.setText(formattedNewBalance);

            int memberNo = Integer.parseInt(txtMemberNo.getText().toString());

            // Update the balance in the database
            java.sql.Connection connection = new Connection().SQLConnection();
            try {
                if (connection != null) {
                    String Query = "UPDATE [Member] SET Balance = " + newBalance + " WHERE MemberNo = " + memberNo;
                    try {
                        Statement st = connection.createStatement();
                        st.executeUpdate(Query);

                        Toast.makeText(MainActivity.this, "Balance Top Up Successful.", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(MainActivity.this, MemberDetailActivity.class);
                        intent.putExtra("MEMBER_NO", memberNo);
                        intent.putExtra("NAME", txtName.getText().toString().trim());
                        intent.putExtra("BALANCE", newBalance);
                        startActivity(intent);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception ex) {
                Log.e("Error ", ex.getMessage());
            }
        }
    }

    private class BtnNewMemberClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            Intent i = new Intent(MainActivity.this, CreateMemberActivity.class);
            startActivity(i);
        }
    }

    private class BtnLogOutClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            showLogOutConfirmationDialog();
        }

        private void showLogOutConfirmationDialog() {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Log Out?")
                    .setMessage("Are you sure you want to log out?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            // Perform the logout action or navigate to the LoginActivity
                            // For now, let's just start the LoginActivity
                            Intent i = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(i);
                        }
                    }).create().show();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(MainActivity.this, "Successful Log Out.", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(i);
                    }
                }).create().show();
    }
}