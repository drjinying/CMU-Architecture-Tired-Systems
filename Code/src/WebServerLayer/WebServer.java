/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WebServerLayer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import static com.sun.org.apache.xerces.internal.xinclude.XIncludeHandler.BUFFER_SIZE;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Shone
 */
public class WebServer {

    static DBAccessLayer.DBAccess db = new DBAccessLayer.DBAccess();

    public static void startServer(int port) throws IOException 
    {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        // accounts db
        server.createContext("/sampleWebApp", new sampleWebAppHandler());
        server.createContext("/switchOver", new switchOverHandler());
        server.createContext("/login", new loginHandler());
        server.createContext("/logout", new logoutHandler());

        // inventory db
        server.createContext("/listTrees", new listTreesHandler());
        server.createContext("/insertTrees", new insertTreesHandler());
        server.createContext("/deleteTrees", new deleteTreesHandler());
        server.createContext("/decreaseTrees", new decreaseTreesHandler());

        server.createContext("/listShrubs", new listShrubsHandler());
        server.createContext("/insertShrubs", new insertShrubsMyHandler());
        server.createContext("/deleteShrubs", new deleteShrubsHandler());
        server.createContext("/decreaseShrubs", new decreaseShrubsHandler());

        server.createContext("/listSeeds", new listSeedsHandler());
        server.createContext("/insertSeeds", new insertSeedsHandler());
        server.createContext("/deleteSeeds", new deleteSeedsHandler());
        server.createContext("/decreaseSeeds", new decreaseSeedsHandler());

        // orderinfo db
        server.createContext("/searchOrders", new searchOrdersHandler());
        server.createContext("/listOrders", new listOrdersHandler());
        server.createContext("/shipOrder", new shipOrderHandle());
        server.createContext("/insertOrder", new insertOrderHandler());
        server.createContext("/createOrderTable", new createOrderTableHandler());
        // !!! "/createOrderTable" will automaticall drop the table if exception happens.
        server.createContext("/dropOrderTable", new dropOrderTableHandler());
        server.createContext("/insertOrderTable", new insertOrderTableHandler());

        // leaftech db
        // cultureboxes,genomics,Processing, referencematerials
        server.createContext("/listCultureboxes", new listCultureboxesHandler());
        server.createContext("/insertCultureboxes", new insertCultureboxesHandler());
        server.createContext("/deleteCultureboxes", new deleteCultureboxesHandler());
        server.createContext("/decreaseCultureboxes", new decreaseCultureboxesHandler());
        server.createContext("/searchCultureboxes", new searchCultureboxesHandler());

        server.createContext("/listGenomics", new listGenomicsHandler());
        server.createContext("/insertGenomics", new insertGenomicsHandler());
        server.createContext("/deleteGenomics", new deleteGenomicsHandler());
        server.createContext("/decreaseGenomics", new decreaseGenomicsHandler());
        server.createContext("/searchGenomics", new searchGenomicsHandler());

        server.createContext("/listProcessing", new listProcessingHandler());
        server.createContext("/insertProcessing", new insertProcessingHandler());
        server.createContext("/deleteProcessing", new deleteProcessingHandler());
        server.createContext("/decreaseProcessing", new decreaseProcessingHandler());
        server.createContext("/searchProcessing", new searchProcessingHandler());

        server.createContext("/listReferencematerials", new listReferencematerialsHandler());
        server.createContext("/insertReferencematerials", new insertReferencematerialsHandler());
        server.createContext("/deleteReferencematerials", new deleteReferencematerialsHandler());
        server.createContext("/decreaseReferencematerials", new decreaseReferencematerialsHandler());
        server.createContext("/searchReferencematerials", new searchReferencematerialsHandler());

        server.setExecutor(null); // creates a default executor
        server.start();
    }

    public static void main(String[] args) throws Exception {
        startServer(8000);
    }

    static class switchOverHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
            String mode = params.get("mode");
            try {
                if (mode.equals("kickoff")) {
                    db.switchOver();
                } else if (mode.equals("restore")) {
                    db.switchOverRestore();
                }
            } catch (Exception ex) {
                Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            String response = "done";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class loginHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
            String name = params.get("name");
            String password = params.get("password");
            String result = "";
            try {
                result = db.login(name, password);
                if (result.length() > 0) {
                    result = "SUCCESS;" + result;
                } else {
                    result = "FAIL";
                }
            } catch (Exception ex) {
                Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            t.sendResponseHeaders(200, result.length());
            OutputStream os = t.getResponseBody();
            os.write(result.getBytes());
            os.close();
        }
    }

    static class logoutHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
            String name = params.get("name");
            String password = params.get("password");
            try {
                String response = db.logout(name, password);
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (Exception ex) {
                Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /*
    
    Trees
    
     */
    static class listTreesHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            try {
               
                String sql = "SELECT * FROM trees;";
                    String response = db.inventoryQuery(sql);
                    String table = "trees;";
                    t.sendResponseHeaders(200, response.getBytes().length+table.getBytes().length);
                    OutputStream os = t.getResponseBody();
                    os.write(table.getBytes());
                    os.write(response.getBytes());
                    os.close();
                
            } catch (Exception ex3) {
                Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex3);
            }
            
        }
    }
    
        static class insertTreesHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
                String productCode = params.get("productCode");
                String description = params.get("description");
                String quantity = params.get("quantity");
                String price = params.get("price");
                try {
                    String sql = String.format("INSERT INTO trees (product_code, description, quantity, price) "
                            + " VALUES (\"%s\",\"%s\",\"%s\",\"%s\");", productCode, description, quantity, price);
                    String response = db.inventoryUpdate(sql);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        static class deleteTreesHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
                String productCode = params.get("productCode");
                try {
                    String sql = "DELETE FROM trees WHERE product_code =\"" + productCode + "\";";
                    String response = db.inventoryUpdate(sql);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        static class decreaseTreesHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                try {
                    Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
                    String productCode = params.get("productCode");
                    String sql = "UPDATE trees set quantity=(quantity-1) where product_code = '" + productCode + "';";
                    String response = db.inventoryUpdate(sql);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        /*
    
    Shrubs
    
         */
        static class listShrubsHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                try {
                    String sql = "SELECT * FROM shrubs;";
                    String response = db.inventoryQuery(sql);
                    String table = "shrubs;";
                    t.sendResponseHeaders(200, response.getBytes().length+table.getBytes().length);
                    OutputStream os = t.getResponseBody();
                    
                    os.write(table.getBytes());
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        static class insertShrubsMyHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
                String productCode = params.get("productCode");
                String description = params.get("description");
                String quantity = params.get("quantity");
                String price = params.get("price");
                try {
                    String sql = String.format("INSERT INTO shrubs (product_code, description, quantity, price) "
                            + " VALUES (\"%s\",\"%s\",\"%s\",\"%s\");", productCode, description, quantity, price);
                    String response = db.inventoryUpdate(sql);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        static class deleteShrubsHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
                String productCode = params.get("productCode");
                try {
                    System.out.println("Deleting shrubs");
                    String sql = "DELETE FROM shrubs WHERE product_code =\"" + productCode + "\";";
                    String response = db.inventoryUpdate(sql);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        static class decreaseShrubsHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                try {
                    Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
                    String productCode = params.get("productCode");
                    String sql = "UPDATE shrubs set quantity=(quantity-1) where product_code = '" + productCode + "';";
                    String response = db.inventoryUpdate(sql);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        /*
    
    Seeds
    
         */
        static class listSeedsHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                try {
                    String sql = "SELECT * FROM seeds;";
                    String response = db.inventoryQuery(sql);
                    String table = "seeds;";
                    t.sendResponseHeaders(200, response.getBytes().length+table.getBytes().length);
                    OutputStream os = t.getResponseBody();
                    
                    
                    os.write(table.getBytes());
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        static class insertSeedsHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
                String productCode = params.get("productCode");
                String description = params.get("description");
                String quantity = params.get("quantity");
                String price = params.get("price");
                try {
                    String sql = String.format("INSERT INTO seeds (product_code, description, quantity, price) "
                            + " VALUES (\"%s\",\"%s\",\"%s\",\"%s\");", productCode, description, quantity, price);
                    String response = db.inventoryUpdate(sql);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        static class deleteSeedsHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
                String productCode = params.get("productCode");
                try {
                    String sql = "DELETE FROM seeds WHERE product_code =\"" + productCode + "\";";
                    String response = db.inventoryUpdate(sql);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        static class decreaseSeedsHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                try {
                    Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
                    String productCode = params.get("productCode");
                    String sql = "UPDATE seeds set quantity=(quantity-1) where product_code = '" + productCode + "';";
                    String response = db.inventoryUpdate(sql);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        /*
    
    Orders
    
         */
        static class searchOrdersHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                try {
                    Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
                    String orderId = params.get("orderId");
                    String sql = "SELECT * FROM orders WHERE order_id ='" + orderId + "';";
                    String response = db.orderinfoQuery(sql);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        static class listOrdersHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                try {
                    String sql = "SELECT * FROM orders;";
                    String response = db.orderinfoQuery(sql);
                    String table = "orders;";
                    t.sendResponseHeaders(200, response.getBytes().length+table.getBytes().length);
                    OutputStream os = t.getResponseBody();
                    
                    os.write(table.getBytes());
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        static class shipOrderHandle implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                try {
                    Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
                    String orderId = params.get("orderId");
                    String sql = "UPDATE orders SET shipped=" + true + " WHERE order_id='" + orderId + "';";
                    String response = db.orderinfoUpdate(sql);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        static class insertOrderHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
                String orderId = params.get("orderId");
                String orderDate = params.get("orderDate");
                String firstName = params.get("firstName");
                String lastName = params.get("lastName");
                String address = params.get("address");
                String phone = params.get("phone");
                String totalCost = params.get("totalCost");
                String shipped = params.get("shipped");
                String orderTable = params.get("orderTable");
                try {
                    String sql = String.format("INSERT INTO orders (order_date,first_name,last_name,address,phone,total_cost,shipped,ordertable) "
                            + " VALUES (\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\");", orderId, orderDate, firstName, lastName, address, phone, totalCost, shipped, orderTable);
                    String response = db.orderinfoUpdate(sql);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);

                }
            }
        }

        static class createOrderTableHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
                String orderTableName = params.get("orderTableName");
                try {
                    String sql = "CREATE TABLE " + orderTableName
                            + "(item_id int unsigned not null auto_increment primary key, "
                            + "product_id varchar(20), description varchar(80), "
                            + "item_price float(7,2) );";
                    String response = db.orderinfoUpdate(sql);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);

                    String sql = "CREATE TABLE " + orderTableName
                            + "(item_id int unsigned not null auto_increment primary key, "
                            + "product_id varchar(20), description varchar(80), "
                            + "item_price float(7,2) );";
                    try {
                        db.orderinfoUpdate(sql);
                    } catch (SQLException ex1) {
                        Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
            }
        }

        static class dropOrderTableHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                try {
                    Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
                    String orderTableName = params.get("orderTableName");
                    String sql = "DROP TABLE " + orderTableName + ";";
                    String response = db.orderinfoUpdate(sql);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        static class insertOrderTableHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
                String orderTableName = params.get("orderTableName");
                String productId = params.get("productCode");
                String description = params.get("description");
                String itemPrice = params.get("itemPrice");
                try {
                    String sql = String.format("INSERT INTO " + orderTableName + " (product_id, description, item_price) "
                            + " VALUES (\"%s\",\"%s\",\"%s\");", productId, description, itemPrice);
                    String response = db.orderinfoUpdate(sql);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        /*
        Culture Box
         */
        static class insertCultureboxesHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
                String productID = params.get("productCode");
                String description = params.get("description");
                String quantity = params.get("quantity");
                String perUnitCost = params.get("perUnitCost");
                try {
                    String sql = "INSERT INTO cultureboxes (productid, "
                            + "productdescription, productquantity, productprice) VALUES ( '"
                            + productID + "', " + "'" + description + "', "
                            + quantity + ", " + perUnitCost + ");";
                    String response = db.leaftechUpdate(sql);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        static class deleteCultureboxesHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
                String productID = params.get("productCode");
                try {
                    String sql = "DELETE FROM cultureboxes WHERE productid = '" + productID + "';";
                    String response = db.leaftechUpdate(sql);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        static class decreaseCultureboxesHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
                String productID = params.get("productCode");
                try {
                    String sql = "UPDATE cultureboxes set productquantity=(productquantity-1) where productid = '" + productID + "';";
                    String response = db.leaftechUpdate(sql);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        static class searchCultureboxesHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
                String productID = params.get("productCode");
                try {
                    String sql = "SELECT * from cultureboxes where productid = '" + productID + "';";
                    String response = db.leaftechQuery(sql);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        static class listCultureboxesHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                try {
                    String sql = "SELECT * FROM cultureboxes;";
                    String response = db.leaftechQuery(sql);
                    String table = "cultureboxes;";
                    
                    t.sendResponseHeaders(200, response.getBytes().length+table.getBytes().length);
                   // t.sendResponseHeaders(200, response.getBytes().length+table.getBytes().length);
                    OutputStream os = t.getResponseBody();
                    
                    os.write(table.getBytes());
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        /*
    
        Genomics
         */
        static class insertGenomicsHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
                String productCode = params.get("productCode");
                String description = params.get("description");
                String quantity = params.get("quantity");
                String price = params.get("price");
                try {
                    String sql = "INSERT INTO genomics (productid, "
                            + "productdescription, productquantity, productprice) VALUES ( '"
                            + productCode + "', " + "'" + description + "', "
                            + quantity + ", " + price + ");";
                    String response = db.leaftechUpdate(sql);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        static class deleteGenomicsHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
                String productID = params.get("productCode");
                try {
                    String sql = "DELETE FROM genomics WHERE productid = '" + productID + "';";
                    String response = db.leaftechUpdate(sql);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        static class decreaseGenomicsHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
                String productID = params.get("productCode");
                try {
                    String sql = "UPDATE genomics set productquantity=(productquantity-1) where productid = '" + productID + "';";
                    String response = db.leaftechUpdate(sql);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        static class searchGenomicsHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
                String productID = params.get("productCode");
                try {
                    String sql = "SELECT * from genomics where productid = '" + productID + "';";
                    String response = db.leaftechQuery(sql);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        static class listGenomicsHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                System.out.println("We are in the genomics");
                try {
                    String sql = "SELECT * FROM genomics;";
                    String response = db.leaftechQuery(sql);
                    String table = "genomics;";
                    System.out.println("Genomics length:"+response.length());
                    System.out.println("Table length:"+table.length());
                    int total = response.length() + table.length()+100;
                    t.sendResponseHeaders(200, response.getBytes().length+table.getBytes().length);
                    
                    OutputStream os = t.getResponseBody();                    
                    os.write(table.getBytes());
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        /*
    
    Processing
         */
        static class insertProcessingHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
               String productCode = params.get("productCode");
                String description = params.get("description");
                String quantity = params.get("quantity");
                String price = params.get("price");
                try {
                    String sql = "INSERT INTO processing (productid, "
                            + "productdescription, productquantity, productprice) VALUES ( '"
                            + productCode + "', " + "'" + description + "', "
                            + quantity + ", " + price + ");";
                    String response = db.leaftechUpdate(sql);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        static class deleteProcessingHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
                String productID = params.get("productCode");
                try {
                    String sql = "DELETE FROM processing WHERE productid = '" + productID + "';";
                    String response = db.leaftechUpdate(sql);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        static class decreaseProcessingHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
                String productID = params.get("productCode");
                try {
                    String sql = "UPDATE processing set productquantity=(productquantity-1) where productid = '" + productID + "';";
                    String response = db.leaftechUpdate(sql);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        static class searchProcessingHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
                String productID = params.get("productCode");
                try {
                    String sql = "SELECT * from processing where productid = '" + productID + "';";
                    String response = db.leaftechQuery(sql);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        static class listProcessingHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                try {
                    String sql = "SELECT * FROM processing;";
                    String response = db.leaftechQuery(sql);
                    String table = "processing;";
                    t.sendResponseHeaders(200, response.getBytes().length+table.getBytes().length);
                    OutputStream os = t.getResponseBody();
                    
                    os.write(table.getBytes());
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        /*
    
    Reference Material
    
         */
        static class insertReferencematerialsHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
                String productCode = params.get("productCode");
                String description = params.get("description");
                String quantity = params.get("quantity");
                String price = params.get("price");
                try {
                    String sql = "INSERT INTO referencematerials (productid, "
                            + "productdescription, productquantity, productprice) VALUES ( '"
                            + productCode + "', " + "'" + description + "', "
                            + quantity + ", " + price + ");";
                    String response = db.leaftechUpdate(sql);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        static class deleteReferencematerialsHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
                String productID = params.get("productCode");
                try {
                    String sql = "DELETE FROM referencematerials WHERE productid = '" + productID + "';";
                    String response = db.leaftechUpdate(sql);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        static class decreaseReferencematerialsHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
                String productID = params.get("productCode");
                try {
                    String sql = "UPDATE referencematerials set productquantity=(productquantity-1) where productid = '" + productID + "';";
                    String response = db.leaftechUpdate(sql);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        static class searchReferencematerialsHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                Map<String, String> params = WebServer.queryToMap(t.getRequestURI().getQuery());
                String productID = params.get("productCode");
                try {
                    String sql = "SELECT * from referencematerials where productid = '" + productID + "';";
                    String response = db.leaftechQuery(sql);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        static class listReferencematerialsHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                try {
                    String sql = "SELECT * FROM referencematerials;";
                    String response = db.leaftechQuery(sql);
                    String table = "referencematerials;";
                    t.sendResponseHeaders(200, response.getBytes().length+table.getBytes().length);
                    OutputStream os = t.getResponseBody();

                    
                    os.write(table.getBytes());
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception ex) {
                    Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        static class sampleWebAppHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange t) throws IOException {
                FileReader rd = new FileReader("src\\WebServerLayer\\sample.html");
                BufferedReader bf = new BufferedReader(rd);
                String line;
                String response = "";
                while ((line = bf.readLine()) != null) {
                    response += line;
                }
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }

        /**
         * returns the url parameters in a map
         *
         * @param query
         * @return map
         */
        public static Map<String, String> queryToMap(String query) {
            Map<String, String> result = new HashMap<String, String>();
            for (String param : query.split("&")) {
                String pair[] = param.split("=");
                if (pair.length > 1) {
                    result.put(pair[0], pair[1]);
                } else {
                    result.put(pair[0], "");
                }
            }
            return result;
        }
    }
