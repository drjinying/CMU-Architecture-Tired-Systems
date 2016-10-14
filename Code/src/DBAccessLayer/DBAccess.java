/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DBAccessLayer;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author Shone
 */
public class DBAccess {
    private Connection leaftechConn = null;
    private Connection orderinfoConn = null;
    private Connection inventoryConn = null;
    private Connection accountsConn = null;
    
    private final String leaftechEP = "jdbc:mysql://localhost:3306/leaftech";
    private final String orderinfoEP = "jdbc:mysql://localhost:3306/orderinfo";
    private final String inventoryEP = "jdbc:mysql://localhost:3306/inventory";
    private final String accountsEP = "jdbc:mysql://localhost:3306/accounts";
    
    public DBAccess(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            leaftechConn = DriverManager.getConnection(leaftechEP, "webserver", "password");
            orderinfoConn = DriverManager.getConnection(orderinfoEP, "webserver", "password");
            inventoryConn = DriverManager.getConnection(inventoryEP, "webserver", "password");
            accountsConn = DriverManager.getConnection(accountsEP, "webserver", "password");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }
    
    private String executeUpdate(Connection conn, String sql) throws SQLException {
        java.sql.Statement sqlStmt = conn.createStatement();
        int result = sqlStmt.executeUpdate(sql);
        return String.valueOf(result);
    }
    
    private String executeQuery (Connection conn, String sql) throws SQLException {
        java.sql.Statement sqlStmt = conn.createStatement();
        java.sql.ResultSet result = sqlStmt.executeQuery(sql);
        String ret = "";
        int colCnt = result.getMetaData().getColumnCount();
        while (result.next()) {
            for (int i = 1; i < colCnt; i++) {
                ret += result.getString(i) + ",";
            }
            ret += result.getString(colCnt) + ";";
        }
        return ret;
    }
    
    public String leaftechUpdate (String sql) throws SQLException {
        return executeUpdate(leaftechConn, sql);
    }
    
    public String leaftechQuery (String sql) throws SQLException {
        return executeQuery(leaftechConn, sql);
    }
    
    public String orderinfoUpdate (String sql) throws SQLException {
        return executeUpdate(orderinfoConn, sql);
    }
    
    public String orderinfoQuery (String sql) throws SQLException {
        return executeQuery(orderinfoConn, sql);
    }
    
    public String inventoryUpdate (String sql) throws SQLException {
        return executeUpdate(inventoryConn, sql);
    }
    
    public String inventoryQuery (String sql) throws SQLException {
        return executeQuery(inventoryConn, sql);
    }
    
    public void addLog(String name, String activity) throws SQLException {
        java.sql.Statement sqlStmt = accountsConn.createStatement();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String sql = "INSERT INTO activitylogs (name, time, event)\n" +
                    "VALUES (\"" + name + "\", \"" + dateFormat.format(date) + "\", \"" + activity + "\");";
        sqlStmt.executeQuery(sql);
    }
    
    public String login(String name, String password) throws SQLException {
        java.sql.Statement sqlStmt = accountsConn.createStatement();
        String sql = "SELECT token FROM credentials WHERE name=\"" + name + "\" AND password=\"" + password + "\";";
        java.sql.ResultSet result = sqlStmt.executeQuery(sql);
        String token = "";
        if (result.next()) {
            token = result.getString(1);
            addLog(name, "login succeed");
        } else {
            addLog(name, "login failed, username/password combination not found");
        }
        return token;
    }
    
    public String logout(String name, String password) throws SQLException {
        if (login(name, password).length() > 0) {
            java.sql.Statement sqlStmt = accountsConn.createStatement();
            String newToken = UUID.randomUUID().toString();
            String sql = "UPDATE credentials SET token=\"" + newToken + "\" WHERE name = \"" + name + "\";";
            java.sql.ResultSet result = sqlStmt.executeQuery(sql);
            addLog(name, "logout succeed");
            return "SUCCEED";
        } else {
            addLog(name, "logout failed, username/password combination not found");
            return "FAILED";
        }
    }
    
    public void switchOver() throws SQLException {
        CallableStatement cStmt = accountsConn.prepareCall("{call switchover('kickoff')}");
        cStmt.execute();
    }
    
    public void switchOverRestore() throws SQLException {
        CallableStatement cStmt = accountsConn.prepareCall("{call switchover('restore')}");
        cStmt.execute();
    }
}
