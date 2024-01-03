package com.globalsion.topup;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;
import java.sql.DriverManager;

public class Connection {

    @SuppressLint("NewApi")
    public java.sql.Connection SQLConnection() {
        java.sql.Connection conn;
        String serverName = "DESKTOP-xxxxx";
        String port = "1433";
        String databaseName = "TopUp";
        String instanceName = "MSSQL2014";
        String userName = "your_user_name";
        String password = "your_password";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String connectionURL = null;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectionURL = "jdbc:jtds:sqlserver://" + serverName + ":" + port + "/" + databaseName + ";instance=" + instanceName + ";useGeneratedKeys=true";
            conn = DriverManager.getConnection(connectionURL, userName, password);
        } catch (Exception ex) {
            Log.e("Error: ", ex.getMessage());
            conn = null;
        }

        return conn;
    }
}
