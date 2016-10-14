/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package combined;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import WebServerLayer.WebServer;
import java.io.DataOutputStream;
import java.util.Date;

/**
 *
 * @author Arun
 */
public class Middleware {

    private boolean connectError = false;
    private Connection DBConn;

    private int port = 4000;
    private String serverDNS;

    public Middleware() {
        try {
            WebServer.startServer(port);
        } catch (Exception e) {
            //e.printStackTrace();
        }

    }

    // Removed
    public String runQuery(String db_name, String ip, String sqlStatement) {

        System.out.println("Run Query :" + db_name + " Ip : " + ip + " statement : \n " + sqlStatement);
        String msgString = "";
        java.sql.Statement s = null;
        establishConnection(ip, db_name);
        System.out.println("Established");
        if (!connectError) {
            try {
                s = DBConn.createStatement();

            } catch (SQLException ex) {
                Logger.getLogger(Middleware.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Error in db connection");
            }
            try {
                System.out.println("Successfully completed DB task");
                msgString = "" + s.executeUpdate(sqlStatement);
            } catch (Exception ex) {
                Logger.getLogger(Middleware.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return msgString;

    }

    // Removed
    public ResultSet runQueryAndGetResultSet(String db_name, String ip, String sqlStatement) {

        System.out.println("Run Query with result set :" + db_name + " Ip : " + ip + " statement : \n " + sqlStatement);
        String msgString = "";
        java.sql.Statement s = null;
        establishConnection(ip, db_name);
        System.out.println("Established");
        if (!connectError) {
            try {
                s = DBConn.createStatement();

            } catch (SQLException ex) {
                Logger.getLogger(Middleware.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Error in db connection");
            }
            try {
                System.out.println("Successfully completed DB task");
                return s.executeQuery(sqlStatement);
            } catch (Exception ex) {
                Logger.getLogger(Middleware.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        System.out.println("Error has occured!!");
        return null;

    }

    // Removed
    public String establishConnection(String ip, String db_name) {
        String msgString = "";

        try {
            msgString += ">> Establishing Driver...";
            //jTextArea1.setText("\n" + msgString);

            //load JDBC driver class for MySQL
            Class.forName("com.mysql.jdbc.Driver");

            msgString += "\n >> Setting up URL...";
            //jTextArea1.append("\n" + msgString);

            //define the data source
            String SQLServerIP = ip;
            String sourceURL = "jdbc:mysql://" + SQLServerIP + ":3306/" + db_name;

            msgString = "\n >> Establishing connection with: " + sourceURL + "...";
            //jTextArea1.append("\n" + msgString);

            //create a connection to the db
            DBConn = DriverManager.getConnection(sourceURL, "remote", "remote_pass");

        } catch (Exception e) {

            String errString = "\nProblem connecting to database:: " + e;
            msgString += (errString);
            connectError = true;

        }
        System.out.println("MSG: " + msgString);
        return msgString;
    }

    /*
    
    Get queries
    
     */
    public String[] getAllTrees(String ip) {
        serverDNS = ip;
        return getContent("/listTrees").split(";");
    }

    public String[] getAllShrubs(String ip) {
        serverDNS = ip;
        return getContent("/listShrubs").split(";");
    }

    public String[] getAllSeeds(String ip) {
        serverDNS = ip;
        return getContent("/listSeeds").split(";");
    }

    public String[] getAllCultureBoxes(String ip) {
        serverDNS = ip;
        return getContent("/listCultureboxes").split(";");
    }

    public String[] getAllProcessing(String ip) {
        serverDNS = ip;
        return getContent("/listProcessing").split(";");
    }

    public String[] getAllGenomics(String ip) {
        serverDNS = ip;
        return getContent("/listGenomics").split(";");
    }

    public String[] getAllReferenceMaterials(String ip) {
        serverDNS = ip;
        return getContent("/listReferencematerials").split(";");
    }

    public String putTrees(String ip, String productCode, String description, String quantity, String price) {
        serverDNS = ip;
        return putContent("/insertTrees", "productCode=" + productCode + "&description=" + description + "&quantity=" + quantity + "&price=" + price);

    }

    public String putShrubs(String ip, String productCode, String description, String quantity, String price) {
        serverDNS = ip;
        return putContent("/insertShrubs", "productCode=" + productCode + "&description=" + description + "&quantity=" + quantity + "&price=" + price);

    }

    public String putSeeds(String ip, String productCode, String description, String quantity, String price) {
        serverDNS = ip;
        return putContent("/insertSeeds", "productCode=" + productCode + "&description=" + description + "&quantity=" + quantity + "&price=" + price);

    }

    public String putCultureBoxes(String ip, String productCode, String description, String quantity, String price) {
        serverDNS = ip;
        return putContent("/insertCultureboxes", "productCode=" + productCode + "&description=" + description + "&quantity=" + quantity + "&price=" + price);

    }

    public String putProcessing(String ip, String productCode, String description, String quantity, String price) {
        serverDNS = ip;
        return putContent("/insertProcessing", "productCode=" + productCode + "&description=" + description + "&quantity=" + quantity + "&price=" + price);

    }

    public String putGenomics(String ip, String productCode, String description, String quantity, String price) {
        serverDNS = ip;
        return putContent("/insertGenomics", "productCode=" + productCode + "&description=" + description + "&quantity=" + quantity + "&price=" + price);

    }

    public String putReferenceMaterials(String ip, String productCode, String description, String quantity, String price) {
        serverDNS = ip;
        return putContent("/insertReferencematerials", "productCode=" + productCode + "&description=" + description + "&quantity=" + quantity + "&price=" + price);

    }

    public String decrementTrees(String ip, String productCode) {

        serverDNS = ip;
        return deleteOrDecrementContent("/decreaseTrees", ip, productCode);

    }

    public String decrementReferenceMaterials(String ip, String productCode) {

        serverDNS = ip;
        return deleteOrDecrementContent("/decreaseReferencematerials", ip, productCode);

    }

    public String decrementGenomics(String ip, String productCode) {

        serverDNS = ip;
        return deleteOrDecrementContent("/decreaseGenomics", ip, productCode);

    }

    public String decrementProcessing(String ip, String productCode) {

        serverDNS = ip;
        return deleteOrDecrementContent("/decreaseProcessing", ip, productCode);

    }

    public String decrementCultureBoxes(String ip, String productCode) {

        serverDNS = ip;
        return deleteOrDecrementContent("/decreaseCultureboxes", ip, productCode);

    }

    public String decrementSeeds(String ip, String productCode) {

        serverDNS = ip;
        return deleteOrDecrementContent("/decreaseSeeds", ip, productCode);

    }

    public String decrementShrubs(String ip, String productCode) {

        serverDNS = ip;
        return deleteOrDecrementContent("/decreaseShrubs", ip, productCode);

    }

    public String deleteTree(String ip, String productCode) {

        serverDNS = ip;
        return deleteOrDecrementContent("/deleteTrees", ip, productCode);
        //return "";

    }

    public String deleteReferenceMaterials(String ip, String productCode) {

        serverDNS = ip;
        return deleteOrDecrementContent("/deleteReferencematerials", ip, productCode);
        //return "";

    }

    public String deleteGenomics(String ip, String productCode) {

        serverDNS = ip;
        return deleteOrDecrementContent("/deleteGenomics", ip, productCode);
        //return "";

    }

    public String deleteProcessing(String ip, String productCode) {

        serverDNS = ip;
        return deleteOrDecrementContent("/deleteProcessing", ip, productCode);
        //return "";

    }

    public String deleteCultureBoxes(String ip, String productCode) {

        serverDNS = ip;
        return deleteOrDecrementContent("/deleteCultureboxes", ip, productCode);
        //return "";

    }

    public String deleteSeeds(String ip, String productCode) {

        serverDNS = ip;
        return deleteOrDecrementContent("/deleteSeeds", ip, productCode);
        //return "";

    }

    public String deleteShrubs(String ip, String productCode) {

        serverDNS = ip;
        return deleteOrDecrementContent("/deleteShrubs", ip, productCode);
        //return "";

    }

    public String deleteOrDecrementContent(String endpoint, String ip, String productCode) {

        HttpURLConnection connection = null;
        try {
            //Create connection
            System.out.println("URL : http://" + serverDNS + ":" + port + endpoint + "/?productCode=" + productCode);
            URL url = new URL("http://" + serverDNS + ":" + port + endpoint + "/?productCode=" + productCode);

            connection = (HttpURLConnection) url.openConnection();
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+ 
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            return response.toString();
        } catch (Exception e) {
            //e.printStackTrace();
            return "";
        }

    }

    public String getContent(String endpoint) {
        HttpURLConnection connection = null;
        try {
            //Create connection
            System.out.println("Url is :" + "http://" + serverDNS + endpoint);
            URL url = new URL("http://" + serverDNS + ":" + port + endpoint);

            connection = (HttpURLConnection) url.openConnection();
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+ 
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            return response.toString();
        } catch (Exception e) {
            //e.printStackTrace();
            return "";
        }
    }

    public String putContent(String endpoint, String urlParameters) {
        HttpURLConnection connection = null;
        try {
            //Create connection
            URL url = new URL("http://" + serverDNS + ":" + port + endpoint + "/?" + urlParameters);

            connection = (HttpURLConnection) url.openConnection();
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+ 
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            return response.toString();
        } catch (Exception e) {
            //e.printStackTrace();
            return "";
        }
    }

    public Boolean authenticate(String username, String password) {
        String token = "&A%@S(aiFSushdf(&iY@#&*R$Y";
        String SQLstatement = null;
        String ip = "localhost";
        String db_name = "accounts";
        java.sql.Statement s = null;
        ResultSet res = null;
        String msgString = null;
        
        try {
            SQLstatement = ("SELECT * FROM credentials WHERE name=\""
                            + username +"\";");
            System.out.println(SQLstatement);
            establishConnection(ip, db_name);
            if(!connectError)
            {
                try {
                    s = DBConn.createStatement();
                
                } catch (SQLException ex) {
                    Logger.getLogger(Middleware.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Error in db connection");
                }
                try {
                    System.out.println("Successfully completed DB task");
                    res=s.executeQuery(SQLstatement);
                    while (res.next()) {
                    msgString = res.getString(2);
                    if (msgString.equals(password)){
                        log(username,"log in");
                        return true;
                    }
                } // while
            } catch (Exception ex) {
                Logger.getLogger(Middleware.class.getName()).log(Level.SEVERE, null, ex);
            }    
        }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        
        return false;
    }
    
    public void log (String username, String activity){
        String SQLstatement = null;
        Date date = new Date();
        
        try {
            String db_name = "accounts";
            SQLstatement = ("INSERT INTO activitylogs VALUES (\""
                            + date.toString() + "\",\"" + username + "\",\"" + activity + "\");");
            String SQLServerIP = "localhost";
            System.out.println(SQLstatement);
            runQuery(db_name,SQLServerIP,SQLstatement);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    
}
