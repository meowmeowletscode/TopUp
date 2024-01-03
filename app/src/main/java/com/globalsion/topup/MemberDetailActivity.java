package com.globalsion.topup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class MemberDetailActivity extends AppCompatActivity {

    TextView txtMemberNo;
    TextView txtName;
    TextView txtBalance;

    Button btnOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_detail);

        Intent intent = getIntent();

        int memberNo = intent.getIntExtra("MEMBER_NO", -1);
        String name = intent.getStringExtra("NAME");
        if (name == null) {
            name = "Member";
        }
        double balance = intent.getDoubleExtra("BALANCE", 0.0);

        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        String formattedBalance = decimalFormat.format(balance);

        // Link to XML
        txtMemberNo = findViewById(R.id.txtMemberNo);
        txtName = findViewById(R.id.txtName);
        txtBalance = findViewById(R.id.txtBalance);

        btnOK = findViewById(R.id.btnOK);

        // Set text to TextViews
        txtMemberNo.setText(String.valueOf(memberNo));
        txtName.setText(name);
        txtBalance.setText((formattedBalance));

        //Click Listener part
        btnOK.setOnClickListener(new BtnOKClickListener());
    }

    private class BtnOKClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MemberDetailActivity.this);
            builder.setTitle("Confirmation")
                    .setMessage("Please make sure you have recorded the Member Information.")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(MemberDetailActivity.this, MainActivity.class);
                            startActivity(i);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss(); // Close the dialog
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        BtnOKClickListener btnOKClickListener = new BtnOKClickListener();
        btnOKClickListener.onClick(null);
    }
}